"use strict";

import { formDataToJson } from "./modules/utils.js";
import { callApi } from "./modules/client.js";

document.addEventListener("DOMContentLoaded", () => {
  const targetLangSelect = document.querySelector("#target-lang");
  const sourceLangSelect = document.querySelector("#source-lang");
  
  callApi(`/lang`)
    .then(body => {
      // console.debug(body);
      for (const langCode of body) {
        // console.debug(`Lang code: ${JSON.stringify(langCode)}`);
        const targetLangOption = document.createElement("option");
        targetLangSelect.appendChild(targetLangOption);
        targetLangOption.value = langCode.code;
        targetLangOption.textContent = langCode.language;

        const sourceLangOption = document.createElement("option");
        sourceLangSelect.appendChild(sourceLangOption);
        sourceLangOption.value = langCode.code;
        sourceLangOption.textContent = langCode.language;
      }
    })
    .catch(err => {
      throw new Error(err.message);
    });
});

document.querySelector("#create-deck-form").addEventListener("submit", (e) => {
  e.preventDefault();
  if (document.querySelector("#target-lang").value === document.querySelector("#source-lang").value) {
    document.querySelector("#feedback-msg").textContent = "Languages cannot be the same!";
    console.debug("Languages the same. Aborted.");
    return;
  }
  const reqBody = formDataToJson(e.currentTarget);
  callApi(`/decks`, {
    method: "POST",
    body: reqBody,
    headers: {
      "Content-Type": "application/json"
    },
  })
    .then((resBody) => {
      console.debug(resBody);
      document.querySelector("#feedback-msg").textContent = `Deck "${resBody.name}" created successfully!`;
      document.querySelector("#create-deck-form").reset();
    })
});
