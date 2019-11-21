$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadSecondaryApps();
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
            actionUrl = getBaseString() + localizedName + "/api/treatments/secondaryapp/" + theTR.id;

            var formData = getFormData("#edit_form");
            formData.numOfApps = $('#secondary_app_table').find('> tbody > tr').length;
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
                    //$('#"button_continue"').prop('disabled', false);
                    form_original_data = $("#edit_form").serialize();
                    sessionStorage.setItem("successMessage", response.message);
                    window.location.reload();
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

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/treatments";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_continue').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/treatments/analysis/desiredcon?trId=" + theTR.id;
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#secondary_app_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#secondary_app_table').find('> tbody > tr');

        if (allRows.length == 0) {
            addRow();
        } else {
            var lastRow = $('#secondary_app_table').find('> tbody > tr').eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length === 0) {
                lastRow.children('td').eq(7).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    $('#secondary_app_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
        addValidationListener('#edit_form');
    });

    $('#secondary_app_table').on('click', 'span.glyphicon-edit', function (e) {
        e.preventDefault();
        var secondaryAppId = $(this).closest("tr").find('td').eq(8).find('input').val();
        if (isEmpty(secondaryAppId)) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.toeditpage_warning_message"), null, "displayMessage");
        } else {
            var redirectUrl = getBaseString() + localizedName + "/treatments/applications/secondaryapp/singleapp?trId=" + theTR.id + "&secondaryAppId=" + secondaryAppId;

            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        }
    });
});

function assignNamesToTableRow() {
    var applicationId = "appId";
    var branchName = "trBranch";
    var branchListName = "allbranches";
    var stationFromName = "trStationFrom";
    var stationFromListName = "allFromStations";
    var stationFromAdjustName = "stationFromAdjust";
    var treatDateName = "trTreatDate";
    var timeStartName = "trTimeStart";
    var secondAppIdName = "secondAppId";

    var allRows = $('#secondary_app_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("input").attr("name", applicationId + String(i)).attr("id", applicationId + String(i));

        tds.eq(1).find("label").attr("for", branchName + String(i));
        tds.eq(1).find("input").first().attr("name", branchName + String(i)).attr("id", branchName + String(i)).attr("list", branchListName + String(i));
        tds.eq(1).find("datalist").attr("id", branchListName + String(i));

        tds.eq(2).find("label").attr("for", stationFromName + String(i));
        tds.eq(2).find("input").first().attr("name", stationFromName + String(i)).attr("id", stationFromName + String(i)).attr("list", stationFromListName + String(i));
        tds.eq(2).find("datalist").attr("id", stationFromListName + String(i));
        tds.eq(2).find("select").attr("name", stationFromAdjustName + String(i)).attr("id", stationFromAdjustName + String(i));

        tds.eq(3).find("label").attr("for", treatDateName + String(i));
        tds.eq(3).find("input").attr("name", treatDateName + String(i)).attr("id", treatDateName + String(i));
        tds.eq(4).find("label").attr("for", timeStartName + String(i));
        tds.eq(4).find("input").attr("name", timeStartName + String(i)).attr("id", timeStartName + String(i));
        tds.eq(8).find("input").attr("name", secondAppIdName + String(i));
    });
}

