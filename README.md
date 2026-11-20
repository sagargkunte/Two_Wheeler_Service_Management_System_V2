# Two-Wheeler Service Management System

A web application for bike owners to find mechanics, book service appointments, and follow repair progress. Mechanics can manage their workshop profile and update the status of repair requests.

## Technologies

| Area | Technologies |
| --- | --- |
| Frontend | HTML5, CSS3, vanilla JavaScript |
| Backend | Java 21, Spring Boot 3.3.5, Spring Web (REST), Spring Data JPA, Maven |
| Database | MySQL (configured by default); H2 is included as a runtime dependency |
| Other libraries and services | Spring Mail, Spring Security Crypto (BCrypt), Leaflet, Lucide, OpenStreetMap, OpenRouteService |

The frontend is plain HTML, CSS, and JavaScript; it does not need npm or a JavaScript build step. The pages are included in the Spring Boot app and served from its static resources.

## Requirements

- Java 21 or newer
- Maven 3.6 or newer
- MySQL 8.x (or a compatible MySQL server)

## Run locally

1. **Create a MySQL database.** Start MySQL and create the database configured by the application:

   ```sql
   CREATE DATABASE two_wheeler_db;
   ```

2. **Configure the database connection.** Edit `backend-springboot/src/main/resources/application.properties` to match your MySQL host, database, username, and password. The checked-in file contains local development settings; change them to match your machine before running the app. Do not commit real passwords or API keys.

   Hibernate is configured to update the tables from the JPA entities on startup. `schema.sql` is also provided as a reference schema; it is not required for the normal startup flow.

3. **Start the application** from the repository root:

   ```bash
   cd backend-springboot
   mvn spring-boot:run
   ```

4. **Open the app** at [http://localhost:8080/](http://localhost:8080/). Spring Boot serves the frontend and API from the same origin.

To build and run the packaged application instead:

```bash
cd backend-springboot
mvn clean package
java -jar target/service-management-system-1.0.0.jar
```

## Demo accounts

The optional seed script `backend-springboot/src/main/resources/data.sql` contains these accounts, each with the password `password123`:

| Role | Email |
| --- | --- |
| Bike owner | `owner@example.com` |
| Mechanic | `amit@mechanic.com` |
| Mechanic | `suresh@mechanic.com` |
| Mechanic | `vikram@mechanic.com` |

The seed script uses `INSERT IGNORE` and expects the application tables to exist. Start the app once to let Hibernate create/update the tables, stop it, then import the seed file:

```bash
mysql -u YOUR_MYSQL_USER -p two_wheeler_db < backend-springboot/src/main/resources/data.sql
```

Restart the app and sign in. If you already have conflicting records, review the seed script before importing it.

## Email and maps

- Registration uses email OTP verification. SMTP settings are in `application.properties`; configure your own mail credentials if you want messages delivered. The email service includes a console fallback for local development.
- Browser location detection uses the browser geolocation API and may require HTTPS or `localhost` permission.
- Map tiles and geocoding use external services (OpenStreetMap/Nominatim); routing uses OpenRouteService and its configured API key. These features need internet access and may be subject to provider limits.

## Project layout

```text
backend-springboot/
  pom.xml                         Maven build and dependencies
  src/main/java/com/twowheeler/   Spring Boot application, controllers,
                                  services, JPA entities, repositories
  src/main/resources/
    application.properties       Database, server, mail, and routing config
    schema.sql                   Reference MySQL schema
    data.sql                     Optional demo data
    static/                      Frontend served by Spring Boot

frontend-html/                    Standalone copy of the HTML/CSS/JS frontend
README.md
```

The `frontend-html` directory is a separate copy of the pages. To use it independently, serve it with a local static web server and keep the backend running at `http://localhost:8080`; opening pages directly with `file://` may be restricted by browser security.
