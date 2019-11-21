/**
 * 
 */
package ca.gc.dfo.slims.constants;

import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;

/**
 * @author ZHUY
 *
 */
public class PagePathes
{
	
	// shared main page
	public static final String	API_PATH								= "/api";
	public static final String	ENG_API_PATH							= PathLocaleChangeInterceptor.ENG_PATH + API_PATH;
	public static final String	FRA_API_PATH							= PathLocaleChangeInterceptor.FRA_PATH + API_PATH;
	
	public static final String	LOCATIONS_PATH							= "/locations";
	public static final String	ENG_LOCATIONS_PATH						= PathLocaleChangeInterceptor.ENG_PATH + LOCATIONS_PATH;
	public static final String	FRA_LOCATIONS_PATH						= PathLocaleChangeInterceptor.FRA_PATH + LOCATIONS_PATH;
	
	public static final String	RESEARCH_AND_ACTIVITIES_PATH			= "/researchAndActivities";
	public static final String	ENG_RESEARCH_AND_ACTIVITIES_PATH		= PathLocaleChangeInterceptor.ENG_PATH + RESEARCH_AND_ACTIVITIES_PATH;
	public static final String	FRA_RESEARCH_AND_ACTIVITIES_PATH		= PathLocaleChangeInterceptor.FRA_PATH + RESEARCH_AND_ACTIVITIES_PATH;
	
	public static final String	SETTINGS_PATH							= "/settings";
	public static final String	ENG_SETTINGS_PATH						= PathLocaleChangeInterceptor.ENG_PATH + SETTINGS_PATH;
	public static final String	FRA_SETTINGS_PATH						= PathLocaleChangeInterceptor.FRA_PATH + SETTINGS_PATH;
	
	public static final String	USER_MANAGEMENT_PATH					= "/userManagement";
	public static final String	ENG_USER_MANAGEMENT_PATH				= PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_PATH;
	public static final String	FRA_USER_MANAGEMENT_PATH				= PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_PATH;

	public static final String  USER_MANAGEMENT_LIST_PATH 				= USER_MANAGEMENT_PATH + "/list";
	public static final String  USER_MANAGEMENT_DATA_PATH 				= USER_MANAGEMENT_PATH + "/data";
	public static final String  USER_MANAGEMENT_CREATE_PATH 			= USER_MANAGEMENT_PATH + "/create";
	public static final String  USER_MANAGEMENT_EDIT_PATH 				= USER_MANAGEMENT_PATH + "/edit";
	public static final String  USER_MANAGEMENT_ACTIVATE_PATH 			= USER_MANAGEMENT_PATH + "/activate";
	public static final String  USER_MANAGEMENT_ROLE_PATH 				= USER_MANAGEMENT_PATH + "/roles/list";
	public static final String  USER_MANAGEMENT_SINGLE_REPORT_PATH 			= USER_MANAGEMENT_PATH + "/list/report/single";
	public static final String  USER_MANAGEMENT_ALL_REPORT_PATH 			= USER_MANAGEMENT_PATH + "/reports";
	public static final String  USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH 			= USER_MANAGEMENT_PATH + "/list/report/download/all";
	public static final String  USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH 			= USER_MANAGEMENT_PATH + "/list/report/download/single";


	public static final String  ENG_USER_MANAGEMENT_SINGLE_REPORT_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_SINGLE_REPORT_PATH ;
	public static final String  FRA_USER_MANAGEMENT_SINGLE_REPORT_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_SINGLE_REPORT_PATH ;


	public static final String  ENG_USER_MANAGEMENT_ALL_REPORT_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_ALL_REPORT_PATH ;
	public static final String  FRA_USER_MANAGEMENT_ALL_REPORT_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_ALL_REPORT_PATH ;

	public static final String  ENG_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH ;
	public static final String  FRA_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH ;


	public static final String  ENG_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.ENG_PATH+USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH ;
	public static final String  FRA_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH 			= PathLocaleChangeInterceptor.FRA_PATH+USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH ;

	public static final String  ENG_USER_MANAGEMENT_LIST_PATH 			= PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_LIST_PATH;
	public static final String  FRA_USER_MANAGEMENT_LIST_PATH 			= PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_LIST_PATH;

	public static final String  ENG_USER_MANAGEMENT_DATA_PATH 			= PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_DATA_PATH;
	public static final String  FRA_USER_MANAGEMENT_DATA_PATH 			= PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_DATA_PATH;

