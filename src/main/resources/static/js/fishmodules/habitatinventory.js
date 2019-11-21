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

    var reloading = sessionStorage.getItem("successMessage");
    if(reloading){
        sessionStorage.removeItem("successMessage");
        showSuccessMessage(reloading, null, "displayMessage");
    }

    setForm();
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        var needValidate = true;
        if (!isOneEmptyRow()) {
            $('#edit_form').validate();
        } else {
            needValidate = false;
        }
        if (!validateTransectBottom()) {
            return;
        } else if (!validateChannelStructure()) {
            return;
        } else if ((needValidate && $('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) || (!needValidate && formIsChanged('#edit_form', form_original_data))) {

            var formData = getHabitatsFormData();

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineFMHabitats("-HABITATS", formData);
                }
            });
        }
    });

    /*$('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/fishmodules";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });*/
    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                var loc = window.location.href;
                if (loc.indexOf('#') !== -1) {
                    redirectUrl = getBaseString() + localizedName + "/fishmodules/main/offline#" + getOfflineId();
                } else
                    redirectUrl = getBaseString() + localizedName + "/fishmodules/main?fmId=" + theFM.id;
            } else {
                redirectUrl = getBaseString() + localizedName + "/fishmodules/main/offline#" + getOfflineId();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });

    });

    $('#hi_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();
        $(this).closest("tr").next().remove();
        $(this).closest("tr").remove();
        setForm();
        var allRows = $('#hi_table').children('tbody').children().not(".clickable");
        if (allRows.length == 0) {
            addRow();
        } else {
            var lastRow = $('#hi_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
            if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                lastRow.children('td').eq(6).html('<a href="#" role="button" title="' + $.getMessage("i18n.btn.add") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
            }
        }
    });

    $('#hi_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        addRow();
        // addValidationListener('#edit_form');
    });

    $('#hi_table').on('click', 'span.glyphicon-expand', function (e) {
        e.preventDefault();
        var hide = $.getMessage("i18n.btn.hide");
        $(this).closest("tr").find('td').eq(4).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand').attr("title", hide);
        $(this).closest("tr").next().toggle();
    });

    $('#hi_table').on('click', 'span.glyphicon-collapse-down', function (e) {
        e.preventDefault();
        var expand = $.getMessage("i18n.btn.expand");
        $(this).closest("tr").find('td').eq(4).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down').attr("title", expand);
        $(this).closest("tr").next().toggle();
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;

        actionType = "PUT";
        actionUrl = getBaseString() + localizedName + "/api/fms/habitats/" + theFM.id;

        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
                sessionStorage.setItem("successMessage", response.message);
                window.location.reload();
                $("#displayMessage").html("");
                showSuccessMessage(response.message, null, "displayMessage");
                setForm();
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayMessage").html("");
                showErrorMessage(err.message, null, "displayMessage");
            }
        });
    }

    function createOfflineFMHabitats(idSuffix, dataJSON) {
        return new Promise(function (doReturn) {

            var offlineFMId = getOfflineId();
            dataJSON.fmOffLine_Id = offlineFMId + idSuffix;

            $.db_insertOrUpdate('fish_modules', dataJSON).then(function (result) {
                if (result) {
                    sessionStorage.setItem("successMessage", response.message);
                    window.location.reload();
                    $("#displayMessage").html("");
                    showSuccessMessage($.getMessage("offlinedata_save_success"), null, "displayMessage");
                    setForm();
                    form_original_data = $("#edit_form").serialize();
                } else {
                    $("#displayMessage").html("");
                    showErrorMessage($.getMessage("offlinedata_save_failed"), null, "displayMessage");
                    doReturn();
                }
            });
        });
    }

    function loadOnlineData() {
        if (theFM.fmHabitats.length > 0) {
            $.each(theFM.fmHabitats, function (i) {
                if ($('#hi_table').children('tbody').children().not(".clickable").length != (i + 1)) {
                    addRow();
                }

                var lastRow = $('#hi_table').children('tbody').children().not(".clickable").last();
                var tds = lastRow.find('td');

                tds.eq(0).find('input').val(i + 1);
                tds.eq(1).find('input').val(this.width);
                tds.eq(2).find('input').val(this.depth);
                tds.eq(3).find('input').val(this.distanceFromLastTransect);
                tds.eq(7).find("input").val(this.id);

                var detailRow = lastRow.next();

                var allFields = detailRow.find('textarea, input');

                allFields.eq(0).val(this.location);
                allFields.eq(1).val(this.bedrock);
                allFields.eq(2).val(this.hardpanClay);
                allFields.eq(3).val(this.rubble);
                allFields.eq(4).val(this.gravel);
                allFields.eq(5).val(this.sand);
                allFields.eq(6).val(this.siltDetritus);
                allFields.eq(7).val(this.claySediments);
                allFields.eq(8).val(this.pools);
                allFields.eq(9).val(this.riffles);
                allFields.eq(10).val(this.eddyLagoon);
                allFields.eq(11).val(this.theRun);
                allFields.eq(12).val(this.bankOverhang);
                allFields.eq(13).val(this.vegetation);
                allFields.eq(14).val(this.woodyDebris);
                allFields.eq(15).val(this.algae);
                allFields.eq(16).val(this.shorelineGrasses);
                allFields.eq(17).val(this.shorelineTressShrubs);
            });
            form_original_data = $("#edit_form").serialize();
        }
    }

    function loadOfflineData() {
        if (hasOfflineId()) {
            var offlineFMId = getOfflineId();
            var offlineHabitatsId = offlineFMId + "-HABITATS";

            $.db_getByKeyPath('fish_modules', offlineFMId).then(function (result) {
                if (result) {
                    $('#sampleStatus').val(result.sampleStatus);
                }
            });
            $.db_getByKeyPath('fish_modules', offlineHabitatsId).then(function (result) {
                if (result) {
                    var numOfHabitats = result.numOfHabitats;
                    var i;
                    for (i = 0; i < numOfHabitats; i++) {
                        if ($('#hi_table').children('tbody').children().not(".clickable").length != (i + 1)) {
                            addRow();
                        }

                        var lastRow = $('#hi_table').children('tbody').children().not(".clickable").last();
                        var tds = lastRow.find('td');

                        tds.eq(0).find('input').val(i + 1);
                        tds.eq(1).find('input').val(result["width" + String(i)]);
                        tds.eq(2).find('input').val(result["depth" + String(i)]);
                        tds.eq(3).find('input').val(result["distance" + String(i)]);

                        var detailRow = lastRow.next();

                        var allFields = detailRow.find('textarea, input');

                        allFields.eq(0).val(result["location_" + String(i)]);
                        allFields.eq(1).val(result["bedrock_" + String(i)]);
                        allFields.eq(2).val(result["hardpanClay_" + String(i)]);
                        allFields.eq(3).val(result["rubble_" + String(i)]);
                        allFields.eq(4).val(result["gravel_" + String(i)]);
                        allFields.eq(5).val(result["sand_" + String(i)]);
                        allFields.eq(6).val(result["siltDetritus_" + String(i)]);
                        allFields.eq(7).val(result["claySediments_" + String(i)]);
                        allFields.eq(8).val(result["pools_" + String(i)]);
                        allFields.eq(9).val(result["riffles_" + String(i)]);
                        allFields.eq(10).val(result["eddyLagoon_" + String(i)]);
                        allFields.eq(11).val(result["run_" + String(i)]);
                        allFields.eq(12).val(result["bankOverhang_" + String(i)]);
                        allFields.eq(13).val(result["vegetation_" + String(i)]);
                        allFields.eq(14).val(result["woodyDebris_" + String(i)]);
                        allFields.eq(15).val(result["algae_" + String(i)]);
                        allFields.eq(16).val(result["grasses_" + String(i)]);
                        allFields.eq(17).val(result["trees_" + String(i)]);
                    }
                    form_original_data = $("#edit_form").serialize();
                }
            });
        }
    }

});

