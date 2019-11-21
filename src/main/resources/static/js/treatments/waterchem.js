$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadWaterChems();
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
            actionUrl = getBaseString() + localizedName + "/api/treatments/waterchem/" + theTR.id;

            var formData = getFormData("#edit_form");
            formData.numOfWaterChems = $('#waterchem_table').children('tbody').children().not(".clickable").length;
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
        var redirectUrl = getBaseString() + localizedName + "/treatments/analysis/discharge?trId=" + theTR.id;
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#waterchem_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").next().remove();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#waterchem_table').children('tbody').children().not(".clickable");
        if (allRows.length == 0) {
            addRow();
        } else {
            var lastRow = $('#waterchem_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                lastRow.children('td').eq(7).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    $('#waterchem_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
        //addValidationListener('#edit_form');
    });

    $('#waterchem_table').on('click', 'span.glyphicon-expand', function (e) {
        e.preventDefault();
        $(this).closest("tr").find('td').eq(5).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand');
        $(this).closest("tr").next().toggle();
    });

    $('#waterchem_table').on('click', 'span.glyphicon-collapse-down', function (e) {
        e.preventDefault();
        $(this).closest("tr").find('td').eq(5).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down');
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
    var sampleTimeName = "sampleTime";
    var waterChemIdName = "waterChemId";

    var allWaterChemRows = $('#waterchem_table').children('tbody').children().not(".clickable");

    allWaterChemRows.each(function (i) {
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
        tds.eq(4).find("label").attr("for", sampleTimeName + String(i));
        tds.eq(4).find("input").attr("name", sampleTimeName + String(i)).attr("id", sampleTimeName + String(i));
        tds.eq(8).find("input").attr("name", waterChemIdName + String(i));

        $(this).next().find('input, select, label').each(function () {
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
                }
            }
        });
    });
}

