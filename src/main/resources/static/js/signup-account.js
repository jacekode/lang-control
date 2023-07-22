document.querySelector("form").addEventListener("submit", (event) => {
    event.preventDefault()
    const validInputValues = checkSignupValidity()
    if (validInputValues) {
        /// Create the new user in database ?? Either json or form data ?? 
    } else {
        /// Show message that input values are not correct
    }


})
function checkSignupValidity() {
    const firstName = document.getElementById("name-input")
    const userName = document.getElementById("username-input")
    const newPassword = document.getElementById("pwd")
    const repeatPassword = document.getElementById("repeat-pwd")
    /// Check to make sure both passwords are the same.
    if (newPassword.value !== repeatPassword.value) {
        newPassword.setCustomValidity("Passwords do not match.")
        repeatPassword.setCustomValidity("Passwords do not match.")
    } else {
        return {
            firstName: firstName.value,
            userName: userName.value,
            repeatPassword: repeatPassword.value
        }
    }

}