create table audit_trail
(
	id varchar(36) not null
		constraint pk_audit
			primary key,

    organization_id varchar(36),
    object_type varchar(255),

    object_id varchar(255),
    operation varchar(255),
    created_by varchar(255),
    created_at timestamp,
    modified_by varchar(255),
    modified_at timestamp,
    oldvalue TEXT,
    newvalue TEXT
);

create index idx_audit_organization_id on audit_trail(organization_id);