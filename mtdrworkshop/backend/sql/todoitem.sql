

CREATE TABLE TODOOWNER.TODOITEM (
                                    id NUMBER GENERATED ALWAYS AS IDENTITY, description VARCHAR2(4000),
                                    creation_ts TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                    done NUMBER(1,0) default 0,
                                    PRIMARY KEY (id)
);

insert into TODOOWNER.todoitem  (description) values ('My first task!');