import GeoJSON from 'ol/format/GeoJSON';
import Map from 'ol/Map';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from "ol/layer/Vector";
import OSM from 'ol/source/OSM';
import VectorSource from "ol/source/Vector";
import View from 'ol/View';
import {Fill, Stroke, Style, Text} from 'ol/style';
import proj4 from 'proj4';
import {register} from 'ol/proj/proj4';
import {ZoomToExtent} from 'ol/control.js';

export default class ShapeMap {

  constructor(module) {
    this.module = module;
    this.geoJsonDataUrl = this.module.getAttribute("data-geojson-url");
    this.mapAttributions = this.module.getAttribute("data-map-attributions");
    this.shapeName = this.module.getAttribute("data-shape-name");
    this.showFieldDeterminationBoundariesLayer = this.module.getAttribute("data-show-field-determination-boundaries-layer") === 'true';
    this.showQuadBlocksLayer = this.module.getAttribute("data-show-quad-blocks-layer") === 'true';
    this.showLicenceBlocksLayer = this.module.getAttribute("data-show-licence-blocks-layer") === 'true';
    this.showFieldBlobsLayer = this.module.getAttribute("data-show-field-blobs-layer") === 'true';

    let fieldDeterminationBoundariesFillColour = 'rgba(0, 0, 0, 0)';
    let fieldDeterminationBoundariesStrokeColour = '#be303d';
    let quadBlockFillColour = 'rgba(0, 0, 0, 0)';
    let quadBlockStrokeColour = 'rgba(0, 46, 109, 0.8)';
    let licenseBlockFillColour = 'rgba(255, 253, 208, 0.8)';
    let licenseBlockStrokeColour = '#0073e6';
    let gasFieldBlobFillColour = '#cb5964';
    let oilFieldBlobFillColour = '#35a978';
    let condensateFieldBlobFillColour = '#e1955f';
    let fieldBlobStrokeColour = 'rgba(0, 0, 0, 0)';
    let shapeFillColour = '#800080';
    let shapeStrokeColour = '#319FD3';

    this.fieldDeterminationBoundariesStyle = ShapeMap._getStyle(fieldDeterminationBoundariesFillColour, fieldDeterminationBoundariesStrokeColour, 0.5);
    this.quadBlockStyle = ShapeMap._getStyle(quadBlockFillColour, quadBlockStrokeColour, 0.5);
    this.licenseBlockStyle = ShapeMap._getStyle(licenseBlockFillColour, licenseBlockStrokeColour, 0.5);
    this.fieldBlobStyles = [
      {name: "OIL", fillColour: oilFieldBlobFillColour, strokeColour: fieldBlobStrokeColour, strokeWidth: 0},
      {name: "GAS", fillColour: gasFieldBlobFillColour, strokeColour: fieldBlobStrokeColour, strokeWidth: 0},
      {name: "COND", fillColour: condensateFieldBlobFillColour, strokeColour: fieldBlobStrokeColour, strokeWidth: 0}
    ];
    this.shapeStyle = ShapeMap._getStyle(shapeFillColour, shapeStrokeColour, 1);
    this.shapeStyle.getText().setText(this.shapeName);
    this.shapeStyle.getText().setOverflow(true);

    let legendBox = this.module.getElementsByClassName("shape-map__legend-box");
    Array.from(legendBox).forEach(box => {
      switch (box.id) {
        case "quad-blocks-legend":
          box.style.backgroundColor = quadBlockFillColour;
          box.style.borderColor = quadBlockStrokeColour;
          break;
        case "license-blocks-legend":
          box.style.backgroundColor = licenseBlockFillColour;
          box.style.borderColor = licenseBlockStrokeColour;
          break;
        case "gas-fields-legend":
          box.style.backgroundColor = gasFieldBlobFillColour;
          box.style.borderColor = fieldBlobStrokeColour;
          break;
        case "oil-fields-legend":
          box.style.backgroundColor = oilFieldBlobFillColour;
          box.style.borderColor = fieldBlobStrokeColour;
          break;
        case "cond-fields-legend":
          box.style.backgroundColor = condensateFieldBlobFillColour;
          box.style.borderColor = fieldBlobStrokeColour;
          break;
        case "field-determination-boundaries-legend":
          box.style.backgroundColor = fieldDeterminationBoundariesFillColour;
          box.style.borderColor = fieldDeterminationBoundariesStrokeColour;
          break;
        case "shape-legend":
          box.style.backgroundColor = shapeFillColour;
          box.style.borderColor = shapeStrokeColour;
          break;
      }
    })

    const buildServiceUrl = (resourcePath, outFields = '') => {
      const serviceBase = 'https://services-eu1.arcgis.com/OZMfUznmLTnWccBc/arcgis/rest/services/';
      const querySuffix = '/FeatureServer/0/query?where=1%3D1&f=geojson';
      const encodedOutFields = outFields ? `&outFields=${encodeURIComponent(outFields)}` : '';

      return `${serviceBase}${resourcePath}${querySuffix}${encodedOutFields}`;
    };

    this.fieldDeterminationBoundariesOffshoreUrl = buildServiceUrl('Petroleum_field_determinations_(WGS84)', 'FIELDNAME');
    this.fieldDeterminationBoundariesOnshoreUrl = buildServiceUrl('Onshore_petroleum_field_determinations_(WGS84)', 'FIELDNAME');
    this.quadBlockUrl = buildServiceUrl('UKCS_blocks_(WGS84)', 'BLOCK_REF');
    this.licenseOffshoreBlockUrl = buildServiceUrl('UKCS offshore petroleum licence blocks WGS84', 'BLOCKREF');
    this.licenseOnshoreBlockUrl = buildServiceUrl('England onshore petroleum licence blocks WGS84', 'BLOCKREF');
    this.fieldBlobOffshoreUrl = buildServiceUrl('Offshore_hydrocarbon_fields_(WGS84)', 'FIELDNAME,FIELDTYPE');
    this.fieldBlobOnshoreUrl = buildServiceUrl('Onshore_hydrocarbon_fields_(WGS84)', 'FIELDNAME,FIELDTYPE');
  }

