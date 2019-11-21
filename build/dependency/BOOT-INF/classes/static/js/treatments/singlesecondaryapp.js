$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadSecondaryApp();
    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_save').click(function (e) {
        e.preventDefault();

        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;

            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/treatments/singlesecondaryapp/" + theTrSecondApp.id;

            var formData = getFormData("#edit_form");
            getBranchToAndStationToIds(formData);

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    $('#button_induced_mortality').prop('disabled', false);
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
    $('#button_induced_mortality').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/treatments/applications/secondaryapp/singleapp/inducedmortality?trId=" + theTR.id + "&secondaryAppId=" + theTrSecondApp.id;
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });
    $('#button_return_summary').click(function (e) {
        e.preventDefault();
        confirmGoBack(form_original_data, $.getMessage("i18n.exit_warning_message"));
    });

    $("#tr_duration").keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this);
    });

    $('.tfm').change(function () {
        var num_of_bars = $.trim($("#numof_bars").val());
        var perc_ai = $.trim($("#tfm_perc_ai").find('option:selected').text());
        var weight_of_bars = $.trim($("#weightof_bars").val());
        if (weight_of_bars.length > 0 && !isEmpty(perc_ai) && !isNaN(weight_of_bars)) {
            perc_ai = parseFloat((perc_ai.split("%")[0]).split(")")[1]);
            num_of_bars = isEmpty(num_of_bars) || isNaN(num_of_bars) ? 1 : num_of_bars;
            $("#kg_ai").val(
                (parseFloat(num_of_bars) * (parseFloat(perc_ai) / 100) * parseFloat(weight_of_bars)).toFixed(2));
        } else {
            $("#kg_ai").val('');
        }
    });

    $('#trBranch_to').on("awesomplete-selectcomplete", function (e) {
        e.preventDefault();
        var inputText = $(this).val();
        var branchId = findObjectId("#allbranches", inputText);
        setStationDropdown(branchId);
    });

});

