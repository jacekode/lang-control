document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll("*").forEach(el => {
    el.hidden = false;
    if (el.textContent === "...") {
      el.textContent = "[TEST]";
    }
  });
});
