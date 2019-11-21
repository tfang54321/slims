$(document).on('wb-ready.wb', function () {
    var localizedName = getLocale();
    var form_original_data;
    loadPC();
    $('input[type="date"]').prop('max', getCurrentDateStr());
    form_original_data = $("#edit_form").serialize();
    addValidationListener('#edit_form');
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/parasiticcollections";
        confirmExitOrContinue(form_original_data, $.getMessage("i18n.exit_warning_message"), redirectUrl);
    });

    $('#button_continue').click(function (e) {
        e.preventDefault();

        var pcId = $('#referpc_id').val();
        var redirectUrl = getBaseString() + localizedName + "/parasiticcollections/attachments?pcId=" + pcId;

        confirmExitOrContinue(form_original_data, $.getMessage("i18n.continue_warning_message"), redirectUrl);
    });

    $('#button_save').on("click", function (e) {
        e.preventDefault();
        $('#edit_form').validate();

        if ($('#edit_form').valid() && formIsChanged('#edit_form', form_original_data)) {
            var actionType, actionUrl;
            if ($('#referpc_id').val() != "#" && $.trim($('#referpc_id').val()).length > 0) {
                actionType = "PUT";
                actionUrl = getBaseString() + localizedName + "/api/pcs/" + $('#referpc_id').val();
            } else {
                actionType = "POST";
                actionUrl = getBaseString() + localizedName + "/api/pcs/add";
            }
            var formData = getFormData("#edit_form");

            formData.lakeDistrictId = findObjectId("#alllds", $("#lakeDistrict").val());

            $.ajax({
                type: actionType,
                url: actionUrl,
                data: formData,
                timeout: 6000,
                success: function (response) {
                    $('#sampleCode').val(response.data.sample.sampleCode);
                    $('#referpc_id').val(response.data.id);
                    $('#button_continue').prop('disabled', false);
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
    });
    
    $('.mesh-dim').change(function () {
      var max_mesh = $.trim($("#maxMesh").val());
      var min_mesh = $.trim($("#minMesh").val());
      
      if (max_mesh.length > 0 && min_mesh.length > 0 && !isNaN(max_mesh) && !isNaN(min_mesh)) {
          $("#avgMesh").val((( parseFloat(max_mesh)+parseFloat(min_mesh) )/2).toFixed(2));
      } else {
          $("#avgMesh").val('');
      }
  });
    
    $('.depth-dim').change(function () {
    	var max_depth = $.trim($("#maxDepth").val());
    	var min_depth = $.trim($("#minDepth").val());
    	
    	if (max_depth.length > 0 && min_depth.length > 0 && !isNaN(max_depth) && !isNaN(min_depth)) {
    		$("#avgDepth").val(Math.round( ( parseInt(max_depth)+parseInt(min_depth) )/2) );
    	} else {
    		$("#avgDepth").val('');
    	}
    });
    
});

function loadPC() {
    if (thePC != null) {
        $('#sampleCode').val(thePC.sample.sampleCode);
        if(thePC.sample.sampleStatus !== null)
           $('#sampleStatus').val(thePC.sample.sampleStatus);
        else
           $('#sampleStatus').val('DRAFT');
        $('#collectedDate').val(thePC.collectedDate);
        $('#firsherName').val(thePC.fisherName);
        $('#lakeDistrict').val(thePC.lakeDistrict.showText);
        if (thePC.managementUnit != undefined && thePC.managementUnit != null)
            $('#managementUnit').val(thePC.managementUnit.codePair.codeName);
        $('#grid_number').val(thePC.gridNumber);
        $('#grid_number_est').val(thePC.gridNumberEst);
        if (thePC.gearType != undefined && thePC.gearType != null)
            $('#gearType').val(thePC.gearType.codePair.codeName);
        $('#meshSize').val(thePC.meshSize);
        $('#maxMesh').val(thePC.meshSizeMax);
        $('#minMesh').val(thePC.meshSizeMin);
        $('#avgMesh').val(thePC.meshSizeAvg);
        $('#waterDepth').val(thePC.waterDepth);
        $('#maxDepth').val(thePC.depthMax);
        $('#minDepth').val(thePC.depthMin);
        $('#avgDepth').val(thePC.depthAvg);

        $('#button_continue').prop('disabled', false);
    } else {
        $('#button_continue').prop('disabled', true);
    }
}