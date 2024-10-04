"use strict";

import { LOGGED_IN_SKEY } from "./modules/constants.js";

const queryParams = new URLSearchParams(window.location.search);
if (queryParams.has("logout")) {
  document.querySelector("#login-form .feedback-msg").textContent = "You have logged out successfully!"
} else if (queryParams.has("accountDeleted")) {
  document.querySelector("#login-form .feedback-msg").textContent = "Account has been deleted!";
}

const loginForm = document.querySelector("#login-form");

loginForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const reqBody = new URLSearchParams(new FormData(loginForm));
  // console.debug(`Request body: ${reqBody}`);
  fetch(`/api/auth/login`, {
    method: "POST",
    body: reqBody,
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
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
