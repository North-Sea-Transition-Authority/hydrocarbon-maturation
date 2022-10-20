let summaryListValue = document.querySelector("dt.fds-data-items-list__key");
let total = 0;

export function hydrocarbonCaseHandler() {
  let form = document.querySelector('form');

  calculateTotalHydrocarbonCase();
  setTotalHydrocarbonProbability();
  form.addEventListener('input', function () {
    calculateTotalHydrocarbonCase();
    setTotalHydrocarbonProbability();
  });
}

function calculateTotalHydrocarbonCase() {
  let gasOnly = document.getElementById("gasOnlyProbability-decimalInputValue").value.trim()
  let oilOnly = document.getElementById("oilOnlyProbability-decimalInputValue").value.trim()
  let oilAndGas = document.getElementById("oilAndGasProbability-decimalInputValue").value.trim()
  let gasOnlyTicked = eval(document.getElementById("gasOnlyCaseType").getAttribute('aria-expanded'))
  let oilOnlyTicked = eval(document.getElementById("oilOnlyCaseType").getAttribute('aria-expanded'))
  let oilAndGasTicked = eval(document.getElementById("oilAndGasCaseType").getAttribute('aria-expanded'))
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
}