	public static final String  ENG_USER_MANAGEMENT_CREATE_PATH 		= PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_CREATE_PATH;
	public static final String  FRA_USER_MANAGEMENT_CREATE_PATH 		= PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_CREATE_PATH;

	public static final String  ENG_USER_MANAGEMENT_EDIT_PATH 			= PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_EDIT_PATH;
	public static final String  FRA_USER_MANAGEMENT_EDIT_PATH 			= PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_EDIT_PATH;

	public static final String  ENG_USER_MANAGEMENT_ACTIVATE_PATH 		= PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_ACTIVATE_PATH;
	public static final String  FRA_USER_MANAGEMENT_ACTIVATE_PATH 		= PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_ACTIVATE_PATH;

	public static final String  ENG_USER_MANAGEMENT_ROLE_PATH 			= PathLocaleChangeInterceptor.ENG_PATH + USER_MANAGEMENT_ROLE_PATH;
	public static final String  FRA_USER_MANAGEMENT_ROLE_PATH 			= PathLocaleChangeInterceptor.FRA_PATH + USER_MANAGEMENT_ROLE_PATH;
	
	public static final String	CODE_TABLES_PATH						= "/codetables";
	public static final String	ENG_CODE_TABLES_PATH					= PathLocaleChangeInterceptor.ENG_PATH + CODE_TABLES_PATH;
	public static final String	FRA_CODE_TABLES_PATH					= PathLocaleChangeInterceptor.FRA_PATH + CODE_TABLES_PATH;
	
	public static final String	CODE_DETAIL_PATH						= CODE_TABLES_PATH + "/detail";
	public static final String	ENG_CODE_DETAIL_PATH					= PathLocaleChangeInterceptor.ENG_PATH + CODE_DETAIL_PATH;
	public static final String	FRA_CODE_DETAIL_PATH					= PathLocaleChangeInterceptor.FRA_PATH + CODE_DETAIL_PATH;

	public static final String  MYACCOUNT_PATH							= "/account";
	public static final String  ENG_MYACCOUNT_PATH						= PathLocaleChangeInterceptor.ENG_PATH + MYACCOUNT_PATH;
	public static final String  FRA_MYACCOUNT_PATH						= PathLocaleChangeInterceptor.FRA_PATH + MYACCOUNT_PATH;

	public static final String HELP_PATH 								= "/help";
	public static final String ENG_HELP_PATH 							= PathLocaleChangeInterceptor.ENG_PATH + HELP_PATH;
	public static final String FRA_HELP_PATH 							= PathLocaleChangeInterceptor.FRA_PATH + HELP_PATH;

	// lakes
	public static final String	LAKE_PATH								= "/lakes";
	public static final String	ENG_LAKE_PATH							= PathLocaleChangeInterceptor.ENG_PATH + LAKE_PATH;
	public static final String	FRA_LAKE_PATH							= PathLocaleChangeInterceptor.FRA_PATH + LAKE_PATH;
	
	// public static final String LAKE_LIST_PATH = LAKE_PATH + "/list";
	// public static final String ENG_LAKE_PATH = PathLocaleChangeInterceptor.ENG_PATH + LAKE_PATH;
	// public static final String FRA_LAKE_PATH = PathLocaleChangeInterceptor.FRA_PATH + LAKE_PATH;
	
	// streams
	public static final String	STREAM_PATH								= "/streams";
	public static final String	ENG_STREAM_PATH							= PathLocaleChangeInterceptor.ENG_PATH + STREAM_PATH;
	public static final String	FRA_STREAM_PATH							= PathLocaleChangeInterceptor.FRA_PATH + STREAM_PATH;
	
	// branchlentics
	public static final String	BRANCHLENTIC_PATH						= "/branchlentics";
	public static final String	ENG_BRANCHLENTIC_PATH					= PathLocaleChangeInterceptor.ENG_PATH + BRANCHLENTIC_PATH;
	public static final String	FRA_BRANCHLENTIC_PATH					= PathLocaleChangeInterceptor.FRA_PATH + BRANCHLENTIC_PATH;
	
	// stations
	public static final String	STATION_PATH							= "/stations";
	public static final String	ENG_STATION_PATH						= PathLocaleChangeInterceptor.ENG_PATH + STATION_PATH;
	public static final String	FRA_STATION_PATH						= PathLocaleChangeInterceptor.FRA_PATH + STATION_PATH;
	
	// reaches
	public static final String	REACH_PATH								= "/reaches";
	public static final String	ENG_REACH_PATH							= PathLocaleChangeInterceptor.ENG_PATH + REACH_PATH;
	public static final String	FRA_REACH_PATH							= PathLocaleChangeInterceptor.FRA_PATH + REACH_PATH;
	
