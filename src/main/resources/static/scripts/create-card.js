"use strict";

import { formDataToJson, getSelectedOption } from "./modules/utils.js";
import { callApi, generateSentences, translateText, lookupDictionary, getUserSettings } from "./modules/client.js";
import LanguageCode from './modules/language-code.js';

const dynExCheckbox = document.querySelector("#dynamic-examples");

document.addEventListener("DOMContentLoaded", () => {
  const deckSelect = document.querySelector("#deck-select");
  callApi(`/decks`)
    .then((body) => {
      // console.debug(body);
      let selected = false;
      for (const deckData of body) {
        const deckOption = document.createElement("option");
        deckSelect.appendChild(deckOption);
        deckOption.value = deckData.id;
        deckOption.textContent = deckData.name;
        deckOption.dataset.targetLang = deckData.targetLang;
        deckOption.dataset.sourceLang = deckData.sourceLang;
        if (!selected) {
          deckOption.selected = true;
          document.getElementById("target-lang").textContent = new LanguageCode(deckData.targetLang).getLangName();
          document.getElementById("source-lang").textContent = new LanguageCode(deckData.sourceLang).getLangName();
          selected = true;
        }
      }
    })
    .catch((err) => {
      throw new Error("Failed to fetch decks");
    });
  getUserSettings()
    .then((body) => {
      dynExCheckbox.checked = body.dynamicSentencesOnByDefault;
      dynExCheckbox.dispatchEvent(new Event("input"));
    })
    .catch((err) => console.error(err));
});

const deckSelect = document.getElementById("deck-select");
deckSelect.addEventListener("change", () => {
  const selectedOption = getSelectedOption("deck-select");
  document.getElementById("target-lang").textContent = new LanguageCode(selectedOption.dataset.targetLang).getLangName();
  document.getElementById("source-lang").textContent = new LanguageCode(selectedOption.dataset.sourceLang).getLangName();
});

const createCardForm = document.querySelector("#create-card-form");
createCardForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const reqBody = formDataToJson(e.currentTarget);
  callApi(`/cards`, {
    method: "POST",
    body: reqBody,
    headers: {
      "Content-Type": "application/json"
    },
  })
    .then((resBody) => {
      // console.debug(resBody);
      document.querySelector("#feedback-msg").textContent = "Flashcard added successfully! Next review in 10 min.";
      document.querySelector("#dictionary-container").innerHTML = "";
      resetForm();
    })
    .catch(err => {
      document.querySelector("#feedback-msg").textContent = "An error has occurred!";
      throw new Error(err.message);
    });
});

dynExCheckbox.addEventListener("input", (e) => {
  if (e.currentTarget.checked) {
    document.querySelectorAll(".example-input, #generate-btn").forEach((el) => {
      el.disabled = true;
    });
  } else {
    document.querySelectorAll(".example-input, #generate-btn").forEach((el) => {
      el.disabled = false;
    });
  }
});

const generateBtn = document.getElementById("generate-btn");
generateBtn.addEventListener("click", () => {
  const keyword = document.getElementById("target-word").value;
  const lang = getSelectedOption("deck-select").dataset.targetLang;
  const pos = getSelectedOption("pos-select").value;
  const num = 1;
  generateSentences(keyword, lang, pos, num)
    .then(body => {
      document.getElementById("target-example").value = body[0];
      translateExample(body[0]);
    })
    .catch(err => console.error(err));
});

function resetForm() {
  document.querySelector("#create-card-form").reset();
  document.querySelectorAll("#create-card-form textarea").forEach((el) => el.value = "");
}

function translateExample(sentence) {
  const langTo = getSelectedOption("deck-select").dataset.sourceLang;
  const langFrom = getSelectedOption("deck-select").dataset.targetLang;
  translateText(sentence, langTo, langFrom)
    .then(body => {
      document.getElementById("translated-example").value = body.translation;
    })
    .catch(err => console.error(err));
}

const translateBtn = document.getElementById("translate-btn");
translateBtn.addEventListener("click", () => {
  document.querySelector("#dictionary-container").innerHTML = "";
  const targetWord = document.getElementById("target-word").value;
  const langFrom = getSelectedOption("deck-select").dataset.targetLang;
  const langTo = getSelectedOption("deck-select").dataset.sourceLang;
  const pos = getSelectedOption("pos-select").value;
  lookupDictionary(targetWord, langFrom, langTo, pos)
    .then(body => {
      for (const word of body) {
        console.debug(`Word: ${word}`);
        appendDictionaryBtn("dictionary-container", word);
      }
    })
    .catch(err => console.error(err));
});

function appendDictionaryBtn(containerId, textContent) {
  const dictionaryContainer = document.getElementById(containerId);
  const btn = document.createElement("button");
  dictionaryContainer.appendChild(btn);
  btn.type = "button";
  btn.textContent = textContent;
  btn.classList.add("dictionary-btn");
  btn.addEventListener("click", (e) => {
    toggleDictionaryBtn(e.currentTarget);
  });
}

/**
 * @param {HTMLButtonElement} dictionaryBtn 
 */
function toggleDictionaryBtn(dictionaryBtn) {
  const sep = "; ";
  const input = document.getElementById("translated-word");
  if (dictionaryBtn.classList.contains("active")) {
    let newValue = input.value.replace(dictionaryBtn.textContent, "");
    newValue = newValue.replace(`${sep}${sep}`, sep);
    if (newValue.startsWith(sep)) {
      newValue = newValue.substring(sep.length);
    }
    if (newValue.endsWith(sep)) {
      newValue = newValue.substring(0, newValue.length - sep.length);
    }
    input.value = newValue;
    dictionaryBtn.classList.remove("active");
  } else {
    if (input.value.length == 0) {
      input.value = dictionaryBtn.textContent;
    } else {
      input.value = input.value + sep + dictionaryBtn.textContent;
    }
    dictionaryBtn.classList.add("active");
  }
}
