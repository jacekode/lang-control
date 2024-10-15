"use strict";

import { callApi } from "./modules/client.js";

class UserRow {

  #rootTr;

  constructor(userData) {
    this.#rootTr = document.createElement("tr");

    const idTd = document.createElement("td");
    this.#rootTr.appendChild(idTd);
    idTd.textContent = userData.accountId;

    const usernameTd = document.createElement("td");
    this.#rootTr.appendChild(usernameTd);
    usernameTd.textContent = userData.username;

    const firstNameTd = document.createElement("td");
    this.#rootTr.appendChild(firstNameTd);
    firstNameTd.textContent = userData.firstName;

    const enabledTd = document.createElement("td");
    this.#rootTr.appendChild(enabledTd);
    enabledTd.textContent = userData.enabled;

    const lockedTd = document.createElement("td");
    this.#rootTr.appendChild(lockedTd);
    lockedTd.textContent = !userData.nonLocked;

    const isAdminTd = document.createElement("td");
    this.#rootTr.appendChild(isAdminTd);
    isAdminTd.textContent = userData.roles.includes("ROLE_ADMIN");
    
    const editTd = document.createElement("td");
    this.#rootTr.appendChild(editTd);
    const editAnchor = document.createElement("a");
    editTd.appendChild(editAnchor);
    editAnchor.href = `/admin/users/${userData.accountId}/edit`;
    editAnchor.textContent = "Edit";
  }

  appendTo(tbody) {
    tbody.appendChild(this.#rootTr);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const usersTableTbody = document.querySelector("#users-table tbody");
  const url = "/admin/users"

  callApi(url)
    .then(body => {
      // console.debug(body.content[0]);
      for (const userOverview of body) {
        const row = new UserRow(userOverview);
        row.appendTo(usersTableTbody);
      }
    });
});