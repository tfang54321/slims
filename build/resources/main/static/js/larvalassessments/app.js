$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();

    var table_online = $('#la_list_table_online').DataTable();
    var table_offline = $('#la_list_table_offline').DataTable();
    setDefaultFilterYear();

    $.isOnline().then(function (isOnline) {
        if (isOnline) {
            showOnline();
            table_online = loadLAOnlineList();
            setShowOfflineButton('larval_assessments');
        } else {
            showOffline();
            $('#button_add_offline').prop('disabled', false);
            table_offline = loadLAOfflineList();

            $('#button_sync').hide();
            $('#button_showonline').hide();
        }
    });

    singleTableRowOnSelect('#la_list_table_online');
    multipleTableRowOnSelect("#la_list_table_offline");//use custom function for multiple select
    $("#loader").css("visibility", "hidden");

    $('#button_showoffline').click(function (e) {
        e.preventDefault();
        showOffline();
        table_offline = loadLAOfflineList();
        $('#button_showonline').show();
        $('#button_sync').show();
    });

    $('#button_showonline').click(function (e) {
        e.preventDefault();
        showOnline();
        table_online = loadLAOnlineList();
        setShowOfflineButton('larval_assessments');
    });

    $('#button_add').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/larvalassessments/main";
    });

    $('#button_add_offline').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/larvalassessments/main/offline";
    });

    $('#button_edit').click(function (e) {
        e.preventDefault();
        if (thereAreRowsSelected("la_list_table_online")) {
            var id = $('#la_list_table_online').DataTable().row($('tr.selected')).data()["id"];
            window.location.href = getBaseString() + localizedName + "/larvalassessments/main?laId=" + id;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_edit_offline').click(function (e) {
        e.preventDefault();
        if ($('#la_list_table_offline').find('tr.selected').length > 1) {
            $("#displayInfoMessageOffline").html("");
            showErrorMessage($.getMessage("multiple_edit_not_support"), 5000, "displayInfoMessageOffline");
        } else {
            if (thereAreRowsSelected("la_list_table_offline")) {
                var offlineId = $('#la_list_table_offline').DataTable().row($('tr.selected')).data()["laOffLine_Id"];
                window.location.href = getBaseString() + localizedName + "/larvalassessments/main/offline#" + offlineId;
            } else {
                $("#displayInfoMessageOffline").html("");
                $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessageOffline");
            }
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#la_list_table_online")) {
            e.preventDefault();
            var id = $('#la_list_table_online').DataTable().row($('tr.selected')).data()["id"];
            deleteLa(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete_offline').click(function (e) {
        if (thereAreRowsSelected("#la_list_table_offline")) {
            e.preventDefault();
            if ($('#la_list_table_offline').find('tr.selected').length > 1) {
                $("#displayInfoMessageOffline").html("");
                showErrorMessage($.getMessage("multiple_delete_not_support"), 5000, "displayInfoMessageOffline");
            } else {
                var offlineId = $('#la_list_table_offline').DataTable().row($('tr.selected')).data()["laOffLine_Id"];

                deleteOfflineData("#la_list_table_offline", "larval_assessments", offlineId, true, true);
            }
        } else {
            $("#displayInfoMessageOffline").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessageOffline");
        }
    });

    $('#button_sync').click(function (e) {
        e.preventDefault();
        syncOfflineLAs()
    });

    $('#btnRefresh').click(function (e){
        loadLAOnlineList();
    });

    function loadLAOfflineList() {
        if ($.fn.DataTable.isDataTable('#la_list_table_offline')) {
            $('#la_list_table_offline').DataTable().destroy();
        }

        $('#la_list_table_offline tbody').empty();

        $.db_getAll('larval_assessments').then(function (results) {
            if (results !== undefined && results !== null) {
                var modules = [];
                $.each(results, function () {
                    var obj = this;
                    if (obj.laOffLine_Id !== undefined && obj.laOffLine_Id !== null && obj.laOffLine_Id.indexOf('-EF') == -1 && obj.laOffLine_Id.indexOf('-GB') == -1 && obj.laOffLine_Id.indexOf('-PC') == -1 && obj.laOffLine_Id.indexOf('-CC') == -1 && obj.laOffLine_Id.indexOf('-FOC') == -1 && obj.laOffLine_Id.indexOf('-INDIS') == -1) {
                        modules.push(obj);
                    }
                });

                table_offline = $('#la_list_table_offline').DataTable({
                    "data": modules,
                    //"select": true,
                    "order": [[0, "asc"]],
                    "aoColumns": [{
                        "mData": "laOffLine_Id"
                    }, {
                        "mData": "laLake"
                    }, {
                        "mData": "laStream"
                    }, {
                        "mData": "laBranch"
                    }, {
                        "mData": "laStationFrom"
                    }, {
                        "mData": "laStationTo"
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

function deleteLa(id) {
    var localizedName = getLocale();

    $.ajax({
        type: "DELETE",
        url: getBaseString() + localizedName + "/api/las/delete/" + id,
        dataType: "json",
        async: true,
        success: function (response) {
            // $('#la_list_table').DataTable().ajax.reload();
            $("#displayInfoMessage").html("");
            showSuccessMessage(response.message, null, "displayInfoMessage");
            $('#la_list_table_online').DataTable().row('.selected').remove().draw(false);
        },
        error: function (response) {
            var err = JSON.parse(response.responseText);
            $("#displayInfoMessage").html("");
            showErrorMessage(err.message);
        }
    });
}

function loadLAOnlineList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#la_list_table_online')) {
        $('#la_list_table_online').DataTable().destroy();
    }

    $('#la_list_table_online tbody').empty();

    var yearOperator = $("#yearAdjust").val();
    
    var yearFilter = $("#filterYear").val();

    if(yearFilter == null){
        yearFilter = new Date().getFullYear();
    }

    var table = $('#la_list_table_online').DataTable({
        //"sAjaxSource": getBaseString() + localizedName + "/api/las?year=" + yearFilter,
    	  "sAjaxSource": getBaseString() + localizedName + "/api/las?yearOp=" + yearOperator + "&year=" + yearFilter,
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

function syncOfflineLAs() {
    var localizedName = getLocale();
    var offlineIds = [];

    var allSelectedRows = $('#la_list_table_offline').find('tr.selected');
    allSelectedRows.each(function () {
        var offlineId = $(this).find('td').eq(0).text();
        if (!isEmpty(offlineId)) {
            offlineIds.push(offlineId);

        }
    });

    $.db_getAll('larval_assessments').then(function (results) {
        var syncObjs = [];
        if (results !== undefined && results !== null && results.length > 0) {
            $.each(results, function () {
                var obj = this;

                $.each(offlineIds, function () {
                    if (obj.laOffLine_Id !== undefined) {
                        if (obj.laOffLine_Id.includes(this)) {
                            syncObjs.push(obj);
                        }
                    }
                });

            });

            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: getBaseString() + localizedName + "/api/las/sync",
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
        if (this.laOffLine_Id !== undefined && this.laOffLine_Id !== null) {
            if (this.laOffLine_Id.indexOf('-EF') == -1 && this.laOffLine_Id.indexOf('-GB') == -1 && this.laOffLine_Id.indexOf('-PC') == -1 && this.laOffLine_Id.indexOf('-CC') == -1 && this.laOffLine_Id.indexOf('-FOC') == -1 && this.laOffLine_Id.indexOf('-INDIS') == -1)
                deleteOfflineData('#la_list_table_offline', 'larval_assessments', this.laOffLine_Id, false, true);
            else
                deleteOfflineDataFromLocalDB('larval_assessments', this.laOffLine_Id, false, true);
        }
    });
}