function assignNamesToTableRow() {
    var transectIdName = "transectId";
    var widthName = "width";
    var depthName = "depth";
    var distanceName = "distance";
    var habitatIdName = "habitatId";

    var allWaterChemRows = $('#hi_table').children('tbody').children().not(".clickable");

    allWaterChemRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("input").attr("name", transectIdName + String(i));
        tds.eq(1).find("label").attr("for", widthName + String(i));
        tds.eq(1).find("input").attr("name", widthName + String(i)).attr("id", widthName + String(i));
        tds.eq(2).find("label").attr("for", depthName + String(i));
        tds.eq(2).find("input").attr("name", depthName + String(i)).attr("id", depthName + String(i));
        tds.eq(3).find("label").attr("for", distanceName + String(i));
        tds.eq(3).find("input").attr("name", distanceName + String(i)).attr("id", distanceName + String(i));
        tds.eq(7).find("input").attr("name", habitatIdName + String(i));

        $(this).next().find('textarea, input, label').each(function () {
            var currentName, namePrefix;

            if ($(this)[0].tagName.toLowerCase() === 'label' && $(this)[0].hasAttribute('for')) {
                currentName = $(this).attr("for");
                if (currentName !== undefined && currentName !== null) {
                    namePrefix = currentName.substring(0, currentName.indexOf("_") + 1);
                    $(this).attr("for", namePrefix + String(i));
                }
            } else {
                currentName = $(this).attr("name");
                if (currentName !== undefined && currentName !== null) {
                    namePrefix = currentName.substring(0, currentName.indexOf("_") + 1);
                    $(this).attr("name", namePrefix + String(i)).attr("id", namePrefix + String(i));
                }
            }
        });
    });
}

