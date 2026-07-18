#!/bin/sh
# test_startup.sh
# Go into the maven app directory

# Ensure the directory exists
mkdir -p /app/target/site/jacoco

# Adjust permissions to ensure the process can write to the directory while allowing it to remain accessible to the host via volumes
echo "Current user: $(whoami)"
# This is not working as expected, so I will need to manually adjust permissions on the host machine
# How to fix this: https://stackoverflow.com/questions/27701930/how-to-give-write-permissions-to-a-folder-in-docker-volume
# Adjust permissions to avoid issues with volume mounts
echo "Adjusting permissions for /app/target/site/jacoco..."
chown -R $(id -u):$(id -g) /app/target/site/jacoco
chmod -R 777 /app/target/site/jacoco

# Build the Spring Boot Maven app in dev mode and generate JaCoCo reports
mvn clean verify jacoco:report

# Keep this thread/container running (without this container will just close out at the end).
tail -f /dev/null