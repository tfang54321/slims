$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();

    var table_online = $('#fm_list_table_online').DataTable();
    var table_offline = $('#fm_list_table_offline').DataTable();
    
    setDefaultFilterYear();

    $.isOnline().then(function (isOnline) {
        if (isOnline) {
            showOnline();
            table_online = loadFMOnlineList();
            setShowOfflineButton('fish_modules');
        } else {
            showOffline();
            $('#button_add_offline').prop('disabled', false);
            table_offline = loadFMOfflineList();

            $('#button_sync').hide();
            $('#button_showonline').hide();
        }
    });

    singleTableRowOnSelect('#fm_list_table_online');
    multipleTableRowOnSelect("#fm_list_table_offline");
    $("#loader").css("visibility", "hidden");

    $('#button_showoffline').click(function (e) {
        e.preventDefault();
        showOffline();
        table_offline = loadFMOfflineList();
        $('#button_showonline').show();
        $('#button_sync').show();
    });

    $('#button_showonline').click(function (e) {
        e.preventDefault();
        showOnline();
        table_online = loadFMOnlineList();
        setShowOfflineButton('fish_modules');
    });

    $('#button_add').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/fishmodules/main";
    });

    $('#button_add_offline').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/fishmodules/main/offline";
    });

    $('#button_edit').click(function (e) {
        e.preventDefault();
        if (thereAreRowsSelected("#fm_list_table_online")) {
            var id = $('#fm_list_table_online').DataTable().row($('tr.selected')).data()["id"];
            window.location.href = getBaseString() + localizedName + "/fishmodules/main?fmId=" + id;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_edit_offline').click(function (e) {
        e.preventDefault();
        if ($('#fm_list_table_offline').find('tr.selected').length > 1) {
            $("#displayInfoMessageOffline").html("");
            showErrorMessage($.getMessage("multiple_edit_not_support"), null, "displayInfoMessageOffline");
        } else {
            if (thereAreRowsSelected("#fm_list_table_offline")) {
                var offlineId = $('#fm_list_table_offline').DataTable().row($('tr.selected')).data()["fmOffLine_Id"];
                window.location.href = getBaseString() + localizedName + "/fishmodules/main/offline#" + offlineId;
            } else {
                $("#displayInfoMessageOffline").html("");
                $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessageOffline");
            }
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#fm_list_table_online")) {
            e.preventDefault();
            var id = $('#fm_list_table_online').DataTable().row($('tr.selected')).data()["id"];
            deleteFm(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete_offline').click(function (e) {
        if (thereAreRowsSelected("#fm_list_table_offline")) {
            e.preventDefault();
            if ($('#fm_list_table_offline').find('tr.selected').length > 1) {
                $("#displayInfoMessageOffline").html("");
                showErrorMessage($.getMessage("multiple_delete_not_support"), null, "displayInfoMessageOffline");
            } else {
                var offlineId = $('#fm_list_table_offline').DataTable().row($('tr.selected')).data()["fmOffLine_Id"];

                deleteOfflineData("#fm_list_table_offline", "fish_modules", offlineId, true, true);
            }
        } else {
            $("#displayInfoMessageOffline").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessageOffline");
        }
    });

    $('#button_sync').click(function (e) {
        e.preventDefault();
        syncOfflineFMs()
    });

    $('#btnRefresh').click(function (e){
        loadFMOnlineList();
    });

    function loadFMOfflineList() {
        if ($.fn.DataTable.isDataTable('#fm_list_table_offline')) {
            $('#fm_list_table_offline').DataTable().destroy();
        }

        $('#fm_list_table_offline tbody').empty();

        $.db_getAll('fish_modules').then(function (results) {
            if (results !== undefined && results !== null) {
                var modules = [];
                $.each(results, function () {
                    var obj = this;
                    if (obj.fmOffLine_Id !== undefined && obj.fmOffLine_Id !== null && obj.fmOffLine_Id.indexOf('-RUNNET') == -1 && obj.fmOffLine_Id.indexOf('-SPECIES') == -1 && obj.fmOffLine_Id.indexOf('-HABITATS') == -1) {
                        modules.push(obj);
                    }
                });

                table_offline = $('#fm_list_table_offline').DataTable({
                    "data": modules,
                    //"select": true,
                    "order": [[0, "asc"]],
                    "aoColumns": [{
                        "mData": "fmOffLine_Id"
                    }, {
                        "mData": "fmLake"
                    }, {
                        "mData": "fmStream"
                    }, {
                        "mData": "fmBranch"
                    }, {
                        "mData": "fmStationFrom"
                    }, {
                        "mData": "fmStationTo"
                    }, {
                        "mData": "sampleDate"
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

function deleteFm(id) {
    var localizedName = getLocale();

    $.ajax({
        type: "DELETE",
        url: getBaseString() + localizedName + "/api/fms/delete/" + id,
        dataType: "json",
        async: true,
        success: function (response) {
            // $('#fm_list_table').DataTable().ajax.reload();
            $("#displayInfoMessage").html("");
            showSuccessMessage(response.message, null, "displayInfoMessage");
            $('#fm_list_table_online').DataTable().row('.selected').remove().draw(false);
        },
        error: function (response) {
            var err = JSON.parse(response.responseText);
            $("#displayInfoMessage").html("");
            showErrorMessage(err.message, null, "displayInfoMessage");
        }
    });
}

function loadFMOnlineList() {
    var localizedName = getLocale();

    if ($.fn.DataTable.isDataTable('#fm_list_table_online')) {
        $('#fm_list_table_online').DataTable().destroy();
    }

    $('#fm_list_table_online tbody').empty();
    
    var yearOperator = $("#yearAdjust").val();
    
    var yearFilter = $("#filterYear").val();

    if(yearFilter == null){
        yearFilter = new Date().getFullYear();
    }

    var table = $('#fm_list_table_online').DataTable({        
        //"sAjaxSource": getBaseString() + localizedName + "/api/fms?year=" + yearFilter,
    	  "sAjaxSource": getBaseString() + localizedName + "/api/fms?yearOp=" + yearOperator + "&year=" + yearFilter,
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
        }, {
            "mData": function (data, type, dataToSet) {
                return data.sampleStatus;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.sampleDate;
            }
        },
            {}],
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

function syncOfflineFMs() {
    var localizedName = getLocale();
    var offlineIds = [];

    var allSelectedRows = $('#fm_list_table_offline').find('tr.selected');
    allSelectedRows.each(function () {
        var offlineId = $(this).find('td').eq(0).text();
        if (!isEmpty(offlineId)) {
            offlineIds.push(offlineId);

        }
    });

    $.db_getAll('fish_modules').then(function (results) {
        var syncObjs = [];
        if (results !== undefined && results !== null && results.length > 0) {
            $.each(results, function () {
                var obj = this;

                $.each(offlineIds, function () {
                    if (obj.fmOffLine_Id !== undefined) {
                        if (obj.fmOffLine_Id.includes(this)) {
                            syncObjs.push(obj);
                        }
                    }
                });

            });
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: getBaseString() + localizedName + "/api/fms/sync",
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
        if (this.fmOffLine_Id !== undefined && this.fmOffLine_Id !== null) {
            if (this.fmOffLine_Id.indexOf('-RUNNET') == -1 && this.fmOffLine_Id.indexOf('-SPECIES') == -1 && this.fmOffLine_Id.indexOf('-HABITATS') == -1)
                deleteOfflineData('#fm_list_table_offline', 'fish_modules', this.fmOffLine_Id, false, true);
            else
                deleteOfflineDataFromLocalDB('fish_modules', this.fmOffLine_Id, false, true);
        }
    });
}
