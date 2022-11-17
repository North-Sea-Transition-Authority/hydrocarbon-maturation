# Resource Maturation Interface

## Background

The Resource Maturation Interface (ReMI) is used to track the life cycle of oil and gas resources
from it being a potential hydrocarbon deposit (Prospect), into Discovery where the resource
is appraised for commercial viability. From there a development plans can be put together by
operators which are reviewed by NSTA. If agreed then the plans are put into action and the
operator will move into the Production phase where oil and / or gas is extracted. Finally
production will end when the field is no longer economically viable and the field will be
decommissioned. These fields may later be re-used for carbon capture, or re-developed to
extract further oil and gas.

The service will capture the information required for the above life cycle of the resource,
as well as acting as a system of record for the captured information to be able to view and
track changes over time.

## Pre-requisites
- Java 17
- Node LTS + NPM
- [Docker for Windows](https://hub.docker.com/editions/community/docker-ce-desktop-windows)
  (See [Docker setup](https://confluence.fivium.co.uk/display/JAVA/Java+development+environment+setup#Javadevelopmentenvironmentsetup-Docker)
  for further information about adding your account to the `docker-users` group)

## Setup

### 1. Run the backend services
- Ensure that you have [Docker for Windows](https://hub.docker.com/editions/community/docker-ce-desktop-windows)
  installed and running (or an alternative way of running docker).
- Run the backing services defined in the `local-dev-compose.yml`. This can be done by clicking the run icon
  next to `services` when in the file.
    - If IntelliJ doesn't detect the file as a docker compose file automatically you may need to
      [Associate docker-compose as file type](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360009394620-Associate-docker-compose-as-file-type) manually.

### 2. Add the required profile

### Development
- In your IntelliJ run configuration for the Spring app, include `development` in your active profiles

### Production
- In your IntelliJ run configuration for the Spring app, include `production` in your active profiles
- The following environment variables are required when using this profile:

| Environment Variable            | Description                                                                                        |
|---------------------------------|----------------------------------------------------------------------------------------------------|
| REMI_DATABASE_URL               | The URL to the database the service connect to                                                     |
| REMI_DATABASE_PASSWORD          | Database schema password                                                                           |
| REMI_SAML_ENTITY_ID             | Fox instance URL (dev: https://itportal.dev.fivium.local/engedudev1/fox)                           |
| REMI_SAML_CERTIFICATE           | The x509 certificate string                                                                        |
| REMI_SAML_LOGIN_URL             | The URL to hit the `login` entry theme of the SAML login module                                    |
| REMI_SAML_BASE_URL              | The url prior to the `/${serverContext}` part of the url  (E.G: https://itportal.dev.fivium.local) |
| REMI_ENABLE_FLYWAY_OUT_OF_ORDER | Flyway patch ordering                                                                              |
| REMI_ENABLE_STACK_TRACE         | Determines if the stack trace will show on error pages                                             |
| S3_ACCESS_KEY                   | S3 username for document uploads / downloads                                                       |
| S3_SECRET_KEY                   | S3 secret for document uploads / downloads                                                         |
| CLAMAV_HOST                     | Virus scanner host location                                                                        |
| FILE_UPLOAD_MAX_ALLOWED_SIZE    | Maximum file upload size in bytes                                                                  |
| FILE_UPLOAD_ALLOWED_EXTENSIONS  | Allowed file extensions for document uploads                                                       |
| REMI_ENERGY_PORTAL_API_URL      | The energy portal API URL                                                                          |
| REMI_ENERGY_PORTAL_API_TOKEN    | Security key for access the energy portal API                                                      |
| REMI_ENERGY_PORTAL_LOGOUT_URL   | Energy Portal logout URL                                                                           |

### 3. Initialise the Fivium Design System
- `git submodule update --init --recursive`
- `cd fivium-design-system-core && npm install && npx gulp build && cd ..`

### 4. Build frontend components
- `npm install`
- `npx gulp buildAll`

### 5. Run the app
Create a run configuration for the Spring app and start the application.

The application will be running on `localhost:8080/remi/<endpoint>`

## Development setup

### Checkstyle
1. In Intellij install the Checkstyle-IDEA plugin (from third-party repositories)
2. Go to File > Settings > Checkstyle
3. Click the plus icon under "Configuration File"
4. Select "Use a local Checkstyle file"
5. Select `devtools/checkstyle.xml`
6. Check the "Active" box next to the new profile

Note that Checkstyle rules are checked during the build process and any broken rules will fail the build.

