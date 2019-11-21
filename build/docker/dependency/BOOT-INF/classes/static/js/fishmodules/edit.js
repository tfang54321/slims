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

    $('#startTime').attr("pattern","^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");
    $('#finishTime').attr("pattern","^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");

    $('input[type="date"]').prop('max', getCurrentDateStr());
    addValidationListener('#edit_form');
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/fishmodules";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_run_net').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                var loc = window.location.href;
                if (loc.indexOf('#') !== -1) {
                    redirectUrl = getOfflineRunnetRedirectURL();
                } else
                    redirectUrl = getOnlineRunnetRedirectURL();
            } else {
                redirectUrl = getOfflineRunnetRedirectURL();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    $('#button_hi').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                var loc = window.location.href;
                if (loc.indexOf('#') !== -1) {
                    redirectUrl = getOfflineHiRedirectURL();
                } else
                    redirectUrl = getOnlineHiRedirectURL();
            } else {
                redirectUrl = getOfflineHiRedirectURL();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();
        if (!validateStartFinishTime()) {
            $("#messageDiv").html("");
            showErrorMessage($.getMessage("i18n.start_finish_time_message"), null, "messageDiv");
        } else if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getFMFormData();

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineFM("TMPFM-", formData);
                }
            });
        }
    });

    $('#fmLake').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("lake");
        e.preventDefault();
        var inputText = $(this).val();
        var lakeId = findObjectId("#alllakes", inputText);
        setStreamDropdown(lakeId);
    });

    $('#fmStream').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("stream");
        e.preventDefault();
        var inputText = $(this).val();
        var streamId = findObjectId("#allstreams", inputText);
        setBranchLenticDropdown(streamId);
    });
    $('#fmBranch').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("branch");
        e.preventDefault();
        var inputText = $(this).val();
        var branchId = findObjectId("#allbranches", inputText);
        setStationDropdown(branchId);
    });

    $("#startTime").keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this)
    });

    $("#finishTime").keyup(function () {
        $this = $(this);
        setTimeValueTwoColonTwo($this)
    });

    function createOrUpdateOfflineFM(dataJSON) {
        $.db_insertOrUpdate('fish_modules', dataJSON).then(function (result) {
            if (result) {
                form_original_data = $("#edit_form").serialize();
                $('#button_run_net').prop('disabled', false);
                $('#button_hi').prop('disabled', false);
                window.location.hash = dataJSON.fmOffLine_Id;
                $("#messageDiv").html("");
                showSuccessMessage($.getMessage("offlinedata_save_success"), null, "messageDiv");
            } else {
                $("#messageDiv").html("");
                showErrorMessage($.getMessage("offlinedata_save_failed"), null, "messageDiv");
                doReturn();
            }
        });
    }

    function sendOnlineForm(formData) {
        var actionType, actionUrl;
        if ($('#referfm_id').val() != "#"
            && $.trim($('#referfm_id').val()).length > 0) {
            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/fms/" + $('#referfm_id').val();
        } else {
            actionType = "POST";
            actionUrl = getBaseString() + localizedName + "/api/fms/add";
        }
        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
                $('#sampleCode').val(response.data.sample.sampleCode);
                $('#referfm_id').val(response.data.id);
                $('#button_run_net').prop('disabled', false);
                $('#button_hi').prop('disabled', false);
                form_original_data = $("#edit_form").serialize();
                $("#messageDiv").html("");
                showSuccessMessage(response.message, null, "messageDiv");
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#messageDiv").html("");
                showErrorMessage(err.message, null, "messageDiv");
            }
        });
    }

    function createOfflineFM(idPrefix, dataJSON) {
        return new Promise(function (doReturn) {
            dataJSON.updateDate = getCurrentDateStr();
            if (hasOfflineId()) {
                dataJSON.fmOffLine_Id = getOfflineId();
                createOrUpdateOfflineFM(dataJSON);
            } else {
                $.db_getAndIncrementSeq('slims_seq').then(function (idSeq) {
                    if (idSeq) {
                        var paddedId = $.padZeroes(idSeq, 4);
                        dataJSON.fmOffLine_Id = idPrefix + paddedId;
                        createOrUpdateOfflineFM(dataJSON);
                    } else {
                        $("#messageDiv").html("");
                        showErrorMessage($.getMessage("offlinedb_sequence_create_failed"), null, "messageDiv");
                        doReturn();
                    }
                });
            }
        });
    }

    function loadOnlineData() {
        if (theFM != null) {
            $('#sampleCode').val(theFM.sample.sampleCode);
            if(theFM.sample.sampleStatus !== null)
               $('#sampleStatus').val(theFM.sample.sampleStatus);
            else
               $('#sampleStatus').val('DRAFT');
            $('#sampleDate').val(theFM.sample.sampleDate);
            $('#startTime').val(theFM.startTime);
            $('#finishTime').val(theFM.finishTime);
            $('#fmLake').val(theFM.locationReference.lake.showText);
            setStreamDropdown(theFM.locationReference.lake.id);
            $('#fmStream').val(theFM.locationReference.stream.showText);
            setBranchLenticDropdown(theFM.locationReference.stream.id);
            $('#fmBranch').val(theFM.locationReference.branchLentic.showText);
            setStationDropdown(theFM.locationReference.branchLentic.id);
            $('#fmStationFrom').val(theFM.locationReference.stationFrom.showText);
            $('#stationFromAdjust').val(theFM.locationReference.stationFromAdjust);
            $('#fmStationTo').val(theFM.locationReference.stationTo.showText);
            $('#stationToAdjust').val(theFM.locationReference.stationToAdjust);

            if ((theFM.geoUTM !== undefined) && (theFM.geoUTM !== null)) {
                if ((theFM.geoUTM.mapDatum !== undefined) && (theFM.geoUTM.mapDatum !== null))
                    $('#mapDatum').val(theFM.geoUTM.mapDatum.codePair.codeName);
                else
                    $('#mapDatum').val("WGS-84");

                if ((theFM.geoUTM.utmZone !== undefined) && (theFM.geoUTM.utmZone !== null))
                    $('#utmZone').val(theFM.geoUTM.utmZone.codePair.codeName);
                else
                    $('#utmZone').val("1");

                if ((theFM.geoUTM.utmEasting01 !== undefined)
                    && (theFM.geoUTM.utmEasting01 !== null))
                    $('#utm_e01').val(theFM.geoUTM.utmEasting01);
                if ((theFM.geoUTM.utmNorthing01 !== undefined)
                    && (theFM.geoUTM.utmNorthing01 !== null))
                    $('#utm_n01').val(theFM.geoUTM.utmNorthing01);

                if ((theFM.geoUTM.utmEasting02 !== undefined)
                    && (theFM.geoUTM.utmEasting02 !== null))
                    $('#utm_e02').val(theFM.geoUTM.utmEasting02);
                if ((theFM.geoUTM.utmNorthing02 !== undefined)
                    && (theFM.geoUTM.utmNorthing02 !== null))
                    $('#utm_n02').val(theFM.geoUTM.utmNorthing02);

                if ((theFM.geoUTM.utmEasting03 !== undefined)
                    && (theFM.geoUTM.utmEasting03 !== null))
                    $('#utm_e03').val(theFM.geoUTM.utmEasting03);
                if ((theFM.geoUTM.utmNorthing03 !== undefined)
                    && (theFM.geoUTM.utmNorthing03 !== null))
                    $('#utm_n03').val(theFM.geoUTM.utmNorthing03);

                if ((theFM.geoUTM.utmEasting04 !== undefined)
                    && (theFM.geoUTM.utmEasting04 !== null))
                    $('#utm_e04').val(theFM.geoUTM.utmEasting04);
                if ((theFM.geoUTM.utmNorthing04 !== undefined)
                    && (theFM.geoUTM.utmNorthing04 !== null))
                    $('#utm_n04').val(theFM.geoUTM.utmNorthing04);

                if ((theFM.geoUTM.location !== undefined)
                    && (theFM.geoUTM.location !== null))
                    $('#fm_location').val(theFM.geoUTM.location);
            }

            if (theFM.locationDetails !== null) {
                $('#containment').val(theFM.locationDetails.containment);
                $('#conductivity').val(theFM.locationDetails.conductivity);
                $('#temperature').val(theFM.locationDetails.temperature);
                $('#meanDepth').val(theFM.locationDetails.meanDepth);
                $('#meanWidth').val(theFM.locationDetails.meanWidth);
                $('#maxDepth').val(theFM.locationDetails.maxDepth);
                $('#measuredArea').val(theFM.locationDetails.measuredArea);
                $('#estimatedArea').val(theFM.locationDetails.estimatedArea);
                $('#distanceSurvey').val(theFM.locationDetails.distanceSurvey);
            }
            if (theFM.effectiveness != null) {
                if (theFM.effectiveness == "good")
                    $('#radio_ef_good').prop("checked", true);
                else if (theFM.effectiveness == "fair")
                    $('#radio_ef_fair').prop("checked", true);
                else if (theFM.effectiveness == "poor")
                    $('#radio_ef_poor').prop("checked", true);
                else
                    $('#radio_ef_na').prop("checked", true);
            }

            if ((theFM.fmPurpose !== undefined) && (theFM.fmPurpose !== null))
                $('#surveyPurpose').val(theFM.fmPurpose.codePair.codeName);

            if ((theFM.technique !== undefined) && (theFM.technique !== null))
                $('#technique').val(theFM.technique.codePair.codeName);

            if ((theFM.methodology !== undefined) && (theFM.methodology !== null))
                $('#methodology').val(theFM.methodology.codePair.codeName);

            if ((theFM.operator1 !== undefined) && (theFM.operator1 !== null))
                $('#fmOperator1').val(theFM.operator1.codePair.codeName);

            if ((theFM.operator2 !== undefined) && (theFM.operator2 !== null))
                $('#fmOperator2').val(theFM.operator2.codePair.codeName);

            if ((theFM.operator3 !== undefined) && (theFM.operator3 !== null))
                $('#fmOperator3').val(theFM.operator3.codePair.codeName);

            $('#button_run_net').prop('disabled', false);
            $('#button_hi').prop('disabled', false);
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        if (hasOfflineId()) {
            var offlineId = getOfflineId();

            $.db_getByKeyPath('fish_modules', offlineId).then(
                function (result) {
                    if (result) {
                        $('#sampleStatus').val(result.sampleStatus);
                        $('#sampleDate').val(result.sampleDate);
                        $('#startTime').val(result.startTime);
                        $('#finishTime').val(result.finishTime);
                        $('#fmLake').val(result.fmLake);
                        var lakeId = findObjectId("#alllakes", result.fmLake);
                        setStreamDropdown(lakeId);
                        $('#fmStream').val(result.fmStream);

                        var streamId = findObjectId("#allstreams", result.fmStream);
                        setBranchLenticDropdown(streamId);

                        $('#fmBranch').val(result.fmBranch);
                        var branchId = findObjectId("#allbranches", result.fmBranch);
                        setStationDropdown(branchId);

                        $('#fmStationFrom').val(result.fmStationFrom);
                        $('#stationFromAdjust').val(result.stationFromAdjust);
                        $('#fmStationTo').val(result.fmStationTo);
                        $('#stationToAdjust').val(result.stationToAdjust);

                        if ((result.MAP_DATUM !== undefined) && (result.MAP_DATUM !== null))
                            $('#mapDatum').val(result.MAP_DATUM);
                        else
                            $('#mapDatum').val("WGS-84");

                        if ((result.UTM_ZONE !== undefined) && (result.UTM_ZONE !== null))
                            $('#utmZone').val(result.UTM_ZONE);
                        else
                            $('#utmZone').val("1");

                        if ((result.utm_e01 !== undefined) && (result.utm_e01 !== null))
                            $('#utm_e01').val(result.utm_e01);
                        if ((result.utm_n01 !== undefined) && (result.utm_n01 !== null))
                            $('#utm_n01').val(result.utm_n01);

                        if ((result.utm_e02 !== undefined) && (result.utm_e02 !== null))
                            $('#utm_e02').val(result.utm_e02);
                        if ((result.utm_n02 !== undefined) && (result.utm_n02 !== null))
                            $('#utm_n02').val(result.utm_n02);

                        if ((result.utm_e03 !== undefined) && (result.utm_e03 !== null))
                            $('#utm_e03').val(result.utm_e03);
                        if ((result.utm_n03 !== undefined) && (result.utm_n03 !== null))
                            $('#utm_n03').val(result.utm_n03);

                        if ((result.utm_e04 !== undefined) && (result.utm_e04 !== null))
                            $('#utm_e04').val(result.utm_e04);
                        if ((result.utm_n04 !== undefined) && (result.utm_n04 !== null))
                            $('#utm_n04').val(result.utm_n04);

                        if ((result.fm_location !== undefined) && (result.fm_location !== null))
                            $('#fm_location').val(result.fm_location);

                        $('#containment').val(result.containment);
                        $('#conductivity').val(result.conductivity);
                        $('#temperature').val(result.temperature);
                        $('#meanDepth').val(result.meanDepth);
                        $('#meanWidth').val(result.meanWidth);
                        $('#maxDepth').val(result.maxDepth);
                        $('#measuredArea').val(result.measuredArea);
                        $('#estimatedArea').val(result.estimatedArea);
                        $('#distanceSurvey').val(result.distanceSurvey);

                        if (result.optradio_effectiveness == "good")
                            $('#radio_ef_good').prop("checked", true);
                        else if (result.optradio_effectiveness == "fair")
                            $('#radio_ef_fair').prop("checked", true);
                        else if (result.optradio_effectiveness == "poor")
                            $('#radio_ef_poor').prop("checked", true);
                        else
                            $('#radio_ef_na').prop("checked", true);

                        if ((result.surveyPurpose !== undefined) && (result.surveyPurpose !== null))
                            $('#surveyPurpose').val(result.surveyPurpose);

                        if ((result.technique !== undefined) && (result.technique !== null))
                            $('#technique').val(result.technique);

                        if ((result.methodology !== undefined) && (result.methodology !== null))
                            $('#methodology').val(result.methodology);

                        if ((result.fmOperator1 !== undefined) && (result.fmOperator1 !== null))
                            $('#fmOperator1').val(result.fmOperator1);

                        if ((result.fmOperator2 !== undefined) && (result.fmOperator2 !== null))
                            $('#fmOperator2').val(result.fmOperator2);

                        if ((result.fmOperator3 !== undefined) && (result.fmOperator3 !== null))
                            $('#fmOperator3').val(result.fmOperator3);

                        $('#button_run_net').prop('disabled', false);
                        $('#button_hi').prop('disabled', false);
                        form_original_data = $("#edit_form").serialize();
                    }
                });
        }
    }

    function getOnlineRunnetRedirectURL() {
        var fmId = $('#referfm_id').val();
        var redirectUrl = getBaseString() + localizedName + "/fishmodules/runnet?fmId=" + fmId;
        return redirectUrl;
    }

    function getOfflineRunnetRedirectURL() {
        var offlineId = getOfflineId();
        var redirectUrl = getBaseString() + localizedName + "/fishmodules/runnet/offline#" + offlineId;
        return redirectUrl;
    }

    function getOnlineHiRedirectURL() {
        var fmId = $('#referfm_id').val();
        var redirectUrl = getBaseString() + localizedName + "/fishmodules/habitatinventory?fmId=" + fmId;
        return redirectUrl;
    }

    function getOfflineHiRedirectURL() {
        var offlineId = getOfflineId();
        var redirectUrl = getBaseString() + localizedName + "/fishmodules/habitatinventory/offline#" + offlineId;
        return redirectUrl;
    }
});

