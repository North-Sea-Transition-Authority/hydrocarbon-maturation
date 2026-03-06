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
- Java 21
- Node 18 + NPM
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
- The following environment variables are required when using this profile:

| Environment Variable            | Description                                                                                                       |
|---------------------------------|-------------------------------------------------------------------------------------------------------------------|
| NOTIFY_API_KEY                  | [TPM link](https://tpm.fivium.co.uk/index.php/pwd/view/2035)                                                      |
| REMI_EMAIL_TEST_RECIPIENT       | Your intended recipient during testing for emails                                                                 |
| REMI_EPMQ_AWS_ACCESS_KEY_ID     | AWS access key id for SNS/SQS. <ul><li>[Local dev](https://tpm.fivium.co.uk/index.php/prj/view/147)</li></ul>     |
| REMI_EPMQ_AWS_SECRET_ACCESS_KEY | AWS secret access key for SNS/SQS. <ul><li>[Local dev](https://tpm.fivium.co.uk/index.php/prj/view/147)</li></ul> |
| REMI_EPMQ_ENVIRONMENT_SUFFIX    | Something unique per environment. For local dev this can be your initials, e.g. `rm`.                             |

### Production
- In your IntelliJ run configuration for the Spring app, include `production` in your active profiles
- The following environment variables are required when using this profile:

| Environment Variable                    | Description                                                                                                                                                                                                                                                                                                     |
|-----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| REMI_DATABASE_URL                       | The URL to the database the service connect to                                                                                                                                                                                                                                                                  |
| REMI_DATABASE_PASSWORD                  | Database schema password                                                                                                                                                                                                                                                                                        |
| REMI_SAML_CERTIFICATE                   | The x509 certificate string                                                                                                                                                                                                                                                                                     |
| REMI_ENABLE_FLYWAY_OUT_OF_ORDER         | Flyway patch ordering                                                                                                                                                                                                                                                                                           |
| REMI_ENABLE_STACK_TRACE                 | Determines if the stack trace will show on error pages                                                                                                                                                                                                                                                          |
| S3_ACCESS_TOKEN                         | S3 username for document uploads / downloads                                                                                                                                                                                                                                                                    |
| S3_SECRET_TOKEN                         | S3 secret for document uploads / downloads                                                                                                                                                                                                                                                                      |
| S3_DEFAULT_BUCKET                       | S3 default bucket for document uploads / downloads                                                                                                                                                                                                                                                              |
| CLAMAV_HOST                             | Virus scanner host location                                                                                                                                                                                                                                                                                     |
| FILE_UPLOAD_MAX_ALLOWED_SIZE            | Maximum file upload size in bytes                                                                                                                                                                                                                                                                               |
| FILE_UPLOAD_ALLOWED_EXTENSIONS          | Allowed file extensions for document uploads                                                                                                                                                                                                                                                                    |
| REMI_ENERGY_PORTAL_API_URL              | The energy portal API URL                                                                                                                                                                                                                                                                                       |
| REMI_ENERGY_PORTAL_API_TOKEN            | Security key for access the energy portal API                                                                                                                                                                                                                                                                   |
| REMI_ENERGY_PORTAL_LOGOUT_URL           | Energy Portal logout URL                                                                                                                                                                                                                                                                                        |
| REMI_ENERGY_PORTAL_LOGOUT_KEY           | The pre-shared logout key for the energy portal.                                                                                                                                                                                                                                                                |
| REMI_EMAIL_MODE                         | Can be test or production. Test mode will redirect all outbound emails to the test recipient(s)                                                                                                                                                                                                                 |
| REMI_EMAIL_TEST_RECIPIENT               | If email is test mode, who to send emails to. Value can be a CSV list                                                                                                                                                                                                                                           |
| REMI_DATAWAREHOUSE_USER_PASSWORD        | The database password for the `datawarehouseuser` database user                                                                                                                                                                                                                                                 |
| NOTIFY_API_KEY                          | The GOV.UK Notify key                                                                                                                                                                                                                                                                                           |
| NOTIFY_CALLBACK_TOKEN                   | The GOV.UK Notify callback token                                                                                                                                                                                                                                                                                |
| NOTIFY_PROXY_HOST                       | The GOV.UK Notify proxy host                                                                                                                                                                                                                                                                                    |
| NOTIFY_PROXY_PORT                       | The GOV.UK Notify proxy port                                                                                                                                                                                                                                                                                    |
| FMS_URL_BASE                            | The URL for the FMS instance on your environment                                                                                                                                                                                                                                                                |
| FMS_SUBMIT_ENDPOINT                     | The FMS endpoint where feedback will be sent. Defaults to /api/v1/save-feedback                                                                                                                                                                                                                                 |
| FMS_PRESHARED_KEY                       | This is the api key used when making requests                                                                                                                                                                                                                                                                   |
| FMS_CONNECTION_TIMEOUT                  | Connection timeout in seconds. Defaults to 20                                                                                                                                                                                                                                                                   |
| REMI_API_PRE_SHARED_KEY                 | The API key used to validate requests from the Energy Portal API                                                                                                                                                                                                                                                |
| REMI_ANALYTICS_ENERGY_PORTAL_IDENTIFIER | The google analytics tag used for all apps                                                                                                                                                                                                                                                                      |
| REMI_ANALYTICS_SERVICE_IDENTIFIER       | The service specific google analytics tag                                                                                                                                                                                                                                                                       |
| ALLOW_FIELD_SUBMISSIONS                 | Allow field submissions and field search (default: false)                                                                                                                                                                                                                                                       |
| REMI_ACTUATOR_ADMIN_USER_PASSWORD       | Password for the actuator admin user                                                                                                                                                                                                                                                                            |
| REMI_EPMQ_AWS_ACCESS_KEY_ID             | AWS access key id for SNS/SQS. <ul><li>[Dev](https://tpm.fivium.co.uk/index.php/prj/view/147)</li><li>[ST](https://tpm.fivium.co.uk/index.php/prj/view/147)</li><li>[Pre-prod](https://tpm.fivium.co.uk/index.php/pwd/view/2217)</li><li>[Prod](https://tpm.fivium.co.uk/index.php/pwd/view/2217)</li></ul>     |
| REMI_EPMQ_AWS_SECRET_ACCESS_KEY         | AWS secret access key for SNS/SQS. <ul><li>[Dev](https://tpm.fivium.co.uk/index.php/prj/view/147)</li><li>[ST](https://tpm.fivium.co.uk/index.php/prj/view/147)</li><li>[Pre-prod](https://tpm.fivium.co.uk/index.php/pwd/view/2217)</li><li>[Prod](https://tpm.fivium.co.uk/index.php/pwd/view/2217)</li></ul> |
| REMI_EPMQ_ENVIRONMENT_SUFFIX            | Something unique per environment, e.g. `dev`.                                                                                                                                                                                                                                                                   |
| EPAS_SAML_ENTITY_ID                     | The entity ID is either:<ul><li>`energy-portal-accounts-service-dev` for dev/local</li><li>`energy-portal-accounts-service-st` for st</li><li>`energy-portal-accounts-service-uat` for uat</li><li>`energy-portal-accounts-service-prod` for prod</li></ul>                                                     |
| EPAS_SAML_LOGIN_URL                     | The login url e.g. https://desnz.itportal.dev.fivium.co.uk/accounts/saml/login                                                                                                                                                                                                                                  |
| EPAS_LOGOUT_REQUEST_URL                 | The logout url e.g https://desnz.itportal.dev.fivium.co.uk/accounts/service-provider-sign-out                                                                                                                                                                                                                   |
| EPAS_SAML_BASE_URL                      | The base url (e.g. https://desnz.itportal.dev.fivium.co.uk)                                                                                                                                                                                                                                                     |
| EPAS_SPRING_BOOT_STARTER_PRESHARED_KEY  | The preshared key that EPAS will use when calling out to this service, is already set up on all environments                                                                                                                                                                                                    |

### Logging
ReMI can log in either JSON or text mode.
In order to turn on JSON logging, set the profile 'json-logging'. This will automatically include any MDC attributes.

### 3. Initialise the Fivium Design System
- `git submodule update --init --recursive`
- `cd fivium-design-system-core && npm install && npx gulp buildAll && cd ..`

### 4. Build frontend components
- `npm install`
- `npx gulp buildAll`

### 5. Generate Jooq classes
- You will need to stop the local ReMI db container
- Execute the gradle task `generateJooq`. You will need to re-generate when you change the database.
- Some errors/warnings are fine as long as the task was _successful_
- Then restart the db container

### 6. Run the app
- Create a run configuration for the Spring app and start the application.
- The application will be running on `localhost:8080/remi/<endpoint>`

### 7. Add the users and teams
- Run `teams.sql` followed by `team_roles.sql` found in the `resources/manualscripts/teams` directory.

### 8. Login to the app
- Go to `localhost:8080/remi/work-area` and log in as `administrator@remi.co.uk`

### 8. Checkstyle
1. In Intellij install the Checkstyle-IDEA plugin (from third-party repositories)
2. Go to File > Settings > Tools > Checkstyle
3. Click the plus icon under "Configuration File"
4. Select "Use a local Checkstyle file"
5. Select `devtools/checkstyle.xml`
6. Check the "Active" box next to the new profile

Note that Checkstyle rules are checked during the build process and any broken rules will fail the build.

