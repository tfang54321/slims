$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadChemanalysis();
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
        if (isOneEmptyRow()) {
            needValidate = false;
        } else {
            $('#edit_form').validate();
        }

        if ((needValidate && $('#edit_form').valid() && formIsChanged('#edit_form', form_original_data))
            || (!needValidate && formIsChanged('#edit_form', form_original_data))) {
            var formData = getFormData("#edit_form");
            formData.numOfChemanalysis = $('#chemanalysis_table').children('tbody').children().not(".clickable").length;
            var valid = getBranchAndStationIds(formData);
            if (!valid){
                return false;
            }

            var actionType = "PUT";
            var actionUrl = getBaseString() + localizedName + "/api/treatments/chemanalysis/" + theTR.id;

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

        var redirectUrl = getBaseString() + localizedName + "/treatments/summary?trId=" + theTR.id;

        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#chemanalysis_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();

        $(this).closest("tr").next().remove();
        $(this).closest("tr").remove();

        setForm();

        var allRows = $('#chemanalysis_table').children('tbody').children().not(".clickable");
        if (allRows.length === 0) {
            addRow();
        } else {
            var lastRow = $('#chemanalysis_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length === 0) {
                lastRow.children('td').eq(7).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    $('#chemanalysis_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
        //addValidationListener('#edit_form');
    });

    $('#chemanalysis_table').on('click', 'span.glyphicon-expand', function (e) {
        e.preventDefault();

        $(this).closest("tr").find('td').eq(5).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand');
        $(this).closest("tr").next().toggle();

    });

    $('#chemanalysis_table').on('click', 'span.glyphicon-collapse-down', function (e) {
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
    var chemAnalysisIdName = "chemAnalysisId";

    var allChemanalyRows = $('#chemanalysis_table').children('tbody').children().not(".clickable");

    allChemanalyRows.each(function (i) {
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
        tds.eq(8).find("input").attr("name", chemAnalysisIdName + String(i));

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
    var tfmcon = $.getMessage("i18n.tfmcon_title");
    var niclosamide_con = $.getMessage("i18n.niclosamide_con_title");

    var rowLength = $('#chemanalysis_table >tbody >tr').length + 1;

    var cell_00 = '<tr><td class="text-center"><input type="text" class="input-sm" value="" name="#" readonly="true" size="1" /></td>';
    var cell_01 = '<td class="text-center"><label for="#"></label><input type="text" id="trbranch0' + rowLength + '" name="trbranch0' + rowLength + '" class="input-sm branchlist" required="required" list="allbranches0' + rowLength + '" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/><datalist id="allbranches0' + rowLength + '"></datalist></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" id="trStation0' + rowLength + '" name="trStation0' + rowLength + '" class="input-sm" list="allStations0' + rowLength + '" required="required" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/> <datalist id="allStations0' + rowLength + '"></datalist> <select class=" input-sm fas" id="stationAdjust0' + rowLength + '" name="stationAdjust0' + rowLength + '"><option class="fas fa-equals" value="equal">&#xf52c;</option><option class="fas fa-minus" value="minus">&#xf068;</option><option class="fas fa-plus" value="plus">&#xf067;</option></select></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="date" class="input-sm appdate" name="#" required="required" /></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm sample-time" name="#" maxlength="5" minlength="5" placeholder="HH:mm(24h)" required="required" th:attr="data-msg-pattern=#{i18n.time_format_24h}" /></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_06 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td><input type="hidden" value=""/></td></tr>'

    var cell_09 = '<tr class="clickable" hidden><td colspan="4"><div class="col-md-12 col-sm-offset-1">';
    var cell_10 = '<div class="row col-md-6"><label for="tfmCon_">' + tfmcon + ' >0.1 ppm</label><br/>';
    var cell_11 = '<input type="text" class="input-sm" size="4" name="tfmCon_" placeholder="" min="0" max="999.99" data-rule-number="true" maxlength="6" /><label>&nbsp;ppm</label></div>';
    var cell_12 = '<div class="row col-md-6"><label for="niclosamideCon_">' + niclosamide_con + '</label><br/><input type="text" class="input-sm" size="4" name="niclosamideCon_" placeholder="" min="0" max="999.99" data-rule-number="true" maxlength="6" /><label>&nbsp;ppb</label></div></div></td></tr>';

    var strHTML = cell_00 + cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10 + cell_11 + cell_12;

    $('#chemanalysis_table > tbody:last').append($.parseHTML(strHTML));

    var branchList = "#allbranches0" + rowLength;

    $.each(thebranches, function () {
        $(branchList).append('<option value="' + this.showText + '"></input></option>');
        $(branchList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    var allRows = $('#chemanalysis_table').children('tbody').children().not(".clickable");
    if (allRows.length > 1) {
        $('#chemanalysis_table').children('tbody').children().not(".clickable").eq(allRows.length - 2).find('td').eq(7).html('');
    }
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    resetIndexToOuterTableRow('#chemanalysis_table', ".clickable");
    addActionListener();
    addValidationListener('#edit_form');
    $('input[type="date"]').prop('max', getCurrentDateStr());

    $.applyAwesomplete('#chemanalysis_table');
}

function setStationDropdown(stationlistObj, branchId, stationObj) {
    var stationList = allthestations[branchId];
    $.each(stationList, function () {
        stationlistObj.append('<option value="' + this.showText + '"></input></option>');
        stationlistObj.children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $(stationObj).attr('list', $(stationObj).attr('data-list'));
    $.applyAwesomplete("#chemanalysis_table");
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
    var allRows = $('#chemanalysis_table').children('tbody').children().not(".clickable");
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
            } else {
                showErrorMessage("Chemical Analysis " + (i +1) + ": " + $.getMessage("i18n.select_value_datalist_dc"), 10, "displayMessage");
                valid = false;
            }
        }
    });
    return valid;
}

function loadChemanalysis() {
    if (theTR.trChemicalAnalysises.length > 0) {
        $.each(theTR.trChemicalAnalysises, function (i) {
            if ($('#chemanalysis_table').children('tbody').children().not(".clickable").length !== (i + 1)) {
                addRow();
            }

            var lastRow = $('#chemanalysis_table').children('tbody').children().not(".clickable").last();
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
            allFields.eq(0).val(this.concentrationOfTFM);
            allFields.eq(1).val(this.concentrationOfNiclosamide);
        });
        $('#button_continue').prop('disabled', false);
    }
}

function isOneEmptyRow() {
    var allRows = $('#chemanalysis_table').children('tbody').children().not(".clickable");
    if (allRows.length === 1) {
        var tds = allRows.eq(0).find('td');
        if ((tds.eq(1).find('input').val() === undefined || $.trim(tds.eq(1).find('input').val()).length === 0)
            && (tds.eq(2).find('input').val() === undefined || $.trim(tds.eq(2).find('input').val()).length === 0))
            return true;
    }
    return false;
}
