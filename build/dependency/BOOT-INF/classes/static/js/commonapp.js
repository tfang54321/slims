var appStore = {
    initDB: function (db) {
        $.db_createObjectStore(db, 'habitat_inventory', {keyPath: "hiOffLine_Id"});
        $.db_createObjectStore(db, 'fish_modules', {keyPath: "fmOffLine_Id"});
        $.db_createObjectStore(db, 'larval_assessments', {keyPath: "laOffLine_Id"});
    }
};

$(document).on('wb-ready.wb', function() {
    $.db_initDB(appStore.initDB);

    $("input[data-rule-digits]").on("input", function() {
        validateDigits(this);
    });

    $("input[data-rule-number]").on("input", function() {
        validateNumber(this);
    });
});

$("#slimMsgBox").click(function () {
    if ($("#slimMsgBox").css("visibility") === "visible")
        $("#slimMsgBox").css("visibility", "hidden");
});

//generic

function validateNumber(elem){
    if(!$(elem).val().match("^([-])?[0-9]+([.])?([0-9]+)*$")){
        $(elem).val('')
    }
}

function validateDigits(elem){
    if(!$(elem).val().match("^([-])?[0-9]+$")){
        $(elem).val('')
    }
}

function getElementId(inputStr) {
    if (!inputStr.startsWith("#")) {
        inputStr = "#" + inputStr;
    }
    return inputStr;
}

function resetIndexToTableRow(inputObj) {
    resetIndexToTableRow2(inputObj, true)
}

function resetIndexToTableRowWithControlledAppId(inputObj) {
    resetIndexToTableRow2(inputObj, false)
}

function resetIndexToTableRow2(inputObj, autoIndex) {
    var allRows;

    if (typeof inputObj === 'string') {
        if (!inputObj.startsWith("#")) {
            inputObj = "#" + inputObj;
        }
        allRows = $(inputObj).find('> tbody > tr');
    } else if (typeof inputObj === 'object') {
        allRows = inputObj.children();
    }

    if (autoIndex === true) {
        allRows.each(function (i) {
            var tds = $(this).find('td');
            tds.eq(0).find("input").val(i + 1);
        });
    } else {
        setIndexWithLargestExistingAppId(allRows)
    }
}

function setIndexWithLargestExistingAppId(allRows) {
    var currentLargestAppId = 0;
    allRows.each(function () {
        var tds = $(this).find('td');
        var appId = tds.eq(0).find("input").val();
        if (appId != null && appId.length !== 0 && Number(appId) > Number(currentLargestAppId)) {
            currentLargestAppId = appId;
        }
    });

    allRows.each(function () {
        var tds = $(this).find('td');
        var appId = tds.eq(0).find("input").val();
        if (appId == null || appId.length === 0) {
            currentLargestAppId = Number(currentLargestAppId) + 1;
            tds.eq(0).find("input").val(currentLargestAppId);
        }
    });
}

function resetIndexToOuterTableRow(tableId, innerFilter) {
    tableId = getElementId(tableId);
    var allRows = $(tableId).children('tbody').children().not(innerFilter);

    allRows.each(function (i) {
        var tds = $(this).find('td');
        tds.eq(0).find("input").val(i + 1);
    });
}

function findObjectId(inputObj, inputText) {
    var objId;

    if (inputObj !== null) {
        if (typeof inputObj === 'string') {
            if (!inputObj.startsWith("#")) {
                inputObj = "#" + inputObj;
            }
            $(inputObj).find("option").each(function () {
                if ($(this).val() === inputText) {
                    objId = $(this).find(":hidden").val();
                    return false;
                }
            });
        } else if (typeof inputObj === 'object') {
            inputObj.find("option").each(function () {
                if ($(this).val() === inputText) {
                    objId = $(this).find(":hidden").val();
                    return false;
                }
            });
        }
    }
    return objId;
}

function thereAreRowsSelected(tableId) {
    tableId = getElementId(tableId);
    if ($(tableId).find('tr.selected').length > 0)
        return true;
    else
        return false;
}

function getFormData(formId) {
    formId = getElementId(formId);
    return $(formId).serializeArray().reduce(function (obj, item) {
        var name = item.name, value = item.value;

        if (obj.hasOwnProperty(name)) {
            if (typeof obj[name] === "string") {
                obj[name] = [obj[name]];
                obj[name].push(value);
            } else {
                obj[name].push(value);
            }
        } else {
            obj[name] = value;
        }
        return obj;
    }, {});
}