	// stations-reaches
	public static final String	STATION_REACH_PATH						= "/stationsreaches";
	public static final String	ENG_STATION_REACH_PATH					= PathLocaleChangeInterceptor.ENG_PATH + STATION_REACH_PATH;
	public static final String	FRA_STATION_REACH_PATH					= PathLocaleChangeInterceptor.FRA_PATH + STATION_REACH_PATH;
	
	// stations-branchlentics
	public static final String	STATION_BRANCHLENTIC_PATH				= "/stationsbranchlentics";
	public static final String	ENG_STATION_BRANCHLENTIC_PATH			= PathLocaleChangeInterceptor.ENG_PATH + STATION_BRANCHLENTIC_PATH;
	public static final String	FRA_STATION_BRANCHLENTIC_PATH			= PathLocaleChangeInterceptor.FRA_PATH + STATION_BRANCHLENTIC_PATH;
	
	// larvalassessments
	public static final String	LARVAL_ASSESSMENT_PATH					= "/larvalassessments";
	public static final String	ENG_LARVAL_ASSESSMENT_PATH				= PathLocaleChangeInterceptor.ENG_PATH + LARVAL_ASSESSMENT_PATH;
	public static final String	FRA_LARVAL_ASSESSMENT_PATH				= PathLocaleChangeInterceptor.FRA_PATH + LARVAL_ASSESSMENT_PATH;
	
	public static final String	LA_MAIN_PATH							= LARVAL_ASSESSMENT_PATH + "/main";
	public static final String	ENG_LA_MAIN_PATH						= PathLocaleChangeInterceptor.ENG_PATH + LA_MAIN_PATH;
	public static final String	FRA_LA_MAIN_PATH						= PathLocaleChangeInterceptor.FRA_PATH + LA_MAIN_PATH;
	
	public static final String	LA_MAIN_PATH_OFFLINE					= LARVAL_ASSESSMENT_PATH + "/main/offline";
	public static final String	ENG_LA_MAIN_PATH_OFFLINE				= PathLocaleChangeInterceptor.ENG_PATH + LA_MAIN_PATH_OFFLINE;
	public static final String	FRA_LA_MAIN_PATH_OFFLINE				= PathLocaleChangeInterceptor.FRA_PATH + LA_MAIN_PATH_OFFLINE;
	
	public static final String	LA_ELECTROFISHING_PATH					= LARVAL_ASSESSMENT_PATH + "/electrofishings";
	public static final String	ENG_LA_ELECTROFISHING_PATH				= PathLocaleChangeInterceptor.ENG_PATH + LA_ELECTROFISHING_PATH;
	public static final String	FRA_LA_ELECTROFISHING_PATH				= PathLocaleChangeInterceptor.FRA_PATH + LA_ELECTROFISHING_PATH;
	
	public static final String	LA_ELECTROFISHING_PATH_OFFLINE			= LARVAL_ASSESSMENT_PATH + "/electrofishings/offline";
	public static final String	ENG_LA_ELECTROFISHING_PATH_OFFLINE		= PathLocaleChangeInterceptor.ENG_PATH + LA_ELECTROFISHING_PATH_OFFLINE;
	public static final String	FRA_LA_ELECTROFISHING_PATH_OFFLINE		= PathLocaleChangeInterceptor.FRA_PATH + LA_ELECTROFISHING_PATH_OFFLINE;
	
	public static final String	LA_GRANULARBAYER_PATH					= LARVAL_ASSESSMENT_PATH + "/granularbayers";
	public static final String	ENG_LA_GRANULARBAYER_PATH				= PathLocaleChangeInterceptor.ENG_PATH + LA_GRANULARBAYER_PATH;
	public static final String	FRA_LA_GRANULARBAYER_PATH				= PathLocaleChangeInterceptor.FRA_PATH + LA_GRANULARBAYER_PATH;
	
	public static final String	LA_GRANULARBAYER_PATH_OFFLINE			= LARVAL_ASSESSMENT_PATH + "/granularbayers/offline";
	public static final String	ENG_LA_GRANULARBAYER_PATH_OFFLINE		= PathLocaleChangeInterceptor.ENG_PATH + LA_GRANULARBAYER_PATH_OFFLINE;
	public static final String	FRA_LA_GRANULARBAYER_PATH_OFFLINE		= PathLocaleChangeInterceptor.FRA_PATH + LA_GRANULARBAYER_PATH_OFFLINE;
	
