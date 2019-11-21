$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadAA();

    $('input[type="date"]').prop('max', getCurrentDateStr());
    form_original_data = $("#edit_form").serialize();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/adultassessments";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_continue').click(function (e) {
        e.preventDefault();

        var aaId = $('#referaa_id').val();
        var redirectUrl = getBaseString() + localizedName + "/adultassessments/main?aaId=" + aaId;

        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;
            if ($('#referaa_id').val() != "#" && $.trim($('#referaa_id').val()).length > 0) {
                actionType = "PUT";
                actionUrl = getBaseString() + localizedName + "/api/aas/" + $('#referaa_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName + "/api/aas/add";
            }
            var formData = getFormData("#edit_form");

            formData.lakeId = findObjectId("#alllakes", $("#aaLake").val());
            formData.streamId = findObjectId("#allstreams", $("#aaStream").val());
            formData.branchId = findObjectId("#allbranches", $("#aaBranch").val());
            formData.stationFromId = findObjectId("#allFromStations", $("#aaStationFrom").val());

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    $('#sampleCode').val(response.data.sample.sampleCode);
                    $('#referaa_id').val(response.data.id);
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

    $('#aaLake').on("awesomplete-selectcomplete", function (e) {
            clearUpChildren("lake");
            e.preventDefault();
            var inputText = $(this).val();
            var lakeId = findObjectId("#alllakes", inputText);
            setStreamDropdown(lakeId);
    });

    $('#aaStream').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("stream");
        e.preventDefault();
        var inputText = $(this).val();
        var streamId = findObjectId("#allstreams", inputText);
        setBranchLenticDropdown(streamId);
    });
    $('#aaBranch').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("branch");
        e.preventDefault();
        var inputText = $(this).val();
        var branchId = findObjectId("#allbranches", inputText);
        setStationDropdown(branchId);
    });

    $('#timeOfCheck').keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this)
    });
});

function setStreamDropdown(lakeId) {
    var streamList = allthestreams[lakeId];

    $('#aaStream').val('');
    $('#allstreams').empty();

    $.each(streamList, function () {
        $('#allstreams').append('<option value="' + this.showText + '"></input></option>');
        $('#allstreams').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#aaStream').attr('list', $('#aaStream').attr('data-list'));
    $.applyAwesomplete("#aaStreamDiv");
}

function setBranchLenticDropdown(streamId) {
    var branchLenticList = allthebranches[streamId];
    $('#aaBranch').val('');
    $('#allbranches').empty();
    $.each(branchLenticList, function () {
        $('#allbranches').append('<option value="' + this.showText + '"></input></option>');
        $('#allbranches').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#aaBranch').attr('list', $('#aaBranch').attr('data-list'));
    $.applyAwesomplete("#aaBranchDiv");
}

function setStationDropdown(branchId) {
    var stationList = allthestations[branchId];
    $('#aaStationFrom').val('');
    $('#allFromStations').empty();
    $.each(stationList, function () {
        $('#allFromStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allFromStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#aaStationFrom').attr('list', $('#aaStationFrom').attr('data-list'));
    $.applyAwesomplete("#aaStationFromDiv");
}

function clearUpChildren(parentName) {
    switch (parentName) {
        case 'lake' :
            $('#aaStream').val('');
            $('#allstreams').empty();
            $('#aaBranch').val('');
            $('#allbranches').empty();
            $('#aaStationFrom').val('');
            $('#allFromStations').empty();
            break;
        case 'stream' :
            $('#aaBranch').val('');
            $('#allbranches').empty();
            $('#aaStationFrom').val('');
            $('#allFromStations').empty();
            break;
        case 'branch' :
            $('#aaStationFrom').val('');
            $('#allFromStations').empty();
            break;
    }
}

function loadAA() {
    if (theAA != null) {
        $('#sampleCode').val(theAA.sample.sampleCode);
        if(theAA.sample.sampleStatus !== null)
          $('#sampleStatus').val(theAA.sample.sampleStatus);
        else
          $('#sampleStatus').val('DRAFT');
        $('#aaLake').val(theAA.locationReference.lake.showText);
        setStreamDropdown(theAA.locationReference.lake.id);
        $('#aaStream').val(theAA.locationReference.stream.showText);
        setBranchLenticDropdown(theAA.locationReference.stream.id);
        $('#aaBranch').val(theAA.locationReference.branchLentic.showText);
        setStationDropdown(theAA.locationReference.branchLentic.id);
        $('#aaStationFrom').val(theAA.locationReference.stationFrom.showText);
        $('#stationFromAdjust').val(theAA.locationReference.stationFromAdjust);
        $('#inspectDate').val(theAA.inspectDate);
        $('#timeOfCheck').val(theAA.timeToCheck);
        $('#aa_location').val(theAA.location);

        $('#button_continue').prop('disabled', false);
    } else {
        if (copyAA != null) {
            $('#aaLake').val(copyAA.locationReference.lake.showText);
            setStreamDropdown(copyAA.locationReference.lake.id);
            $('#aaStream').val(copyAA.locationReference.stream.showText);
            setBranchLenticDropdown(copyAA.locationReference.stream.id);
            $('#aaBranch').val(copyAA.locationReference.branchLentic.showText);
            setStationDropdown(copyAA.locationReference.branchLentic.id);
            $('#aaStationFrom').val(copyAA.locationReference.stationFrom.showText);
            $('#stationFromAdjust').val(copyAA.locationReference.stationFromAdjust);
        }
        $('#button_continue').prop('disabled', true);
    }
}