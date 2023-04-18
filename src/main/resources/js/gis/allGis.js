import ShapeMap from "./shapeMap";

document.querySelectorAll("[data-module='shape-map']")
  .forEach(module => new ShapeMap(module).init());