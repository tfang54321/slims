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

    form_original_data = $("#edit_form").serialize();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                if (theLA.laElectrofishingDetails != null) {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/electrofishings?laId=" + theLA.id;
                } else if (theLA.laGranularBayerDetails != null) {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/granularbayers?laId=" + theLA.id;
                } else {
                    redirectUrl = getBaseString() + localizedName + "/larvalassessments/main?laId=" + theLA.id;
                }

                confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
            } else {
                confirmGoBack(form_original_data, $.getMessage("i18n.exit_warning_message"));
            }
        });
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getFormData("#edit_form");
            formData.laOffLine_Id = "#";

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflinePC("-PC", formData);
                }
            });
        }
    });

    $('#button_continue').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                var loc = window.location.href;
                if (loc.indexOf('#') !== -1) {
                    redirectUrl = getOfflineContinueRedirectURL();
                } else
                    redirectUrl = getOnlineContinueRedirectURL();
            } else {
                redirectUrl = getOfflineContinueRedirectURL();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;

        actionType = "PUT";
        actionUrl = getBaseString() + localizedName + "/api/las/physicalchemical/" + theLA.id;

        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
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

    function createOfflinePC(idSuffix, dataJSON) {
        return new Promise(function (doReturn) {

            var offlineLAId = getOfflineId();
            dataJSON.laOffLine_Id = offlineLAId + idSuffix;

            $.db_insertOrUpdate('larval_assessments', dataJSON).then(function (result) {
                if (result) {
                    $('#button_continue').prop('disabled', false);
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

    function getOnlineContinueRedirectURL() {
        var redirectURL = getBaseString() + localizedName + "/larvalassessments/collectioncons?laId=" + theLA.id;
        return redirectURL;
    }

    function getOfflineContinueRedirectURL() {
        var redirectURL;
        var offlineId = getOfflineId();
        redirectURL = getBaseString() + localizedName + "/larvalassessments/collectioncons/offline#" + offlineId;
        return redirectURL;
    }

    function loadOnlineData() {
        $('#goto').show();
        if (theLA.laPhysicalChemicalData != null) {
            if ((theLA.laPhysicalChemicalData.waterSurfaceTemp !== undefined) && (theLA.laPhysicalChemicalData.waterSurfaceTemp !== null))
                $('#surface_temp').val(theLA.laPhysicalChemicalData.waterSurfaceTemp);

            if ((theLA.laPhysicalChemicalData.waterBottomTemp !== undefined) && (theLA.laPhysicalChemicalData.waterBottomTemp !== null))
                $('#bottom_temp').val(theLA.laPhysicalChemicalData.waterBottomTemp);

            if ((theLA.laPhysicalChemicalData.conductivity !== undefined) && (theLA.laPhysicalChemicalData.conductivity !== null))
                $('#conductivity').val(theLA.laPhysicalChemicalData.conductivity);

            if ((theLA.laPhysicalChemicalData.conductivityAt !== undefined) && (theLA.laPhysicalChemicalData.conductivityAt !== null))
                $('#conductivity_temp').val(theLA.laPhysicalChemicalData.conductivityAt);

            if ((theLA.laPhysicalChemicalData.phValue !== undefined) && (theLA.laPhysicalChemicalData.phValue !== null))
                $('#conductivity_ph').val(theLA.laPhysicalChemicalData.phValue);

            if ((theLA.laPhysicalChemicalData.meanDepth !== undefined) && (theLA.laPhysicalChemicalData.meanDepth !== null))
                $('#mean_depth').val(theLA.laPhysicalChemicalData.meanDepth);

            if ((theLA.laPhysicalChemicalData.meanStreamWidth !== undefined) && (theLA.laPhysicalChemicalData.meanStreamWidth !== null))
                $('#mean_stream_width').val(theLA.laPhysicalChemicalData.meanStreamWidth);

            if ((theLA.laPhysicalChemicalData.discharge !== undefined) && (theLA.laPhysicalChemicalData.discharge !== null))
                $('#discharge').val(theLA.laPhysicalChemicalData.discharge);

            if ((theLA.laPhysicalChemicalData.method !== undefined) && (theLA.laPhysicalChemicalData.method !== null))
                $('#laMethod').val(theLA.laPhysicalChemicalData.method.codePair.codeName);

            if (theLA.laPhysicalChemicalData.turbidity == "low")
                $('#radio_low').prop("checked", true);
            else if (theLA.laPhysicalChemicalData.turbidity == "medium")
                $('#radio_medium').prop("checked", true);
            else if (theLA.laPhysicalChemicalData.turbidity == "high")
                $('#radio_high').prop("checked", true);
            else
                $('#radio_na').prop("checked", true);

            $('#button_continue').prop('disabled', false);
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        $('#goto').hide();
        if (hasOfflineId()) {
            var offlineLAId = getOfflineId();
            var offlinePCId = offlineLAId + "-PC";

            $.db_getByKeyPath('larval_assessments', offlineLAId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                }
            });
            $.db_getByKeyPath('larval_assessments', offlinePCId).then(function (result) {
                if (result) {
                    if ((result.surface_temp !== undefined) && (result.surface_temp !== null))
                        $('#surface_temp').val(result.surface_temp);

                    if ((result.bottom_temp !== undefined) && (result.bottom_temp !== null))
                        $('#bottom_temp').val(result.bottom_temp);

                    if ((result.conductivity !== undefined) && (result.conductivity !== null))
                        $('#conductivity').val(result.conductivity);

                    if ((result.conductivity_temp !== undefined) && (result.conductivity_temp !== null))
                        $('#conductivity_temp').val(result.conductivity_temp);

                    if ((result.conductivity_ph !== undefined) && (result.conductivity_ph !== null))
                        $('#conductivity_ph').val(result.conductivity_ph);

                    if ((result.mean_depth !== undefined) && (result.mean_depth !== null))
                        $('#mean_depth').val(result.mean_depth);

                    if ((result.mean_stream_width !== undefined) && (result.mean_stream_width !== null))
                        $('#mean_stream_width').val(result.mean_stream_width);

                    if ((result.discharge !== undefined) && (result.discharge !== null))
                        $('#discharge').val(result.discharge);

                    if ((result.DISCHARGE_CODE !== undefined) && (result.DISCHARGE_CODE !== null))
                        $('#laMethod').val(result.DISCHARGE_CODE);

                    if (result.optradio_turbidity == "low")
                        $('#radio_low').prop("checked", true);
                    else if (result.optradio_turbidity == "medium")
                        $('#radio_medium').prop("checked", true);
                    else if (result.optradio_turbidity == "high")
                        $('#radio_high').prop("checked", true);
                    else
                        $('#radio_na').prop("checked", true);

                    $('#button_continue').prop('disabled', false);
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }
});

