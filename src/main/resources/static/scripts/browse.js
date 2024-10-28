"use strict";

import { callApi } from "./modules/client.js";
import LanguageCode from './modules/language-code.js';

let pageMetadata = {
  curPage: 0,
  pageSize: 10,
  totalPages: 0,
  totalElements: 0
};

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
  urlParams.append("page", pageMetadata.curPage);
  urlParams.append("size", pageMetadata.pageSize);
  const url = `/cards?${urlParams}`;

  callApi(url)
    .then(body => {
      // console.debug(body.content[0]);
      for (const deck of body.content) {
        const row = new CardRow(deck);
        row.appendTo(cardsTableTbody);
      }
      fillPageMetadata(body.page.number, body.page.totalPages, body.page.size, body.page.totalElements);
    });
});

function fillPageMetadata(curPage, totalPages, pageSize, totalElements) {
  pageMetadata.curPage = curPage;
  pageMetadata.totalPages = totalPages;
  pageMetadata.pageSize = pageSize;
  pageMetadata.totalElements = totalElements;
  document.querySelector("#page-num").textContent = curPage+1;
  document.querySelector("#total-pages").textContent = totalPages;
  document.querySelector("#total-elements").textContent = totalElements;
}

const pageSizeInput = document.querySelector("#page-size-input");
pageSizeInput.addEventListener("change", (e) => {
  pageMetadata.pageSize = e.target.value;
  replaceTableRows(0, pageMetadata.pageSize);
});

function replaceTableRows(curPage, pageSize) {
  const cardsTableTbody = document.querySelector("#cards-table tbody");
  cardsTableTbody.innerHTML = "";
  let urlParams = new URLSearchParams();
  urlParams.append("sort", "created");
  urlParams.append("order", "desc");
  urlParams.append("page", curPage);
  urlParams.append("size", pageSize);
  const url = `/cards?${urlParams}`;

  callApi(url)
    .then(body => {
      // console.debug(body.content[0]);
      for (const deck of body.content) {
        const row = new CardRow(deck);
        row.appendTo(cardsTableTbody);
      }
      fillPageMetadata(body.page.number, body.page.totalPages, body.page.size, body.page.totalElements);
    });
}

const prevPageBtn = document.querySelector(".prev-page");
prevPageBtn.addEventListener("click", (e) => {
  const newPageNum = pageMetadata.curPage-1 < 0 ? 0 : pageMetadata.curPage-1;
  replaceTableRows(newPageNum, pageMetadata.pageSize);
});

const nextPageBtn = document.querySelector(".next-page");
nextPageBtn.addEventListener("click", (e) => {
  const newPageNum = pageMetadata.curPage+1 < pageMetadata.totalPages ? pageMetadata.curPage+1 : pageMetadata.totalPages-1;
  replaceTableRows(newPageNum, pageMetadata.pageSize);
});
