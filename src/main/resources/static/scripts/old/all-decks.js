let openDeckBtns = document.querySelectorAll('.open-deck-btn');
for (let openDeckBtn of openDeckBtns) {
  openDeckBtn.addEventListener('click', () => {
    let deckId = openDeckBtn.closest('.deck-container').dataset.deckId;
    openDeck(deckId);
  });
}

document.addEventListener('DOMContentLoaded', () => {
  fillAllDeckDetails();
});


function openDeck(deckId) {
  let timezoneId = Intl.DateTimeFormat().resolvedOptions().timeZone;
  let baseUrl = '/review';
  let urlParams = new URLSearchParams();
  urlParams.append('deckId', deckId);
  urlParams.append('timezone', timezoneId);
  let url = baseUrl + '?' + urlParams.toString();
  window.location.replace(url);
}


function fillAllDeckDetails() {
  let timezoneId = Intl.DateTimeFormat().resolvedOptions().timeZone;
  let deckContainerElems = document.querySelectorAll('.deck-container');
  for (let deckContainerElem of deckContainerElems) {
    fillSingleDeckDetails(deckContainerElem, timezoneId);
  }
}


async function fillSingleDeckDetails(deckContainerElem, timezoneId) {
  let deckId = deckContainerElem.dataset.deckId;
  let baseUrl = `/api/decks/${deckId}/details`;
  let urlParams = new URLSearchParams();
  urlParams.append('timezone', timezoneId);
  let url = baseUrl + '?' + urlParams.toString();
  let deckDetails = await performGetRequestBodyAsObject(url);

  let forReviewNumElem = deckContainerElem.querySelector('.for-review-num');
  forReviewNumElem.textContent = deckDetails.cardsForReviewNumber;
  let allCardsNumElem = deckContainerElem.querySelector('.all-cards-num');
  allCardsNumElem.textContent = deckDetails.totalCardsNumber;
}



function performGetRequestBodyAsObject(url) {
  return fetch(url)
    .then(response => {
      if (response.status >= 200 && response.status <= 299) {
        return response.json();
      } else {
        console.log(`Received the response status code ${response.status} from the server.`);
      }
    })
    .catch(error => {
      console.log(`An error has occured while trying to perform the request: ${error}`);
    });
}