	public static final String	LA_PHYSICALCHEMICAL_PATH				= LARVAL_ASSESSMENT_PATH + "/physicalchemicals";
	public static final String	ENG_LA_PHYSICALCHEMICAL_PATH			= PathLocaleChangeInterceptor.ENG_PATH + LA_PHYSICALCHEMICAL_PATH;
	public static final String	FRA_LA_PHYSICALCHEMICAL_PATH			= PathLocaleChangeInterceptor.FRA_PATH + LA_PHYSICALCHEMICAL_PATH;
	
	public static final String	LA_PHYSICALCHEMICAL_PATH_OFFLINE		= LARVAL_ASSESSMENT_PATH + "/physicalchemicals/offline";
	public static final String	ENG_LA_PHYSICALCHEMICAL_PATH_OFFLINE	= PathLocaleChangeInterceptor.ENG_PATH + LA_PHYSICALCHEMICAL_PATH_OFFLINE;
	public static final String	FRA_LA_PHYSICALCHEMICAL_PATH_OFFLINE	= PathLocaleChangeInterceptor.FRA_PATH + LA_PHYSICALCHEMICAL_PATH_OFFLINE;
	
	public static final String	LA_COLLECTIONCON_PATH					= LARVAL_ASSESSMENT_PATH + "/collectioncons";
	public static final String	ENG_LA_COLLECTIONCON_PATH				= PathLocaleChangeInterceptor.ENG_PATH + LA_COLLECTIONCON_PATH;
	public static final String	FRA_LA_COLLECTIONCON_PATH				= PathLocaleChangeInterceptor.FRA_PATH + LA_COLLECTIONCON_PATH;
	
	public static final String	LA_COLLECTIONCON_PATH_OFFLINE			= LARVAL_ASSESSMENT_PATH + "/collectioncons/offline";
	public static final String	ENG_LA_COLLECTIONCON_PATH_OFFLINE		= PathLocaleChangeInterceptor.ENG_PATH + LA_COLLECTIONCON_PATH_OFFLINE;
	public static final String	FRA_LA_COLLECTIONCON_PATH_OFFLINE		= PathLocaleChangeInterceptor.FRA_PATH + LA_COLLECTIONCON_PATH_OFFLINE;
	
	public static final String	LA_FISHOBSERCOL_PATH					= LARVAL_ASSESSMENT_PATH + "/fishobsercols";
	public static final String	ENG_LA_FISHOBSERCOL_PATH				= PathLocaleChangeInterceptor.ENG_PATH + LA_FISHOBSERCOL_PATH;
	public static final String	FRA_LA_FISHOBSERCOL_PATH				= PathLocaleChangeInterceptor.FRA_PATH + LA_FISHOBSERCOL_PATH;
	
	public static final String	LA_FISHOBSERCOL_PATH_OFFLINE			= LARVAL_ASSESSMENT_PATH + "/fishobsercols/offline";
	public static final String	ENG_LA_FISHOBSERCOL_PATH_OFFLINE		= PathLocaleChangeInterceptor.ENG_PATH + LA_FISHOBSERCOL_PATH_OFFLINE;
	public static final String	FRA_LA_FISHOBSERCOL_PATH_OFFLINE		= PathLocaleChangeInterceptor.FRA_PATH + LA_FISHOBSERCOL_PATH_OFFLINE;
	
	public static final String	LA_FISHOBSERCOLSUM_PATH					= LA_FISHOBSERCOL_PATH + "/summary";
	public static final String	ENG_LA_FISHOBSERCOLSUM_PATH				= PathLocaleChangeInterceptor.ENG_PATH + LA_FISHOBSERCOLSUM_PATH;
	public static final String	FRA_LA_FISHOBSERCOLSUM_PATH				= PathLocaleChangeInterceptor.FRA_PATH + LA_FISHOBSERCOLSUM_PATH;
	
	public static final String	LA_FISHOBSERCOLSUM_PATH_OFFLINE			= LA_FISHOBSERCOL_PATH + "/summary/offline";
	public static final String	ENG_LA_FISHOBSERCOLSUM_PATH_OFFLINE		= PathLocaleChangeInterceptor.ENG_PATH + LA_FISHOBSERCOLSUM_PATH_OFFLINE;
	public static final String	FRA_LA_FISHOBSERCOLSUM_PATH_OFFLINE		= PathLocaleChangeInterceptor.FRA_PATH + LA_FISHOBSERCOLSUM_PATH_OFFLINE;
	
