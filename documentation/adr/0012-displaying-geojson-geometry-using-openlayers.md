# Displaying GeoJSON Geometry Using OpenLayers 
- Author: Harshid Dattani
- Status: DRAFT
- Approved by:

## Requirements

We want to display GeoJSON geometries on a map in the ReMI service.

## Existing pattern and ADR

We have already an existing pattern for displaying geometric data on a map and a corresponding ADR which can be found here:  
https://github.com/Fivium/energy-infrastructure-portal/blob/0f2ba36f42c0321a4b0ffd6a8968d11a7d26a986/docs/adr/0013-route-maps.md

### Summary of referenced ADR
The outcome of the ADR was to use OpenLayers as the client-side mapping library for displaying geometric data on a map. The requirements included support for displaying vector layers, tiled layers from OpenStreetMap, displaying arbitrary data, and support for `EPSG:27700` (OSGB). OpenLayers provides a feature-rich environment compared to its alternative, Leaflet.

To overlay geometric data onto OpenStreetMap, the geometric data need to be converted from `EPSG:27700 `to `EPSG:3857`, which can be done using proj4js. The conversion results in a slight loss of accuracy, but it is negligible for the application's purpose.

IE support will not be provided for the mapping feature, as it is considered an extra feature and not a requirement for completing the application form. Furthermore, less than 1% of users are on IE, and the majority of them can use another browser. A fallback placeholder will be displayed for IE users.

### Decisions

In order to keep a close level consistency between Fivium Digital - NSTA projects, we will be honouring the following outcomes of the referenced ADR:
- We will use OpenLayers to display the map.
  - As OpenLayers can handle GeoJSON directly as a vector layer, making it suitable for our requirements. 
- We will use proj4js for coordinate transformations.
- We will not support Internet Explorer

## Proof of concept

I have created a proof of concept building on the POC in the referenced ADR:

![adr0012-img01-poc.png](images%2Fadr0012-img01-poc.png)

### Implementation Details
To meet our requirement we will be focusing only on displaying the GeoJSON geometry.  

We will utilize OpenLayers' built-in support for loading GeoJSON as a **vector layer**:
- Fetch the GeoJSON data from an endpoint.
- Add the GeoJSON data to a **vector source**.
- Create a **vector layer** using the **vector source**.
- Style the **vector layer** as required.
- Add the **vector layer** to the map.

We will use proj4js for coordinate transformations:
- Load the required **projection** definitions (`EPSG:3857` and any other required **projections**) into proj4js.
- Register the **projections** with OpenLayers.
- Transform the GeoJSON data to `EPSG:3857 `if needed.


