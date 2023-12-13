# PaDI Migration

## Requirement
The data from the existing PaDI database needs to be migrated into the new ReMI service so that it is accessible and viewable by NSTA
and the operators who have access to specific prospect and discovery data not created through ReMI.

## Technical approach

- Convert excel extracts from PaDI into CSV and manually import into each of the environment.
- Have ability to clean down the imported data so that updated datasets can be imported
- Create PadI table SQL scripts using IntelliJ SQL generator. Save these as manual scripts to be run on each environment before imports
- Use pgAdmin to import data to each environment
- Use Postgres PLSQL scripts to migrate data from PaDI extracts to Remi data model. These will be ran manually on each environment
- Mark data from PaDI in the ReMI data model so that it is easily identifiable

## Prospects and discoveries

- A prospect will be created for each resource, grouped by the resource id and the organisation for that prospect
- A prospect group will be created for each prospect resource
- Possibly need an empty prospect resource for discoveries which are not linked to prospects in the PaDI dataset
- Discoveries will be created based on the prospects created above
- Relinquishments need discussion. Is this a new submission type? Is it a question on the prospect / discovery?

## Before import (CSV things to remember)

- Edit padi_res_header in Notepad++ to avoid quads and blocks being converted to dates
- Remove single quote characters from end of text columns using Notepad++ searching for '"
- Remove blank spaces from data columns where the data should be null
- Format number cells to remove commas
- Check where INT/NUMERIC data should be TEXT and vice-versa
- Convert end of line characters to UNIX using Notepad++
- Replace &amp; with &

## Considerations

- Null pointer exceptions should not be a concern due to the service design. The save and complete later pattern allows for partial datasets already.
The summaries will already cater for partial datasets
- Mappings of text data from PaDI to Energy Portal concepts such as organisations, quads/blocks will need some input from NSTA where no obvious mappings occur

## NSTA and BGS involvement

- An updated PaDI dataset should be provided by BGS when going to UAT and then before go-live
- A cutoff point for new data input into PaDI will need to be discussed with NSTA to ensure no data is missing when migrated into ReMI
- Discuss with NSTA fields which occur in PaDI but aren't captured in ReMI. Are any missing fields actually required?
- Full PaDI dataset for migration will be kept on the ReMI database for future access