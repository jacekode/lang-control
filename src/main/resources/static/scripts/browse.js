"use strict";

import { callApi } from "./modules/client.js";
import LanguageCode from './modules/language-code.js';

class CardRow {

  #rootTr;

  constructor(cardData) {
    this.#rootTr = document.createElement("tr");

    const targetWordTd = document.createElement("td");
    this.#rootTr.appendChild(targetWordTd);
    targetWordTd.textContent = cardData.targetWord;

    const translatedWordTd = document.createElement("td");
    this.#rootTr.appendChild(translatedWordTd);
    translatedWordTd.textContent = cardData.translatedWord;

    const posTd = document.createElement("td");
    this.#rootTr.appendChild(posTd);
    posTd.textContent = cardData.partOfSpeech;

    const intervalTd = document.createElement("td");
    this.#rootTr.appendChild(intervalTd);
    intervalTd.textContent = cardData.currentIntervalMinutes + " min";

    const deckTd = document.createElement("td");
    this.#rootTr.appendChild(deckTd);
    deckTd.textContent = cardData.deck.name;

    const targetLangTd = document.createElement("td");
    this.#rootTr.appendChild(targetLangTd);
    targetLangTd.textContent = new LanguageCode(cardData.targetLang).getLangName();

    const sourceLangTd = document.createElement("td");
    this.#rootTr.appendChild(sourceLangTd);
    sourceLangTd.textContent = new LanguageCode(cardData.sourceLang).getLangName();

    const dynExamplesTd = document.createElement("td");
    this.#rootTr.appendChild(dynExamplesTd);
    dynExamplesTd.textContent = cardData.dynamicExamples;

    const learnModeTd = document.createElement("td");
    this.#rootTr.appendChild(learnModeTd);
    learnModeTd.textContent = cardData.inLearnMode ? "LEARN" : "REVIEW";

    const nextViewTd = document.createElement("td");
    this.#rootTr.appendChild(nextViewTd);
    nextViewTd.textContent = cardData.nextView;

    const createdAtTd = document.createElement("td");
    this.#rootTr.appendChild(createdAtTd);
    createdAtTd.textContent = cardData.createdAt;
    
    const editTd = document.createElement("td");
    this.#rootTr.appendChild(editTd);
    const editAnchor = document.createElement("a");
    editTd.appendChild(editAnchor);
    editAnchor.href = `/cards/${cardData.id}/edit`;
    editAnchor.textContent = "Edit";
  }

  appendTo(tbody) {
    tbody.appendChild(this.#rootTr);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const cardsTableTbody = document.querySelector("#cards-table tbody");
  let urlParams = new URLSearchParams();
  urlParams.append("sort", "created");
  urlParams.append("order", "desc");
  const url = `/cards?${urlParams}`;

  callApi(url)
    .then(body => {
      // console.debug(body.content[0]);
      for (const deck of body.content) {
        const row = new CardRow(deck);
        row.appendTo(cardsTableTbody);
      }
    });
});
