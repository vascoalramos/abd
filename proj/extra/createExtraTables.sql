-- run with:
-- psql -h locahost -f createExtraTables.sql tpcc

DROP TABLE IF EXISTS nation;
CREATE TABLE NATION  ( N_NATIONKEY  INTEGER NOT NULL,
                       N_NAME       CHAR(25) NOT NULL,
                       N_REGIONKEY  INTEGER NOT NULL,
                       N_COMMENT    VARCHAR(152));

DROP TABLE if exists region;
CREATE TABLE REGION  ( R_REGIONKEY  INTEGER NOT NULL,
                       R_NAME       CHAR(25) NOT NULL,
                       R_COMMENT    VARCHAR(152));

DROP TABLE if exists SUPPLIER;
CREATE TABLE SUPPLIER ( SU_SUPPKEY     INTEGER NOT NULL,
                        SU_NAME        CHAR(25) NOT NULL,
                        SU_ADDRESS     VARCHAR(40) NOT NULL,
                        SU_NATIONKEY   INTEGER NOT NULL,
                        SU_PHONE       CHAR(15) NOT NULL,
                        SU_ACCTBAL     DECIMAL(15,2) NOT NULL,
                        SU_COMMENT     VARCHAR(101) NOT NULL);

\copy region FROM './region.tbl' WITH (FORMAT csv, DELIMITER '|');
\copy nation FROM './nation.tbl' WITH (FORMAT csv, DELIMITER '|');
\copy supplier FROM './supplier.tbl' WITH (FORMAT csv, DELIMITER '|');

