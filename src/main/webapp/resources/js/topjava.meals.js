const mealAjaxUrl = "ajax/profile/meals/";

$.ajaxSetup({
    converters: {
        "text json": function (result) {
            var json = JSON.parse(result);
            // Casting to a jQquery object prevents from iteration over non-object types
            $(json).each(function (index, element) {
                element.dateTime = element.dateTime.replace('T', ' ').substr(0, 16);
            });
            return json;
        }
    }
});

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: mealAjaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get("ajax/profile/meals/", updateTableByData);
}

$(function () {
    makeEditable({
        ajaxUrl: mealAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
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
                    "orderable": false,
                    "render": renderEditBtn
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false,
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                $(row).attr("data-mealExcess", data.excess);
            }
        }),
        updateTable: updateFilteredTable
    });
});

$(function () {
    let dates = ["startDate", "endDate"];
    $.each(dates, function (index, value) {
        jQuery('#' + value).datetimepicker({
            timepicker: false,
            format: 'Y-m-d'
        });
    });

    let times = ["startTime", "endTime"];
    $.each(times, function (index, value) {
        jQuery('#' + value).datetimepicker({
            datepicker: false,
            format: 'H:i'
        });
    });

    jQuery("#dateTime").datetimepicker({format: 'Y-m-d H:i'});
});