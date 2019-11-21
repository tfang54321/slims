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
    if (reloading) {
        sessionStorage.removeItem("successMessage");
        showSuccessMessage(reloading, null, "displayMessage");
    }

    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();
        if (!verifyTotalIndividuals()) {
            return;
        } else if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getIndiFormData("#edit_form");

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineIndi(formData);
                }
            });
        }
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;
        actionType = "PUT";
        actionUrl = getBaseString() + localizedName + "/api/las/fishindividual/" + theSpecie.id;

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

    function createOfflineIndi(dataJSON) {
        return new Promise(function (doReturn) {

            var offlineIndiId = getOfflineId();
            offlineIndiId = offlineIndiId.substring(0, offlineIndiId.indexOf('#'));
            //dataJSON.laOffLine_Id = getOfflineId();
            dataJSON.laOffLine_Id = offlineIndiId;

            $.db_insertOrUpdate('larval_assessments', dataJSON).then(function (result) {
                if (result) {
                    form_original_data = $("#edit_form").serialize();
                    sessionStorage.setItem("successMessage", response.message);
                    window.location.reload();
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

    function loadOnlineData() {
        if (theSpecie != null && theSpecie.fishIndividuals.length > 0) {
            $.each(theSpecie.fishIndividuals, function (i) {
                if ($('#fish_individuals_table').find('> tbody > tr').length !== (i + 1)) {
                    addRow();
                }
                var lastRow = $('#fish_individuals_table').find('> tbody > tr').last();
                var tds = lastRow.find('td');

                tds.eq(0).find('input').val(this.individualLength);
                tds.eq(1).find('input').val(this.individualWeight);
                tds.eq(2).find('select option[value="' + this.specimenState.codePair.codeName + '"]').prop('selected', true);
                tds.eq(3).find('input').val(this.conditionfactor);
                tds.eq(4).find('select option[value="' + this.individualSex.codePair.codeName + '"]').prop('selected', true);

                if (this.recapture) {
                    tds.eq(5).find('input[value="yes"]').prop('checked', true);
                } else {
                    tds.eq(5).find('input[value="no"]').prop('checked', true);
                }
                tds.eq(8).find('input').val(this.id);
            });
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        if (hasOfflineId()) {
            var offlineIndiId = getOfflineId();
            offlineIndiId = offlineIndiId.substring(0, offlineIndiId.indexOf('#'));
            var offlineLAId = offlineIndiId.substring(0, offlineIndiId.indexOf("-INDIS"));

            $.db_getByKeyPath('larval_assessments', offlineLAId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                }
            });
            $.db_getByKeyPath('larval_assessments', offlineIndiId).then(function (result) {
                if (result) {
                    var numOfIndividuals = result.numOfIndividuals;
                    var i;
                    for (i = 0; i < numOfIndividuals; i++) {
                        if ($('#fish_individuals_table').find('> tbody > tr').length != (i + 1)) {
                            addRow();
                        }
                        var lastRow = $('#fish_individuals_table').find('> tbody > tr').last();
                        var tds = lastRow.find('td');

                        tds.eq(0).find('input').val(result["fishLength" + String(i)]);
                        tds.eq(1).find('input').val(result["fishWeight" + String(i)]);
                        tds.eq(2).find('select option[value="' + result["specimen" + String(i)] + '"]').prop('selected', true);
                        tds.eq(3).find('input').val(result["conFactor" + String(i)]);
                        tds.eq(4).find('select option[value="' + result["fishSex" + String(i)] + '"]').prop('selected', true);

                        if (result["recapture" + String(i)] === "yes") {
                            tds.eq(5).find('input[value="yes"]').prop('checked', true);
                        } else {
                            tds.eq(5).find('input[value="no"]').prop('checked', true);
                        }
                    }
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }

    $('#button_return_summary').click(function (e) {
        e.preventDefault();
        confirmGoBack(form_original_data, $.getMessage("i18n.exit_warning_message"));
    });

    $('#fish_individuals_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
        addValidationListener('#edit_form');
    });

    $('#fish_individuals_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#fish_individuals_table').find('> tbody > tr');
        if (allRows.length === 0) {
            addRow();
        } else {
            var lastRow = $('#fish_individuals_table').find('> tbody > tr').eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length === 0) {
                lastRow.children('td').eq(7).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    function verifyTotalIndividuals() {
        var allRows = $('#fish_individuals_table').find('> tbody > tr');
        
        var hash = window.location.hash;
        if (hash.indexOf('#') !== -1) {
            var totalFish;
            totalFish = hash.substring(hash.lastIndexOf('#') + 1);

            if (allRows.length < totalFish) {
                $("#displayMessage").html("");
                showWarningMessage($.getMessage("i18n.less_fishindividuals_message"), null, "displayMessage");
                return true;
            }
            if (allRows.length > totalFish) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("i18n.toomany_fishindividuals_message"), null, "displayMessage");
                return false;
            } else if (allRows.length > 25) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("i18n.max_fishindividuals_message"), null, "displayMessage");
                return false;
            } else {
                return true;
            }
        } else {
            if (allRows.length < theSpecie.total) {
                $("#displayMessage").html("");
                showWarningMessage($.getMessage("i18n.less_fishindividuals_message"), null, "displayMessage");
                return true;
            }
            if (allRows.length > theSpecie.total) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("i18n.toomany_fishindividuals_message"), null, "displayMessage");
                return false;
            } else if (allRows.length > 25) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("i18n.max_fishindividuals_message"), null, "displayMessage");
                return false;
            } else {
                return true;
            }
        }

    }

});

