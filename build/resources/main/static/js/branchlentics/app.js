$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    var table = loadStreamBranchlenticsList();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");
    singleTableRowOnSelect('#branchlentic_list_table');
    form_original_data = $("#edit_form").serialize();

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#branchlentic_list_table")) {
            e.preventDefault();
            var id = $('#branchlentic_list_table').DataTable().row($('tr.selected')).data()["id"];
            editbranchlentic(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_station').click(function (e) {
        e.preventDefault();
        if (thereAreRowsSelected("#branchlentic_list_table")) {
            var id = $('#branchlentic_list_table').DataTable().row($('tr.selected')).data()["id"];
            var lakeId = $('#referlake_id').val();
            var streamId = $('#referstream_id').val();
            window.location.href = getBaseString() + localizedName + "/stations?branchLenticId=" + id + "&streamId=" + streamId + "&lakeId=" + lakeId;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#branchlentic_list_table")) {
            e.preventDefault();
            var id = $('#branchlentic_list_table').DataTable().row($('tr.selected')).data()["id"];
            deletebranchlentic(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_add').click(function () {
        $('#branchlentic_form').show();
        $('#branchLenticCode').focus();
        $('#branchLenticCode').prop("readonly", false);
        $('#branchlentic_id').attr("value", "#");
    });

    $('#button_reset').click(function (event) {
        //$('#edit_form')[0].reset();
        resetForm();
        $('#branchlentic_form').show();
    });

    $('#button_cancel').click(function (event) {
        resetForm();
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;
            if ($('#branchlentic_id').val() != "#") {
                actionType = "PUT";
                actionUrl = getBaseString() + localizedName + "/api/branchLentics/" + $('#branchlentic_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName + "/api/branchLentics/add/" + $('#referstream_id').val();
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
                    $('#branchlentic_list_table').DataTable().ajax.reload();
                    $('#button_station').prop('disabled', false);
                },
                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayInfoMessage").html("");
                    showErrorMessage(err.message, null, "displayInfoMessage");
                }
            });
        }
    });

    function editbranchlentic(id) {
        $('#branchlentic_form').show();
        $('#nameEn').focus();
        //var localizedName = getLocale();

        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/branchLentics/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $('#branchLenticCode').val(response.data.branchLenticCode);
                $('#branchLenticCode').prop("readonly", true);
                $('#nameEn').val(response.data.nameEn);
                $('#nameFr').val(response.data.nameFr);
                $('#description').val(response.data.description);
                $('#branchlentic_id').attr("value", id);
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function deletebranchlentic(id) {
        //var localizedName = getLocale();

        $.ajax({
            type: "DELETE",
            url: getBaseString() + localizedName + "/api/branchLentics/delete/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetForm();
                $('#branchlentic_list_table').DataTable().row('.selected').remove().draw(false);
                if (branchListIsEmpty()) {
                    $('#button_station').prop('disabled', true);
                }
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function resetForm() {
        $('#branchlentic_form').hide();
        $('#errors-edit_form').hide();
        $('#edit_form')[0].reset();
        $("#edit_form").validate().resetForm();
        form_original_data = $("#edit_form").serialize();
    }
});

function loadStreamBranchlenticsList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#branchlentic_list_table')) {
        $('#branchlentic_list_table').DataTable().destroy();
    }
    $('#branchlentic_list_table tbody').empty();

    var table = $('#branchlentic_list_table').DataTable({
        //"data": branchLenticList,
        "sAjaxSource": getBaseString() + localizedName + "/api/branchLentics/stream/" + $('#referstream_id').val(),
        "sAjaxDataProp": "",
        "order": [[0, "asc"]],
        "deferRender": true,
        //"stateSave": true,
        "aoColumns": [{
            "mData": "branchLenticCode"
        }, {
            "mData": "nameEn"
        }, {
            "mData": "nameFr"
        }, {
            "mData": "description"
        }, {}],
        "columnDefs": [{
            data: "id",
            "targets": 4,
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
    /*if(branchLenticList !== null && branchLenticList.length>0)
    	{
    	   $('#button_station').prop('disabled', false);
    	}*/
    return table;
}

function branchListIsEmpty() {
    if ($('#branchlentic_list_table').find('> tbody > tr').length === 0)
        return true;

    else if ($('#branchlentic_list_table').find('> tbody > tr').length === 1) {
        if ($('#branchlentic_list_table').find('> tbody > tr').eq(0).find('td').length === 1)
            return true;
    }

    return false;
}