function getLocale() {
    var localizedName;
    if ($('html')[0].lang.toLowerCase().includes("fr")) {
        localizedName = 'fra';
    } else {
        localizedName = 'eng';
    }
    return localizedName;
}

function thereIsRadioChecked(rootSectionId) {
    rootSectionId = getElementId(rootSectionId);
    var elementId = rootSectionId + " input[type=radio]:checked";

    return ($(elementId).length > 0);
}

function getLanguageURL(localeStr) {
    var enURL = "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/English.json";
    var frURL = "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/French.json";

    if ($('html')[0].lang.toLowerCase().includes("fr")) {
        return frURL;
    } else {
        return enURL;
    }
}

/*function singleTableRowOnSelect(table) {
    table.on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        }
        else {
            $('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });
}*/

function singleTableRowOnSelect(tableId) {
    tableId = getElementId(tableId);
    $(tableId).on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            $('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });
}

// multiple select rows in a table
var lastSelectedRow;

function multipleTableRowOnSelect(tableId) {
    tableId = getElementId(tableId);
    $(tableId).on('click', 'tr', function () {

        var allRows = $(tableId).find('> tbody > tr');

        if (window.event.ctrlKey) {
            this.className = this.className === 'selected' ? '' : 'selected';
            lastSelectedRow = this;
        }

        if (window.event.button === 0) {
            if (!window.event.ctrlKey && !window.event.shiftKey) {
                if ((this.className === 'selected') && ($(tableId).find('tr.selected').length === 1)) {
                    $(this).removeClass('selected');
                } else {
                    clearAll(allRows);
                    this.className = this.className === 'selected' ? '' : 'selected';
                    lastSelectedRow = this;
                }
            }

            if (window.event.shiftKey) {
                selectRowsBetweenIndexes(allRows, [lastSelectedRow.rowIndex, this.rowIndex])
            }
        }
    });
}

function selectRowsBetweenIndexes(trs, indexes) {
    indexes.sort(function (a, b) {
        return a - b;
    });

    for (var i = indexes[0]; i <= indexes[1]; i++) {
        trs[i - 1].className = 'selected';
    }
}

function clearAll(trs) {
    for (var i = 0; i < trs.length; i++) {
        trs[i].className = '';
    }
}

//end of multiple select

function isEmpty(inputStr) {
    if (inputStr === null || inputStr === undefined || inputStr.length === 0)
        return true;
    else
        return false;
}

function loaderControl() {
    $("#loader").css("visibility", "hidden");
}

//message box
function showSuccessMessage(msg, waitsec, appendToId) {
    var waittime = 1000;
    if (waitsec !== undefined && waitsec !== null) {
        waittime = waittime * waitsec;
    } else {
        waittime = 0;
    }
    $.showAlert("success", msg, waittime, appendToId);
}

function showErrorMessage(msg, waitsec, appendToId) {
    var waittime = 1000;
    if (waitsec !== undefined && waitsec !== null) {
        waittime = waittime * waitsec;
    } else {
        waittime = 0;
    }
    $.showAlert("danger", msg, waittime, appendToId);
}

function showWarningMessage(msg, waitsec, appendToId) {
    var waittime = 1000;
    if (waitsec !== undefined && waitsec !== null) {
        waittime = waittime * waitsec;
    } else {
        waittime = 0;
    }
    $.showAlert("warning", msg, waittime, appendToId);
}

//form control
function formIsChanged(formId, form_original_data) {
    formId = getElementId(formId);
    var current_form_data = $(formId).serialize();
    if (current_form_data !== form_original_data) {
        return true;
    }
    return false;
}

function addValidationListener(formId) {
    formId = getElementId(formId);
    $(formId).find('input').each(function () {
        $(this).on('blur', function () {
            if (!$(this).valid()) {
                $(this).validate();
            }
        });
    });
}

/*
 * function confirmContinue(form_original_data, warningMsg) { var
 * current_form_data = $("#edit_form").serialize(); if(current_form_data !=
 * form_original_data) { if( ! confirm(warningMsg)) { return false; } } return
 * true; }
 */

//confirm box
function confirmDialog(msg) {
    // $('#winmask').css("visibility", "visible");
    // $('#confirmBox').css("visibility", "visible");
    // $('#confirmBox').css("background-color", "#FFE4E1");
    $('#confirmBox p').text(msg);
    $('#confirmBox h2').text($.getMessage("confirm"));

    wb.doc.trigger("open.wb-lbx", [
        [
            {
                src: "#confirm-modal",
                type: "inline"
            }
        ],
        true
    ]);
    return new Promise(function (doReturn) {
        $('#confirmYes').click(function () {
            $("#winmask").css("visibility", "hidden");
            $("#confirmBox").css("visibility", "hidden");
            doReturn(true);
        });
        $('#confirmNo').click(function () {
            $("#winmask").css("visibility", "hidden");
            $("#confirmBox").css("visibility", "hidden");
            doReturn(false);
        });
    });
}

function confirmExitOrContinue(form_original_data, warningMsg, redirectUrl) {
    if (formIsChanged('#edit_form', form_original_data)) {
        confirmDialog(warningMsg).then(function (check) {
            if (check) {
                window.location.href = redirectUrl;
            }
        });
    } else
        window.location.href = redirectUrl;
}

function confirmGoBack(form_original_data, warningMsg) {
    if (formIsChanged('#edit_form', form_original_data)) {
        confirmDialog(warningMsg).then(function (check) {
            if (check) {
                window.history.back();
            }
        });
    } else
        window.history.back();
}

function confirmAndDo(warningMsg, dofunc) {
    confirmDialog(warningMsg).then(function (check) {
        if (check) {
            dofunc();
        }
    });
}

//offline
function hasOfflineId() {
    var hash = window.location.hash;
    if (hash.indexOf('#') !== -1) {
        if ($.trim(hash.substring(hash.indexOf('#') + 1)).length > 0)
            return true;
    }
    return false;
}

function getOfflineId() {
    var offlineId;
    var hash = window.location.hash;
    if (hash.indexOf('#') !== -1) {
        offlineId = hash.substring(hash.indexOf('#') + 1);
    }
    return offlineId;
}

//date and time
function getCurrentDateStr() {
    var today = new Date();

    var yyyy = today.getFullYear().toString();
    var mm = (today.getMonth() + 1).toString();
    var dd = today.getDate().toString();

    if (mm.length === 1)
        mm = "0" + mm;
    if (dd.length === 1)
        dd = "0" + dd;
    return yyyy + "-" + mm + "-" + dd;
}

function getDiffTime(start, end) {
    start = start.split(":");
    end = end.split(":");
    var startDate = new Date(0, 0, 0, start[0], start[1], 0);
    var endDate = new Date(0, 0, 0, end[0], end[1], 0);
    var diff = endDate.getTime() - startDate.getTime();
    var hours = Math.floor(diff / 1000 / 60 / 60);
    diff -= hours * 1000 * 60 * 60;
    var minutes = Math.floor(diff / 1000 / 60);

    if (hours < 0)
        hours = hours + 24;
    return (hours <= 9 ? "0" : "") + hours + ":" + (minutes <= 9 ? "0" : "") + minutes;
}

function getFullDiffTime(startDate, endDate, startTime, endTime) {
    startTime = startTime.split(":");
    endTime = endTime.split(":");
    var startDateTime = new Date(0, 0, 0, startTime[0], startTime[1], 0);
    var endDateTime = new Date(0, 0, 0, endTime[0], endTime[1], 0);
    var diff = endDateTime.getTime() - startDateTime.getTime();
    var hours = Math.floor(diff / 1000 / 60 / 60);
    diff -= hours * 1000 * 60 * 60;
    var minutes = Math.floor(diff / 1000 / 60);

    var sDate = new Date(startDate);
    var eDate = new Date(endDate);
    var diffDays = Math.floor((eDate.getTime() - sDate.getTime()) / (1000 * 60 * 60 * 24));

    hours = diffDays * 24 + hours;

    return (hours <= 9 ? "0" : "") + hours + ":" + (minutes <= 9 ? "0" : "") + minutes;
}

function compareTime(startTime, endTime) {
    startTime = startTime.split(":");
    endTime = endTime.split(":");
    var startDateTime = new Date(0, 0, 0, startTime[0], startTime[1], 0);
    var endDateTime = new Date(0, 0, 0, endTime[0], endTime[1], 0);

    if (startDateTime > endDateTime)
        return 1;
    else if (startDateTime === endDateTime)
        return 0;
    else
        return -1;
}

function compareDate(startDate, endDate) {
    var startDay = new Date(startDate);
    var endDay = new Date(endDate);

    if (startDate > endDate)
        return 1;
    else if (startDate === endDate)
        return 0;
    else
        return -1;
}

//treatment pages
function gotoTRPage(selectedVal) {
    var localizedName = getLocale();
    var redirectUrl;
    var trId;
    if (theTR !== null) {
        trId = theTR.id;
        switch (selectedVal) {
            case 'trList' :
                redirectUrl = getBaseString() + localizedName + "/treatments";
                break;
            case 'trMain' :
                redirectUrl = getBaseString() + localizedName + "/treatments/main?trId=" + trId;
                break;
            case 'primaryApp' :
                redirectUrl = getBaseString() + localizedName + "/treatments/applications/primaryapp?trId=" + trId;
                break;
            case 'secondaryApp' :
                redirectUrl = getBaseString() + localizedName + "/treatments/applications/secondaryapp?trId=" + trId;
                break;
            case 'desiredCon' :
                redirectUrl = getBaseString() + localizedName + "/treatments/analysis/desiredcon?trId=" + trId;
                break;
            case 'mlc' :
                redirectUrl = getBaseString() + localizedName + "/treatments/analysis/minlethalcon?trId=" + trId;
                break;
            case 'waterChem' :
                redirectUrl = getBaseString() + localizedName + "/treatments/analysis/waterchem?trId=" + trId;
                break;
            case 'discharge' :
                redirectUrl = getBaseString() + localizedName + "/treatments/analysis/discharge?trId=" + trId;
                break;
            case 'chemAnalysis' :
                redirectUrl = getBaseString() + localizedName + "/treatments/analysis/chemanalysis?trId=" + trId;
                break;
            case 'summary' :
                redirectUrl = getBaseString() + localizedName + "/treatments/summary?trId=" + trId;
                break;
        }
    }

    if (redirectUrl !== null && redirectUrl !== undefined && redirectUrl.length > 0) {
        window.location.href = redirectUrl;
        //$('#gotoMenu').val(selectedVal);
    } else {
        if (selectedVal === "trList") {
            redirectUrl = getBaseString() + localizedName + "/treatments";
            window.location.href = redirectUrl;
        }
        $('#gotoMenu').val('');
    }
}

//la pages
function gotoLAPage(selectedVal) {
    var localizedName = getLocale();
    var redirectUrl;
    var laId;
    if (theLA !== null) {
        laId = theLA.id;
        switch (selectedVal) {
            case 'laList' :
                redirectUrl = getBaseString() + localizedName + "/larvalassessments";
                break;
            case 'laMain' :
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/main?laId=" + laId;
                break;
            case 'electroFishing' :
                if (theLA.laElectrofishingDetails != null) {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/electrofishings?laId=" + laId;
                }
                break;
            case 'granularBayer' :
                if (theLA.laGranularBayerDetails != null) {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/granularbayers?laId=" + laId;
                }
                break;
            case 'physicalChem' :
                if (theLA.laPhysicalChemicalData != null) {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/physicalchemicals?laId=" + laId;
                }
                break;
            case 'colcon' :
                if (theLA.collectCondition != null || theLA.effectiveness != null) {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/collectioncons?laId=" + laId;
                }
                break;
            case 'fishObserCol' :
                if (theLA.species.length > 0) {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/fishobsercols/summary?laId=" + laId;
                }
                break;
        }
    }

    if (redirectUrl !== null && redirectUrl !== undefined && redirectUrl.length > 0) {
        window.location.href = redirectUrl;
    } else {
        if (selectedVal === "laList") {
            redirectUrl = getBaseString() + localizedName + "/larvalassessments";
            window.location.href = redirectUrl;
        }
        $('#gotoMenu').val('');
    }
}

function showOnline() {
    $('#online_data').show();
    $('#offline_data').hide();
}

function showOffline() {
    $('#online_data').hide();
    $('#offline_data').show();
    $('#button_add_offline').prop('disabled', true);
}

function setShowOfflineButton(offlineDBName) {
    $.db_getAll(offlineDBName).then(function (results) {
        if (results !== undefined && results !== null && results.length > 0) {
            $('#button_showoffline').show();
        } else {
            $('#button_showoffline').hide();
        }
    });
}

function deleteOfflineData(tableId, dbName, offlineId, showSuccessMsg, showFailedMsg) {
    tableId = getElementId(tableId);
    $.db_getByKeyPath(dbName, offlineId).then(function (result) {
        if (result) {
            $.db_deleteByKeyPath(dbName, offlineId).then(function (result) {
                if (result) {
                    if (showSuccessMsg)
                        showSuccessMessage($.getMessage("offlinedata_delete_success"));
                    $(tableId).DataTable().row('.selected').remove().draw(false);
                } else {
                    if (showFailedMsg)
                        showErrorMessage($.getMessage("offlinedata_delete_failed"));
                }
            });
        } else {
            showErrorMessage($.getMessage("offlinedata_not_found"));
        }
    });
}

function deleteOfflineDataFromLocalDB(dbName, offlineId, showSuccessMsg, showFailedMsg) {
    $.db_getByKeyPath(dbName, offlineId).then(function (result) {
        if (result) {
            $.db_deleteByKeyPath(dbName, offlineId).then(function (result) {
                if (result) {
                    if (showSuccessMsg)
                        showSuccessMessage($.getMessage("offlinedata_delete_success"));
                } else {
                    if (showFailedMsg)
                        showErrorMessage($.getMessage("offlinedata_delete_failed"));
                }
            });
        } else {
            showErrorMessage($.getMessage("offlinedata_not_found"));
        }
    });
}

function getSpeciesCode(inputStr) {
    return inputStr.substring(inputStr.indexOf("(") + 1, inputStr.indexOf(")"));
}

function getBaseString() {
    var baseStr = contextedPath;
    if (baseStr !== '/') {
        if (baseStr.lastIndexOf('/') === 0)
            baseStr = baseStr + '/';
    }
    return baseStr;
}

function convertDataTextOnly() {
    var textOnlyElems = $('input[data-text-only="true"],textarea[data-text-only="true"],select[data-text-only="true"]');
    if ($(textOnlyElems).length) {
        $(textOnlyElems).each(function(index,elem) {
            var elemId = $(elem).attr('id');
            if (typeof elemId !== 'undefined' && elemId !== null) {
                var text = '';
                $(elem).hide();
                if ($(elem).prop('tagName').toLowerCase() === 'select') {
                    var texts = [];
                    $(elem).children('option:selected').each(function (index, opt) {
                        texts.push($(opt).text());
                    });
                    text += texts.join(",");
                } else {
                    text += $(elem).val();
                }
                var textOnlyElem = $('#' + elemId + '_textOnly');
                if ($(textOnlyElem).length) {
                    $(textOnlyElem).html(text);
                } else {
                    $(elem).after('<span id="' + elemId + '_textOnly">' + text + '</span>');
                }
            }
        });
    }
}

function setDefaultFilterYear() {
    if($('#filterYear option').length > 1) {
        $('#filterYear option:eq(1)').prop('selected', true);
    }
}

function validateInputDateVsTrStartEndDates(inputDateStr) {
    if (isEmpty(inputDateStr)) {
        return;
    }

    var trStartdate = theTR.trLogistics.treatmentStart;
    var trEndDate = theTR.trLogistics.treatmentEnd;
    $("#displayMessage").html("");
    if (compareDate(inputDateStr, trStartdate) === -1
        || compareDate(inputDateStr, trEndDate) === 1) {
        showErrorMessage($.getMessage("tr_app_start_end_date_message"), null, "displayMessage");
    }
}

function setTimeValueTwoColonTwo(elem) {
    var valarray = elem.val().split(':');
    valarray.forEach(function(item, index) {
        valarray[index] = item.replace(/\D/g, '');
    });

    var firstSectionVal = valarray[0].length > 2 ? valarray[0].substr(0, 2) : valarray[0];
    var delimiter = valarray[0].length > 2 || valarray.length > 1 ? ':' : '';
    var secondSectionVal =
        valarray[0].length > 2 ? valarray[0].substring(2) : '' +
        valarray.length > 1 ? (valarray [1].length >= 2 ? valarray[1].substr(0, 2) : valarray[1]) : '';
    var newVal = firstSectionVal + delimiter + secondSectionVal;

    elem.val(newVal);
}

function setTimeValueThreeColonTwo(elem) {
    var valarray = elem.val().split(':');
    valarray.forEach(function(item, index) {
        valarray[index] = item.replace(/\D/g, '');
    });

    var firstSectionVal = valarray[0].length > 3 ? valarray[0].substr(0, 3) : valarray[0];
    var delimiter = valarray[0].length > 3 || valarray.length >= 2 ? ':' : '';
    var secondSectionVal =
        valarray[0].length > 3 ? valarray[0].substring(3) : '' +
        valarray.length >= 2 ? (valarray [1].length >= 2 ? valarray[1].substr(0, 2) : valarray[1]) : '';
    var newVal = firstSectionVal + delimiter + secondSectionVal;

    elem.val(newVal);
}
