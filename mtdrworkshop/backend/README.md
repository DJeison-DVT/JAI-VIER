# Todolist Helidon backend

Todolist application backend built with Helidon SE, using Oracle JDBC
- __App Version `v2.1.0`__
- __Oracle JDBC Version `v23.4.0.24.05`__
- __Helidon SE Version `v2.4.2`__

## Environment Variables
The following environment variables are expected by the application. 
In order to successfully run the application, the environment variables below are __REQUIRED__.

| Variable            | Name              | Default | Description                                                     |
|---------------------|-------------------|---------|-----------------------------------------------------------------|
| `database.url`      | Database URL      | -       | Connection to URL, in the form of `jdbc:oracle:thin:@<details>` |
| `database.user`     | Database User     | -       | Database user with access to the necessary tables               |
| `database.password` | Database Password | -       | Database user credentials                                       |

## API Endpoints

The following endpoints are endpoints used by the application.

| Method | REST Endpoint                             | Sample Data                            | Description           |
|--------|-------------------------------------------|----------------------------------------|-----------------------|
| GET    | `http://localhost:8080/api/todolist`      | -                                      | Retrieves all Todos   |
| POST   | `http://localhost:8080/api/todolist`      | `{"description" : "Second new task!"}` | Saves a new Todo      |
| GET    | `http://localhost:8080/api/todolist/{id}` | -                                      | Retrieves a Todo item |
| PUT    | `http://localhost:8080/api/todolist/{id}` | `{"description": "...", "done": true}` | Updates a Todo item   |
| DELETE | `http://localhost:8080/api/todolist/{id}` | -                                      | Deletes a Todo item   |


## SQL Schema, Tables and Queries

The application expects and makes use of the following:

- __Database Schemas__: `TODOOWNER`
- __Database Tables__: `TODOITEM`
- __Database Queries and Privilege__:
  - `select, insert, update, delete` on `TODOOWNER.TODOITEM`


# Building the Application
The application uses Maven to build and manage the project with its dependencies. 
Since the [Dockerfile](./src/main/docker/Dockerfile) expects the JAR, you need to run mvn first.
```bash
mvn clean package
```

When building for docker, you can use the following command:
```bash
docker build -f src/main/docker/Dockerfile -t <image> .
```

# Deploying to Kubernetes
To deploy the application on Kubernetes, 
the environment variables and image must be replaced.

For example, you can create the following manifest.yaml file:
```yaml
# manifest
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend-deployment
  labels:
    app: backendapp
spec:
  replicas: 1
  selector:
    matchLabels:
      app: backendapp
  template:
    metadata:
      labels:
        app: backendapp
    spec:
      containers:
      - name: app
        image: example:v1 # update with your container image
        env:
        - name: database.user
          value: myUser # update with your database user
        - name: database.url
          value: "jdbc:oracle:thin:@<details>" # update with your database URL
        - name: database.password
          valueFrom:
            secretKeyRef:
              name: myDatabasePWDSecret # update with your database secret
              key: password
        ports:
        - containerPort: 8080

        # if database wallet is required
        volumeMounts:
        - name: creds
          mountPath: /app/creds # update with the right path to the wallet
        # end if
        
      restartPolicy: Always
      
      # if database wallet is required
      volumes:
      - name: creds
        secret:
          secretName: db-wallet-secret # update with the actual secret
      # end if


---

apiVersion: v1
kind: Service
metadata:
  name: backend-service
spec:
  type: ClusterIP
  ports:
  - port: 8080
    targetPort: 8080
  selector:
    app: backendapp
```

This configuration requires the following secret to be created:
```bash
kubectl create secret generic myDatabasePWDSecret --from-literal=password=<value>
```

If a wallet is necessary, you can run the following command to create the wallet secret
```bash
kubectl create secret generic wallet --from-file=<wallet_location>
```