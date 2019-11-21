$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;

    loadAttachments();

    var reloading = sessionStorage.getItem("successMessage");
    if(reloading){
        sessionStorage.removeItem("successMessage");
        showSuccessMessage(reloading, null, "displayMessage");
    }
    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_save').click(function (e) {
        e.preventDefault();

        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;

            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/pcs/attachments/" + thePC.id;

            var formData = getFormData("#edit_form");
            getNumOfAttachments(formData);

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    sessionStorage.setItem("successMessage", response.message);
                    window.location.reload();
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

    $('#attachments_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();

        if ($(this).closest("tr").prop("class") === "attachment") {
            $(this).closest("tr").next().remove();
            $(this).closest("tr").remove();

            setForm();
            var allRows = $('#attachments_table').children('tbody').children().not(".clickable");
            if (allRows.length == 0) {
                addRow();
            } else {
                var lastRow = $('#attachments_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
                if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                    lastRow.children('td').eq(8).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
        } else if ($(this).closest("tr").prop("class") === "individual") {
            var theParent = $(this).closest("tr").parent();
            $(this).closest("tr").next().remove();
            $(this).closest("tr").remove();

            resetIndividulsIndex(theParent);
            setForm();

            if (theParent.children().length == 0) {
                addIndividualRow(theParent);
            } else {
                var lastIndividualRow = theParent.children(".individual").eq(theParent.children(".individual").length - 1);
                if (lastIndividualRow.find('.glyphicon-plus-sign').length == 0) {
                    lastIndividualRow.children('td').eq(4).html('<a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
        }
    });

    $('#attachments_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        if ($(this).closest("tr").prop("class") === "attachment") {
            addRow();
        } else if ($(this).closest("tr").prop("class") === "individual") {
            var theParent = $(this).closest("tr").parent();
            addIndividualRow(theParent);
        }
        //addValidationListener('#edit_form');
    });

    $('#attachments_table').on('click', 'span.glyphicon-expand', function (e) {
        e.preventDefault();
        if ($(this).closest("tr").prop("class") === "attachment") {
            $(this).closest("tr").find('td').eq(6).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand');
            $(this).closest("tr").next().toggle();
        } else if ($(this).closest("tr").prop("class") === "individual") {
            $(this).closest("tr").find('td').eq(2).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand');
            $(this).closest("tr").next().toggle();
        }
    });

    $('#attachments_table').on('click', 'span.glyphicon-collapse-down', function (e) {
        e.preventDefault();

        if ($(this).closest("tr").prop("class") === "attachment") {
            $(this).closest("tr").find('td').eq(6).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down');
            $(this).closest("tr").next().toggle();
        } else if ($(this).closest("tr").prop("class") === "individual") {
            $(this).closest("tr").find('td').eq(2).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down');
            $(this).closest("tr").next().toggle();
        }
    });

    $('#button_exit').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/parasiticcollections";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });
});

