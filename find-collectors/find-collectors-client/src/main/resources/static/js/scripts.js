function checkPassword() {
  var password = document.getElementById("password");
  var passwordConfirm = document.getElementById("passwordConfirm");

  passwordConfirm.addEventListener("keyup", () => {
    if (password.value) {
      if (passwordConfirm.value.length >= 4) {
        var forms = document.querySelectorAll(".needs-validation");
        forms[0].classList.add("was-validated");

        if (password.value !== passwordConfirm.value) {
          passwordConfirm.setCustomValidity("isvalid");
        } else {
          passwordConfirm.setCustomValidity("");
        }
      }
    }
  });
}

function checkValues() {
  var form = document.querySelectorAll(".needs-validation")[0];

  if (!form.checkValidity()) {
    form.classList.add("was-validated");
    return false;
  }
  return true;
}

function loadCities() {}
