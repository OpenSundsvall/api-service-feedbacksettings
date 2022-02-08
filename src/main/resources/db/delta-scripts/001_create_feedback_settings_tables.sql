-- Creation of tables and indexes 
    create table feedback_channels (
       setting_id varchar(255) not null,
        contact_method varchar(255) not null,
        destination varchar(255) not null,
        send_feedback bit not null
    ) engine=InnoDB;

    create table feedback_settings (
       id varchar(255) not null,
        created datetime(6) not null,
        modified datetime(6),
        organization_id varchar(255),
        person_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    alter table feedback_channels 
       add constraint feedback_channels_unique_combinations_constraint unique (setting_id, contact_method, destination);
create index feedback_settings_organization_id_index on feedback_settings (organization_id);
create index feedback_settings_person_id_index on feedback_settings (person_id);

    alter table feedback_settings 
       add constraint feedback_settings_person_id_organization_id_unique_constraint unique (person_id, organization_id);

    alter table feedback_channels 
       add constraint fk_feedback_settings_id 
       foreign key (setting_id) 
       references feedback_settings (id);

-- Necessary line in order to document the change. 
insert into schema_history (schema_version,comment,applied) VALUES ('001','Created feedback settings tables', NOW());
