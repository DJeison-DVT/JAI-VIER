#!/bin/bash
# Copyright (c) 2022 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

# Fail on error
set -e


# Create Object Store Bucket (Should be replaced by terraform one day)
while ! state_done OBJECT_STORE_BUCKET; do
  echo "Checking object storage bucket"
#  oci os bucket create --compartment-id "$(state_get COMPARTMENT_OCID)" --name "$(state_get RUN_NAME)"
  if oci os bucket get --name "$(state_get RUN_NAME)-$(state_get MTDR_KEY)"; then
    state_set_done OBJECT_STORE_BUCKET
    echo "finished checking object storage bucket"
  fi
done


# Wait for Order DB OCID
while ! state_done MTDR_DB_OCID; do
  echo "`date`: Waiting for MTDR_DB_OCID"
  sleep 2
done


# Get Wallet
while ! state_done WALLET_GET; do
  echo "creating wallet"
  cd $MTDRWORKSHOP_LOCATION
  mkdir wallet
  cd wallet
  oci db autonomous-database generate-wallet --autonomous-database-id "$(state_get MTDR_DB_OCID)" --file 'wallet.zip' --password 'Welcome1' --generate-type 'ALL'
  unzip wallet.zip
  cd $MTDRWORKSHOP_LOCATION
  state_set_done WALLET_GET
  echo "finished creating wallet"
done


# Get DB Connection Wallet and to Object Store
while ! state_done CWALLET_SSO_OBJECT; do
  echo "grabbing wallet"
  cd $MTDRWORKSHOP_LOCATION/wallet
  oci os object put --bucket-name "$(state_get RUN_NAME)-$(state_get MTDR_KEY)" --name "cwallet.sso" --file 'cwallet.sso'
  cd $MTDRWORKSHOP_LOCATION
  state_set_done CWALLET_SSO_OBJECT
  echo "done grabbing wallet"
done


# Create Authenticated Link to Wallet
while ! state_done CWALLET_SSO_AUTH_URL; do
  echo "creating authenticated link to wallet"
  ACCESS_URI=`oci os preauth-request create --object-name 'cwallet.sso' --access-type 'ObjectRead' --bucket-name "$(state_get RUN_NAME)-$(state_get MTDR_KEY)" --name 'mtdrworkshop' --time-expires $(date '+%Y-%m-%d' --date '+7 days') --query 'data."access-uri"' --raw-output`
  state_set CWALLET_SSO_AUTH_URL "https://objectstorage.$(state_get REGION).oraclecloud.com${ACCESS_URI}"
  echo "done creating authenticated link to wallet"
done


# Give DB_PASSWORD priority
while ! state_done DB_PASSWORD; do
  echo "Waiting for DB_PASSWORD"
  sleep 5
done


# Create Inventory ATP Bindings
while ! state_done DB_WALLET_SECRET; do
  echo "creating Inventory ATP Bindings"
  cd $MTDRWORKSHOP_LOCATION/wallet
  cat - >sqlnet.ora <<!
WALLET_LOCATION = (SOURCE = (METHOD = file) (METHOD_DATA = (DIRECTORY="/mtdrworkshop/creds")))
SSL_SERVER_DN_MATCH=yes
!
  if kubectl create -f - -n mtdrworkshop; then
    state_set_done DB_WALLET_SECRET
  else
    echo "Error: Failure to create db-wallet-secret.  Retrying..."
    sleep 5
  fi <<!
apiVersion: v1
data:
  README: $(base64 -w0 README)
  cwallet.sso: $(base64 -w0 cwallet.sso)
  ewallet.p12: $(base64 -w0 ewallet.p12)
  keystore.jks: $(base64 -w0 keystore.jks)
  ojdbc.properties: $(base64 -w0 ojdbc.properties)
  sqlnet.ora: $(base64 -w0 sqlnet.ora)
  tnsnames.ora: $(base64 -w0 tnsnames.ora)
  truststore.jks: $(base64 -w0 truststore.jks)
kind: Secret
metadata:
  name: db-wallet-secret
!
  cd $MTDRWORKSHOP_LOCATION
done


# DB Connection Setup
export TNS_ADMIN=$MTDRWORKSHOP_LOCATION/wallet
cat - >$TNS_ADMIN/sqlnet.ora <<!
WALLET_LOCATION = (SOURCE = (METHOD = file) (METHOD_DATA = (DIRECTORY="$TNS_ADMIN")))
SSL_SERVER_DN_MATCH=yes
!
MTDR_DB_SVC="$(state_get MTDR_DB_NAME)_tp"
TODO_USER=TODOUSER
ORDER_LINK=ORDERTOINVENTORYLINK
ORDER_QUEUE=ORDERQUEUE


# Get DB Password
while true; do
  if DB_PASSWORD=`kubectl get secret dbuser -n mtdrworkshop --template={{.data.dbpassword}} | base64 --decode`; then
    if ! test -z "$DB_PASSWORD"; then
      break
    fi
  fi
  echo "Error: Failed to get DB password.  Retrying..."
  sleep 5
done


# Wait for DB Password to be set in Order DB
while ! state_done MTDR_DB_PASSWORD_SET; do
  echo "`date`: Waiting for MTDR_DB_PASSWORD_SET"
  sleep 2
done


# Order DB User, Objects
while ! state_done TODO_USER; do
  echo "connecting to mtdr database"
  U=$TODO_USER
  SVC=$MTDR_DB_SVC
  sqlplus /nolog <<!
