"use strict";

import { LOGGED_IN_SKEY, CSRF_HEADER_NAME } from "./modules/constants.js";
import { getCsrfToken } from "./modules/client.js";

/**
 * @type {HTMLFormElement}
 */
const loginForm = document.querySelector("#login-form");
const feedbackMsg = document.querySelector(".feedback-msg");
const usernameInput = document.querySelector("#username");
const pwdInput = document.querySelector("#pwd");


const queryParams = new URLSearchParams(window.location.search);
if (queryParams.has("logout")) {
  document.querySelector("#login-form .feedback-msg").textContent = "You have logged out successfully!"
} else if (queryParams.has("accountDeleted")) {
  document.querySelector("#login-form .feedback-msg").textContent = "Account has been deleted!";
}


loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  loginForm.reportValidity();
  const reqBody = new URLSearchParams(new FormData(loginForm));
  const csrfToken = await getCsrfToken();
  // console.debug(`Request body: ${reqBody}`);
  // console.debug("Csrf token: " + csrfToken)
  fetch(`/api/auth/login`, {
    method: "POST",
    body: reqBody,
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      [CSRF_HEADER_NAME]: csrfToken,
    }
  })
    .then((response) => {
      console.log(`Response status: ${response.status}`);
      if (response.ok) {
        sessionStorage.setItem(LOGGED_IN_SKEY, "true");
        document.querySelector(".feedback-msg").textContent = "Logged in successfully! Redirecting to the app...";
        setTimeout(() => {
          window.location.href = "/";
        }, 2000);
      } else if (400 <= response.status <= 499) {
        response.json()
          .then(body => {
            let desc = body.errorDescription.toLowerCase();
            console.log("desc: " + desc);
            if (desc.includes("locked")) {
              document.querySelector(".feedback-msg").textContent = "Your account has been locked.";
            } else if (desc.includes("disabled")) {
              document.querySelector(".feedback-msg").textContent = "Your account is disabled.";
            } else if (desc.includes("invalid")) {
              document.querySelector(".feedback-msg").textContent = "Invalid username or password.";
            } else {
              document.querySelector(".feedback-msg").textContent = "Login was unsuccessful.";
            }
          });
      }
    })
});


// LOGIN FORM VALIDATION
usernameInput.addEventListener("input", (e) => {
  if (e.currentTarget.validity.valueMissing) {
    e.currentTarget.setCustomValidity("The username is required.");
    feedbackMsg.textContent = "The username is required.";
  } else if (e.currentTarget.validity.tooShort || e.currentTarget.validity.tooLong) {
    e.currentTarget.setCustomValidity("The username must be between 4 and 30 characters long.");
    feedbackMsg.textContent = "The username must be between 4 and 30 characters long.";
  } else {
    e.currentTarget.setCustomValidity("");
    feedbackMsg.textContent = "";
  }
});

pwdInput.addEventListener("input", (e) => {
  if (e.currentTarget.validity.valueMissing) {
    e.currentTarget.setCustomValidity("The password is required.");
    feedbackMsg.textContent = "The password is required.";
  } else if (e.currentTarget.validity.tooShort || e.currentTarget.validity.tooLong) {
    e.currentTarget.setCustomValidity("The password must be between 8 and 50 characters.");
    feedbackMsg.textContent = "The password must be between 8 and 50 characters.";
  } else {
    e.currentTarget.setCustomValidity("");
    feedbackMsg.textContent = "";
  }
});