	public static final String	LA_FISHOBSERCOLINDI_PATH				= LA_FISHOBSERCOL_PATH + "/individuals";
	public static final String	ENG_LA_FISHOBSERCOLINDI_PATH			= PathLocaleChangeInterceptor.ENG_PATH + LA_FISHOBSERCOLINDI_PATH;
	public static final String	FRA_LA_FISHOBSERCOLINDI_PATH			= PathLocaleChangeInterceptor.FRA_PATH + LA_FISHOBSERCOLINDI_PATH;
	
	public static final String	LA_FISHOBSERCOLINDI_PATH_OFFLINE		= LA_FISHOBSERCOL_PATH + "/individuals/offline";
	public static final String	ENG_LA_FISHOBSERCOLINDI_PATH_OFFLINE	= PathLocaleChangeInterceptor.ENG_PATH + LA_FISHOBSERCOLINDI_PATH_OFFLINE;
	public static final String	FRA_LA_FISHOBSERCOLINDI_PATH_OFFLINE	= PathLocaleChangeInterceptor.FRA_PATH + LA_FISHOBSERCOLINDI_PATH_OFFLINE;
	
	// habitat inventory
	public static final String	HABITAT_INVENTORY_PATH					= "/habitatinventory";
	public static final String	ENG_HABITAT_INVENTORY_PATH				= PathLocaleChangeInterceptor.ENG_PATH + HABITAT_INVENTORY_PATH;
	public static final String	FRA_HABITAT_INVENTORY_PATH				= PathLocaleChangeInterceptor.FRA_PATH + HABITAT_INVENTORY_PATH;
	
	public static final String	HI_MAIN_PATH							= HABITAT_INVENTORY_PATH + "/main";
	public static final String	ENG_HI_MAIN_PATH						= PathLocaleChangeInterceptor.ENG_PATH + HI_MAIN_PATH;
	public static final String	FRA_HI_MAIN_PATH						= PathLocaleChangeInterceptor.FRA_PATH + HI_MAIN_PATH;
	
	public static final String	HI_MAIN_PATH_OFFLINE					= HABITAT_INVENTORY_PATH + "/main/offline";
	public static final String	ENG_HI_MAIN_PATH_OFFLINE				= PathLocaleChangeInterceptor.ENG_PATH + HI_MAIN_PATH_OFFLINE;
	public static final String	FRA_HI_MAIN_PATH_OFFLINE				= PathLocaleChangeInterceptor.FRA_PATH + HI_MAIN_PATH_OFFLINE;
	
	public static final String	HI_TRANSECT_PATH						= HABITAT_INVENTORY_PATH + "/transect";
	public static final String	ENG_HI_TRANSECT_PATH					= PathLocaleChangeInterceptor.ENG_PATH + HI_TRANSECT_PATH;
	public static final String	FRA_HI_TRANSECT_PATH					= PathLocaleChangeInterceptor.FRA_PATH + HI_TRANSECT_PATH;
	
	public static final String	HI_TRANSECT_PATH_OFFLINE				= HABITAT_INVENTORY_PATH + "/transect/offline";
	public static final String	ENG_HI_TRANSECT_PATH_OFFLINE			= PathLocaleChangeInterceptor.ENG_PATH + HI_TRANSECT_PATH_OFFLINE;
	public static final String	FRA_HI_TRANSECT_PATH_OFFLINE			= PathLocaleChangeInterceptor.FRA_PATH + HI_TRANSECT_PATH_OFFLINE;
	
	// treatments
	public static final String	TREATMENTS_PATH							= "/treatments";
	public static final String	ENG_TREATMENTS_PATH						= PathLocaleChangeInterceptor.ENG_PATH + TREATMENTS_PATH;
	public static final String	FRA_TREATMENTS_PATH						= PathLocaleChangeInterceptor.FRA_PATH + TREATMENTS_PATH;
	
	public static final String	TR_MAIN_PATH							= TREATMENTS_PATH + "/main";
	public static final String	ENG_TR_MAIN_PATH						= PathLocaleChangeInterceptor.ENG_PATH + TR_MAIN_PATH;
	public static final String	FRA_TR_MAIN_PATH						= PathLocaleChangeInterceptor.FRA_PATH + TR_MAIN_PATH;
	
	public static final String	TR_SUMMARY_PATH							= TREATMENTS_PATH + "/summary";
	public static final String	ENG_TR_SUMMARY_PATH						= PathLocaleChangeInterceptor.ENG_PATH + TR_SUMMARY_PATH;
	public static final String	FRA_TR_SUMMARY_PATH						= PathLocaleChangeInterceptor.FRA_PATH + TR_SUMMARY_PATH;
	
