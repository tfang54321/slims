$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadSpecies();
    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    var reloading = sessionStorage.getItem("successMessage");
    if(reloading){
        sessionStorage.removeItem("successMessage");
        showSuccessMessage(reloading, null, "displayMessage");
    }

    $('#button_save').click(function (e) {
        e.preventDefault();

        var needValidate = true;
        if (!isOneEmptyRow()) {
            $('#edit_form').validate();
        } else {
            needValidate = false;
        }

        $('#edit_form').validate();

        if ((needValidate && $('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) || (!needValidate && formIsChanged('#edit_form', form_original_data))) {
            var actionType, actionUrl;

            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/aas/species/" + theAA.id;

            var formData = getFormData("#edit_form");
            getNumOfSpecies(formData);

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    sessionStorage.setItem("successMessage", response.message);
                    window.location.reload();
                    if ($('#button_save').attr('disabled') !== 'disabled')
                        $('#button_addnew_sameloc').prop('disabled', false);
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
    $('#species_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();

        if ($(this).closest("tr").prop("class") === "specie") {
            $(this).closest("tr").next().remove();
            $(this).closest("tr").remove();

            //setForm();
            var allRows = $('#species_table').children('tbody').children().not(".clickable");
            if (allRows.length === 0) {
                addRow();
            } else {
                var lastRow = $('#species_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
                if (lastRow.find('.glyphicon-plus-sign').length === 0) {
                    lastRow.children('td').eq(9).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
            setForm();
        } else if ($(this).closest("tr").prop("class") === "individual") {
            var theParent = $(this).closest("tr").parent();
            $(this).closest("tr").remove();

            if (theParent.children().length === 0) {
                addIndividualRow(theParent);
            } else {
                var lastIndividualRow = theParent.children().eq(theParent.children().length - 1);
                if (lastIndividualRow.find('.glyphicon-plus-sign').length === 0) {
                    lastIndividualRow.children('td').eq(8).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
            resetIndexToTableRow(theParent);
            setForm();
        }
    });

    $('#species_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        if ($(this).closest("tr").prop("class") === "specie") {
            addRow();
        } else if ($(this).closest("tr").prop("class") === "individual") {
            var theParent = $(this).closest("tr").parent();
            addIndividualRow(theParent);
        }
        addValidationListener('#edit_form');
    });

    $('#species_table').on('click', 'span.glyphicon-expand', function (e) {
        e.preventDefault();

        $(this).closest("tr").find('td').eq(7).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand');
        $(this).closest("tr").next().toggle();

    });

    $('#species_table').on('click', 'span.glyphicon-collapse-down', function (e) {
        e.preventDefault();

        $(this).closest("tr").find('td').eq(7).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down');
        $(this).closest("tr").next().toggle();

    });

    $('#button_end').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/adultassessments";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_addnew_sameloc').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/adultassessments/location?aaCopyId=" + theAA.id;
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });
});

function addRow() {
    var rowLength = $('#species_table').children('tbody').children().not(".clickable").length + 1;

    var cell_01 = '<tr class="specie"><td class="text-center"><input type="text" required="required" id="aaspecies02" name="aaspecies" class="input-sm" list="allspecies0' + rowLength + '" data-ap-auto-select-first="true"/><datalist id="allspecies0' + rowLength + '"></datalist></td>';
    var cell_02 = '<td class="text-center"><select class="input-sm" required="required" id="trapchamber_0' + rowLength + '"><option value="" th:utext="#{i18n.default.select}">Select One...</option></select></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" data-rule-digits="true" maxlength="4" /></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" data-rule-digits="true" maxlength="4" /></td>';
    var cell_05 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" data-rule-digits="true" maxlength="4" /></td>';
    var cell_06 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" data-rule-digits="true" maxlength="4" /></td>';
    var cell_07 = '<td class="text-center"><input type="text" class="input-sm" size="4" name="#" data-rule-digits="true" maxlength="4" readOnly="true"/></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_10 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_11 = '<td><input type="hidden" value=""/></td></tr>';

    var cell_12 = '<tr class="clickable" hidden><td colspan="9"><div class="row col-md-12 col-sm-offset-1"><table id="myid" style="width: 90%; border-collapse: separate; border-spacing: 0 5px">';
    var cell_13 = '<thead><th></th><th class="text-center">' + $.getMessage("i18n.fishsex_title") + '</th><th class="text-center">' + $.getMessage("i18n.fishlength_title") + '</th>';
    var cell_14 = '<th class="text-center">' + $.getMessage("i18n.fishweight_title") + '</th><th class="text-center">' + $.getMessage("i18n.spawning_condition_title") + '</th>';
    var cell_15 = '<th class="text-center">' + $.getMessage("i18n.specimen_state_title") + '</th><th class="text-center">' + $.getMessage("i18n.recapture_title") + '</th><th></th><th></th><th></th></thead><tbody>';

    var strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10 + cell_11 + cell_12 + cell_13 + cell_14 + cell_15 + getInnerTableRow(rowLength) + '</tbody></table></div></td></tr>';

    $('#species_table').children('tbody').last().append($.parseHTML(strHTML));

    var speciesList = "#allspecies0" + rowLength;
    var trapChamberId = "#trapchamber_0" + rowLength;

    $.each(speciecodesList, function () {
        $(speciesList).append('<option value="' + this.showText + '"></input></option>');
        $(speciesList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $.each(trapChamberList, function () {
        $(trapChamberId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    setIndividualDropdowns(rowLength);

    var allRows = $('#species_table').children('tbody').children().not(".clickable");
    if (allRows.length > 1) {
        $('#species_table').children('tbody').children().not(".clickable").eq(allRows.length - 2).find('td').eq(9).html('');
    }
    setForm();
}

function addIndividualRow(theParent) {
    var aboveRow = theParent.closest("tr").prev();
    var numTotal = aboveRow.find('td').eq(6).find('input').val();
    if (numTotal !== null && $.trim(numTotal).length > 0 && !isNaN(numTotal)) {
        if (!isOneEmptyRow() && theParent.children().length >= parseInt(numTotal)) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.overindividuals_warning_message"), null, "displayMessage");
            return;
        }
    }
    var rowLength = theParent.children().length + 1;
    var strHTML = getInnerTableRow(rowLength);

    theParent.last().append($.parseHTML(strHTML));

    setIndividualDropdowns(rowLength);

    var allRows = theParent.children();
    if (allRows.length > 1) {
        theParent.children().eq(allRows.length - 2).find('td').eq(8).html('');
    }
    resetIndexToTableRow(theParent);
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    addRowListener();
    addValidationListener('#edit_form');
    $.applyAwesomplete('#species_table');
}

function assignNamesToTableRow() {
    var specieListName = "allSpecieList";
    var specieListId = "allSpecies";
    var fishSpecieName = "fishSpecies";
    var trapchamberName = "trapChamber";
    var malesName = "males";
    var femalesName = "females";
    var aliveName = "alive";
    var deadName = "dead";
    var totalName = "total";
    var specieIdName = "specieId";

    var sexName = "fishSex";
    var indiLengthName = "indiLen";
    var indiWeightName = "indiWeight";
    var spawningConName = "spawningCon";
    var specimenName = "specimen";
    var recaptureName = "recapture";
    var individualIdName = "individualId";

    var allSpecieRows = $('#species_table').children('tbody').children().not(".clickable");

    allSpecieRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("label").attr("for", specieListId + String(i));
        tds.eq(0).find("input").first().attr("name", fishSpecieName + String(i)).attr("id", specieListId + String(i)).attr("list", specieListName + String(i));
        tds.eq(0).find("datalist").attr("id", specieListName + String(i));
        tds.eq(1).find("label").attr("for", trapchamberName + String(i));
        tds.eq(1).find("select").attr("id", trapchamberName + String(i)).attr("name", trapchamberName + String(i));
        tds.eq(2).find("label").attr("for", malesName + String(i));
        tds.eq(2).find("input").attr("name", malesName + String(i)).attr("id", malesName + String(i));
        tds.eq(3).find("label").attr("for", femalesName + String(i));
        tds.eq(3).find("input").attr("name", femalesName + String(i)).attr("id", femalesName + String(i));
        tds.eq(4).find("label").attr("for", aliveName + String(i));
        tds.eq(4).find("input").attr("name", aliveName + String(i)).attr("id", aliveName + String(i));
        tds.eq(5).find("label").attr("for", deadName + String(i));
        tds.eq(5).find("input").attr("name", deadName + String(i)).attr("id", deadName + String(i));
        tds.eq(6).find("input").attr("name", totalName + String(i));
        tds.eq(10).find("input").attr("name", specieIdName + String(i));

        var allIndiRows = $(this).next().find('tbody > tr');

        allIndiRows.each(function (j) {
            var inditds = $(this).find('td');
            inditds.eq(1).find("select").attr("id", totalName + String(i) + "_" + sexName + String(j)).attr("name", totalName + String(i) + "_" + sexName + String(j));
            inditds.eq(2).find("label").attr("for", totalName + String(i) + "_" + indiLengthName + String(j));
            inditds.eq(2).find("input").attr("name", totalName + String(i) + "_" + indiLengthName + String(j)).attr("id", totalName + String(i) + "_" + indiLengthName + String(j));
            inditds.eq(3).find("label").attr("for", totalName + String(i) + "_" + indiWeightName + String(j));
            inditds.eq(3).find("input").attr("name", totalName + String(i) + "_" + indiWeightName + String(j)).attr("id", totalName + String(i) + "_" + indiWeightName + String(j));
            inditds.eq(4).find("select").attr("id", totalName + String(i) + "_" + spawningConName + String(j)).attr("name", totalName + String(i) + "_" + spawningConName + String(j));
            inditds.eq(5).find("select").attr("id", totalName + String(i) + "_" + specimenName + String(j)).attr("name", totalName + String(i) + "_" + specimenName + String(j));
            inditds.eq(6).find("input").attr("name", totalName + String(i) + "_" + recaptureName + String(j));
            inditds.eq(9).find("input").attr("name", totalName + String(i) + "_" + individualIdName + String(j));
        });
    });
}

function getNumOfSpecies(theForm) {
    var numOfSpeciesName = "numOfSpecies";
    var numOfindividualsName = "_numOfIndi";
    var specieName = "specie";

    var allSpecieRows = $('#species_table').children('tbody').children().not(".clickable");
    theForm[numOfSpeciesName] = allSpecieRows.length;

    allSpecieRows.each(function (i) {
        var allIndiRows = $(this).next().find('tbody > tr');
        theForm[specieName + String(i) + numOfindividualsName] = allIndiRows.length;
    });
}

function loadSpecies() {
    if (theAA.species.length <= 0) {
        return;
    }

    $.each(theAA.species, function (i) {

        if ($('#species_table').children('tbody').children().not(".clickable").length !== (i + 1)) {
            addRow();
        }

        var lastRow = $('#species_table').children('tbody').children().not(".clickable").last();
        var tds = lastRow.find('td');

        tds.eq(0).find('input').first().val(getSpecieText(tds.eq(0).find('datalist'), this.speciesCode));
        if (this.trapChamber != null) {
            tds.eq(1).find('select option[value="' + this.trapChamber.codePair.codeName + '"]').prop('selected', true);
        } else {
            tds.eq(1).find('select option[value="N"]').prop('selected', true);
        }
        tds.eq(2).find('input').first().val(this.firstTimeCaptureMales);
        tds.eq(3).find('input').first().val(this.firstTimeCaptureFemales);
        tds.eq(4).find('input').first().val(this.firstTimeCaptureAlive);
        tds.eq(5).find('input').first().val(this.firstTimeCaptureDead);
        tds.eq(6).find('input').first().val(this.firstTimeCaptureTotal);
        tds.eq(10).find('input').first().val(this.id);

        var theParent = lastRow.next().find('tbody');

        if (this.fishIndividuals.length > 0) {
            var individuals = this.fishIndividuals;
            $.each(individuals, function (j) {
                if (theParent.find('tr').length !== (j + 1)) {
                    addIndividualRow(theParent);
                }
                var indiLastRow = theParent.find('tr').last();
                var indiTds = indiLastRow.find('td');
                indiTds.eq(0).find('input').val(j + 1);
                if (this.individualSex != null)
                    indiTds.eq(1).find('select option[value="' + this.individualSex.codePair.codeName + '"]').prop('selected', true);
                indiTds.eq(2).find('input').val(this.individualLength);
                indiTds.eq(3).find('input').val(this.individualWeight);
                if (this.spawningCondition != null)
                    indiTds.eq(4).find('select option[value="' + this.spawningCondition.codePair.codeName + '"]').prop('selected', true);
                if (this.specimenState != null)
                    indiTds.eq(5).find('select option[value="' + this.specimenState.codePair.codeName + '"]').prop('selected', true);
                if (this.recapture) {
                    indiTds.eq(6).find('input[value="yes"]').prop('checked', true);
                } else {
                    indiTds.eq(6).find('input[value="no"]').prop('checked', true);
                }
                indiTds.eq(9).find('input').val(this.id);
            });
        }
    });
    if ($('#button_save').attr('disabled') !== 'disabled')
        $('#button_addnew_sameloc').prop('disabled', false);
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

function setIndividualDropdowns(rowLength) {
    var sexId = "#aaFishSex_0" + rowLength;
    var spawningconId = "#aaSpawningCon_0" + rowLength;
    var specimenId = "#aaSpecimenState_0" + rowLength;

    $.each(fishSexList, function () {
        if (this.codeName === 'sex2')
            $(sexId).append($("<option />").attr('value', this.codeName).attr('selected', "selected").text(this.showText));
        else
            $(sexId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(spawConditionList, function () {
        $(spawningconId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });
    $.each(speciemenStateList, function () {
        $(specimenId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });
}

function getInnerTableRow(rowLength) {
    var cell_01 = '<tr class="individual"><td class="text-center"><input type="text" class="input-sm" value="1" name="#" readonly="true" size="1" /></td>';
    var cell_02 = '<td class="text-center"><select class="input-sm" id="aaFishSex_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="2" name="#" min="0" max="9999" data-rule-digits="true" maxlength="4" /></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="2" name="#" min="0" max="999999.9999" data-rule-number="true" maxlength="11" /></td>';
    var cell_05 = '<td class="text-center"><select class="input-sm" id="aaSpawningCon_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_06 = '<td class="text-center"><select class="input-sm" id="aaSpecimenState_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_07 = '<td class="text-center"><label class="radio-inline"><input type="radio" id="radio_yes" name="optradio_recap0' + rowLength + '" checked="checked" value="yes"/>' + $.getMessage("i18n.yes_title") + '</label><label class="radio-inline"><input type="radio" id="radio_no" name="optradio_recap0' + rowLength + '" value="no" />' + $.getMessage("i18n.no_title") + '</label></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_10 = '<td><input type="hidden" value=""/></td></tr>'

    return cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10;
}

function addRowListener() {
    $('#species_table').children('tbody').children().not(".clickable").each(function () {
        $(this).change(function () {
            var tds = $(this).find('td');

            var numObserAlive = $.trim(tds.eq(4).find('input').val());
            var numObserDead = $.trim(tds.eq(5).find('input').val());
            var isAliveEmpty = numObserAlive === '';
            var isDeadEmpty = numObserDead === '';

            if (isAliveEmpty && isDeadEmpty) {
                tds.eq(6).find('input').val('');
            } else {

                if (isAliveEmpty || isNaN(numObserAlive)) {
                    numObserAlive = 0;
                } else {
                    numObserAlive = parseInt(numObserAlive);
                }
                if (isDeadEmpty || isNaN(numObserDead)) {
                    numObserDead = 0;
                } else {
                    numObserDead = parseInt(numObserDead);
                }
                tds.eq(6).find('input').val(numObserAlive + numObserDead);
            }
        });
    });
}

function isOneEmptyRow() {
    var allRows = $('#species_table').children('tbody').children().not(".clickable");
    if (allRows.length === 1) {
        var tds = allRows.eq(0).find('td');
        if ((tds.eq(0).find('input').val() === undefined || $.trim(tds.eq(0).find('input').val()).length === 0)
            && (tds.eq(1).find('input').val() === undefined || $.trim(tds.eq(1).find('input').val()).length === 0))
            return true;
    }
    return false;
}