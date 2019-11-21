$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadTreatment();
    $('input[type="date"]').prop('max', getCurrentDateStr());
    form_original_data = $("#edit_form").serialize();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/treatments";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_continue').click(function (e) {
        e.preventDefault();

        var trId = $('#refertr_id').val();
        var redirectUrl = getBaseString() + localizedName + "/treatments/applications/primaryapp?trId=" + trId;

        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();

        $('#edit_form').validate();

        if (!validateStartEndDate()) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.start_end_date_message"), null, "displayMessage");
        } else if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;
            if ($('#refertr_id').val() != "#" && $.trim($('#refertr_id').val()).length > 0) {
                actionType = "PUT";
                actionUrl = getBaseString() + localizedName + "/api/treatments/" + $('#refertr_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName + "/api/treatments/add";
            }
            var formData = getFormData("#edit_form");

            formData.lakeId = findObjectId("#alllakes", $("#trLake").val());
            formData.streamId = findObjectId("#allstreams", $("#trStream").val());

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    $('#sampleCode').val(response.data.sample.sampleCode);
                    $('#refertr_id').val(response.data.id);
                    $('#button_continue').prop('disabled', false);
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

    $('#trLake').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("lake");
        e.preventDefault();
        var inputText = $(this).val();
        var lakeId = findObjectId("#alllakes", inputText);

        setStreamDropdown(lakeId);
    });

    $("#treatmentStart, #treatmentEnd").change(function () {
        if (!validateStartEndDate()) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.start_end_date_message"), null, "displayMessage");
            return;
        }
    });
});

function setStreamDropdown(lakeId) {
    var streamList = allthestreams[lakeId];

    $('#trStream').val('');
    $('#allstreams').empty();

    $.each(streamList, function () {
        $('#allstreams').append('<option value="' + this.showText + '"></input></option>');
        $('#allstreams').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#trStream').attr('list', $('#trStream').attr('data-list'));
    $.applyAwesomplete("#trStreamDiv");
}

function clearUpChildren(parentName) {
    switch (parentName) {
        case 'lake' :
            $('#trStream').val('');
            $('#allstreams').empty();
            break;
    }
}

function validateStartEndDate() {
    var startDateVal = $.trim($('#treatmentStart').val());
    var endDateVal = $.trim($('#treatmentEnd').val());

    if (endDateVal.length == 0) {
        return true;
    } else if (compareDate(startDateVal, endDateVal) === 1) {
        return false;
    } else
        return true;
}

function loadTreatment() {
    if (theTR != null) {
        $('#sampleCode').val(theTR.sample.sampleCode);
        if(theTR.sample.sampleStatus !== null)
           $('#sampleStatus').val(theTR.sample.sampleStatus);
        else
           $('#sampleStatus').val('DRAFT');
        $('#trLake').val(theTR.lake.showText);
        setStreamDropdown(theTR.lake.id);
        $('#trStream').val(theTR.stream.showText);
        
        if(theTR.trLogistics !== null)
        	{
			        $('#treatmentStart').val(theTR.trLogistics.treatmentStart);
			        $('#treatmentEnd').val(theTR.trLogistics.treatmentEnd);
			
			        if ((theTR.trLogistics.methodology !== undefined) && (theTR.trLogistics.methodology !== null))
			            $('#trMethodology').val(theTR.trLogistics.methodology.codePair.codeName);
			
			        $('#total_discharge').val(theTR.trLogistics.totalDischarge);
			        $('#kilo_treated').val(theTR.trLogistics.kilometerTreated);
			        $('#calendar_days').val(theTR.trLogistics.calendarDays);
        	
			        if ((theTR.trLogistics.abundanceIndex !== undefined) && (theTR.trLogistics.abundanceIndex !== null))
			            $('#trAbundanceIndex').val(theTR.trLogistics.abundanceIndex.codePair.codeName);
			
			        $('#tr_remarks').val(theTR.trLogistics.remarks);
			        $('#max_crew').val(theTR.trLogistics.maxCrewSize);
			        $('#person_days').val(theTR.trLogistics.personDays);
        	}

        $('#button_continue').prop('disabled', false);
    } else {
        $('#button_continue').prop('disabled', true);
    }
}