  async init() {

    this.registerProjections();

    const layers = this.getBaseLayers();

    const shapeLayer = this.getShapeLayer(this.shapeStyle, this.fieldBlobStyles);
    layers.push(shapeLayer);

    const map = this.createMap(layers);
    const shapeName = this.shapeName;

    shapeLayer.getSource().once('change', function(e){
      if (e.target.getState() === 'ready') {
        ShapeMap._fitMapToExtentOfLayer(map, shapeLayer);
        map.getControls().extend([
          new ZoomToExtent({
            extent: shapeLayer.getSource().getExtent(),
            label: '\u26ef',
            tipLabel: 'Refocus to ' + shapeName,
          }),
        ])
      }
    });

  }

  registerProjections() {
    proj4.defs('EPSG:27700', '+proj=tmerc +lat_0=49 +lon_0=-2 +k=0.9996012717 +x_0=400000 +y_0=-100000 +ellps=airy +datum=OSGB36 +units=m +no_defs');
    proj4.defs("EPSG:4230","+proj=longlat +ellps=intl +towgs84=-87,-98,-121,0,0,0,0 +no_defs +type=crs");
    proj4.defs("EPSG:4326","+proj=longlat +datum=WGS84 +no_defs +type=crs");
    proj4.defs("EPSG:4258","+proj=longlat +ellps=GRS80 +no_defs +type=crs");

    register(proj4);
  }

  getFieldDeterminationBoundariesOffshoreLayer() {
    return ShapeMap._getVectorLayer(
      'Field Determination Boundaries',
      ShapeMap._getVectorSource(this.fieldDeterminationBoundariesOffshoreUrl, this.mapAttributions),
      this.fieldDeterminationBoundariesStyle,
      'FIELDNAME',
      !this.showFieldBlobsLayer
    );
  }

