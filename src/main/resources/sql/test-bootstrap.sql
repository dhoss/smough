CREATE DATABASE smough_test;
-- change this, obviously.
CREATE USER smough_test WITH ENCRYPTED PASSWORD 'smough_test';
-- may want to fine tune this
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO smough_test;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO smough_test;
