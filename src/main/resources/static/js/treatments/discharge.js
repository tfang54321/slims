$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadDischarges();
    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    var reloading = sessionStorage.getItem("successMessage");
    if(reloading){
        sessionStorage.removeItem("successMessage");
        showSuccessMessage(reloading, null, "displayMessage");
    }

    $('#button_save').on("click", function (e) {
        e.preventDefault();

        var needValidate = true;

        if (!isOneEmptyRow()) {
            $('#edit_form').validate();
        } else {
            needValidate = false;
        }

        if ((needValidate && $('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) || (!needValidate && formIsChanged('#edit_form', form_original_data))) {
            var actionType, actionUrl;

            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/treatments/discharge/" + theTR.id;

            var formData = getFormData("#edit_form");
            formData.numOfDischarges = $('#discharge_table').children('tbody').children().not(".clickable").length;
            var valid = getBranchAndStationIds(formData);

            if(!valid){
                return false;
            }

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    sessionStorage.setItem("successMessage", response.message);
                    window.location.reload();
                    $("#displayMessage").html("");
                    showSuccessMessage(response.message, null, "displayMessage");
                    $('#button_continue').prop('disabled', false);
                    setForm();
                    form_original_data = $("#edit_form").serialize();
                },
                error: function (response) {
                    var err = JSON.parse(response.responseText);
                    $("#displayMessage").html("");
                    showErrorMessage(err.message, null, "displayMessage");
                }
            });
        }
    });

    $('#button_exit').on("click", function (e) {
        e.preventDefault();

        var redirectUrl = getBaseString() + localizedName + "/treatments";

        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_continue').click(function (e) {
        e.preventDefault();

        var redirectUrl = getBaseString() + localizedName + "/treatments/analysis/chemanalysis?trId=" + theTR.id;

        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#discharge_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();

        $(this).closest("tr").next().remove();
        $(this).closest("tr").remove();

        setForm();

        var allRows = $('#discharge_table').children('tbody').children().not(".clickable");
        if (allRows.length === 0) {
            addRow();
        } else {
            var lastRow = $('#discharge_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length === 0) {
                lastRow.children('td').eq(6).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    $('#discharge_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
    });

    $('#discharge_table').on('click', 'span.glyphicon-expand', function (e) {
        e.preventDefault();

        $(this).closest("tr").find('td').eq(4).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand');
        $(this).closest("tr").next().toggle();
    });

    $('#discharge_table').on('click', 'span.glyphicon-collapse-down', function (e) {
        e.preventDefault();

        $(this).closest("tr").find('td').eq(4).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down');
        $(this).closest("tr").next().toggle();
    });
});

function assignNamesToTableRow() {
    var branchName = "trBranch";
    var branchListName = "allbranches";

    var stationName = "trStation";
    var stationListName = "allStations";
    var stationAdjustName = "stationAdjust";

    var sampleDateName = "sampleDate";
    var dischargeIdName = "dischargeId";

    var allDischargeRows = $('#discharge_table').children('tbody').children().not(".clickable");

    allDischargeRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(1).find("label").attr("for", branchName + String(i));
        tds.eq(1).find("input").first().attr("name", branchName + String(i)).attr("id", branchName + String(i)).attr("list", branchListName + String(i));
        tds.eq(1).find("datalist").attr("id", branchListName + String(i));

        tds.eq(2).find("label").attr("for", stationName + String(i));
        tds.eq(2).find("input").first().attr("name", stationName + String(i)).attr("id", stationName + String(i)).attr("list", stationListName + String(i));
        tds.eq(2).find("datalist").attr("id", stationListName + String(i));
        tds.eq(2).find("select").attr("name", stationAdjustName + String(i)).attr("id", stationAdjustName + String(i));

        tds.eq(3).find("label").attr("for", sampleDateName + String(i));
        tds.eq(3).find("input").attr("name", sampleDateName + String(i)).attr("id", sampleDateName + String(i));
        tds.eq(7).find("input").attr("name", dischargeIdName + String(i));

        $(this).next().find('input[type!="hidden"], select, label').each(function () {
            var currentName, namePrefix;

            if ($(this)[0].tagName.toLowerCase() === 'label' && $(this)[0].hasAttribute('for')) {
                currentName = $(this).attr("for");
                if (currentName !== undefined && currentName !== null) {
                    namePrefix = currentName.substring(0, currentName.indexOf("_") + 1);
                    $(this).attr("for", namePrefix + String(i));
                }
            } else {
                currentName = $(this).attr("name");
                if (currentName !== undefined && currentName !== null) {
                    namePrefix = currentName.substring(0, currentName.indexOf("_") + 1);
                    $(this).attr("name", namePrefix + String(i)).attr("id", namePrefix + String(i));
                    /*if (namePrefix === "dischargeCode_") {
                        $(this).attr("id", namePrefix + String(i));
                    }
                    if (namePrefix === "flowTimeCode_") {
                        $(this).attr("id", namePrefix + String(i));
                    }*/
                }
            }
        });
    });
}