function addRow() {
    var location = $.getMessage("i18n.location_title");
    var bottom = $.getMessage("i18n.avg_bottom_composition_title");
    var bedrock = $.getMessage("i18n.bedrock_caption");
    var hardpanClay = $.getMessage("i18n.hardpan_clay_caption");
    var rubble = $.getMessage("i18n.rubble_caption");
    var gravel = $.getMessage("i18n.gravel_caption");
    var sand = $.getMessage("i18n.sand_caption");
    var siltDetritus = $.getMessage("i18n.silt_detritus_caption");
    var sediments = $.getMessage("i18n.clay_sediments_caption");
    var channelStructure = $.getMessage("i18n.channel_structure_title");
    var pools = $.getMessage("i18n.pools_caption");
    var riffles = $.getMessage("i18n.riffles_caption");
    var eddy = $.getMessage("i18n.eddy_lagoon_caption");
    var therun = $.getMessage("i18n.run_caption");
    var avgInstreamShore = $.getMessage("i18n.avg_instream_shoreline_structure_title");
    var overHang = $.getMessage("i18n.bank_overhang_caption");
    var vegetation = $.getMessage("i18n.vegetation_caption");
    var woody = $.getMessage("i18n.woody_debris_caption");
    var algae = $.getMessage("i18n.algae_caption");
    var grasses = $.getMessage("i18n.shoreline_grasses_caption");
    var trees = $.getMessage("i18n.shoreline_trees_caption");

    var expand = $.getMessage("i18n.btn.expand");
    var add = $.getMessage("i18n.btn.add");
    var remove = $.getMessage("i18n.btn.remove");


    var strHTML = '';
    var rowLength = $('#hi_table >tbody >tr').length + 1;

    var cell_01 = '<tr><td class="text-center"><input type="text" class="input-sm" readonly="true" size="1" /></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" name="#" placeholder="" max="9999.9" min="0" data-rule-number="true" maxlength="6" /></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" name="#" placeholder="" max="99.9" min="0" data-rule-number="true" maxlength="4" /></td>';
    var cell_04 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" name="#" placeholder="" max="999.9" min="0" data-rule-number="true" maxlength="5" /></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button" title="' + expand + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_06 = '<td class="text-center icon"><a href="#" role="button" title="' + remove + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_07 = '<td class="text-center icon"><a href="#" role="button" title="' + add + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_08 = '<td><input type="hidden" value=""/></td></tr>'

    var cell_09 = '<tr class="clickable" hidden><td colspan="4"><div class="col-md-12">';
    var cell_10 = '<div class="row col-md-12 mrgn-tp-md col-sm-offset-1"><label for="location_"><span class="field-name"> ' + location + '</span></label><br/><textarea class="input-sm" name="location_" id="location_" rows="2" cols="50" maxlength="250"></textarea></div>';
    var cell_11 = '<div class="row col-sm-12 mrgn-tp-md mrgn-bttm-sm mrgn-left-sm col-sm-offset-1"><h5 class="remove_margin" style="color: #6A5ACD">' + bottom + '</h5></div>';
    var cell_12 = '<div class="col-sm-12"><div class="row col-sm-3 col-sm-offset-1 mrgn-bttm-sm"><label for="bedrock_"><span class="field-name">' + bedrock + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="bedrock_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_13 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="hardpanClay_"><span class="field-name">' + hardpanClay + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="hardpanClay_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_14 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="rubble_"><span class="field-name">' + rubble + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="rubble_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_15 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="gravel_"><span class="field-name">' + gravel + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="gravel_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div></div>';
    var cell_16 = '<div class="col-sm-12"><div class="row col-sm-3 col-sm-offset-1 mrgn-bttm-sm"><label for="sand_"><span class="field-name">' + sand + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="sand_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_17 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="siltDetritus_"><span class="field-name">' + siltDetritus + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="siltDetritus_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_18 = '<div class="row col-sm-6 col-md-3 mrgn-bttm-md"><label for="claySediments_"><span class="field-name">' + sediments + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="claySediments_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div></div>';

    var cell_19 = '<div class="row col-sm-12 mrgn-tp-md mrgn-bttm-sm mrgn-left-sm col-sm-offset-1"><h5 class="remove_margin" style="color: #6A5ACD">' + channelStructure + '</h5></div>';
    var cell_20 = '<div class="col-sm-12"><div class="row col-sm-3 col-sm-offset-1 mrgn-bttm-sm"><label for="pools_"><span class="field-name">' + pools + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="pools_" placeholder="" value="0" max="100" min="0"data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_21 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="riffles_"><span class="field-name">' + riffles + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="riffles_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_22 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="eddyLagoon_"><span class="field-name">' + eddy + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="eddyLagoon_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_23 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="run_"><span class="field-name">' + therun + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="run_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div></div>';

    var cell_24 = '<div class="row col-sm-12 mrgn-tp-md mrgn-bttm-sm mrgn-left-sm col-sm-offset-1"><h5 class="remove_margin" style="color: #6A5ACD">' + avgInstreamShore + '</h5></div>';
    var cell_25 = '<div class="col-sm-12"><div class="row col-sm-3 col-sm-offset-1 mrgn-bttm-sm"><label for="vegetation_"><span class="field-name">' + vegetation + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="vegetation_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_26 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="woodyDebris_"><span class="field-name">' + woody + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="woodyDebris_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_27 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="algae_"><span class="field-name">' + algae + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="algae_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div></div>';
    var cell_28 = '<div class="col-sm-12"><div class="row col-sm-3 col-sm-offset-1 mrgn-bttm-sm"><label for="grasses_"><span class="field-name">' + grasses + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="grasses_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_29 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="trees_"><span class="field-name">' + trees + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="trees_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div>';
    var cell_30 = '<div class="row col-sm-3 mrgn-bttm-sm"><label for="bankOverhang_"><span class="field-name">' + overHang + '</span></label><br/><input type="text" class="input-sm" size="4" maxlength="3" name="bankOverhang_" placeholder="" value="0" max="100" min="0" data-rule-digits="true" /><label>&nbsp;%</label></div></div>';
    var cell_31 = '</div></td></tr>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10 + cell_11 + cell_12 + cell_13 + cell_14 + cell_15 + cell_16
        + cell_17 + cell_18 + cell_19 + cell_20 + cell_21 + cell_22 + cell_23 + cell_24 + cell_25 + cell_26 + cell_27 + cell_28 + cell_29 + cell_30 + cell_31;

    $('#hi_table > tbody:last').append($.parseHTML(strHTML));

    var allRows = $('#hi_table').children('tbody').children().not(".clickable");
    if (allRows.length > 1) {
        $('#hi_table').children('tbody').children().not(".clickable").eq(allRows.length - 2).find('td').eq(6).html('');
    }
    setForm();
}

