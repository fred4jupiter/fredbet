# Docker Compose with Traefik, Postgres and AWS S3 Backup

## AWS S3 Backup

### Backup

You can create a manual backup running with:

    docker exec <container name> sh backup.sh

### Restore

Restoring the latest backup:

    docker exec <container name> sh restore.sh

Restoring from a specific backup:

    docker exec <container name> sh restore.sh <timestamp>

### Links

* https://github.com/eeshugerman/postgres-backup-s3