function addRow() {
    var discharge = $.getMessage("i18n.discharge_title");
    var dischargeCode = $.getMessage("i18n.discharge_code_title");
    var flowTiming = $.getMessage("i18n.flow_timing_title");
    var stationTo = $.getMessage("i18n.stationto_caption");
    var elapsedTime = $.getMessage("i18n.elapsetime_title");
    var cumulativeTime = $.getMessage("i18n.comulativetime_title");
    var flowTimeCode = $.getMessage("i18n.flowtime_code_title");

    var strHTML = '';
    var rowLength = $('#discharge_table >tbody >tr').length + 1;

    var cell_00 = '<tr><td class="text-center"><input type="text" class="input-sm" value="" name="#" readonly="true" size="1" /></td>';
    var cell_01 = '<td class="text-center"><label for="#"></label><input type="text" id="trbranch0' + rowLength + '" name="trbranch0' + rowLength + '" class="input-sm branchlist" required="required" list="allbranches0' + rowLength + '" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/><datalist id="allbranches0' + rowLength + '"></datalist></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" id="trStation0' + rowLength + '" name="trStation0' + rowLength + '" class="input-sm" list="allStations0' + rowLength + '" required="required" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)" /> <datalist id="allStations0' + rowLength + '"></datalist> <select class=" input-sm fas" id="stationAdjust0' + rowLength + '" name="stationAdjust0' + rowLength + '"><option class="fas fa-equals" value="equal">&#xf52c;</option><option class="fas fa-minus" value="minus">&#xf068;</option><option class="fas fa-plus" value="plus">&#xf067;</option></select></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="date" class="input-sm appdate" name="#" required="required" /></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_06 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_07 = '<td><input type="hidden" value=""/></td></tr>'

    var cell_08 = '<tr class="clickable" hidden><td colspan="4"><div class="col-md-12 col-sm-offset-1">';
    var cell_09 = '<div class="row col-md-6"><label for="discharge_">' + discharge + '</label><br/>';
    var cell_10 = '<input type="text" class="input-sm" size="4" name="discharge_" placeholder="" min="0" max="99999.999" data-rule-number="true" maxlength="9" /><label>&nbsp;m3/s</label></div>';
    var cell_11 = '<div class="row col-md-6"><label>' + dischargeCode + '</label><br/><select class="input-sm" name="dischargeCode_" id="dischargeCode0' + rowLength + '"><option value=""></option></select></div>';
    var cell_12 = '<div class="row col-md-12"><h5 class="remove_margin" style="color: #6A5ACD">' + flowTiming + '</h5></div>';
    var cell_13 = '<div class="col-md-12"><div class="row col-md-6"><label>' + stationTo + '</label><br/><input type="text" id="trStationTo0' + rowLength + '" name="trStationTo_' + rowLength + '" class="input-sm" list="allToStations0' + rowLength + '" required="required" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/> <datalist id="allToStations0' + rowLength + '"></datalist><select class=" input-sm fas" id="stationToAdjust0' + rowLength + '" name="stationToAdjust_' + rowLength + '"><option class="fas fa-equals" value="equal">&#xf52c;</option><option class="fas fa-minus" value="minus">&#xf068;</option><option class="fas fa-plus" value="plus">&#xf067;</option></select></div>';
    var cell_14 = '<div class="row col-md-6"><label>' + flowTimeCode + '</label><br/><select class="input-sm" name="flowTimeCode_" id="flowTimeCode0' + rowLength + '"><option value=""></option></select></div></div>';
    var cell_15 = '<div class="col-md-12"><div class="row col-md-6"><label for="elapsedTime_">' + elapsedTime + '</label><br/><input type="text" class="input-sm elapse-time" name="elapsedTime_" placeholder="hh:mm" maxlength="5" minlength="5" pattern="^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$" /><label>&nbsp;hours:minutes</label></div>';
    var cell_16 = '<div class="row col-md-6"><label for="cumulativeTime_">' + cumulativeTime + '</label><br/><input type="text" class="input-sm cumulative-time" name="cumulativeTime_" placeholder="hhh:mm" maxlength="6" pattern="^([0-9]|[0-9][0-9]|[0-9][0-9][0-9]):[0-5][0-9]$" /><label>&nbsp;hours:minutes</label></div></div></div></td></tr>';

    strHTML = cell_00 + cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10 + cell_11 + cell_12 + cell_13 + cell_14 + cell_15 + cell_16;

    $('#discharge_table > tbody:last').append($.parseHTML(strHTML));

    var branchList = "#allbranches0" + rowLength;
    var dischargeCodeId = "#dischargeCode0" + rowLength;
    var flowTimeCodeId = "#flowTimeCode0" + rowLength;

    $.each(thebranches, function () {
        $(branchList).append('<option value="' + this.showText + '"></input></option>');
        $(branchList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $.each(dischargeCodeList, function () {
        $(dischargeCodeId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(flowTimeCodeList, function () {
        $(flowTimeCodeId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    var allRows = $('#discharge_table').children('tbody').children().not(".clickable");
    if (allRows.length > 1) {
        $('#discharge_table').children('tbody').children().not(".clickable").eq(allRows.length - 2).find('td').eq(6).html('');
    }
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    resetIndexToOuterTableRow('#discharge_table', ".clickable");
    addActionListener();
    addValidationListener('#edit_form');
    $('input[type="date"]').prop('max', getCurrentDateStr());

    $.applyAwesomplete("#discharge_table");
}

function setStationDropdown(stationlistObj, branchId, stationObj) {
    stationlistObj.empty();
    var stationList = allthestations[branchId];
    $.each(stationList, function () {
        stationlistObj.append('<option value="' + this.showText + '"></input></option>');
        stationlistObj.children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $(stationObj).attr('list', $(stationObj).attr('data-list'));
    $.applyAwesomplete("#discharge_table");
}

function addActionListener() {
    $('.branchlist').on("awesomplete-selectcomplete", function (e) {
        $(this).closest('td').next().find('input').val('');
        $(this).closest('td').next().find('datalist').empty();

        var inputText = $(this).val();
        var branchlistObj = $(this).closest('td').find('datalist');
        var branchId = findObjectId(branchlistObj, inputText);

        var stationlistObj = $(this).closest('td').next().find('datalist');
        var stationObj = $(this).closest('td').next().find('input');
        setStationDropdown(stationlistObj, branchId, stationObj);

        var stationTolistObj = $(this).closest('tr').next().find('datalist').first();
        var stationToObj = $(this).closest('tr').next().find('input')[1];
        setStationDropdown(stationTolistObj, branchId, stationToObj);
    });

    $('.elapse-time').keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this);
    });

    $('.cumulative-time').keyup(function () {
        $this = $(this);
        setTimeValueThreeColonTwo($this);
    });

    $('.appdate').focusout(function () {
        validateInputDateVsTrStartEndDates($(this).val());
    });
}

function getBranchAndStationIds(theform) {
    var branchId = "branchId";
    var stationId = "stationId";
    var stationToId = "stationToId_";
    var allRows = $('#discharge_table').children('tbody').children().not(".clickable");
    var valid = true;

    var i = 0;
    allRows.each(function () {
        var tds = $(this).find('td');

        var rowBranch = tds.eq(1).find('input').val();
        var rowStation = tds.eq(2).find('input').val();

        if (rowBranch != null && $.trim(rowBranch).length > 0 && rowStation != null && $.trim(rowStation).length > 0) {
            var branchlistObj = tds.eq(1).find('datalist');
            var stationlistObj = tds.eq(2).find('datalist');

            var brId = findObjectId(branchlistObj, rowBranch);
            var sfId = findObjectId(stationlistObj, rowStation);

            if(brId !== undefined && sfId !== undefined) {
                theform[branchId + String(i)] = brId;
                theform[stationId + String(i)] = sfId;
            }
            else {
                showErrorMessage("Discharge " + (i +1) + ": " + $.getMessage("i18n.select_value_datalist_dc"), 10, "displayMessage");
                valid = false;
            }

            var allDetailFields = $(this).next().find('input, select');
            if (allDetailFields.eq(2).val() !== null && $.trim(allDetailFields.eq(2).val()).length > 0) {
                var stationToListObj = $(this).next().find('datalist');
                var stId = findObjectId(stationToListObj, allDetailFields.eq(2).val());

                if(stId != undefined) {
                    theform[stationToId + String(i)] = stId;
                }
                else {
                    showErrorMessage("Discharge " + (i +1) + ": " + $.getMessage("i18n.select_value_datalist_st"), 10, "displayMessage");
                    valid = false;
                }

            }
        }
        ++i;
    });
    return valid;
}

function loadDischarges() {
    if (theTR.trDischarges.length > 0) {
        $.each(theTR.trDischarges, function (i) {
            if ($('#discharge_table').children('tbody').children().not(".clickable").length !== (i + 1)) {
                addRow();
            }

            var lastRow = $('#discharge_table').children('tbody').children().not(".clickable").last();
            var tds = lastRow.find('td');

            tds.eq(0).find('input').val(i + 1);
            tds.eq(1).find('input').first().val(this.branchLentic.showText);
            setStationDropdown(tds.eq(2).find('datalist'), this.branchLentic.id, tds.eq(2).find('input'));
            tds.eq(2).find('input').first().val(this.stationFrom.showText);
            tds.eq(2).find("select").val(this.stationFromAdjust);
            tds.eq(3).find('input').val(this.analysisDate);
            tds.eq(7).find("input").val(this.id);

            var detailRow = lastRow.next();

            var allFields = detailRow.find('input[type!="hidden"], select');

            allFields.eq(0).val(this.discharge);
            if (this.dischargeCode !== null)
                allFields.eq(1).val(this.dischargeCode.codePair.codeName);
            if (this.stationTo !== null) {
                setStationDropdown(detailRow.find('datalist').first(), this.branchLentic.id, detailRow.find('input')[1]);
                allFields.eq(2).val(this.stationTo.showText);
            }
            if (this.stationToAdjust !== null)
                allFields.eq(3).val(this.stationToAdjust);
            if (this.flowTimeCode !== null)
                allFields.eq(4).val(this.flowTimeCode.codePair.codeName);
            allFields.eq(5).val(this.elapsedTime);
            allFields.eq(6).val(this.cumulativeTime);

        });
        $('#button_continue').prop('disabled', false);
    }
}

function isOneEmptyRow() {
    var allRows = $('#discharge_table').children('tbody').children().not(".clickable");
    if (allRows.length === 1) {
        var tds = allRows.eq(0).find('td');
        if ((tds.eq(1).find('input').val() === undefined || $.trim(tds.eq(1).find('input').val()).length === 0) && (tds.eq(2).find('input').val() === undefined || $.trim(tds.eq(2).find('input').val()).length == 0))
            return true;
    }
    return false;
}