function addRow() {
    var strHTML = '';
    //var rowLength = $('#attachments_table').children('tbody').children().not(".clickable").length + 1;
    var rowLength = $('#attachments_table').children('tbody').children(".attachment").length + 1;

    var cell_01 = '<tr class="attachment"><td class="text-center"><label for="#"></label><input type="text" class="input-sm" required="required" size="4" name="#" value="" data-rule-digits="true" maxlength="5" /></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><select class="input-sm" required="required" id="hosttype_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_03 = '<td class="text-center"><input type="text" id="thespecies02" name="thespecies" class="input-sm" list="theallspecies0' + rowLength + '" data-ap-auto-select-first="true"/><datalist id="theallspecies0' + rowLength + '"></datalist></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" value="0" data-rule-digits="true" maxlength="4" /></td>';
    var cell_05 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" value="0" data-rule-digits="true" maxlength="4" /></td>';
    var cell_06 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="4" name="#" value="0" data-rule-digits="true" maxlength="4" /></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_10 = '<td><input type="hidden" value=""/></td></tr>'

    var cell_11 = '<tr class="clickable" hidden><td colspan="5"><div class="row col-md-12 col-sm-offset-1">';
    var cell_12 = '<table id="individual_table" style="width: 90%; border-collapse: separate; border-spacing: 0 5px"><thead><tr><th class="text-center">' + $.getMessage("i18n.individuals_title") + '</th><th class="text-center">' + $.getMessage("i18n.species_code_title") + '</th><th></th><th></th><th></th><th></th></tr>';
    var cell_13 = '<tr><th></th><th class="text-center required"' + $.getMessage("i18n.species_code_title") + '</th><th></th><th></th><th></th><th></th></tr></thead><tbody>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10 + cell_11 + cell_12 + cell_13 + getIndividualRowStr(rowLength) + getIndividualDetailStr(rowLength) + '</tbody></table></div></td></tr>';

    $('#attachments_table').children('tbody').last().append($.parseHTML(strHTML));

    var speciesList = "#theallspecies0" + rowLength;
    var hostTypes = "#hosttype_0" + rowLength;

    $.each(speciecodesList, function () {
        $(speciesList).append('<option value="' + this.showText + '"></input></option>');
        $(speciesList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $.each(hostTypeList, function () {
        $(hostTypes).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    setIndividualDropdowns(rowLength);

    var allRows = $('#attachments_table').children('tbody').children(".attachment");
    if (allRows.length > 1) {
        $('#attachments_table').children('tbody').children(".attachment").eq(allRows.length - 2).find('td').eq(8).html('');
    }
    setForm();
}

function addIndividualRow(theParent) {
    var strHTML = '';
    var rowLength = theParent.children(".individual").length + 1;

    strHTML = getIndividualRowStr(rowLength) + getIndividualDetailStr(rowLength);

    theParent.last().append($.parseHTML(strHTML));

    setIndividualDropdowns(rowLength);

    var allRows = theParent.children(".individual");
    if (allRows.length > 1) {
        theParent.children(".individual").eq(allRows.length - 2).find('td').eq(4).html('');
    }
    resetIndividulsIndex(theParent);
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    //addRowListener();
    addValidationListener('#edit_form');
    $.applyAwesomplete('#attachments_table');
}

function assignNamesToTableRow() {
    var idNumName = "idNumber";
    var hostTypeName = "hostType";
    var hostSpecieName = "hostSpecie";
    var specieListName = "allSpecieList";
    var specieListId = "allSpecies";
    var seaLampSampledName = "seaLampSampled";
    var silverLampSampledName = "silverLampSampled";
    var totalAttachedName = "totalAttached";
    var attachmentIdName = "attachmentId";

    var indiSpecieName = "indiSpecie";
    var indiSpecieListName = "indiSpecieList";
    var indiSpecieListId = "indiSpecies";
    var individualIdName = "individualId";

    var allAttachmentRows = $('#attachments_table').children('tbody').children('.attachment');

    allAttachmentRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("label").attr("for", idNumName + String(i));
        tds.eq(0).find("input").attr("name", idNumName + String(i)).attr("id", idNumName + String(i));
        tds.eq(1).find("label").attr("for", hostTypeName + String(i));
        tds.eq(1).find("select").attr("id", hostTypeName + String(i)).attr("name", hostTypeName + String(i));
        tds.eq(2).find("input").first().attr("name", hostSpecieName + String(i)).attr("id", specieListId + String(i)).attr("list", specieListName + String(i));
        tds.eq(2).find("datalist").attr("id", specieListName + String(i));
        tds.eq(3).find("label").attr("for", seaLampSampledName + String(i));
        tds.eq(3).find("input").attr("name", seaLampSampledName + String(i)).attr("id", seaLampSampledName + String(i));
        tds.eq(4).find("label").attr("for", silverLampSampledName + String(i));
        tds.eq(4).find("input").attr("name", silverLampSampledName + String(i)).attr("id", silverLampSampledName + String(i));
        tds.eq(5).find("label").attr("for", totalAttachedName + String(i));
        tds.eq(5).find("input").attr("name", totalAttachedName + String(i)).attr("id", totalAttachedName + String(i));

        tds.eq(9).find("input").attr("name", attachmentIdName + String(i));

        var allIndiRows = $(this).next().find('tbody').first().children('.individual');

        allIndiRows.each(function (j) {
            var inditds = $(this).find('td');
            inditds.eq(1).find("label").attr("for", idNumName + String(i) + "_" + indiSpecieListId + String(j));
            inditds.eq(1).find("input").first().attr("name", idNumName + String(i) + "_" + indiSpecieName + String(j)).attr("id", idNumName + String(i) + "_" + indiSpecieListId + String(j)).attr("list", idNumName + String(i) + "_" + indiSpecieListName + String(j));
            inditds.eq(1).find("datalist").attr("id", idNumName + String(i) + "_" + indiSpecieListName + String(j));
            inditds.eq(5).find("input").attr("name", idNumName + String(i) + "_" + individualIdName + String(j));

            $(this).next().find('input, select, label').each(function () {
                var currentName, namePrefix;

                if ($(this)[0].tagName.toLowerCase() === 'label' && $(this)[0].hasAttribute('for')) {
                    currentName = $(this).attr("for");
                    if (currentName !== undefined && currentName !== null) {
                        namePrefix = currentName.substring(0, currentName.indexOf("_") + 1);
                        $(this).attr("for", namePrefix + String(i) + String(j));
                    }
                } else {
                    currentName = $(this).attr("name");
                    if (currentName !== undefined && currentName !== null) {
                        namePrefix = currentName.substring(0, currentName.indexOf("_") + 1);
                        $(this).attr("name", namePrefix + String(i) + String(j));
                        $(this).attr("id", namePrefix + String(i) + String(j));
                    }
                }
            });
        });
    });
}

function getNumOfAttachments(theForm) {
    var numOfAttachmentsName = "numOfAttachments";
    var numOfindividualsName = "_numOfIndi";
    var attachmentName = "attachment";

    var allAttachmentRows = $('#attachments_table').children('tbody').children(".attachment");
    theForm[numOfAttachmentsName] = allAttachmentRows.length;

    allAttachmentRows.each(function (i) {
        var allIndiRows = $(this).next().find('tbody').first().children('.individual');
        theForm[attachmentName + String(i) + numOfindividualsName] = allIndiRows.length;
    });
}

function loadAttachments() {

    if (thePC !== null && thePC.parasiticAttachments.length > 0) {
        $.each(thePC.parasiticAttachments, function (i) {

            if ($('#attachments_table').children('tbody').children(".attachment").length != (i + 1)) {
                addRow();
            }

            var lastRow = $('#attachments_table').children('tbody').children(".attachment").last();
            var tds = lastRow.find('td');

            tds.eq(0).find('input').first().val(this.idNumber);
            if(this.lampreysattachedto != undefined  && this.lampreysattachedto != null)
                tds.eq(1).find('select option[value="' + this.lampreysattachedto.codePair.codeName + '"]').prop('selected', true);
            tds.eq(2).find('input').first().val(getSpecieText(tds.eq(2).find('datalist'), this.hostSpecies));
            tds.eq(3).find('input').first().val(this.seaLampreySampled);
            tds.eq(4).find('input').first().val(this.silverLampreySampled);
            tds.eq(5).find('input').first().val(this.totalAttached);
            tds.eq(9).find('input').first().val(this.id);

            var theParent = lastRow.next().find('tbody');

            if (this.fishIndividuals.length > 0) {
                var individuals = this.fishIndividuals;
                $.each(individuals, function (j) {

                    if (theParent.find('tr.individual').length != (j + 1)) {
                        addIndividualRow(theParent);
                    }
                    var indiLastRow = theParent.find('tr.individual').last();
                    var indiTds = indiLastRow.find('td');
                    indiTds.eq(0).find('input').val(j + 1);
                    indiTds.eq(1).find('input').first().val(getSpecieText(indiTds.eq(1).find('datalist'), this.specieCode));
                    indiTds.eq(5).find('input').val(this.id);

                    var detailRow = indiLastRow.next();
                    var allFields = detailRow.find('input, select');

                    allFields.eq(0).val(this.individualLength);
                    allFields.eq(1).val(this.individualWeight);

                    if (this.individualSex !== null)
                        allFields.eq(2).val(this.individualSex.codePair.codeName);

                    if (this.individualMaturity !== null)
                        allFields.eq(3).val(this.individualMaturity.codePair.codeName);

                    if (this.conditionOfGut !== null)
                        allFields.eq(4).val(this.conditionOfGut.codePair.codeName);

                    if (this.fullnessOfGut !== null)
                        allFields.eq(5).val(this.fullnessOfGut.codePair.codeName);

                    if (this.contentsOfGut !== null)
                        allFields.eq(6).val(this.contentsOfGut.codePair.codeName);

                    if (this.specimenState !== null)
                        allFields.eq(7).val(this.specimenState.codePair.codeName);

                    if (this.recapture)
                        allFields.eq(8).prop('checked', true);
                    else
                        allFields.eq(9).prop('checked', true);
                });
            }
        });
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

function setIndividualDropdowns(rowLength) {
    var speciesId = "#allspecies0" + rowLength;
    var sexId = "#pcsex_0" + rowLength;
    var maturityId = "#maturity_0" + rowLength;
    var gutConditionId = "#gutcondition_0" + rowLength;
    var gutFullnessId = "#gutfullness_0" + rowLength;
    var gutContentsId = "#gutcontents_0" + rowLength;
    var specimenId = "#specimen_0" + rowLength;

    $.each(speciecodesList, function () {
        $(speciesId).append('<option value="' + this.showText + '"></input></option>');
        $(speciesId).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $.each(fishSexList, function () {
        if (this.codeName == 'sex2')
            $(sexId).append($("<option />").attr('value', this.codeName).attr('selected', "selected").text(this.showText));
        else
            $(sexId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(spawConditionList, function () {
        $(maturityId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(gutConditionList, function () {
        $(gutConditionId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(gutFullNessList, function () {
        $(gutFullnessId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(gutContentsList, function () {
        $(gutContentsId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(speciemenStateList, function () {
        $(specimenId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });
}

function getIndividualRowStr(rowLength) {
    var strHTML = '';

    var cell_01 = '<tr class="individual"><td class="text-center"><input type="text" class="input-sm" value="1" name="#" readonly="true" size="1" /></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" required="required" id="pcspecies2" name="pcspecies" class="input-sm" list="allspecies0' + rowLength + '" data-ap-auto-select-first="true"/><datalist id="allspecies0' + rowLength + '"></datalist></td>';
    var cell_03 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_06 = '<td><input type="hidden" value=""/></td></tr>'

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06;

    return strHTML;
}

function getIndividualDetailStr(rowLength) {
    var strHTML = '';

    var cell_01 = '<tr class="clickable02" hidden><td colspan="5"><div class="col-md-12 col-sm-offset-1"><div class="col-md-4 mrgn-bttm-md"><label for="length_">' + $.getMessage("i18n.length_title") + '</label><br/><input type="text" class="input-sm" size="4" name="length_" placeholder="" min="0" value="0" max="9999" data-rule-digits="true" maxlength="4" /><label>&nbsp;mm</label></div>';
    var cell_02 = '<div class="col-md-4 mrgn-bttm-md"><label for="weight_"><span class="field-name">' + $.getMessage("i18n.weight_title") + '</span></label><br/><input type="text" class="input-sm" size="4" name="weight_" placeholder="" min="0" value="0" max="9999" data-rule-digits="true" maxlength="4" /><label>&nbsp;g</label></div>';
    var cell_03 = '<div class="col-md-4 mrgn-bttm-md"><label><span class="field-name">' + $.getMessage("i18n.fishsex_title") + '</span></label><br/><select class="input-sm" name="sex_" id="pcsex_0' + rowLength + '"><option value=""></option></select></div>';
    var cell_04 = '<div class="col-md-4 mrgn-bttm-md"><label><span class="field-name">' + $.getMessage("i18n.maturity_title") + '</span></label><br/><select class="input-sm" name="maturity_" id="maturity_0' + rowLength + '"><option value=""></option></select></div>';
    var cell_05 = '<div class="col-md-4 mrgn-bttm-md"><label><span class="field-name">' + $.getMessage("i18n.gut_condition_title") + '</span></label><br/><select class="input-sm" name="gutCondition_" id="gutcondition_0' + rowLength + '"><option value=""></option></select></div>';
    var cell_06 = '<div class="col-md-4 mrgn-bttm-md"><label><span class="field-name">' + $.getMessage("i18n.gut_fullness_title") + '</span></label><br/><select class="input-sm" name="gutFullness_" id="gutfullness_0' + rowLength + '"><option value=""></option></select></div>';
    var cell_07 = '<div class="col-md-4 mrgn-bttm-md"><label><span class="field-name">' + $.getMessage("i18n.gut_contents_title") + '</span></label><br/><select class="input-sm" name="gutContents_" id="gutcontents_0' + rowLength + '"><option value=""></option></select></div>';
    var cell_08 = '<div class="col-md-4 mrgn-bttm-md"><label><span class="field-name">' + $.getMessage("i18n.specimen_state_title") + '</span></label><br/><select class="input-sm" name="specimenState_" id="specimen_0' + rowLength + '"><option value=""></option></select></div>';
    var cell_09 = '<div class="col-md-4"><label><span class="field-name">' + $.getMessage("i18n.recapture_title") + '</span></label><br/><label class="radio-inline"><input type="radio" id="radio_yes" name="optradioRecap_' + rowLength + '" checked="checked" value="yes"/>' + $.getMessage("i18n.yes_title") + '</label><label class="radio-inline"><input type="radio" id="radio_no" name="optradioRecap_' + rowLength + '" value="no"/>' + $.getMessage("i18n.no_title") + '</label></div></div></td></tr>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09;

    return strHTML;
}

/*function addRowListener() {
	$('#species_table').children('tbody').children().not(".clickable").each(function () {
      $(this).change(function () {
          var tds = $(this).find('td');

          var numObserAlive = $.trim(tds.eq(4).find('input').val());
          var numObserDead = $.trim(tds.eq(5).find('input').val());
          
          if (isNaN(numObserAlive)) {
              numObserAlive = 0;
          }
          else {
              numObserAlive = parseInt(numObserAlive);
          }
          if (isNaN(numObserDead)) {
              numObserDead = 0;
          }
          else {
              numObserDead = parseInt(numObserDead);
          }
          
          tds.eq(6).find('input').val(numObserAlive + numObserDead);
      });
  });
}*/

function resetIndividulsIndex(inputObj) {

    var allRows = inputObj.children(".individual");

    allRows.each(function (i) {
        var tds = $(this).find('td');
        tds.eq(0).find("input").val(i + 1);
    });
}