  getFieldDeterminationBoundariesOnshoreLayer() {
    return ShapeMap._getVectorLayer(
      'Field Determination Boundaries',
      ShapeMap._getVectorSource(this.fieldDeterminationBoundariesOnshoreUrl, this.mapAttributions),
      this.fieldDeterminationBoundariesStyle,
      'FIELDNAME',
      !this.showFieldBlobsLayer
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

  getLicenceOffshoreBlocksLayer() {
    return ShapeMap._getVectorLayer(
      'License Blocks',
      ShapeMap._getVectorSource(this.licenseOffshoreBlockUrl, this.mapAttributions),
      this.licenseBlockStyle,
      'BLOCKREF'
    );
  }

  getLicenceOnshoreBlocksLayer() {
    return ShapeMap._getVectorLayer(
      'License Blocks',
      ShapeMap._getVectorSource(this.licenseOnshoreBlockUrl, this.mapAttributions),
      this.licenseBlockStyle,
      'BLOCKREF'
    );
  }

  getFieldBlobsOffshoreLayer() {
    return ShapeMap._getDynamicVectorLayer(
      'License Blocks',
      ShapeMap._getVectorSource(this.fieldBlobOffshoreUrl, this.mapAttributions),
      'FIELDNAME',
      'FIELDTYPE',
      this.fieldBlobStyles,
    );
  }

  getFieldBlobsOnshoreLayer() {
    return ShapeMap._getDynamicVectorLayer(
      'License Blocks',
      ShapeMap._getVectorSource(this.fieldBlobOnshoreUrl, this.mapAttributions),
      'FIELDNAME',
      'FIELDTYPE',
      this.fieldBlobStyles,
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
      layers.push(this.getLicenceOffshoreBlocksLayer());
      layers.push(this.getLicenceOnshoreBlocksLayer());
    }

    if (this.showFieldDeterminationBoundariesLayer) {
      layers.push(this.getFieldDeterminationBoundariesOffshoreLayer());
      layers.push(this.getFieldDeterminationBoundariesOnshoreLayer());
    }

    if (this.showFieldBlobsLayer) {
      layers.push(this.getFieldBlobsOffshoreLayer());
      layers.push(this.getFieldBlobsOnshoreLayer());
    }

    return layers;
  }

  getBackgroundLayer() {
    return new TileLayer({
      source: new OSM()
    });
  }

  getShapeLayer(fixedStyle, fieldStyles) {
    return new VectorLayer({
      source: new VectorSource({
        url: this.geoJsonDataUrl,
        format: new GeoJSON(),
      }),
      style: function (feature) {
        let dynamicStyle = fieldStyles.find(({name}) => name === feature.get("FIELDTYPE"));
        if (dynamicStyle === undefined) {
          return fixedStyle;
        }

        let style = ShapeMap._getStyle(dynamicStyle.fillColour, dynamicStyle.strokeColour, dynamicStyle.strokeWidth);
        style.getText().setText(String(feature.get("FIELDNAME")));
        return style;
      }
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

  static _getVectorLayer(title, vectorSource, style, textFeatureName, showText = true) {
    return new VectorLayer({
      source: vectorSource,
      style: function (feature) {
        if (showText) {
          style.getText().setText(String(feature.get(textFeatureName)));
        }
        return style;
      }
    });
  }

  static _getDynamicVectorLayer(title, vectorSource, textFeatureName, dynamicFeatureName, dynamicStyles, showText = true) {
    return new VectorLayer({
      source: vectorSource,
      style: function (feature) {
        let dynamicStyle = dynamicStyles.find(({name}) => name === feature.get(dynamicFeatureName));
        if (dynamicStyle === undefined) {
          return undefined;
        }

        let style = ShapeMap._getStyle(dynamicStyle.fillColour, dynamicStyle.strokeColour, dynamicStyle.strokeWidth);

        if (showText) {
          style.getText().setText(String(feature.get(textFeatureName)));
        }

        return style;
      }
    });
  }

  static _getVectorSource(layerUrl, attributions) {
    return new VectorSource({
      format: new GeoJSON(),
      attributions: attributions,
      loader: async function(extent, resolution, projection) {
        const urlObj = new URL(layerUrl);
        const format = this.getFormat();

        let offset = 0;
        let hasMore = true;

        try {
          while (hasMore) {
            urlObj.searchParams.set('resultOffset', offset);

            const response = await fetch(urlObj.toString());
            if (!response.ok) {
              throw new Error(`Failed to fetch ${urlObj.toString()}: ${response.status} ${response.statusText}`);
            }

            const data = await response.json();

            const features = format.readFeatures(data, {
              featureProjection: projection
            });

            this.addFeatures(features);

            offset += features.length;
            hasMore = data?.properties?.exceededTransferLimit === true;
          }
        } catch (error) {
          console.error('Error loading vector source:', error);
          this.removeLoadedExtent(extent);
        }
      }
    });
  }

  static _getStyle(fillColour = 'rgba(0, 0, 0, 0)', strokeColour = '#319FD3', strokeWidth = 1) {
    return new Style({
      fill: new Fill({
        color: fillColour
      }),
      stroke: new Stroke({
        color: strokeColour,
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

