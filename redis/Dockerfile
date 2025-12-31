# Use the official Bitnami Redis 7.4 image as the base
FROM docker.io/bitnami/redis:latest

# Define default environment variables
ENV REDIS_PASSWORD="password" \
    ALLOW_EMPTY_PASSWORD="no" \
    REDIS_PORT_NUMBER=6379 \
    ENVIRONMENT="dev" \
    REDIS_ACLFILE="/opt/bitnami/redis/etc/redis-acl.conf" \
    REDIS_CONFIG_FILE="/opt/bitnami/redis/etc/redis.conf"

# Set the Redis configuration file path - according to environment (we assume dev for now)
# Copy custom Redis configuration files into the container
COPY ./redis-config/redis-dev.conf /opt/bitnami/redis/etc/redis.conf
COPY ./redis-config/redis-dev-acl.conf /opt/bitnami/redis/etc/redis-acl.conf

# Copy your custom entrypoint script to handle dynamic configuration
COPY ./startup_shell/startup-dev.sh /opt/bitnami/scripts/redis/startup.sh

# Switch to root user to change permissions
USER root 
# Make sure the entrypoint script is executable
RUN chmod +x /opt/bitnami/scripts/redis/startup.sh

# Source for redis ENV
# https://github.com/bitnami/containers/blob/main/bitnami/redis/7.4/debian-12/rootfs/opt/bitnami/scripts/redis-env.sh

# Set the Redis configuration file path

# Run Redis with the custom configuration file
# Note: Original Dockerfile source
# https://github.com/bitnami/containers/blob/main/bitnami/redis/7.4/debian-12/Dockerfile


# Use the custom entrypoint to handle dynamic environment-based config
# An entrypoint that has been defined by redis image is used to start the redis server using additional configuration
ENTRYPOINT [ "/opt/bitnami/scripts/redis/startup.sh" ]
CMD [ "/opt/bitnami/scripts/redis/run.sh" ]