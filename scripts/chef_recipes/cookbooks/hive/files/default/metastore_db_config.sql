CREATE DATABASE metastore;
USE metastore;
SOURCE /home/hadoop/hive-0.12.0/scripts/metastore/upgrade/mysql/hive-schema-0.12.0.mysql.sql;

CREATE USER 'hive'@'localhost' IDENTIFIED BY 'hive';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'hive'@'localhost';
GRANT SELECT,INSERT,UPDATE,DELETE,LOCK TABLES,EXECUTE ON metastore.* TO 'hive'@'localhost';

CREATE USER 'hive'@'%' IDENTIFIED BY 'hive';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'hive'@'%';
GRANT SELECT,INSERT,UPDATE,DELETE,LOCK TABLES,EXECUTE ON metastore.* TO 'hive'@'%';

FLUSH PRIVILEGES;