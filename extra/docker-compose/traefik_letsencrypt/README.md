# Docker Compose with Traefik, Postgres and Database Backup

## Schedule configuration

You can use cron patterns for setting up the backup interval.

Examples:

    SCHEDULE=@hourly
    SCHEDULE=0 0 */3 * * *

## Backup

For backups there is a `databasus` container that will create a backup of the database and upload it to an S3 bucket. The backup is scheduled using cron.

## docker-compose adjustments

You can use the build in AWS logging driver:

    logging: # use if running on EC2 in AWS
      driver: awslogs
      options:
        awslogs-region: eu-central-1
        awslogs-group: fredbet # create log group manually

### Links

* https://github.com/eeshugerman/postgres-backup-s3