	public static final String	TR_APPLICATINOS_PATH					= TREATMENTS_PATH + "/applications";
	public static final String	ENG_TR_APPLICATINOS_PATH				= PathLocaleChangeInterceptor.ENG_PATH + TR_MAIN_PATH;
	public static final String	FRA_TR_APPLICATINOS_PATH				= PathLocaleChangeInterceptor.FRA_PATH + TR_MAIN_PATH;
	
	public static final String	TR_PRIMARYAPP_PATH						= TR_APPLICATINOS_PATH + "/primaryapp";
	public static final String	ENG_TR_PRIMARYAPP_PATH					= PathLocaleChangeInterceptor.ENG_PATH + TR_PRIMARYAPP_PATH;
	public static final String	FRA_TR_PRIMARYAPP_PATH					= PathLocaleChangeInterceptor.FRA_PATH + TR_PRIMARYAPP_PATH;
	
	public static final String	TR_PRIMARYAPP_SINGLEAPP_PATH			= TR_PRIMARYAPP_PATH + "/singleapp";
	public static final String	ENG_TR_PRIMARYAPP_SINGLEAPP_PATH		= PathLocaleChangeInterceptor.ENG_PATH + TR_PRIMARYAPP_SINGLEAPP_PATH;
	public static final String	FRA_TR_PRIMARYAPP_SINGLEAPP_PATH		= PathLocaleChangeInterceptor.FRA_PATH + TR_PRIMARYAPP_SINGLEAPP_PATH;
	
	public static final String	TR_SECONDARYAPP_PATH					= TR_APPLICATINOS_PATH + "/secondaryapp";
	public static final String	ENG_TR_SECONDARYAPP_PATH				= PathLocaleChangeInterceptor.ENG_PATH + TR_SECONDARYAPP_PATH;
	public static final String	FRA_TR_SECONDARYAPP_PATH				= PathLocaleChangeInterceptor.FRA_PATH + TR_SECONDARYAPP_PATH;
	
	public static final String	TR_SECONDARYAPP_SINGLEAPP_PATH			= TR_SECONDARYAPP_PATH + "/singleapp";
	public static final String	ENG_TR_SECONDARYAPP_SINGLEAPP_PATH		= PathLocaleChangeInterceptor.ENG_PATH + TR_SECONDARYAPP_SINGLEAPP_PATH;
	public static final String	FRA_TR_SECONDARYAPP_SINGLEAPP_PATH		= PathLocaleChangeInterceptor.FRA_PATH + TR_SECONDARYAPP_SINGLEAPP_PATH;
	
	public static final String	TR_SECONDARYAPP_INMORTALITY_PATH		= TR_SECONDARYAPP_SINGLEAPP_PATH + "/inducedmortality";
	public static final String	ENG_TR_SECONDARYAPP_INMORTALITY_PATH	= PathLocaleChangeInterceptor.ENG_PATH + TR_SECONDARYAPP_INMORTALITY_PATH;
	public static final String	FRA_TR_SECONDARYAPP_INMORTALITY_PATH	= PathLocaleChangeInterceptor.FRA_PATH + TR_SECONDARYAPP_INMORTALITY_PATH;
	
	public static final String	TR_ANALYSIS_PATH						= TREATMENTS_PATH + "/analysis";
	public static final String	ENG_TR_ANALYSIS_PATH					= PathLocaleChangeInterceptor.ENG_PATH + TR_ANALYSIS_PATH;
	public static final String	FRA_TR_ANALYSIS_PATH					= PathLocaleChangeInterceptor.FRA_PATH + TR_ANALYSIS_PATH;
	
	public static final String	TR_DESIREDCON_PATH						= TR_ANALYSIS_PATH + "/desiredcon";
	public static final String	ENG_TR_TR_DESIREDCON_PATH				= PathLocaleChangeInterceptor.ENG_PATH + TR_DESIREDCON_PATH;
	public static final String	FRA_TR_TR_DESIREDCON_PATH				= PathLocaleChangeInterceptor.FRA_PATH + TR_DESIREDCON_PATH;
	
	public static final String	TR_MINLETHALCON_PATH					= TR_ANALYSIS_PATH + "/minlethalcon";
	public static final String	ENG_TR_MINLETHALCON_PATH				= PathLocaleChangeInterceptor.ENG_PATH + TR_MINLETHALCON_PATH;
	public static final String	FRA_TR_MINLETHALCON_PATH				= PathLocaleChangeInterceptor.FRA_PATH + TR_MINLETHALCON_PATH;
	
