

# user-settings-service-persistence

This service interacts with the database of system users account settings (email, phone etc.).

### Related components:
* [user-settings-service-api](https://gitbud.epam.com/mdtu-ddm/data-architecture/application/user-settings-service-api) - service, which interacts with web clients via HTTP, usually starts before this application
* Kafka for message exchanging with user-settings-service-api
* PostgreSQL database for data persistence

### Local deployment:
###### Prerequisites:

* Kafka is configured and running
* Database `settings` is configured and running

###### Settings database setup:
1. Create database `settings`
1. Run setup [script](https://gitbud.epam.com/mdtu-ddm/data-architecture/devops-application/citus/-/blob/master/platform-db/changesets/settings/00010_initial-db-setup.sql)

###### Steps:

1. Check `src/main/resources/application-local.yaml` and replace if needed:
   * data-platform.datasource... properties with actual values from local db
   * data-platform.kafka.boostrap and audit.kafka.bootstrap with url of local kafka
1. (Optional) Package application into jar file with `mvn clean package`
1. Add `--spring.profiles.active=local` to application run arguments
1. Run application with your favourite IDE or via `java -jar ...` with jar file, created on step 2

###### Additional information
All properties, related to other third-party services, not mentioned above (dso, keycloak, ceph) are mocked in `application-local.yaml` (like keycloak.realm=realm), to check such integrations, mock values must be replaced with real ones
