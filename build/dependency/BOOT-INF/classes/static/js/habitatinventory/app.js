$(document).on('wb-ready.wb', function () {

    var localizedName = getLocale();

    var table_online = $('#hi_list_table_online').DataTable();
    var table_offline = $('#hi_list_table_offline').DataTable();
    
    setDefaultFilterYear();

    $.isOnline().then(function (isOnline) {
        if (isOnline) {
            showOnline();
            table_online = loadHIOnlineList();
            setShowOfflineButton('habitat_inventory');
        } else {
            showOffline();
            $('#button_add_offline').prop('disabled', false);
            table_offline = loadHIOfflineList();

            $('#button_sync').hide();
            $('#button_showonline').hide();
        }
    });

    singleTableRowOnSelect('#hi_list_table_online');
    multipleTableRowOnSelect("#hi_list_table_offline");
    $("#loader").css("visibility", "hidden");

    $('#button_showoffline').click(function (e) {
        e.preventDefault();
        showOffline();
        table_offline = loadHIOfflineList();
        $('#button_showonline').show();
        $('#button_sync').show();
    });

    $('#button_showonline').click(function (e) {
        e.preventDefault();
        showOnline();
        table_online = loadHIOnlineList();
        setShowOfflineButton('habitat_inventory');
    });

    $('#button_add').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/habitatinventory/main";
    });
    $('#button_add_offline').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/habitatinventory/main/offline";
    });

    $('#button_edit').click(function (e) {
        e.preventDefault();
        if (thereAreRowsSelected("#hi_list_table_online")) {
            var id = $('#hi_list_table_online').DataTable().row($('tr.selected')).data()["id"];
            window.location.href = getBaseString() + localizedName + "/habitatinventory/main?hiId=" + id;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_edit_offline').click(function (e) {
        e.preventDefault();
        if ($('#hi_list_table_offline').find('tr.selected').length > 1) {
            $("#displayInfoMessageOffline").html("");
            showErrorMessage($.getMessage("multiple_edit_not_support"), null, "displayInfoMessageOffline");
        } else {
            if (thereAreRowsSelected("#hi_list_table_offline")) {
                var offlineId = $('#hi_list_table_offline').DataTable().row($('tr.selected')).data()["hiOffLine_Id"];
                window.location.href = getBaseString() + localizedName + "/habitatinventory/main/offline#" + offlineId;
            } else {
                $("#displayInfoMessageOffline").html("");
                $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessageOffline");
            }
        }
    });

    $('#button_sync').click(function (e) {
        e.preventDefault();
        syncOfflineHIs();
    });

    $('#btnRefresh').click(function (e){
        loadHIOnlineList();
    });

    function syncOfflineHIs() {
        var offlineIds = [];

        var allSelectedRows = $('#hi_list_table_offline').find('tr.selected');
        allSelectedRows.each(function () {
            var offlineId = $(this).find('td').eq(0).text();
            var runnetId, runnetSpeciesId, habitatsId;
            if (!isEmpty(offlineId)) {
                offlineIds.push(offlineId);
            }
        });

        $.db_getAll('habitat_inventory').then(function (results) {
            var syncObjs = [];
            if (results !== undefined && results !== null && results.length > 0) {
                $.each(results, function () {
                    var obj = this;
                    $.each(offlineIds, function () {
                        if (obj.hiOffLine_Id !== undefined) {
                            if (obj.hiOffLine_Id.includes(this)) {
                                syncObjs.push(obj);
                            }
                        }
                    });
                });
                $.ajax({
                    type: "POST",
                    contentType: "application/json",
                    url: getBaseString() + localizedName + "/api/hi/sync",
                    data: JSON.stringify(syncObjs),
                    dataType: 'json',
                    timeout: 6000,
                    success: function (response) {
                        $("#displayInfoMessage").html("");
                        showSuccessMessage(response.message, null, "displayInfoMessage");
                        deleteSyncedOfflineData(syncObjs);
                    },
                    error: function (response) {
                        var err = JSON.parse(response.responseText);
                        $("#displayInfoMessage").html("");
                        showErrorMessage(err.message, null, "displayInfoMessage");
                    }
                });
            }
        });
    }

    function deleteSyncedOfflineData(syncObjs) {
        $.each(syncObjs, function () {
            if (this.hiOffLine_Id !== undefined && this.hiOffLine_Id !== null) {
                if (this.hiOffLine_Id.indexOf('-TRANSECT') == -1)
                    deleteOfflineData('#hi_list_table_offline', 'habitat_inventory', this.hiOffLine_Id, false, true);
                else
                    deleteOfflineDataFromLocalDB('habitat_inventory', this.hiOffLine_Id, false, true);
            }
        });
    }

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#hi_list_table_online")) {
            e.preventDefault();
            var id = $('#hi_list_table_online').DataTable().row($('tr.selected')).data()["id"];
            deleteHi(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete_offline').click(function (e) {
        if (thereAreRowsSelected("#hi_list_table_offline")) {
            e.preventDefault();
            if ($('#hi_list_table_offline').find('tr.selected').length > 1) {
                $("#displayInfoMessageOffline").html("");
                showErrorMessage($.getMessage("multiple_delete_not_support"), null, "displayInfoMessageOffline");
            } else {
                var offlineId = $('#hi_list_table_offline').DataTable().row($('tr.selected')).data()["hiOffLine_Id"];
                deleteOfflineData('#hi_list_table_offline', "habitat_inventory", offlineId, true, true);
            }
        } else {
            $("#displayInfoMessageOffline").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessageOffline");
        }
    });

    function loadHIOfflineList() {
        if ($.fn.DataTable.isDataTable('#hi_list_table_offline')) {
            $('#hi_list_table_offline').DataTable().destroy();
        }
        $('#hi_list_table_offline tbody').empty();
        $.db_getAll('habitat_inventory').then(function (results) {
            if (results !== undefined && results !== null) {

                var modules = [];
                $.each(results, function () {
                    var obj = this;
                    if (obj.hiOffLine_Id !== undefined && obj.hiOffLine_Id !== null && obj.hiOffLine_Id.indexOf('-TRANSECT') == -1) {
                        modules.push(obj);
                    }
                });
                table_offline = $('#hi_list_table_offline').DataTable({
                    "data": modules,
                    //"select": true,
                    "order": [[0, "asc"]],
                    "aoColumns": [{
                        "mData": "hiOffLine_Id"
                    }, {
                        "mData": "transectId"
                    }, {
                        "mData": "inventoryDate"
                    }, {
                        "mData": "hiLake"
                    }, {
                        "mData": "hiStream"
                    }, {
                        "mData": "hiBranch"
                    }, {
                        "mData": "hiStationFrom"
                    }, {
                        "mData": "hiStationTo"
                    }, {
                        "mData": "updateDate"
                    }],
                    "language": {
                        "url": getLanguageURL()
                    }
                });
            }
        });
    }
});

