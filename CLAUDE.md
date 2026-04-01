# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **ReMI** (Resource Maturation Interface) — a Spring Boot web application for the North Sea Transition Authority (NSTA) to track hydrocarbon resources through their lifecycle: Prospect → Discovery → Appraisal → Development → Production → Decommissioning.

## Build & Run Commands

### Backend (Gradle)
```bash
./gradlew build                  # Full build (tests + checkstyle + jar)
./gradlew test                   # Run all unit tests
./gradlew bootJar                # Build the Spring Boot executable JAR
./gradlew checkstyleMain         # Run Checkstyle (max 0 warnings, no ignoring failures)
./gradlew generateJooq           # Regenerate JOOQ classes (requires running DB container)
./gradlew sonarqube              # Run SonarCloud analysis
```

**Run a single test:**
```bash
./gradlew test --tests uk.co.nstauthority.hydrocarbonmaturation.path.to.TestClass
./gradlew test --tests uk.co.nstauthority.hydrocarbonmaturation.path.to.TestClass.methodName
```

### Frontend (Node/Gulp)
```bash
npm install                      # Install NPM dependencies
npx gulp buildAll                # Build all frontend assets (SASS + JS bundles)
# Build design system submodule first if needed:
cd fivium-design-system-core && npm install && npx gulp buildAll && cd ..
```

### Local Development Environment
Docker Compose is used for local services (PostgreSQL, S3Mock, ClamAV):
```bash
docker compose -f devtools/local-dev-compose.yml up -d
```
Run with the `development` Spring profile. See README.md for the full 20+ step setup, including JOOQ generation and initial team setup.

## Architecture

### Backend Structure
`src/main/java/uk/co/nstauthority/hydrocarbonmaturation/`

The application is organised into domain modules, each typically containing controllers, services, repositories, and Freemarker templates:

- **authentication** — SAML2 service provider against Energy Portal Accounts Service
- **authorisation** — Resource-level access control rules
- **discovery / prospect / field** — Core lifecycle domain modules; each manages a phase of the hydrocarbon resource lifecycle
- **energyportal** — GraphQL integration with Energy Portal API (fields, licences, wells, organisation units, users)
- **energyresource** — Resource groups and supporting documents
- **submissions** — Submission tracking and update workflows
- **systemofrecord** — Search and view capability for recorded information
- **gis** — GIS/shapefile handling and coordinate management (OpenLayers on the frontend)
- **file** — S3 document uploads with ClamAV antivirus scanning
- **email** — Notifications via GOV.UK Notify
- **epmqmessage** — AWS SNS/SQS via Energy Portal Message Queue
- **fds** — Fivium Design System component wrappers (file upload, navigation, search selectors)
- **audit** — Change tracking via Hibernate Envers
- **configuration** — Spring beans and application configuration

### Database
- PostgreSQL 14, schema `remi`
- Flyway migrations in `src/main/resources/db/` (out-of-order migrations allowed)
- JPA for entity mapping; JOOQ for type-safe queries — generated classes go to `src/main/java/.../generated/` (excluded from Checkstyle)
- Spring Session stored in DB (`SPRING_SESSION` table); 180-minute timeout

### Frontend
- Templates: Freemarker (`.ftl`) in `src/main/resources/templates/`
- Styles: SASS/SCSS in `src/main/resources/scss/`, compiled via Gulp
- JavaScript: ES6 with Babel + Rollup, entry points in `src/main/resources/js/`
- GIS: OpenLayers 7.2.2 + proj4 for map rendering; esri-geometry-api + javadbf for shapefile handling
- Design system: `fivium-design-system-core` Git submodule (wraps govuk-frontend 5.x)

### Application Profiles
- `development` — mocked email (test mode), S3Mock, local DB
- `production` — real AWS S3, SNS/SQS, GOV.UK Notify, SAML
- `test` — H2 in-memory database
- `debug` / `json-logging` — enhanced or structured logging

### Testing
- **JUnit 5** for unit tests; **Testcontainers** (PostgreSQL) for integration tests; **ArchUnit** for architecture enforcement
- ~744 test files under `src/test/java/`
- JaCoCo coverage reports generated automatically with tests

## Code Style
- Checkstyle rules in `devtools/checkstyle.xml` (version 9.3)
- Zero-warning policy — build fails on any Checkstyle violation
- Generated JOOQ classes (`generated/**`) are excluded from Checkstyle

## Key External Dependencies
| Dependency | Purpose |
|---|---|
| AWS S3 | Document storage |
| GOV.UK Notify | Email/SMS notifications |
| ClamAV | Antivirus scanning on upload |
| Energy Portal API (GraphQL) | Fields, licences, wells, users |
| Energy Portal Accounts Service | SAML2 authentication |
| AWS SNS/SQS (EPMQ) | Async messaging with Energy Portal |

## Architecture Decision Records
`documentation/adr/` contains 18+ ADRs covering significant design decisions — consult these before changing patterns around probability distributions, document uploads, save-and-resume journeys, system-of-record searching, GIS/shapefile handling, and submission update flows.