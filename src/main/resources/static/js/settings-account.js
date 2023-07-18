document.getElementById('new-username-input').addEventListener('input', () => {
  manageDisabledAndHiddenStateInUsernameUpdateForm();
  checkUsernameInputValidity();
});

document.getElementById('update-username-form').addEventListener('submit', (e) => {
  e.preventDefault();
  performUsernameUpdatePutRequest(e.currentTarget, e.currentTarget.action)
    .then(responseBody => handleUsernameUpdateResponseBody(responseBody));
});

Array.from(document.querySelectorAll('#new-password-input, #repeat-password-input, #current-password-input'))
  .forEach(elem => {
    elem.addEventListener('input', () => {
      manageDisabledAndHiddenStateInPasswordUpdateForm();
      checkNewPasswordInputsValidity();
    });
  });

document.getElementById('update-password-form').addEventListener('submit', (e) => {
  e.preventDefault();
  performPasswordUpdatePutRequest(e.currentTarget, e.currentTarget.action)
    .then(responseBody => handlePasswordUpdateResponseBody(responseBody));
});

document.getElementById('delete-account-password-input')
  .addEventListener('input', (e) => {
    document.getElementById('delete-account-password-incorrect-info').setAttribute('hidden', 'true');
    if (e.currentTarget.value) {
      document.getElementById('open-delete-account-modal-btn').disabled = false;
    } else {
      document.getElementById('open-delete-account-modal-btn').disabled = true;
    }
  });

document.getElementById('delete-account-btn').addEventListener('click', () => {
  let deleteAccountForm = document.getElementById('delete-account-form');
  performDeleteAccountPostRequest(deleteAccountForm, deleteAccountForm.action);
});


function performUsernameUpdatePutRequest(formElem, requestUrl) {
  let requestBody = convertFormToUrlEncodedString(formElem);

  let requestOptions = {
    method: "PUT",
    headers: {
      'Content-Type': "application/x-www-form-urlencoded",
    },
    body: requestBody,
  };

  return fetch(requestUrl, requestOptions)
    .then(response => {
      if (response.ok) {
        document.getElementById('username-success-info').removeAttribute('hidden');
        return response.json();
      } else if (response.status == 409) {
        document.getElementById('username-taken-info').removeAttribute('hidden');
      }
    })
    .catch(error => console.log(error));
}


function handleUsernameUpdateResponseBody(responseBody) {
  let currentUsernameSpan = document.getElementById('current-username-span');
  currentUsernameSpan.textContent = responseBody.username;
  document.getElementById('new-username-input').value = '';
}


function performPasswordUpdatePutRequest(formElem, requestUrl) {
  let requestBody = convertFormToUrlEncodedString(formElem);

  let requestOptions = {
    method: "PUT",
    headers: {
      'Content-Type': "application/x-www-form-urlencoded",
    },
    body: requestBody,
  };

  return fetch(requestUrl, requestOptions)
  .then(response => {
    if (response.ok) {
      document.getElementById('password-success-info').removeAttribute('hidden');
      return response.json();
    } else if (response.status == 403) {
      document.getElementById('password-incorrect-info').removeAttribute('hidden');
    }
  })
  .catch(error => console.log(error));
}


function handlePasswordUpdateResponseBody(responseBody) {
  Array.from(document.querySelectorAll('#new-password-input, #repeat-password-input, #current-password-input'))
    .forEach(elem => {
      elem.value = '';
    });
}


function performDeleteAccountPostRequest(formElem, requestUrl) {
  let requestBody = convertFormToUrlEncodedString(formElem);

  let requestOptions = {
    method: "POST",
    headers: {
      'Content-Type': "application/x-www-form-urlencoded",
    },
    body: requestBody,
  };

  return fetch(requestUrl, requestOptions)
    .then(response => {
      if (response.ok) {
        window.location.replace('/?accountDeleted');
        return response.json();
      } else if (response.status == 403) {
        document.getElementById('delete-account-password-incorrect-info').removeAttribute('hidden');
      }
    })
    .catch(error => console.log(error));
}


function convertFormToUrlEncodedString(formElem) {
  let formDataUrlEncoded = new URLSearchParams(new FormData(formElem)).toString();
  return formDataUrlEncoded;
}


function manageDisabledAndHiddenStateInUsernameUpdateForm() {
  document.getElementById('username-taken-info').setAttribute('hidden', 'true');
  document.getElementById('username-success-info').setAttribute('hidden', 'true');
  let submitBtn = document.getElementById('update-username-submit-btn');
  if (document.getElementById('new-username-input').value) {
    submitBtn.removeAttribute('disabled');
  } else {
    submitBtn.setAttribute('disabled', 'true');
  }
}


function checkUsernameInputValidity() {
  let newUsernameInput = document.getElementById('new-username-input');
  let currentUsername = document.getElementById('current-username-span').textContent;
  if (newUsernameInput.value == currentUsername) {
    newUsernameInput.setCustomValidity('The username must be different.');
  } else {
    newUsernameInput.setCustomValidity('');
  }
}


function manageDisabledAndHiddenStateInPasswordUpdateForm() {
  document.getElementById('password-incorrect-info').setAttribute('hidden', 'true');
  document.getElementById('password-success-info').setAttribute('hidden', 'true');
  let submitBtn = document.getElementById('update-password-submit-btn');
  if (document.getElementById('new-password-input').value && 
      document.getElementById('repeat-password-input').value && 
      document.getElementById('current-password-input').value) {
    submitBtn.removeAttribute('disabled');
  } else {
    submitBtn.setAttribute('disabled', 'true');
  }
}


function checkNewPasswordInputsValidity() {
  let newPasswordInput = document.getElementById('new-password-input');
  let repeatPasswordInput = document.getElementById('repeat-password-input');
  if (newPasswordInput.value !== repeatPasswordInput.value) {
    newPasswordInput.setCustomValidity('Passwords do not match.');
    repeatPasswordInput.setCustomValidity('Passwords do not match.');
  } else {
    newPasswordInput.setCustomValidity('');
    repeatPasswordInput.setCustomValidity('');
  }
}