#!/bin/sh
# Go into the maven app directory
#cd ./political-scorecard-backend

# Creates a jar file that can be extracted and installed elsewhere.
# Also allows you to see if you would be able to do a production build
# (A dev build and production build will not always act the same, best to test both each time).
mvn clean install -q

# Build the springboot maven app in dev mode
mvn spring-boot:run

# Keep this thread/container running (without this container will just close out at the end).
tail -f /dev/null