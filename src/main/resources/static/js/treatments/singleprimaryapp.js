$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadPrimarApp();
    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_save').click(function (e) {
        e.preventDefault();
        $('#edit_form').validate();
        if (!validateStartEndDate()) {
            $("#displayDateErrMessage").html("");
            showErrorMessage($.getMessage("i18n.start_end_date_message"), 10000, "displayDateErrMessage");
            return;
        } else if (!validateStartEndDateVsTrDates()) {
            $("#displayDateErrMessage").html("");
            showErrorMessage($.getMessage("tr_app_start_end_date_message"), 10000, "displayDateErrMessage");
            return;
        } else if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;

            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/treatments/singleprimaryapp/" + theTrPrimApp.id;

            var formData = getFormData("#edit_form");
            formData.numOfTFMs = $('#tfm_table').find('> tbody > tr').length;
            formData.numOfWPs = $('#wp_table').find('> tbody > tr').length;
            formData.numOfECs = $('#ec_table').find('> tbody > tr').length;

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
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

    $('#button_return_summary').click(function (e) {
        e.preventDefault();
        confirmGoBack(form_original_data, $.getMessage("i18n.exit_warning_message"));
    });

    listenToPlusEvent("#tfm_table");
    listenToPlusEvent("#wp_table");
    listenToPlusEvent("#ec_table");

    listenToMinusEvent("#tfm_table");
    listenToMinusEvent("#wp_table");
    listenToMinusEvent("#ec_table");

    $("#time_on, #time_off").keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this);
    });

    $("#trStartDate, #trEndDate, #time_on, #time_off").blur(function () {
        $('#tr_duration').val('');
        var startDateVal = $.trim($('#trStartDate').val());
        var endDateVal = $.trim($('#trEndDate').val());
        var timeOff = $.trim($("#time_off").val());
        var timeOn = $.trim($("#time_on").val());

        if (timeOff.length > 0 && (/^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/.test(timeOff))
            && timeOn.length > 0 && (/^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/.test(timeOn))) {
            $("#displayDateErrMessage").html("");
            if (!validateStartEndDate()) {
                showErrorMessage($.getMessage("i18n.start_end_date_message"), 10000, "displayDateErrMessage");
            } else if (!validateStartEndDateVsTrDates()) {
                showErrorMessage($.getMessage("tr_app_start_end_date_message"), 10000, "displayDateErrMessage");
            } else {
                $('#tr_duration').val(getFullDiffTime(startDateVal, endDateVal, timeOn, timeOff));
            }
        }
    });
});

function assignNamesToTFMTableRow() {
    var lpName = "tfmLP";
    var literName = "tfmLiterUsed";

    var allRows = $('#tfm_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("select").attr("id", lpName + String(i)).attr("name", lpName + String(i));
        tds.eq(1).find("label").attr("for", literName + String(i));
        tds.eq(1).find("input").attr("name", literName + String(i)).attr("id", literName + String(i));
    });
}

function assignNamesToWPTableRow() {
    //var lpName = "wpLP";
    var kgName = "wpKgUsed";
    var percAIName = "wpPercAI";
    var kgAIName = "wpKgAI";

    var allRows = $('#wp_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');

        //tds.eq(0).find("select").attr("id", lpName + String(i)).attr("name", lpName + String(i));
        tds.eq(0).find("label").attr("for", kgName + String(i));
        tds.eq(0).find("input").attr("name", kgName + String(i)).attr("id", kgName + String(i));
        tds.eq(1).find("select").attr("id", percAIName + String(i)).attr("name", percAIName + String(i));
        //tds.eq(1).find("input").attr("name", percAIName + String(i));
        tds.eq(2).find("input").attr("name", kgAIName + String(i));
    });
}

function assignNamesToECTableRow() {
    var literName = "ecLiterUsed";
    var percAIName = "ecPercAI";
    var kgAIName = "ecKgAI";

    var allRows = $('#ec_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("label").attr("for", literName + String(i));
        tds.eq(0).find("input").attr("name", literName + String(i)).attr("id", literName + String(i));
        tds.eq(1).find("select").attr("id", percAIName + String(i)).attr("name", percAIName + String(i));
        tds.eq(2).find("input").attr("name", kgAIName + String(i));
    });
}

