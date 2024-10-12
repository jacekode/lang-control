/**
 * Converts the form element's data to a JSON string.
 * 
 * @param {HTMLFormElement} formElement
 * @returns {string} a JSON string built from the form element's data
 */
function formDataToJson(formElement) {
  const formData = new FormData(formElement);
  return JSON.stringify(Object.fromEntries(formData));
}

/**
 * Returns the currently selected option element of a particular select element.
 * 
 * @param {string} selectId the id of an HTML select tag
 * @returns {HTMLOptionElement} the currently selected option element
 */
function getSelectedOption(selectId) {
  const select = document.getElementById(selectId);
  return select.options[select.selectedIndex];
}

/**
 * Returns the value of the cookie with the given name.
 * 
 * @param {string} name the name of the cookie
 * @returns {string} the value of the cookie
 */
function getCookieValue(name) {
  const cookies = document.cookie.split("; ");
  for (const cookie of cookies) {
    const [cookieName, cookieValue] = cookie.split("=");
    if (cookieName === name) {
      return decodeURIComponent(cookieValue);
    }
  }
  return null;
}

export { formDataToJson, getSelectedOption, getCookieValue };
