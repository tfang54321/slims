$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    var table = loadStreamReachesList();
    setForm();
    $("#loader").css("visibility", "hidden");
    singleTableRowOnSelect('#reach_list_table');

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#reach_list_table")) {
            if (!$('#edit_panel').is(":visible")) {
                e.preventDefault();
                var id = $('#reach_list_table').DataTable().row($('tr.selected')).data()["id"];
                editreach(id);
            }
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_station').click(function (e) {
        e.preventDefault();
        if ($('#reach_list_table').find('> tbody > tr').length > 0) {
            var lakeId = $('#referlake_id').val();
            var streamId = $('#referstream_id').val();
            window.location.href = getBaseString() + localizedName + "/stationsreaches?streamId=" + streamId + "&lakeId=" + lakeId;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#reach_list_table")) {
            e.preventDefault();
            var id = $('#reach_list_table').DataTable().row($('tr.selected')).data()["id"];
            deletereach(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#edit_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").remove();
        var allRows = $('#edit_table').find('> tbody > tr');
        if (allRows.length == 0) {
            addRow();
        } else {
            var lastRow = $('#edit_table').find('> tbody > tr').eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                lastRow.children('td').eq(3).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
        setForm();
    });

    $('#edit_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
    });

    $('#button_add').click(function () {
        $('#edit_panel').show();
        $('#reachCode').focus();
        $('#reach_id').attr("value", "#");
        setForm();
    });

    $('#button_reset').click(function (event) {
        //$('#edit_form')[0].reset();
        resetEditForm();
        if ($('#reach_id').val() != "#" && $.trim($('#reach_id').val()).length > 0) {
            var id = $('#reach_list_table').DataTable().row($('tr.selected')).data()["id"];
            editreach(id);
        } else
            $('#edit_panel').show();
    });

    $('#button_cancel').click(function (event) {
        resetEditForm();
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();

        $('#edit_form').validate();
        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;
            if ($('#reach_id').val() != "#" && $.trim($('#reach_id').val()).length > 0) {
                actionType = "PUT";
                actionUrl = actionUrl = getBaseString() + localizedName + "/api/reaches/" + $('#reach_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName + "/api/reaches/add/" + $('#referstream_id').val();
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
                    resetEditForm();
                    $('#reach_list_table').DataTable().ajax.reload();
                },
                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayInfoMessage").html("");
                    showErrorMessage(err.message, null, "displayInfoMessage");
                }
            });
        }
    });

    function editreach(id) {
        $('#edit_panel').show();
        $('#reachName').focus();
        //var localizedName = getLocale();

        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/reaches/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $('#reachCode').val(response.data.reachCode);
                $('#reachCode').prop("readonly", true);
                $('#reachName').val(response.data.reachName);
                if (response.data.lengthAndUpdateYears.length > 0) {
                    $.each(response.data.lengthAndUpdateYears, function (i) {
                        if ($('#edit_table').find('> tbody > tr').length != (i + 1)) {
                            addRow();
                        }
                        var lastRow = $('#edit_table').find('> tbody > tr').last();
                        var tds = lastRow.find('td');

                        tds.eq(0).find('input').val(this.reachLength);
                        tds.eq(1).find('input').val(this.updatedYear);
                    });
                }
                /*var $tbody = $('#edit_table tbody');
                $tbody.find('tr').sort(function (a, b) {
                    var tda = $(a).find('td:eq(1)').find('input').val();
                    var tdb = $(b).find('td:eq(1)').find('input').val();
                    // if a < b return 1
                    return tda < tdb ? 1
                           // else if a > b return -1
                           : tda > tdb ? -1
                           // else they are equal - return 0
                           : 0;
                }).appendTo($tbody);*/
                $('#reach_id').attr("value", id);
                setForm();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function deletereach(id) {
        //var localizedName = getLocale();
        $.ajax({
            type: "DELETE",
            url: getBaseString() + localizedName + "/api/reaches/delete/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetEditForm();
                // $('#reach_list_table').DataTable().ajax.reload();
                $('#reach_list_table').DataTable().row('.selected').remove().draw(false);
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function addRow() {

        var cell_01 = '<tr><td class="text-center"><label for="editLength"></label><input type="text" data-rule-number="true" id="editLength" class="input-sm" name="#" placeholder="" min="0" max="999999.9" maxlength="8"/></td>';
        var cell_02 = '<td class="text-center"><label for="editLengthyear"></label><input type="text" data-rule-digits="true" minlength="4" maxlength="4" id="editLengthyear" class="input-sm" name="#" placeholder=""/></td>';
        var cell_03 = '<td class="text-center"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
        var cell_04 = '<td class="text-center"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td></tr>';

        var addstr = cell_01 + cell_02 + cell_03 + cell_04;
        $('#edit_table > tbody:last').append($.parseHTML(addstr));

        var allRows = $('#edit_table').find('tbody').find('tr');

        if (allRows.length > 1) {

            $('#edit_table').find('tbody').find('tr').eq(allRows.length - 2).find('td').eq(3).html('');
        }
        setForm();
    }

    function resetEditForm() {
        $('#edit_panel').hide();
        $('#errors-edit_form').hide();
        $('#edit_form')[0].reset();
        $("#edit_form").validate().resetForm();
        $('#reachCode').prop("readonly", false);
        $("#edit_table > tbody").html("");
        addRow();
    }

    function setForm() {
        assignNameToTableRow();
        addValidationListener('#edit_form');
    }

});

function loadStreamReachesList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#reach_list_table')) {
        $('#reach_list_table').DataTable().destroy();
    }

    $('#reach_list_table tbody').empty();

    var table = $('#reach_list_table').DataTable(
        {
            //"data": reachList,
            "sAjaxSource": getBaseString() + localizedName + "/api/reaches/stream/" + $('#referstream_id').val(),
            "sAjaxDataProp": "",
            "order": [[0, "asc"]],
            "deferRender": true,
            //"stateSave": true,
            "aoColumns": [
                {
                    "mData": "reachCode"
                },
                {
                    "mData": "reachName"
                },
                {
                    "mData": function (data, type, dataToSet) {
                        if (data.lengthAndUpdateYears != null && data.lengthAndUpdateYears.length > 0)
                            return data.lengthAndUpdateYears[0].reachLength;
                        else
                            return "";
                    }
                },
                {
                    "mData": function (data, type, dataToSet) {
                        if (data.lengthAndUpdateYears != null && data.lengthAndUpdateYears.length > 0)
                            return data.lengthAndUpdateYears[0].updatedYear;
                        else
                            return "";
                    }
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
    return table;
}

function assignNameToTableRow() {
    var lengthName = "editLength";
    var yearName = "year";

    var allRows = $('#edit_table').find('> tbody > tr');

    allRows.each(function (i) {
        $(this).find('td').eq(0).find("label").attr("for", lengthName + String(i));
        $(this).find('td').eq(0).find("input").attr("name", lengthName + String(i)).attr("id", lengthName + String(i));
        $(this).find('td').eq(1).find("label").attr("for", lengthName + String(i) + yearName);
        $(this).find('td').eq(1).find("input").attr("name", lengthName + String(i) + yearName).attr("id", lengthName + String(i) + yearName);
    });
}

function getLengthAndUpdateYears() {
    var returnData = [];
    var lengthData, yearData;
    $('#edit_table tr').each(function (i, val) {
        lengthData = $(this).children("input.first").val();
        yearData = $(this).children("input.second").val();
        returnData.push(lengthData + "|" + yearData);
    });
    return returnData;
}