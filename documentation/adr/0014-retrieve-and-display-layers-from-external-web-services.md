# Retrieve and display other layers on maps from external web services

* Author: Harshid Dattani
* Status: DRAFT
* Approved by:

## Requirements
We will be displaying user provided shapes on a map.
Along with the shape it would be useful to be able to pull in other layers from NSTA web services to display on the map: 
- Field determination areas
- Quad/blocks
- Licence blocks
- Field blobs

We need to know:
- Which web services we will be pulling these layers from
- How we will be querying these web services
- How we will be adding these layers to OpenLayers
- What order and styling we will be using in order to adhere to industry norms

## Layer sources
The NSTA provides an arcgis server which hosts several dozen geospatial layers and fortunately all the layers we care are hosted on this platform.
https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84

The following is the layers we need and which NSTA arcgis layer they correspond to:
- Field determination areas
  - Onshore - Public_WGS84/ENG_Onshore_FieldsDets_WGS84/FeatureServer
  - Onshore - Public_WGS84/UKCS_Offshore_FieldDets_WGS84/FeatureServer
- Quad/blocks
  - Public_WGS84/UKCS_ReferenceBlocks_WGS84/FeatureServer
- Licence blocks
  - Public_WGS84/UKCS_Licences_WGS84/FeatureServer
- Field blobs
  - Onshore - Public_WGS84/ENG_Onshore_Fields_WGS84/FeatureServer
  - Onshore - Public_WGS84/UKCS_Offshore_Fields_WGS84/FeatureServer

## Querying services and displaying in OpenLayer
Arcgis server is an industry-wide used platform so knowledge on how to query it is well understood even within our own company:

Dan Ashworth has written an extensive guide on how to query the platform: 
https://confluence.fivium.co.uk/x/GYP_B

It is possible to get the entire layer from the platform as a GeoJSON geometry which can be fed directly into OpenLayers.

Additionally, James Barnett has created a proof on concept on how pull NSTA provided layers directly into OpenLayers
https://bitbucket.org/fiviumuk/pipeline-works-authorisations/pull-requests/86
https://confluence.fivium.co.uk/x/UYkpAw

## Redundancy
We could also consider as a non MVP goal to download these geometries from NSTA and host them directly on the ReMI service. This way if the NSTA arcgis server goes down for a short period, we have a some sort of redundancy in place.

However, we would not want to use our hosted geometries as the primary source, as this would require we implement a mechanism to keep the layers up to date.

## Styling considerations
We should be styling the layers we show on the map in way the NSTA and the wider industry expects.

We will be displaying the layers in the follow order and styling
1. Quad/blocks - Dark Grey sold line around boundary
2. Licence blocks - Blue solid line with Yellow background
3. Field determination areas - Red solid line around boundary
4. Field blobs
   - Condensate - Orange background with darker solid line
   - Gas - Red background with darker solid line
   - Oil - Green background with darker solid line