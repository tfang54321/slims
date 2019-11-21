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

        if (!validateElectroFishTime()) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.electrofish_time_message"), null, "displayMessage");
        } else if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getFormData("#edit_form");
            formData.laOffLine_Id = "#";

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineEF("-EF", formData);
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

    $("#radios_inline_abp").change(function () {
        var currentValue = $("#radios_inline_abp input[type=radio]:checked").val();

        switch (currentValue) {
            case 'standard' :
                $("#peak_vol").val("125");
                $("#pulse_rate_slow").val("3");
                $("#pulse_rate_fast").val("30");
                $("#duty_cycle_slow").val("25");
                $("#duty_cycle_fast").val("25");
                $("#burst_rate").val("75");
                break;
            case 'other' :
                $("#peak_vol").val('');
                $("#pulse_rate_slow").val('');
                $("#pulse_rate_fast").val('');
                $("#duty_cycle_slow").val('');
                $("#duty_cycle_fast").val('');
                $("#burst_rate").val('');
                break;
        }
    });

    $("#te").keyup(function () {
        $this = $(this);
        setTimeValueThreeColonTwo($this);
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;

        actionType = "PUT";
        actionUrl = getBaseString() + localizedName + "/api/las/electrofishing/" + theLA.id;

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

    function createOfflineEF(idSuffix, dataJSON) {
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
        if (theLA.laElectrofishingDetails != null) {
            if (theLA.laElectrofishingDetails.abpSettingType == "standard")
                $('#radio_standard').prop("checked", true);
            else
                $('#radio_other').prop("checked", true);

            if ((theLA.laElectrofishingDetails.abpPeakVolt !== undefined) && (theLA.laElectrofishingDetails.abpPeakVolt !== null))
                $('#peak_vol').val(theLA.laElectrofishingDetails.abpPeakVolt);

            if ((theLA.laElectrofishingDetails.pulseRateSlow !== undefined) && (theLA.laElectrofishingDetails.pulseRateSlow !== null))
                $('#pulse_rate_slow').val(theLA.laElectrofishingDetails.pulseRateSlow);

            if ((theLA.laElectrofishingDetails.pulseRateFast !== undefined) && (theLA.laElectrofishingDetails.pulseRateFast !== null))
                $('#pulse_rate_fast').val(theLA.laElectrofishingDetails.pulseRateFast);

            if ((theLA.laElectrofishingDetails.dutyCycleSlow !== undefined) && (theLA.laElectrofishingDetails.dutyCycleSlow !== null))
                $('#duty_cycle_slow').val(theLA.laElectrofishingDetails.dutyCycleSlow);

            if ((theLA.laElectrofishingDetails.dutyCycleFast !== undefined) && (theLA.laElectrofishingDetails.dutyCycleFast !== null))
                $('#duty_cycle_fast').val(theLA.laElectrofishingDetails.dutyCycleFast);

            if ((theLA.laElectrofishingDetails.burstRate !== undefined) && (theLA.laElectrofishingDetails.burstRate !== null))
                $('#burst_rate').val(theLA.laElectrofishingDetails.burstRate);

            if ((theLA.laElectrofishingDetails.surveyDistance !== undefined) && (theLA.laElectrofishingDetails.surveyDistance !== null))
                $('#tds').val(theLA.laElectrofishingDetails.surveyDistance);

            if ((theLA.laElectrofishingDetails.percAreaElectrofished !== undefined) && (theLA.laElectrofishingDetails.percAreaElectrofished !== null))
                $('#pae').val(theLA.laElectrofishingDetails.percAreaElectrofished);

            if ((theLA.laElectrofishingDetails.areaElectrofished !== undefined) && (theLA.laElectrofishingDetails.areaElectrofished !== null))
                $('#ae').val(theLA.laElectrofishingDetails.areaElectrofished);

            if (theLA.laElectrofishingDetails.areaElectrofishedSource == "estimated")
                $('#radio_est_ae').prop("checked", true);
            else
                $('#radio_mea_ae').prop("checked", true);

            if ((theLA.laElectrofishingDetails.timeElectrofished !== undefined) && (theLA.laElectrofishingDetails.timeElectrofished !== null))
                $('#te').val(theLA.laElectrofishingDetails.timeElectrofished);

            if (theLA.laElectrofishingDetails.timeElectrofishedSource == "estimated")
                $('#radio_est_te').prop("checked", true);
            else
                $('#radio_mea_te').prop("checked", true);

            $('#button_continue').prop('disabled', false);
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        $('#goto').hide();
        if (hasOfflineId()) {
            var offlineLAId = getOfflineId();
            var offlineEFId = offlineLAId + "-EF";

            $.db_getByKeyPath('larval_assessments', offlineLAId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                    $('#methodology').val(result.SURVEY_METHODOLOGY);
                }
            });
            $.db_getByKeyPath('larval_assessments', offlineEFId).then(function (result) {
                if (result) {
                    if (result.optradio_abp == "standard")
                        $('#radio_standard').prop("checked", true);
                    else
                        $('#radio_other').prop("checked", true);

                    if ((result.peak_vol !== undefined) && (result.peak_vol !== null))
                        $('#peak_vol').val(result.peak_vol);

                    if ((result.pulse_rate_slow !== undefined) && (result.pulse_rate_slow !== null))
                        $('#pulse_rate_slow').val(result.pulse_rate_slow);

                    if ((result.pulse_rate_fast !== undefined) && (result.pulse_rate_fast !== null))
                        $('#pulse_rate_fast').val(result.pulse_rate_fast);

                    if ((result.duty_cycle_slow !== undefined) && (result.duty_cycle_slow !== null))
                        $('#duty_cycle_slow').val(result.duty_cycle_slow);

                    if ((result.duty_cycle_fast !== undefined) && (result.duty_cycle_fast !== null))
                        $('#duty_cycle_fast').val(result.duty_cycle_fast);

                    if ((result.burst_rate !== undefined) && (result.burst_rate !== null))
                        $('#burst_rate').val(result.burst_rate);

                    if ((result.tds !== undefined) && (result.tds !== null))
                        $('#tds').val(result.tds);

                    if ((result.pae !== undefined) && (result.pae !== null))
                        $('#pae').val(result.pae);

                    if ((result.ae !== undefined) && (result.ae !== null))
                        $('#ae').val(result.ae);

                    if (result.optradio_ae == "estimated")
                        $('#radio_est_ae').prop("checked", true);
                    else
                        $('#radio_mea_ae').prop("checked", true);

                    if ((result.te !== undefined) && (result.te !== null))
                        $('#te').val(result.te);

                    if (result.optradio_te == "estimated")
                        $('#radio_est_te').prop("checked", true);
                    else
                        $('#radio_mea_te').prop("checked", true);

                    $('#button_continue').prop('disabled', false);
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }
});

function validateElectroFishTime() {
    var timeVal = $('#te').val();

    if ($.trim(timeVal).length == 0) {
        return true;
    } else {
        return /^([0-9]|[0-9][0-9]|[0-9][0-9][0-9]):[0-5][0-9]$/.test(timeVal);
    }
}
