# Sports Dashboard

A Spring Boot Premier League dashboard that displays the live league table and the latest finished matches for every club. Match data is fetched from Football-Data.org and saved in PostgreSQL.

## Features

- Live Premier League standings for all 20 clubs
- Click any club to load up to its last 10 finished matches
- Saves fetched matches to PostgreSQL with duplicate protection
- Live refresh button for the table and selected club
- REST endpoints for standings and team match history

## Tech Stack

- Java 17
- Spring Boot 3.3.x
- Spring Data JPA
- PostgreSQL
- Maven
- Plain HTML/CSS/JavaScript

## Prerequisites

- Java 17+
- Maven
- PostgreSQL database running locally
- Football-Data.org API key

## Local setup

1. Create a PostgreSQL database named `sports_dashboard`.
2. Set the database password and Football-Data.org API key in environment variables. The application properties file intentionally contains no secrets.

```bash
export DB_PASSWORD=your_database_password
export FOOTBALL_DATA_API_KEY=your_api_key_here
```

On Windows PowerShell:

```powershell
$env:DB_PASSWORD="your_database_password"
$env:FOOTBALL_DATA_API_KEY="your_api_key_here"
```

4. Run the application:

```bash
mvn spring-boot:run
```

5. Open the dashboard in your browser:

```text
http://localhost:8080
```

## API endpoints

- `GET /api/standings` — returns the live Premier League table
- `GET /api/teams/{teamId}/matches` — fetches up to 10 finished matches for a team and stores them
- `GET /api/matches` — returns all stored match data as JSON
- `POST /api/matches/refresh` — keeps the original Arsenal refresh endpoint

## Configuration

The application properties file includes placeholders for the database and API settings:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/sports_dashboard}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
football-data.api-key=${FOOTBALL_DATA_API_KEY}
football-data.base-url=${FOOTBALL_DATA_BASE_URL:https://api.football-data.org/v4}
```

Do not commit API keys, database passwords, or production connection strings.

## Publish it on GitHub

GitHub stores the source code but does not run this Spring Boot application or provide its PostgreSQL database. To make the dashboard publicly accessible:

1. Create a GitHub repository and push this project.
2. Create a web service on a host such as Render, Railway, or Fly.io, connected to that repository.
3. Create a managed PostgreSQL database with the same provider.
4. Configure `SPRING_DATASOURCE_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `FOOTBALL_DATA_API_KEY` in the host's environment settings.
5. Use `mvn clean package -DskipTests` as the build command and `java -jar target/sports-dashboard-0.0.1-SNAPSHOT.jar` as the start command.

The hosting provider will give you a public HTTPS URL that anyone can open.

## Notes

This is intentionally a lightweight foundation. The first version focuses on getting data flowing reliably from the API into the database and showing it properly on the page.
