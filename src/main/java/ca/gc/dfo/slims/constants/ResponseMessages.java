/**
 * 
 */
package ca.gc.dfo.slims.constants;

/**
 * @author ZHUY
 *
 */
public enum ResponseMessages
{
    // all other exception
	GENERAL_EXCEPTION("general_exception"),
	
	CANNOT_BE_FOUND("cannot_be_found"),
	
	BAD_SAMPLEID_COUNTERFORMAT("bad_sampleid_counterformat"),
	
    // lakes
	SAVE_LAKE_SUCCESS("lake_save_success"),
	
	SAVE_LAKE_ERROR("lake_save_failed"),
	
	UPDATE_LAKE_SUCCESS("lake_update_success"),
	
	UPDATE_LAKE_ERROR("lake_update_failed"),
	
	DELETE_LAKE_SUCCESS("lake_delete_success"),
	
	DELETE_LAKE_ERROR("lake_delete_failed"),
	
	DUPLICATE_LAKE_ERROR("duplicate_lake_error"),
	
    // streams
	SAVE_STREAM_SUCCESS("stream_save_success"),
	
	SAVE_STREAM_ERROR("stream_save_failed"),
	
	UPDATE_STREAM_SUCCESS("stream_update_success"),
	
	UPDATE_STREAM_ERROR("stream_update_failed"),
	
	DELETE_STREAM_SUCCESS("stream_delete_success"),
	
	DELETE_STREAM_ERROR("stream_delete_failed"),
	
	DUPLICATE_STREAM_ERROR("duplicate_stream_error"),
	
    // branchLentics
	SAVE_BRANCHLENTIC_SUCCESS("branchLentic_save_success"),
	
	SAVE_BRANCHLENTIC_ERROR("branchLentic_save_failed"),
	
	SAVE_BRANCHLENTICS_ERROR("branchLentics_save_failed"),
	
	UPDATE_BRANCHLENTIC_SUCCESS("branchLentic_update_success"),
	
	UPDATE_BRANCHLENTIC_ERROR("branchLentic_update_failed"),
	
	DELETE_BRANCHLENTIC_SUCCESS("branchLentic_delete_success"),
	
	DELETE_BRANCHLENTIC_ERROR("branchLentic_delete_failed"),
	
	DUPLICATE_BRANCHLENTIC_ERROR("duplicate_branchLentic_error"),
	
    // reaches
	SAVE_REACH_SUCCESS("reach_save_success"),
	
	SAVE_REACH_ERROR("reach_save_failed"),
	
	SAVE_REACHES_ERROR("reaches_save_failed"),
	
	UPDATE_REACH_SUCCESS("reach_update_success"),
	
	UPDATE_REACH_ERROR("reach_update_failed"),
	
	DELETE_REACH_SUCCESS("reach_delete_success"),
	
	DELETE_REACH_ERROR("reach_delete_failed"),
	
	REACH_DELETE_FAILED_ASSIGNED_STATIONS("reach_delete_failed_assigned_stations"),
	
	DUPLICATE_REACH_ERROR("duplicate_reach_error"),
	
    // stations
	SAVE_STATION_SUCCESS("station_save_success"),
	
	SAVE_STATION_ERROR("station_save_failed"),
	
	UPDATE_STATION_SUCCESS("station_update_success"),
	
	UPDATE_STATION_ERROR("station_update_failed"),
	
	DELETE_STATION_SUCCESS("station_delete_success"),
	
	DELETE_STATION_ERROR("station_delete_failed"),
	
	DUPLICATE_STATION_ERROR("duplicate_station_error"),
	
	SAVE_STATIONS_ERROR("stations_save_failed"),
	
    // stations and reaches
	UPDATE_ASSIGNED_STATION_SUCCESS("update_assigned_station_success"),
	UPDATE_ASSIGNED_STATION_ERROR("update_assigned_station_failed"),
	
    // larvalassessment
	SAVE_LARVALASSESSMENT_SUCCESS("larvalassessment_save_success"),
	
	SAVE_LARVALASSESSMENT_ERROR("larvalassessment_save_failed"),
	
	UPDATE_LARVALASSESSMENT_SUCCESS("larvalassessment_update_success"),
	
	UPDATE_LARVALASSESSMENT_ERROR("larvalassessment_update_failed"),
	
	DELETE_LARVALASSESSMENT_SUCCESS("larvalassessment_delete_success"),
	
	DELETE_LARVALASSESSMENT_ERROR("larvalassessment_delete_failed"),
	
	DUPLICATE_LARVALASSESSMENT_ERROR("duplicate_larvalassessment_error"),
	
	SAVE_LAELECTROFISHINGDETAILS_ERROR("save_laelectrofishingdetails_error"),
	
	SAVE_LAGRANULARBAYERDETAILS_ERROR("save_lagranularbayerdetails_error"),
	
	SAVE_LAPHYSICALCHEMICALDATASERVICE_ERROR("save_laphysicalchemicaldataservice_error"),
	
    // habitat inventory
	SAVE_HABITATINVENTORY_SUCCESS("habitatinventory_save_success"),
	
	SAVE_HABITATINVENTORY_ERROR("habitatinventory_save_failed"),
	
	UPDATE_HABITATINVENTORY_SUCCESS("habitatinventory_update_success"),
	
	UPDATE_HABITATINVENTORY_ERROR("habitatinventory_update_failed"),
	
	DELETE_HABITATINVENTORY_SUCCESS("habitatinventory_delete_success"),
	