	public static final String	TR_WATERCHEM_PATH						= TR_ANALYSIS_PATH + "/waterchem";
	public static final String	ENG_TR_WATERCHEM_PATH					= PathLocaleChangeInterceptor.ENG_PATH + TR_WATERCHEM_PATH;
	public static final String	FRA_TR_WATERCHEM_PATH					= PathLocaleChangeInterceptor.FRA_PATH + TR_WATERCHEM_PATH;
	
	public static final String	TR_DISCHARGE_PATH						= TR_ANALYSIS_PATH + "/discharge";
	public static final String	ENG_TR_DISCHARGE_PATH					= PathLocaleChangeInterceptor.ENG_PATH + TR_DISCHARGE_PATH;
	public static final String	FRA_TR_DISCHARGE_PATH					= PathLocaleChangeInterceptor.FRA_PATH + TR_DISCHARGE_PATH;
	
	public static final String	TR_CHEMANALYSIS_PATH					= TR_ANALYSIS_PATH + "/chemanalysis";
	public static final String	ENG_TR_CHEMANALYSIS_PATH				= PathLocaleChangeInterceptor.ENG_PATH + TR_CHEMANALYSIS_PATH;
	public static final String	FRA_TR_CHEMANALYSIS_PATH				= PathLocaleChangeInterceptor.FRA_PATH + TR_CHEMANALYSIS_PATH;
	
	// fishmodule
	public static final String	FISHMODULE_PATH							= "/fishmodules";
	public static final String	ENG_FISHMODULE_PATH						= PathLocaleChangeInterceptor.ENG_PATH + FISHMODULE_PATH;
	public static final String	FRA_FISHMODULE_PATH						= PathLocaleChangeInterceptor.FRA_PATH + FISHMODULE_PATH;
	
	public static final String	FM_MAIN_PATH							= FISHMODULE_PATH + "/main";
	public static final String	ENG_FM_MAIN_PATH						= PathLocaleChangeInterceptor.ENG_PATH + FM_MAIN_PATH;
	public static final String	FRA_FM_MAIN_PATH						= PathLocaleChangeInterceptor.FRA_PATH + FM_MAIN_PATH;
	
	public static final String	FM_MAIN_PATH_OFFLINE					= FISHMODULE_PATH + "/main/offline";
	public static final String	ENG_FM_MAIN_PATH_OFFLINE				= PathLocaleChangeInterceptor.ENG_PATH + FM_MAIN_PATH_OFFLINE;
	public static final String	FRA_FM_MAIN_PATH_OFFLINE				= PathLocaleChangeInterceptor.FRA_PATH + FM_MAIN_PATH_OFFLINE;
	
	public static final String	FM_RUNNET_PATH							= FISHMODULE_PATH + "/runnet";
	public static final String	ENG_FM_RUNNET_PATH						= PathLocaleChangeInterceptor.ENG_PATH + FM_RUNNET_PATH;
	public static final String	FRA_FM_RUNNET_PATH						= PathLocaleChangeInterceptor.FRA_PATH + FM_RUNNET_PATH;
	
	public static final String	FM_RUNNET_PATH_OFFLINE					= FISHMODULE_PATH + "/runnet/offline";
	public static final String	ENG_FM_RUNNET_PATH_OFFLINE				= PathLocaleChangeInterceptor.ENG_PATH + FM_RUNNET_PATH_OFFLINE;
	public static final String	FRA_FM_RUNNET_PATH_OFFLINE				= PathLocaleChangeInterceptor.FRA_PATH + FM_RUNNET_PATH_OFFLINE;
	
	public static final String	FM_SPECIES_PATH							= FM_RUNNET_PATH + "/species";
	public static final String	ENG_FM_SPECIES_PATH						= PathLocaleChangeInterceptor.ENG_PATH + FM_SPECIES_PATH;
	public static final String	FRA_FM_SPECIES_PATH						= PathLocaleChangeInterceptor.FRA_PATH + FM_SPECIES_PATH;
	
	public static final String	FM_SPECIES_PATH_OFFLINE					= FM_RUNNET_PATH + "/species/offline";
	public static final String	ENG_FM_SPECIES_PATH_OFFLINE				= PathLocaleChangeInterceptor.ENG_PATH + FM_SPECIES_PATH_OFFLINE;
	public static final String	FRA_FM_SPECIES_PATH_OFFLINE				= PathLocaleChangeInterceptor.FRA_PATH + FM_SPECIES_PATH_OFFLINE;
	
