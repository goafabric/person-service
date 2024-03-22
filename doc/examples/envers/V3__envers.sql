CREATE SEQUENCE username_revision_seq START WITH 1 INCREMENT BY 50 MINVALUE 1;

CREATE TABLE username_revision (
    id INTEGER NOT NULL,
    timestamp bigint,
    user_name varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE revinfo
(
    rev      INTEGER GENERATED BY DEFAULT
             AS IDENTITY ( START WITH 1 ),
    revtstmp BIGINT,
    PRIMARY KEY ( rev )
);

---


CREATE TABLE person_aud
(
    id      varchar(36) not null,
    rev     INTEGER NOT NULL,
    revtype TINYINT,

    orgunit_id varchar(36) NULL, -- All fields need to be nullable for audit delete

	first_name varchar(255) NULL,
	last_name varchar(255) NULL,

    PRIMARY KEY ( id, rev )
);

CREATE TABLE address_aud
(
    id      varchar(36) not null,
    rev     INTEGER NOT NULL,
    revtype TINYINT,

    person_id varchar(36),

	street varchar(255) NULL,
	city varchar(255) NULL,

    PRIMARY KEY ( id, rev )
);

CREATE TABLE person_eo_address_eo_aud
(
    id      varchar(36) not null,
    rev     INTEGER NOT NULL,
    revtype TINYINT,

    person_id varchar(36),

    PRIMARY KEY ( id, rev )
);