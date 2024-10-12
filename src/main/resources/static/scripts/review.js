"use strict";

import { callApi, generateSentences, translateText } from "./modules/client.js";
import { rsCompletedParam, rsErrorParam, RC_SKEY, RR_SKEY } from "./modules/constants.js";

document.addEventListener("DOMContentLoaded", (e) => {
  resetContainerState();
  const curCard = getFirstCard();
  fillReviewContainer(curCard);
  document.querySelector("#remaining-counter").textContent = sessionStorage.getItem(RR_SKEY);
  document.querySelector("#session-counter").textContent = "0";
});

const answerBtn = document.querySelector("#answer-btn");
answerBtn.addEventListener("click", (e) => {
  e.currentTarget.style.display = "none";
  document.querySelector(".divider").style.display = "block";
  document.querySelector("#card-back").style.display = "block";
  const curCard = getFirstCard();
  if (curCard.inLearnMode) {
    document.querySelector("#learn-mode-ratings").style.display = "flex";
  } else {
    document.querySelector("#review-mode-ratings").style.display = "flex";
  }
  if (!!curCard.targetExample ||
      !!curCard.translatedExample ||
      curCard.dynamicExamples) {
    document.querySelector("#example").style.display = "block";
  }
  showIntervalForecasts(curCard.id, curCard.inLearnMode);
});


document.querySelectorAll(".btn-rating").forEach(elem => {
  elem.addEventListener("click", (e) => {
    const cardId = document.querySelector("#review-container").dataset.cardId;
    const rating = e.currentTarget.dataset.urlValue;
    console.debug(`Rating: ${rating}, cardId: ${cardId}`);
    const resBody = postRating(cardId, rating);
    console.debug(resBody);
    goToNextCard();
  });
});

function getFirstCard() {
  const reviewCards = fetchObjFromStorage(RC_SKEY);
  if (reviewCards === null) {
    console.debug(`Session storage key "${RC_SKEY}" doesn't hold any value (returns null).`);
    window.location.replace(`/?${rsErrorParam}`);
    return;
  }
  if (reviewCards.length === 0) {
    console.debug(`"reviewCards.length is 0`);
    window.location.replace(`/?${rsCompletedParam}`);
    return;
  }
  return reviewCards[0];
}

function fetchObjFromStorage(storageKey) {
  const jsonString = sessionStorage.getItem(storageKey);
  if (jsonString === null) {
    return null;
  }
  return JSON.parse(jsonString);
}

function saveObjToStorage(storageKey, obj) {
  sessionStorage.setItem(storageKey, JSON.stringify(obj));
}

function showIntervalForecasts(cardId, isInLearnMode) {
  callApi(`/cards/${cardId}/forecasts`)
    .then((body) => {
      if (isInLearnMode) {
        document.querySelector("#dknow-forecast").textContent = body.forLearnDontKnow + " min";
        document.querySelector("#know-forecast").textContent = body.forLearnKnow + " min";
      } else {
        document.querySelector("#for-forecast").textContent = body.forReviewForgot + " min";
        document.querySelector("#par-forecast").textContent = body.forReviewPartially + " min";
        document.querySelector("#rem-forecast").textContent = body.forReviewRemember + " min";
      }
    })
    .catch((err) => {
      console.error(err);
    });
}

function postRating(cardId, rating) {
  const reqBody = {
    cardId: cardId,
    rating: rating,
  };
  const options = {
    method: "POST",
    body: JSON.stringify(reqBody),
    headers: {
      "Content-Type": "application/json"
    }
  };
  const url = `/sr/rating`;
  callApi(url, options)
    .then((body) => {
      return body;
    })
    .catch(err => console.error(err));
}

function goToNextCard() {
  removeFirstCard();
  const newFirst = getFirstCard();
  resetContainerState();
  fillReviewContainer(newFirst);
  updateCounters();
}

function removeFirstCard() {
  const cards = fetchObjFromStorage(RC_SKEY);
  cards.shift();
  saveObjToStorage(RC_SKEY, cards);
}

function fillReviewContainer(card) {
  document.querySelector("#review-container").dataset.cardId = card.id;
  document.querySelector("#deck-name").textContent = card.deck.name;
  document.querySelector("#card-front").textContent = card.translatedWord;
  document.querySelector("#card-back").textContent = card.targetWord;
  if (card.dynamicExamples) {
    generateDynamicExamples(card);
  } else {
    console.debug("Trying to use handwritten examples.");
    console.debug(`Target example: ${card.targetExample}`);
    console.debug(`!!card.targetExample = ${!!card.targetExample}`);
    if (!!card.targetExample) {
      console.debug(`Inside the if statement. card.targetExample = ${card.targetExample}`);
      document.querySelector("#target-example").textContent = card.targetExample;
    }
    if (!!card.translatedExample) {
      document.querySelector("#translated-example").textContent = card.translatedExample;
    }
  }
  console.debug(`Filled card data with id=${card.id}`);
}

async function generateDynamicExamples(card) {
  try {
    const genBody = await generateSentences(card.targetWord, card.targetLang, card.partOfSpeech, 1);
    document.querySelector("#target-example").textContent = genBody[0];
  } catch (error) {
    console.error(`Error during example generation: ${error}`);
    return;
  }
  try {
    const text = document.querySelector("#target-example").textContent;
    const traBody = await translateText(text, card.sourceLang, card.targetLang);
    document.querySelector("#translated-example").textContent = traBody.translation;
  } catch (error) {
    console.error(`Error during example generation: ${error}`);
  }
}

function resetContainerState() {
  document.querySelector("#answer-btn").style.display = "block";
  document.querySelector(".divider").style.display = "none";
  document.querySelector("#card-back").style.display = "none";
  document.querySelector("#learn-mode-ratings").style.display = "none";
  document.querySelector("#review-mode-ratings").style.display = "none";
  document.querySelector("#example").style.display = "none";
  document.querySelector("#target-example").textContent = "...";
  document.querySelector("#translated-example").textContent = "...";
}

function updateCounters() {
  const remainingCounter = document.querySelector("#remaining-counter");
  let rCount = Number(remainingCounter.textContent);
  remainingCounter.textContent =  rCount - 1;
  const sessionCounter = document.querySelector("#session-counter");
  let sCount = Number(sessionCounter.textContent);
  sessionCounter.textContent= sCount + 1;
}

function purgeSessionStorage() {
  sessionStorage.removeItem(RC_SKEY);
  sessionStorage.removeItem(RR_SKEY);
}

window.onbeforeunload = purgeSessionStorage;