	public static final String	FM_INDIVIDUALS_PATH						= FM_SPECIES_PATH + "/individuals";
	public static final String	ENG_FM_INDIVIDUALS_PATH					= PathLocaleChangeInterceptor.ENG_PATH + FM_INDIVIDUALS_PATH;
	public static final String	FRA_FM_INDIVIDUALS_PATH					= PathLocaleChangeInterceptor.FRA_PATH + FM_INDIVIDUALS_PATH;
	
	public static final String	FM_HI_PATH								= FISHMODULE_PATH + "/habitatinventory";
	public static final String	ENG_FM_HI_PATH							= PathLocaleChangeInterceptor.ENG_PATH + FM_HI_PATH;
	public static final String	FRA_FM_HI_PATH							= PathLocaleChangeInterceptor.FRA_PATH + FM_HI_PATH;
	
	public static final String	FM_HI_PATH_OFFLINE						= FISHMODULE_PATH + "/habitatinventory/offline";
	public static final String	ENG_FM_HI_PATH_OFFLINE					= PathLocaleChangeInterceptor.ENG_PATH + FM_HI_PATH_OFFLINE;
	public static final String	FRA_FM_HI_PATH_OFFLINE					= PathLocaleChangeInterceptor.FRA_PATH + FM_HI_PATH_OFFLINE;
	
	// adultassessments
	public static final String	ADULT_ASSESSMENT_PATH					= "/adultassessments";
	public static final String	ENG_ADULT_ASSESSMENT_PATH				= PathLocaleChangeInterceptor.ENG_PATH + ADULT_ASSESSMENT_PATH;
	public static final String	FRA_ADULT_ASSESSMENT_PATH				= PathLocaleChangeInterceptor.FRA_PATH + ADULT_ASSESSMENT_PATH;
	
	public static final String	AA_MAIN_PATH							= ADULT_ASSESSMENT_PATH + "/main";
	public static final String	ENG_AA_MAIN_PATH						= PathLocaleChangeInterceptor.ENG_PATH + AA_MAIN_PATH;
	public static final String	FRA_AA_MAIN_PATH						= PathLocaleChangeInterceptor.FRA_PATH + AA_MAIN_PATH;
	
	public static final String	AA_LOCATION_PATH						= ADULT_ASSESSMENT_PATH + "/location";
	public static final String	ENG_AA_LOCATION_PATH					= PathLocaleChangeInterceptor.ENG_PATH + AA_LOCATION_PATH;
	public static final String	FRA_AA_LOCATION_PATH					= PathLocaleChangeInterceptor.FRA_PATH + AA_LOCATION_PATH;
	
	public static final String	AA_SPECIES_PATH							= ADULT_ASSESSMENT_PATH + "/species";
	public static final String	ENG_AA_SPECIES_PATH						= PathLocaleChangeInterceptor.ENG_PATH + AA_SPECIES_PATH;
	public static final String	FRA_AA_SPECIES_PATH						= PathLocaleChangeInterceptor.FRA_PATH + AA_SPECIES_PATH;
	
	// parasiticcollection
	public static final String	PARASITIC_COLLECTION_PATH				= "/parasiticcollections";
	public static final String	ENG_PARASITIC_COLLECTION_PATH			= PathLocaleChangeInterceptor.ENG_PATH + PARASITIC_COLLECTION_PATH;
	public static final String	FRA_PARASITIC_COLLECTION_PATH			= PathLocaleChangeInterceptor.FRA_PATH + PARASITIC_COLLECTION_PATH;
	
	public static final String	PC_MAIN_PATH							= PARASITIC_COLLECTION_PATH + "/main";
	public static final String	ENG_PC_MAIN_PATH						= PathLocaleChangeInterceptor.ENG_PATH + PC_MAIN_PATH;
	public static final String	FRA_PC_MAIN_PATH						= PathLocaleChangeInterceptor.FRA_PATH + PC_MAIN_PATH;
	
	public static final String	PC_ATTACHMENTS_PATH						= PARASITIC_COLLECTION_PATH + "/attachments";
	public static final String	ENG_PC_ATTACHMENTS_PATH					= PathLocaleChangeInterceptor.ENG_PATH + PC_ATTACHMENTS_PATH;
	public static final String	FRA_PC_ATTACHMENTS_PATH					= PathLocaleChangeInterceptor.FRA_PATH + PC_ATTACHMENTS_PATH;
}
