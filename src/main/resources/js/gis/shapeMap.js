import GeoJSON from 'ol/format/GeoJSON.js';
import Map from 'ol/Map.js';
import TileLayer from 'ol/layer/Tile';
import {OSM} from 'ol/source';
import VectorLayer from 'ol/layer/Vector.js';
import VectorSource from 'ol/source/Vector.js';
import View from 'ol/View.js';
import {Fill, Style} from 'ol/style.js';

export default class ShapeMap {

  constructor(module) {
    this.module = module;
    this.geoJsonDataUrl = this.module.getAttribute("data-geojson-url");
  }

  async init() {

    // TODO HM-480 - Get CRS from shapefile and register projection using proj4

    const shapeLayer = this.getShapeLayer();

    const backgroundLayer = this.getBackgroundLayer();

    // TODO HM-479 - Add NSTA layers to reusable shape map
    const map = this.createMap(backgroundLayer, shapeLayer);

    this.fitMapToExtentOfShapeLayer(shapeLayer, map);
  }

  getShapeLayer() {
    return new VectorLayer({
      source: new VectorSource({
        url: this.geoJsonDataUrl,
        format: new GeoJSON(),
      }),
      style: function (feature) {
        return new Style({
          fill: new Fill({
            color: '#eeeeee',
          }),
        });
      },
    });
  }

  getBackgroundLayer() {
    return new TileLayer({
      source: new OSM()
    });
  }

  createMap(backgroundLayer, shapeLayer) {
    return new Map({
      layers: [
        backgroundLayer,
        shapeLayer
      ],
      target: this.module,
      view: new View({
        center: [0, 0],
        zoom: 1,
      }),
    });
  }

  fitMapToExtentOfShapeLayer(shapeLayer, map) {
    try {
      map.getView().fit(shapeLayer.getExtent(), {
        padding: [50, 50, 50, 50],
        maxZoom: 15 // Limit the zoom level, so if we only have a few points we don't start massively zoomed in to fit the points
      });
    } catch (e) {
      // If we can't fit due to empty/invalid geom then fallback to view of England and Wales
      const ukView = new View({
        center: [-244598, 6939259],
        zoom: 6
      });
      map.setView(ukView);
    }
  }
}

