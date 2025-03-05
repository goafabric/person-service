ALTER TABLE person ADD COLUMN middle_name VARCHAR(256);

ALTER TABLE person ALTER COLUMN first_name TYPE VARCHAR(512);
ALTER TABLE person ALTER COLUMN last_name TYPE VARCHAR(512);

ALTER TABLE person RENAME COLUMN first_name TO given_name;
ALTER TABLE person RENAME COLUMN last_name TO family_name;
