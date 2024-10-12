"use strict";

import { callApi, callApiExpectNoBody } from "./modules/client.js";
import { errorParam } from "./modules/constants.js";
import { formDataToJson } from "./modules/utils.js";

document.addEventListener("DOMContentLoaded", () => {
  const deckId = deckIdFromUrl();
  const url = `/decks/${deckId}`;
  callApi(url)
    .then(deckData => {
      document.getElementById("deck-name").value = deckData.name;
      document.getElementById("deck-name").dataset.origValue = deckData.name;
      document.getElementById("edit-deck-form").dataset.deckId = deckData.id;
    })
    .catch(err => console.error(err));
});

const deckNameInput = document.getElementById("deck-name");
deckNameInput.addEventListener("input", () => {
  if (deckNameInput.value === deckNameInput.dataset.origValue) {
    document.querySelector('#edit-deck-form button[type="submit"]').disabled = true;
  } else {
    document.querySelector('#edit-deck-form button[type="submit"]').disabled = false;
  }
});

const editDeckForm = document.getElementById("edit-deck-form");
editDeckForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const options = {
    method: "PUT",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  const deckId = deckIdFromUrl();
  const url = `/decks/${deckId}`;
  callApiExpectNoBody(url, options)
    .then(() => {
      document.querySelector(".feedback-msg").textContent = "Deck updated successfully!";
      updateState();
    })
    .catch(err => console.error(err));
});

const deleteBtn = document.querySelector("#delete-deck-btn");
deleteBtn.addEventListener("click", () => {
  const deckId = deckIdFromUrl();
  const url = `/decks/${deckId}`;
  const options = {
    method : "DELETE"
  }
  callApiExpectNoBody(url, options)
    .then((resCode) => {
      console.debug(resCode);
      window.location.replace("/");
    })
    .catch(err => console.error(err));
});

function deckIdFromUrl() {
  const urlRegex = /\/decks\/(\d+)\/edit/;
  const result = urlRegex.exec(window.location.pathname);
  let deckId;
  if (result) {
    deckId = result[1];
  } else {
    window.location.replace(`/?${errorParam}`);
    return;
  }
  return deckId;
}

function updateState() {
  const deckNameInput = document.getElementById("deck-name");
  deckNameInput.dataset.origValue = deckNameInput.value;
  document.querySelector('#edit-deck-form button[type="submit"]').disabled = true;
}
