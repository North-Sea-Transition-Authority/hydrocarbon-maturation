# Field Data Sharing with New Services via ReMI Integration
* Author: Harshid Dattani

## Context
- The NSTA requires sharing field data from ReMI with new services to link applications and actions within any new service to a field. The source of this data is transitioning from a legacy system to ReMI (a new service).
- Integration must ensure that existing services, such as Field Consents and WIOS, which currently consume field metadata, remain unaffected by any changes in ReMI's mnemonic.

## Technical Approach
To be consistent with established patterns by other services (SCAP, PWA), a REST endpoint will be created for data sourcing from ReMI. 

### Required REST Endpoints
Based on the GraphQL queries in EPA for fields, the following REST endpoints will be implemented:
- Single Field Retrieval: `/api/external/v1/fields/{fieldId}` to retrieve a single field by ID.
- Multiple Fields Retrieval: `/api/external/v1/fields?fieldName={name}&status={status}&ids={id1,id2,...}` for querying multiple fields based on name, status, or IDs.

### Fields EPA GraphQL Queries and Schema
The current GraphQL queries for the Fields API is as follows:

```graphql
field(fieldId: Int): Field
fields(fieldName: String, fieldStatusIn: [FieldStatus], fieldIds: [Int]): [Field]
```

The current GraphQL schema for the Fields API is as follows:

```graphql
type Field {
fieldId: Int
fieldName: String
shore: FieldShore
shoreDisplayName: String
status: FieldStatus
statusDisplayName: String
subType: FieldSubType
subTypeDisplayName: String
geographicArea: FieldGeographicArea
geographicAreaDisplayName: String
fieldOperator: OrganisationUnit
licences: [Licence]
}

enum FieldShore { ONSHORE, OFFSHORE, UNKNOWN }
enum FieldStatus { STATUS000, STATUS050, ..., STATUS9999, UNKNOWN }
enum FieldSubType { DETERMINED, SUB_AREA, UNDETERMINED }
enum FieldGeographicArea { CNS, IS, LAND, NNS, SNS, WOS, UNKNOWN }
```
### Mapping Tables between EPA Fields Enums to ReMI Fields Enums

### FieldShore Mapping Table

| EPA Enum | ReMI Enum |
|----------|-----------|
| ONSHORE  | ONSHORE   |
| OFFSHORE | OFFSHORE  |
| UNKNOWN  |           |

### FieldStatus Mapping Table

Note: Not all `FieldStatus` enums exist in ReMI

| EPA Enum   | ReMI Enum  |
|------------|------------|
| STATUS200  | STATUS_200 |
| STATUS300  | STATUS_300 |
| STATUS400  | STATUS_400 |
| STATUS500  | STATUS_500 |
| STATUS600  | STATUS_600 |
| STATUS700  | STATUS_700 |
| STATUS799  | STATUS_799 |
| STATUS800  | STATUS_800 |
| STATUS899  | STATUS_899 |
| STATUS900  | STATUS_900 |
| STATUS000  |            |
| STATUS050  |            |
| STATUS100  |            |
| STATUS450  |            |
| STATUS610  |            |
| STATUS620  |            |
| STATUS1000 |            |
| STATUS2000 |            |
| STATUS9999 |            |
| UNKNOWN    |            |

### FieldSubType Mapping Table

| EPA Enum     | ReMI Enum                    |
|--------------|------------------------------|
| DETERMINED   | DETERMINED                   |
| SUB_AREA     | SUB_AREA_OF_DETERMINED_FIELD |
| UNDETERMINED | UNDETERMINED                 |

### FieldGeographicArea Mapping Table

| EPA Enum | ReMI Enum          |
|----------|--------------------|
| CNS      | CENTRAL_NORTH_SEA  |
| IS       | EAST_IRISH_SEA     |
| LAND     | ONSHORE            |
| NNS      | NORTHERN_NORTH_SEA |
| SNS      | SOUTHERN_NORTH_SEA |
| WOS      | WEST_OF_SHETLAND   |
| UNKNOWN  |                    |