function addRow() {
    var stream_temp = $.getMessage("i18n.stream_caption_temp_title");
    var tfmcon = $.getMessage("i18n.tfmcon_title");
    var i18n_no = $.getMessage("i18n.no_title");
    var i18n_yes = $.getMessage("i18n.yes_title");
    var dis_oxy = $.getMessage("i18n.disolve_oxygen_title");
    var ammonia = $.getMessage("i18n.ammonia_title");
    var alka_ph = $.getMessage("i18n.alkalinity_ph_title");
    var pred_chart = $.getMessage("i18n.predchart_title");
    var ph_mlc = $.getMessage("i18n.ph_mlc_title");

    var strHTML = '';
    var rowLength = $('#waterchem_table >tbody >tr').length + 1;

    var cell_00 = '<tr><td class="text-center"><input type="text" class="input-sm" value="" name="#" readonly="true" size="1" /></td>';
    var cell_01 = '<td class="text-center"><label for="#"></label><input type="text" id="trbranch0' + rowLength + '" name="trbranch0' + rowLength + '" class="input-sm branchlist" required="required" list="allbranches0' + rowLength + ' " data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/><datalist id="allbranches0' + rowLength + '"></datalist></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" id="trStation0' + rowLength + '" name="trStation0' + rowLength + '" class="input-sm" list="allStations0' + rowLength + '" required="required" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)" /> <datalist id="allStations0' + rowLength + '"></datalist> <select class=" input-sm fas" id="stationAdjust0' + rowLength + '" name="stationAdjust0' + rowLength + '"><option class="fas fa-equals" value="equal">&#xf52c;</option><option class="fas fa-minus" value="minus">&#xf068;</option><option class="fas fa-plus" value="plus">&#xf067;</option></select></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="date" class="input-sm appdate" name="#" required="required" /></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm sample-time" name="#" maxlength="5" minlength="5" placeholder="HH:mm(24h)" required="required" th:attr="data-msg-pattern=#{i18n.time_format_24h}" /></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_06 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td><input type="hidden" value=""/></td></tr>';

    var cell_09 = '<tr class="clickable" hidden><td colspan="5"><div class="col-md-12 col-sm-offset-1">';
    var cell_10 = '<div class="row col-md-6"><label for="streamTemp_">' + stream_temp + '</label><br/>';
    var cell_11 = '<input type="text" class="input-sm" size="4" name="streamTemp_" placeholder="" min="-99" max="99" data-rule-number="true" maxlength="3" pattern="^-?[0-9]{0,2}$"/><label>&nbsp;&#8451;</label></div>';
    var cell_12 = '<div class="row col-md-6"><label>' + tfmcon + '>0.1 mg/L</label><br/>';
    var cell_13 = '<div class="radio-inline"><label class="radio-inline"><input type="radio" name="optradioTfmcon_' + rowLength + '" value="no" checked="checked"/>' + i18n_no + '</label>';
    var cell_14 = '<label class="radio-inline"> <input type="radio" name="optradioTfmcon_' + rowLength + '" value="yes"/>' + i18n_yes + '</label></div></div>';
    var cell_15 = '<div class="row col-md-6"><label for="disolOxy_">' + dis_oxy + '</label><br/><input type="text" class="input-sm" size="4" name="disolOxy_" placeholder="" min="0" max="999.9" data-rule-number="true" maxlength="5" /><label>&nbsp;mg/L</label></div>';
    var cell_16 = '<div class="row col-md-6"><label for="ammonia_">' + ammonia + '</label><br/><input type="text" class="input-sm" size="4" name="ammonia_" placeholder="" min="0" max="999.9" data-rule-number="true" maxlength="5" /><label>&nbsp;mg/L</label></div>';
    var cell_17 = '<div class="row col-md-6"><label for="ph_">pH</label><br/><input type="text" class="input-sm" size="4" name="ph_" placeholder="" min="0" max="99.99" data-rule-number="true" maxlength="5" /></div>';
    var cell_18 = '<div class="row col-md-6"><label for="alkaPh_">' + alka_ph + '</label><br/><input type="text" class="input-sm" size="4" name="alkaPh_" placeholder="" min="0" max="999.9" data-rule-number="true" maxlength="5" /><label>&nbsp;mg/L</label></div>';
    var cell_19 = '<div class="row col-md-6"><label for="predChart_">' + pred_chart + '</label><br/><select class="input-sm" name="predChart_" id="predChart0' + rowLength + '"><option value=""></option></select></div>';
    var cell_20 = '<div class="row col-md-6"><label for="phMlc_">' + ph_mlc + '</label><br/><input type="text" class="input-sm" size="4" name="phMlc_" placeholder="" min="0" max="99.9" data-rule-number="true" maxlength="4" /></div></div></td></tr>';

    strHTML = cell_00 + cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10 + cell_11 + cell_12 + cell_13 + cell_14 + cell_15 + cell_16 + cell_17 + cell_18 + cell_19 + cell_20;

    $('#waterchem_table > tbody:last').append($.parseHTML(strHTML));

    var branchList = "#allbranches0" + rowLength;
    var predChartId = "#predChart0" + rowLength;

    $.each(thebranches, function () {
        $(branchList).append('<option value="' + this.showText + '"></input></option>');
        $(branchList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $.each(predictChartList, function () {
        $(predChartId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    var allRows = $('#waterchem_table').children('tbody').children().not(".clickable");
    if (allRows.length > 1) {
        $('#waterchem_table').children('tbody').children().not(".clickable").eq(allRows.length - 2).find('td').eq(7).html('');
    }
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    resetIndexToOuterTableRow('#waterchem_table', ".clickable");
    addActionListener();
    addValidationListener('#edit_form');
    $('input[type="date"]').prop('max', getCurrentDateStr());
    $.applyAwesomplete('#waterchem_table');
}

function setStationDropdown(stationlistObj, branchId, stationObj) {
    var stationList = allthestations[branchId];
    $.each(stationList, function () {
        stationlistObj.append('<option value="' + this.showText + '"></input></option>');
        stationlistObj.children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });
    $(stationObj).attr('list', $(stationObj).attr('data-list'));
    $.applyAwesomplete("#waterchem_table");
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
    });

    $('.sample-time').keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this);
    });

    $('.appdate').focusout(function () {
        validateInputDateVsTrStartEndDates($(this).val());
    });
}

function getBranchAndStationIds(theform) {
    var branchId = "branchId";
    var stationId = "stationId";
    var allRows = $('#waterchem_table').children('tbody').children().not(".clickable");
    var valid = true;


    allRows.each(function (i) {
        var tds = $(this).find('td');
        var rowBranch = tds.eq(1).find('input').val();
        var rowStation = tds.eq(2).find('input').val();
        if (rowBranch != null && $.trim(rowBranch).length > 0 && rowStation != null && $.trim(rowStation).length > 0) {

            var branchlistObj = tds.eq(1).find('datalist');
            var stationlistObj = tds.eq(2).find('datalist');
            var brId = findObjectId(branchlistObj, rowBranch);
            var sId = findObjectId(stationlistObj, rowStation);
            if(brId !== undefined && sId !== undefined) {
                theform[branchId + String(i)] = brId;
                theform[stationId + String(i)] = sId;
            }
            else {
                showErrorMessage("Water Chemistry " + (i +1) + ": " + $.getMessage("i18n.select_value_datalist_dc"), 10, "displayMessage");
                valid = false;
            }
        }
    });
    return valid;
}

function loadWaterChems() {
    if (theTR.trWaterChemistries.length > 0) {
        $.each(theTR.trWaterChemistries, function (i) {
            if ($('#waterchem_table').children('tbody').children().not(".clickable").length !== (i + 1)) {
                addRow();
            }

            var lastRow = $('#waterchem_table').children('tbody').children().not(".clickable").last();
            var tds = lastRow.find('td');

            tds.eq(0).find('input').val(i + 1);
            tds.eq(1).find('input').first().val(this.branchLentic.showText);
            setStationDropdown(tds.eq(2).find('datalist'), this.branchLentic.id, tds.eq(2).find('input'));
            tds.eq(2).find('input').first().val(this.station.showText);
            tds.eq(2).find("select").val(this.stationAdjust);
            tds.eq(3).find('input').val(this.analysisDate);
            tds.eq(4).find('input').val(this.timeOfSample);
            tds.eq(8).find("input").val(this.id);

            var detailRow = lastRow.next();

            var allFields = detailRow.find('input, select');
            allFields.eq(0).val(this.streamTemp);
            if (this.tfmConcenGTPointOnePerc)
                allFields.eq(2).prop('checked', true);
            else
                allFields.eq(1).prop('checked', true);
            allFields.eq(3).val(this.dissolvedOxygen);
            allFields.eq(4).val(this.ammonia);
            allFields.eq(5).val(this.phValue);
            allFields.eq(6).val(this.alkalinityPh);
            if (this.predictChart !== null)
                allFields.eq(7).val(this.predictChart.codePair.codeName);
            allFields.eq(8).val(this.phMinLethalConcentration);
        });
        $('#button_continue').prop('disabled', false);
    }
}

function isOneEmptyRow() {
    var allRows = $('#waterchem_table').children('tbody').children().not(".clickable");
    if (allRows.length === 1) {
        var tds = allRows.eq(0).find('td');
        if ((tds.eq(1).find('input').val() === undefined || $.trim(tds.eq(1).find('input').val()).length === 0) && (tds.eq(2).find('input').val() === undefined || $.trim(tds.eq(2).find('input').val()).length == 0))
            return true;
    }
    return false;
}
