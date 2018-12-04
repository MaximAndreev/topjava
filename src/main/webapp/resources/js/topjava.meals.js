const ajaxUrl = "ajax/profile/meals/";
let datatableApi;

// $(document).ready(function () {
$(function () {
    datatableApi = $("#datatable").DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "dateTime"
            },
            {
                "data": "description"
            },
            {
                "data": "calories"
            },
            {
                "defaultContent": "Edit",
                "orderable": false
            },
            {
                "defaultContent": "Delete",
                "orderable": false
            }
        ],
        "order": [
            [
                0,
                "desc"
            ]
        ]
    });
    makeEditable();
});

function getFilterUrl() {
    return ajaxUrl + "filter?" + $("#filterTable").serialize();
}

function filter() {
    updateTable(getFilterUrl())
}

function clearFilter() {
    $("#filterTable")[0].reset();
    updateTable(ajaxUrl);
}
