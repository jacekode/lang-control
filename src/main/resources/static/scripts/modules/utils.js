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


/**
 * Represents a time duration, e.g.: 34 seconds, 17 minutes, 3 hours, 20 days, etc.
 */
class Duration {

  #seconds;

  constructor(seconds) {
    this.#seconds = seconds;
  }

  /**
   * A static factory method which instantiates a new Duration object.
   * 
   * @param {number} value a float which specifies the value of this duration
   * @param {DurationUnit} durationUnit defines the time unit of this duration value
   * @returns {Duration} a Duration object with specified values
   */
  static of(value, durationUnit) {
    let seconds = value * durationUnit.multiplier;
    return new Duration(seconds);
  }

  /**
   * Converts the value of the duration to the specified DurationUnit.
   * 
   * @param {DurationUnit} durationUnit 
   * @param {number} decimalNum the number of decimal spaces to round to (no rounding if parameter not given)
   * @returns {number} a float value representing the duration in the desired DurationUnit
   */
  convertTo(durationUnit, decimalNum) {
    const converted = this.#seconds / durationUnit.multiplier;
    if (decimalNum === undefined) {
      return converted;
    }
    return parseFloat(converted.toFixed(decimalNum));
  }
}

/**
 * Enum for units of duration.
 * @readonly
 * @enum {{multiplier: number}}
 */
const DurationUnit = Object.freeze({
  SECOND: Object.freeze({multiplier: 1}),
  MINUTE: Object.freeze({multiplier: 60}),
  HOUR: Object.freeze({multiplier: 60 * 60}),
  DAY: Object.freeze({multiplier: 60 * 60 * 24}),
});


export { formDataToJson, getSelectedOption, getCookieValue, Duration, DurationUnit };
