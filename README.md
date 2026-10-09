# PetConnect

PetConnect is a role-based pet adoption web application that connects adopters with shelter listings. Shelters manage their pets and adoption requests, adopters discover pets and submit applications, and administrators oversee users and listings.

## Features

| Role | Available workflows |
| --- | --- |
| **Adopter** | Browse and filter available pets, view pet details, receive preference-based compatibility scores, submit adoption applications, review application history, and exchange messages. |
| **Shelter** | Add and edit pet listings, review applications, approve or reject requests, and message adopters. |
| **Administrator** | View platform statistics, review pending pet listings, manage pets, and manage user accounts. |

Other application features include profile management, registration, BCrypt password hashing, per-account upgrade of existing salted SHA-256 hashes after successful login, and CSRF protection for POST requests.

## Technology

- Java 21 and Maven
- Java Servlets and JSP (`javax.servlet`)
- Apache Tomcat 9
- MySQL with JDBC (MySQL Connector/J)
- JUnit 5 and Mockito for automated tests

Tomcat 9 is required: this application uses `javax.servlet`, while Tomcat 10+ uses `jakarta.servlet`.

## Application structure

- `src/main/java/com/petconnect/servlet/` — request handlers for authentication, profiles, and role workflows.
- `src/main/java/com/petconnect/dao/` — database access.
- `src/main/java/com/petconnect/filter/` — role authorization and CSRF checks.
- `src/main/webapp/` — JSP views, shared fragments, and CSS.
- `src/test/` — automated unit and regression tests.
- `docs/test-cases.md` — manual test cases and their historical execution notes.

Requests under `/admin/*`, `/shelter/*`, and `/adopter/*` require a logged-in account with the matching role. Shelter operations also check pet ownership in the relevant servlet. The application does not include a database migration or automatic sample-data loader.

## Prerequisites

Install JDK 21, Maven 3.9 or later, MySQL, and Apache Tomcat 9. Confirm that `java -version` and `mvn -version` use Java 21.

## Database configuration

The application expects a configured MySQL schema named `petconnect`. Set these variables in the environment of the process that starts Tomcat (or in the IDE's Tomcat run configuration):

| Variable | Required | Default / example |
| --- | --- | --- |
| `DB_URL` | No | `jdbc:mysql://localhost:3306/petconnect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USER` | Yes | Your MySQL account name |
| `DB_PASSWORD` | Yes | Your MySQL password |

Tomcat must inherit the variables when its JVM starts; setting them in an unrelated terminal after Tomcat is already running has no effect. Never put real credentials in source code, this README, or version control. A `.env` file is not loaded automatically by Java or Tomcat, and any populated local configuration file must remain untracked.

### Database safety

The local `database/petconnect.sql` initializer is intentionally excluded from Git. It contains `DROP DATABASE IF EXISTS petconnect` and sample rows. **Do not run it against a database containing data you need.** The application and Maven do not execute SQL scripts automatically. Configure the schema separately using a reviewed, safe process. The initializer's sample password hashes are placeholders and cannot be used to sign in.

## Build, test, and deploy

Run the automated tests:

```sh
mvn test
```

Build the deployable WAR:

```sh
mvn package
```

The output is `target/petconnect.war`. Copy it to Tomcat 9's `webapps` directory and start Tomcat with the database environment variables configured. The default application context is `/petconnect`.

The automated tests use mocks and do not require MySQL. Manual checks should use existing accounts and data where possible; avoid destructive admin or shelter actions when validating a populated database. The cases in `docs/test-cases.md` describe prior manual checks and are historical, not a guarantee of current behavior on another machine.

## Security notes

- Every POST request requires a cryptographically random CSRF token associated with the browser session, including login, registration, profile changes, adoption applications, shelter and admin actions, messaging, logout, and multipart pet uploads.
- Logout is a POST action.
- Passwords are stored as BCrypt hashes with cost 12. Existing salted SHA-256 hashes are verified and upgraded to BCrypt after that account's next successful login.
- The existing `VARCHAR(255)` password column accommodates BCrypt hashes. Hash upgrades happen per account at login; they do not reset accounts or run a database-wide migration.
- Replace `PLACEHOLDER_HASH` values with real generated hashes before using any initializer-provided account in a disposable database.

## Repository hygiene

The repository excludes generated Maven output, local IDE settings, local secrets/configuration, and SQL dumps or database initializers. Before committing, review `git status`, the staged file list, and the diff to ensure that only intended files and no credentials or database contents are included.
