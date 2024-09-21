# Use the official Maven image with Eclipse Temurin 21
FROM maven:3.9-eclipse-temurin-21

# Switch to root user
USER root

# Set the working directory
WORKDIR /app

# Copy the startup scripts and make them executable
COPY ./startup_shell ./startup_shell
RUN chmod +rx ./startup_shell ./startup_shell/backend_startup_shell.sh

# Copy the entire project directory to the working directory
COPY ./ ./political-scorecard-backend

# Make the project directory and its contents executable
RUN chmod +rwx ./political-scorecard-backend

# Set the entry point to the startup script
ENTRYPOINT ["./startup_shell/backend_startup_shell.sh"]