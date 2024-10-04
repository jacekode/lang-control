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

export { formDataToJson, getSelectedOption };