function setForm() {
    assignNamesToTableRow();
    resetIndexToOuterTableRow('#hi_table', ".clickable");
    addActionListener();
    addValidationListener('#edit_form');
}

function addActionListener() {


}

function validateTransectBottom() {
    var flag = true;

    var allrows = $('#hi_table').children('tbody').children().not(".clickable").length;

    var i;

    $("#displayMessage").html("");

    for (i = 0; i < allrows; i++) {
        var bedrock = $.trim($('#bedrock_' + String(i)).val());
        var hardpan_clay = $.trim($('#hardpanClay_' + String(i)).val());
        var rubble = $.trim($('#rubble_' + String(i)).val());
        var gravel = $.trim($('#gravel_' + String(i)).val());
        var sand = $.trim($('#sand_' + String(i)).val());
        var silt_det = $.trim($('#siltDetritus_' + String(i)).val());
        var clay_sed = $.trim($('#claySediments_' + String(i)).val());

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

        if (rubble == null || rubble.length == 0 || isNaN(rubble)) {
            rubble = 0;
        } else {
            rubble = parseInt(rubble);
        }

        if (gravel == null || gravel.length == 0 || isNaN(gravel)) {
            gravel = 0;
        } else {
            gravel = parseInt(gravel);
        }

        if (sand == null || sand.length == 0 || isNaN(sand)) {
            sand = 0;
        } else {
            sand = parseInt(sand);
        }

        if (silt_det == null || silt_det.length == 0 || isNaN(silt_det)) {
            silt_det = 0;
        } else {
            silt_det = parseInt(silt_det);
        }
        if (clay_sed == null || clay_sed.length == 0 || isNaN(clay_sed)) {
            clay_sed = 0;
        } else {
            clay_sed = parseInt(clay_sed);
        }

        bottomTotal = bedrock + hardpan_clay + rubble + gravel + sand + silt_det + clay_sed;

        if (bottomTotal !== 100 && bottomTotal !== 0) {
            flag = false;
            showErrorMessage($.getMessage("il8n.transect_bottom_warning_message_param", [i + 1]), null, "displayMessage");
        }
    }
    return flag;
}