function deleteHi(id) {
    var localizedName = getLocale();

    $.ajax({
        type: "DELETE",
        url: getBaseString() + localizedName + "/api/hi/delete/" + id,
        dataType: "json",
        async: true,
        success: function (response) {
            $("#displayInfoMessage").html("");
            showSuccessMessage(response.message, null, "displayInfoMessage");
            $('#hi_list_table_online').DataTable().row('.selected').remove().draw(false);
        },
        error: function (response) {
            $("#displayInfoMessage").html("");
            var err = JSON.parse(response.responseText);
            showErrorMessage(err.message, null, "displayInfoMessage");
        }
    });
}

function loadHIOnlineList() {
    var localizedName = getLocale();

    if ($.fn.DataTable.isDataTable('#hi_list_table_online')) {
        $('#hi_list_table_online').DataTable().destroy();
    }
    $('#hi_list_table_online tbody').empty();

    var yearOperator = $("#yearAdjust").val();
    
    var yearFilter = $("#filterYear").val();

    if(yearFilter == null){
        yearFilter = new Date().getFullYear();
    }

    var table = $('#hi_list_table_online').DataTable({
        //"sAjaxSource": getBaseString() + localizedName + "/api/his?year=" + yearFilter,
        "sAjaxSource": getBaseString() + localizedName + "/api/his?yearOp=" + yearOperator + "&year=" + yearFilter,
        "sAjaxDataProp": "",
        "order": [[0, "asc"]],
        "deferRender": true,
        //"stateSave": true,
        "aoColumns": [{
            "mData": function (data, type, dataToSet) {
                return data.sampleCode;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.transectId;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.inventoryDate;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.lake;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.stream;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.branchLentic;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.stationFrom;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.stationTo;
            }
        }, {}],
        "columnDefs": [{
            data: "id",
            "targets": 8,
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
