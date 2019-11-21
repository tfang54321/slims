$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    var table = loadCodeList();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");
    singleTableRowOnSelect("details_table");
    form_original_data = $("#edit_form").serialize();

    $('#button_add').click(function (e) {
        e.preventDefault();
        $('#code_form').show();
        $('#codeValue').prop("readonly", false);
        $('#codeValue').focus();
    });
    
    $('#button_exit').on("click", function (e) {
      e.preventDefault();
      var redirectUrl = getBaseString() + localizedName + "/codetables";
      window.location.href = redirectUrl;
      //confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_reset').click(function (e) {
        e.preventDefault();
        resetForm();
        $('#code_form').show();
    });

    $('#button_cancel').click(function (e) {
        e.preventDefault();
        resetForm();
    });

    $('#button_edit').click(function (e) {
        if (thereAreRowsSelected("#details_table")) {
            e.preventDefault();
            var id = $('#details_table').DataTable().row($('tr.selected')).data()["id"];

            editCode(id);
        }
    });
    
    $('#button_save').on("click", function (e) {
      e.preventDefault();
      $('#edit_form').validate();
      if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
          var actionType, actionUrl;
          if ($('#code_id').val() != "#" && $.trim($('#code_id').val()).length > 0) {
              actionType = "PUT";
              actionUrl = getBaseString() + localizedName + "/api/refcode/" + $('#code_id').val();
          }
          else {
              actionType = "POST";
              actionUrl = getBaseString() + localizedName + "/api/refcode/add/" + $('#refercode_type').val();
          }
          var formData = getFormData("#edit_form");

          $.ajax({
              type: actionType,
              url: actionUrl,
              data: formData,         
              timeout: 6000,
              success: function (response) {
              	  $("#displayInfoMessage").html("");
                  showSuccessMessage(response.message, null, "displayInfoMessage");
                  resetForm();
                  $('#details_table').DataTable().ajax.reload();          
              },
              error: function (response) {
                  var err = JSON.parse(response.responseText);
                  $("#displayInfoMessage").html("");
                  showErrorMessage(err.message, null, "displayInfoMessage");
              }
          });
      }
  });
    
    function resetForm() {
      $('#code_form').hide();
      $('#errors-edit_form').hide();
      $('#edit_form')[0].reset();
      $("#edit_form").validate().resetForm();
      form_original_data = $("#edit_form").serialize();
    }

    function editCode(id) {
        $('#code_form').show();
        $('#desEn').focus();
        var localizedName = getLocale();
        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/refcode/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $('#codeValue').val(response.data.codeValue);
                $('#codeValue').prop("readonly", true);
                $('#desEn').val(response.data.codeMeaningEn);
                $('#desFr').val(response.data.codeMeaningFr);
                $('#codeAbb').val(response.data.codeAbbreviation);
                $('#code_id').attr("value", id);
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                showErrorMessage(err.message);
            }
        });
    }

});

function loadCodeList() {
	  var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#details_table')) {
        $('#details_table').DataTable().destroy();
    }

    $('#details_table tbody').empty();

    var table = $('#details_table').DataTable({
    	  "sAjaxSource": getBaseString() + localizedName + "/api/refcodes/" + $('#refercode_type').val(),
        "sAjaxDataProp": "",
        "order": [[0, "asc"]],
        "deferRender": true,
        //"stateSave": true,
        "aoColumns": [{
        	"mData": "codeValue"
        }, 
        {
        	"mData": "codeMeaningEn"
        }, 
        {
        	"mData": "codeMeaningFr"
        }, 
        {
        	"mData": "codeAbbreviation"
        }, 
        {}],
        "columnDefs": [       	
        	{
        		data: "id",
        		"targets": 4,
        		"searchable": false,
        		"orderable": false,
        		"visible": false,
        		"render": function (data, type, full, meta) {
        			return '<input type="hidden" value="' + data + '"/>';
        		}
        	}
        ],
        "language": {
            "url": getLanguageURL()
        }
    });
    return table;
}
