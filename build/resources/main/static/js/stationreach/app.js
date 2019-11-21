$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    $("#loader").css("visibility", "hidden");

    $('#button_cancel').click(function (e) {
        e.preventDefault();
        window.history.back();
    });

    $("#assign_reaches_table input[type=radio]").on('change', function (e) {
        e.preventDefault();
        $('#assign_stations_table').find('> tbody > tr').each(function () {
            $(this).children("td").find(":checkbox").prop('checked', false);
        });
        var id = $(this).closest("tr").children("td").find(":hidden").val();
        tableHandler(id);
    });

    $("#radios_inline input[type=radio]").on('change', function (e) {
        e.preventDefault();
        if (thereIsRadioChecked("#assign_reaches_table")) {
            var id = $("#assign_reaches_table input[type=radio]:checked").closest("tr").children("td").find(":hidden").val();
            tableHandler(id);
        }
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        if (thereIsRadioChecked("#assign_reaches_table")) {
            var id = $("#assign_reaches_table input[type=radio]:checked").closest("tr").children("td").find(":hidden").val();
            var stationIds = [];
            // if ($("#assign_stations_table input[type=checkbox]:checked").length == 0) {
            //     $("#displayMessage").html("");
            //     $.showAlert("info", $.getMessage("select_station"), 0, "displayMessage")
            //     return;
            // }
            $("#assign_stations_table input[type=checkbox]:checked").each(function () {
                stationIds.push(parseInt($(this).closest("tr").children("td").find(":hidden").val()));
            });

            $.ajax({
                type: "PUT",
                url: getBaseString() + localizedName + "/api/stationsreaches",
                data: {
                    reachId: id,
                    stationIds: stationIds
                },
                success: function (response) {
                    $("#displayMessage").html("");
                    showSuccessMessage(response.message, null, "displayMessage");
                },

                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayMessage").html("");
                    showErrorMessage(err.message, null, "displayMessage");
                }
            });
        } else {
            $("#displayMessage").html("");
            $.showAlert("info", $.getMessage("select_station_reach"), 0, "displayMessage")
        }
    });
});

function tableHandler(id) {
    var localizedName = getLocale();

    $.ajax({
        type: "GET",
        url: getBaseString() + localizedName + "/api/reaches/" + id,
        dataType: "json",
        async: true,
        success: function (response) {
            var showRadioValue = $("#radios_inline input[type=radio]:checked").val();
            var assignedStationIds = response.data.assignedStationIds;
            var allRows = $('#assign_stations_table').find('> tbody > tr');

            switch (showRadioValue) {
                case 'all' :
                    allRows.each(function () {
                        var rowId = parseInt($(this).children("td").find(":hidden").val());
                        var rowCheckBox = $(this).children("td").find(":checkbox");
                        if ($.inArray(rowId, assignedStationIds) != -1) {
                            if (!rowCheckBox.is("checked"))
                                rowCheckBox.prop('checked', true);
                            if (!$(this).is(":visible"))
                                $(this).toggle();
                        } else {
                            if (rowCheckBox.is("checked")) {
                                rowCheckBox.prop('checked', false);
                            }
                            if (!$(this).is(":visible"))
                                $(this).toggle();
                        }
                    });
                    break;

                case 'assigned' :
                    allRows.each(function () {
                        var rowId = parseInt($(this).children("td").find(":hidden").val());
                        var rowCheckBox = $(this).children("td").find(":checkbox");
                        if ($.inArray(rowId, assignedStationIds) != -1) {
                            if (!rowCheckBox.is("checked"))
                                rowCheckBox.prop('checked', true);
                            if (!$(this).is(":visible"))
                                $(this).toggle();
                        } else {
                            if (rowCheckBox.is("checked"))
                                rowCheckBox.prop('checked', false);
                            if ($(this).is(":visible"))
                                $(this).toggle();
                        }
                    });
                    break;

                case 'unassigned' :
                    allRows.each(function () {
                        var rowId = parseInt($(this).children("td").find(":hidden").val());
                        var rowCheckBox = $(this).children("td").find(":checkbox");
                        if ($.inArray(rowId, assignedStationIds) != -1) {
                            if (!rowCheckBox.is("checked"))
                                rowCheckBox.prop('checked', true);
                            if ($(this).is(":visible"))
                                $(this).toggle();
                        } else {
                            if (rowCheckBox.is("checked"))
                                rowCheckBox.prop('checked', false);
                            if (!$(this).is(":visible"))
                                $(this).toggle();
                        }
                    });
                    break;
            }
        },
        error: function (response) {
            var err = JSON.parse(response.responseText);
            $("#displayInfoMessage").html("");
            showErrorMessage(err.message, null, "displayMessage");
        }
    });
}
