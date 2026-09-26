function togglePassword(fieldId, button) {
    const input = document.getElementById(fieldId);
    const icon = button.querySelector("i");

    if (input.type === "password") {
        input.type = "text";
        icon.classList.remove("bi-eye");
        icon.classList.add("bi-eye-slash");
    } else {
        input.type = "password";
        icon.classList.remove("bi-eye-slash");
        icon.classList.add("bi-eye");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const alert = document.getElementById("floatingAlert");

    if (alert) {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        }, 5000);
    }
});

// ROLE SEARCH
document.getElementById("rSearch")
    .addEventListener("keyup", function () {

        let sear = this.value.toLowerCase();

        document
            .querySelectorAll("#rTable tbody tr")
            .forEach(function (row) {

                let text = row.innerText.toLowerCase();

                row.style.display =
                    text.includes(sear)
                        ? ""
                        : "none";

            });

    });

// STATUS SEARCH
document.getElementById("sSearch")
    .addEventListener("keyup", function () {

        let seas = this.value.toLowerCase();

        document
            .querySelectorAll("#sTable tbody tr")
            .forEach(function (row) {

                let text = row.innerText.toLowerCase();

                row.style.display =
                    text.includes(seas)
                        ? ""
                        : "none";

            });

    });

// CLASS SEARCH
document.getElementById("cSearch")
    .addEventListener("keyup", function () {

        let seac = this.value.toLowerCase();

        document
            .querySelectorAll("#cTable tbody tr")
            .forEach(function (row) {

                let text = row.innerText.toLowerCase();

                row.style.display =
                    text.includes(seac)
                        ? ""
                        : "none";

            });

    });

// TYPE SEARCH
document.getElementById("tSearch")
    .addEventListener("keyup", function () {

        let seat = this.value.toLowerCase();

        document
            .querySelectorAll("#tTable tbody tr")
            .forEach(function (row) {

                let text = row.innerText.toLowerCase();

                row.style.display =
                    text.includes(seat)
                        ? ""
                        : "none";

            });

    });

    // TYPE SEARCH
document.getElementById("userSearch")
    .addEventListener("keyup", function () {

        let seat = this.value.toLowerCase();

        document
            .querySelectorAll("#uTable tbody tr")
            .forEach(function (row) {

                let text = row.innerText.toLowerCase();

                row.style.display =
                    text.includes(seat)
                        ? ""
                        : "none";

            });

    });