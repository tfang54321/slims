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
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/main?laId=" + theLA.id;
            } else {
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/main/offline#" + getOfflineId();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
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
                    createOfflineGB("-GB", formData);
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

    $('.plot-dim').change(function () {
        var plot_len = $.trim($("#plot_size_length").val());
        var plot_width = $.trim($("#plot_size_width").val());
        if (plot_len.length > 0 && plot_width.length > 0 && !isNaN(plot_len) && !isNaN(plot_width)) {
            $("#area").val((parseFloat(plot_len) * parseFloat(plot_width)).toFixed(2));
            $("#quantity_used").val((parseFloat(plot_len) * parseFloat(plot_width) * 0.0175).toFixed(2));
        } else {
            $("#area").val('');
            $("#quantity_used").val('');
        }
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;

        actionType = "PUT";
        actionUrl = getBaseString() + localizedName + "/api/las/granularbayer/" + theLA.id;

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

    function createOfflineGB(idSuffix, dataJSON) {
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
        var redirectURL = getBaseString() + localizedName + "/larvalassessments/physicalchemicals?laId=" + theLA.id;
        return redirectURL;
    }

    function getOfflineContinueRedirectURL() {
        var redirectURL;
        var offlineId = getOfflineId();
        redirectURL = getBaseString() + localizedName + "/larvalassessments/physicalchemicals/offline#" + offlineId;
        return redirectURL;
    }

    function loadOnlineData() {
        $('#goto').show();
        if (theLA.laGranularBayerDetails != null) {

            if ((theLA.laGranularBayerDetails.plotLength !== undefined) && (theLA.laGranularBayerDetails.plotLength !== null))
                $('#plot_size_length').val(theLA.laGranularBayerDetails.plotLength);

            if ((theLA.laGranularBayerDetails.plotWidth !== undefined) && (theLA.laGranularBayerDetails.plotWidth !== null))
                $('#plot_size_width').val(theLA.laGranularBayerDetails.plotWidth);

            if ((theLA.laGranularBayerDetails.plotArea !== undefined) && (theLA.laGranularBayerDetails.plotArea !== null))
                $('#area').val(theLA.laGranularBayerDetails.plotArea);

            if ((theLA.laGranularBayerDetails.quantityUsed !== undefined) && (theLA.laGranularBayerDetails.quantityUsed !== null))
                $('#quantity_used').val(theLA.laGranularBayerDetails.quantityUsed);

            if ((theLA.laGranularBayerDetails.timeToFirstAmmocete !== undefined) && (theLA.laGranularBayerDetails.timeToFirstAmmocete !== null))
                $('#tfoa').val(theLA.laGranularBayerDetails.timeToFirstAmmocete);

            if ((theLA.laGranularBayerDetails.personHours !== undefined) && (theLA.laGranularBayerDetails.personHours !== null))
                $('#effort_hour').val(theLA.laGranularBayerDetails.personHours);

            if ((theLA.laGranularBayerDetails.numBoats !== undefined) && (theLA.laGranularBayerDetails.numBoats !== null))
                $('#effort_boat').val(theLA.laGranularBayerDetails.numBoats);

            $('#button_continue').prop('disabled', false);
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        $('#goto').hide();
        if (hasOfflineId()) {
            var offlineLAId = getOfflineId();
            var offlineGBId = offlineLAId + "-GB";

            $.db_getByKeyPath('larval_assessments', offlineLAId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                    $('#methodology').val(result.SURVEY_METHODOLOGY);
                }
            });
            $.db_getByKeyPath('larval_assessments', offlineGBId).then(function (result) {
                if (result) {
                    if ((result.plot_size_length !== undefined) && (result.plot_size_length !== null))
                        $('#plot_size_length').val(result.plot_size_length);

                    if ((result.plot_size_width !== undefined) && (result.plot_size_width !== null))
                        $('#plot_size_width').val(result.plot_size_width);

                    if ((result.area !== undefined) && (result.area !== null))
                        $('#area').val(result.area);

                    if ((result.quantity_used !== undefined) && (result.quantity_used !== null))
                        $('#quantity_used').val(result.quantity_used);

                    if ((result.tfoa !== undefined) && (result.tfoa !== null))
                        $('#tfoa').val(result.tfoa);

                    if ((result.effort_hour !== undefined) && (result.effort_hour !== null))
                        $('#effort_hour').val(result.effort_hour);

                    if ((result.effort_boat !== undefined) && (result.effort_boat !== null))
                        $('#effort_boat').val(result.effort_boat);

                    $('#button_continue').prop('disabled', false);
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }
});

