"use strict";

import { callApi, callApiExpectNoBody } from "./modules/client.js";
import { formDataToJson } from "./modules/utils.js";
import { errorParam } from "./modules/constants.js";


document.addEventListener("DOMContentLoaded", () => {
  const cardId = cardIdFromUrl();
  const url = `/cards/${cardId}`;
  callApi(url)
    .then(cardData => {
      fillCurrentData(cardData);
    })
    .catch(err => console.error(err));
});

const editables = document.querySelectorAll(".editable");
editables.forEach((el) => {
  el.addEventListener("input", () => {
    checkValueSameness();
  });
});

const editCardForm = document.querySelector('#edit-card-form');
editCardForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const options = {
    method: "PUT",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  const cardId = cardIdFromUrl();
  const url = `/cards/${cardId}`;
  callApiExpectNoBody(url, options)
    .then(() => {
      document.querySelector("#feedback-msg").textContent = "Card updated successfully!";
      updateState();
    })
    .catch(err => console.error(err));
});

const deleteBtn = document.querySelector("#delete-card-btn");
deleteBtn.addEventListener("click", () => {
  const cardId = cardIdFromUrl();
  const url = `/cards/${cardId}`;
  const options = {
    method : "DELETE"
  }
  callApiExpectNoBody(url, options)
    .then((resCode) => {
      console.debug(resCode);
      window.location.replace("/browse");
    })
    .catch(err => console.error(err));
});

function cardIdFromUrl() {
  const urlRegex = /\/cards\/(\d+)\/edit/;
  const result = urlRegex.exec(window.location.pathname);
  let cardId;
  if (result) {
    cardId = result[1];
  } else {
    window.location.replace(`/?${errorParam}`);
    return;
  }
  return cardId;
}

function fillCurrentData(cardData) {
  document.getElementById("target-lang").textContent = cardData.targetLang;
  document.getElementById("source-lang").textContent = cardData.sourceLang;
  document.getElementById("target-word").value = cardData.targetWord;
  document.getElementById("target-word").dataset.origValue = cardData.targetWord;
  document.getElementById("translated-word").value = cardData.translatedWord;
  document.getElementById("translated-word").dataset.origValue = cardData.translatedWord;
  for (const opt of document.getElementById("pos-select").options) {
    if (opt.value === cardData.partOfSpeech) {
      opt.selected = true;
      document.getElementById("pos-select").dataset.origValue = cardData.partOfSpeech;
      break;
    }
  }
  document.getElementById("dynamic-examples").checked = cardData.dynamicExamples;
  document.getElementById("dynamic-examples").dataset.origValue = cardData.dynamicExamples;
  document.getElementById("target-example").value = cardData.targetExample;
  document.getElementById("target-example").dataset.origValue = cardData.targetExample;
  document.getElementById("translated-example").value = cardData.translatedExample;
  document.getElementById("translated-example").dataset.origValue = cardData.translatedExample;
}

function checkValueSameness() {
  const elems = document.querySelectorAll(".editable");
  const submitBtn = document.querySelector('#edit-card-form button[type="submit"]');
  for (const elem of elems) {
    if (elem.type === "checkbox") {
      console.debug(`elem.checked=${elem.checked}`);
      console.debug(`elem.dataset.origValue=${elem.dataset.origValue}`);
      console.debug(`typeof elem.checked=${typeof elem.checked}`);
      console.debug(`typeof elem.dataset.origValue=${typeof elem.dataset.origValue}`);
      if (elem.checked.toString() !== elem.dataset.origValue) {
        submitBtn.disabled = false;
        return;
      }
    } else if (elem.value !== elem.dataset.origValue) {
      submitBtn.disabled = false;
      return;
    }
  }
  submitBtn.disabled = true;
}

function updateState() {
  const targetWord = document.getElementById("target-word");
  targetWord.dataset.origValue = targetWord.value;
  const translatedWord = document.getElementById("translated-word");
  translatedWord.dataset.origValue = translatedWord.value;
  const posSelect = document.getElementById("pos-select");
  posSelect.dataset.origValue = posSelect.value;
  const dynamicExamples = document.getElementById("dynamic-examples");
  dynamicExamples.dataset.origValue = dynamicExamples.checked;
  const targetExample = document.getElementById("target-example");
  targetExample.dataset.origValue = targetExample.value;
  const translatedExample = document.getElementById("translated-example");
  translatedExample.dataset.origValue = translatedExample.value;
  document.querySelector('#edit-card-form button[type="submit"]').disabled = true;
}
