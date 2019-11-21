$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    var table = loadBranchStationsList();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");
    singleTableRowOnSelect('#station_list_table');
    form_original_data = $("#edit_form").serialize();

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#station_list_table")) {
            e.preventDefault();
            var id = $('#station_list_table').DataTable().row($('tr.selected')).data()["id"];
            editstation(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_reach').click(
        function (e) {
            e.preventDefault();
            if ($('#station_list_table').find('> tbody > tr').length > 0) {
                var lakeId = $('#referlake_id').val();
                var streamId = $('#referstream_id').val();
                window.location.href = getBaseString() + localizedName + "/stationsreaches?streamId=" + streamId + "&lakeId=" + lakeId;
            } else {
                $("#displayInfoMessage").html("");
                $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
            }
        });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#station_list_table")) {
            e.preventDefault();
            var id = $('#station_list_table').DataTable().row($('tr.selected')).data()["id"];
            deletestation(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_add').click(function () {
        $('#station_form').show();
        $('#stationCode').focus();
        $('#stationCode').prop("readonly", false);
        $('#station_id').attr("value", "#");
    });

    $('#button_reset').click(function (event) {
        resetForm();
        $('#station_form').show();
    });

    $('#button_cancel').click(function (event) {
        resetForm();
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();
        if (!validateLongDeg()) {
            $("#displayInfoMessage").html("");
            showErrorMessage($.getMessage("i18n.longitude_message"), null, "displayInfoMessage");
            return;
        }

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;
            if ($('#station_id').val() !== "#") {
                actionType = "PUT";
                actionUrl = getBaseString() + localizedName + "/api/stations/"
                    + $('#station_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName
                    + "/api/stations/add/" + $('#referbranchlentic_id').val();
            }

            var formData = getFormData("#edit_form");

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    $("#displayInfoMessage").html("");
                    showSuccessMessage(response.message, null, "displayInfoMessage");
                    resetForm();
                    $('#station_list_table').DataTable().ajax.reload();
                },
                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayInfoMessage").html("");
                    showErrorMessage(err.message, null, "displayInfoMessage");
                }
            });

        }
    });

    function editstation(id) {
        resetForm();
        $('#station_form').show();
        $('#description').focus();

        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/stations/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $('#stationCode').val(response.data.stationCode);
                $('#stationCode').prop("readonly", true);
                $('#description').val(response.data.description);
                $('#latDeg').val(response.data.latDeg);
                $('#longDeg').val(response.data.longDeg);

                if ((response.data.mapDatum !== undefined)
                    && (response.data.mapDatum !== null))
                    $('#mapDatum').val(response.data.mapDatum.codePair.codeName);
                else
                    $('#mapDatum').val("selectone");

                if ((response.data.utmZone !== undefined)
                    && (response.data.utmZone !== null))
                    $('#utmZone').val(response.data.utmZone.codePair.codeName);
                else
                    $('#utmZone').val("selectone");

                if ((response.data.utmBand !== undefined)
                    && (response.data.utmBand !== null))
                    $('#utmBand').val(response.data.utmBand.codePair.codeName);
                else
                    $('#utmBand').val("selectone");

                $('#utmEasting').val(response.data.utmEasting);
                $('#utmNorthing').val(response.data.utmNorthing);
                $('#station_id').attr("value", id)
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function deletestation(id) {
        $.ajax({
            type: "DELETE",
            url: getBaseString() + localizedName + "/api/stations/delete/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetForm();
                $('#station_list_table').DataTable().row('.selected').remove()
                    .draw(false);
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function resetForm() {
        $('#station_form').hide();
        $('#errors-edit_form').hide();
        $('#edit_form')[0].reset();
        $("#edit_form").validate().resetForm();
        form_original_data = $("#edit_form").serialize();
    }
});

function loadBranchStationsList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#station_list_table')) {
        $('#station_list_table').DataTable().destroy();
    }

    $('#station_list_table tbody').empty();

    var table = $('#station_list_table').DataTable({
        //"data" : stationList,
        "sAjaxSource": getBaseString() + localizedName + "/api/stations/branchlentic/" + $('#referbranchlentic_id').val(),
        "sAjaxDataProp": "",
        "order": [[0, "asc"]],
        "deferRender": true,
        // "stateSave": true,
        "aoColumns": [{
            "mData": "stationCode"
        }, {
            "mData": "description"
        }, {
            "mData": function (data, type, dataToSet) {
                if ((data.latDeg !== null) && (data.latDeg !== null))
                    return data.latDeg;
                else
                    return "";
            }
        }, {
            "mData": function (data, type, dataToSet) {
                if ((data.longDeg !== null) && (data.longDeg !== null))
                    return data.longDeg;
                else
                    return "";
            }
        }, {
            "mData": function (data, type, dataToSet) {
                if ((data.mapDatum !== null) && (data.mapDatum !== null))
                    return data.mapDatum.codePair.showText;
                else
                    return "";
            }
        }, {
            "mData": function (data, type, dataToSet) {
                if ((data.utmZone != null) && (data.utmBand != null))
                    return data.utmZone.codePair.valueEn + data.utmBand.codePair.valueEn;
                else if (data.utmZone != null)
                    return data.utmZone.codePair.showText;
                else if (data.utmBand != null)
                    return data.utmBand.codePair.showText;
                else
                    return "";
            }
        }, {
            "mData": function (data, type, dataToSet) {
                if (data.utmEasting !== null)
                    return data.utmEasting;
                else
                    return "";
            }
        }, {
            "mData": function (data, type, dataToSet) {
                if (data.utmNorthing !== null)
                    return data.utmNorthing;
                else
                    return "";
            }
        }, {}],
        "columnDefs": [{
            data: "id",
            "targets": 8,
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

function validateLongDeg() {
    var longDegVal = $.trim($('#longDeg').val());
    if (longDegVal.length > 0) {
        longDegVal = parseInt(longDegVal);
        if (longDegVal > 0) {
            return (longDegVal >= 74 && longDegVal <= 95);
        } else {
            return (longDegVal >= -95 && longDegVal <= -74);
        }
    }
    return true;
}