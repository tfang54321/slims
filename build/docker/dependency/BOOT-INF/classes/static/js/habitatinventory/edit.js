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
        var redirectUrl = getBaseString() + localizedName + "/habitatinventory";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_transect').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                var loc = window.location.href;
                if (loc.indexOf('#') !== -1) {
                    redirectUrl = getOfflineRedirectURL();
                } else
                    redirectUrl = getOnlineRedirectURL();
            } else {
                redirectUrl = getOfflineRedirectURL();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();
        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getHIFormData();

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineHI("TMPHI-", formData);
                }
            });
        }
    });

    $('#hiLake').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("lake");
        e.preventDefault();
        var inputText = $(this).val();
        var lakeId = findObjectId("#alllakes", inputText);
        setStreamDropdown(lakeId);
    });
    $('#hiStream').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("stream");
        e.preventDefault();
        var inputText = $(this).val();
        var streamId = findObjectId("#allstreams", inputText);
        setBranchLenticDropdown(streamId);
    });
    $('#hiBranch').on("awesomplete-selectcomplete", function (e) {
        clearUpChildren("branch");
        e.preventDefault();
        var inputText = $(this).val();
        var branchId = findObjectId("#allbranches", inputText);
        setStationDropdown(branchId);
    });

    function getOnlineRedirectURL() {
        var id = $('#referhi_id').val();
        var redirectUrl = getBaseString() + localizedName + "/habitatinventory/transect?hiId=" + id;
        return redirectUrl;
    }

    function getOfflineRedirectURL() {
        var offlineId = getOfflineId();
        var redirectUrl = getBaseString() + localizedName + "/habitatinventory/transect/offline#" + offlineId;
        return redirectUrl;
    }

    function sendOnlineForm(formData) {
        var localizedName = getLocale();
        var actionType, actionUrl;
        if ($('#referhi_id').val() != "#" && $.trim($('#referhi_id').val()).length > 0) {
            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/hi/" + $('#referhi_id').val();
        } else {
            actionType = "POST";
            actionUrl = getBaseString() + localizedName + "/api/hi/add";
        }
        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
                $('#sampleCode').val(response.data.sample.sampleCode);
                $('#referhi_id').val(response.data.id);
                $('#button_transect').prop('disabled', false);
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

    function createOfflineHI(idPrefix, dataJSON) {
        return new Promise(function (doReturn) {

            dataJSON.updateDate = getCurrentDateStr();
            if (hasOfflineId()) {
                dataJSON.hiOffLine_Id = getOfflineId();
                createOrUpdateOfflineHI(dataJSON);
            } else {
                $.db_getAndIncrementSeq('slims_seq').then(function (idSeq) {
                    if (idSeq) {
                        var paddedId = $.padZeroes(idSeq, 4);
                        dataJSON.hiOffLine_Id = idPrefix + paddedId;
                        createOrUpdateOfflineHI(dataJSON);
                    } else {
                        $("#displayMessage").html("");
                        showErrorMessage($.getMessage("offlinedb_sequence_create_failed"), null, "displayMessage");
                        doReturn();
                    }
                });
            }
        });
    }

    function createOrUpdateOfflineHI(dataJSON) {
        $.db_insertOrUpdate('habitat_inventory', dataJSON).then(function (result) {
            if (result) {
                form_original_data = $("#edit_form").serialize();
                $('#button_transect').prop('disabled', false);
                window.location.hash = dataJSON.hiOffLine_Id;
                $("#displayMessage").html("");
                showSuccessMessage($.getMessage("offlinedata_save_success"), null, "displayMessage");
            } else {
                $("#displayMessage").html("");
                showErrorMessage($.getMessage("offlinedata_save_failed"), null, "displayMessage");
                doReturn();
            }
        });
    }

    function loadOnlineData() {
        if (theHI != null) {
            $('#sampleCode').val(theHI.sample.sampleCode);
            if(theHI.sample.sampleStatus !== null)
              $('#sampleStatus').val(theHI.sample.sampleStatus);
            else
              $('#sampleStatus').val("DRAFT");
            $('#transectId').val(theHI.transectId);
            $('#inventoryDate').val(theHI.inventoryDate);
            $('#hiLake').val(theHI.locationReference.lake.showText);
            setStreamDropdown(theHI.locationReference.lake.id);
            $('#hiStream').val(theHI.locationReference.stream.showText);
            setBranchLenticDropdown(theHI.locationReference.stream.id);
            $('#hiBranch').val(theHI.locationReference.branchLentic.showText);
            setStationDropdown(theHI.locationReference.branchLentic.id);
            $('#hiStationFrom').val(theHI.locationReference.stationFrom.showText);
            $('#stationFromAdjust').val(theHI.locationReference.stationFromAdjust);
            $('#hiStationTo').val(theHI.locationReference.stationTo.showText);
            $('#stationToAdjust').val(theHI.locationReference.stationToAdjust);

            if ((theHI.geoUTM !== undefined) && (theHI.geoUTM !== null)) {
                if ((theHI.geoUTM.mapDatum !== undefined) && (theHI.geoUTM.mapDatum !== null))
                    $('#mapDatum').val(theHI.geoUTM.mapDatum.codePair.codeName);
                else
                    $('#mapDatum').val("WGS-84");

                if ((theHI.geoUTM.utmZone !== undefined) && (theHI.geoUTM.utmZone !== null))
                    $('#utmZone').val(theHI.geoUTM.utmZone.codePair.codeName);
                else
                    $('#utmZone').val("1");

                if ((theHI.geoUTM.utmEasting01 !== undefined) && (theHI.geoUTM.utmEasting01 !== null))
                    $('#utm_e01').val(theHI.geoUTM.utmEasting01);
                if ((theHI.geoUTM.utmNorthing01 !== undefined) && (theHI.geoUTM.utmNorthing01 !== null))
                    $('#utm_n01').val(theHI.geoUTM.utmNorthing01);

                if ((theHI.geoUTM.utmEasting02 !== undefined) && (theHI.geoUTM.utmEasting02 !== null))
                    $('#utm_e02').val(theHI.geoUTM.utmEasting02);
                if ((theHI.geoUTM.utmNorthing02 !== undefined) && (theHI.geoUTM.utmNorthing02 !== null))
                    $('#utm_n02').val(theHI.geoUTM.utmNorthing02);

                if ((theHI.geoUTM.location !== undefined) && (theHI.geoUTM.location !== null))
                    $('#hi_location').val(theHI.geoUTM.location);
            }

            if ((theHI.operationUnit !== undefined) && (theHI.operationUnit !== null))
                $('#hiOperUnit').val(theHI.operationUnit.codePair.codeName);

            if ((theHI.habitatMeasurements !== undefined) && (theHI.habitatMeasurements !== null))
                $('#hiMeasure').val(theHI.habitatMeasurements.codePair.codeName);

            if ((theHI.operator1 !== undefined) && (theHI.operator1 !== null))
                $('#hiOperator1').val(theHI.operator1.codePair.codeName);
            else
                $('#hiOperator1').val("selectone");

            if ((theHI.operator2 !== undefined) && (theHI.operator2 !== null))
                $('#hiOperator2').val(theHI.operator2.codePair.codeName);
            else
                $('#hiOperator2').val("selectone");

            if ((theHI.operator3 !== undefined) && (theHI.operator3 !== null))
                $('#hiOperator3').val(theHI.operator3.codePair.codeName);
            else
                $('#hiOperator3').val("selectone");

            $('#button_transect').prop('disabled', false);
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        if (hasOfflineId()) {
            var offlineId = getOfflineId();

            $.db_getByKeyPath('habitat_inventory', offlineId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                    $('#transectId').val(result.transectId);
                    $('#inventoryDate').val(result.inventoryDate);

                    $('#hiLake').val(result.hiLake);
                    var lakeId = findObjectId("#alllakes", result.hiLake);
                    setStreamDropdown(lakeId);

                    $('#hiStream').val(result.hiStream);
                    var streamId = findObjectId("#allstreams", result.hiStream);
                    setBranchLenticDropdown(streamId);

                    $('#hiBranch').val(result.hiBranch);
                    var branchId = findObjectId("#allbranches", result.hiBranch);
                    setStationDropdown(branchId);

                    $('#hiStationFrom').val(result.hiStationFrom);
                    $('#stationFromAdjust').val(result.stationFromAdjust);
                    $('#hiStationTo').val(result.hiStationTo);
                    $('#stationToAdjust').val(result.hiStationTo);

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

                    if ((result.hi_location !== undefined) && (result.hi_location !== null))
                        $('#hi_location').val(result.hi_location);

                    if ((result.OPERATING_UNIT !== undefined) && (result.OPERATING_UNIT !== null))
                        $('#hiOperUnit').val(result.OPERATING_UNIT);

                    if ((result.HI_MEASUREMENTS !== undefined) && (result.HI_MEASUREMENTS !== null))
                        $('#hiMeasure').val(result.HI_MEASUREMENTS);

                    if ((result.hiOperator1 !== undefined) && (result.hiOperator1 !== null))
                        $('#hiOperator1').val(result.hiOperator1);
                    else
                        $('#hiOperator1').val("selectone");

                    if ((result.hiOperator2 !== undefined) && (result.hiOperator2 !== null))
                        $('#hiOperator2').val(result.hiOperator2);
                    else
                        $('#hiOperator2').val("selectone");

                    if ((result.hiOperator3 !== undefined) && (result.hiOperator3 !== null))
                        $('#hiOperator3').val(result.hiOperator3);
                    else
                        $('#hiOperator3').val("selectone");

                    $('#button_transect').prop('disabled', false);
                    form_original_data = $("#edit_form").serialize();
                } else {
                    // showErrorMessage("Cannot find the offline object.");
                }
            });
        }
    }
});

