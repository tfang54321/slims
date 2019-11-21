$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    var table = loadCodeList();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");
    singleTableRowOnSelect("code_list_table");
    form_original_data = $("#edit_form").serialize();

    $('#button_add').click(function (e) {
        e.preventDefault();
        $('#code_form').show();
        $('#codeName').prop("readonly", false);
        $('#codeName').focus();
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
        if (thereAreRowsSelected("#code_list_table")) {
            e.preventDefault();
            var id = $('#code_list_table').DataTable().row($('tr.selected')).data()["id"];

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
              actionUrl = getBaseString() + localizedName + "/api/codetables/" + $('#code_id').val();
          }
          else {
              actionType = "POST";
              actionUrl = getBaseString() + localizedName + "/api/codetables/add"
          }
          var formData = getFormData("#edit_form");

          $.ajax({
              type: actionType,
              contentType: "application/json",
              url: actionUrl,
              data: JSON.stringify(formData),
              dataType: 'json',
              timeout: 6000,
              success: function (response) {
              	$("#displayInfoMessage").html("");
                showSuccessMessage(response.message, null, "displayInfoMessage");
                resetForm();
                $('#code_list_table').DataTable().ajax.reload();          
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
        $('#descriptionEn').focus();
        var localizedName = getLocale();
        $.ajax({
            type: "GET",
            url: getBaseString() + localizedName + "/api/codetables/" + id,
            dataType: "json",
            async: true,
            success: function (response) {
                $('#codeName').val(response.data.codeName);
                $('#codeName').prop("readonly", true);
                $('#descriptionEn').val(response.data.descriptionEn);
                $('#descriptionFr').val(response.data.descriptionFr);
                $('#code_id').attr("value", id);
                form_original_data = $("#edit_form").serialize();
            },
            error: function (response) {
                var err = JSON.parse(response.responseText);
                showErrorMessage(err.message);
            }
        });
    }
    
    $('#code_list_table').on('click', 'span.glyphicon-list-alt', function (e) {
      e.preventDefault();
      
		  var codeName = $(this).closest("tr").find('td').eq(0).text();
		  window.location.href = getBaseString() + localizedName + "/codetables/detail?codeName=" + codeName;   	
    });

});

function loadCodeList() {
	  var localizedName = getLocale();
    if ($.fn.DataTable.isDataTable('#code_list_table')) {
        $('#code_list_table').DataTable().destroy();
    }

    $('#code_list_table tbody').empty();

    var table = $('#code_list_table').DataTable({
    	  "sAjaxSource": getBaseString() + localizedName + "/api/codetables",
        "sAjaxDataProp": "",
        "order": [[0, "asc"]],
        "deferRender": true,
        //"stateSave": true,
        "aoColumns": [{
        	"mData": "codeName"
        }, {
            "mData": function (data, type, dataToSet) {
                return data.showText;
            }
        }, 
        {}],
        "columnDefs": [
        	{
		          "targets": 2,
		          "searchable": false,
		          "orderable": false,
		          "className": "text-center",
		          "render": function (data, type, full, meta) {
		              return '<a href="javascript:void(0)" role="button"><span style="font-size: 1.3em; vertical-align: middle;" class="glyphicon glyphicon-list-alt" aria-hidden="true"></span></a>';
		             }
          },
        	{
        		data: "id",
        		"targets": 3,
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
