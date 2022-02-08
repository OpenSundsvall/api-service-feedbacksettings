-- Creation of table for schema history versioning mechanism
    create table schema_history (
       schema_version varchar(255) not null,
        applied datetime(6) not null,
        comment varchar(8192) not null,
        primary key (schema_version)
    ) engine=InnoDB;


-- Necessary line in order to document the change. 
INSERT INTO schema_history (schema_version,comment,applied) VALUES ('000','Applied versioning of schema', NOW());