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

    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_save').click(function (e) {
        e.preventDefault();

        $('#edit_form').validate();

        if (!validateTransectDetailTable()) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.transect_details_warning_message"), null, "displayMessage");
            return;
        }
        if (!validateTransectBottom()) {
            $("#displayMessage").html("");
            showErrorMessage($.getMessage("i18n.transect_bottom_warning_message"), null, "displayMessage");
            return;
        } else if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var formData = getHIFormData();

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineHITransect("-TRANSECT", formData);
                }
            });
        }
    });

    $('#transect_details_table').on('click', 'span.glyphicon-plus-sign',
        function (e) {
            e.preventDefault();
            addRow();
        });

    $('#transect_details_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#transect_details_table').find('> tbody > tr');
        if (allRows.length == 0) {
            addRow();
        }
    });
    $('#button_exit').click(function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/habitatinventory";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    function loadOnlineData() {
    	if(theHI.sample.sampleStatus !== null)
        $('#sampleStatus').val(theHI.sample.sampleStatus);
      else
        $('#sampleStatus').val("DRAFT");
    	
        if (theHI.haTransect != null) {
            $('#stream_width').val(theHI.haTransect.streamWidth);
            $('#transect_spacing').val(theHI.haTransect.transectSpacing);
            $('#total_reachlen').val(theHI.haTransect.totalReachLength);
            $('#est_discharge').val(theHI.haTransect.estDischarge);
            $('#stream_conditions').val(theHI.haTransect.streamCondition);

            if (theHI.haTransect.haTransectDetails.length > 0) {
                $.each(theHI.haTransect.haTransectDetails, function (i) {
                    if ($('#transect_details_table').find('> tbody > tr').length != (i + 1)) {
                        addRow();
                    }
                    var lastRow = $('#transect_details_table').find('> tbody > tr').last();
                    var tds = lastRow.find('td');

                    tds.eq(0).find('input').val(i + 1);
                    tds.eq(1).find('input').val(this.distanceFromLeftBank);
                    tds.eq(2).find('input').val(this.depth);
                    tds.eq(3).find('input').val(this.habitatType);
                    // addRow();
                });
            }

            $('#hi_bedrock').val(theHI.haTransect.bedrock);
            $('#hardpan_clay').val(theHI.haTransect.hardpanClay);
            $('#clay_sediments').val(theHI.haTransect.claySediments);
            $('#hi_gravel').val(theHI.haTransect.gravel);
            $('#hi_rubble').val(theHI.haTransect.rubble);
            $('#hi_sand').val(theHI.haTransect.sand);
            $('#hi_silt').val(theHI.haTransect.silt);
            $('#silt_detritus').val(theHI.haTransect.siltDetritus);
            $('#hi_detritus').val(theHI.haTransect.detritus);
            $('#cumulative_spawning').val(theHI.haTransect.cumulativeSpawning);
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        if (hasOfflineId()) {
            var offlineHiId = getOfflineId();
            var offlineTransectId = offlineHiId + "-TRANSECT";

            $.db_getByKeyPath('habitat_inventory', offlineHiId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                    $('#transectId').val(result.transectId);
                    $('#inventoryDate').val(result.inventoryDate);
                }
            });
            $.db_getByKeyPath('habitat_inventory', offlineTransectId).then(function (result) {
                if (result) {
                    $('#stream_width').val(result.stream_width);
                    $('#transect_spacing').val(result.transect_spacing);
                    $('#total_reachlen').val(result.total_reachlen);
                    $('#est_discharge').val(result.est_discharge);
                    $('#stream_conditions').val(result.stream_conditions);

                    if (result.numOfTransects > 0) {
                        for (i = 0; i < result.numOfTransects; i++) {
                            if ($('#transect_details_table').find('> tbody > tr').length != (i + 1)) {
                                addRow();
                            }
                            var lastRow = $('#transect_details_table').find('> tbody > tr').last();
                            var tds = lastRow.find('td');

                            tds.eq(0).find('input').val(i + 1);
                            tds.eq(1).find('input').val(result["disFromLeftBank" + i]);
                            tds.eq(2).find('input').val(result["depth" + i]);
                            tds.eq(3).find('input').val(result["hitype" + i]);
                        }
                    }

                    $('#hi_bedrock').val(result.hi_bedrock);
                    $('#hardpan_clay').val(result.hardpan_clay);
                    $('#clay_sediments').val(result.clay_sediments);
                    $('#hi_gravel').val(result.hi_gravel);
                    $('#hi_rubble').val(result.hi_rubble);
                    $('#hi_sand').val(result.hi_sand);
                    $('#hi_silt').val(result.hi_silt);
                    $('#silt_detritus').val(result.silt_detritus);
                    $('#hi_detritus').val(result.hi_detritus);
                    $('#cumulative_spawning').val(result.cumulative_spawning);
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }

    function sendOnlineForm(formData) {
        var actionType, actionUrl;

        actionType = "PUT";
        actionUrl = getBaseString() + localizedName + "/api/hi/transect/" + theHI.id;

        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
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

    function createOfflineHITransect(idSuffix, dataJSON) {
        return new Promise(function (doReturn) {

            var offlineHiId = getOfflineId();
            dataJSON.hiOffLine_Id = offlineHiId + idSuffix;

            $.db_insertOrUpdate('habitat_inventory', dataJSON).then(function (result) {
                if (result) {
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
});

function assignNamesToTableRow() {
    var disFromLeftName = "disFromLeftBank";
    var depthName = "depth";
    var hiTypeName = "hitype";

    var allRows = $('#transect_details_table').find('> tbody > tr');

    allRows.each(function (i) {
        var tds = $(this).find('td');
        tds.eq(0).find("input").val(i + 1);
        tds.eq(1).find("input").attr("name", disFromLeftName + String(i)).attr("id", disFromLeftName + String(i));
        tds.eq(1).find("label").attr("for", disFromLeftName + String(i));
        tds.eq(2).find("input").attr("name", depthName + String(i)).attr("id", depthName + String(i));
        tds.eq(2).find("label").attr("for", depthName + String(i));
        tds.eq(3).find("input").attr("name", hiTypeName + String(i)).attr("id", hiTypeName + String(i));
        tds.eq(3).find("label").attr("for", hiTypeName + String(i));
    });
}

function addRow() {
    var strHTML = '';
    var rowLength = $('#transect_details_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><br/><input type="text" class="input-sm" value="" readonly="true" size="4" /></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><br/><input type="text" class="input-sm" name="#" placeholder="" data-rule-number="true" max="9999.9999" maxlength="9" size="4" /></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><br/><input type="text" class="input-sm" name="#" placeholder="" data-rule-number="true" max="99.9" maxlength="4" size="4" /></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="#" placeholder="" max="100" min="0" data-rule-digits="true" /></td>';
    var cell_05 = '<td class="text-center icon"><br/><a href="#" role="button" title="' + $.getMessage("i18n.btn.remove") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td></tr>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05;
    $('#transect_details_table > tbody:last').append($.parseHTML(strHTML));

    setForm();
}

function setForm() {
    assignNamesToTableRow();
    resetIndexToTableRow('#transect_details_table');
    addValidationListener('#edit_form');
}

function validateTransectDetailTable() {
    var allRows = $('#transect_details_table').find('> tbody > tr');
    var flag = true;

    allRows.each(function (i) {
        if (i > 0) {
            var previousDFLB = parseFloat($(this).prev().find('td').eq(1).find("input").val());
            var currentDFLB = parseFloat($(this).find('td').eq(1).find("input").val());
            if (currentDFLB <= previousDFLB) {
                flag = false;
                return;
            }
        }
    });
    return flag;
}

function validateTransectBottom() {
    var flag = true;

    var bedrock = $.trim($('#hi_bedrock').val());
    var hardpan_clay = $.trim($('#hardpan_clay').val());
    var clay_sed = $.trim($('#clay_sediments').val());
    var gravel = $.trim($('#hi_gravel').val());
    var rubble = $.trim($('#hi_rubble').val());
    var sand = $.trim($('#hi_sand').val());
    var silt = $.trim($('#hi_silt').val());
    var silt_det = $.trim($('#silt_detritus').val());
    var det = $.trim($('#hi_detritus').val());

    var bottomTotal;
    if (bedrock == null || bedrock.length == 0 || isNaN(bedrock)) {
        bedrock = 0;
    } else {
        bedrock = parseInt(bedrock);
    }
    if (hardpan_clay == null || hardpan_clay.length == 0 || isNaN(hardpan_clay)) {
        hardpan_clay = 0;
    } else {
        hardpan_clay = parseInt(hardpan_clay);
    }
    if (clay_sed == null || clay_sed.length == 0 || isNaN(clay_sed)) {
        clay_sed = 0;
    } else {
        clay_sed = parseInt(clay_sed);
    }
    if (gravel == null || gravel.length == 0 || isNaN(gravel)) {
        gravel = 0;
    } else {
        gravel = parseInt(gravel);
    }
    if (rubble == null || rubble.length == 0 || isNaN(rubble)) {
        rubble = 0;
    } else {
        rubble = parseInt(rubble);
    }
    if (sand == null || sand.length == 0 || isNaN(sand)) {
        sand = 0;
    } else {
        sand = parseInt(sand);
    }
    if (silt == null || silt.length == 0 || isNaN(silt)) {
        silt = 0;
    } else {
        silt = parseInt(silt);
    }
    if (silt_det == null || silt_det.length == 0 || isNaN(silt_det)) {
        silt_det = 0;
    } else {
        silt_det = parseInt(silt_det);
    }
    if (det == null || det.length == 0 || isNaN(det)) {
        det = 0;
    } else {
        det = parseInt(det);
    }
    bottomTotal = bedrock + hardpan_clay + clay_sed + gravel + rubble + sand + silt + silt_det + det;

    if (bottomTotal !== 100) {
        flag = false;
    }
    return flag;
}

function getHIFormData() {
    var formData = getFormData("#edit_form");
    formData.numOfTransects = $('#transect_details_table').find('> tbody > tr').length;
    formData.hiOffLine_Id = "#";

    return formData;
}
