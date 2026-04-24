-- Sequence for jv_commit primary key
CREATE SEQUENCE jv_commit_pk_seq AS BIGINT START WITH 1;

-- Table: jv_commit
CREATE TABLE jv_commit
(
    commit_pk BIGINT DEFAULT NEXT VALUE FOR jv_commit_pk_seq PRIMARY KEY,
    author VARCHAR(200),
    commit_date TIMESTAMP,
    commit_date_instant VARCHAR(30),
    commit_id NUMERIC(22,2)
);

-- Index: jv_commit_commit_id_idx
CREATE INDEX jv_commit_commit_id_idx
    ON jv_commit (commit_id);

----

-- Sequence for jv_commit_property primary key
-- Note: No auto-increment here, as the primary key is composite.

-- Table: jv_commit_property
CREATE TABLE jv_commit_property
(
    property_name VARCHAR(191) NOT NULL,
    property_value VARCHAR(600),
    commit_fk BIGINT NOT NULL,
    CONSTRAINT jv_commit_property_pk PRIMARY KEY (commit_fk, property_name),
    CONSTRAINT jv_commit_property_commit_fk FOREIGN KEY (commit_fk)
        REFERENCES jv_commit (commit_pk)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Index: jv_commit_property_commit_fk_idx
CREATE INDEX jv_commit_property_commit_fk_idx
    ON jv_commit_property (commit_fk);

-- Index: jv_commit_property_property_name_property_value_idx
CREATE INDEX jv_commit_property_property_name_property_value_idx
    ON jv_commit_property (property_name, property_value);

----

-- Sequence for jv_global_id primary key
CREATE SEQUENCE jv_global_id_pk_seq AS BIGINT START WITH 1;

-- Table: jv_global_id
CREATE TABLE jv_global_id
(
    global_id_pk BIGINT DEFAULT NEXT VALUE FOR jv_global_id_pk_seq PRIMARY KEY,
    local_id VARCHAR(191),
    fragment VARCHAR(200),
    type_name VARCHAR(200),
    owner_id_fk BIGINT,
    CONSTRAINT jv_global_id_owner_id_fk FOREIGN KEY (owner_id_fk)
        REFERENCES jv_global_id (global_id_pk)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Index: jv_global_id_local_id_idx
CREATE INDEX jv_global_id_local_id_idx
    ON jv_global_id (local_id);

-- Index: jv_global_id_owner_id_fk_idx
CREATE INDEX jv_global_id_owner_id_fk_idx
    ON jv_global_id (owner_id_fk);

----

-- Sequence for jv_snapshot primary key
CREATE SEQUENCE jv_snapshot_pk_seq AS BIGINT START WITH 1;

-- Table: jv_snapshot
CREATE TABLE jv_snapshot
(
    snapshot_pk BIGINT DEFAULT NEXT VALUE FOR jv_snapshot_pk_seq PRIMARY KEY,
    type VARCHAR(200),
    version BIGINT,
    state TEXT,
    changed_properties TEXT,
    managed_type VARCHAR(200),
    global_id_fk BIGINT,
    commit_fk BIGINT,
    CONSTRAINT jv_snapshot_commit_fk FOREIGN KEY (commit_fk)
        REFERENCES jv_commit (commit_pk)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT jv_snapshot_global_id_fk FOREIGN KEY (global_id_fk)
        REFERENCES jv_global_id (global_id_pk)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Index: jv_snapshot_commit_fk_idx
CREATE INDEX jv_snapshot_commit_fk_idx
    ON jv_snapshot (commit_fk);

-- Index: jv_snapshot_global_id_fk_idx
CREATE INDEX jv_snapshot_global_id_fk_idx
    ON jv_snapshot (global_id_fk);