function setStationDropdown(branchId) {
    var stationList = allthestations[branchId];
    $('#trStationTo').val('');
    $('#allToStations').empty();
    $.each(stationList, function () {
        $('#allToStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allToStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#trStationTo').attr('list', $('#trStationTo').attr('data-list'));
    $.applyAwesomplete("#trStationDiv");
}

function setForm() {
    addValidationListener('#edit_form');
}

function loadSecondaryApp() {
    if (theTrSecondApp != null) {
        if (theTrSecondApp.branchLenticTo !== null)
            $('#trBranch_to').val(theTrSecondApp.branchLenticTo.showText);

        if (theTrSecondApp.stationTo !== null)
            $('#trStationTo').val(theTrSecondApp.stationTo.showText);

        if (theTrSecondApp.stationToAdjust !== null)
            $('#stationToAdjust').val(theTrSecondApp.stationToAdjust);

        if (theTrSecondApp.duration !== null)
            $('#tr_duration').val(theTrSecondApp.duration);

        if (theTrSecondApp.applicationCode !== null)
            $('#app_code').val(theTrSecondApp.applicationCode.codePair.codeName);

        if (theTrSecondApp.applicationMethod !== null)
            $('#app_method').val(theTrSecondApp.applicationMethod.codePair.codeName);

        if (theTrSecondApp.geoUTM !== null) {
            if ((theTrSecondApp.geoUTM.mapDatum !== undefined) && (theTrSecondApp.geoUTM.mapDatum !== null))
                $('#mapDatum').val(theTrSecondApp.geoUTM.mapDatum.codePair.codeName);
            else
                $('#mapDatum').val("WGS-84");

            if ((theTrSecondApp.geoUTM.utmZoneBand !== undefined) && (theTrSecondApp.geoUTM.utmZoneBand !== null))
                $('#utmZoneBand').val(theTrSecondApp.geoUTM.utmZoneBand.codePair.codeName);
            else
                $('#utmZoneBand').val("1");

            if ((theTrSecondApp.geoUTM.utmEasting01 !== undefined) && (theTrSecondApp.geoUTM.utmEasting01 !== null))
                $('#utm_e01').val(theTrSecondApp.geoUTM.utmEasting01);

            if ((theTrSecondApp.geoUTM.utmNorthing01 !== undefined) && (theTrSecondApp.geoUTM.utmNorthing01 !== null))
                $('#utm_n01').val(theTrSecondApp.geoUTM.utmNorthing01);

            if ((theTrSecondApp.geoUTM.utmEasting02 !== undefined) && (theTrSecondApp.geoUTM.utmEasting02 !== null))
                $('#utm_e02').val(theTrSecondApp.geoUTM.utmEasting02);

            if ((theTrSecondApp.geoUTM.utmNorthing02 !== undefined) && (theTrSecondApp.geoUTM.utmNorthing02 !== null))
                $('#utm_n02').val(theTrSecondApp.geoUTM.utmNorthing02);

            if ((theTrSecondApp.geoUTM.utmEasting03 !== undefined) && (theTrSecondApp.geoUTM.utmEasting03 !== null))
                $('#utm_e03').val(theTrSecondApp.geoUTM.utmEasting03);

            if ((theTrSecondApp.geoUTM.utmNorthing03 !== undefined) && (theTrSecondApp.geoUTM.utmNorthing03 !== null))
                $('#utm_n03').val(theTrSecondApp.geoUTM.utmNorthing03);

            if ((theTrSecondApp.geoUTM.utmEasting04 !== undefined) && (theTrSecondApp.geoUTM.utmEasting04 !== null))
                $('#utm_e04').val(theTrSecondApp.geoUTM.utmEasting04);

            if ((theTrSecondApp.geoUTM.utmNorthing04 !== undefined) && (theTrSecondApp.geoUTM.utmNorthing04 !== null))
                $('#utm_n04').val(theTrSecondApp.geoUTM.utmNorthing04);

            if ((theTrSecondApp.geoUTM.location !== undefined) && (theTrSecondApp.geoUTM.location !== null))
                $('#tr_location').val(theTrSecondApp.geoUTM.location);
        }
        if (theTrSecondApp.trTFM !== null) {
            $('#numof_bars').val(theTrSecondApp.trTFM.numOfBars);
            $('#weightof_bars').val(theTrSecondApp.trTFM.weightOfBars);
            //$('#tfm_perc_ai').val(theTrSecondApp.trTFM.tfmPercAI);
            if (theTrSecondApp.trTFM.tfmPercAI !== null) {
                $('#tfm_perc_ai').val(theTrSecondApp.trTFM.tfmPercAI.codePair.codeName);
            }
            $('#kg_ai').val(theTrSecondApp.trTFM.tfmKgAI);

            if ((theTrSecondApp.trTFM.tfmLPId !== undefined)
                && (theTrSecondApp.trTFM.tfmLPId !== null))
                $('#tfmlps').val(theTrSecondApp.trTFM.tfmLPId);
            $('#litersUsed').val(theTrSecondApp.trTFM.litresUsed);
        }
        if (theTrSecondApp.trGranularBayluscide !== null) {
            $('#amount_used').val(theTrSecondApp.trGranularBayluscide.amountUsed);
            //$('#gb_perc_ai').val(theTrSecondApp.trGranularBayluscide.gbPercAI);
            if (theTrSecondApp.trGranularBayluscide.gbPercAI !== null)
                $('#gb_perc_ai').val(theTrSecondApp.trGranularBayluscide.gbPercAI.codePair.codeName);
            /*if ((theTrSecondApp.trGranularBayluscide.gbLPId !== undefined) && (theTrSecondApp.trGranularBayluscide.gbLPId !== null))
                $('#gblps').val(theTrSecondApp.trGranularBayluscide.gbLPId);*/
        }
        if (theTrSecondApp.branchLenticTo !== null && theTrSecondApp.duration !== null && theTrSecondApp.applicationCode !== null)
            $('#button_induced_mortality').prop('disabled', false);
    }
}

function getBranchToAndStationToIds(theform) {
    var branchToId = "branchToId";
    var stationToId = "stationToId";

    var brId = findObjectId("#allbranches", $('#trBranch_to').val());
    theform[branchToId] = brId;

    if ($('#trStationTo').val().length > 0) {
        var sfId = findObjectId("#allToStations", $('#trStationTo').val());
        theform[stationToId] = sfId;
    }
}