function assignNamesToTableRow() {
    var lengthName = "fishLength";
    var weightName = "fishWeight";
    var specimenName = "specimen";
    var conFactorName = "conFactor";
    var sexName = "fishSex";
    var recaptureName = "recapture";
    var individualIdName = "individualId";

    var allRows = $('#fish_individuals_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("label").attr("for", lengthName + String(i));
        tds.eq(0).find("input").attr("name", lengthName + String(i)).attr("id", lengthName + String(i));
        tds.eq(1).find("label").attr("for", weightName + String(i));
        tds.eq(1).find("input").attr("name", weightName + String(i)).attr("id", weightName + String(i));
        tds.eq(2).find("select").attr("id", specimenName + String(i)).attr("name", specimenName + String(i));
        tds.eq(3).find("input").attr("name", conFactorName + String(i));
        tds.eq(4).find("select").attr("id", sexName + String(i)).attr("name", sexName + String(i));
        tds.eq(5).find("input").attr("name", recaptureName + String(i));
        tds.eq(8).find("input").attr("name", individualIdName + String(i));
    });
}

function addRow() {
    var strHTML = '';
    var rowLength = $('#fish_individuals_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="6" name="#" placeholder="" data-rule-number="true" maxlength="4" oninput="validateNumber(this)"/></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="6" name="#" placeholder="" data-rule-number="true" max="99999999.9999" maxlength="13" oninput="validateNumber(this)"/></td>';
    var cell_03 = '<td class="text-center"><select class="input-sm" style="width: 100%" id="laSpecimenState_0' + rowLength + '"></option></select></td>';
    var cell_04 = '<td class="text-center"><input type="text" class="input-sm" size="8" name="#" placeholder="" value="0" readOnly="true"/></td>';
    var cell_05 = '<td class="text-center"><select class="input-sm" style="width: 100%" id="laFishSex_0' + rowLength + '"><option value="selectone">Select One...</option></option></select></td>';
    var cell_06 = '<td class="text-center"><label class="radio-inline"><input type="radio" id="radio_yes" name="optradio_recap0' + rowLength + '" value="yes"/>' + $.getMessage("i18n.yes_title") + '</label><label class="radio-inline"><input type="radio" id="radio_no" name="optradio_recap0' + rowLength + '" checked="checked" value="no" />' + $.getMessage("i18n.no_title") + '</label></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td><input type="hidden" value=""/></td></tr>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09;
    $('#fish_individuals_table > tbody:last').append($.parseHTML(strHTML));

    var specimenId = "#laSpecimenState_0" + rowLength;
    var sexId = "#laFishSex_0" + rowLength;
    $.each(speciemenStateList, function () {
        $(specimenId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(fishSexList, function () {
        if (this.codeName === 'sex2')
            $(sexId).append($("<option />").attr('value', this.codeName).attr('selected', "selected").text(this.showText));
        else
            $(sexId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    var allRows = $('#fish_individuals_table').find('> tbody > tr');
    if (allRows.length > 1) {
        $('#fish_individuals_table').find('> tbody > tr').eq(allRows.length - 2).find('td').eq(7).html('');
    }
    setForm();
}

function addRowListener() {
    $('#fish_individuals_table').find('> tbody > tr').each(function () {
        $(this).change(function () {
            var tds = $(this).find('td');

            var length = $.trim(tds.eq(0).find('input').val());
            var weight = $.trim(tds.eq(1).find('input').val());

            if (isNaN(length)) {
                length = 0;
            } else {
                length = parseFloat(length);
            }
            if (isNaN(weight)) {
                weight = 0;
            } else {
                weight = parseFloat(weight);
            }
            if (length !== 0) {
                var cf = ((weight / (Math.pow(length, 3))) * 1000000).toFixed(3);
                if (isNaN(cf)) {
                    cf = ""
                }
                tds.eq(3).find('input').val(cf);
            }
        });
    });
}

function setForm() {
    assignNamesToTableRow();
    addRowListener();
    addValidationListener('#edit_form');
    $(this).trigger('wb-ready.wb');
}

function getIndiFormData() {
    var formData = getFormData("#edit_form");
    formData.numOfIndividuals = $('#fish_individuals_table').find('> tbody > tr').length;
    formData.laOffLine_Id = "#";

    return formData;
}