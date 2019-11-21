$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;

    loadAADetail();
    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        //confirmGoBack(form_original_data, $.getMessage("i18n.exit_warning_message"));
        var redirectUrl = getBaseString() + localizedName + "/adultassessments/location?aaId=" + theAA.id;
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;

            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/aas/detail/" + theAA.id;

            var formData = getFormData("#edit_form");
            formData.numOfWeekCaptures = $('#week_of_capture_table').find('> tbody > tr').length;

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    //$('#button_continue').prop('disabled', false);
                    form_original_data = $("#edit_form").serialize();
                    $("#displayMessage").html("");
                    showSuccessMessage(response.message, null, "displayMessage");
                },
                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayMessage").html("");
                    showErrorMessage(err.message, null, "displayMessage");
                }
            });
        }
    });

    $('#button_continue').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/adultassessments/species?aaId=" + theAA.id;
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#week_of_capture_table').on('click', 'span.glyphicon-plus-sign',
        function (e) {
            e.preventDefault();
            addRow();
        });

    $('#week_of_capture_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#week_of_capture_table').find('> tbody > tr');
        if (allRows.length == 0) {
            addRow();
        } else {
            var lastRow = $('#week_of_capture_table').find('> tbody > tr').eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                lastRow.children('td').eq(3).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });
});

function addRow() {
    var strHTML = '';
    var rowLength = $('#week_of_capture_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><label for="#"></label><input type="text" class="input-sm" name="#" id="myid" placeholder="" max="99" min="1" data-rule-digits="true" maxlength="2" size="4"/></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" name="#" id="myid" placeholder="" max="9999" min="0" data-rule-digits="true" maxlength="4" size="4"/></td>';
    var cell_03 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td></tr>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04;
    $('#week_of_capture_table > tbody:last').append($.parseHTML(strHTML));

    var allRows = $('#week_of_capture_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#week_of_capture_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(3).html('');
    }
    setForm();
}

function assignNamesToTableRow() {
    var tagWeekName = "tagWeek";
    var adultsCapturedName = "adultsCaptured";

    var allRows = $('#week_of_capture_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');
        tds.eq(0).find("label").attr("for", tagWeekName + String(i));
        tds.eq(0).find("input").attr("name", tagWeekName + String(i)).attr("id", tagWeekName + String(i));
        tds.eq(1).find("label").attr("for", adultsCapturedName + String(i));
        tds.eq(1).find("input").attr("name", adultsCapturedName + String(i)).attr("id", adultsCapturedName + String(i));
    });
}

function setForm() {
    assignNamesToTableRow();
    addValidationListener('#edit_form');
    addFieldListener();
}

function addFieldListener() {

    $('#marked').change(function () {
        // The expectation is to do check the following as per Gale's description
        // if the number in Marked (on the Main page) is greater than the number of 1st Time Captures,
        // Sea Lamprey, Alive (on the Species page), we could have a warning message saying:
        // "Number of sea lamprey Marked cannot be greater than sea lamprey captured on a given day"
        // or,
        // "The number of sea lamprey Marked should not be greater than number of sea lamprey captured alive on a given day"

        // do nothing for now
    });

    $('#water_temp').change(function () {
        var waterTemp = $.trim($('#water_temp').val());
        if (!isEmpty(waterTemp) && !isNaN(waterTemp)) {
            waterTemp = parseFloat(waterTemp);

            if (waterTemp < 3 && waterTemp > 25) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("aa_watertemp_message"), null, "displayWatertempErrMessage");
            }
        }
    });

    $('#upstream').change(function () {
        var upstream = $.trim($('#upstream').val());
        if (!isEmpty(upstream) && !isNaN(upstream)) {
            upstream = parseFloat(upstream);

            if (upstream < 1.1 && upstream > 2) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("aa_upstream_message"), null, "displayUpstreamErrMessage");
            }
        }
    });
    $('#downstream').change(function () {
        var downstream = $.trim($('#downstream').val());
        if (!isEmpty(downstream) && !isNaN(downstream)) {
            downstream = parseFloat(downstream);

            if (downstream < 0.1 && downstream > 1) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("aa_downstream_message"), null, "displayDownstreamErrMessage");
            }
        }
    });
}

