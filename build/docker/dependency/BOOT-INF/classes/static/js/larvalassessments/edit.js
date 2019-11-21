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

    $('input[type="date"]').prop('max', getCurrentDateStr());
    form_original_data = $("#edit_form").serialize();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/larvalassessments";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
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

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if (!validateStartFinishTime()) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.start_finish_time_message"), null, "displayMessage");
        } else if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {

            if (!$("#assessment_purpose_table input[type=checkbox]:checked").length > 0) {
                //showErrorMessage($.getMessage("i18n.assessment_purpose_message"));
            } else {
                var formData = getLAFormData();

                $.isOnline().then(function (isOnline) {
                    if (isOnline) {
                        sendOnlineForm(formData);
                    } else {
                        createOfflineLA("TMPLA-", formData);
                    }
                });
            }
        }
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;
        if ($('#referla_id').val() != "#" && $.trim($('#referla_id').val()).length > 0) {
            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/las/" + $('#referla_id').val();
        } else {
            actionType = "POST";
            actionUrl = getBaseString() + localizedName + "/api/las/add";
        }

        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
                $('#sampleCode').val(response.data.sample.sampleCode);
                $('#referla_id').val(response.data.id);
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

    function createOfflineLA(idPrefix, dataJSON) {
        return new Promise(function (doReturn) {

            dataJSON.updateDate = getCurrentDateStr();
            if (hasOfflineId()) {
                dataJSON.laOffLine_Id = getOfflineId();
                createOrUpdateOfflineLA(dataJSON);
            } else {
                $.db_getAndIncrementSeq('slims_seq').then(function (idSeq) {
                    if (idSeq) {
                        var paddedId = $.padZeroes(idSeq, 4);
                        dataJSON.laOffLine_Id = idPrefix + paddedId;
                        createOrUpdateOfflineLA(dataJSON);
                    } else {
                        $("#displayMessage").html("");
                        showErrorMessage($.getMessage("offlinedb_sequence_create_failed"), null, "displayMessage");
                        doReturn();
                    }
                });
            }

        });
    }

    function createOrUpdateOfflineLA(dataJSON) {
        $.db_insertOrUpdate('larval_assessments', dataJSON).then(function (result) {
            if (result) {
                form_original_data = $("#edit_form").serialize();
                $('#button_continue').prop('disabled', false);
                window.location.hash = dataJSON.laOffLine_Id;
                $("#displayMessage").html("");
                showSuccessMessage($.getMessage("offlinedata_save_success"), null, "displayMessage");
            } else {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("offlinedata_save_failed"), null, "displayMessage");
                doReturn();
            }
        });
    }

    $('#laLake').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("lake");
        e.preventDefault();
        var inputText = $(this).val();
        var lakeId = findObjectId("#alllakes", inputText);
        setStreamDropdown(lakeId);
        // $("#laStream").removeAttr('disabled');
    });

    $('#laStream').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("stream");
        e.preventDefault();
        var inputText = $(this).val();
        var streamId = findObjectId("#allstreams", inputText);
        setBranchLenticDropdown(streamId);
        // $("#laBranch").removeAttr('disabled');
    });
    $('#laBranch').on("awesomplete-selectcomplete", function (e) {
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

    function loadOnlineData() {
        $('#goto').show();
        if (theLA != null) {
            $('#sampleCode').val(theLA.sample.sampleCode);
            
            if(theLA.sample.sampleStatus !== null)
              $('#sampleStatus').val(theLA.sample.sampleStatus);
            else
            	$('#sampleStatus').val('DRAFT');
            $('#sampleDate').val(theLA.sample.sampleDate);
            $('#startTime').val(theLA.startTime);
            $('#finishTime').val(theLA.finishTime);
            $('#laLake').val(theLA.locationReference.lake.showText);
            setStreamDropdown(theLA.locationReference.lake.id);
            $('#laStream').val(theLA.locationReference.stream.showText);
            setBranchLenticDropdown(theLA.locationReference.stream.id);
            $('#laBranch').val(theLA.locationReference.branchLentic.showText);
            setStationDropdown(theLA.locationReference.branchLentic.id);
            $('#laStationFrom').val(theLA.locationReference.stationFrom.showText);
            $('#stationFromAdjust').val(theLA.locationReference.stationFromAdjust);
            if(theLA.locationReference.stationTo !== null)
               $('#laStationTo').val(theLA.locationReference.stationTo.showText);
            $('#stationToAdjust').val(theLA.locationReference.stationToAdjust);

            if ((theLA.geoUTM !== undefined) && (theLA.geoUTM !== null)) {
                if ((theLA.geoUTM.mapDatum !== undefined) && (theLA.geoUTM.mapDatum !== null))
                    $('#mapDatum').val(theLA.geoUTM.mapDatum.codePair.codeName);
                else
                    $('#mapDatum').val("WGS-84");

                if ((theLA.geoUTM.utmZone !== undefined) && (theLA.geoUTM.utmZone !== null))
                    $('#utmZone').val(theLA.geoUTM.utmZone.codePair.codeName);
                else
                    $('#utmZone').val("1");

                if ((theLA.geoUTM.utmEasting01 !== undefined) && (theLA.geoUTM.utmEasting01 !== null))
                    $('#utm_e01').val(theLA.geoUTM.utmEasting01);
                if ((theLA.geoUTM.utmNorthing01 !== undefined) && (theLA.geoUTM.utmNorthing01 !== null))
                    $('#utm_n01').val(theLA.geoUTM.utmNorthing01);

                if ((theLA.geoUTM.utmEasting02 !== undefined) && (theLA.geoUTM.utmEasting02 !== null))
                    $('#utm_e02').val(theLA.geoUTM.utmEasting02);
                if ((theLA.geoUTM.utmNorthing02 !== undefined) && (theLA.geoUTM.utmNorthing02 !== null))
                    $('#utm_n02').val(theLA.geoUTM.utmNorthing02);

                if ((theLA.geoUTM.utmEasting03 !== undefined) && (theLA.geoUTM.utmEasting03 !== null))
                    $('#utm_e03').val(theLA.geoUTM.utmEasting03);
                if ((theLA.geoUTM.utmNorthing03 !== undefined) && (theLA.geoUTM.utmNorthing03 !== null))
                    $('#utm_n03').val(theLA.geoUTM.utmNorthing03);

                if ((theLA.geoUTM.utmEasting04 !== undefined) && (theLA.geoUTM.utmEasting04 !== null))
                    $('#utm_e04').val(theLA.geoUTM.utmEasting04);
                if ((theLA.geoUTM.utmNorthing04 !== undefined) && (theLA.geoUTM.utmNorthing04 !== null))
                    $('#utm_n04').val(theLA.geoUTM.utmNorthing04);

                if ((theLA.geoUTM.location !== undefined) && (theLA.geoUTM.location !== null))
                    $('#la_location').val(theLA.geoUTM.location);
            }

            if ((theLA.surveyMethodology !== undefined) && (theLA.surveyMethodology !== null))
                $('#laMethodology').val(theLA.surveyMethodology.codePair.codeName);

            if ((theLA.operator1 !== undefined) && (theLA.operator1 !== null))
                $('#laOperator1').val(theLA.operator1.codePair.codeName);
            else
                $('#laOperator1').val("selectone");

            if ((theLA.operator2 !== undefined) && (theLA.operator2 !== null))
                $('#laOperator2').val(theLA.operator2.codePair.codeName);
            else
                $('#laOperator2').val("selectone");

            if ((theLA.operator3 !== undefined) && (theLA.operator3 !== null))
                $('#laOperator3').val(theLA.operator3.codePair.codeName);
            else
                $('#laOperator3').val("selectone");

            var laPurposes = theLA.purposeCodeNames;
            var allRows = $('#assessment_purpose_table').find('> tbody > tr');
            allRows.each(function () {
                var rowId = $(this).children("td").find(":hidden").val();
                var rowCheckBox = $(this).children("td").find(":checkbox");

                if ($.inArray(rowId, laPurposes) != -1) {
                    if (!rowCheckBox.is("checked"))
                        rowCheckBox.prop('checked', true);
                }
            });
            form_original_data = $("#edit_form").serialize();
            $('#button_continue').prop('disabled', false);
        }
    }

    function loadOfflineData() {
        $('#goto').hide();
        if (hasOfflineId()) {
            var offlineId = getOfflineId();

            $.db_getByKeyPath('larval_assessments', offlineId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                    $('#sampleDate').val(result.sampleDate);
                    $('#startTime').val(result.startTime);
                    $('#finishTime').val(result.finishTime);
                    $('#laLake').val(result.laLake);
                    var lakeId = findObjectId("#alllakes", result.laLake);
                    setStreamDropdown(lakeId);
                    $('#laStream').val(result.laStream);

                    var streamId = findObjectId("#allstreams", result.laStream);
                    setBranchLenticDropdown(streamId);

                    $('#laBranch').val(result.laBranch);
                    var branchId = findObjectId("#allbranches", result.laBranch);
                    setStationDropdown(branchId);

                    $('#laStationFrom').val(result.laStationFrom);
                    $('#stationFromAdjust').val(result.stationFromAdjust);
                    $('#laStationTo').val(result.laStationTo);
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

                    if ((result.la_location !== undefined) && (result.la_location !== null))
                        $('#la_location').val(result.la_location);

                    if ((result.SURVEY_METHODOLOGY !== undefined) && (result.SURVEY_METHODOLOGY !== null))
                        $('#laMethodology').val(result.SURVEY_METHODOLOGY);

                    if ((result.laOperator1 !== undefined)
                        && (result.laOperator1 !== null))
                        $('#laOperator1').val(result.laOperator1);

                    if ((result.laOperator2 !== undefined)
                        && (result.laOperator2 !== null))
                        $('#laOperator2').val(result.laOperator2);

                    if ((result.laOperator3 !== undefined) && (result.laOperator3 !== null))
                        $('#laOperator3').val(result.laOperator3);

                    var keys = Object.keys(result);
                    var values = [];
                    $.each(keys, function () {
                        if (this.includes('purposeCode')) {
                            values.push(result[this]);
                        }
                    });
                    var allRows = $('#assessment_purpose_table').find('> tbody > tr');
                    allRows.each(function () {
                        var rowId = $(this).children("td").find(":hidden").val();
                        var rowCheckBox = $(this).children("td").find(":checkbox");

                        if ($.inArray(rowId, values) != -1) {
                            if (!rowCheckBox.is("checked"))
                                rowCheckBox.prop('checked', true);
                        }
                    });

                    form_original_data = $("#edit_form").serialize();
                    $('#button_continue').prop('disabled', false);
                }
            });
        }
    }

    function getOnlineContinueRedirectURL() {
        var redirectURL;
        var methodology = $('#laMethodology').val();

        if (methodology === "0" || methodology === "1" || methodology === "2") {
            redirectURL = getBaseString() + localizedName + "/larvalassessments/electrofishings?laId=";
        } else if (methodology === "3" || methodology === "4") {
            redirectURL = getBaseString() + localizedName + "/larvalassessments/granularbayers?laId=";
        } else {
            redirectURL = getBaseString() + localizedName + "/larvalassessments/physicalchemicals?laId=";
        }
        var laId = $('#referla_id').val();
        redirectURL = redirectURL + laId;
        return redirectURL;
    }

    function getOfflineContinueRedirectURL() {
        var redirectURL;
        var offlineId = getOfflineId();
        var methodology = $('#laMethodology').val();

        if (methodology === "0" || methodology === "1" || methodology === "2") {
            redirectURL = getBaseString() + localizedName + "/larvalassessments/electrofishings/offline#";
        } else if (methodology === "3" || methodology === "4") {
            redirectURL = getBaseString() + localizedName + "/larvalassessments/granularbayers/offline#";
        } else {
            redirectURL = getBaseString() + localizedName + "/larvalassessments/physicalchemicals/offline#";
        }
        redirectURL = redirectURL + offlineId;
        return redirectURL;
    }

});

