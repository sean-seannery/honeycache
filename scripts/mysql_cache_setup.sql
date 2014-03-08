-- the mysql metastore db should be initialized at this point. If it isn't this script wont work
-- Google how to set that up if you haven't already
-- this should be executed as dba or root or someone with full access to the metastore db

CREATE DATABASE IF NOT EXISTS hcache_store;

CREATE USER 'hcache'@'localhost' IDENTIFIED BY 'hcachepw';
GRANT ALL PRIVILEGES ON hcache_store.* TO 'hcache'@'localhost'; 
CREATE USER 'hcache'@'%' IDENTIFIED BY 'hcachepw';
GRANT ALL PRIVILEGES ON hcache_store.* TO 'hcache'@'%'; 

USE hcache_store;

DROP TABLE IF EXISTS hcache_key_data;
CREATE TABLE hcache_key_data
 (key_id VARCHAR(36),
  table_name VARCHAR(50) NOT NULL,
  date_accessed DATETIME,
  frequency_accessed INT,
  size INT,
  PRIMARY KEY (key_id)
  );
  
SELECT CONCAT( 'DROP TABLE ', GROUP_CONCAT(table_name) , ';' ) 
    AS statement FROM information_schema.tables 
    WHERE table_schema = 'hcache_store' AND table_name LIKE 't_%';

TRUNCATE hcache_key_data; 