WHENEVER SQLERROR EXIT 1
connect admin/"$DB_PASSWORD"@$SVC
CREATE USER TODOUSER IDENTIFIED BY "$DB_PASSWORD" DEFAULT TABLESPACE data QUOTA UNLIMITED ON data;
GRANT CREATE SESSION, CREATE VIEW, CREATE SEQUENCE, CREATE PROCEDURE TO $U;
GRANT CREATE TABLE, CREATE TRIGGER, CREATE TYPE, CREATE MATERIALIZED VIEW TO $U;
GRANT CONNECT, RESOURCE, pdb_dba, SODA_APP to $U;
-- Tabla de Users
CREATE TABLE TODOUSER.users (
    id NUMBER GENERATED ALWAYS AS IDENTITY, 
    username VARCHAR2(200) NOT NULL UNIQUE,
    email VARCHAR2(100) NOT NULL UNIQUE,
    full_name VARCHAR2(100) NOT NULL,
    password_hash VARCHAR2(100) NOT NULL,  -- Store hashed passwords, avoid plaintext
    role VARCHAR2(20) NOT NULL CHECK (role IN ('ADMIN', 'MANAGER', 'DEVELOPER')),
    work_mode VARCHAR2(20) NOT NULL CHECK (work_mode IN ('REMOTE', 'HYBRID')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Needs trigger for automatic updates
    last_login TIMESTAMP,
    active NUMBER(1) DEFAULT 1 NOT NULL CHECK (active IN (0, 1)),
	
	PRIMARY KEY(id)
);

CREATE OR REPLACE TRIGGER users_updated_at
BEFORE UPDATE ON TODOUSER.users
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- Tabla de Proyectos
CREATE TABLE TODOUSER.project (
    id NUMBER GENERATED ALWAYS AS IDENTITY, 
    name VARCHAR2(200) NOT NULL,
    description VARCHAR2(4000), 
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE,
    status NUMBER(1) NOT NULL CHECK (status BETWEEN 0 AND 3),  -- 'PLANNING', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
	
    -- manager_id NUMBER,
	
	PRIMARY KEY (id)

    -- CONSTRAINT fk_project_manager FOREIGN KEY (manager_id) REFERENCES TODOUSER.users(id)
);

-- Create trigger for automatically updating updated_at on row update
CREATE OR REPLACE TRIGGER update_project_timestamp
BEFORE UPDATE ON TODOUSER.project
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/


CREATE TABLE TODOUSER.task (
    id NUMBER GENERATED ALWAYS AS IDENTITY, 
    title VARCHAR2(200) NOT NULL,
    description VARCHAR2(4000), 
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
    due_date TIMESTAMP WITH TIME ZONE,
    priority NUMBER(1) NOT NULL CHECK (priority BETWEEN 0 AND 3), -- 0: LOW, 1: MEDIUM, 2: HIGH, 3: CRITICAL
    status NUMBER(1) NOT NULL CHECK (status BETWEEN 0 AND 3),  -- 0: TODO, 1: IN_PROGRESS, 2: IN_REVIEW, 3: DONE
    estimated_hours NUMBER(5,2),

    project_id NUMBER NOT NULL,
    -- sprint_id NUMBER,
    -- assignee_id NUMBER,
    -- reporter_id NUMBER NOT NULL,

    PRIMARY KEY (id),
    
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES TODOUSER.project(id)
    -- CONSTRAINT fk_task_sprint FOREIGN KEY (sprint_id) REFERENCES sprints(sprint_id),
    -- CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id) REFERENCES users(user_id),
    -- CONSTRAINT fk_task_reporter FOREIGN KEY (reporter_id) REFERENCES users(user_id)
);

-- Create trigger for automatically updating updated_at on row update
CREATE OR REPLACE TRIGGER update_task_timestamp
BEFORE UPDATE ON TODOUSER.task
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/


-- Tabla de Subtareas
CREATE TABLE TODOUSER.subtask (
    id NUMBER GENERATED ALWAYS AS IDENTITY, 
	title VARCHAR2(200) NOT NULL,
    description VARCHAR2(4000), 
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, 
    status NUMBER(1) NOT NULL CHECK (status BETWEEN 0 AND 2),  -- 0: TODO, 1: IN_PROGRESS, 3: DONE
	
	task_id NUMBER NOT NULL,
    -- assignee_id NUMBER,

	PRIMARY KEY (id),
    
	CONSTRAINT fk_subtask_task FOREIGN KEY (task_id) REFERENCES TODOUSER.task(id)
    -- CONSTRAINT fk_subtask_assignee FOREIGN KEY (assignee_id) REFERENCES users(user_id)
);

-- Create trigger for automatically updating updated_at on row update
CREATE OR REPLACE TRIGGER update_subtask_timestamp
BEFORE UPDATE ON TODOUSER.subtask
FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- Tabla de Equipos de Proyecto
CREATE TABLE TODOUSER.project_member (
    project_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    joined_date DATE DEFAULT CURRENT_DATE NOT NULL,
    PRIMARY KEY (project_id, user_id),
    CONSTRAINT fk_projectmember_project FOREIGN KEY (project_id) REFERENCES TODOUSER.project(id),
    CONSTRAINT fk_projectmember_user FOREIGN KEY (user_id) REFERENCES TODOUSER.users(id)
);

COMMIT;

!
  state_set_done TODO_USER
  echo "finished connecting to database and creating attributes"
done
# DB Setup Done
state_set_done DB_SETUP