# Docker Compose with Traefik, Postgres and AWS S3 Backup

## AWS S3 Backup

### Schedule configuration

You can use cron patterns for setting up the backup interval.

Examples:

    SCHEDULE=@hourly
    SCHEDULE=0 0 */3 * * *

## Backup and Restore from S3 bucket

In the examples below the `container name` is the name of the backup container.

### Backup

You can create a manual backup running with:

    docker exec <container name> sh backup.sh

Example:

    docker exec fredbet-backup-1 sh backup.sh

### Restore

Restoring the latest backup. 

    docker exec <container name> sh restore.sh

Restoring from a specific backup:

    docker exec <container name> sh restore.sh <timestamp>

Example:

    docker exec fredbet-backup-1 sh restore.sh 2022-10-24T09:54:52

### Links

* https://github.com/eeshugerman/postgres-backup-s3
