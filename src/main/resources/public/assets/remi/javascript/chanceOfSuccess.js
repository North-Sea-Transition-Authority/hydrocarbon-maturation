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
    let reservoirPercentage = module.querySelector("#prospectSpecificChanceOfSuccess-chanceOfSuccessDetailsForm-sourcePercentage-decimalInputValue").value.trim();
    let sourcePercentage = module.querySelector("#prospectSpecificChanceOfSuccess-chanceOfSuccessDetailsForm-reservoirPercentage-decimalInputValue").value.trim();
    let topSealPercentage = module.querySelector("#prospectSpecificChanceOfSuccess-chanceOfSuccessDetailsForm-topSealPercentage-decimalInputValue").value.trim();
    let trapPercentage = module.querySelector("#prospectSpecificChanceOfSuccess-trapPercentage-decimalInputValue").value.trim();
    let migrationPercentage = module.querySelector("#prospectSpecificChanceOfSuccess-migrationPercentage-decimalInputValue").value.trim();

    let total = 0;

    if (!isNaN(sourcePercentage)
      && sourcePercentage.length > 0
      && !isNaN(reservoirPercentage)
      && reservoirPercentage.length > 0
      && !isNaN(topSealPercentage)
      && topSealPercentage.length > 0
      && !isNaN(trapPercentage)
      && trapPercentage.length > 0
      && !isNaN(migrationPercentage)
      && migrationPercentage.length > 0) {
      total = (parseFloat(sourcePercentage) / 100)
        * (parseFloat(reservoirPercentage) / 100)
        * (parseFloat(topSealPercentage) / 100)
        * (parseFloat(trapPercentage) / 100)
        * (parseFloat(migrationPercentage) / 100);
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