function setStreamDropdown(lakeId) {
    var streamList = allthestreams[lakeId];

    $('#fmStream').val('');
    $('#allstreams').empty();

    $.each(streamList, function () {
        $('#allstreams').append('<option value="' + this.showText + '"></input></option>');
        $('#allstreams').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#fmStream').attr('list', $('#fmStream').attr('data-list'));
    $.applyAwesomplete("#fmStreamDiv");
}

function setBranchLenticDropdown(streamId) {
    var branchLenticList = allthebranches[streamId];
    $('#fmBranch').val('');
    $('#allbranches').empty();
    $.each(branchLenticList, function () {
        $('#allbranches').append('<option value="' + this.showText + '"></input></option>');
        $('#allbranches').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#fmBranch').attr('list', $('#fmBranch').attr('data-list'));
    $.applyAwesomplete("#fmBranchDiv");
}

function setStationDropdown(branchId) {
    var stationList = allthestations[branchId];
    $('#fmStationFrom').val('');
    $('#fmStationTo').val('');
    $('#allFromStations').empty();
    $('#allToStations').empty();
    $.each(stationList, function () {
        $('#allFromStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allFromStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');

        $('#allToStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allToStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#fmStationTo').attr('list', $('#fmStationTo').attr('data-list'));
    $('#fmStationFrom').attr('list', $('#fmStationFrom').attr('data-list'));

    $.applyAwesomplete("#fmStationDiv");
}

function clearUpChildren(parentName) {
    switch (parentName) {
        case 'lake' :
            $('#fmStream').val('');
            $('#allstreams').empty();
            $('#fmBranch').val('');
            $('#allbranches').empty();
            $('#fmStationFrom').val('');
            $('#fmStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
        case 'stream' :
            $('#fmBranch').val('');
            $('#allbranches').empty();
            $('#fmStationFrom').val('');
            $('#fmStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
        case 'branch' :
            $('#fmStationFrom').val('');
            $('#fmStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
    }
}

function validateStartFinishTime() {
    var startTimeVal = $.trim($('#startTime').val());
    var finishTimeVal = $.trim($('#finishTime').val());

    if (startTimeVal.length === 0 && finishTimeVal.length === 0) {
        return true;
    }
    if ((startTimeVal.length > 0 && finishTimeVal.length === 0)
        || startTimeVal.length === 0 && finishTimeVal.length > 0) {
        return false;
    }
    if (!(/^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/.test(startTimeVal))
        && (/^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/.test(finishTimeVal))) {
        return false;
    }
    var startTime = startTimeVal.split(":");
    var endTime = finishTimeVal.split(":");
    var startDateTime = new Date(0, 0, 0, startTime[0], startTime[1], 0);
    var endDateTime = new Date(0, 0, 0, endTime[0], endTime[1], 0);
    return startDateTime <= endDateTime;
}

function getFMFormData() {
    var formData = getFormData("#edit_form");

    formData.lakeId = findObjectId("#alllakes", $("#fmLake").val());
    formData.streamId = findObjectId("#allstreams", $("#fmStream").val());
    formData.branchId = findObjectId("#allbranches", $("#fmBranch").val());
    formData.stationFromId = findObjectId("#allFromStations", $("#fmStationFrom").val());
    formData.stationToId = findObjectId("#allToStations", $("#fmStationTo").val());

    formData.fmOffLine_Id = "#";

    return formData;
}


