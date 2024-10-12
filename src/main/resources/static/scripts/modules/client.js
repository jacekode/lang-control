import { USER_SETTINGS_SKEY, CSRF_HEADER_NAME } from "./constants.js";
import { getCookieValue } from "./utils.js";

/**
 * Prefix to the LangControl server REST API.
 */
const apiPref = "/api";

/**
 * Calls the LangControl API and returns the response body.
 * 
 * @param {string} url - a relative url to one of the LangControl REST API endpoints
 * @param {Object} options - optional Fetch API `fetch()`'s options object
 * @returns a response body parsed to an object
 */
async function callApi(url, options) {
  let response;
  if (options === undefined) {
    response = await fetch(`${apiPref}${url}`);
  } else {
    options = await setCsrfHeader(options);
    response = await fetch(`${apiPref}${url}`, options);
  }
  if (!response.ok) {
    console.debug(`Error thrown because of API response status code ${response.status}`);
    throw new Error(`An error has occurred during API call (status code: ${response.status}).`);
  } else {
    console.debug(`API response status: ${response.status}`);
    return await response.json();
  }
}

/**
 * Calls the LangControl API. Suggested use with endpoints which don't return any body
 * 
 * @param {string} url - a relative url to one of the LangControl REST API endpoints
 * @param {Object} options - optional Fetch API `fetch()`'s options object
 * @returns {Promise<number>} the status code of the response
 */
async function callApiExpectNoBody(url, options) {
  let response;
  if (options === undefined) {
    response = await fetch(`${apiPref}${url}`);
  } else {
    options = await setCsrfHeader(options);
    response = await fetch(`${apiPref}${url}`, options);
  }
  if (!response.ok) {
    console.debug(`Error thrown because of API response status code ${response.status}`);
    throw new Error(`An error has occurred during API call (status code: ${response.status}).`);
  } else {
    console.debug(`API response status: ${response.status}`);
    return response.status;
  }
}

/**
 * Calls the LangControl API to generate sentences with the given keyword.
 * 
 * @param {string} keyword - the word to be included in the sentence
 * @param {string} lang - ISO 639-1 language code of the keyword
 * @param {string} pos - grammatical part of speech of the keyword
 * @param {number} num - the number of sentences to generate (min 1, max 3)
 * @returns a response body object
 */
async function generateSentences(keyword, lang, pos, num) {
  const urlParams = new URLSearchParams();
  urlParams.append("keyword", keyword);
  urlParams.append("lang", lang);
  urlParams.append("pos", pos);
  urlParams.append("num", num);
  const url = `/generate/sentences?${urlParams}`;
  // console.debug(`Generate target sentence url: ${url}`);
  const resBody = await callApi(url);
  // console.debug(`Generated sentence: ${resBody[0]}`);
  return resBody;
}

/**
 * Calls the LangControl API to create a text translation.
 * 
 * @param {string} text - the text to be translated; max 240 characters
 * @param {string} to - ISO 639-1 language code of the desired translation language
 * @param {string} from - ISO 639-1 language code of the text to translate
 * @returns a response body object
 */
async function translateText(text, to, from) {
  const reqBody = {
    text: text,
    to: to,
    from: from,
  };
  const options = {
    method: "POST",
    body: JSON.stringify(reqBody),
    headers: {
      "Content-Type": "application/json"
    }
  };
  const url = "/generate/translate";
  // console.debug(`Translation request body: ${JSON.stringify(reqBody)}`);
  const resBody = await callApi(url, options);
  // console.debug(`Returned translation: ${JSON.stringify(resBody)}`);
  return resBody;
}

/**
 * Calls the LangControl API to fetch dictionary translations of a word.
 * 
 * @param {string} word the word to be translated
 * @param {string} from the ISO 639-1 language code of the source word
 * @param {string} to the ISO 639-1 language code of the desired translations' language
 * @param {string} pos the grammatical part of speech of the source word
 * @returns a response body object
 */
async function lookupDictionary(word, from, to, pos) {
  const urlParams = new URLSearchParams();
  urlParams.append("word", word);
  urlParams.append("from", from);
  urlParams.append("to", to);
  urlParams.append("pos", pos);
  const url = `/generate/dictionary?${urlParams}`;
  console.debug(`Dictionary call url: ${url}`);
  const resBody = await callApi(url);
  console.debug(`Dictionary response body: ${JSON.stringify(resBody)}`);
  return resBody;
}

/**
 * Calls the LangControl API to fetch the current user settings object.
 * 
 * @returns a user settings object
 */
async function getUserSettings() {
  if (sessionStorage.getItem(USER_SETTINGS_SKEY)) {
    return JSON.parse(sessionStorage.getItem(USER_SETTINGS_SKEY));
  }

  const url = "/settings";
  let resBody;
  try {
    resBody = await callApi(url);
    sessionStorage.setItem(USER_SETTINGS_SKEY, JSON.stringify(resBody));
  } catch (err) {
    console.error(err);
  }
  return resBody;
}

async function getCsrfToken() {
  let token = getCookieValue("XSRF-TOKEN");
  if (token) {
    return token;
  }
  await fetch(apiPref, {
    method: "POST"
  });
  token = getCookieValue("XSRF-TOKEN");
  console.debug("Token: " + token);
  return token;
}

async function setCsrfHeader(options) {
  console.debug("Options before: " + JSON.stringify(options));
  if (["POST", "PUT", "DELETE"].includes(options.method)) {
    if (options.headers) {
      options.headers[CSRF_HEADER_NAME] = await getCsrfToken();
    } else {
      options.headers = { [CSRF_HEADER_NAME]: await getCsrfToken() };
    }
  }
  console.debug("Options after: " + JSON.stringify(options));
  return options;
}

export { callApi, callApiExpectNoBody, generateSentences, translateText, lookupDictionary, getUserSettings, getCsrfToken };
