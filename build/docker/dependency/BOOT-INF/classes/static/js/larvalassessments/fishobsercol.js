$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;

    $.isOnline().then(function (isOnline) {
        if (isOnline) {
            var loc = window.location.href;
            if (loc.indexOf('#') !== -1)
                loadOfflineData();
            else
                loadOnlineData();
        } else {
            loadOfflineData();
        }
    });

    var reloading = sessionStorage.getItem("successMessage");
    if(reloading){
        sessionStorage.removeItem("successMessage");
        showSuccessMessage(reloading, null, "displayMessage");
    }

    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getFOCFormData("#edit_form");

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineFOC("-FOC", formData);
                }
            });
        }
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;

        actionType = "PUT";
        actionUrl = getBaseString() + localizedName + "/api/las/fishobsercol/" + theLA.id;

        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
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

    function createOfflineFOC(idSuffix, dataJSON) {
        return new Promise(function (doReturn) {

            var offlineLAId = getOfflineId();
            dataJSON.laOffLine_Id = offlineLAId + idSuffix;

            $.db_insertOrUpdate('larval_assessments', dataJSON).then(function (result) {
                if (result) {
                    form_original_data = $("#edit_form").serialize();
                    $("#displayMessage").html("");
                    showSuccessMessage($.getMessage("offlinedata_save_success"), null, "displayMessage");
                } else {
                    $("#displayMessage").html("");
                    showErrorMessage($.getMessage("offlinedata_save_failed"), null, "displayMessage");
                    doReturn();
                }
            });
        });
    }

    /*$('#button_return').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/larvalassessments/collectioncons?laId=" + theLA.id;
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });*/
    $('#button_return').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/collectioncons?laId=" + theLA.id;
            } else {
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/collectioncons/offline#" + getOfflineId();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    $('#button_end').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/larvalassessments";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#fish_collect_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#fish_collect_table').find('> tbody > tr');
        if (allRows.length == 0) {
            addRow();
        } else {
            var lastRow = $('#fish_collect_table').find('> tbody > tr').eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                lastRow.children('td').eq(8).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    $('#fish_collect_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
        addValidationListener('#edit_form');
    });

    $('#fish_collect_table').on('click', 'span.glyphicon-edit', function (e) {
        e.preventDefault();
        var rowSpecie = $(this).closest("tr").find('input').first().val();
        var totalVal = $(this).closest("tr").find('td').eq(5).find('input').val();
        var specieId = $(this).closest("tr").find('td').eq(9).find('input').val();
        var specieCode = getSpeciesCode(rowSpecie);

        $.isOnline().then(function (isOnline) {
            var redirectUrl, offlineId;
            if (isOnline) {
                var loc = window.location.href;
                if (loc.indexOf('#') !== -1) {
                    offlineId = getOfflineId();
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/fishobsercols/individuals/offline#" + offlineId + "-INDIS-" + specieCode + "#" + totalVal;
                } else {
                    if (isEmpty(specieId)) {
                        $("#displayMessage").html("");
                        showErrorMessage($.getMessage("i18n.toeditpage_warning_message"), null, "displayMessage");
                        return false;
                    } else if (rowSpecie != null && $.trim(rowSpecie).length > 0 && totalVal != '0') {
                        redirectUrl = getBaseString() + localizedName + "/larvalassessments/fishobsercols/individuals?laId=" + theLA.id + "&specieId=" + specieId;
                    }
                    else if(totalVal == '0') {
                        $("#displayMessage").html("");
                        showErrorMessage($.getMessage("i18n.totalval_warning_message"), null, "displayMessage");
                        return false;
                    }
                }
            } else if (rowSpecie != null && $.trim(rowSpecie).length > 0 && totalVal != '0') {
                offlineId = getOfflineId();
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/fishobsercols/individuals/offline#" + offlineId + "-INDIS-" + specieCode + "#" + totalVal;
            }
            else if(totalVal == '0') {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("i18n.totalval_warning_message"), null, "displayMessage");
                return false;
            }
            if (redirectUrl !== null)
                confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    function loadOnlineData() {
        $('#goto').show();
        if (theLA.species.length > 0) {
            $.each(theLA.species, function (i) {
                if ($('#fish_collect_table').find('> tbody > tr').length != (i + 1)) {
                    addRow();
                }
                var lastRow = $('#fish_collect_table').find('> tbody > tr').last();
                var tds = lastRow.find('td');

                tds.eq(0).find('input').first().val(getSpecieText(tds.eq(0).find('datalist'), this.speciesCode));
                tds.eq(1).find('input').val(this.observedAlived);
                tds.eq(2).find('input').val(this.observedDead);
                tds.eq(3).find('input').val(this.collectedReleased);
                tds.eq(4).find('input').val(this.collectedDead);
                tds.eq(5).find('input').val(this.total);
                tds.eq(9).find('input').val(this.id);
            });
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        $('#goto').hide();
        if (hasOfflineId()) {
            var offlineLAId = getOfflineId();
            var offlineFOCId = offlineLAId + "-FOC";

            $.db_getByKeyPath('larval_assessments', offlineLAId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                }
            });
            $.db_getByKeyPath('larval_assessments', offlineFOCId).then(function (result) {
                if (result) {
                    var numOfSpecies = result.numOfSpecies;
                    var i;
                    for (i = 0; i < numOfSpecies; i++) {
                        if ($('#fish_collect_table').find('> tbody > tr').length != (i + 1)) {
                            addRow();
                        }

                        var lastRow = $('#fish_collect_table').find('> tbody > tr').last();
                        var tds = lastRow.find('td');

                        var speciesCode = result["fishSpecies" + String(i)];

                        tds.eq(0).find('input').first().val(getSpecieText(tds.eq(0).find('datalist'), getSpeciesCode(speciesCode)));
                        tds.eq(1).find('input').val(result["obserAlive" + String(i)]);
                        tds.eq(2).find('input').val(result["obserDead" + String(i)]);
                        tds.eq(3).find('input').val(result["colReleased" + String(i)]);
                        tds.eq(4).find('input').val(result["colDead" + String(i)]);
                        tds.eq(5).find('input').val(result["fishTotal" + String(i)]);
                    }
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }
});

function assignNamesToTableRow() {
    var specieListName = "allSpecieList";
    var specieListId = "allSpecies";
    var fishSpecieName = "fishSpecies";
    var aliveName = "obserAlive";
    var deadName = "obserDead";
    var releasedName = "colReleased";
    var colDeadName = "colDead";
    var totalName = "fishTotal";
    var specieIdName = "specieId";

    var allRows = $('#fish_collect_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("input").first().attr("name", fishSpecieName + String(i)).attr("id", specieListId + String(i)).attr("list", specieListName + String(i));
        tds.eq(0).find("datalist").attr("id", specieListName + String(i));

        tds.eq(1).find("label").attr("for", aliveName + String(i));
        tds.eq(1).find("input").attr("name", aliveName + String(i)).attr("id", aliveName + String(i));

        tds.eq(2).find("label").attr("for", deadName + String(i));
        tds.eq(2).find("input").attr("name", deadName + String(i)).attr("id", deadName + String(i));

        tds.eq(3).find("label").attr("for", releasedName + String(i));
        tds.eq(3).find("input").attr("name", releasedName + String(i)).attr("id", releasedName + String(i));

        tds.eq(4).find("label").attr("for", colDeadName + String(i));
        tds.eq(4).find("input").attr("name", colDeadName + String(i)).attr("id", colDeadName + String(i));

        tds.eq(5).find("input").attr("name", totalName + String(i));

        tds.eq(9).find("input").attr("name", specieIdName + String(i));
    });
}

function addRow() {
    var strHTML = '';
    var rowLength = $('#fish_collect_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><input type="text" id="laspecies02" name="laspecies" class="input-sm" list="allspecies0' + rowLength + '" style="width: 100%" data-ap-auto-select-first="true"/><datalist id="allspecies0' + rowLength + '"></datalist></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" value="0" data-rule-digits="true" maxlength="4" placeholder="" oninput="validateDigits(this)"></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" value="0" data-rule-digits="true" maxlength="4" placeholder="" oninput="validateDigits(this)"/></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" value="0" data-rule-digits="true" maxlength="4" placeholder="" oninput="validateDigits(this)"/></td>';
    var cell_05 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" value="0" data-rule-digits="true" maxlength="4" placeholder="" oninput="validateDigits(this)"/></td>';
    var cell_06 = '<td class="text-center"><input type="text" class="input-sm" size="4" name="#" value="0" placeholder="" readOnly="true"/></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-edit" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_10 = '<td><input type="hidden" value=""/></td></tr>'

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10;

    $('#fish_collect_table > tbody:last').append($.parseHTML(strHTML));

    var speciesList = "#allspecies0" + rowLength;

    $.each(speciecodesList, function () {
        $(speciesList).append('<option value="' + this.showText + '"></input></option>');
        $(speciesList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    var allRows = $('#fish_collect_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#fish_collect_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(8).html('');
    }
    setForm();
}

function addRowListener() {
    $('#fish_collect_table').find('> tbody > tr').each(function () {
        $(this).change(function () {
            var tds = $(this).find('td');

            var numObserAlive = $.trim(tds.eq(1).find('input').val());
            var numObserDead = $.trim(tds.eq(2).find('input').val());
            var numColReleased = $.trim(tds.eq(3).find('input').val());
            var numColDead = $.trim(tds.eq(4).find('input').val());

            if (isEmpty(numObserAlive) || isNaN(numObserAlive)) {
                numObserAlive = 0;
            } else {
                numObserAlive = parseInt(numObserAlive);
            }
            if (isEmpty(numObserDead) || isNaN(numObserDead)) {
                numObserDead = 0;
            } else {
                numObserDead = parseInt(numObserDead);
            }
            if (isEmpty(numColReleased) || isNaN(numColReleased)) {
                numColReleased = 0;
            } else {
                numColReleased = parseInt(numColReleased);
            }
            if (isEmpty(numColDead) || isNaN(numColDead)) {
                numColDead = 0;
            } else {
                numColDead = parseInt(numColDead);
            }
            tds.eq(5).find('input').val(numObserAlive + numObserDead + numColReleased + numColDead);
        });
    });
}

function getSpecieText(listObj, specieCode) {
    var spText = '';
    listObj.find("option").each(function () {
        if ($(this).val().includes("(" + specieCode + ")")) {
            spText = $(this).val();
            return spText;
        }
    });
    return spText;
}

function setForm() {
    assignNamesToTableRow();
    addRowListener();
    addValidationListener('#edit_form');
    $.applyAwesomplete('#fish_collect_table');
}

function getFOCFormData() {
    var formData = getFormData("#edit_form");
    formData.numOfSpecies = $('#fish_collect_table').find('> tbody > tr').length;
    formData.laOffLine_Id = "#";

    return formData;
}