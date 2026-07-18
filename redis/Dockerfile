# PSC cache — KeyDB (protocol-compatible Redis fork; same image family as the
# suite's prf-keydb). Replaces the former bitnami/redis base, whose tags were
# removed from Docker Hub and broke every build that touched this image.
FROM eqalpha/keydb:latest

# The conf carries bind/protected-mode and the ACL users the Spring backend
# authenticates with (psc-server / pscpassword). KeyDB reads the same ACL
# `user` directives as Redis 6. Port defaults to 6379 (dev).
COPY ./redis-config/redis-dev.conf /etc/keydb/keydb.conf

HEALTHCHECK --interval=10s --timeout=5s --start-period=5s --retries=3 \
  CMD keydb-cli -p 6379 --user psc-server --pass pscpassword ping | grep -q PONG || exit 1

# The base image's entrypoint drops root and runs this against /data.
CMD ["keydb-server", "/etc/keydb/keydb.conf"]
