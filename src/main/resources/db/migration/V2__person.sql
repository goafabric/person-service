create table person
(
	id varchar(36) not null
		constraint pk_person
			primary key,
    organization_id varchar(36) not null,

	first_name varchar(255),
	last_name varchar(255),

	middle_name varchar(255),

    version bigint default 0
);

create index idx_person_organization_id on person(organization_id);

create table address
(
	id varchar(36) not null
		constraint pk_address
			primary key,

    person_id varchar(36),

	street varchar(255) NULL,
	city varchar(255) NULL,
	version bigint default 0
);
