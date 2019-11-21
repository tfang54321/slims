$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();

    setDefaultFilterYear();

    var table = loadPCList();

    singleTableRowOnSelect('#pc_list_table');
    $("#loader").css("visibility", "hidden");

    $('#button_add').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/parasiticcollections/main";
    });

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#pc_list_table")) {
            e.preventDefault();
            var id = $('#pc_list_table').DataTable().row($('tr.selected')).data()["id"];
            window.location.href = getBaseString() + localizedName + "/parasiticcollections/main?pcId=" + id;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#pc_list_table")) {
            e.preventDefault();
            var id = $('#pc_list_table').DataTable().row($('tr.selected')).data()["id"];
            deletePc(id);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#btnRefresh').click(function (e){
        loadPCList();
    });
});

function deletePc(id) {
    var localizedName = getLocale();

    $.ajax({
        type: "DELETE",
        url: getBaseString() + localizedName + "/api/pcs/delete/" + id,
        dataType: "json",
        async: true,
        success: function (response) {
            // $('#pc_list_table').DataTable().ajax.reload();
            $("#displayInfoMessage").html("");
            showSuccessMessage(response.message, null, "displayInfoMessage");
            $('#pc_list_table').DataTable().row('.selected').remove().draw(false);
        },
        error: function (response) {
            var err = JSON.parse(response.responseText);
            $("#displayInfoMessage").html("");
            showErrorMessage(err.message, null, "displayInfoMessage");
        }
    });
}

function loadPCList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#pc_list_table')) {
        $('#pc_list_table').DataTable().destroy();
    }
    $('#pc_list_table tbody').empty();

    var yearOperator = $("#yearAdjust").val();
    
    var yearFilter = $("#filterYear").val();

    if(yearFilter == null){
        yearFilter = new Date().getFullYear();
    }

    var table = $('#pc_list_table').DataTable({
        //"sAjaxSource": getBaseString() + localizedName + "/api/pcs?year=" + yearFilter,
    	  "sAjaxSource": getBaseString() + localizedName + "/api/pcs?yearOp=" + yearOperator + "&year=" + yearFilter,
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
                return data.lakeDistrict;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.fisherName;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.sampleStatus;
            }
        }, {
            "mData": function (data, type, dataToSet) {
                return data.collectedDate;
            }
        },
            {}],
        "columnDefs": [{
            data: "id",
            "targets": 5,
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
