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

function removeOptions(select) {
  while (select.options.length > 1) {
    select.remove(1);
  }
}

function loadCities() {
  const statesSelect = document.getElementById("state");

  statesSelect.addEventListener("change", async (state) => {
    const citiesSelect = document.getElementById("cityId");

    removeOptions(citiesSelect);

    const response = await fetch(
      `http://localhost:8081/api/city?stateId=${state.target.value}`
    );

    const cities = await response.json();

    cities.forEach((city) => {
      citiesSelect.appendChild(new Option(city.name, city.id));
    });
  });
}

function format(date) {
  return (
    [date.getFullYear(), date.getMonth() + 1, date.getDate()].join("-") +
    " " +
    [date.getHours(), date.getMinutes(), date.getSeconds()].join(":")
  );
}

function formatDateAndTime() {
  const dateSelected = document.getElementById("dateCollect");

  dateSelected.addEventListener("change", (date) => {
    const dateCollect = new Date(date.target.value);

    const dateAndTime = document.getElementById("dateAndTime");

    dateAndTime.value = format(dateCollect);
  });
}
