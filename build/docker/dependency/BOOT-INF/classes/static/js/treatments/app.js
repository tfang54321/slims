$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    setDefaultFilterYear();
    var table = loadTRList();
    singleTableRowOnSelect('#tr_list_table');
    $("#loader").css("visibility", "hidden");

    $('#button_add').click(function (e) {
        e.preventDefault();
        window.location.href = getBaseString() + localizedName + "/treatments/main";
    });

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#tr_list_table")) {
            e.preventDefault();
            var id = $('#tr_list_table').DataTable().row($('tr.selected')).data()["id"];
            window.location.href = getBaseString() + localizedName + "/treatments/main?trId=" + id;
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#button_delete').click(function (e) {
        if (thereAreRowsSelected("#tr_list_table")) {
            var my_button = $('#button_delete');
            my_button.prop('disabled', true);
            e.preventDefault();
            var id = $('#tr_list_table').DataTable().row($('tr.selected')).data()["id"];
            deleteTr(id);
            my_button.prop('disabled', false);
        } else {
            $("#displayInfoMessage").html("");
            $.showAlert("info", $.getMessage("select_record"), 5000, "displayInfoMessage");
        }
    });

    $('#btnRefresh').click(function (e){
        loadTRList();
    });
});

function deleteTr(id) {
    var localizedName = getLocale();

    $.ajax({
        type: "DELETE",
        url: getBaseString() + localizedName + "/api/treatments/delete/" + id,
        dataType: "json",
        async: true,
        success: function (response) {
            $("#displayInfoMessage").html("");
            showSuccessMessage(response.message, null, "displayInfoMessage");
            $('#tr_list_table').DataTable().row('.selected').remove().draw(false);
        },
        error: function (response) {
            var err = JSON.parse(response.responseText);
            $("#displayInfoMessage").html("");
            showErrorMessage(err.message, null, "displayInfoMessage");
        }
    });
}

function loadTRList() {
    var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#tr_list_table')) {
        $('#tr_list_table').DataTable().destroy();
    }

    $('#tr_list_table tbody').empty();

    var yearOperator = $("#yearAdjust").val();
    
    var yearFilter = $("#filterYear").val();

    if(yearFilter == null){
        yearFilter = new Date().getFullYear();
    }

    var table = $('#tr_list_table').DataTable({
        //"sAjaxSource": getBaseString() + localizedName + "/api/treatments?year=" + yearFilter,
    	  "sAjaxSource": getBaseString() + localizedName + "/api/treatments?yearOp=" + yearOperator + "&year=" + yearFilter,
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
                return data.sampleStatus;
            }
        }, {
            "mData": function (data, type, dataToSet) {
            	  if(data.treatmentStart !== null)
                   return data.treatmentStart;
            	  else
            	  	return null;
            }
        }, {
            "mData": function (data, type, dataToSet) {
            	if(data.treatmentEnd !== null)
                return data.treatmentEnd;
            	else
            		return null;
            }
        }, {}],
        "columnDefs": [{
            data: "id",
            "targets": 6,
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
