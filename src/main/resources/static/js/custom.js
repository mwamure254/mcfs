function togglePassword(id, btn) {
    const input = document.getElementById(id);
    const icon = btn.querySelector('i');
    if (input.type === 'password') { input.type = 'text'; icon.classList.replace('fa-eye', 'fa-eye-slash'); }
    else { input.type = 'password'; icon.classList.replace('fa-eye-slash', 'fa-eye'); }
}

/*!-- ================= Alert Dismiss ================= -->*/
document.addEventListener("DOMContentLoaded", function () {
    const alertError = document.getElementById("floatingAlertError");
    if (alertError) {
        setTimeout(() => {
            const brAlert = bootstrap.Alert.getOrCreateInstance(alertError);
            brAlert.close();
        }, 5000);
    }

    const alertSuccess = document.getElementById("floatingAlertSuccess");
    if (alertSuccess) {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alertSuccess);
            bsAlert.close();
        }, 5000);
    }
});

/*!-- ================= TABLE SEARCH ================= -->*/
$(document).ready(function () {

    const table = $("#table");
    const tbody = table.find("tbody");

    const rows = tbody.find("tr");

    const previousButton = $("#auditPrevious");
    const nextButton = $("#auditNext");
    const pagination = $("#auditPagination");
    const pageInfo = $("#auditPageInfo");

    const searchInput = $("#tableSearch");

    // Number of records per page
    const rowsPerPage = 20;

    let currentPage = 1;
    let filteredRows = rows;

    /*
     * ==========================================
     * DISPLAY TABLE
     * ==========================================
     */
    function displayTable() {
        const totalRows = filteredRows.length;
        const totalPages = Math.max(
            1,
            Math.ceil(totalRows / rowsPerPage)
        );

        // Make sure current page is valid
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        const start = (currentPage - 1) * rowsPerPage;
        const end = start + rowsPerPage;

        // Hide every row first
        rows.hide();

        // Show rows for current page
        filteredRows.slice(start, end).show();

        /*
         * ==========================================
         * PAGE INFORMATION
         * ==========================================
         */
        if (totalRows === 0) {
            pageInfo.text("No entries found");

        } else {

            const first = start + 1;
            const last = Math.min(end, totalRows);

            pageInfo.text(
                "Showing " +
                first +
                "–" +
                last +
                " of " +
                totalRows +
                " entries"
            );
        }

        /*
         * ==========================================
         * PREVIOUS BUTTON
         * ==========================================
         */
        if (currentPage <= 1) {

            previousButton.addClass("disabled");

        } else {

            previousButton.removeClass("disabled");
        }

        /*
         * ==========================================
         * NEXT BUTTON
         * ==========================================
         */
        if (currentPage >= totalPages) {

            nextButton.addClass("disabled");

        } else {

            nextButton.removeClass("disabled");
        }

        /*
         * ==========================================
         * PAGE NUMBERS
         * ==========================================
         */
        pagination
            .find(".audit-page-number")
            .remove();

        /*
         * Show a limited number of page buttons
         */
        let startPage = Math.max(
            1,
            currentPage - 2
        );

        let endPage = Math.min(
            totalPages,
            startPage + 4
        );

        // Adjust start if near the end
        if ((endPage - startPage) < 4) {
            startPage = Math.max(
                1,
                endPage - 4
            );
        }

        for (let page = startPage; page <= endPage; page++) {

            const li = $("<li>")
                .addClass("page-item audit-page-number");

            if (page === currentPage) {
                li.addClass("active");
            }

            const link = $("<a>")
                .addClass("page-link")
                .attr("href", "#")
                .text(page);

            link.on("click", function (event) {
                event.preventDefault();
                currentPage = page;
                displayTable();
            });

            li.append(link);

            // Insert before Next button
            nextButton.before(li);
        }
    }

    /*
     * ==========================================
     * PREVIOUS
     * ==========================================
     */
    previousButton.find("a").on("click", function (event) {
        event.preventDefault();
        if (currentPage > 1) {

            currentPage--;

            displayTable();
        }

    });

    /*
     * ==========================================
     * NEXT
     * ==========================================
     */
    nextButton.find("a").on("click", function (event) {
        event.preventDefault();
        const totalPages = Math.ceil(
            filteredRows.length / rowsPerPage
        );

        if (currentPage < totalPages) {

            currentPage++;
            displayTable();
        }

    });

    /*
     * ==========================================
     * SEARCH
     * ==========================================
     */
    searchInput.on("keyup", function () {
        const value = $(this)
            .val()
            .toLowerCase()
            .trim();

        filteredRows = rows.filter(function () {
            return $(this)
                .text()
                .toLowerCase()
                .indexOf(value) > -1;

        });

        currentPage = 1;
        displayTable();
    });

    /*
     * ==========================================
     * INITIAL DISPLAY
     * ==========================================
     */
    filteredRows = rows;
    displayTable();

});