function addRow() {
    var strHTML = '';
    var rowLength = $('#secondary_app_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><input type="text" class="input-sm" value="" name="#" readonly="true" size="2" /></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" id="trbranch0' + rowLength + '" name="trbranch0' + rowLength + '" class="input-sm branchlist" required="required" list="allbranches0' + rowLength + '"  data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/><datalist id="allbranches0' + rowLength + '"></datalist></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="text" id="trStationFrom0' + rowLength + '" name="trStationFrom0' + rowLength + '" class="input-sm" list="allFromStations0' + rowLength + '" required="required"  data-ap-combobox="true" data-ap-auto-select-first="true" data-ap-filter-startsWith-ignore="(,)"/> <datalist id="allFromStations0' + rowLength + '"></datalist> <select class=" input-sm fas" id="stationFromAdjust0' + rowLength + '" name="stationFromAdjust0' + rowLength + '"><option class="fas fa-equals" value="equal">&#xf52c;</option><option class="fas fa-minus" value="minus">&#xf068;</option><option class="fas fa-plus" value="plus">&#xf067;</option></select></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="date" class="input-sm appdate" name="#" required="required" /></td>';
    var cell_05 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm time-start" name="#" maxlength="5" minlength="5" placeholder="HH:mm(24h)" required="required" th:attr="data-msg-pattern=#{i18n.time_format_24h}" /></td>';
    var cell_06 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-edit" aria-hidden="true"></span></a></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td><input type="hidden" value=""/></td></tr>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09;

    $('#secondary_app_table > tbody:last').append($.parseHTML(strHTML));

    var branchList = "#allbranches0" + rowLength;

    $.each(thebranches, function () {
        $(branchList).append('<option value="' + this.showText + '"></input></option>');
        $(branchList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    var allRows = $('#secondary_app_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#secondary_app_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(7).html('');
    }
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    resetIndexToTableRowWithControlledAppId('#secondary_app_table');
    addActionListener();
    addValidationListener('#edit_form');
    $('input[type="date"]').prop('max', getCurrentDateStr());
    $.applyAwesomplete('#secondary_app_table');
}

function setStationDropdown(stationlistObj, branchId, stationObj) {
    var stationList = allthestations[branchId];
    $.each(stationList, function () {
        stationlistObj.append('<option value="' + this.showText + '"></input></option>');
        stationlistObj.children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $(stationObj).attr('list', $(stationObj).attr('data-list'));
    $.applyAwesomplete("#secondary_app_table");
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

    $('.time-start').keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this);
    });

    $('.appdate').focusout(function () {
        validateInputDateVsTrStartEndDates($(this).val());
    });
}

function getBranchAndStationFromIds(theform) {
    var branchId = "branchId";
    var stationFromId = "stationFromId";
    var allRows = $('#secondary_app_table').find('> tbody > tr');

    var valid = true;

    allRows.each(function (i) {
        var tds = $(this).find('td');

        var rowBranch = tds.eq(1).find('input').val();
        var rowStationFrom = tds.eq(2).find('input').val();

        if (rowBranch != null && $.trim(rowBranch).length > 0 && rowStationFrom != null && $.trim(rowStationFrom).length > 0) {

            var branchlistObj = tds.eq(1).find('datalist');
            var stationlistObj = tds.eq(2).find('datalist');
            var brId = findObjectId(branchlistObj, rowBranch);
            var sfId = findObjectId(stationlistObj, rowStationFrom);

            if(brId !== undefined && sfId !== undefined) {
                theform[branchId + String(i)] = brId;
                theform[stationFromId + String(i)] = sfId;
            }
            else {
                showErrorMessage("Application " + (i +1) + ": " + $.getMessage("i18n.select_value_datalist"), 10, "displayMessage");
                valid = false;
            }
        }
    });
    return valid;
}

function loadSecondaryApps() {
    if (theTR.trSecondaryApplications.length > 0) {
        $.each(theTR.trSecondaryApplications, function (i) {

            if ($('#secondary_app_table').find('> tbody > tr').length !== (i + 1)) {
                addRow();
            }
            var lastRow = $('#secondary_app_table').find('>tbody > tr').last();
            var tds = lastRow.find('td');

            tds.eq(0).find('input').val(this.applicationId);
            tds.eq(1).find('input').first().val(this.branchLenticFrom.showText);
            setStationDropdown(tds.eq(2).find('datalist'), this.branchLenticFrom.id, tds.eq(2).find('input'));
            tds.eq(2).find('input').first().val(this.stationFrom.showText);
            tds.eq(2).find("select").val(this.stationFromAdjust);
            tds.eq(3).find('input').val(this.treatmentDate);
            tds.eq(4).find('input').val(this.timeStart);
            tds.eq(8).find('input').val(this.id);
            // addRow();
        });
        //$('#button_continue').prop('disabled', false);
    }
}

function isOneEmptyRow() {
    var allRows = $('#secondary_app_table').find('> tbody > tr');
    if (allRows.length === 1) {
        var tds = allRows.eq(0).find('td');
        if ($.trim(tds.eq(1).find('input').val()).length === 0
            && $.trim(tds.eq(2).find('input').val()).length === 0)
            return true;
    }
    return false;
}
