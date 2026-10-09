# PetConnect

PetConnect is a Java web application for pet adoption and shelter management. It provides adopter, shelter, and administrator workflows for browsing pets, managing adoption applications, and exchanging messages.

## Technology

- Java 21
- Maven, packaged as a WAR
- JSP and Java Servlets using `javax.servlet`
- Apache Tomcat 9
- MySQL with JDBC (MySQL Connector/J)
- JUnit 5 and Mockito for unit/regression tests

Tomcat 9 is required for this version because the project uses the `javax.servlet` API. Tomcat 10+ uses `jakarta.servlet` and is not compatible without migrating the application.

## Prerequisites

Install a JDK 21, Maven 3.9 or later, MySQL, and Apache Tomcat 9. Confirm that `java -version` and `mvn -version` use Java 21.

## Database configuration

The application expects a MySQL database named `petconnect`. Configure the environment of the JVM that runs the application:

| Variable | Required | Default / example |
| --- | --- | --- |
| `DB_URL` | No | `jdbc:mysql://localhost:3306/petconnect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USER` | Yes | Your MySQL account name |
| `DB_PASSWORD` | Yes | Your MySQL password |

Do not put real credentials in Java source, this README, or version control. Set the variables in your IDE's run configuration or in the environment used to start Tomcat. Tomcat must inherit these variables when its JVM starts; setting them in an unrelated terminal after Tomcat is already running has no effect. Missing or blank `DB_USER` / `DB_PASSWORD` values produce a clear configuration error.

For local development, a gitignored `.env` file may be used as a private place to keep values, but Java and Tomcat do not load `.env` automatically. Export the values into the launching process or configure them in the IDE/Tomcat startup environment. Never add a populated local config file to Git.

## Database setup safety

The local `database/petconnect.sql` initializer is deliberately excluded from Git. It contains `DROP DATABASE IF EXISTS petconnect` and sample rows. **Do not execute it against any database containing data you need.** It deletes the existing `petconnect` database before recreating it. No SQL script is run automatically by the application, Maven, or this repository setup. Review the script and make a backup before any deliberate use; preferably create a fresh disposable development database and adapt the script to avoid the drop statement.

Create/configure the schema separately using a safe, reviewed process before starting the app. The application does not migrate or seed the database automatically. The initializer's sample password hashes are placeholders and are not usable login credentials.

## Build and deploy

From the project root:

```sh
mvn clean package
```

This creates `target/petconnect.war`. Copy that WAR into Tomcat 9's `webapps` directory, ensuring the Tomcat process has `DB_USER` and `DB_PASSWORD` configured, then start/restart Tomcat. The app is available under `/petconnect` by default. Build output under `target/` is generated and ignored by Git.

## Tests

Run the automated test suite with:

```sh
mvn test
```

The unit/regression tests use mocks and do not require connecting to MySQL. `docs/test-cases.md` records manual application checks and their prior execution context; those results are historical and should not be treated as a fresh run for another machine.

## Authentication and request security

Every POST request requires a cryptographically random synchronizer token tied to the browser session. Forms obtain it from the session, including login, registration, profile changes, adoption applications, shelter/admin actions, messaging, logout, and multipart pet image uploads. Logout is a POST action. Passwords are stored as BCrypt hashes (cost 12); the existing `VARCHAR(255)` column is sufficient. Existing salted SHA-256 hashes remain verifiable and are upgraded to BCrypt after that account's next successful login. This migration is automatic and per-account; it does not run SQL or reset accounts. The initializer's `PLACEHOLDER_HASH` values remain unusable until replaced with actual hashes from `PasswordHashGenerator`.

## Repository contents and publishing

The project repository intentionally excludes Maven output, local IDE settings, local secrets/configuration, and SQL dumps or database initializers. Review `git status` and the staged file list before committing or pushing. This setup does not create a commit or publish the repository.
