export class ShowIfIe {
  constructor(module) {
    if (isIe()) {
      module.classList.remove("if-ie--hidden");
    }
  }
}

export class HideIfIe {
  constructor(module) {
    if (isIe()) {
      module.classList.add("if-ie--hidden");
    }
  }
}

function isIe() {
  return document.documentMode;
}