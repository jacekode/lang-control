"use strict";

import { formDataToJson } from "./modules/utils.js";
import { getCsrfToken } from "./modules/client.js";
import { CSRF_HEADER_NAME } from "./modules/constants.js";


const registerForm = document.querySelector("#register-form");

registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const reqBody = formDataToJson(registerForm);
  const csrfToken = await getCsrfToken();
  // console.debug(`Request body: ${reqBody}`);
  fetch(`/api/auth/register`, {
    method: "POST",
    body: reqBody,
    headers: {
      "Content-Type": "application/json",
      [CSRF_HEADER_NAME]: csrfToken,
    },
  })
    .then((response) => {
      // console.debug(`Response status: ${response.status}`);
      if (response.ok) {
        document.querySelector(".feedback-msg").textContent = "Account created successfully!";
      } else if (response.status === 409) {
        document.querySelector(".feedback-msg").textContent = "The chosen username is already taken.";
      } else {
        console.log(`Response status received: ${response.status}`);
        throw new Error("An error has occurred during registration. Please try again.");
      }
      return response.json();
    })
    // .then((resBody) => console.debug("Response body: " + JSON.stringify(resBody)))
    .catch((err) => {
      document.querySelector(".feedback-msg").textContent = "An error has occurred during registration. Please try again.";
    });
});


// REGISTER FORM VALIDATION
const usernameInput = document.querySelector("#username");
usernameInput.addEventListener("input", (e) => {
  if (e.currentTarget.validity.valueMissing) {
    e.currentTarget.setCustomValidity("The username is required.");
  } else if (e.currentTarget.validity.tooShort || e.currentTarget.validity.tooLong) {
    e.currentTarget.setCustomValidity("The username must be between 4 and 30 characters long.");
  } else if (e.currentTarget.validity.patternMismatch) {
    e.currentTarget.setCustomValidity("The username may only consist of letters, digits and underscores but not two underscores next to each other.");
  } else {
    e.currentTarget.setCustomValidity("");
  }
});

const firstNameInput = document.querySelector("#fname");
firstNameInput.addEventListener("input", (e) => {
  if (e.currentTarget.validity.valueMissing) {
    e.currentTarget.setCustomValidity("The name is required.");
  } else if (e.currentTarget.validity.tooLong) {
    e.currentTarget.setCustomValidity("The name cannot be longer than 50 characters.");
  } else if (e.currentTarget.validity.patternMismatch) {
    e.currentTarget.setCustomValidity("The name may only contain letters.");
  } else {
    e.currentTarget.setCustomValidity("");
  }
});

const pwdInput = document.querySelector("#pwd");
pwdInput.addEventListener("input", (e) => {
  if (e.currentTarget.validity.valueMissing) {
    e.currentTarget.setCustomValidity("The password is required.");
  } else if (e.currentTarget.validity.tooShort || e.currentTarget.validity.tooLong) {
    e.currentTarget.setCustomValidity("The password must be between 8 and 50 characters long.");
  } else if (e.currentTarget.validity.patternMismatch) {
    e.currentTarget.setCustomValidity("The password should contain a lowercase letter, an uppercase letter, a digit and a special symbol.");
  } else {
    e.currentTarget.setCustomValidity("");
  }
});
