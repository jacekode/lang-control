"use strict";

import { callApi } from "./modules/client.js";
import { RC_SKEY, RR_SKEY, rsErrorParam } from "./modules/constants.js";
import LanguageCode from './modules/language-code.js';

const feedbackMsg = document.querySelector("#feedback-msg");

class Deck {

  #rootDiv;
  deckData;
  toReviewCount;
  totalCardsCount;

  /**
   * 
   * @param {number} id 
   * @param {string} name 
   * @param {number} toReviewCount
   * @param {number} totalCardsCount 
   */
  constructor(deckData, toReviewCount, totalCardsCount) {
    this.deckData = deckData;
    this.toReviewCount = toReviewCount;
    this.totalCardsCount = totalCardsCount;

    this.#rootDiv = document.createElement("div");
    this.#rootDiv.classList.add("deck");
    this.#rootDiv.dataset.deckId = this.deckData.id;
    
    const titlePara = document.createElement("p");
    this.#rootDiv.appendChild(titlePara);
    titlePara.classList.add("deck-title");
    titlePara.textContent = this.deckData.name;

    const menuDiv = document.createElement("div");
    this.#rootDiv.appendChild(menuDiv);
    menuDiv.classList.add("deck-menu");

    const editAnchor = document.createElement("a");
    menuDiv.appendChild(editAnchor);
    editAnchor.href = `/decks/${this.deckData.id}/edit`;
    editAnchor.classList.add("edit-deck");
    editAnchor.textContent = "Edit";

    const langInfoDiv = document.createElement("div");
    this.#rootDiv.appendChild(langInfoDiv);
    langInfoDiv.classList.add("deck-info");

    const targetLangInfoPara = document.createElement("p");
    langInfoDiv.appendChild(targetLangInfoPara);
    langInfoDiv.classList.add("info-item");

    const targetLangInfoText = document.createTextNode("Target: ");
    langInfoDiv.appendChild(targetLangInfoText);

    const targetLangSpan = document.createElement("span");
    langInfoDiv.appendChild(targetLangSpan);
    targetLangSpan.classList.add("target-lang");
    targetLangSpan.textContent = new LanguageCode(this.deckData.targetLang).getLangName();

    const sourceLangInfoPara = document.createElement("p");
    langInfoDiv.appendChild(sourceLangInfoPara);
    langInfoDiv.classList.add("info-item");

    const sourceLangInfoText = document.createTextNode("Source: ");
    langInfoDiv.appendChild(sourceLangInfoText);

    const sourceLangSpan = document.createElement("span");
    langInfoDiv.appendChild(sourceLangSpan);
    sourceLangSpan.classList.add("source-lang");
    sourceLangSpan.textContent = new LanguageCode(this.deckData.sourceLang).getLangName();

    const statsInfoDiv = document.createElement("div");
    this.#rootDiv.appendChild(statsInfoDiv);
    statsInfoDiv.classList.add("deck-info");

    const reviewInfoPara = document.createElement("p");
    statsInfoDiv.appendChild(reviewInfoPara);
    reviewInfoPara.classList.add("info-item");

    const reviewInfoText = document.createTextNode("To review: ");
    reviewInfoPara.appendChild(reviewInfoText);

    const reviewCountSpan = document.createElement("span");
    reviewInfoPara.appendChild(reviewCountSpan);
    reviewCountSpan.classList.add("review-count");
    reviewCountSpan.textContent = this.toReviewCount;

    const totalCardsInfoPara = document.createElement("p");
    statsInfoDiv.appendChild(totalCardsInfoPara);
    totalCardsInfoPara.classList.add("info-item");

    const totalCardsInfoText = document.createTextNode("Total cards: ");
    totalCardsInfoPara.appendChild(totalCardsInfoText);

    const totalCountSpan = document.createElement("span");
    totalCardsInfoPara.appendChild(totalCountSpan);
    totalCountSpan.classList.add("total-count");
    totalCountSpan.textContent = this.totalCardsCount;

    const reviewBtn = document.createElement("button");
    this.#rootDiv.appendChild(reviewBtn);
    reviewBtn.classList.add("review-btn", "btn-primary");
    reviewBtn.textContent = "Review";
    this.#addEventReview(reviewBtn);
  }

  appendTo(parentNode) {
    parentNode.appendChild(this.#rootDiv);
  }

  #addEventReview(reviewBtn) {
    reviewBtn.addEventListener("click", (e) => {
      const urlParams = new URLSearchParams();
      urlParams.append("deck", this.deckData.id);
      const url = `/sr?${urlParams}`;
      console.debug(url);
      callApi(url)
        .then((body) => {
          console.debug(`Loaded ${body.length} cards`);
          sessionStorage.setItem(RC_SKEY, JSON.stringify(body));
          sessionStorage.setItem(RR_SKEY, this.toReviewCount);
          window.location.replace("/review");
        })
        .catch((err) => {
          window.location.replace(`/?${rsErrorParam}`);
        });
    });
  }
}

document.addEventListener("DOMContentLoaded", () => {
    callApi(`/decks`)
      .then(async body => {
        // console.debug(`Response body: ${body}`);
        for (const deckData of body) {
          // console.debug(deckData);
          const details = await callApi(`/decks/${deckData.id}/details`);
          // console.debug(`Details: ${JSON.stringify(details)}`);
          const deck = new Deck(deckData, details.readyForReview, details.totalCards);
          deck.appendTo(document.querySelector("#deck-container"));
        }
        if (body.length == 0) {
          feedbackMsg.textContent = "You currently don't have any decks. ";
          const createDeckAnchor = document.createElement("a");
          feedbackMsg.appendChild(createDeckAnchor);
          createDeckAnchor.href = "/create/deck";
          createDeckAnchor.textContent = "Create one.";
        } else {
          feedbackMsg.textContent = "";
        }
      })
      .catch(error => {
        feedbackMsg.textContent = "An error has occurred while fetching decks.";
      });
});

