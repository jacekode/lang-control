Array.from(document.querySelectorAll('#signup-password-input, #repeat-signup-password-input'))
.forEach(input => {
    input.addEventListener('input', () => {
        checkSignupValidity()
    })
})
function checkSignupValidity() {
    let signUpPasswordInput = document.getElementById('signup-password-input')
    let signUpRepeatPasswordInput = document.getElementById('repeat-signup-password-input')
    if (signUpPasswordInput.value !== signUpRepeatPasswordInput.value) {
        signUpPasswordInput.setCustomValidity('Passwords do not match.')
        signUpRepeatPasswordInput.setCustomValidity('Passwords do not match.')
    } else {
        signUpPasswordInput.setCustomValidity('')
        signUpRepeatPasswordInput.setCustomValidity('')
    }
}