DROP USER IF EXISTS 'psc-server-test'@'%';

CREATE USER 'psc-server-test'@'%' IDENTIFIED BY 'test';

GRANT ALL PRIVILEGES ON *.* TO 'psc-server-test'@'%' WITH GRANT OPTION;

FLUSH PRIVILEGES;