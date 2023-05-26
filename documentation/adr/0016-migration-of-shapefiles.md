# Migration of Shapefiles From Legacy System to ReMI Using Flyway

* Author: Harshid Dattani
* Status: DRAFT
* Approved by:

## Requirements

We have a collection of shapefiles from a legacy system that needs to be migrated into ReMI. The legacy data has largely already been migrated successfully, and the legacy resource submissions can be identified by the `legacy_res_data_id` in submission details. The shapefiles contain an attribute named `pid`, which directly maps to a resource submission via the `padi_res_data_header` table.

We need to create a migration that is repeatable, validates shapefiles as part of the process, stores the validated geometries on s3 directly and links them to the appropriate resources.

## Solution
### Flyway Java Migration

The adoption of Flyway's Java migration functionality will be a crucial component of this solution.

A significant difference lies in the organization of Java migrations in contrast to SQL migrations. The Java migrations need to be kept in a single project defined package and a configuration property needs to be defined to let flyway know of the location:
`spring.flyway.locations=classpath:uk/co/nstauthority/hydrocarbonmaturation/migration`

Fortunately it is possible to use both Java and SQL migrations in Flyway at the same time. We just need to make sure the migrations don't have conflicting versions/names.

See [spring docs](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/howto.html#howto.data-initialization.migration-tool.flyway) for more information.

### Two-Step Migration Pipeline

We propose the following steps using Flyway's Java migration:

1. **Data Validation**: A Flyway Java migration will be created that verifies the association between shapefiles and their corresponding resources.This involves matching the `pid` in the shapefiles with the `polygon_id` in `padi_res_data_header`, as well as validating the geometries within each shapefile.
   If validation fails, a table record with the `pid` and the respective error will be logged for the invalid geometry.
   Incorporating validation during the migration process will allow for early detection and correction of any issues, leading to improved data integrity and reduced need for post-migration validation.

2. **Data Conversion and Storage**: After the validation, we'll proceed within the same Flyway Java migration to upload the shapefiles to an S3 bucket.
   As part of this process, each geometry that has passed validation will be converted to GeoJSON format.
   As we know which resource each geometry is associated with, the upload will be recorded as a supporting document against the corresponding resource.
   A record logging the successful migration of the geometry will be created against its `pid`.

Flyway's built-in rollback mechanism will be utilized to ensure that any problematic migrations can be effectively reversed, ensuring that the system can be restored to its original state if required.
Validating all geometries before uploading should also minimize the risk of orphan files being created on S3 due to failed validations, thus maintaining a clean and accurate data storage system.