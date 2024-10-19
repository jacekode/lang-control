"use strict";

export default class LanguageCode {

  #code;

  static #codeToNameMap = {
    en: "English",
    de: "German",
    es: "Spanish",
    it: "Italian",
    fr: "French",
    nl: "Dutch",
    cs: "Czech",
    uk: "Ukrainian",
    pt: "Portuguese",
    da: "Danish",
    sv: "Swedish",
    pl: "Polish",
    lv: "Latvian",
    lt: "Lithuanian",
    sk: "Slovak",
    et: "Estonian",
    fi: "Finnish",
    el: "Greek",
    sl: "Slovenian",
    bg: "Bulgarian",
    ro: "Romanian",
    tr: "Turkish",
    hu: "Hungarian",
    id: "Indonesian",
  };

  constructor(code) {
    this.#code = code;
  }

  getLangName() {
    return LanguageCode.#codeToNameMap[this.#code];
  }
}
