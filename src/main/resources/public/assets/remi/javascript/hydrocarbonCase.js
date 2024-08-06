import announce from "../../../../templates/fds/js/accessibility";

let summaryListValue;
let total = 0;

export function hydrocarbonCaseHandler(module) {
  let form = module.querySelector('form');
  summaryListValue = module.querySelector("dt.fds-data-items-list__key");
  calculateTotalHydrocarbonCase(module);
  setTotalHydrocarbonProbability(module);
  form.addEventListener('input', function () {
    calculateTotalHydrocarbonCase(module);
    setTotalHydrocarbonProbability(module);
  });
}

function calculateTotalHydrocarbonCase(module) {
  let gasOnly = module.querySelector("#gasOnlyProbability-decimalInputValue").value.trim()
  let oilOnly = module.querySelector("#oilOnlyProbability-decimalInputValue").value.trim()
  let oilAndGas = module.querySelector("#oilAndGasProbability-decimalInputValue").value.trim()
  let gasOnlyTicked = eval(module.querySelector("#gasOnlyCaseType").getAttribute('aria-expanded'))
  let oilOnlyTicked = eval(module.querySelector("#oilOnlyCaseType").getAttribute('aria-expanded'))
  let oilAndGasTicked = eval(module.querySelector("#oilAndGasCaseType").getAttribute('aria-expanded'))
  total = 0;

  if (gasOnlyTicked
    && !isNaN(gasOnly)
    && gasOnly.length > 0) {
    total += parseFloat(gasOnly);
  }

  if (oilOnlyTicked
    && !isNaN(oilOnly)
    && oilOnly.length > 0) {
    total += parseFloat(oilOnly);
  }

  if (!isNaN(oilAndGas)
    && oilAndGasTicked
    && oilAndGas.length > 0) {
    total += parseFloat(oilAndGas);
  }
  return total;
}

function setTotalHydrocarbonProbability() {
  summaryListValue.innerHTML = `${total}%`;
  announce(`${total} percent total hydrocarbon case`);
}
