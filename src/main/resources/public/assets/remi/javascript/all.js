import {hydrocarbonCaseHandler} from "./hydrocarbonCase.js";
import {chanceOfSuccessFormHandler} from "./chanceOfSuccess.js";
import {ShowIfIe, HideIfIe} from "./ifIe.js"

// Init Hydrocarbon Case
const hydrocarbonCase = document.querySelector("[data-module='hydrocarbon-case']");

if(hydrocarbonCase != null) {
  hydrocarbonCaseHandler(hydrocarbonCase);
}

// Init chance of success
const chanceOfSuccess = document.querySelector("[data-module='chance-of-success']");

if(chanceOfSuccess != null) {
  chanceOfSuccessFormHandler(chanceOfSuccess);
}

document.querySelectorAll("[data-module='show-if-ie']")
  .forEach(module => new ShowIfIe(module));

document.querySelectorAll("[data-module='hide-if-ie']")
  .forEach(module => new HideIfIe(module));