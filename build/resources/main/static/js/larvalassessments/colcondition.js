$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data = $("#edit_form").serialize();

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
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/physicalchemicals?laId=" + theLA.id;
            } else {
                redirectUrl = getBaseString() + localizedName + "/larvalassessments/physicalchemicals/offline#" + getOfflineId();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();

        if ($('#radios_genconditions input[type=radio]:checked').val() == "poor") {
            if ($.trim($('#condition_detail').val()).length == 0) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("i18n.poor_condition_message"), null, "displayMessage");
                return;
            }
        }
        if ($('#radios_effectiveness input[type=radio]:checked').val() == "poor") {
            if ($.trim($('#effectiveness_detail').val()).length == 0) {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("i18n.poor_condition_message"), null, "displayMessage");
                return;
            }
        }
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getFormData("#edit_form");
            formData.laOffLine_Id = "#";

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineCC("-CC", formData);
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
        actionUrl = getBaseString() + localizedName + "/api/las/collectioncon/" + $('#referla_id').val();

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

    function createOfflineCC(idSuffix, dataJSON) {
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

    function loadOnlineData() {
        $('#goto').show();
        if (theLA.collectCondition != null || theLA.effectiveness != null) {
            if (theLA.collectCondition != null) {
                if (theLA.collectCondition == "good")
                    $('#radio_gc_good').prop("checked", true);
                else if (theLA.collectCondition == "fair")
                    $('#radio_gc_fair').prop("checked", true);
                else if (theLA.collectCondition == "poor")
                    $('#radio_gc_poor').prop("checked", true);
                else
                    $('#radio_gc_na').prop("checked", true);
            }

            if ((theLA.collectConditionDetails !== undefined) && (theLA.collectConditionDetails !== null))
                $('#condition_detail').val(theLA.collectConditionDetails);

            if (theLA.effectiveness != null) {
                if (theLA.effectiveness == "good")
                    $('#radio_ef_good').prop("checked", true);
                else if (theLA.effectiveness == "fair")
                    $('#radio_ef_fair').prop("checked", true);
                else if (theLA.effectiveness == "poor")
                    $('#radio_ef_poor').prop("checked", true);
                else
                    $('#radio_ef_na').prop("checked", true);
            }

            if ((theLA.effectivenessDetails !== undefined) && (theLA.effectivenessDetails !== null))
                $('#effectiveness_detail').val(theLA.effectivenessDetails);

            $('#button_continue').prop('disabled', false);
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        $('#goto').hide();
        if (hasOfflineId()) {
            var offlineLAId = getOfflineId();
            var offlineCCId = offlineLAId + "-CC";

            $.db_getByKeyPath('larval_assessments', offlineLAId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                }
            });
            $.db_getByKeyPath('larval_assessments', offlineCCId).then(function (result) {
                if (result) {

                    if (result.optradio_gencon == "good")
                        $('#radio_gc_good').prop("checked", true);
                    else if (result.optradio_gencon == "fair")
                        $('#radio_gc_fair').prop("checked", true);
                    else if (result.optradio_gencon == "poor")
                        $('#radio_gc_poor').prop("checked", true);
                    else
                        $('#radio_gc_na').prop("checked", true);

                    if ((result.condition_detail !== undefined) && (result.condition_detail !== null))
                        $('#condition_detail').val(result.condition_detail);

                    if (result.optradio_effectiveness == "good")
                        $('#radio_ef_good').prop("checked", true);
                    else if (result.optradio_effectiveness == "fair")
                        $('#radio_ef_fair').prop("checked", true);
                    else if (result.optradio_effectiveness == "poor")
                        $('#radio_ef_poor').prop("checked", true);
                    else
                        $('#radio_ef_na').prop("checked", true);

                    if ((result.effectiveness_detail !== undefined) && (result.effectiveness_detail !== null))
                        $('#effectiveness_detail').val(result.effectiveness_detail);

                    $('#button_continue').prop('disabled', false);
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }

    function getOnlineContinueRedirectURL() {
        var redirectURL = getBaseString() + localizedName + "/larvalassessments/fishobsercols/summary?laId=" + theLA.id;
        return redirectURL;
    }

    function getOfflineContinueRedirectURL() {
        var redirectURL;
        var offlineId = getOfflineId();
        redirectURL = getBaseString() + localizedName + "/larvalassessments/fishobsercols/summary/offline#" + offlineId;
        return redirectURL;
    }

});

