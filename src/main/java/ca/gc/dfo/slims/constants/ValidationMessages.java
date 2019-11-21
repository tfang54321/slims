package ca.gc.dfo.slims.constants;

/**
 * @author ZHUY
 *
 */
public enum ValidationMessages {
	//generic
	INVALID_ID("invalid_id"),
	
	REMARKS_VALIDATION("remarks_validation"),
	
	FISHINDIVIDUL_NUMBER_VALIDATION("fishindividul_number_validation"),
	
	FISHINDIVIDUL_TOTALCAUGHT_VALIDATION("fishindividul_totalcaught_validation"),
	
	FISHINDIVIDUL_TOTALOBSERVED_VALIDATION("fishindividul_totalobserved_validation"),

    // UTM
	LATDEG_VALIDATION("latdeg_validation"),
	
	LONGDEG_VALIDATION("longdeg_validation"),
	
	LONGDEG_HIGH_VALIDATION("longdeg_high_validation"),
	
	LONGDEG_LOW_VALIDATION("longdeg_low_validation"),
	
	UTM_EASTING_VALIDATION("utm_easting_validation"),
	
	UTM_NORTHING_VALIDATION("utm_northing_validation"),
	
	LOCATION_DESCRIPTION_VALIDATION("location_description_validation"),
	
    // shared
	REQUIRED_LAKE_VALIDATION("required_lake_validation"),
	
	REQUIRED_STREAM_VALIDATION("required_stream_validation"),
	
	REQUIRED_BRANCHLENTIC_VALIDATION("required_branchlentic_validation"),
	
	REQUIRED_STATION_FROM_VALIDATION("required_station_from_validation"),
	
	REQUIRED_STATION_TO_VALIDATION("required_station_to_validation"),
	
	EN_NAME_VALIDATION("en_name_validation"),
	
	FR_NAME_VALIDATION("fr_name_validation"),
	
    // lakes
	LAKE_CODE_VALIDATION("lake_code_validation"),
	
    // streams
	STREAM_CODE_VALIDATION("stream_code_validation"),
	
    // branch-lentics
	BRANCHLENTIC_CODE_VALIDATION("branchlentic_code_validation"),
	
    // reaches
	REACH_CODE_VALIDATION("reach_code_validation"),
	
	REACH_NAME_VALIDATION("reach_name_validation"),
	
	REACH__VALIDATION("reach_name_validation"),
	
	REACH_LENGTH_VALIDATION("reach_length_validation"),
	
	REACH_UPDATEYEAR_VALIDATION("reach_updateyear_validation"),
	
    // stations
	STATION_CODE_VALIDATION("station_code_validation"),
	
	STATION_DESCRIPTION_VALIDATION("station_description_validation"),

    // larvalassessments
	LA_STARTTIME_VALIDATION("la_starttime_validation"),
	LA_FINISHTIME_VALIDATION("la_finishtime_validation"),
	LA_FINISHTIME_BEFORE_STARTTIME_VALIDATION("la_finishtime_before_starttime_validation"),
	LA_FINISHTIME_STARTTIME_VALIDATION("la_finishtime_starttime_validation"),
	LA_SURVEYPURPOSE_VALIDATION("la_surveypurpose_validation"),
	LA_METHODOLOGY_VALIDATION("la_methodology_validation"),
	OPERATOR1_VALIDATION("operator1_validation"),
	LA_SAMPLEDATE_VALIDATION("la_sampledate_validation"),

    // la electrofishing
	ELECTROFISH_METHODOLOGY_VALIDATION("electrofish_methodology_validation"),
	ELECTROFISH_PEAKVOLT_VALIDATION("electrofish_peakvolt_validation"),
	ELECTROFISH_PULSERATESLOW_VALIDATION("electrofish_pulserateslow_validation"),
	ELECTROFISH_PULSERATESFAST_VALIDATION("electrofish_pulseratesfast_validation"),
	ELECTROFISH_DUTYCYCLESLOW_VALIDATION("electrofish_dutycycleslow_validation"),
	ELECTROFISH_DUTYCYCLESFAST_VALIDATION("electrofish_dutycyclesfast_validation"),
	ELECTROFISH_BURSTRATE_VALIDATION("electrofish_burstrate_validation"),
	ELECTROFISH_SURVEYDISTANCE_VALIDATION("electrofish_surveydistance_validation"),
	ELECTROFISH_AREAPERCENT_VALIDATION("electrofish_areapercent_validation"),
	ELECTROFISH_AREAELECTRO_VALIDATION("electrofish_areaelectro_validation"),
	ELECTROFISH_TIMEELECTRO_VALIDATION("electrofish_timeelectro_validation"),

