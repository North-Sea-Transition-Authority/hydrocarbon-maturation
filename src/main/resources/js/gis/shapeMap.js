import GeoJSON from 'ol/format/GeoJSON';
import Map from 'ol/Map';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from "ol/layer/Vector";
import OSM from 'ol/source/OSM';
import VectorSource from "ol/source/Vector";
import View from 'ol/View';
import {Fill, Stroke, Style, Text} from 'ol/style';

export default class ShapeMap {

  constructor(module) {
    this.module = module;
    this.geoJsonDataUrl = this.module.getAttribute("data-geojson-url");
    this.mapAttributions = this.module.getAttribute("data-map-attributions");
    this.showFieldDeterminationBoundariesLayer = this.module.getAttribute("data-show-field-determination-boundaries-layer") === 'true';
    this.showQuadBlocksLayer = this.module.getAttribute("data-show-quad-blocks-layer") === 'true';
    this.showLicenceBlocksLayer = this.module.getAttribute("data-show-licence-blocks-layer") === 'true';
    this.showFieldBlobsLayer = this.module.getAttribute("data-show-field-blobs-layer") === 'true';

    let fieldDeterminationBoundariesFillColor = 'rgba(0, 0, 0, 0)';
    let fieldDeterminationBoundariesStrokeColor = '#be303d';
    let quadBlockFillColor = 'rgba(0, 0, 0, 0)';
    let quadBlockStrokeColor = 'rgba(0, 46, 109, 0.8)';
    let licenseBlockFillColor = 'rgba(255, 253, 208, 0.8)';
    let licenseBlockStrokeColor = '#0073e6';
    let fieldBlobFillColor = '#cb5964';
    let fieldBlobStrokeColor = 'rgba(0, 0, 0, 0)';

    this.fieldDeterminationBoundariesStyle = ShapeMap._getStyle(fieldDeterminationBoundariesFillColor, fieldDeterminationBoundariesStrokeColor, 0.5);
    this.quadBlockStyle = ShapeMap._getStyle(quadBlockFillColor, quadBlockStrokeColor, 0.5);
    this.licenseBlockStyle = ShapeMap._getStyle(licenseBlockFillColor, licenseBlockStrokeColor, 0.5);
    this.fieldBlobStyle = ShapeMap._getStyle(fieldBlobFillColor, fieldBlobStrokeColor, 0);

    this.module.getElementsByClassName("shape-map__legend-box").forEach(box => {
      switch (box.id) {
        case "quad-blocks-legend":
          box.style.backgroundColor = quadBlockFillColor;
          box.style.borderColor = quadBlockStrokeColor;
          break;
        case "license-blocks-legend":
          box.style.backgroundColor = licenseBlockFillColor;
          box.style.borderColor = licenseBlockStrokeColor;
          break;
        case "fields-legend":
          box.style.backgroundColor = fieldBlobFillColor;
          box.style.borderColor = fieldBlobStrokeColor;
          break;
        case "field-determination-boundaries-legend":
          box.style.backgroundColor = fieldDeterminationBoundariesFillColor;
          box.style.borderColor = fieldDeterminationBoundariesStrokeColor;
          break;
      }
    })

    this.fieldDeterminationBoundariesUrl = 'https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/UKCS_Offshore_FieldDets_WGS84/FeatureServer/0/query?where=1%3D1&outFields=*&returnGeometry=true&f=geojson';
    this.quadBlockUrl = 'https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/UKCS_ReferenceBlocks_WGS84/FeatureServer/0/query?where=1%3D1&outFields=*&returnGeometry=true&f=geojson';
    this.licenseBlockUrl = 'https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/UKCS_Licences_WGS84/FeatureServer/0/query?where=1%3D1&outFields=*&returnGeometry=true&f=geojson';
    this.fieldBlobUrl = 'https://data.nstauthority.co.uk/arcgis/rest/services/Public_WGS84/UKCS_Offshore_Fields_WGS84/FeatureServer/0/query?where=1%3D1&outFields=*&returnGeometry=true&f=geojson';
  }

