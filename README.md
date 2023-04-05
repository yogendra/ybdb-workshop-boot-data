[![SpringBoot CI with Gradle](https://github.com/srinivasa-vasu/yb-boot-data/actions/workflows/gradle-boot.yml/badge.svg?branch=devx)](https://github.com/srinivasa-vasu/yb-boot-data/actions/workflows/gradle-boot.yml)

[Spring Boot](https://spring.io/projects/spring-boot) makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".

This describes how to build a simple JPA based web application using Spring Boot framework for YSQL API using [Yugabyte JDBC Driver](https://docs.yugabyte.com/latest/integrations/jdbc-driver/).

## Prerequisites

- Follow [YB Quick start](https://docs.yugabyte.com/latest/quick-start/) instructions to run a local YugabyteDB cluster. Test YugabyteDB's YSQL API, as [documented](../../quick-start/explore/ysql/) so that you can confirm that you have YSQL service running on `localhost:5433`.
- You will need JDK 17 or later. You can use [SDKMAN](https://sdkman.io/install) to install the JDK runtime.

## Dependencies

This project depends on the following libraries.
```gradle
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.retry:spring-retry")
    implementation("com.yugabyte:jdbc-yugabytedb:42.3.5-yb-1")
    
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.flywaydb.flyway-test-extensions:flyway-spring-test:7.0.0")
    testImplementation("org.testcontainers:yugabytedb")
    testImplementation("org.testcontainers:junit-jupiter")
```
Update the driver dependency library **("com.yugabyte:jdbc-yugabytedb")** to the latest version. Grab the latest version from [Yugabyte JDBC driver](https://docs.yugabyte.com/latest/integrations/jdbc-driver/).

## Driver Configuration

Refer to the file `yb-ms-data/springboot/src/main/resources/application.yaml` in the project directory:

```yml
spring:
  jpa:
    properties:
      hibernate:
        connection:
          provider_disables_autocommit: true
        default_schema: todo
    open-in-view: false
  datasource:
    url: jdbc:yugabytedb://[hostname:port]/yugabyte?load-balance=true
    username: yugabyte
    password: yugabyte
    driver-class-name: com.yugabyte.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: false
```

- **url** is the JDBC connection string. You can set YugabyteDB driver specific properties such as `load-balance` and `topology-keys` as part of this connection string.
- **driver-class-name** is the JDBC driver class name.

Update the JDBC url with the appropriate `hostname` and `port` number details `jdbc:yugabytedb://[hostname:port]/yugabyte` in the application.yaml file. Remember to remove the square brackets. It is just a place holder to indicate the fields that need user inputs.

## Unit Test Configuration

It includes two spring profiles
- `src/main/resources/application-tcysql.yaml` - configuration for the [YugabyteDB Testcontainers module](https://www.testcontainers.org/modules/databases/yugabytedb/)
- `src/main/resources/application-tysql.yaml` - configuration for the local/external YugabyteDB instance

### application-tcysql.yaml

```
spring:
  datasource:
    url: jdbc:tc:yugabyte:2.16.0.0-b90:///yugabyte
    username: yugabyte
    password: yugabyte
```
Update yugabytedb version **jdbc:tc:yugabyte:2.16.0.0-b90:///yugabyte** accordingly.

### application-tysql.yaml

```
spring:
  flyway:
    clean-disabled: false
  jpa:
    show-sql: true
  datasource:
    url: jdbc:yugabytedb://127.0.0.2:5433/yugabyte
#    url: jdbc:yugabytedb://127.0.0.2:5433/yugabyte?load-balance=true
    username: yugabyte
    password: yugabyte
    driver-class-name: com.yugabyte.Driver
    hikari:
#      data-source-properties:
#        load-balance: true
#        sslMode: require
```
- **url** is the JDBC connection string. You can set YugabyteDB driver specific properties such as `load-balance` and `topology-keys` as part of this connection string. Update the `host` details accordingly.
- Driver specific properties can also be specified under **hikari:** sub-section.

## Build the app

To build the app:

```sh
gradle build
```

## Unit test the app

To test with Testcontainers:

```sh
gradle -Dspring.profiles.active=tcysql test 
```

To test with an externally running self/hosted YugabyteDB instance:

```sh
gradle -Dspring.profiles.active=tysql test 
```