    // la granularbayer
	GRANULARBAYER_METHODOLOGY_VALIDATION("granularbayer_methodology_validation"),
	GRANULARBAYER_PLOTLENGTH_VALIDATION("granularbayer_plotlength_validation"),
	GRANULARBAYER_PLOTWIDTH_VALIDATION("granularbayer_plotwidth_validation"),
	GRANULARBAYER_TOFA_VALIDATION("granularbayer_tofa_validation"),
	GRANULARBAYER_PERSONHOUR_VALIDATION("granularbayer_personhour_validation"),
	GRANULARBAYER_BOATEFFORT_VALIDATION("granularbayer_boateffort_validation"),
	
    // la phychemical
	PHYCHEMICAL_TOPTEMP_VALIDATION("phychemical_toptemp_validation"),
	
	PHYCHEMICAL_BOTTOMTEMP_VALIDATION("phychemical_bottomtemp_validation"),
	
	PHYCHEMICAL_CONDUCTIVITY_VALIDATION("phychemical_conductivity_validation"),
	
	PHYCHEMICAL_CONDUCTIVITYTEMP_VALIDATION("phychemical_conductivitytemp_validation"),
	
	PHYCHEMICAL_CONDUCTIVITYPH_VALIDATION("phychemical_conductivityph_validation"),
	
	PHYCHEMICAL_MEANDEPTH_VALIDATION("phychemical_meandepth_validation"),
	
	PHYCHEMICAL_MEANSTREAMWIDTH_VALIDATION("phychemical_meanstreamwidth_validation"),
	
	PHYCHEMICAL_DISCHARGE_VALIDATION("phychemical_discharge_validation"),
	
    // la collcondition
	COLLCONDITION_CONDETAIL_VALIDATION("collcondition_condetail_validation"),
	
	COLLCONDITION_EFFECDETAIL_VALIDATION("collcondition_effecdetail_validation"),
	
	COLLCONDITION_POORCON_VALIDATION("collcondition_poorcon_validation"),
	
    // fish observed and collected
	FISHOBSCOLL_ALLSPECIES_VALIDATION("fishobscoll_allspecies_validation"),
	
	FISHOBSCOLL_SPECIE_VALIDATION("fishobscoll_specie_validation"),
	
	FISHOBSCOLL_OBSRALIVE_VALIDATION("fishobscoll_obsralive_validation"),
	
	FISHOBSCOLL_OBSRDEAD_VALIDATION("fishobscoll_obsrdead_validation"),
	
	FISHOBSCOLL_COLRELEASED_VALIDATION("fishobscoll_colreleased_validation"),
	
	FISHOBSCOLL_COLDEAD_VALIDATION("fishobscoll_coldead_validation"),
	
    // fish individuals
	FISHINDI_LENGTHWEIGHT_VALIDATION("fishindi_lengthweight_validation"),
	FISHINDI_LENGTH_VALIDATION("fishindi_length_validation"),
	FISHINDI_WEIGHT_VALIDATION("fishindi_weight_validation"),
	
    // habitat inventory
	HI_TYPE_VALIDATION("hi_type_validation"),
	
	HI_BOTTOM_VALIDATION("hi_bottom_validation"),
	
	HI_OPERATOR1_VALIDATION("hi_operator1_validation"),
	
	HI_INVENTORYDATE_VALIDATION("hi_inventorydate_validation"),
	
	HI_DISTANCE_LEFTBANK_VALIDATION("hi_distance_leftbank_validation"),
	
	HI_DEPTH_VALIDATION("hi_depth_validation"),
	
	HI_STREAM_WIDTH_VALIDATION("hi_stream_width_validation"),
	
