import {hydrocarbonCaseHandler} from "./hydrocarbonCase.js";
import {chanceOfSuccessFormHandler} from "./chanceOfSuccess.js";

// Init Hydrocarbon Case
const hydroCarbonCase = document.getElementById("oilOnlyCaseType");

if(hydroCarbonCase != null) {
  hydrocarbonCaseHandler();
}

// Init chance of success
const chanceOfSuccess = document.getElementById("playChanceOfSuccess-sourcePercentage-decimalInputValue");

if(chanceOfSuccess != null) {
  chanceOfSuccessFormHandler();
}