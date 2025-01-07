CREATE TABLE goae AS SELECT * FROM read_csv('/host/Downloads/goae.csv‘);

CREATE TABLE customers AS SELECT * FROM read_csv('/host/Downloads/customers-20.csv');
https://www.datablist.com/learn/csv/download-sample-csv-files

.open testdb 
=> creates a database file, import will be slower