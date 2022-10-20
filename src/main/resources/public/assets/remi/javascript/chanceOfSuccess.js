let percentListKeyElements = document.getElementsByClassName("fds-data-items-list__key")
let totalPlayChanceOfSuccess = percentListKeyElements[0];
let totalProspectSpecificChance = percentListKeyElements[1]
let overallChanceElem = percentListKeyElements[2]

export function chanceOfSuccessFormHandler() {
  calculateTotals()

  let form = document.querySelector('form');
  form.addEventListener('input', function () {
    calculateTotals()
  });
}

function calculateTotals() {
    let totalPlayChanceOfSuccess = calculateTotalPlayChanceOfSuccess();
    let totalProspectSpecificChance = calculateTotalProspectSpecificChance();
    calculateOverallChanceOfSuccess(totalProspectSpecificChance, totalPlayChanceOfSuccess);
  }

function calculateTotalPlayChanceOfSuccess() {
    let sourcePercentage = document.getElementById("playChanceOfSuccess-sourcePercentage-decimalInputValue").value.trim();
    let reservoirPercentage = document.getElementById("playChanceOfSuccess-reservoirPercentage-decimalInputValue").value.trim();
    let topSealPercentage = document.getElementById("playChanceOfSuccess-topSealPercentage-decimalInputValue").value.trim();
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

function calculateTotalProspectSpecificChance() {
    let reservoirPercentage = document.getElementById("prospectSpecificChanceOfSuccess-chanceOfSuccessDetailsForm-sourcePercentage-decimalInputValue").value.trim();
    let sourcePercentage = document.getElementById("prospectSpecificChanceOfSuccess-chanceOfSuccessDetailsForm-reservoirPercentage-decimalInputValue").value.trim();
    let topSealPercentage = document.getElementById("prospectSpecificChanceOfSuccess-chanceOfSuccessDetailsForm-topSealPercentage-decimalInputValue").value.trim();
    let trapPercentage = document.getElementById("prospectSpecificChanceOfSuccess-trapPercentage-decimalInputValue").value.trim();
    let migrationPercentage = document.getElementById("prospectSpecificChanceOfSuccess-migrationPercentage-decimalInputValue").value.trim();

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
  }

function roundToTwoDecimalPlaces(value) {
    return value.toFixed(2)
  }