function validateChannelStructure() {
    var flag = true;

    var allrows = $('#hi_table').children('tbody').children().not(".clickable").length;

    var i;

    $("#displayMessage").html("");

    for (i = 0; i < allrows; i++) {
        var pools = $.trim($('#pools_' + String(i)).val());
        var riffles = $.trim($('#riffles_' + String(i)).val());
        var eddyLagoon = $.trim($('#eddyLagoon_' + String(i)).val());
        var therun = $.trim($('#run_' + String(i)).val());

        var channelTotal;

        if (pools == null || pools.length == 0 || isNaN(pools)) {
            pools = 0;
        } else {
            pools = parseInt(pools);
        }

        if (riffles == null || riffles.length == 0 || isNaN(riffles)) {
            riffles = 0;
        } else {
            riffles = parseInt(riffles);
        }

        if (eddyLagoon == null || eddyLagoon.length == 0 || isNaN(eddyLagoon)) {
            eddyLagoon = 0;
        } else {
            eddyLagoon = parseInt(eddyLagoon);
        }

        if (therun == null || therun.length == 0 || isNaN(therun)) {
            therun = 0;
        } else {
            therun = parseInt(therun);
        }

        channelTotal = pools + riffles + eddyLagoon + therun;

        if (channelTotal !== 100 && channelTotal !== 0) {
            flag = false;
            showErrorMessage($.getMessage("i18n.channel_structure_warning_message_param", [i + 1]), null, "displayMessage");
        }
    }
    return flag;
}

function isOneEmptyRow() {
    var allRows = $('#hi_table').children('tbody').children().not(".clickable");
    if (allRows.length == 1) {
        var tds = allRows.eq(0).find('td');
        if ((tds.eq(1).find('input').val() === undefined || $.trim(tds.eq(1).find('input').val()).length == 0) && (tds.eq(2).find('input').val() === undefined || $.trim(tds.eq(2).find('input').val()).length == 0))
            return true;
    }
    return false;
}

function getHabitatsFormData() {
    var formData = getFormData("#edit_form");
    formData.numOfHabitats = $('#hi_table').children('tbody').children().not(".clickable").length;
    formData.fmOffLine_Id = "#";

    return formData;
}
