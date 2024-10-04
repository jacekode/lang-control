"use strict";

import { callApi } from "./modules/client.js";
import { RC_SKEY, RR_SKEY, rsErrorParam } from "./modules/constants.js";

class Deck {

  #rootDiv;
  id;
  name;
  toReviewCount;
  totalCardsCount;

  /**
   * 
   * @param {number} id 
   * @param {string} name 
   * @param {number} toReviewCount
   * @param {number} totalCardsCount 
   */
  constructor(id, name, toReviewCount, totalCardsCount) {
    this.id = id;
    this.name = name;
    this.toReviewCount = toReviewCount;
    this.totalCardsCount = totalCardsCount;

    this.#rootDiv = document.createElement("div");
    this.#rootDiv.classList.add("deck");
    this.#rootDiv.dataset.deckId = this.id;
    
    const titlePara = document.createElement("p");
    this.#rootDiv.appendChild(titlePara);
    titlePara.classList.add("deck-title");
    titlePara.textContent = this.name;

    const menuDiv = document.createElement("div");
    this.#rootDiv.appendChild(menuDiv);
    menuDiv.classList.add("deck-menu");

    const editAnchor = document.createElement("a");
    menuDiv.appendChild(editAnchor);
    editAnchor.href = `/decks/${this.id}/edit`;
    editAnchor.classList.add("edit-deck");
    editAnchor.textContent = "Edit";

    const infoDiv = document.createElement("div");
    this.#rootDiv.appendChild(infoDiv);
    infoDiv.classList.add("deck-info");

    const reviewInfoPara = document.createElement("p");
    infoDiv.appendChild(reviewInfoPara);
    reviewInfoPara.classList.add("review-info");

    const reviewInfoText = document.createTextNode("To review: ");
    reviewInfoPara.appendChild(reviewInfoText);

    const reviewCountSpan = document.createElement("span");
    reviewInfoPara.appendChild(reviewCountSpan);
    reviewCountSpan.classList.add("review-count");
    reviewCountSpan.textContent = this.toReviewCount;

    const totalCardsInfoPara = document.createElement("p");
    infoDiv.appendChild(totalCardsInfoPara);
    totalCardsInfoPara.classList.add("total-cards-info");

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
      urlParams.append("deck", this.id);
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
          const deck = new Deck(deckData.id, deckData.name, details.readyForReview, details.totalCards);
          deck.appendTo(document.querySelector("#deck-container"));
        }
        document.querySelector("#feedback-msg").textContent = "";
      })
      .catch(error => {
        document.querySelector("#feedback-msg").textContent = "An error has occurred while fetching decks.";
      });
});

