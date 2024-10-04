"use strict";

import { callApi, callApiExpectNoBody } from "./modules/client.js";
import { formDataToJson } from "./modules/utils.js";

document.addEventListener("DOMContentLoaded", () => {
  const url = "/settings";
  callApi(url)
    .then(body => {
      const defDynExCheckbox = document.getElementById("default-dynamic-examples");
      defDynExCheckbox.checked = body.dynamicSentencesOnByDefault;
      defDynExCheckbox.dataset.origValue = body.dynamicSentencesOnByDefault;
      const zenModeCheckbox = document.getElementById("enable-zen-mode");
      zenModeCheckbox.checked = body.zenModeEnabled;
      zenModeCheckbox.dataset.origValue = body.zenModeEnabled;
    })
    .catch(err => console.error(err));
});

const appSettingsForm = document.getElementById("app-settings-form");
appSettingsForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const options = {
    method: "PUT",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  const url = "/settings";
  callApiExpectNoBody(url, options)
    .then(() => {
      document.querySelector(".feedback-msg").textContent = "Settings updated successfully!";
      updateState();
    })
    .catch(err => console.error(err));
});

const checkboxes = document.querySelectorAll('input[type="checkbox"]');
checkboxes.forEach((el) => {
  el.addEventListener("change", () => {
    checkValueSameness();
  });
});

function updateState() {
  const defDynExCheckbox = document.getElementById("default-dynamic-examples");
  defDynExCheckbox.dataset.origValue = defDynExCheckbox.checked;
  document.querySelector('#app-settings-form button[type="submit"]').disabled = true;
}

function checkValueSameness() {
  console.log("INSIDE checkValueSameness()!!!!!");
  const checkboxes = document.querySelectorAll('input[type="checkbox"]');
  const submitBtn = document.querySelector('#app-settings-form button[type="submit"]');
  for (const elem of checkboxes) {
    console.log(`elem.checked.toString() = ${elem.checked.toString()}`);
    console.log(`elem.dataset.origValue = ${elem.dataset.origValue}`);
    console.log(`elem.checked.toString() !== elem.dataset.origValue = ${elem.checked.toString() !== elem.dataset.origValue}`);
    if (elem.checked.toString() !== elem.dataset.origValue) {
      submitBtn.disabled = false;
      return;
    }
  }
  submitBtn.disabled = true;
}
