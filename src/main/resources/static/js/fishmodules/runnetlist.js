$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;

    var table = $('#runnet_list_table').DataTable();

    $.isOnline().then(function (isOnline) {
        if (isOnline) {
            var loc = window.location.href;
            if (loc.indexOf('#') !== -1)
                table = loadOfflineRunnetList();
            else
                table = loadOnlineRunnetList();
        } else {
            table = loadOfflineRunnetList();
        }
    });

    setForm();
    singleTableRowOnSelect('#runnet_list_table');
    form_original_data = $("#edit_form").serialize();
    $("#loader").css("visibility", "hidden");

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#runnet_list_table")) {
            e.preventDefault();
            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    var loc = window.location.href;
                    if (loc.indexOf('#') !== -1) {
                        var offlineId = $('#runnet_list_table').DataTable().row($('tr.selected')).data()["fmOffLine_Id"];
                        editRunnetOffline(offlineId);
                    } else {
                        var id = table.row($('tr.selected')).data()["id"];
                        editRunnet(id);
                    }
                } else {
                    var offlineId = $('#runnet_list_table').DataTable().row($('tr.selected')).data()["fmOffLine_Id"];
                    editRunnetOffline(offlineId);
                }
            });
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#runnet_list_table")) {
            e.preventDefault();
            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    var loc = window.location.href;
                    if (loc.indexOf('#') !== -1) {
                        var offlineId = $('#runnet_list_table').DataTable().row($('tr.selected')).data()["fmOffLine_Id"];
                        deleteOfflineData("#runnet_list_table", "fish_modules", offlineId, true, true);
                    } else {
                        var id = table.row($('tr.selected')).data()["id"];
                        deleteRunnet(id);
                    }
                } else {
                    var offlineId = $('#runnet_list_table').DataTable().row($('tr.selected')).data()["fmOffLine_Id"];
                    deleteOfflineData("#runnet_list_table", "fish_modules", offlineId, true, true);
                }
            });
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_add').click(function () {
        $('#runnet_form').show();
        $('#runnetNumber').prop("readonly", false);
        $('#runnetNumber').prop("required", true);
        $('#runnetNumber').focus();
        //$('#streamCode').prop("readonly", false);
        $('#runnet_id').attr("value", "#");
    });

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        $.isOnline().then(function (isOnline) {
            var redirectUrl;
            if (isOnline) {
                var loc = window.location.href;
                if (loc.indexOf('#') !== -1) {
                    redirectUrl = getBaseString() + localizedName + "/fishmodules/main/offline#" + getOfflineId();
                } else
                    redirectUrl = getBaseString() + localizedName + "/fishmodules/main?fmId=" + $('#referfm_id').val();
            } else {
                redirectUrl = getBaseString() + localizedName + "/fishmodules/main/offline#" + getOfflineId();
            }
            confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
        });
    });

    function editRunnet(id) {
        $('#runnet_form').show();
        $('#runnetNumber').prop("readonly", true);
        $('#runnetNumber').prop("required", false);
        $('#personElecFishing').focus();

        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/runnet/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                loadOnlineRunnetData(response.data);
                $('#runnet_id').attr("value", id)
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function editRunnetOffline(id) {
        $('#runnet_form').show();
        $('#runnetNumber').prop("readonly", false);
        $('#runnetNumber').prop("required", true);
        $('#runnetNumber').focus();

        loadRunnetOfflineData(id);
    }

    function deleteRunnet(id) {
        //var localizedName = getLocale();
        $.ajax({
            type: "DELETE",
            url: getBaseString() + localizedName + "/api/runnet/delete/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetForm();
                // $('#runnet_list_table').DataTable().ajax.reload();
                $('#runnet_list_table').DataTable().row('.selected').remove().draw(
                    false);
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    $('#button_cancel').click(function (event) {
        resetForm();
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        /*if (!validateMeasuredDuration()) {
            showErrorMessage($.getMessage("i18n.electrofish_time_message"));
        }*/
        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {

            var formData = getRunnetFormData();

            $.isOnline().then(function (isOnline) {
                if (isOnline) {
                    sendOnlineForm(formData);
                } else {
                    createOfflineRunnet("-RUNNET", formData);
                }
            });
        }
    });

    function resetForm() {
        $('#runnet_form').hide();
        $('#errors-edit_form').hide();
        $('#edit_form')[0].reset();
        $("#edit_form").validate().resetForm();
        $("#species_table tbody tr").remove();
        addRow();
        form_original_data = $("#edit_form").serialize();
    }

    $('#species_table').on('click', 'span.glyphicon-minus-sign', function (e) {
        e.preventDefault();

        if ($(this).closest("tr").prop("class") === "specie") {
            $(this).closest("tr").next().remove();
            $(this).closest("tr").remove();

            setForm();
            var allRows = $('#species_table').children('tbody').children().not(".clickable");
            if (allRows.length == 0) {
                addRow();
            } else {
                var lastRow = $('#species_table').children('tbody').children().not(".clickable").eq(allRows.length - 1);
                if (lastRow.find('.glyphicon-plus-sign').length == 0) {
                    lastRow.children('td').eq(5).html('<a href="#" role="button" title="' + $.getMessage("i18n.btn.add") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
        } else if ($(this).closest("tr").prop("class") === "individual") {
            var theParent = $(this).closest("tr").parent();
            $(this).closest("tr").remove();

            resetIndexToTableRow(theParent);
            setForm();

            if (theParent.children().length == 0) {
                addIndividualRow(theParent);
            } else {
                var lastIndividualRow = theParent.children().eq(theParent.children().length - 1);
                if (lastIndividualRow.find('.glyphicon-plus-sign').length == 0) {
                    lastIndividualRow.children('td').eq(8).html('<a href="#" role="button" title="' + $.getMessage("i18n.btn.add") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a>');
                }
            }
        }
    });

    $('#species_table').on('click', 'span.glyphicon-plus-sign', function (e) {
        e.preventDefault();
        if ($(this).closest("tr").prop("class") === "specie") {
            addRow();
        } else if ($(this).closest("tr").prop("class") === "individual") {
            var theParent = $(this).closest("tr").parent();
            addIndividualRow(theParent);
        }
        addValidationListener('#edit_form');
    });

    $('#species_table').on('click', 'span.glyphicon-expand', function (e) {
        e.preventDefault();
        var hide = $.getMessage("i18n.btn.hide");
        $(this).closest("tr").find('td').eq(3).find('span').addClass('glyphicon-collapse-down').removeClass('glyphicon-expand').attr("title", hide);
        $(this).closest("tr").next().toggle();
    });

    $('#species_table').on('click', 'span.glyphicon-collapse-down', function (e) {
        e.preventDefault();
        var expand = $.getMessage("i18n.btn.expand");
        $(this).closest("tr").find('td').eq(3).find('span').addClass('glyphicon-expand').removeClass('glyphicon-collapse-down').attr("title", expand);
        $(this).closest("tr").next().toggle();
    });

    $("#radios_elecsetting").change(function () {
        var currentValue = $("#radios_elecsetting input[type=radio]:checked").val();

        switch (currentValue) {
            case 'fish' :
                $("#peak_vol").val("250");
                $("#burst_mode").val("0");
                $("#slow_rate").val("6");
                $("#fast_rate").val("60");
                $("#slow_duty").val("10");
                $("#fast_duty").val("10");
                break;
            case 'other' :
                $("#peak_vol").val("");
                $("#burst_mode").val("");
                $("#slow_rate").val("");
                $("#fast_rate").val("");
                $("#slow_duty").val("");
                $("#fast_duty").val("");
                break;
        }
    });

    $("#measured_duration").keyup(function () {
        $this = $(this);
        setTimeValueThreeColonTwo($this);
    });

    function sendOnlineForm(formData) {
        var actionType, actionUrl;

        if ($('#runnet_id').val() != "#" && $.trim($('#runnet_id').val()).length > 0) {
            actionType = "PUT";
            actionUrl = getBaseString() + localizedName + "/api/runnet/" + $('#runnet_id').val();
        } else {
            actionType = "POST";
            actionUrl = getBaseString() + localizedName + "/api/runnet/add/" + $('#referfm_id').val();
        }

        $.ajax({
            type: actionType,
            url: actionUrl,
            data: formData,
            timeout: 6000,
            success: function (response) {
                $("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetForm();
                $('#runnet_list_table').DataTable().ajax.reload();
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                $("#displayInfoMessage").html("");
                showErrorMessage(err.message, null, "displayInfoMessage");
            }
        });
    }

    function createOfflineRunnet(idSuffix, dataJSON) {
        return new Promise(function (doReturn) {
            var offlineFMId = getOfflineId();

            $.db_getAndIncrementSeq('slims_seq').then(function (idSeq) {
                if (idSeq) {
                    var paddedId = $.padZeroes(idSeq, 4);
                    dataJSON.fmOffLine_Id = offlineFMId + idSuffix + paddedId;
                    createOrUpdateOfflineRunnet(dataJSON);
                } else {
                    $("#messageDiv").html("");
                    showErrorMessage($.getMessage("offlinedb_sequence_create_failed"), null, "messageDiv");
                    doReturn();
                }
            });
        });
    }

    function createOrUpdateOfflineRunnet(dataJSON) {
        $.db_insertOrUpdate('fish_modules', dataJSON).then(function (result) {
            if (result) {

                $("#displayInfoMessage").html("");
                showSuccessMessage($.getMessage("offlinedata_save_success"), null, "displayInfoMessage");
                resetForm();
                //window.location.reload();
                loadOfflineRunnetList();
                form_original_data = $("#edit_form").serialize();
            } else {
                $("#messageDiv").html("");
                showErrorMessage($.getMessage("offlinedata_save_failed"), null, "messageDiv");
                doReturn();
            }
        });
    }

});

function getRunnetFormData() {
    var formData = getFormData("#edit_form");
    getNumOfSpecies(formData);
    formData.fmOffLine_Id = "#";

    return formData;
}

function addRow() {
    var strHTML = '';
    var rowLength = $('#species_table').children('tbody').children().not(".clickable").length + 1;

    var cell_01 = '<tr class="specie"><td class="text-center"><label for="#"></label><input type="text" required="required" id="fmspecies02" name="fmspecies" class="input-sm" list="allspecies0' + rowLength + '"data-ap-auto-select-first="true"/><datalist id="allspecies0' + rowLength + '"></datalist></td>';
    var cell_02 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="5" name="#" value="0" data-rule-digits="true" maxlength="5" /></td>';
    var cell_03 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="5" name="#" value="0" data-rule-digits="true" maxlength="5" /></td>';
    var cell_04 = '<td class="text-center icon"><a href="#" role="button" title="' + $.getMessage("i18n.btn.expand") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-expand" aria-hidden="true"></span></a></td>';
    var cell_05 = '<td class="text-center icon"><a href="#" role="button" title="' + $.getMessage("i18n.btn.remove") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_06 = '<td class="text-center icon"><a href="#" role="button" title="' + $.getMessage("i18n.btn.add") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_07 = '<td><input type="hidden" value=""/></td></tr>';

    var cell_08 = '<tr class="clickable" hidden><td colspan="9"><div class="row col-md-12 col-sm-offset-1"><table id="myid" style="width: 90%; border-collapse: separate; border-spacing: 0 10px">';
    var cell_09 = '<thead><th></th><th class="text-center">' + $.getMessage("i18n.specimen_state_title") + '</th><th class="text-center">' + $.getMessage("i18n.fishsex_title") + '</th>';
    var cell_10 = '<th class="text-center">' + $.getMessage("i18n.kept_title") + '</th><th class="text-center">' + $.getMessage("i18n.fishlength_title") + '</th>';
    var cell_11 = '<th class="text-center">' + $.getMessage("i18n.fishweight_title") + '</th><th class="text-center">' + $.getMessage("i18n.date_measured_title") + '</th>';
    var cell_12 = '</th><th></th><th></th><th></th></thead><tbody>';

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10 + cell_11 + cell_12 + getInnerTableRow(rowLength) + '</tbody></table></div></td></tr>';

    $('#species_table').children('tbody').last().append($.parseHTML(strHTML));

    var speciesList = "#allspecies0" + rowLength;

    $.each(speciecodesList, function () {
        $(speciesList).append('<option value="' + this.showText + '"></input></option>');
        $(speciesList).children("option").last().append('<input type="hidden" value="' + this.id + '"></input>');
    });

    setIndividualDropdowns(rowLength);

    var allRows = $('#species_table').children('tbody').children().not(".clickable");
    if (allRows.length > 1) {
        $('#species_table').children('tbody').children().not(".clickable").eq(allRows.length - 2).find('td').eq(5).html('');
    }
    setForm();
}

function addIndividualRow(theParent) {
    var aboveRow = theParent.closest("tr").prev();
    /*var numTotal = aboveRow.find('td').eq(6).find('input').val();
    if (numTotal !== null && $.trim(numTotal).length > 0 && !isNaN(numTotal)) {
        if (theParent.children().length >= parseInt(numTotal)) {
            showErrorMessage($.getMessage("i18n.overindividuals_warning_message"));
            return;
        }
    }*/
    var strHTML = '';
    var rowLength = theParent.children().length + 1;

    strHTML = getInnerTableRow(rowLength);
    theParent.last().append($.parseHTML(strHTML));
    setIndividualDropdowns(rowLength);

    var allRows = theParent.children();
    if (allRows.length > 1) {
        theParent.children().eq(allRows.length - 2).find('td').eq(8).html('');
    }
    resetIndexToTableRow(theParent);
    setForm();
}

function setForm() {
    assignNamesToTableRow();
//addRowListener();
    addValidationListener('#edit_form');
    $('input[type="date"]').prop('max', getCurrentDateStr());
    $.applyAwesomplete("#species_table");
}

function assignNamesToTableRow() {
    var specieListName = "allSpecieList";
    var specieListId = "allSpecies";
    var fishSpecieName = "fishSpecies";
    var totalCaughtName = "totalCaught";
    var totalObservedName = "totalObserved";
    var specieIdName = "specieId";

    var specimenName = "specimen";
    var sexName = "fishSex";
    var keptName = "kept";
    var indiLengthName = "indiLen";
    var indiWeightName = "indiWeight";
    var dateMeasuredName = "dateMeasured";
    var individualIdName = "individualId";

    var allSpecieRows = $('#species_table').children('tbody').children().not(".clickable");

    allSpecieRows.each(function (i) {
        var tds = $(this).find('td');

        tds.eq(0).find("label").attr("for", specieListId + String(i));
        tds.eq(0).find("input").first().attr("name", fishSpecieName + String(i)).attr("id", specieListId + String(i)).attr("list", specieListName + String(i));
        tds.eq(0).find("datalist").attr("id", specieListName + String(i));
        tds.eq(1).find("label").attr("for", totalCaughtName + String(i));
        tds.eq(1).find("input").attr("name", totalCaughtName + String(i)).attr("id", totalCaughtName + String(i));
        tds.eq(2).find("label").attr("for", totalObservedName + String(i));
        tds.eq(2).find("input").attr("name", totalObservedName + String(i)).attr("id", totalObservedName + String(i));
        tds.eq(6).find("input").attr("name", specieIdName + String(i));

        var allIndiRows = $(this).next().find('tbody > tr');

        allIndiRows.each(function (j) {
            var inditds = $(this).find('td');
            inditds.eq(1).find("select").attr("id", fishSpecieName + String(i) + "_" + specimenName + String(j)).attr("name", fishSpecieName + String(i) + "_" + specimenName + String(j));
            inditds.eq(2).find("select").attr("id", fishSpecieName + String(i) + "_" + sexName + String(j)).attr("name", fishSpecieName + String(i) + "_" + sexName + String(j));
            inditds.eq(3).find("input").attr("name", fishSpecieName + String(i) + "_" + keptName + String(j));
            inditds.eq(4).find("label").attr("for", fishSpecieName + String(i) + "_" + indiLengthName + String(j));
            inditds.eq(4).find("input").attr("name", fishSpecieName + String(i) + "_" + indiLengthName + String(j)).attr("id", fishSpecieName + String(i) + "_" + indiLengthName + String(j));
            inditds.eq(5).find("label").attr("for", fishSpecieName + String(i) + "_" + indiWeightName + String(j));
            inditds.eq(5).find("input").attr("name", fishSpecieName + String(i) + "_" + indiWeightName + String(j)).attr("id", fishSpecieName + String(i) + "_" + indiWeightName + String(j));
            inditds.eq(6).find("input").attr("name", fishSpecieName + String(i) + "_" + dateMeasuredName + String(j));

            inditds.eq(9).find("input").attr("name", fishSpecieName + String(i) + "_" + individualIdName + String(j));
        });
    });
}

function getNumOfSpecies(theForm) {
    var numOfSpeciesName = "numOfSpecies";
    var numOfindividualsName = "_numOfIndi";
    var specieName = "specie";

    var allSpecieRows = $('#species_table').children('tbody').children().not(".clickable");
    theForm[numOfSpeciesName] = allSpecieRows.length;

    allSpecieRows.each(function (i) {
        var allIndiRows = $(this).next().find('tbody > tr');
        theForm[specieName + String(i) + numOfindividualsName] = allIndiRows.length;
    });
}

function getSpecieText(listObj, specieCode) {
    var spText = '';
    listObj.find("option").each(function () {
        if ($(this).val().includes("(" + specieCode + ")")) {
            spText = $(this).val();
            return spText;
        }
    });
    return spText;
}

function setIndividualDropdowns(rowLength) {
    var specimenId = "#fmSpecimenState_0" + rowLength;
    var sexId = "#fmFishSex_0" + rowLength;

    $.each(speciemenStateList, function () {
        $(specimenId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });

    $.each(fishSexList, function () {
        if (this.codeName == 'sex2')
            $(sexId).append($("<option />").attr('value', this.codeName).attr('selected', "selected").text(this.showText));
        else
            $(sexId).append($("<option />").attr('value', this.codeName).text(this.showText));
    });
}

function getInnerTableRow(rowLength) {
    var strHTML = '';

    var cell_01 = '<tr class="individual"><td class="text-center"><input type="text" class="input-sm" value="1" readonly="true" size="1" /></td>';
    var cell_02 = '<td class="text-center"><select class="input-sm" id="fmSpecimenState_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_03 = '<td class="text-center"><select class="input-sm" id="fmFishSex_0' + rowLength + '"><option value=""></option></select></td>';
    var cell_04 = '<td class="text-center"><label class="radio-inline"><input type="radio" id="radio_yes" name="optradio_kept0' + rowLength + '" checked="checked" value="yes"/>' + $.getMessage("i18n.yes_title") + '</label><label class="radio-inline"><input type="radio" id="radio_no" name="optradio_kept0' + rowLength + '" value="no" />' + $.getMessage("i18n.no_title") + '</label></td>';
    var cell_05 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="2" name="#" value="0" data-rule-digits="true" maxlength="4" /></td>';
    var cell_06 = '<td class="text-center"><label for="#"></label><input type="text" class="input-sm" size="12" name="#" value="0" data-rule-number="true" max="999999.99" min="0" maxlength="9" /></td>';
    var cell_07 = '<td class="text-center"><input type="date" class="input-sm" name="#" /></td>';
    var cell_08 = '<td class="text-center icon"><a href="#" role="button" title="' + $.getMessage("i18n.btn.remove") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-minus-sign" aria-hidden="true"></span></a></td>';
    var cell_09 = '<td class="text-center icon"><a href="#" role="button" title="' + $.getMessage("i18n.btn.add") + '"><span style="font-size: 1.3em;" class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span></a></td>';
    var cell_10 = '<td><input type="hidden" value=""/></td></tr>'

    strHTML = cell_01 + cell_02 + cell_03 + cell_04 + cell_05 + cell_06 + cell_07 + cell_08 + cell_09 + cell_10;

    return strHTML;
}

function loadOfflineRunnetList() {
    $('#offlineid').show();
    $('#offline_id').text(getOfflineId());
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#runnet_list_table')) {
        $('#runnet_list_table').DataTable().destroy();
    }

    $('#runnet_list_table tbody').empty();

    $.db_getAll('fish_modules').then(function (results) {
        if (results !== undefined && results !== null) {
            var modules = [];
            $.each(results, function () {
                var obj = this;
                if (obj.fmOffLine_Id !== undefined && obj.fmOffLine_Id !== null && obj.fmOffLine_Id.indexOf('-RUNNET') !== -1) {
                    modules.push(obj);
                }
            });

            var table = $('#runnet_list_table').DataTable({
                "data": modules,
                "order": [[0, "asc"]],
                "aoColumns": [{
                    "mData": "runnetNumber"
                }, {
                    "mData": "numOfSpecies"
                },
                    {}
                ],
                "columnDefs": [{
                    data: "fmOffLine_Id",
                    "targets": 2,
                    "searchable": false,
                    "orderable": false,
                    "visible": false,
                    "render": function (data, type, full, meta) {
                        return '<input type="hidden" value="' + data + '"/>';
                    }
                }],
                "language": {
                    "url": getLanguageURL()
                }
            });
            return table;
        }
    });
}

function loadOnlineRunnetList() {
    $('#offlineid').hide();
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#runnet_list_table')) {
        $('#runnet_list_table').DataTable().destroy();
    }
    $('#runnet_list_table tbody').empty();

    var table = $('#runnet_list_table').DataTable({
        "sAjaxSource": getBaseString() + localizedName + "/api/runnet/fm/" + $('#referfm_id').val(),
        "sAjaxDataProp": "",
        "order": [[0, "asc"]],
        "deferRender": true,
        //"stateSave": true,
        "aoColumns": [{
            "mData": "runNetNumber"
        }, {
            "mData": function (data, type, dataToSet) {
                return data.species.length;
            }
        },
            {}
        ],
        "columnDefs": [{
            data: "id",
            "targets": 2,
            "searchable": false,
            "orderable": false,
            "visible": false,
            "render": function (data, type, full, meta) {
                return '<input type="hidden" value="' + data + '"/>';
            }
        }],
        "language": {
            "url": getLanguageURL()
        }
    });
    return table;
}

function loadOnlineRunnetData(data) {
    if (data != null) {
        if ((data.runNetNumber !== undefined) && (data.runNetNumber !== null))
            $('#runnetNumber').val(data.runNetNumber);

        if ((data.personElectroFishing !== undefined) && (data.personElectroFishing !== null))
            $('#personElecFishing').val(data.personElectroFishing);

        if ((data.personCatching !== undefined) && (data.personCatching !== null))
            $('#personCatching').val(data.personCatching);

        if ((data.estduration !== undefined) && (data.estduration !== null))
            $('#est_duration').val(data.estduration);

        if ((data.measuredDuration !== undefined) && (data.measuredDuration !== null))
            $('#measured_duration').val(data.measuredDuration);

        if (data.electroFishType == "fish")
            $('#radio_fish').prop("checked", true);
        else
            $('#radio_other').prop("checked", true);

        if ((data.peakVolt !== undefined) && (data.peakVolt !== null))
            $('#peak_vol').val(data.peakVolt);

        if ((data.burstRate !== undefined) && (data.burstRate !== null))
            $('#burst_mode').val(data.burstRate);

        if ((data.slowRate !== undefined) && (data.slowRate !== null))
            $('#slow_rate').val(data.slowRate);

        if ((data.fastRate !== undefined) && (data.fastRate !== null))
            $('#fast_rate').val(data.fastRate);

        if ((data.slowDuty !== undefined) && (data.slowDuty !== null))
            $('#slow_duty').val(data.slowDuty);

        if ((data.fastDuty !== undefined) && (data.fastDuty !== null))
            $('#fast_duty').val(data.fastDuty);

        loadOnlineSpeciesData(data);

        form_original_data = $("#edit_form").serialize();
    }
}

function loadOnlineSpeciesData(data) {

    if (data.species.length > 0) {
        $.each(data.species, function (i) {

            if ($('#species_table').children('tbody').children().not(".clickable").length != (i + 1)) {
                addRow();
            }
            var lastRow = $('#species_table').children('tbody').children().not(".clickable").last();
            var tds = lastRow.find('td');

            tds.eq(0).find('input').first().val(getSpecieText(tds.eq(0).find('datalist'), this.speciesCode));
            tds.eq(1).find('input').first().val(this.totalcaught);
            tds.eq(2).find('input').first().val(this.totalObserved);
            tds.eq(6).find('input').first().val(this.id);

            var theParent = lastRow.next().find('tbody');

            if (this.fishIndividuals.length > 0) {
                var individuals = this.fishIndividuals;
                $.each(individuals, function (j) {
                    if (theParent.find('tr').length != (j + 1)) {
                        addIndividualRow(theParent);
                    }
                    var indiLastRow = theParent.find('tr').last();
                    var indiTds = indiLastRow.find('td');
                    indiTds.eq(0).find('input').val(j + 1);
                    if (this.specimenState !== null)
                        indiTds.eq(1).find('select option[value="' + this.specimenState.codePair.codeName + '"]').prop('selected', true);
                    if (this.individualSex !== null)
                        indiTds.eq(2).find('select option[value="' + this.individualSex.codePair.codeName + '"]').prop('selected', true);
                    if (this.kept) {
                        indiTds.eq(3).find('input[value="yes"]').prop('checked', true);
                    } else {
                        indiTds.eq(3).find('input[value="no"]').prop('checked', true);
                    }
                    indiTds.eq(4).find('input').val(this.individualLength);
                    indiTds.eq(5).find('input').val(this.individualWeight);
                    indiTds.eq(6).find('input').val(this.dateMeasured);
                    indiTds.eq(9).find('input').val(this.id);
                });
            }
        });
        //form_original_data = $("#edit_form").serialize();
    }
}

function loadRunnetOfflineData(offlineRunnetId) {

    $.db_getByKeyPath('fish_modules', offlineRunnetId).then(function (result) {
        if (result) {
            if ((result.runnetNumber !== undefined) && (result.runnetNumber !== null))
                $('#runnetNumber').val(result.runnetNumber);

            if ((result.personElecFishing !== undefined) && (result.personElecFishing !== null))
                $('#personElecFishing').val(result.personElecFishing);

            if ((result.personCatching !== undefined) && (result.personCatching !== null))
                $('#personCatching').val(result.personCatching);

            if ((result.est_duration !== undefined) && (result.est_duration !== null))
                $('#est_duration').val(result.est_duration);

            if ((result.measured_duration !== undefined) && (result.measured_duration !== null))
                $('#measured_duration').val(result.measured_duration);

            if (result.optradio_elecsetting == "fish")
                $('#radio_fish').prop("checked", true);
            else
                $('#radio_other').prop("checked", true);

            if ((result.peak_vol !== undefined) && (result.peak_vol !== null))
                $('#peak_vol').val(result.peak_vol);

            if ((result.burst_mode !== undefined) && (result.burst_mode !== null))
                $('#burst_mode').val(result.burst_mode);

            if ((result.slow_rate !== undefined) && (result.slow_rate !== null))
                $('#slow_rate').val(result.slow_rate);

            if ((result.fast_rate !== undefined) && (result.fast_rate !== null))
                $('#fast_rate').val(result.fast_rate);

            if ((result.slow_duty !== undefined) && (result.slow_duty !== null))
                $('#slow_duty').val(result.slow_duty);

            if ((result.fast_duty !== undefined) && (result.fast_duty !== null))
                $('#fast_duty').val(result.fast_duty);

            var numOfSpecies = result.numOfSpecies;
            var i;
            for (i = 0; i < numOfSpecies; i++) {
                if ($('#species_table').children('tbody').children().not(".clickable").length != (i + 1)) {
                    addRow();
                }
                var lastRow = $('#species_table').children('tbody').children().not(".clickable").last();
                var tds = lastRow.find('td');

                var speciesCode = result["fishSpecies" + String(i)];

                tds.eq(0).find('input').first().val(getSpecieText(tds.eq(0).find('datalist'), getSpeciesCode(speciesCode)));
                tds.eq(1).find('input').first().val(result["totalCaught" + String(i)]);
                tds.eq(2).find('input').first().val(result["totalObserved" + String(i)]);

                var theParent = lastRow.next().find('tbody');

                if (result["specie" + String(i) + "_numOfIndi"] !== undefined && result["specie" + String(i) + "_numOfIndi"] !== null) {
                    var numOfIndis = result["specie" + String(i) + "_numOfIndi"];
                    var j;
                    for (j = 0; j < numOfIndis; j++) {
                        if (theParent.find('tr').length != (j + 1)) {
                            addIndividualRow(theParent);
                        }
                        var indiLastRow = theParent.find('tr').last();
                        var indiTds = indiLastRow.find('td');
                        indiTds.eq(0).find('input').val(j + 1);
                        indiTds.eq(1).find('select option[value="' + result["fishSpecies" + String(i) + "_specimen" + String(j)] + '"]').prop('selected', true);
                        indiTds.eq(2).find('select option[value="' + result["fishSpecies" + String(i) + "_fishSex" + String(j)] + '"]').prop('selected', true);
                        if (result["fishSpecies" + String(i) + "_kept" + String(j)] === "yes") {
                            indiTds.eq(3).find('input[value="yes"]').prop('checked', true);
                        } else {
                            indiTds.eq(3).find('input[value="no"]').prop('checked', true);
                        }
                        indiTds.eq(4).find('input').val(result["fishSpecies" + String(i) + "_indiLen" + String(j)]);
                        indiTds.eq(5).find('input').val(result["fishSpecies" + String(i) + "_indiWeight" + String(j)]);
                        indiTds.eq(6).find('input').val(result["fishSpecies" + String(i) + "_dateMeasured" + String(j)]);
                    }
                }
            }
        }
    });
}