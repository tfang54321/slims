$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadInducedMortality();
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

        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;

            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/treatments/singlesecondaryapp/inducedmortality/" + theTrSecondApp.id;

            var formData = getFormData("#edit_form");
            getNumOfSpecies(formData);

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
    });
    $('#species_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();

        if ($(this).closest("tr").prop("class") === "specie") {
            $(this).closest("tr").next().remove();
            $(this).closest("tr").remove();

            setForm();
            var allRows = $('#species_table').children('tbody').children().not(".clickable");
            if (allRows.length == 0) {
                addRow();
            } else {
                var lastRow = $('#species_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
                if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                    lastRow.children('td').eq(4).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
        } else if ($(this).closest("tr").prop("class") === "individual") {
            var theParent = $(this).closest("tr").parent();
            $(this).closest("tr").remove();

            resetIndexToTableRow(theParent);
            setForm();

            if (theParent.children().length == 0) {
                addIndividualRow(theParent);
            } else {
                var lastIndividualRow = theParent.children().eq(theParent.children().length - 1);
                if (lastIndividualRow.find('.glyphicon-plus-sign').length == 0) {
                    lastIndividualRow.children('td').eq(3).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
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

        $(this).closest("tr").find('td').eq(2).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand');
        $(this).closest("tr").next().toggle();

    });

    $('#species_table').on('click', 'span.glyphicon-collapse-down', function (e) {
        e.preventDefault();

        $(this).closest("tr").find('td').eq(2).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down');
        $(this).closest("tr").next().toggle();

    });

    $('#button_exit').click(
        function (e) {
            e.preventDefault();
            confirmGoBack(form_original_data, $.getMessage("i18n.exit_warning_message"));
        });
});

function addRow() {
    var strHTML = '';
    var rowLength = $('#species_table').children('tbody').children().not(".clickable").length + 1;

    var cell_01 = '<tr class="specie"><td class="text-center"><input type="text" id="trspecies02" name="trspecies" class="input-sm" list="allspecies0' + rowLength + '" style="width: 100%" data-ap-auto-select-first="true"/><datalist id="allspecies0' + rowLength + '"></datalist></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class=" input-sm" size="6" name="#" value="0" data-rule-digits="true" maxlength="4" /></td>';
    var cell_03 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_06 = '<td><input type="hidden" value=""/></td></tr>'

    var cell_07 = '<tr class="clickable" hidden><td colspan="2"><table id="myid" style="width: 70%; border-collapse: separate; border-spacing: 0 5px">';
    var cell_08 = '<thead><th></th><th class="text-center">' + $.getMessage("i18n.fishlength_title") + '</th><th></th><th></th></thead><tbody>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + getInnerTableRow(rowLength) + '</tbody></table></td></tr>';

    $('#species_table').children('tbody').last().append($.parseHTML(strHTML));

    var speciesList = "#allspecies0" + rowLength;

    $.each(speciecodesList, function () {
        $(speciesList).append('<option value="' + this.showText + '"></input></option>');
        $(speciesList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    var allRows = $('#species_table').children('tbody').children().not(".clickable");
    if (allRows.length > 1) {
        $('#species_table').children('tbody').children().not(".clickable").eq(allRows.length - 2).find('td').eq(4).html('');
    }
    setForm();
}

function addIndividualRow(theParent) {
    var aboveRow = theParent.closest("tr").prev();
    var numObserved = aboveRow.find('td').eq(1).find('input').val();
    if (numObserved !== null && $.trim(numObserved).length > 0 && !isNaN(numObserved)) {
        if (theParent.children().length >= parseInt(numObserved)) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.overindividuals_warning_message"), null, "displayMessage");
            return;
        }
    }
    var strHTML = '';
    var rowLength = theParent.children().length + 1;

    strHTML = getInnerTableRow(rowLength);

    theParent.last().append($.parseHTML(strHTML));

    var allRows = theParent.children();
    if (allRows.length > 1) {
        theParent.children().eq(allRows.length - 2).find('td').eq(3).html('');
    }
    resetIndexToTableRow(theParent);
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    addValidationListener('#edit_form');
    $.applyAwesomplete('#species_table');
}

function assignNamesToTableRow() {
    var specieListName = "allSpecieList";
    var specieListId = "allSpecies";
    var fishSpecieName = "fishSpecies";
    var observeName = "observed";
    var specieIdName = "specieId";
    var indiLengthName = "indiLen";
    var indiIdName = "indiId";

    var allSpecieRows = $('#species_table').children('tbody').children().not(".clickable");

    allSpecieRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("input").first().attr("name", fishSpecieName + String(i)).attr("id", specieListId + String(i)).attr("list", specieListName + String(i));
        tds.eq(0).find("datalist").attr("id", specieListName + String(i));

        tds.eq(1).find("label").attr("for", observeName + String(i));
        tds.eq(1).find("input").attr("name", observeName + String(i)).attr("id", observeName + String(i));
        tds.eq(5).find("input").attr("name", specieIdName + String(i));

        var allIndiRows = $(this).next().find('tbody > tr');

        allIndiRows.each(function (j) {
            var inditds = $(this).find('td');
            inditds.eq(1).find("label").attr("for", observeName + String(i) + "_" + indiLengthName + String(j));
            inditds.eq(1).find("input").attr("name", observeName + String(i) + "_" + indiLengthName + String(j)).attr("id", observeName + String(i) + "_" + indiLengthName + String(j));
            inditds.eq(4).find("input").attr("name", observeName + String(i) + "_" + indiIdName + String(j));
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

function loadInducedMortality() {
    if (theTrSecondApp.trSecondAppInducedMortality != null) {
        if (theTrSecondApp.trSecondAppInducedMortality.collectCondition !== null) {
            if (theTrSecondApp.trSecondAppInducedMortality.collectCondition == "good")
                $('#radio_col_good').prop("checked", true);
            else if (theTrSecondApp.trSecondAppInducedMortality.collectCondition == "fair")
                $('#radio_col_fair').prop("checked", true);
            else if (theTrSecondApp.trSecondAppInducedMortality.collectCondition == "poor")
                $('#radio_col_poor').prop("checked", true);
        }

        if (theTrSecondApp.trSecondAppInducedMortality.larvae !== null)
            $('#larvae_est').val(theTrSecondApp.trSecondAppInducedMortality.larvae.codePair.codeName);

        if (theTrSecondApp.trSecondAppInducedMortality.transformers !== null)
            $('#transformers_est').val(theTrSecondApp.trSecondAppInducedMortality.transformers.codePair.codeName);

        if (theTrSecondApp.trSecondAppInducedMortality.adults !== null)
            $('#tr_adults').val(theTrSecondApp.trSecondAppInducedMortality.adults.codePair.codeName);

        if (theTrSecondApp.trSecondAppInducedMortality.remarks !== null)
            $('#tr_remarks').val(theTrSecondApp.trSecondAppInducedMortality.remarks);

        if (theTrSecondApp.trSecondAppInducedMortality.species.length > 0) {
            $.each(theTrSecondApp.trSecondAppInducedMortality.species, function (i) {

                if ($('#species_table').children('tbody').children().not(".clickable").length != (i + 1)) {
                    addRow();
                }

                var lastRow = $('#species_table').children('tbody').children().not(".clickable").last();
                var tds = lastRow.find('td');

                tds.eq(0).find('input').first().val(getSpecieText(tds.eq(0).find('datalist'), this.speciesCode));
                tds.eq(1).find('input').first().val(this.totalObserved);
                tds.eq(5).find('input').first().val(this.id);

                var theParent = lastRow.next().find('tbody');

                if (this.fishIndividuals.length > 0) {
                    var individuals = this.fishIndividuals;
                    $.each(individuals, function (j) {

                        if (theParent.find('tr').length != (j + 1)) {
                            addIndividualRow(theParent);
                        }
                        var indiLastRow = theParent.find('tr').last();
                        var indiTds = indiLastRow.find('td');
                        indiTds.eq(0).find('input').val(j + 1);
                        indiTds.eq(1).find('input').val(this.individualLength);
                    });
                }
            });
        }
    }
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

function getInnerTableRow(rowLength) {
    var strHTML = '';

    var cell_01 = '<tr class="individual"><td class="text-center"><input type="text" class="input-sm" value="1" name="#" readonly="true" size="2" /></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" value="" name="#" size="2" /></td>';
    var cell_03 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_05 = '<td><input type="hidden" value=""/></td></tr>'

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05;

    return strHTML;
}