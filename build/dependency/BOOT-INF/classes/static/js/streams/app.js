$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    var table = loadLakeStreamsList();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");
    singleTableRowOnSelect('#stream_list_table');
    form_original_data = $("#edit_form").serialize();

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#stream_list_table")) {
            e.preventDefault();
            var id = $('#stream_list_table').DataTable().row($('tr.selected')).data()["id"];
            editstream(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_branchlentic').click(function (e) {
        e.preventDefault();
        if (thereAreRowsSelected("#stream_list_table")) {
            var id = $('#stream_list_table').DataTable().row($('tr.selected')).data()["id"];
            var lakeId = $('#referlake_id').val();
            window.location.href = getBaseString() + localizedName + "/branchlentics?streamId=" + id + "&lakeId=" + lakeId;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_reach').click(function (e) {
        e.preventDefault();
        if (thereAreRowsSelected("#stream_list_table")) {
            var id = $('#stream_list_table').DataTable().row($('tr.selected')).data()["id"];
            var lakeId = $('#referlake_id').val();
            window.location.href = getBaseString() + localizedName + "/reaches?streamId=" + id + "&lakeId=" + lakeId;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#stream_list_table")) {
            e.preventDefault();
            var id = $('#stream_list_table').DataTable().row($('tr.selected')).data()["id"];
            deletestream(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_add').click(function () {
        $('#stream_form').show();
        $('#streamCode').focus();
        $('#streamCode').prop("readonly", false);
        $('#stream_id').attr("value", "#");
    });

    $('#button_reset').click(function (event) {
        //$('#edit_form')[0].reset();
        resetForm();
        $('#stream_form').show();
    });

    $('#button_cancel').click(function (event) {
        resetForm();
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;

            if ($('#stream_id').val() != "#"
                && $.trim($('#stream_id').val()).length > 0) {
                actionType = "PUT";
                actionUrl = getBaseString() + localizedName + "/api/streams/" + $('#stream_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName + "/api/streams/add/" + $('#referlake_id').val();
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
                    $('#stream_list_table').DataTable().ajax.reload();
                },
                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayInfoMessage").html("");
                    showErrorMessage(err.message, null, "displayInfoMessage");
                }
            });
        }
    });

    function editstream(id) {
        $('#stream_form').show();
        $('#nameEn').focus();
        //var localizedName = getLocale();

        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/streams/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $('#streamCode').val(response.data.streamCode);
                $('#streamCode').prop("readonly", true);
                $('#nameEn').val(response.data.nameEn);
                $('#nameFr').val(response.data.nameFr);
                $('#stream_id').attr("value", id)
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");

            }
        });
    }

    function deletestream(id) {
        //var localizedName = getLocale();
        $.ajax({
            type: "DELETE",
            url: getBaseString() + localizedName + "/api/streams/delete/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetForm();
                // $('#stream_list_table').DataTable().ajax.reload();
                $('#stream_list_table').DataTable().row('.selected').remove().draw(
                    false);
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function resetForm() {
        $('#stream_form').hide();
        $('#errors-edit_form').hide();
        $('#edit_form')[0].reset();
        $("#edit_form").validate().resetForm();
        form_original_data = $("#edit_form").serialize();
    }
});

function loadLakeStreamsList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#stream_list_table')) {
        $('#stream_list_table').DataTable().destroy();
    }
    $('#stream_list_table tbody').empty();

    var table = $('#stream_list_table').DataTable({
        //"data": streamList,
        "sAjaxSource": getBaseString() + localizedName + "/api/streams/lake/" + $('#referlake_id').val(),
        "sAjaxDataProp": "",
        "order": [[0, "asc"]],
        "deferRender": true,
        //"stateSave": true,
        "aoColumns": [{
            "mData": "streamCode"
        }, {
            "mData": "nameEn"
        }, {
            "mData": "nameFr"
        }
        ],
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