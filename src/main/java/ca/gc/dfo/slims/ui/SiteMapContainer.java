package ca.gc.dfo.slims.ui;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.ui.AbstractSiteMapContainer;
import ca.gc.dfo.spring_commons.commons_offline_wet.ui.SiteMapNode;
import org.springframework.stereotype.Component;

import static ca.gc.dfo.slims.constants.PagePathes.*;

@Component
public class SiteMapContainer extends AbstractSiteMapContainer
{
	@Override
	public void populateNodes()
	{
		// MENU NODES
		// Home >
		SiteMapNode locations = addNode(new SiteMapNode("page.locations", "page.locations.desc", LOCATIONS_PATH, false, true));
		SiteMapNode researchAndActivities = addNode(new SiteMapNode("page.research_and_activities", "page.research_and_activities.desc", RESEARCH_AND_ACTIVITIES_PATH, false, true));
		SiteMapNode settings = addNode(new SiteMapNode("page.settings", "page.settings.desc", SETTINGS_PATH, false, true));
		
		// Home > Settings >
		SiteMapNode tUserManagement = addNode(new SiteMapNode("page.settings.user_mgmt", "page.settings.user_mgmt.desc", USER_MANAGEMENT_PATH, false, true), settings);
		SiteMapNode tCodeTables = addNode(new SiteMapNode("page.settings.code_tables", "page.settings.code_tables.desc", CODE_TABLES_PATH, SecurityHelper.EL_VIEW_CODE_TABLES, false, true, true), settings);
		SiteMapNode tCodeDetail = addNode(new SiteMapNode("page.settings.code_detail", "page.settings.code_detail.desc", CODE_DETAIL_PATH, SecurityHelper.EL_VIEW_CODE_TABLES, true, false, false), tCodeTables);
		
		// Home > Settings > User Management >
		SiteMapNode userNode = addNode(new SiteMapNode("page.settings.user_mgmt.users", "page.settings.user_mgmt.users.desc", USER_MANAGEMENT_LIST_PATH, SecurityHelper.EL_VIEW_USER_MANAGEMENT, false, true, true), tUserManagement);
		addNode(new SiteMapNode("page.settings.user_mgmt.user.create", null, USER_MANAGEMENT_CREATE_PATH, SecurityHelper.EL_MODIFY_USER_MANAGEMENT, false, true, true), userNode);
		addNode(new SiteMapNode("page.settings.user_mgmt.user.edit", null, USER_MANAGEMENT_EDIT_PATH, SecurityHelper.EL_VIEW_USER_MANAGEMENT, true, true, true), userNode);
		addNode(new SiteMapNode("page.settings.user_mgmt.roles", "page.settings.user_mgmt.roles.desc", USER_MANAGEMENT_ROLE_PATH, false, true), tUserManagement);
		
		// Home > Locations >
		SiteMapNode lakes = addNode(new SiteMapNode("i18n.lake_captions_title", "i18n.lake_captions_title.desc", LAKE_PATH, SecurityHelper.EL_VIEW_LOCATIONS, false, true, true), locations);
		
		SiteMapNode streams = addNode(new SiteMapNode("i18n.stream_captions_title", null, STREAM_PATH, SecurityHelper.EL_VIEW_LOCATIONS, true, false, false), lakes);
		SiteMapNode branchlentics = addNode(new SiteMapNode("i18n.branchlentics_caption", null, BRANCHLENTIC_PATH, SecurityHelper.EL_VIEW_LOCATIONS, true, false, false), streams);
		SiteMapNode stations = addNode(new SiteMapNode("i18n.stations_title", null, STATION_PATH, SecurityHelper.EL_VIEW_LOCATIONS, true, false, false), branchlentics);
		SiteMapNode reaches = addNode(new SiteMapNode("i18n.reaches_title", null, REACH_PATH, SecurityHelper.EL_VIEW_LOCATIONS, true, false, false), streams);
		SiteMapNode assign_stations_reaches = addNode(new SiteMapNode("page.locations.assign.stations.reaches", null, STATION_REACH_PATH, SecurityHelper.EL_VIEW_LOCATIONS, true, false, false), reaches);
		
		// Home > Data Modules >
		SiteMapNode fms = addNode(new SiteMapNode("i18n.fm_title", "i18n.fm_title.desc", FISHMODULE_PATH, SecurityHelper.EL_VIEW_FISH_COLLECTIONS, false, true, true), researchAndActivities);
		SiteMapNode habitatInventory = addNode(new SiteMapNode("i18n.hi_title", "i18n.hi_title.desc", HABITAT_INVENTORY_PATH, SecurityHelper.EL_VIEW_HABITAT_INVENTORY, false, true, true), researchAndActivities);
		SiteMapNode las = addNode(new SiteMapNode("i18n.la_title", "i18n.la_title.desc", LARVAL_ASSESSMENT_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, false, true, true), researchAndActivities);
		SiteMapNode pcs = addNode(new SiteMapNode("i18n.pc_title", "i18n.pc_title.desc", PARASITIC_COLLECTION_PATH, SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS, false, true, true), researchAndActivities);
		SiteMapNode aas = addNode(new SiteMapNode("page.adultassessment", "page.adultassessment.desc", ADULT_ASSESSMENT_PATH, SecurityHelper.EL_VIEW_ADULT_SPAWNING, false, true, true), researchAndActivities);
		SiteMapNode treatments = addNode(new SiteMapNode("i18n.tr_title", "i18n.tr_title.desc", TREATMENTS_PATH, SecurityHelper.EL_VIEW_TREATMENTS, false, true, true), researchAndActivities);
		
		// Home > Data Modules > Larval Assessments >
		SiteMapNode laMain = addNode(new SiteMapNode("i18n.main_caption", null, LA_MAIN_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), las);
		SiteMapNode laElectrofishing = addNode(new SiteMapNode("i18n.la_title.electrofishing", null, LA_ELECTROFISHING_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMain);
		SiteMapNode laGranularBayer = addNode(new SiteMapNode("i18n.la_title.granbayer", null, LA_GRANULARBAYER_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMain);
		SiteMapNode laPhysicalChemical = addNode(new SiteMapNode("i18n.la_title.physicalchemical", null, LA_PHYSICALCHEMICAL_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMain);
		SiteMapNode laCollectionCon = addNode(new SiteMapNode("i18n.la_title.collectioncon", null, LA_COLLECTIONCON_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMain);
		SiteMapNode laFishObserCol = addNode(new SiteMapNode("i18n.la_title.fishobsercol", null, LA_FISHOBSERCOLSUM_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMain);
		SiteMapNode laFishObserColSum = addNode(new SiteMapNode("i18n.summary.title", null, LA_FISHOBSERCOLSUM_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laFishObserCol);
		SiteMapNode laFishObserColIndi = addNode(new SiteMapNode("i18n.individuals_title", null, LA_FISHOBSERCOLINDI_PATH, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laFishObserCol);
		
		SiteMapNode laMainOffline = addNode(new SiteMapNode("i18n.main_caption_offline", null, LA_MAIN_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), las);
		SiteMapNode laElectrofishingOffline = addNode(new SiteMapNode("i18n.la_title.electrofishing_offline", null, LA_ELECTROFISHING_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMainOffline);
		SiteMapNode laGranularBayerOffline = addNode(new SiteMapNode("i18n.la_title.granbayer_offline", null, LA_GRANULARBAYER_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMainOffline);
		SiteMapNode laPhysicalChemicalOffline = addNode(new SiteMapNode("i18n.la_title.physicalchemical_offline", null, LA_PHYSICALCHEMICAL_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMainOffline);
		SiteMapNode laCollectionConOffline = addNode(new SiteMapNode("i18n.la_title.collectioncon_offline", null, LA_COLLECTIONCON_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMainOffline);
		SiteMapNode laFishObserColOffline = addNode(new SiteMapNode("i18n.la_title.fishobsercol_offline", null, LA_FISHOBSERCOLSUM_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laMainOffline);
		SiteMapNode laFishObserColSumOffline = addNode(new SiteMapNode("i18n.summary.title_offline", null, LA_FISHOBSERCOLSUM_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laFishObserColOffline);
		SiteMapNode laFishObserColIndiOffline = addNode(new SiteMapNode("i18n.individuals_title_offline", null, LA_FISHOBSERCOLINDI_PATH_OFFLINE, SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS, true, false, false), laFishObserColOffline);
		
		// Home > Data Modules > Habitat Inventory >
		SiteMapNode hiMain = addNode(new SiteMapNode("i18n.main_caption", null, HI_MAIN_PATH, SecurityHelper.EL_VIEW_HABITAT_INVENTORY, true, false, false), habitatInventory);
		SiteMapNode hiTransect = addNode(new SiteMapNode("i18n.hi_title.transect", null, HI_TRANSECT_PATH, SecurityHelper.EL_VIEW_HABITAT_INVENTORY, true, false, false), hiMain);
		
		SiteMapNode hiMainOffline = addNode(new SiteMapNode("i18n.main_caption_offline", null, HI_MAIN_PATH_OFFLINE, SecurityHelper.EL_VIEW_HABITAT_INVENTORY, true, false, false), habitatInventory);
		SiteMapNode hiTransectOffline = addNode(new SiteMapNode("i18n.hi_title.transect_offline", null, HI_TRANSECT_PATH_OFFLINE, SecurityHelper.EL_VIEW_HABITAT_INVENTORY, true, false, false), hiMainOffline);
		
		// Home > Data Modules > Treatments >
		SiteMapNode trMain = addNode(new SiteMapNode("i18n.main_caption", null, TR_MAIN_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), treatments);
		SiteMapNode trApplications = addNode(new SiteMapNode("i18n.applications_title", null, TR_APPLICATINOS_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), treatments);
		SiteMapNode trPrimaryApp = addNode(new SiteMapNode("page.tr.primaryapp.title", null, TR_PRIMARYAPP_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trApplications);
		SiteMapNode trPrimaryAppSingleApp = addNode(new SiteMapNode("page.tr.primaryapp.title.singleapp", null, TR_PRIMARYAPP_SINGLEAPP_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trPrimaryApp);
		SiteMapNode trSecondaryApp = addNode(new SiteMapNode("page.tr.secondaryapp.title", null, TR_SECONDARYAPP_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trApplications);
		SiteMapNode trSecondarySingleApp = addNode(new SiteMapNode("page.tr.secondaryapp.title.singleapp", null, TR_SECONDARYAPP_SINGLEAPP_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trSecondaryApp);
		SiteMapNode trInducedMortality = addNode(new SiteMapNode("page.tr.secondaryapp.title.inducedmortality", null, TR_SECONDARYAPP_INMORTALITY_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trSecondarySingleApp);
		
		SiteMapNode trAnalysis = addNode(new SiteMapNode("i18n.analysis_title", null, TR_ANALYSIS_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), treatments);
		SiteMapNode trDesiredCon = addNode(new SiteMapNode("i18n.tr_title.desiredcon", null, TR_DESIREDCON_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trAnalysis);
		SiteMapNode trMinLethalCon = addNode(new SiteMapNode("i18n.tr_title.minlethalcon", null, TR_MINLETHALCON_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trAnalysis);
		SiteMapNode trWaterchem = addNode(new SiteMapNode("i18n.tr_title.waterchem", null, TR_WATERCHEM_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trAnalysis);
		SiteMapNode trDischarge = addNode(new SiteMapNode("i18n.discharge_title", null, TR_DISCHARGE_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trAnalysis);
		SiteMapNode trChemAnalysis = addNode(new SiteMapNode("i18n.tr_title.chemanalysis", null, TR_CHEMANALYSIS_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), trAnalysis);
		SiteMapNode trSummary = addNode(new SiteMapNode("i18n.summary.title", null, TR_SUMMARY_PATH, SecurityHelper.EL_VIEW_TREATMENTS, true, false, false), treatments);
		
		// Home > Research and Activites > Spawning / Adult Assessments >
		SiteMapNode aaLocation = addNode(new SiteMapNode("i18n.location_title", null, AA_LOCATION_PATH, SecurityHelper.EL_VIEW_ADULT_SPAWNING, true, false, false), aas);
		SiteMapNode aaMain = addNode(new SiteMapNode("i18n.main_caption", null, AA_MAIN_PATH, SecurityHelper.EL_VIEW_ADULT_SPAWNING, true, false, false), aaLocation);
		SiteMapNode aaSpecies = addNode(new SiteMapNode("i18n.species_title", null, AA_SPECIES_PATH, SecurityHelper.EL_VIEW_ADULT_SPAWNING, true, false, false), aaMain);

		// Home > Research and Activites > Parasitic Collections >
		SiteMapNode pcMain = addNode(new SiteMapNode("i18n.main_caption", null, PC_MAIN_PATH, SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS, true, false, false), pcs);
		SiteMapNode pcAttachments = addNode(new SiteMapNode("i18n.pc_title.attachments", null, PC_ATTACHMENTS_PATH, SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS, true, false, false), pcMain);

		// Home > Research and Activites > Fish Collections >
		SiteMapNode fmMain = addNode(new SiteMapNode("i18n.main_caption", null, FM_MAIN_PATH, SecurityHelper.EL_VIEW_FISH_COLLECTIONS, true, false, false), fms);
		SiteMapNode fmRunNet = addNode(new SiteMapNode("i18n.runnet_title", null, FM_RUNNET_PATH, SecurityHelper.EL_VIEW_FISH_COLLECTIONS, true, false, false), fms);
		SiteMapNode fmHabitatInventory = addNode(new SiteMapNode("i18n.hi_title", null, FM_HI_PATH, SecurityHelper.EL_VIEW_FISH_COLLECTIONS, true, false, false), fms);
		
		SiteMapNode fmMainOffline = addNode(new SiteMapNode("i18n.main_caption_offline", null, FM_MAIN_PATH_OFFLINE, SecurityHelper.EL_VIEW_FISH_COLLECTIONS, true, false, false), fms);
		SiteMapNode fmRunNetOffline = addNode(new SiteMapNode("i18n.runnet_title_offline", null, FM_RUNNET_PATH_OFFLINE, SecurityHelper.EL_VIEW_FISH_COLLECTIONS, true, false, false), fms);
		SiteMapNode fmHabitatInventoryOffline = addNode(new SiteMapNode("i18n.hi_title_offline", null, FM_HI_PATH_OFFLINE, SecurityHelper.EL_VIEW_FISH_COLLECTIONS, true, false, false), fms);
	}
}
