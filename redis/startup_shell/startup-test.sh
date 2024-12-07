#!/bin/sh

# redis/startup_shell/startup-test.sh

# Test that the redis server is running
redis-server --version

# Start the redis server
redis-server /opt/bitnami/redis/etc/redis.conf --daemonize yes

# Wait a moment to ensure Redis has time to start up
sleep 2

echo "Testing without authentication:"
redis-cli -h localhost -p 6800 ping

# Test Redis server without authentication (only works if the default user has access)
echo "Testing without authentication:"
redis-cli -h 127.0.0.1 -p 6800 ping

# Test Redis server with password authentication (replace 'test' with actual password)
echo "Testing with password authentication:"
redis-cli -h 127.0.0.1 -p 6800 -a test ping

# Test Redis server with full URI for authentication (replace with actual credentials)
# Format: redis://username:password@host:port
echo "Testing with URI-based authentication for a default user:"
redis-cli -u redis://default:test@127.0.0.1:6800 ping

# Test Redis server with full URI for authentication (replace with actual credentials)
# Format: redis://username:password@host:port
echo "Testing with URI-based authentication for a psc-server-test user:"
redis-cli -u redis://psc-server-test:pscpassword@127.0.0.1:6800 ping

echo "Testing with URI-based authentication for a psc-server-test user:"
redis-cli -u redis://psc-server-test:pscpassword@127.0.0.1:6800 ping

# Keep this thread/container running (without this container will just close out at the end).
tail -f /dev/null