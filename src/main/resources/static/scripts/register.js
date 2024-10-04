"use strict";

import { formDataToJson } from "./modules/utils.js";

const registerForm = document.querySelector("#register-form");

registerForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const reqBody = formDataToJson(registerForm);
  // console.debug(`Request body: ${reqBody}`);
  fetch(`/api/auth/register`, {
    method: "POST",
    body: reqBody,
    headers: {
      "Content-Type": "application/json"
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