	DELETE_HABITATINVENTORY_ERROR("habitatinventory_delete_failed"),
	
	OFFLINEDATA_SYNC_SUCCESS("offlinedata_sync_success"),
	
    // treatments
	SAVE_TREATMENT_SUCCESS("treatment_save_success"),
	
	SAVE_TREATMENT_ERROR("treatment_save_failed"),
	
	SAVE_DUPLICATE_TREATMENT_ERROR("save_duplicate_treatment_error"),
	
	UPDATE_TREATMENT_SUCCESS("treatment_update_success"),
	
	UPDATE_TREATMENT_ERROR("treatment_update_failed"),
	
	DELETE_TREATMENT_SUCCESS("treatment_delete_success"),
	
	DELETE_TREATMENT_ERROR("treatment_delete_failed"),
	
	DUPLICATE_TREATMENT_ERROR("duplicate_treatment_error"),
	
	SAVE_TRLOGISTICS_ERROR("save_trlogistics_error"),
	
	SAVE_TRPRIMARYAPPLICATION_ERROR("save_trprimaryapplication_error"),
	
	SAVE_TRSECONDARYAPPLICATION_ERROR("save_trsecondaryapplication_error"),
	
	SAVE_TRSECONDAPPINDUCEDMORTALITY_ERROR("save_trsecondappinducedmortality_error"),
	
    // adultassessment
	SAVE_ADULTASSESSMENT_SUCCESS("adultassessment_save_success"),
	
	SAVE_ADULTASSESSMENT_ERROR("adultassessment_save_failed"),
	
	UPDATE_ADULTASSESSMENT_SUCCESS("adultassessment_update_success"),
	
	UPDATE_ADULTASSESSMENT_ERROR("adultassessment_update_failed"),
	
	DELETE_ADULTASSESSMENT_SUCCESS("adultassessment_delete_success"),
	
	DELETE_ADULTASSESSMENT_ERROR("adultassessment_delete_failed"),
	
	DUPLICATE_ADULTASSESSMENT_ERROR("duplicate_adultassessment_error"),
	
    // parasitic collection
	SAVE_PARASITICCOLLECTION_SUCCESS("parasiticcollection_save_success"),
	
	SAVE_PARASITICCOLLECTION_ERROR("parasiticcollection_save_failed"),
	
	UPDATE_PARASITICCOLLECTION_SUCCESS("parasiticcollection_update_success"),
	
	UPDATE_PARASITICCOLLECTION_ERROR("parasiticcollection_update_failed"),
	
	DELETE_PARASITICCOLLECTION_SUCCESS("parasiticcollection_delete_success"),
	
	DELETE_PARASITICCOLLECTION_ERROR("parasiticcollection_delete_failed"),
	
	DUPLICATE_PARASITICCOLLECTION_ERROR("duplicate_parasiticcollection_error"),
	
    // fish module
	SAVE_FISHMODULE_SUCCESS("fishmodule_save_success"),
	
	SAVE_FISHMODULE_ERROR("fishmodule_save_failed"),
	
	UPDATE_FISHMODULE_SUCCESS("fishmodule_update_success"),
	
	UPDATE_FISHMODULE_ERROR("fishmodule_update_failed"),
	
	DELETE_FISHMODULE_SUCCESS("fishmodule_delete_success"),
	
	DELETE_FISHMODULE_ERROR("fishmodule_delete_failed"),
	
	DUPLICATE_FISHMODULE_ERROR("duplicate_fishmodule_error"),
	
	//runnet
	SAVE_FMRUNNET_SUCCESS("fmrunnet_save_success"),
	
	SAVE_FMRUNNET_ERROR("fmrunnet_save_failed"),
	
	UPDATE_FMRUNNET_SUCCESS("fmrunnet_update_success"),
	
	UPDATE_FMRUNNET_ERROR("fmrunnet_update_failed"),
	
	DELETE_FMRUNNET_SUCCESS("fmrunnet_delete_success"),
	
	DELETE_FMRUNNET_ERROR("fmrunnet_delete_failed"),
	
	DUPLICATE_FMRUNNET_ERROR("duplicate_fmrunnet_error"),
	
    // codeview
	SAVE_CODEVIEW_SUCCESS("codeview_save_success"),
	
	SAVE_CODEVIEW_ERROR("codeview_save_failed"),
	
	UPDATE_CODEVIEW_SUCCESS("codeview_update_success"),
	
	UPDATE_CODEVIEW_ERROR("codeview_update_failed"),
	
	DELETE_CODEVIEW_SUCCESS("codeview_delete_success"),
	
	DELETE_CODEVIEW_ERROR("codeview_delete_failed"),
	
	DUPLICATE_CODEVIEW_ERROR("duplicate_codeview_error"),
	
	// refcode
	SAVE_REFCODE_SUCCESS("refcode_save_success"),
	
	SAVE_REFCODE_ERROR("refcode_save_failed"),
	
	UPDATE_REFCODE_SUCCESS("refcode_update_success"),
	
	UPDATE_REFCODE_ERROR("refcode_update_failed"),
	
	DELETE_REFCODE_SUCCESS("refcode_delete_success"),
	
	DELETE_REFCODE_ERROR("refcode_delete_failed"),
	
	DUPLICATE_REFCODE_ERROR("duplicate_refcode_error");
	
	private final String name;
	
	private ResponseMessages(String name)
	{
		this.name = name;
		
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
}
