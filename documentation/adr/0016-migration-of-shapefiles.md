# Migration of Shapefiles From Legacy System to ReMI

* Author: Harshid Dattani
* Status: APPROVED
* Approved by: Danny Betts, James Garry

## Requirements

We have a collection of shapefiles from a legacy system that needs to be migrated into ReMI. The legacy data has largely already been migrated successfully, and the legacy resource submissions can be identified by the `legacy_res_data_id` in submission details. The shapefiles contain an attribute named `pid`, which directly maps to a resource submission via the `padi_res_data_header` table.

We need to create a migration that is repeatable, validates shapefiles as part of the process, stores the validated geometries on s3 directly and links them to the appropriate resources.

## Solution

### Two-Step Migration Pipeline

We propose the following steps using a Java based migration:

1. **Data Validation**: A migration service will be created that verifies the association between shapefiles and their corresponding resources.This involves matching the `pid` in the shapefiles with the `polygon_id` in `padi_res_data_header`, as well as validating the geometries within each shapefile.
   If validation fails, a table record with the `pid` and the respective error will be logged for the invalid geometry.
   Incorporating validation during the migration process will allow for early detection and correction of any issues, leading to improved data integrity and reduced need for post-migration validation.

2. **Data Conversion and Storage**: After the validation, we'll proceed within the same migration service to upload the shapefiles to an S3 bucket.
   As part of this process, each geometry that has passed validation will be converted to GeoJSON format.
   As we know which resource each geometry is associated with, the upload will be recorded as a supporting document against the corresponding resource.
   A record logging the successful migration of the geometry will be created against its `pid`.

Validating all geometries before uploading should also minimize the risk of orphan files being created on S3 due to failed validations, thus maintaining a clean and accurate data storage system.