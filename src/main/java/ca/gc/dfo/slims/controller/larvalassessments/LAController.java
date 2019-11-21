package ca.gc.dfo.slims.controller.larvalassessments;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.common.SpecieCode;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LarvalAssessment;
import ca.gc.dfo.slims.service.common.SpecieCodesService;
import ca.gc.dfo.slims.service.common.SpecieService;
import ca.gc.dfo.slims.service.larvalassessments.LarvalAssessmentService;
import ca.gc.dfo.slims.service.loaders.LocationLoadService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.spring_commons.commons_offline_wet.annotations.Offline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_LARVAL_ASSESSMENT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_LARVAL_ASSESSMENT_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping({ ENG_LARVAL_ASSESSMENT_PATH, FRA_LARVAL_ASSESSMENT_PATH })
public class LAController {
    private static final String CONTROLLER_NAME = "LAController";

    @Autowired
    private LarvalAssessmentService    laService;
    @Autowired
    private ResourceLoadService        resourceLoadService;
    @Autowired
    private LocationLoadService        locationLoadService;
    @Autowired
    private SpecieCodesService         specieCodesService;
    @Autowired
    private SpecieService              specieService;
    @Autowired
    private AppMessages                messages;

    @Offline
    @GetMapping
    public String laList(Model model) {
        CommonUtils.getLogger().debug("{}:laList", CONTROLLER_NAME);
         model.addAttribute("laYear", laService.getYearList());
        return "larvalassessments/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/main")
    public String addOrEditPage(@RequestParam(name = "laId", required = false) String laId, Model model) {
        CommonUtils.getLogger().info("{}:addOrEditPage with laId({})", CONTROLLER_NAME, laId);
        if (CommonUtils.isValidId(laId)) {
            setModelReferLa(laId, model);
        }
        model.addAttribute("alllakeslist", locationLoadService.getAllLakes());
        model.addAttribute("allstreamslist", locationLoadService.getAllStreams());
        model.addAttribute("allbrancheslist", locationLoadService.getAllBranchLentics());
        model.addAttribute("allstationslist", locationLoadService.getAllStations());

        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());

        return "larvalassessments/edit";
    }

    @Offline
    @GetMapping(value = "/main/offline")
    public String addOrEditPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:addOrEditPageOffline", CONTROLLER_NAME);
        model.addAttribute("alllakeslist", locationLoadService.getAllLakes());
        model.addAttribute("allstreamslist", locationLoadService.getAllStreams());
        model.addAttribute("allbrancheslist", locationLoadService.getAllBranchLentics());
        model.addAttribute("allstationslist", locationLoadService.getAllStations());
        
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        
        return "larvalassessments/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/electrofishings")
    public String electrofishingPage(@RequestParam(name = "laId") String laId, Model model) {
        String methodName = "electrofishingPage";
        CommonUtils.getLogger().info("{}:{} with id({})", CONTROLLER_NAME, methodName, laId);
        validateId(laId, methodName);
        setModelReferLa(laId, model);
        return "larvalassessments/electrofishing";
    }

    @SuppressWarnings("unused")
    @Offline
    @GetMapping(value = "/electrofishings/offline")
    public String electrofishingPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:electrofishingPageOffline", CONTROLLER_NAME);
        return "larvalassessments/electrofishing";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/granularbayers")
    public String granularbayerPage(@RequestParam(name = "laId") String laId, Model model) {
        String methodName = "granularbayerPage";
        CommonUtils.getLogger().info("{}:{} with id({})", CONTROLLER_NAME, methodName, laId);
        validateId(laId, methodName);
        setModelReferLa(laId, model);
        return "larvalassessments/granularbayer";
    }

    @SuppressWarnings("unused")
    @Offline
    @GetMapping(value = "/granularbayers/offline")
    public String granularbayerPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:granularbayerPageOffline", CONTROLLER_NAME);
        return "larvalassessments/granularbayer";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/physicalchemicals")
    public String physicalchemicalPage(@RequestParam(name = "laId") String laId, Model model) {
        String methodName = "physicalchemicalPage";
        CommonUtils.getLogger().info("{}:{} with id({})", CONTROLLER_NAME, methodName, laId);
        validateId(laId, methodName);
        setModelReferLa(laId, model);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "larvalassessments/physicalchemical";
    }

    @Offline
    @GetMapping(value = "/physicalchemicals/offline")
    public String physicalchemicalPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:physicalchemicalPageOffline", CONTROLLER_NAME);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "larvalassessments/physicalchemical";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/collectioncons")
    public String collectconditionPage(@RequestParam(name = "laId") String laId, Model model) {
        String methodName = "collectconditionPage";
        CommonUtils.getLogger().info("{}:{} with id({})", CONTROLLER_NAME, methodName, laId);
        validateId(laId, methodName);
        setModelReferLa(laId, model);
        return "larvalassessments/colcondition";
    }

    @SuppressWarnings("unused")
    @Offline
    @GetMapping(value = "/collectioncons/offline")
    public String collectconditionPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:collectconditionPageOffline", CONTROLLER_NAME);
        return "larvalassessments/colcondition";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/fishobsercols/summary")
    public String fishobsercolPage(@RequestParam(name = "laId") String laId, Model model) {
        String methodName = "fishobsercolPage";
        CommonUtils.getLogger().info("{}:{} with id({})", CONTROLLER_NAME, methodName, laId);
        validateId(laId, methodName);
        setModelReferLa(laId, model);
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());
        return "larvalassessments/fishobsercol";
    }

    @Offline
    @GetMapping(value = "/fishobsercols/summary/offline")
    public String fishobsercolPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:fishobsercolPageOffline", CONTROLLER_NAME);
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());
        return "larvalassessments/fishobsercol";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/fishobsercols/individuals")
    public String fishIndividualPage(@RequestParam(name = "laId") String laId,
                                     @RequestParam(name = "specieId") String specieId,
                                     Model model) {
        String methodName = "fishIndividualPage";
        CommonUtils.getLogger().info(
            "{}:{} with laId({}) and specieId({})", CONTROLLER_NAME, methodName, laId, specieId);
        validateId(laId, methodName);
        setModelReferLa(laId, model);

        Specie sp = specieService.getById(CommonUtils.getLongFromString(specieId, "ID of Specie"));
        model.addAttribute("referspecie", sp);
        SpecieCode specieCode = specieCodesService.getByCode(sp.getSpeciesCode());
        model.addAttribute("referSpecieCode", specieCode);

        setModelResourceLoadDetails(model);

        return "larvalassessments/fishindividuals";
    }

    @Offline
    @GetMapping(value = "/fishobsercols/individuals/offline")
    public String fishIndividualPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:fishIndividualPageOffline", CONTROLLER_NAME);
        setModelResourceLoadDetails(model);
        return "larvalassessments/fishindividuals";
    }

    private void validateId(String id, String caller) {
        if (!CommonUtils.isValidId(id)) {
            CommonUtils.getLogger().error("{}:{} found invalid id({}), throw BAD REQUEST", CONTROLLER_NAME, caller, id);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.INVALID_ID);
        }
    }

    private void setModelReferLa(String laId, Model model) {
        LarvalAssessment la = laService.getById(CommonUtils.getLongFromString(laId, "ID of LarvalAssessment"));
        model.addAttribute("referla", la);
    }

    private void setModelResourceLoadDetails(Model model) {
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        model.addAttribute("specimenlist",
            resourceLoadService.getRefcodesMap().get("SPECIMEN_STATE").getCodeValues());
        model.addAttribute("sexlist", resourceLoadService.getRefcodesMap().get("SEX").getCodeValues());
    }
}
