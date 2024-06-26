# PARS undeveloped discovery migration

## Requirement
The undeveloped discovery data from the existing PARS database needs to be migrated into the new ReMI service so that it is accessible and viewable in ReMI.

## Technical approach

- Provide NSTA with extract of all ReMI discoveries and their ReMI resource IDs
- Provide NSTA with undeveloped discovery template
- Convert populated excel template into CSV and manually import.
  - Follow similar approach as with PaDI migration
- Use ReMI resource ID to find the correct discovery to map to
  - If not found, note as failure - not found
  - If found, verify the other imported fields match with the found discovery
    - Resource name
    - Centre of interest quad block
    - Organisation name
    - Created date
    - Trap description
- Verify undeveloped discovery data meets business logic validation, if not note as failure with validation failure