function loadAADetail() {
    if (theAA != null) {

        if ((theAA.deviceMethod !== undefined) && (theAA.deviceMethod !== null))
            $('#methodCode').val(theAA.deviceMethod.codePair.codeName);

        if ((theAA.trapNumber !== undefined) && (theAA.trapNumber !== null))
            $('#trap_number').val(theAA.trapNumber);

        if ((theAA.opcodeInitialOrReplaced !== undefined) && (theAA.opcodeInitialOrReplaced !== null))
            $('#opcode_init').val(theAA.opcodeInitialOrReplaced.codePair.codeName);

        if ((theAA.additionalOpcode !== undefined) && (theAA.additionalOpcode !== null))
            $('#opcode_additional').val(theAA.additionalOpcode.codePair.codeName);

        if ((theAA.opcodeOnLeaving !== undefined) && (theAA.opcodeOnLeaving !== null))
            $('#opcode_leaving').val(theAA.opcodeOnLeaving.codePair.codeName);

        if ((theAA.remarks !== undefined) && (theAA.remarks !== null))
            $('#aa_remarks').val(theAA.remarks);

        if ((theAA.airTemp !== undefined) && (theAA.airTemp !== null))
            $('#air_temp').val(theAA.airTemp);

        if ((theAA.recaptured !== undefined) && (theAA.recaptured !== null))
            $('#recaptured').val(theAA.recaptured);

        if ((theAA.marked !== undefined) && (theAA.marked !== null))
            $('#marked').val(theAA.marked);

        if ((theAA.weekOfTagging !== undefined) && (theAA.weekOfTagging !== null))
            $('#week_of_tagging').val(theAA.weekOfTagging);

        if ((theAA.device !== undefined) && (theAA.device !== null))
            $('#device').val(theAA.device.codePair.codeName);

        if ((theAA.waterTemp !== undefined) && (theAA.waterTemp !== null))
            $('#water_temp').val(theAA.waterTemp);

        if ((theAA.waterTempMax !== undefined) && (theAA.waterTempMax !== null))
            $('#max_temp').val(theAA.waterTempMax);

        if ((theAA.waterTempMin !== undefined) && (theAA.waterTempMin !== null))
            $('#min_temp').val(theAA.waterTempMin);

        if (theAA.turbidity == "low")
            $('#radio_low').prop("checked", true);
        else if (theAA.turbidity == "medium")
            $('#radio_medium').prop("checked", true);
        else if (theAA.turbidity == "high")
            $('#radio_high').prop("checked", true);
        else
            $('#radio_na').prop("checked", true);

        if ((theAA.gaugeUsed !== undefined) && (theAA.gaugeUsed !== null))
            $('#gaugeUsed').val(theAA.gaugeUsed.codePair.codeName);

        if ((theAA.upStream !== undefined) && (theAA.upStream !== null))
            $('#upstream').val(theAA.upStream);

        if ((theAA.downStream !== undefined) && (theAA.downStream !== null))
            $('#downstream').val(theAA.downStream);

        if ((theAA.ifOther !== undefined) && (theAA.ifOther !== null))
            $('#ifother').val(theAA.ifOther);

        if (theAA.aaWeekOfCaptures.length > 0) {
            $.each(theAA.aaWeekOfCaptures, function (i) {

                if ($('#week_of_capture_table').find('> tbody > tr').length != (i + 1)) {
                    addRow();
                }
                var lastRow = $('#week_of_capture_table').find('>tbody > tr').last();
                var tds = lastRow.find('td');

                tds.eq(0).find('input').val(this.taggingWeek);
                tds.eq(1).find('input').first().val(this.adultCaptured);
            });
        }
        //$('#button_continue').prop('disabled', false);
    }
}