	HI_TRANSECT_SPACING_VALIDATION("hi_transect_spacing_validation"),
	
	HI_TOTAL_REACHLEN_VALIDATION("hi_total_reachlen_validation"),
	
	HI_EST_DISCHARGE_VALIDATION("hi_est_discharge_validation"),
	
	HI_STREAM_CONDITION_VALIDATION("hi_stream_condition_validation"),
	
	HI_BEDROCK_VALIDATION("hi_bedrock_validation"),
	
	HI_HARDPAN_CLAY_VALIDATION("hi_hardpan_clay_validation"),
	
	HI_CLAY_SEDIMENTS_VALIDATION("hi_clay_sediments_validation"),
	
	HI_GRAVEL_VALIDATION("hi_gravel_validation"),
	
	HI_RUBBLE_VALIDATION("hi_rubble_validation"),
	
	HI_SAND_VALIDATION("hi_sand_validation"),
	
	HI_SILT_VALIDATION("hi_silt_validation"),
	
	HI_SILT_DETRITUS_VALIDATION("hi_silt_detritus_validation"),
	
	HI_DETRITUS_VALIDATION("hi_detritus_validation"),
	
	HI_CUMULATIVE_SPAWNING_VALIDATION("hi_cumulative_spawning_validation"),
	
	//Treatment
	TR_LAKE_ID_VALIDATION("tr_lake_id_validation"),
	TR_STREAM_ID_VALIDATION("tr_stream_id_validation"),
	TR_START_DATE_VALIDATION("tr_start_date_validation"),
	TR_START_END_DATE_VALIDATION("tr_start_end_date_validation"),
	TR_APP_START_END_DATE_MESSAGE("tr_app_start_end_date_message"),
	TR_METHODOLOGY_VALIDATION("tr_methodology_validation"),
	TR_TOTALDISCHARGE_VALIDATION("tr_totaldischarge_validation"),
	TR_KIOTREATED_VALIDATION("tr_kiotreated_validation"),
	TR_CALENDARDAYS_VALIDATION("tr_calendardays_validation"),
	TR_MAXCREW_VALIDATION("tr_maxcrew_validation"),
	TR_PERSONDAYS_VALIDATION("tr_persondays_validation"),
	TR_PRIMARY_VALIDATION("tr_primary_validation"),
	TR_SECONDAPP_VALIDATION("tr_secondapp_validation"),
	TR_PRIMAPP_TREATDATE_VALIDATION("tr_primapp_treatdate_validation"),
	TR_PRIMAPP_TIMEON_VALIDATION("tr_primapp_timeon_validation"),
	TR_PRIMAPP_TIMEOFF_VALIDATION("tr_primapp_timeoff_validation"),
	TR_SECONDAPP_TIMESTART_VALIDATION("tr_secondapp_timestart_validation"),
	TR_DURATION_VALIDATION("tr_duration_validation"),
	TR_APPLICATIONCODE_VALIDATION("tr_applicationcode_validation"),
	TR_PRIMTFM_VALIDATION("tr_primtfm_validation"),
	TR_TFM_LITERUSED_VALIDATION("tr_tfm_literused_validation"),
	TR_PRIMWP_VALIDATION("tr_primwp_validation"),
	TR_PRIMWP_KGUSED_VALIDATION("tr_primwp_kgused_validation"),
	TR_PRIMEC_VALIDATION("tr_primec_validation"),
	TR_PRIMEC_KGAILITER_VALIDATION("tr_primec_kgailiter_validation"),
	TR_PRIMEC_LITERUSED_VALIDATION("tr_primec_literused_validation"),
	TR_TFM_NUMBARS_VALIDATION("tr_tfm_numbars_validation"),
	TR_TFM_WEIGHTOFBAR_VALIDATION("tr_tfm_weightofbar_validation"),
	TR_PERCAI_VALIDATION("tr_percai_validation"),
	TR_AMOUNTUSED_VALIDATION("tr_amountused_validation"),
	TR_NUMOFADULTS_VALIDATION("tr_numofadults_validation"),
	TR_DEDIREDCON_VALIDATION("tr_dediredcon_validation"),
	TR_MINAPPCON_VALIDATION("tr_minappcon_validation"),
	TR_MAXAPPCON_VALIDATION("tr_maxappcon_validation"),
	TR_NICLOPERC_VALIDATION("tr_nicloperc_validation"),
	TR_MLC_VALIDATION("tr_mlc_validation"),
	TR_MLCVALUE_VALIDATION("tr_mlcvalue_validation"),
	TR_EXPOSURETIME_VALIDATION("tr_exposuretime_validation"),
	TR_WATERCHEM_VALIDATION("tr_waterchem_validation"),
	TR_STREAMTEMP_VALIDATION("tr_streamtemp_validation"),
	TR_DISOXYGEN_VALIDATION("tr_disoxygen_validation"),
	TR_AMMONIA_VALIDATION("tr_ammonia_validation"),
	TR_PH_VALIDATION("tr_ph_validation"),
	TR_ALKALINITYPH_VALIDATION("tr_alkalinityph_validation"),
	TR_PHMLC_VALIDATION("tr_phmlc_validation"),
	TR_DISCHARGE_VALIDATION("tr_discharge_validation"),
	TR_DISCHARGEVALUE_VALIDATION("tr_dischargevalue_validation"),
	TR_ELAPSEDTIME_VALIDATION("tr_elapsedtime_validation"),
	TR_CUMULATIVETIME_VALIDATION("tr_cumulativetime_validation"),
	TR_CHEMANALYSIS_VALIDATION("tr_chemanalysis_validation"),
	TR_TFMCON_VALIDATION("tr_tfmcon_validation"),
	TR_NICLOCON_VALIDATION("tr_niclocon_validation"),