function addTFMRow() {
    var rowLength = $('#tfm_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><select class="input-sm" style="width: 100%" id="tfmlist_0' + rowLength + '"><option value="">' + $.getMessage("i18n.default.select") + '</option></select></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="6" name="#" placeholder="" data-rule-number="true" min="0" max="9999999.99" maxlength="10" /></td>';
    var cell_03 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td></tr>';

    var strHTML = cell_01 + cell_02 + cell_03 + cell_04;
    $('#tfm_table > tbody:last').append($.parseHTML(strHTML));

    var lpsId = "#tfmlist_0" + rowLength;
    $.each(tfmList, function () {
        $(lpsId).append($("<option />").attr('value', this.id).text(this.showText));
    });

    var allRows = $('#tfm_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#tfm_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(3).html('');
    }
    setForm();
}

function addWPRow() {
    var rowLength = $('#wp_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="6" name="#" placeholder="" data-rule-number="true" min="0" max="9999999.99" maxlength="10" /></td>';
    var cell_02 = '<td class="text-center"><select class="input-sm" id="wppercai_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_03 = '<td class="text-center"><input type="text" class="input-sm" size="6" name="#" placeholder="" readOnly="true" /></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td></tr>';

    var strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05;
    $('#wp_table > tbody:last').append($.parseHTML(strHTML));

    var percAIId = "#wppercai_0" + rowLength;
    $.each(wpPercAIList, function () {
        $(percAIId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    var allRows = $('#wp_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#wp_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(4).html('');
    }
    setForm();
}

function addECRow() {
    var rowLength = $('#ec_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="6" name="#" placeholder="" data-rule-number="true" min="0" max="9999999.99" maxlength="10" /></td>';
    var cell_02 = '<td class="text-center"><select class="input-sm" id="ecpercai_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_03 = '<td class="text-center"><input type="text" class="input-sm" size="6" name="#" placeholder="" readOnly="true" /></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td></tr>';

    var strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05;
    $('#ec_table > tbody:last').append($.parseHTML(strHTML));

    var percAIId = "#ecpercai_0" + rowLength;
    $.each(ecPercAIList, function () {
        $(percAIId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    var allRows = $('#ec_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#ec_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(4).html('');
    }
    setForm();
}

function addRowListener() {
    $('#wp_table').find('> tbody > tr').each(function () {
        $(this).change(function () {
            var tds = $(this).find('td');

            var kgUsed = $.trim(tds.eq(0).find('input').val());
            var percAI = $.trim(tds.eq(1).find('select').find('option:selected').text());

            if (isNaN(kgUsed)) {
                kgUsed = 0;
            } else {
                kgUsed = parseFloat(kgUsed);
            }
            if (isEmpty(percAI)) {
                percAI = 0;
            } else {
                percAI = parseFloat((percAI.split("%")[0]).split(")")[1]);
            }

            tds.eq(2).find('input').val((percAI / 100 * kgUsed).toFixed(2));
        });
    });

    $('#ec_table').find('> tbody > tr').each(function () {
        $(this).change(function () {
            var tds = $(this).find('td');

            var literUsed = $.trim(tds.eq(0).find('input').val());
            var percAI = $.trim(tds.eq(1).find('select').find('option:selected').text());

            if (isNaN(literUsed)) {
                kgUsed = 0;
            } else {
                literUsed = parseFloat(literUsed);
            }
            if (isEmpty(percAI)) {
                percAI = 0;
            } else {
                percAI = parseFloat((percAI.split("%")[0]).split(")")[1]);
            }
            tds.eq(2).find('input').val((percAI / 100 * literUsed).toFixed(2));
        });
    });
}

function setForm() {
    assignNamesToTFMTableRow();
    assignNamesToWPTableRow();
    assignNamesToECTableRow();
    addRowListener();
    addValidationListener('#edit_form');
    $('input[type="date"]').prop('max', getCurrentDateStr());
}

function listenToMinusEvent(tableId) {
    if (!tableId.startsWith("#")) {
        tableId = "#" + tableId;
    }
    $(tableId).on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $(tableId).find('> tbody > tr');
        if (allRows.length === 0) {
            if (tableId === "#tfm_table")
                addTFMRow();
            else if (tableId === "#wp_table")
                addWPRow();
            else if (tableId === "#ec_table")
                addECRow();
        } else {
            var lastRow = $(tableId).find('> tbody > tr').eq(
                allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length === 0) {
                lastRow.children('td').last().html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });
}

function listenToPlusEvent(tableId) {
    if (!tableId.startsWith("#")) {
        tableId = "#" + tableId;
    }
    $(tableId).on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        if (tableId === "#tfm_table")
            addTFMRow();
        else if (tableId === "#wp_table")
            addWPRow();
        else if (tableId === "#ec_table")
            addECRow();
        addValidationListener('#edit_form');
    });
}

function validateStartEndDate() {
    var startDateVal = $.trim($('#trStartDate').val());
    var endDateVal = $.trim($('#trEndDate').val());
    var startTimeVal = $.trim($('#time_on').val());
    var finishTimeVal = $.trim($('#time_off').val());

    if (isEmpty(startDateVal) || isEmpty(endDateVal) || isEmpty(startTimeVal) || isEmpty(finishTimeVal))
        return false;

    if (compareDate(startDateVal, endDateVal) === 1) {
        return false;
    } else if (compareDate(startDateVal, endDateVal) === 0) {
        return compareTime(startTimeVal, finishTimeVal) !== 1;
    }

    return true;
}

function validateStartEndDateVsTrDates() {
    var startDateVal = $.trim($('#trStartDate').val());
    var endDateVal = $.trim($('#trEndDate').val());
    var trStartdate = theTR.trLogistics.treatmentStart;
    var trEndDate = theTR.trLogistics.treatmentEnd;

    return !(compareDate(startDateVal, trStartdate) === -1 || compareDate(endDateVal, trEndDate) === 1);
}

function loadPrimarApp() {
    if (theTrPrimApp != null) {
        if (theTrPrimApp.treatmentStartDate !== null)
            $('#trStartDate').val(theTrPrimApp.treatmentStartDate);

        if (theTrPrimApp.treatmentEndDate !== null)
            $('#trEndDate').val(theTrPrimApp.treatmentEndDate);

        if (theTrPrimApp.timeOn !== null)
            $('#time_on').val(theTrPrimApp.timeOn);

        if (theTrPrimApp.timeOff !== null)
            $('#time_off').val(theTrPrimApp.timeOff);

        if (theTrPrimApp.duration !== null)
            $('#tr_duration').val(theTrPrimApp.duration);

        if (theTrPrimApp.applicationCode !== null)
            $('#app_code').val(theTrPrimApp.applicationCode.codePair.codeName);

        if (theTrPrimApp.applicationMethod !== null)
            $('#app_method').val(theTrPrimApp.applicationMethod.codePair.codeName);

        if (theTrPrimApp.geoUTM !== null) {
            if ((theTrPrimApp.geoUTM.mapDatum !== undefined) && (theTrPrimApp.geoUTM.mapDatum !== null))
                $('#mapDatum').val(theTrPrimApp.geoUTM.mapDatum.codePair.codeName);
            else
                $('#mapDatum').val("WGS-84");

            if ((theTrPrimApp.geoUTM.utmZone !== undefined) && (theTrPrimApp.geoUTM.utmZone !== null))
                $('#utmZone').val(theTrPrimApp.geoUTM.utmZone.codePair.codeName);
            else
                $('#utmZone').val("1");

            if ((theTrPrimApp.geoUTM.utmEasting01 !== undefined) && (theTrPrimApp.geoUTM.utmEasting01 !== null))
                $('#utm_e01').val(theTrPrimApp.geoUTM.utmEasting01);
            if ((theTrPrimApp.geoUTM.utmNorthing01 !== undefined) && (theTrPrimApp.geoUTM.utmNorthing01 !== null))
                $('#utm_n01').val(theTrPrimApp.geoUTM.utmNorthing01);

            if ((theTrPrimApp.geoUTM.utmEasting02 !== undefined) && (theTrPrimApp.geoUTM.utmEasting02 !== null))
                $('#utm_e02').val(theTrPrimApp.geoUTM.utmEasting02);
            if ((theTrPrimApp.geoUTM.utmNorthing02 !== undefined) && (theTrPrimApp.geoUTM.utmNorthing02 !== null))
                $('#utm_n02').val(theTrPrimApp.geoUTM.utmNorthing02);

            if ((theTrPrimApp.geoUTM.utmEasting03 !== undefined) && (theTrPrimApp.geoUTM.utmEasting03 !== null))
                $('#utm_e03').val(theTrPrimApp.geoUTM.utmEasting03);
            if ((theTrPrimApp.geoUTM.utmNorthing03 !== undefined) && (theTrPrimApp.geoUTM.utmNorthing03 !== null))
                $('#utm_n03').val(theTrPrimApp.geoUTM.utmNorthing03);

            if ((theTrPrimApp.geoUTM.utmEasting04 !== undefined) && (theTrPrimApp.geoUTM.utmEasting04 !== null))
                $('#utm_e04').val(theTrPrimApp.geoUTM.utmEasting04);
            if ((theTrPrimApp.geoUTM.utmNorthing04 !== undefined) && (theTrPrimApp.geoUTM.utmNorthing04 !== null))
                $('#utm_n04').val(theTrPrimApp.geoUTM.utmNorthing04);

            if ((theTrPrimApp.geoUTM.location !== undefined) && (theTrPrimApp.geoUTM.location !== null))
                $('#tr_location').val(theTrPrimApp.geoUTM.location);
        }
        if (theTrPrimApp.trTFMs != null && theTrPrimApp.trTFMs.length > 0) {
            $.each(theTrPrimApp.trTFMs, function (i) {
                if ($('#tfm_table').find('> tbody > tr').length !== (i + 1)) {
                    addTFMRow();
                }
                var lastRow = $('#tfm_table').find('> tbody > tr').last();
                var tds = lastRow.find('td');

                tds.eq(0).find('select option[value="' + this.tfmLPId + '"]').prop('selected', true);
                tds.eq(1).find('input').val(this.litresUsed);
            });
        }
        if (theTrPrimApp.trWettablePowders != null && theTrPrimApp.trWettablePowders.length > 0) {
            $.each(theTrPrimApp.trWettablePowders, function (i) {
                if ($('#wp_table').find('> tbody > tr').length !== (i + 1)) {
                    addWPRow();
                }
                var lastRow = $('#wp_table').find('> tbody > tr').last();
                var tds = lastRow.find('td');

                tds.eq(0).find('input').val(this.kgUsed);
                tds.eq(1).find('select option[value="' + this.wpPercAI.codePair.codeName + '"]').prop('selected', true);
                //tds.eq(1).find('input').val(this.wpPercAI);
                tds.eq(2).find('input').val(this.wpKgAI);
            });
        }
        if (theTrPrimApp.trEmulsifiableConcentrates != null
            && theTrPrimApp.trEmulsifiableConcentrates.length > 0) {
            $.each(theTrPrimApp.trEmulsifiableConcentrates, function (i) {
                if ($('#ec_table').find('> tbody > tr').length !== (i + 1)) {
                    addECRow();
                }
                var lastRow = $('#ec_table').find('> tbody > tr').last();
                var tds = lastRow.find('td');

                tds.eq(0).find('input').val(this.litresUsed);
                //tds.eq(1).find('input').val(this.ecPercAI);
                tds.eq(1).find('select option[value="' + this.ecPercAI.codePair.codeName + '"]').prop('selected', true);
                tds.eq(2).find('input').val(this.ecKgAI);
            });
        }
    }
}
