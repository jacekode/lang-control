document.addEventListener('DOMContentLoaded', () => {
  convertDateTimesToLocal();
});


function convertDateTimesToLocal() {
  let dateTimeCells = document.querySelectorAll('.created-at-cell, .next-learn-encounter-cell');
  for (let cell of dateTimeCells) {
    let dateTimeString = cell.textContent;
    dateTimeString = dateTimeString + 'Z';
    let local = new Date(dateTimeString);
    let d = local.getDate();
    let m = local.getMonth();
    let y = local.getFullYear();
    let h = local.getHours();
    let min = local.getMinutes();
    let formattedDateTimeString = local.toLocaleString("en-GB", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit"
    });
    cell.textContent = formattedDateTimeString;
  }

  let dateCells = document.querySelectorAll('.next-review-encounter-cell');
  for (let cell of dateCells) {
    let dateTimeString = cell.textContent;
    dateTimeString = dateTimeString + 'Z';
    let local = new Date(dateTimeString);
    let d = local.getDate();
    let m = local.getMonth();
    let y = local.getFullYear();
    let formattedDateTimeString = local.toLocaleString("en-GB", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      timeZone: "UTC"
    });
    cell.textContent = formattedDateTimeString;
  }
}