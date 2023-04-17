# User Experience for Shapefile Upload

* Author: Harshid Dattani
* Status: DRAFT
* Approved by:

## Requirements

Our service must enable users to upload shapefiles in a specific format, a zip file containing four separate files:
* .shp
* .shx
* .dbf
* .prj

The zip file must not contain more than one instance of each file type.

The shapefile must adhere to certain validation criteria, such as simple geometry and a specified coordinate reference system (CRS). If the shapefile fails to meet these criteria, the user should be notified.

Upon successful validation, users should see their uploaded shape displayed on a map. If the shape is not what they expected, they should have the option to clear the shapefile and upload a different one.

## Option 1 - User Journey

This option entails adding a task list item dedicated to shapefile uploading, which opens a journey over two (potentially four) screens.

1. The first screen involves a file upload widget alongside guidance on the validation criteria (zip file with required formats, simple geometry, and recognized CRS). If the shapefile fails validation, standard GDS validation errors will be displayed.
   - If geometry validation fails, then we will make sure to list out the technical reasons as to why it has failed, e.g. segments can touch other segments only at the end points.
2. The second screen displays the uploaded shape on a map, along with a table of shapefile attributes. Users can confirm the shape and attributes are correct or reject it, where they will be taken back to first screen to re-upload a new shapefile.

If hypothetically, there is a need to upload multiple shapefiles for a particular data item (e.g. Upload one or more field boundaries), then we would need to add two additional screens to handle the cardinality:
1. After the second screen, ask the user if they would like to upload another shapefile.
2. If they have already uploaded a shapefiles, when entering the journey; show the user an index of uploaded shapefiles, an option to remove each uploaded shapefile and an option to upload another.

If the need to upload multiple shapefiles is over multiple data items, then we will add a task list item for each data item.

### Pros:
* Simpler screens for users to follow
* Adheres to GDS pattern of one screen per question/action

### Cons:
* Necessitates a separate task list item for shapefile upload

## Option 2 - Shapefile Upload Widget

This option involves adding a file upload widget to an existing task screen, accompanied by the same guidance as in Option 1.

Validation occurs upon file upload, with errors displayed as file upload errors. The file upload codebase must be expanded to allow for custom file validation.

After a successful upload, a map preview appears below the file upload widget. The FDS file upload widget must also be extended to trigger the map preview upon successful upload.

### Pros:
* Eliminates the need for a dedicated task list item per shapefile upload

### Cons:
* Requires substantial extension of the existing file upload infrastructure

## Decision
We will be going with option 1 as this approach requires the least amount of extension of the existing file upload infrastructure and also delivers the best user experience.
ReMI currently only requires a single shapefile upload for a single data item (Shapefile outline for maximum case) so we will be implementing the simpler two screen journey.