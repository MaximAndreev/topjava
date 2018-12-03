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

function updateTable() {
    let form = $("#filterTable");
    $.get(ajaxUrl + "filter?" + form.serialize(), function (data) {
        datatableApi.clear().rows.add(data).draw();
    });
}

function clearFilter() {
    let form = $("#filterTable");
    form[0].reset();
    updateTable();
}
