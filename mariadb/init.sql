-- Create the user (if not already exists)
CREATE USER IF NOT EXISTS 'psc-scorecard-server'@'%' IDENTIFIED BY 'test';

-- Grant DBA-related privileges to the user
GRANT ALL PRIVILEGES ON *.* TO 'psc-scorecard-server'@'%' WITH GRANT OPTION;

-- OR: Grant specific DBA privileges individually (choose the ones you need)
GRANT CREATE, ALTER, DROP, INSERT, UPDATE, DELETE, SELECT, INDEX, LOCK TABLES, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EXECUTE, EVENT, TRIGGER, CREATE USER, RELOAD, SHUTDOWN, PROCESS, FILE, GRANT OPTION ON *.* TO 'psc-scorecard-server'@'%';

-- Apply the privileges
FLUSH PRIVILEGES;