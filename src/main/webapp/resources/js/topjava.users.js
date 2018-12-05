const ajaxUrl = "ajax/admin/users/";
let datatableApi;

// $(document).ready(function () {
$(function () {
    datatableApi = $("#datatable").DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "email"
            },
            {
                "data": "roles"
            },
            {
                "data": "enabled"
            },
            {
                "data": "registered"
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
                "asc"
            ]
        ]
    });
    makeEditable();
});

function getFilterUrl() {
    return ajaxUrl;
}

function disable(id) {
    let checkbox = $("#enabled-" + id);
    let enable = checkbox.is(":checked");
    $.ajax({
        url: ajaxUrl + id + "?enabled=" + enable,
        type: "PUT"
    }).done(function () {
        let row = $("#row-" + id);
        enable ? row.removeClass("text-muted") : row.addClass("text-muted");
        successNoty(enable ? "Enabled" : "Disabled");
    }).fail(function () {
         checkbox.prop('checked', !enable);
    });
}