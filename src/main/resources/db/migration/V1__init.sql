drop table if exists person;

create table person
(
	id varchar(36) not null
		constraint pk_person
			primary key,
    company_id varchar(36),

	first_name varchar(255),
	last_name varchar(255),

    address_id varchar(36) NOT NULL,
    version bigint default 0
);

create index idx_person_company_id on person(company_id);

drop table if exists person_audit;

create table person_audit
(
	id varchar(36) not null
		constraint pk_person_audit
			primary key,
    company_id varchar(36) not null,

    reference_id varchar(255),
    operation varchar(255),
    created_by varchar(255),
    created_at date,
    modified_by varchar(255),
    modified_at date,
    oldvalue TEXT,
    newvalue TEXT
);

create index idx_person_audit_company_id on person_audit(company_id);

drop table if exists address;

create table address
(
	id varchar(36) not null
		constraint pk_address
			primary key,

    company_id varchar(36) not null,

	street varchar(255) NULL,
	city varchar(255) NULL,
	version bigint default 0
);

drop table if exists address_audit;

create table address_audit
(
	id varchar(36) not null
		constraint pk_address_audit
			primary key,
    company_id varchar(36),

    reference_id varchar(255),
    operation varchar(255),
    created_by varchar(255),
    created_at date,
    modified_by varchar(255),
    modified_at date,
    oldvalue TEXT,
    newvalue TEXT
);
