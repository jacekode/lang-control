"use strict";

import { callApi, callApiExpectNoBody } from "./modules/client.js";
import { formDataToJson } from "./modules/utils.js";
import { LOGGED_IN_SKEY, accountDelParam } from "./modules/constants.js";

const usernameInput = document.querySelector("#username");
const fnInput = document.querySelector("#fname");
const profileInputs = document.querySelectorAll("#update-account-profile-form input");
const profileSubmitBtn = document.querySelector('#update-account-profile-form button[type="submit"]');
const changePwdInputs = document.querySelectorAll("#update-pwd-form input");
const changePwdSubmitBtn = document.querySelector('#update-pwd-form button[type="submit"]');
const deleteAccountInput = document.querySelector("#delete-account-form input");
const deleteAccountSubmitBtn = document.querySelector('#delete-account-form button[type="submit"]');
const profileForm = document.querySelector("#update-account-profile-form");
const changePwdForm = document.querySelector("#update-pwd-form");
const deleteAccountForm = document.querySelector("#delete-account-form");

document.addEventListener("DOMContentLoaded", () => {
  callApi("/account")
    .then(body => {
      usernameInput.value = body.username;
      usernameInput.dataset.origValue = body.username;
    })
    .catch(err => console.error(err));
  callApi("/profile")
    .then(body => {
      fnInput.value = body.firstName;
      fnInput.dataset.origValue = body.firstName;
    })
    .catch(err => console.error(err));
});

profileInputs.forEach(elem => {
  elem.addEventListener("input", () => {
    enableDisableProfileSubmitBtn();
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

profileForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const url = "/account";
  const options = {
    method: "PUT",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  callApi(url, options)
    .then(() => document.getElementById("feedback-msg-acc").textContent = "Updated successfully!")
    .catch(err => console.error(err));
})

changePwdForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const url = "/account/password";
  const options = {
    method: "PUT",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  callApiExpectNoBody(url, options)
    .then(() => {
      document.getElementById("feedback-msg-pwd").textContent = "Updated successfully!";
      e.currentTarget.reset();
    })
    .catch(err => console.error(err));
});

deleteAccountForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const url = "/account";
  const options = {
    method: "DELETE",
    body: formDataToJson(e.currentTarget),
    headers: {
      "Content-Type": "application/json"
    }
  };
  callApiExpectNoBody(url, options)
    .then(() => {
      sessionStorage.setItem(LOGGED_IN_SKEY, "false");
      window.location.replace(`/?${accountDelParam}`);
    })
    .catch(err => console.error(err));
});


function enableDisableProfileSubmitBtn() {
  for (const input of profileInputs) {
    if (input.dataset.origValue !== input.value) {
      profileSubmitBtn.disabled = false;
      return;
    }
  }
  profileSubmitBtn.disabled = true;
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
