$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadDesiredConcentrations();
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
            actionUrl = getBaseString() + localizedName + "/api/treatments/desiredcon/" + theTR.id;

            var formData = getFormData("#edit_form");
            formData.numOfCons = $('#desiredcon_table').find('> tbody > tr').length;
            var valid = getBranchAndStationFromIds(formData);

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

        var redirectUrl = getBaseString() + localizedName + "/treatments/analysis/minlethalcon?trId=" + theTR.id;

        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#desiredcon_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();

        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#desiredcon_table').find('> tbody > tr');

        if (allRows.length === 0) {
            addRow();
        } else {
            var lastRow = $('#desiredcon_table').find('> tbody > tr').eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length === 0) {
                lastRow.children('td').eq(7).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    $('#desiredcon_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
        addValidationListener('#edit_form');
    });

});

function assignNamesToTableRow() {
    var branchName = "trBranch";
    var branchListName = "allbranches";

    var stationName = "trStation";
    var stationListName = "allStations";
    var stationAdjustName = "stationAdjust";

    var minName = "appConMin";
    var maxName = "appConMax";
    var nicloPercName = "nicloPerc";

    var desiredConIdName = "desiredConId";

    var allRows = $('#desiredcon_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(1).find("input").first().attr("name", branchName + String(i)).attr("id", branchName + String(i)).attr("list", branchListName + String(i));
        tds.eq(1).find("datalist").attr("id", branchListName + String(i));
        tds.eq(1).find("label").attr("for", branchName + String(i));

        tds.eq(2).find("input").first().attr("name", stationName + String(i)).attr("id", stationName + String(i)).attr("list", stationListName + String(i));
        tds.eq(2).find("datalist").attr("id", stationListName + String(i));
        tds.eq(2).find("label").attr("for", stationName + String(i));

        tds.eq(2).find("select").attr("name", stationAdjustName + String(i)).attr("id", stationAdjustName + String(i));
        tds.eq(3).find("input").attr("name", minName + String(i));
        tds.eq(3).find("input").attr("id", minName + String(i));
        tds.eq(3).find("label").attr("for", minName + String(i));
        tds.eq(4).find("input").attr("name", maxName + String(i));
        tds.eq(4).find("input").attr("id", maxName + String(i));
        tds.eq(4).find("label").attr("for", maxName + String(i));
        tds.eq(5).find("input").attr("name", nicloPercName + String(i));
        tds.eq(5).find("input").attr("id", nicloPercName + String(i));
        tds.eq(5).find("label").attr("for", nicloPercName + String(i));
        tds.eq(8).find("input").attr("name", desiredConIdName + String(i));
    });
}

function addRow() {
    var rowLength = $('#desiredcon_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><input type="text" class="input-sm" value="" name="#" readonly="true" size="2" /></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" id="trbranch0' + rowLength + '" name="trbranch0' + rowLength + '" class="input-sm branchlist" required="required" list="allbranches0' + rowLength + '" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/><datalist id="allbranches0' + rowLength + '"></datalist></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="text" id="trStation0' + rowLength + '" name="trStation0' + rowLength + '" class="input-sm" list="allStations0' + rowLength + '" required="required" data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/> <datalist id="allStations0' + rowLength + '"></datalist> <select class=" input-sm fas" id="stationAdjust0' + rowLength + '" name="stationAdjust0' + rowLength + '"><option class="fas fa-equals" value="equal">&#xf52c;</option><option class="fas fa-minus" value="minus">&#xf068;</option><option class="fas fa-plus" value="plus">&#xf067;</option></select></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" value="0" name="#" placeholder="" data-rule-number="true" min="0" max="999.99" maxlength="6" /></td>';
    var cell_05 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" value="0" name="#" placeholder="" data-rule-number="true" min="0" max="999.99" maxlength="6" /></td>';
    var cell_06 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" placeholder="" data-rule-number="true" min="0" max="100" maxlength="4" /></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td><input type="hidden" value=""/></td></tr>'

    var strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09;

    $('#desiredcon_table > tbody:last').append($.parseHTML(strHTML));

    var branchList = "#allbranches0" + rowLength;

    $.each(thebranches, function () {
        $(branchList).append('<option value="' + this.showText + '"></input></option>');
        $(branchList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    var allRows = $('#desiredcon_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#desiredcon_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(7).html('');
    }
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    resetIndexToTableRow('#desiredcon_table');
    addActionListener();
    addValidationListener('#edit_form');
    $.applyAwesomplete('#desiredcon_table')
}

function setStationDropdown(stationlistObj, branchId, stationObj) {
    stationlistObj.empty();
    var stationList = allthestations[branchId];
    $.each(stationList, function () {
        stationlistObj.append('<option value="' + this.showText + '"></input></option>');
        stationlistObj.children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $(stationObj).attr('list', $(stationObj).attr('data-list'));
    $.applyAwesomplete("#desiredcon_table");
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
}

function getBranchAndStationFromIds(theform) {
    var branchId = "branchId";
    var stationId = "stationId";
    var allRows = $('#desiredcon_table').find('> tbody > tr');
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
                showErrorMessage("Desired Concentration " + (i +1) + ": " + $.getMessage("i18n.select_value_datalist_dc"), 10, "displayMessage");
                valid = false;
            }
        }
    });
    return valid;
}

function loadDesiredConcentrations() {
    if (theTR.desiredConcentrations.length > 0) {
        $.each(theTR.desiredConcentrations, function (i) {

            if ($('#desiredcon_table').find('> tbody > tr').length !== (i + 1)) {
                addRow();
            }
            var lastRow = $('#desiredcon_table').find('>tbody > tr').last();
            var tds = lastRow.find('td');

            tds.eq(0).find('input').val(i + 1);
            tds.eq(1).find('input').first().val(this.branchLentic.showText);
            setStationDropdown(tds.eq(2).find('datalist'), this.branchLentic.id, tds.eq(2).find('input'));
            tds.eq(2).find('input').first().val(this.station.showText);
            tds.eq(2).find("select").val(this.stationAdjust);
            tds.eq(3).find('input').val(this.appConcentrationMin);
            tds.eq(4).find('input').val(this.appConcentrationMax);
            tds.eq(5).find('input').val(this.niclosamide);
            tds.eq(8).find('input').val(this.id);
            // addRow();
        });
        $('#button_continue').prop('disabled', false);
    }
}

function isOneEmptyRow() {
    var allRows = $('#desiredcon_table').find('> tbody > tr');
    if (allRows.length === 1) {
        var tds = allRows.eq(0).find('td');
        if ((tds.eq(1).find('input').val() === undefined || $.trim(tds.eq(1).find('input').val()).length === 0)
            && (tds.eq(2).find('input').val() === undefined || $.trim(tds.eq(2).find('input').val()).length === 0))
            return true;
    }
    return false;
}