function setStreamDropdown(lakeId) {
    var streamList = allthestreams[lakeId];

    $('#laStream').val('');
    $('#allstreams').empty();

    $.each(streamList, function () {
        $('#allstreams').append('<option value="' + this.showText + '"></input></option>');
        $('#allstreams').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#laStream').attr('list', $('#laStream').attr('data-list'));
    $.applyAwesomplete("#laStreamDiv");
}

function setBranchLenticDropdown(streamId) {
    var branchLenticList = allthebranches[streamId];
    $('#laBranch').val('');
    $('#allbranches').empty();
    $.each(branchLenticList, function () {
        $('#allbranches').append('<option value="' + this.showText + '"></input></option>');
        $('#allbranches').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#laBranch').attr('list', $('#laBranch').attr('data-list'));
    $.applyAwesomplete("#laBranchDiv");
}

function setStationDropdown(branchId) {
    var stationList = allthestations[branchId];
    $('#laStationFrom').val('');
    $('#laStationTo').val('');
    $('#allFromStations').empty();
    $('#allToStations').empty();
    $.each(stationList, function () {
        $('#allFromStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allFromStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');

        $('#allToStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allToStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#laStationFrom').attr('list', $('#laStationFrom').attr('data-list'));
    $('#laStationTo').attr('list', $('#laStationTo').attr('data-list'));
    $.applyAwesomplete("#laStationDiv");
}

function clearUpChildren(parentName) {
    switch (parentName) {
        case 'lake' :
            $('#laStream').val('');
            $('#allstreams').empty();
            $('#laBranch').val('');
            $('#allbranches').empty();
            $('#laStationFrom').val('');
            $('#laStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
        case 'stream' :
            $('#laBranch').val('');
            $('#allbranches').empty();
            $('#laStationFrom').val('');
            $('#laStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
        case 'branch' :
            $('#laStationFrom').val('');
            $('#laStationTo').val('');
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

function getLAFormData() {
    var formData = getFormData("#edit_form");
    var purposeCode = "purposeCode";
    var i = 0;
    $("#assessment_purpose_table input[type=checkbox]:checked").each(function () {
        ++i;
        formData[purposeCode + i] = $(this).closest("tr").children("td").find(":hidden").val();
    });

    formData.lakeId = findObjectId("#alllakes", $("#laLake").val());
    formData.streamId = findObjectId("#allstreams", $("#laStream").val());
    formData.branchId = findObjectId("#allbranches", $("#laBranch").val());
    formData.stationFromId = findObjectId("#allFromStations", $("#laStationFrom").val());
    formData.stationToId = findObjectId("#allToStations", $("#laStationTo").val());

    formData.laOffLine_Id = "#";

    return formData;
}