import {hydrocarbonCaseHandler} from "./hydrocarbonCase.js";
import {chanceOfSuccessFormHandler} from "./chanceOfSuccess.js";

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