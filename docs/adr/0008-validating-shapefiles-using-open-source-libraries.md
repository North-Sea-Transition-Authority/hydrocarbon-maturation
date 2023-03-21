# Validating shapefiles using open source libraries - GeoTools vs ESRI Java Geometry API


## Overview

This document compares the use of the GeoTools library and the ESRI Java Geometry API to meet the following requirements:

1.  Validate a shapefile is the correct type (polygon, line, multi-line, etc).
2.  Validate a shapefile is in the correct coordinate system (WGS 84, OSGB, etc).
3.  Validate a shapefile has a valid geometry.

The library JTS (Java Topology Suite) was also considered but was ruled out as it does contain a method of importing ESRI Shape files.
It's worth noting that Geotools uses JTS for its geometry functionality.

## Requirement 1: Validate a shapefile is the correct type

### GeoTools

In Geotools, you can import a shapefile as a collection of features (`SimpleFeatureCollection`). You can get the feature type from the collection, which is bound to a JTS geometry type. You can derive the type from the geometry type class name.

Example:

```java
var shapefile = new File("path/to/shapefile.shp");
var dataStore = FileDataStoreFinder.getDataStore(Map.of("url", file.toURL()));
var typeName = dataStore.getTypeNames()[0];
var schema = dataStore.getSchema();
var featureSource = dataStore.getFeatureSource(typeName);
var features =  featureSource.getFeatures();
var featureType = features.getSchema();

String geometryType = featureType.getGeometryDescriptor().getType().getBinding().getSimpleName();
```

It would be possible to create an enum from that class name:

```java
enum GeometryType {
  POINT,
  LINESTRING,
  POLYGON,
  MULTIPOINT,
  MULTILINESTRING,
  MULTIPOLYGON,
  GEOMETRYCOLLECTION,
  UNKNOWN;

  public static GeometryType fromSimpleFeatureType(SimpleFeatureType type) {
    if (type == null) {
      return UNKNOWN;
    }

    var geometryClassName = type.getGeometryDescriptor().getType().getBinding().getSimpleName();

    return switch (geometryClassName) {
      case "Point" -> POINT;
      case "LineString" -> LINESTRING;
      case "Polygon" -> POLYGON;
      case "MultiPoint" -> MULTIPOINT;
      case "MultiLineString" -> MULTILINESTRING;
      case "MultiPolygon" -> MULTIPOLYGON;
      case "GeometryCollection" -> GEOMETRYCOLLECTION;
      default -> UNKNOWN;
    };
  }
}
```

### ESRI Java Geometry API

In ESRI Java Geometry API it is fairly trivial to get the geometry type from a shapefile. Using `GeometryEngine.geometryFromEsriShape` you import the shapefile, which gives you a `Geometry` class. You can grab the type directly from there.

Example:

```java
var shapefilePath = "path/to/shapefile.shp";
var fis = new FileInputStream(shapefilePath);
var geometry = GeometryEngine.geometryFromEsriShape(fis.readAllBytes(), Geometry.Type.Unknown);
Geometry.Type type = geometry.getType();
```

## Requirement 2: Validate a shapefile is in the correct coordinate system

### GeoTools

In Geotools, you can get the coordinate system from the feature type object. Then passing that to `CRS.lookupIdentifier` which returns a standard EPSG code.

Example:

```java
var crs = featureType.getCoordinateReferenceSystem();
var epsgCode = CRS.lookupIdentifier(crs, true);
```

It would be possible create an enum from that EPSG code:

```java
enum CoordinateSystem {
  WGS84("EPSG:4326"),
  OSGB("EPSG:27700"),
  ED50("EPSG:4230"),
  UNKNOWN("Unknown");

  private final String epsgCode;

  CoordinateSystem(String epsgCode) {
    this.epsgCode = epsgCode;
  }

  public static CoordinateSystem fromCoordinateReferenceSystem(CoordinateReferenceSystem crs) {
    if (crs == null) {
      return UNKNOWN;
    }

    try {
      var epsgCode = CRS.lookupIdentifier(crs, true);

      return Arrays.stream(values())
          .filter(coordinateSystem -> coordinateSystem.epsgCode.equals(epsgCode))
          .findFirst()
          .orElse(UNKNOWN);

    } catch (FactoryException e) {
      return UNKNOWN;
    }
  }
}
```

### ESRI Java Geometry API

ESRI Java Geometry API does not provide a way of getting the coordinate system (which it calls a spatial reference) from a shapefile or geometry.

## Requirement 3: Validate a shapefile has a valid geometry

### GeoTools

Geotools allows you to check the validity of each feature within a shapefile.

Example:

```java
boolean isValid(SimpleFeatureCollection featureCollection) {
  try (var iterator = featureCollection.features()) {
    while (iterator.hasNext()) {

      var feature = iterator.next();
      var geometry = (Geometry) feature.getDefaultGeometry();
      if (!geometry.isValid()) {
        return false;
      }
    }
    return true;
  }
}
```

### ESRI Java Geometry API

ESRI Java Geometry API does not provide a clear way of validating geometries. However, it does provide a way of checking whether a geometry is 'simple' against the OGC Simple Feature Specification which seems to provide the same level of validation JTS' isValid does on its Geometry objects. 

Example:

```java
var operatorSimplify = (OperatorSimplifyOGC) OperatorFactoryLocal.getInstance().getOperator(Operator.Type.SimplifyOGC);

var isSimple = operatorSimplify.isSimpleAsFeature(geometry, null, false, null, null);
// the first null is for the spatial reference "Can be null, then a very small tolerance value is derived from the geometry bounds."
// the second null is for a result enum. Allows consumers to know the reason for the failure.
// the third null is for a progress tracker "Allows cancellation of a long operation. Can be null."
```

## Review

### GeoTools

Pros:
- Large feature set
- Can get the coordinate system from a shapefile
- Much more examples on the internet

Cons:
- Complex and non-modern API
- The library has a steeper learning curve due to its comprehensive functionality.

### ESRI Java Geometry API

Pros:
- Much simpler and modern API
- Decent documentation
- Next to no example on the internet

Cons:
- Cannot get the coordinate system from a shapefile

## Decision

Considering that ESRI Java Geometry API outright fails in the requirement of being able to get the coordinate system from the shapefile, if getting this information is crucial, we are left with no choice but to use Geotools.

However, it remains to be seen how well the two libraries compare when it comes to converting shapefiles to other formats (GeoJSON, KML, etc.) and therefore how easy it would be to display the geometries from a shapefile on a map using Open Layers.