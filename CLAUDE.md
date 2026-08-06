# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **ReMI** (Resource Maturation Interface) — a Spring Boot web application for the North Sea Transition Authority (NSTA) to track hydrocarbon resources through their lifecycle: Prospect → Discovery → Appraisal → Development → Production → Decommissioning.

Java 21, Spring Boot 3.5.x, Gradle. Main branch is `develop`.

## Build & Run Commands

### Backend (Gradle)
```bash
./gradlew build                  # Full build (tests + checkstyle + jar)
./gradlew test                   # Run all unit tests
./gradlew bootJar                # Build the Spring Boot executable JAR (remi.jar)
./gradlew checkstyleMain         # Run Checkstyle (max 0 warnings, no ignoring failures)
./gradlew generateJooq           # Regenerate JOOQ classes (starts/stops the DB container itself)
./gradlew sonarqube              # Run SonarCloud analysis
```

**Run a single test:**
```bash
./gradlew test --tests uk.co.nstauthority.hydrocarbonmaturation.path.to.TestClass
./gradlew test --tests uk.co.nstauthority.hydrocarbonmaturation.path.to.TestClass.methodName
```

**EPMQ messages JAR** — the `epmqmessage` package is published as a separate artifact (`remi-epmq-messages`) consumed by other Energy Portal services:
```bash
./gradlew epmqMessagesJar                    # Build the messages-only JAR
./gradlew epmqMessagesJarIntegrationTest     # Verify the published JAR deserialises correctly
```

### Frontend (Node/Gulp)
```bash
npm install                      # Install NPM dependencies
npx gulp buildAll                # sassCi + both rollup bundles
# Build design system submodule first if needed:
cd fivium-design-system-core && npm install && npx gulp buildAll && cd ..
```

`buildAll` runs `initFds` (copies FDS/GOV.UK assets into `templates/fds` and `public/assets`), compiles SASS, then produces two Rollup bundles. Output goes to `src/main/resources/public/assets/static/` and `templates/fds/`, both of which are **gitignored** — a fresh clone must run the frontend build (and the FDS submodule build) before the app renders correctly.

### Local Development Environment
Docker Compose is used for local services (PostgreSQL 14, S3Mock, ClamAV):
```bash
docker compose -f devtools/local-dev-compose.yml up -d
```
Run with the `development` Spring profile. See README.md for the full 8-step setup, including JOOQ generation and initial team/user setup.

## Architecture

### Backend Structure
`src/main/java/uk/co/nstauthority/hydrocarbonmaturation/`

Organised into domain modules, each typically containing controllers, services, repositories, forms/validators, and Freemarker templates.

**Core lifecycle domains:**
- **prospect / discovery / field** — Entry points for each resource type; creating, starting and updating a resource
- **submissiondetails** — By far the largest module. Every data-capture *section* of a submission is a subpackage (`generaldetails`, `opportunitydetails`, `sourceandseal`, `trapproperties`, `hydrocarboncase`, `*volumetrics`, `*volumetricparameters`, `*caseoutputs`, `reservoirproperties`, `supportingdocuments`, `tasklist`, `review`, …). Each follows the same controller/form/validator/service/entity shape — when adding a section, copy the closest existing subpackage rather than inventing a new pattern
- **submissions** — The `Submission` aggregate that owns submission details, plus submission emails
- **energyresource** — Resource groups and supporting documents
- **undevelopeddiscoveries / relinquishment** — Discovery-specific reporting and relinquishment journeys
- **regulatormanagedinformation / fieldregulatormanagedinformation / regulatorreportinginformation** — NSTA-only data held against resources
- **systemofrecord** — Search and view capability for recorded information
- **workarea** — Landing-page submission queries, split by operator vs responsible-team perspective

