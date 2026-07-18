# Use the official Maven image with Eclipse Temurin 21
FROM maven:3.9-eclipse-temurin-22

# Switch to root user
USER root

# Set the working directory
WORKDIR /app

# Copy the startup scripts and make them executable
COPY ./startup_shell ./startup_shell
RUN chmod +rx ./startup_shell ./startup_shell/*.sh

# Copy the entire project directory to the working directory
COPY ./ ./political-scorecard-backend

# Make the project directory and its contents executable
RUN chmod +rwx ./political-scorecard-backend

# Default to the dev startup script. CMD (not ENTRYPOINT) so a compose
# `command:` (e.g. ./startup_shell/test_startup.sh) actually replaces it —
# an ENTRYPOINT here silently swallowed compose commands and ran dev mode.
CMD ["./startup_shell/dev_startup_shell.sh"]