  async init() {

    const layers = this.getBaseLayers();

    const shapeLayer = this.getShapeLayer(ShapeMap._getStyle('#eeeeee'));
    layers.push(shapeLayer);

    const map = this.createMap(layers);

    shapeLayer.getSource().once('change', function(e){
      if (e.target.getState() === 'ready') {
        ShapeMap._fitMapToExtentOfLayer(map, shapeLayer);
      }
    });

  }

  getFieldDeterminationBoundariesLayer() {
    return ShapeMap._getVectorLayer(
      'Field Determination Boundaries',
      ShapeMap._getVectorSource(this.fieldDeterminationBoundariesUrl, this.mapAttributions),
      this.fieldDeterminationBoundariesStyle,
      'FIELDNAME'
    );
  }

  getQuadBlocksLayer() {
    return ShapeMap._getVectorLayer(
      'Quadrant and Blocks',
      ShapeMap._getVectorSource(this.quadBlockUrl, this.mapAttributions),
      this.quadBlockStyle,
      'BLOCK_REF'
    );
  }

  getLicenceBlocksLayer() {
    return ShapeMap._getVectorLayer(
      'License Blocks',
      ShapeMap._getVectorSource(this.licenseBlockUrl, this.mapAttributions),
      this.licenseBlockStyle ,
      'BLOCKREF'
    );
  }

  getFieldBlobsLayer() {
    return ShapeMap._getVectorLayer(
      'License Blocks',
      ShapeMap._getVectorSource(this.fieldBlobUrl, this.mapAttributions),
      this.fieldBlobStyle ,
      'FIELDNAME'
    );
  }

  getBaseLayers() {
    const layers = [];

    const backgroundLayer = this.getBackgroundLayer();
    layers.push(backgroundLayer);

    if (this.showQuadBlocksLayer) {
      layers.push(this.getQuadBlocksLayer());
    }

    if (this.showLicenceBlocksLayer) {
      layers.push(this.getLicenceBlocksLayer());
    }

    if (this.showFieldDeterminationBoundariesLayer) {
      layers.push(this.getFieldDeterminationBoundariesLayer());
    }

    if (this.showFieldBlobsLayer) {
      layers.push(this.getFieldBlobsLayer());
    }

    return layers;
  }

  getBackgroundLayer() {
    return new TileLayer({
      source: new OSM()
    });
  }

  getShapeLayer(style) {
    return new VectorLayer({
      source: new VectorSource({
        url: this.geoJsonDataUrl,
        format: new GeoJSON(),
      }),
      style: style,
    });
  }

  createMap(layers) {
    return new Map({
      layers: layers,
      target: this.module,
      view: new View({
        center: [0, 0],
        zoom: 1,
      }),
    });
  }

  static _fitMapToExtentOfLayer(map, layer) {
    try {
      map.getView().fit(layer.getSource().getExtent(), {
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

  static _getVectorLayer(title, vectorSource, style, featureName) {
    return new VectorLayer({
      source: vectorSource,
      style: function (feature) {
        style.getText().setText(String(feature.get(featureName)));
        return style;
      }
    });
  }

  static _getVectorSource(layerUrl, attributions) {
    return new VectorSource({
      url: layerUrl,
      format: new GeoJSON(),
      attributions: attributions
    });
  }

  static _getStyle(fillColor = 'rgba(0, 0, 0, 0)', strokeColor = '#319FD3', strokeWidth = 1) {
    return new Style({
      fill: new Fill({
        color: fillColor
      }),
      stroke: new Stroke({
        color: strokeColor,
        width: strokeWidth
      }),
      text: new Text({
        font: '12px Calibri,sans-serif',
        fill: new Fill({
          color: '#000'
        }),
        stroke: new Stroke({
          color: '#fff',
          width: 3
        })
      })
    });
  }
}

