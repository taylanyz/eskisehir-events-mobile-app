# PostgreSQL Development Setup Guide

## Prerequisites

- PostgreSQL installed locally (version 13+)
- Maven 3.6+
- Java 21

## Local PostgreSQL Setup (Windows)

### Option 1: Using PostgreSQL Installer
1. Download and install PostgreSQL from https://www.postgresql.org/download/windows/
2. During installation, set password for `postgres` user
3. PostgreSQL will run on `localhost:5432` by default

### Option 2: Using Docker (Recommended)
```bash
docker run --name postgre-dev \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=eskisehir_events_db \
  -p 5432:5432 \
  postgres:15
```

## Create Development Database

If database doesn't exist, create it:
```bash
psql -U postgres -c "CREATE DATABASE eskisehir_events_db OWNER postgres;"
```

Or use pgAdmin GUI.

## Running Backend with PostgreSQL

### Using Default Configuration (PostgreSQL localhost)
```bash
cd backend
mvn clean spring-boot:run
```

Assumes PostgreSQL is running on `localhost:5432` with default credentials:
- username: `postgres`
- password: `postgres`

### Using Environment Variables
```bash
mvn clean spring-boot:run \
  -Dspring-boot.run.arguments="--DB_USERNAME=your_user --DB_PASSWORD=your_password"
```

Or set them before running:
```bash
$env:DB_USERNAME="your_user"
$env:DB_PASSWORD="your_password"
mvn clean spring-boot:run
```

### Using H2 In-Memory Database (Quick Testing)
```bash
mvn clean spring-boot:run -Dspring.profiles.active=h2
```

This activates the `application-h2.properties` profile for fast testing without PostgreSQL.

## Flyway Migrations

Migrations are automatically applied on application startup:
- Located in: `src/main/resources/db/migration/`
- Convention: `V<version>__<description>.sql`
- Example: `V1__Initial_Schema.sql`

### Manual Migration Check
```bash
mvn flyway:info
```

### Validate Schema
```bash
mvn flyway:validate
```

## Troubleshooting

### Connection Refused
- Ensure PostgreSQL is running: `pg_isready -h localhost`
- Check port: `netstat -an | findstr 5432` (Windows)
- Verify credentials and database name

### Flyway Baseline Needed
If you have existing data and Flyway complains:
```sql
-- In PostgreSQL shell:
CREATE TABLE flyway_schema_history (
  installed_rank INTEGER NOT NULL PRIMARY KEY,
  version VARCHAR(50),
  description VARCHAR(100) NOT NULL,
  type VARCHAR(20) NOT NULL,
  script VARCHAR(1000) NOT NULL,
  checksum INTEGER,
  installed_by VARCHAR(100) NOT NULL,
  installed_on TIMESTAMP NOT NULL,
  execution_time INTEGER NOT NULL,
  success BOOLEAN NOT NULL
);

INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, installed_by, installed_on, execution_time, success)
VALUES (1, '1', 'Initial Schema', 'SQL', 'V1__Initial_Schema.sql', 'admin', now(), 1000, true);
```

## Development Workflow

1. **Start PostgreSQL:**
   ```bash
   # Docker
   docker start postgre-dev
   
   # Or if using system PostgreSQL, ensure it's running
   ```

2. **Run application:**
   ```bash
   cd backend
   mvn clean spring-boot:run
   ```

3. **Application will:**
   - Validate database schema (Flyway)
   - Apply any pending migrations automatically
   - Start on `http://localhost:8080`

4. **Access H2 Console (if using h2 profile):**
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:eventdb`
   - Username: `sa`
   - Password: (leave blank)

## Testing

Run tests with PostgreSQL:
```bash
mvn test
```

Run tests with H2:
```bash
mvn test -Dspring.profiles.active=h2
```

## Next Steps

- Phase 8: Backend service structure refinement
- Phase 9: AI/ML recommendation deepening
- Phase 10+: Advanced features and optimization