function setStreamDropdown(lakeId) {
    var streamList = allthestreams[lakeId];

    $('#hiStream').val('');
    $('#allstreams').empty();

    $.each(streamList, function () {
        $('#allstreams').append('<option value="' + this.showText + '"></input></option>');
        $('#allstreams').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#hiStream').attr('list', $('#hiStream').attr('data-list'));
    $.applyAwesomplete("#hiStreamDiv");
}

function setBranchLenticDropdown(streamId) {
    var branchLenticList = allthebranches[streamId];
    $('#hiBranch').val('');
    $('#allbranches').empty();
    $.each(branchLenticList, function () {
        $('#allbranches').append('<option value="' + this.showText + '"></input></option>');
        $('#allbranches').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#hiBranch').attr('list', $('#hiBranch').attr('data-list'));
    $.applyAwesomplete("#hiBranchDiv");
}

function setStationDropdown(branchId) {
    var stationList = allthestations[branchId];
    $('#hiStationFrom').val('');
    $('#hiStationTo').val('');
    $('#allFromStations').empty();
    $('#allToStations').empty();
    $.each(stationList, function () {
        $('#allFromStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allFromStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');

        $('#allToStations').append('<option value="' + this.showText + '"></input></option>');
        $('#allToStations').children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    $('#hiStationFrom').attr('list', $('#hiStationFrom').attr('data-list'));
    $('#hiStationTo').attr('list', $('#hiStationTo').attr('data-list'));
    $.applyAwesomplete("#hiStationDiv");
}

function clearUpChildren(parentName) {
    switch (parentName) {
        case 'lake' :
            $('#hiStream').val('');
            $('#allstreams').empty();
            $('#hiBranch').val('');
            $('#allbranches').empty();
            $('#hiStationFrom').val('');
            $('#hiStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
        case 'stream' :
            $('#hiBranch').val('');
            $('#allbranches').empty();
            $('#hiStationFrom').val('');
            $('#hiStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
        case 'branch' :
            $('#hiStationFrom').val('');
            $('#hiStationTo').val('');
            $('#allFromStations').empty();
            $('#allToStations').empty();
            break;
    }
}

function getHIFormData() {
    var formData = getFormData("#edit_form");

    formData.lakeId = findObjectId("#alllakes", $("#hiLake").val());
    formData.streamId = findObjectId("#allstreams", $("#hiStream").val());
    formData.branchId = findObjectId("#allbranches", $("#hiBranch").val());
    formData.stationFromId = findObjectId("#allFromStations", $("#hiStationFrom").val());
    formData.stationToId = findObjectId("#allToStations", $("#hiStationTo").val());

    formData.hiOffLine_Id = "#";

    return formData;
}