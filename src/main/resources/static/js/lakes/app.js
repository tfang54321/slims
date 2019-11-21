$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    var table = loadLakeList();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");
    singleTableRowOnSelect('#lake_list_table');
    form_original_data = $("#edit_form").serialize();

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#lake_list_table")) {
            e.preventDefault();
            var id = $('#lake_list_table').DataTable().row($('tr.selected')).data()["id"];

            editLake(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });
    $('#button_stream').click(function (e) {
        e.preventDefault();
        if (thereAreRowsSelected("#lake_list_table")) {
            var id = $('#lake_list_table').DataTable().row($('tr.selected')).data()["id"];

            window.location.href = getBaseString() + localizedName + "/streams?lakeId=" + id;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#lake_list_table")) {
            e.preventDefault();

            var id = $('#lake_list_table').DataTable().row($('tr.selected')).data()["id"];
            deleteLake(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_add').click(function (e) {
        e.preventDefault();
        $('#lake_form').show();
        $('#lakeCode').focus();
        $('#lakeCode').prop("readonly", false);
        $('#lake_id').attr("value", "#");
    });

    $('#button_reset').click(function (e) {
        //$('#edit_form')[0].reset();
        e.preventDefault();
        resetForm();
        $('#lake_form').show();
    });

    $('#button_cancel').click(function (e) {
        e.preventDefault();
        resetForm();
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();
        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;
            if ($('#lake_id').val() != "#" && $.trim($('#lake_id').val()).length > 0) {
                actionType = "PUT";
                actionUrl = getBaseString() + localizedName + "/api/lakes/" + $('#lake_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName + "/api/lakes/add"
            }
            var formData = getFormData("#edit_form");

            $.ajax({
                type: actionType,
                contentType: "application/json",
                url: actionUrl,
                data: JSON.stringify(formData),
                dataType: 'json',
                timeout: 6000,
                success: function (response) {
                    $("#displayInfoMessage").html("");
                    showSuccessMessage(response.message, null, "displayInfoMessage");
                    resetForm();
                    $('#lake_list_table').DataTable().ajax.reload();
                },
                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayInfoMessage").html("");
                    showErrorMessage(err.message, null, "displayInfoMessage");
                }
            });
        }
    });

    function editLake(id) {
        $('#lake_form').show();
        $('#nameEn').focus();
        //var localizedName = getLocale();
        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/lakes/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $('#lakeCode').val(response.data.lakeCode);
                $('#lakeCode').prop("readonly", true);
                $('#nameEn').val(response.data.nameEn);
                $('#nameFr').val(response.data.nameFr);
                $('#lake_id').attr("value", id);
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function deleteLake(id) {
        //var localizedName = getLocale();

        $.ajax({
            type: "DELETE",
            url: getBaseString() + localizedName + "/api/lakes/delete/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetForm();
                $('#lake_list_table').DataTable().row('.selected').remove().draw(false);
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function resetForm() {
        $('#lake_form').hide();
        $('#errors-edit_form').hide();
        $('#edit_form')[0].reset();
        $("#edit_form").validate().resetForm();
        form_original_data = $("#edit_form").serialize();
    }
});

function loadLakeList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#lake_list_table')) {
        $('#lake_list_table').DataTable().destroy();
    }
    $('#lake_list_table tbody').empty();

    var table = $('#lake_list_table').DataTable({
        //"data": lakeList,
        "sAjaxSource": getBaseString() + localizedName + "/api/lakes",
        "sAjaxDataProp": "",
        "order": [[1, "asc"]],
        "deferRender": true,
        // "stateSave": true,
        "aoColumns": [{
            "mData": "lakeCode"
        }, {
            "mData": "nameEn"
        }, {
            "mData": "nameFr"
        }, {}],
        "columnDefs": [{
            data: "id",
            "targets": 3,
            "searchable": false,
            "orderable": false,
            "visible": false,
            "render": function (data, type, full, meta) {
                return '<input type="hidden" value="' + data + '"/>';
            }
        }],
        "language": {
            "url": getLanguageURL()
        }
    });
    return table;
}