$(document).on('wb-ready.wb', function(){
			var localizedName = getLocale();
			var table = loadAAList();
			setDefaultFilterYear();
			singleTableRowOnSelect("aa_list_table");

			$('#button_add').click(
					function(e)
					{
						e.preventDefault();
						if(thereAreRowsSelected("#aa_list_table"))
						{
							var id = $('#aa_list_table').DataTable().row($('tr.selected'))
									.data()["id"];
							window.location.href = getBaseString() + localizedName
									+ "/adultassessments/location?aaCopyId=" + id;
						}
						else
							window.location.href = getBaseString() + localizedName
									+ "/adultassessments/location";
					});

			$('#button_edit').click(
					function(e)
					{
						e.preventDefault();
						if(thereAreRowsSelected("#aa_list_table"))
						{
							var id = $('#aa_list_table').DataTable().row($('tr.selected'))
									.data()["id"];
							window.location.href = getBaseString() + localizedName
									+ "/adultassessments/location?aaId=" + id;
						}
						else
						{
							$("#displayInfoMessage").html("");
							$.showAlert("info", $.getMessage("select_record"), 5000,
									"displayInfoMessage");
						}
					});

			$('#button_delete').click(
					function(e)
					{
						if(thereAreRowsSelected("#aa_list_table"))
						{
							e.preventDefault();
							var id = $('#aa_list_table').DataTable().row($('tr.selected'))
									.data()["id"];
							deleteAa(id);
						}
						else
						{
							$("#displayInfoMessage").html("");
							$.showAlert("info", $.getMessage("select_record"), 5000,
									"displayInfoMessage");
						}
					});
			$("#loader").css("visibility", "hidden");

			$('#btnRefresh').click(function(e)
			{
				loadAAList();
			});
		});

function deleteAa(id)
{
	var localizedName = getLocale();

	$.ajax({
		type : "DELETE",
		url : getBaseString() + localizedName + "/api/aas/delete/" + id,
		dataType : "json",
		async : true,
		success : function(response)
		{
			// $('#aa_list_table').DataTable().ajax.reload();
			$("#displayInfoMessage").html("");
			showSuccessMessage(response.message, null, "displayInfoMessage");
			$('#aa_list_table').DataTable().row('.selected').remove().draw(false);
		},
		error : function(response)
		{
			var err = JSON.parse(response.responseText);
			$("#displayInfoMessage").html("");
			showErrorMessage(err.message, null, "displayInfoMessage");
		}
	});
}

function loadAAList()
{
	var localizedName = getLocale();
	if($.fn.DataTable.isDataTable('#aa_list_table'))
	{
		$('#aa_list_table').DataTable().destroy();
	}

	$('#aa_list_table tbody').empty();

	var yearOperator = $("#yearAdjust").val();

	var yearFilter = $("#filterYear").val();

	if(yearFilter == null)
	{
		yearFilter = new Date().getFullYear();
	}

	var table = $('#aa_list_table').DataTable(
			{
				//"sAjaxSource" : getBaseString() + localizedName + "/api/aas?year=" + yearFilter,
				"sAjaxSource": getBaseString() + localizedName + "/api/aas?yearOp=" + yearOperator + "&year=" + yearFilter,
				"sAjaxDataProp" : "",
				"order" : [ [ 0, "asc" ] ],
				"deferRender" : true,
				// "stateSave": true,
				"aoColumns" : [ {
					"mData" : function(data, type, dataToSet)
					{
						return data.sampleCode;
					}
				}, {
					"mData" : function(data, type, dataToSet)
					{
						return data.lake;
					}
				}, {
					"mData" : function(data, type, dataToSet)
					{
						return data.stream;
					}
				}, {
					"mData" : function(data, type, dataToSet)
					{
						return data.branchLentic;
					}
				}, {
					"mData" : function(data, type, dataToSet)
					{
						return data.stationFrom;
					}
				}, {
					"mData" : function(data, type, dataToSet)
					{
						return data.sampleStatus;
					}
				}, {
					"mData" : function(data, type, dataToSet)
					{
						return data.inspectDate;
					}
				}, { } ],
				"columnDefs" : [ {
					data : "id",
					"targets" : 7,
					"searchable" : false,
					"orderable" : false,
					"visible" : false,
					"render" : function(data, type, full, meta)
					{
						return '<input type="hidden" value="' + data + '"/>';
					}
				} ],
				"language" : {
					"url" : getLanguageURL()
				}
			});
	return table;
}
