# Converting Shapefile to GeoJSON - comparing GeoTools and ESRI Java Geometry API 
- Author: Harshid Dattani
- Status: DRAFT
- Approved by:

## Context

We need to convert ESRI shapefiles into GeoJSON format for the purposes of displaying the geometry on a screen.

There are two available Java libraries that can both import ESRI Shapefiles and export as GeoJSON: GeoTools and ESRI Java Geometry API

We evaluated each library based on the following factors:
1. Documentation, community support and examples
2. Active maintenance of library
3. API complexity

### GeoTools

#### Pros
- There are numerous community examples available for converting shapefiles to GeoJSON using GeoTools.
- It supports a wide range of geographic data formats, including shapefiles and GeoJSON.

#### Cons
- The GeoTools GeoJSON module is not actively maintained or supported.
- GeoTools does not produce completely valid GeoJSON, which could be why it's unsupported.
- GeoTools has limited documentation when it comes to using the GeoJSON module, making it difficult to understand its functionality.
- The API is not modern.

#### Example

```java
var file = ResourceUtils.getFile("classpath:" + "path/to/shapefile.shp");

var map = Map.of("url", file.toURL());
var dataStore = DataStoreFinder.getDataStore(map);
var typeName = dataStore.getTypeNames()[0];
var featureSource = dataStore.getFeatureSource(typeName);
var featureCollection = featureSource.getFeatures();

var writer = new StringWriter();
var featureJSON = new FeatureJSON();
featureJSON.setFeatureType(featureSource.getSchema());
featureJSON.writeFeatureCollection(featureCollection, writer);

var json = writer.toString();
```

### ESRI Java Geometry API

#### Pros
- The ESRI Java Geometry API has more comprehensive documentation, making it easy for new users to understand and use the library.
- It is actively maintained and supported, ensuring that the library is up-to-date and reliable.
- The API is more modern.

#### Cons
- The documentation lacks specific examples for loading shapefiles, which made it challenging to successfully load shapefiles.
- There are fewer community examples available for the ESRI Java Geometry API compared to GeoTools.

#### Example

```java
var path = 'path/to/shapefile.shp';
var file = ResourceUtils.getFile("classpath:" + path);

try (var stream = new FileInputStream(file)) {
    var shapefileGeometryCursor = new ShapefileGeometryCursor(stream);
    var geometry = shapefileGeometryCursor.next();
    
    var json = GeometryEngine.geometryToGeoJson(geometry);
}
```

Decision
----------
Based on the evaluation, we will be using the ESRI Java Geometry API for converting shapefiles to GeoJSON.
Despite the lack of specific shapefile examples, its better documentation, active maintenance, and support make it the more reliable option.
Crucially, it actually produces valid GeoJSON so is the only real option.