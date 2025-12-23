import announce from "../../../../templates/fds/js/accessibility";

let percentListKeyElements;
let totalPlayChanceOfSuccess;
let totalProspectSpecificChance;
let overallChanceElem;

export function chanceOfSuccessFormHandler(module) {
  let form = module.querySelector('form');
  percentListKeyElements = module.getElementsByClassName("fds-data-items-list__key")
  totalPlayChanceOfSuccess = percentListKeyElements[0];
  totalProspectSpecificChance = percentListKeyElements[1];
  overallChanceElem = percentListKeyElements[2];
  calculateTotals(module)
  form.addEventListener('input', function () {
    calculateTotals(module)
  });
}

function calculateTotals(module) {
    let totalPlayChanceOfSuccess = calculateTotalPlayChanceOfSuccess(module);
    let totalProspectSpecificChance = calculateTotalProspectSpecificChance(module);
    let overallChanceOfSuccess = calculateOverallChanceOfSuccess(totalProspectSpecificChance, totalPlayChanceOfSuccess);
    announce(`
      ${roundToTwoDecimalPlaces((totalPlayChanceOfSuccess * 100))} percent total play chance of success. 
      ${roundToTwoDecimalPlaces((totalProspectSpecificChance * 100))} percent total prospect specific chance of success. 
      ${roundToTwoDecimalPlaces((overallChanceOfSuccess * 100))} percent overall chance of success.
    `)
  }

function calculateTotalPlayChanceOfSuccess(module) {
    let sourcePercentage = module.querySelector("#playChanceOfSuccess-sourcePercentage-decimalInputValue").value.trim();
    let reservoirPercentage = module.querySelector("#playChanceOfSuccess-reservoirPercentage-decimalInputValue").value.trim();
    let topSealPercentage = module.querySelector("#playChanceOfSuccess-topSealPercentage-decimalInputValue").value.trim();
    let total = 0;

    if (!isNaN(sourcePercentage)
      && sourcePercentage.length > 0
      && !isNaN(reservoirPercentage)
      && reservoirPercentage.length > 0
      && !isNaN(topSealPercentage)
      && topSealPercentage.length > 0) {
      total = (parseFloat(sourcePercentage) / 100) * (parseFloat(reservoirPercentage) / 100) * (parseFloat(topSealPercentage) / 100);
    }

    totalPlayChanceOfSuccess.innerHTML = roundToTwoDecimalPlaces((total * 100)) + "%";
    return total;
  }

function calculateTotalProspectSpecificChance(module) {
  let trapPresence = module.querySelector("#prospectSpecificChanceOfSuccess-trapPresence-decimalInputValue").value.trim();
  let trapEffectiveness = module.querySelector("#prospectSpecificChanceOfSuccess-trapEffectiveness-decimalInputValue").value.trim();
  let sourcePresence = module.querySelector("#prospectSpecificChanceOfSuccess-sourcePresence-decimalInputValue").value.trim();
  let sourceEffectiveness = module.querySelector("#prospectSpecificChanceOfSuccess-sourceEffectiveness-decimalInputValue").value.trim();
  let reservoirPresence = module.querySelector("#prospectSpecificChanceOfSuccess-reservoirPresence-decimalInputValue").value.trim();
  let reservoirEffectiveness = module.querySelector("#prospectSpecificChanceOfSuccess-reservoirEffectiveness-decimalInputValue").value.trim();

    let total = 0;

    if (!isNaN(trapPresence)
      && trapPresence.length > 0
      && !isNaN(trapEffectiveness)
      && trapEffectiveness.length > 0
      && !isNaN(sourcePresence)
      && sourcePresence.length > 0
      && !isNaN(sourceEffectiveness)
      && sourceEffectiveness.length > 0
      && !isNaN(reservoirPresence)
      && reservoirPresence.length > 0
      && !isNaN(reservoirEffectiveness)
      && reservoirEffectiveness.length > 0) {
      total = (parseFloat(trapPresence) / 100)
        * (parseFloat(trapEffectiveness) / 100)
        * (parseFloat(sourcePresence) / 100)
        * (parseFloat(sourceEffectiveness) / 100)
        * (parseFloat(reservoirPresence) / 100)
        * (parseFloat(reservoirEffectiveness) / 100);
    }

    totalProspectSpecificChance.innerHTML = roundToTwoDecimalPlaces((total * 100)) + "%";

    return total;
  }

function calculateOverallChanceOfSuccess(totalProspectSpecificChance, totalPlayChanceOfSuccess) {
    let total = totalPlayChanceOfSuccess * totalProspectSpecificChance;
    overallChanceElem.innerHTML = roundToTwoDecimalPlaces((total * 100)) + "%";
    return total;
}

function roundToTwoDecimalPlaces(value) {
    return value.toFixed(2)
  }