	//adult assessment
	AA_TRAPNUMBER_VALIDATION("aa_trapnumber_validation"),
	AA_AIRTEMP_VALIDATION("aa_airtemp_validation"),
	AA_RECAPTURED_VALIDATION("aa_recaptured_validation"),
	AA_MARKED_VALIDATION("aa_marked_validation"),
	AA_WEEKOF_TAGGING_VALIDATION("aa_weekof_tagging_validation"),
	AA_WATERTEMP_VALIDATION("aa_watertemp_validation"),
	AA_MAXTEMP_VALIDATION("aa_maxtemp_validation"),
	AA_MINTEMP_VALIDATION("aa_mintemp_validation"),
	AA_UPSTREAM_VALIDATION("aa_upstream_validation"),
	AA_DOWNSTREAM_VALIDATION("aa_downstream_validation"),
	AA_IFOTHER_VALIDATION("aa_ifother_validation"),
	AA_SPECIES_LENGTH_VALIDATION("aa_species_length_validation"),
	AA_SPECIES_WEIGHT_VALIDATION("aa_species_weight_validation"),
	
	AA_MARKED_MESSAGE("aa_marked_message"),
	AA_WATERTEMP_MESSAGE("aa_watertemp_message"),
	AA_UPSTREAM_MESSAGE("aa_upstream_message"),
	AA_DOWNSTREAM_MESSAGE("aa_downstream_message"),
	
	//Parasitic Collection
	PC_COLLECTEDDATE_VALIDATION("pc_collecteddate_validation"),
	
	PC_FISHERNAME_VALIDATION("pc_fishername_validation"),
	
	PC_LAKEDISTRICT_VALIDATION("pc_lakedistrict_validation"),
	
	PC_GRIDNUM_VALIDATION("pc_gridnum_validation"),
	
	PC_GRIDNUMEST_VALIDATION("pc_gridnumest_validation"),
	
	PC_MESHSIZE_VALIDATION("pc_meshsize_validation"),
	
	PC_MAXMESH_VALIDATION("pc_maxmesh_validation"),
	
	PC_MINMESH_VALIDATION("pc_minmesh_validation"),
	
	PC_AVGMESH_VALIDATION("pc_avgmesh_validation"),
	
	PC_WATERDEPTH_VALIDATION("pc_waterdepth_validation"),
	
	PC_MAXDEPTH_VALIDATION("pc_maxdepth_validation"),
	