**Cross-cutting / supporting:**
- **authentication** — SAML2 service provider against Energy Portal Accounts Service
- **authorisation / teams** — Resource-level access rules; industry and internal (regulator) teams with roles
- **energyportal** — GraphQL integration with Energy Portal API (fields, licences, wells, organisation units, users)
- **externalapi** — Outbound REST API exposing field data to other services (pre-shared-key auth)
- **gis** — GIS/shapefile handling and coordinate management (OpenLayers on the frontend)
- **file** — S3 document uploads with ClamAV antivirus scanning
- **email / notify** — Notifications via GOV.UK Notify (digital notification library)
- **epmqmessage** — AWS SNS/SQS via Energy Portal Message Queue; also published as a standalone JAR
- **probabilitydistribution** — Reusable distribution-backed field pattern (see ADR 0000); shared by many volumetric sections
- **summary** — Summary-card and cross-tab rendering shared across sections
- **fds** — Fivium Design System component wrappers (file upload, navigation, search selectors)
- **audit** — Change tracking via Hibernate Envers (spring-data-envers)
- **reservoirs / formation / complexity / coordinateinput / decimalinput** — Shared domain and input primitives
- **configuration / mvc / exception / util** — Spring beans, interceptors, error handling, helpers
- **branding / breadcrumb / footer / topnavigation / cookies / feedback / technicalsupport / businesssupport** — Site chrome and support pages
- **metrics / correlationid** — Micrometer/StatsD metrics and request correlation

### Database
- PostgreSQL 14 locally, schema `remi`
- Flyway migrations in `src/main/resources/db/migration/` — versioned `V<n>__*.sql` plus repeatable `R__*.sql` for views (out-of-order migrations allowed)
- JPA for entity mapping; JOOQ for type-safe queries (search, work area, reporting views)
- JOOQ classes are **generated into `build/generated-src/jooq/main/`** (package `...hydrocarbonmaturation.generated.jooq`) — they are not checked in, so run `./gradlew generateJooq` after changing the schema. Checkstyle excludes `.../generated/**`
- Spring Session stored in DB (`SPRING_SESSION` table); 180-minute timeout

### Configuration
Spring properties live in `src/main/resources/config/`, not the resources root:
`application.properties` plus `application-development.properties`, `application-production.properties`, `application-debug.properties`, and `application-local-epas.yaml`.

### Frontend
- Templates: Freemarker (`.ftl`) in `src/main/resources/templates/`
- Styles: SASS/SCSS in `src/main/resources/scss/`, compiled via Gulp to `public/assets/static/css/`
- JavaScript: ES6 with Babel + Rollup, two bundles:
  - `src/main/resources/public/assets/remi/javascript/all.js` → `remi-all-bundle.js`
  - `src/main/resources/js/gis/allGis.js` → `remi-gis-bundle.js` (kept separate because OpenLayers can't be bundled with the main JS)
- GIS: OpenLayers 7.2.2 + proj4 for map rendering; esri-geometry-api + javadbf for shapefile handling
- Design system: `fivium-design-system-core` Git submodule, pinned to a `release/*-govuk-5.x` branch (wraps govuk-frontend 5.x)

### Application Profiles
- `development` — mocked email (test mode), S3Mock, local DB
- `production` — real AWS S3, SNS/SQS, GOV.UK Notify, SAML
- `test` — H2 in PostgreSQL compatibility mode; used by the vast majority of tests
- `integration-test` — Testcontainers PostgreSQL via the `jdbc:tc:` URL; used by a small number of tests that need real Postgres
- `debug` / `json-logging` — enhanced or structured logging

### Testing
- **JUnit 5** for unit tests; **Testcontainers** (PostgreSQL) for the few integration tests; **ArchUnit** for architecture enforcement (`src/test/java/.../architecture/`)
- ~745 test files under `src/test/java/`
- `./gradlew test` forces `spring.profiles.active=test`
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
| Feedback Management Service | User feedback submission |
| ShedLock | Distributed scheduler locking |

## Architecture Decision Records
`documentation/adr/` contains 19 ADRs (`0000`–`0018`) covering significant design decisions — consult these before changing patterns around probability distributions, document uploads, save-and-resume journeys, system-of-record searching/viewing/QA, GIS/shapefile handling and migration, submission update flows, resource corrections, and table usability.
