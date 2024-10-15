"use strict";

import { callApi, callApiExpectNoBody } from "./modules/client.js";
import { formDataToJson } from "./modules/utils.js";
import { errorParam } from "./modules/constants.js";

const accountProfileForm = document.querySelector("#admin-update-account-profile-form");
const pwdForm = document.querySelector("#admin-update-pwd-form");
const deleteAccountForm = document.querySelector("#admin-delete-account-form");

const changePwdInputs = document.querySelectorAll("#admin-update-pwd-form input");
const changePwdSubmitBtn = document.querySelector('#admin-update-pwd-form button[type="submit"]');
const deleteAccountInput = document.querySelector("#admin-delete-account-form input");
const deleteAccountSubmitBtn = document.querySelector('#admin-delete-account-form button[type="submit"]');



document.addEventListener("DOMContentLoaded", () => {
  const accountId = accountIdFromUrl();
  const url = `/admin/users/${accountId}`;
  callApi(url)
    .then(userData => {
      fillCurrentData(userData);
    })
    .catch(err => console.error(err));
});


function accountIdFromUrl() {
  const urlRegex = /\/admin\/users\/(\d+)\/edit/;
  const result = urlRegex.exec(window.location.pathname);
  let accountId;
  if (result) {
    accountId = result[1];
  } else {
    window.location.replace(`/?${errorParam}`);
    return;
  }
  return accountId;
}

function fillCurrentData(userData) {
  document.getElementById("username").value = userData.username;
  document.getElementById("username").dataset.origValue = userData.username;
  document.getElementById("fname").value = userData.firstName;
  document.getElementById("fname").dataset.origValue = userData.firstName;
  document.getElementById("enabled").checked = userData.enabled;
  document.getElementById("enabled").dataset.origValue = userData.enabled;
  document.getElementById("non-locked").checked = userData.nonLocked;
  document.getElementById("non-locked").dataset.origValue = userData.nonLocked;
}

accountProfileForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const accountId = accountIdFromUrl();
  const url = `/admin/users/${accountId}`;
  const options = {
    method: "PUT",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  callApiExpectNoBody(url, options)
    .then(() => {
      document.querySelector("#feedback-msg-acc").textContent = "User updated successfully!";
      resetAccountProfileFormState();
    })
    .catch(err => console.error(err));
})

pwdForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const accountId = accountIdFromUrl();
  const url = `/admin/users/${accountId}/password`;
  const options = {
    method: "PUT",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  callApiExpectNoBody(url, options)
    .then(() => {
      document.getElementById("feedback-msg-pwd").textContent = "Password has been overwritten!";
      e.currentTarget.reset();
    })
    .catch(err => console.error(err));
});

deleteAccountForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const accountId = accountIdFromUrl();
  const url = `/admin/users/${accountId}`;
  const options = {
    method: "DELETE",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  callApiExpectNoBody(url, options)
    .then(() => {
      window.location.replace(`/admin`);
    })
    .catch(err => console.error(err));
});

const accEditables = document.querySelectorAll(".acc-editable");
accEditables.forEach((el) => {
  el.addEventListener("input", () => {
    checkValueSameness(".acc-editable", '#admin-update-account-profile-form button[type="submit"]');
  });
});

changePwdInputs.forEach(elem => {
  elem.addEventListener("input", () => {
    enableDisableChangePwdSubmitBtn();
  });
});

deleteAccountInput.addEventListener("input", () => {
  enableDisableDeleteAccountSubmitBtn();
});


function resetAccountProfileFormState() {
  const usernameInput = document.getElementById("username");
  usernameInput.dataset.origValue = usernameInput.value;
  const firstNameInput = document.getElementById("fname");
  firstNameInput.dataset.origValue = firstNameInput.value;
  const enabledCheckbox = document.getElementById("enabled");
  enabledCheckbox.dataset.origValue = enabledCheckbox.checked;
  const nonLockedCheckbox = document.getElementById("non-locked");
  nonLockedCheckbox.dataset.origValue = nonLockedCheckbox.checked;
  document.querySelector('#admin-update-account-profile-form button[type="submit"]').disabled = true;
}

function checkValueSameness(inputElemClass, submitBtnSelector) {
  const elems = document.querySelectorAll(inputElemClass);
  const submitBtn = document.querySelector(submitBtnSelector);
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

function enableDisableChangePwdSubmitBtn() {
  for (const input of changePwdInputs) {
    if (input.value === "") {
      changePwdSubmitBtn.disabled = true;
      return;
    }
  }
  changePwdSubmitBtn.disabled = false;
}

function enableDisableDeleteAccountSubmitBtn() {
  if (deleteAccountInput.value === "") {
    deleteAccountSubmitBtn.disabled = true;
  } else {
    deleteAccountSubmitBtn.disabled = false;
  }
}