	PC_MINDEPTH_VALIDATION("pc_mindepth_validation"),
	
	PC_AVGDEPTH_VALIDATION("pc_avgdepth_validation"),
	
	PC_IDNUMBER_VALIDATION("pc_idnumber_validation"),
	
	PC_HOSTTYPE_VALIDATION("pc_hosttype_validation"),
	
	PC_SEALAMPREYSAMPLED_VALIDATION("pc_sealampreysampled_validation"),
	
	PC_SILVERLAMPREYSAMPLED_VALIDATION("pc_silverlampreysampled_validation"),
	
	PC_TOTALATTACHED_VALIDATION("pc_totalattached_validation"),
	
	PC_INDISPECIECODE_VALIDATION("pc_indispeciecode_validation"),
	
	PC_ATTACHMENTS_VALIDATION("pc_attachments_validation"),
	
	PC_HOSTSPECIE_VALIDATION("pc_hostspecie_validation"),
	
	//fish module
	FM_METHODOLOGY_VALIDATION("fm_methodology_validation"),
	
	FM_TECHNIQUE_VALIDATION("fm_technique_validation"),
	
	FM_CONTAINMENT_VALIDATION("fm_containment_validation"),
	
	FM_CONDUCTIVITY_VALIDATION("fm_conductivity_validation"),
	
	FM_TEMPERATURE_VALIDATION("fm_temperature_validation"),
	
	FM_MEANDEPTH_VALIDATION("fm_meandepth_validation"),
	
	FM_MEANWIDTH_VALIDATION("fm_meanwidth_validation"),
	
	FM_MAXDEPTH_VALIDATION("fm_maxdepth_validation"),
	
	FM_MEASUREDAREA_VALIDATION("fm_measuredarea_validation"),
	
	FM_ESTIMATEDAREA_VALIDATION("fm_estimatedarea_validation"),
	
	FM_DISTANCESURVEY_VALIDATION("fm_distancesurvey_validation"),
	
	FM_RUNNETNUMBER_VALIDATION("fm_runnetnumber_validation"),
	
	FM_ELECFISHING_VALIDATION("fm_elecfishing_validation"),
	
	FM_PERSONCATCHING_VALIDATION("fm_personcatching_validation"),
	
	FM_ESTDURATION_VALIDATION("fm_estduration_validation"),
	
	FM_MEASUREDDURATION_VALIDATION("fm_measuredduration_validation"),
	
	FM_PEAKVOL_VALIDATION("fm_peakvol_validation"),
	
	FM_BURSTMODE_VALIDATION("fm_burstmode_validation"),
	
	FM_SLOWRATE_VALIDATION("fm_slowrate_validation"),
	
	FM_FASTRATE_VALIDATION("fm_fastrate_validation"),
	
	FM_SLOWDUTY_VALIDATION("fm_slowduty_validation"),
	
	FM_FASTDUTY_VALIDATION("fm_fastduty_validation"),
	
	FM_HABITATS_VALIDATION("fm_habitats_validation"),
	
	FM_HABITATS_DISTANCE_VALIDATION("fm_habitats_distance_validation"),
	
	FM_HABITATS_DEPTH_VALIDATION("fm_habitats_depth_validation"),
	
	FM_HABITATS_WIDTH_VALIDATION("fm_habitats_width_validation"),
	
	HI_POOLS_VALIDATION("hi_pools_validation"),
	
	HI_RIFFLES_VALIDATION("hi_riffles_validation"),
	
	HI_EDDYLAGOON_VALIDATION("hi_eddylagoon_validation"),
	
	HI_RUN_VALIDATION("hi_run_validation"),
	
	HI_OVERHANG_VALIDATION("hi_overhang_validation"),
	
	HI_VEGETATION_VALIDATION("hi_vegetation_validation"),
	
	HI_WOODYDEBRIS_VALIDATION("hi_woodydebris_validation"),
	
	HI_ALGAE_VALIDATION("hi_algae_validation"),
	
	HI_GRASSES_VALIDATION("hi_grasses_validation"),
	
	HI_TREES_VALIDATION("hi_trees_validation");
	
	private final String name;
	
	private ValidationMessages(String name)
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
