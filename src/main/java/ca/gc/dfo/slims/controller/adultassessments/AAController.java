package ca.gc.dfo.slims.controller.adultassessments;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.adultassessments.AdultAssessment;
import ca.gc.dfo.slims.service.adultassessments.AdultAssessmentService;
import ca.gc.dfo.slims.service.loaders.LocationLoadService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_ADULT_ASSESSMENT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_ADULT_ASSESSMENT_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping({ ENG_ADULT_ASSESSMENT_PATH, FRA_ADULT_ASSESSMENT_PATH })
public class AAController {
    private static final String CONTROLLER_NAME = "AAController";
    
    @Autowired
    private AdultAssessmentService    aaService;
    @Autowired
    private ResourceLoadService        resourceLoadService;
    @Autowired
    private LocationLoadService        locationLoadService;
    @Autowired
    private AppMessages                messages;

    @PreAuthorize(SecurityHelper.EL_VIEW_ADULT_SPAWNING)
    @GetMapping
    public String aaList(Model model) {
        CommonUtils.getLogger().debug("{}:aaList", CONTROLLER_NAME);
        model.addAttribute("aaYear", aaService.getYearList());
        return "adultassessments/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_ADULT_SPAWNING)
    @GetMapping(value = "/location")
    public String addOrEditPage(@RequestParam(name = "aaId", required = false) String aaId,
                                @RequestParam(name = "aaCopyId", required = false) String aaCopyId,
                                Model model) {
        CommonUtils.getLogger().debug("{}:addOrEditPage with aaId({}) aaCopyId({})", CONTROLLER_NAME, aaId, aaCopyId);
        if (CommonUtils.isValidId(aaId)) {
            AdultAssessment aa = getAdultAssessmentById(aaId);
            model.addAttribute("referaa", aa);
        } else if(CommonUtils.isValidId(aaCopyId)) {
            AdultAssessment aa = getAdultAssessmentById(aaCopyId);
            model.addAttribute("copyaa", aa);
        }
        model.addAttribute("alllakeslist", locationLoadService.getAllLakes());
        model.addAttribute("allstreamslist", locationLoadService.getAllStreams());
        model.addAttribute("allbrancheslist", locationLoadService.getAllBranchLentics());
        model.addAttribute("allstationslist", locationLoadService.getAllStations());
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "adultassessments/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_ADULT_SPAWNING)
    @GetMapping(value = "/main")
    public String detailPage(@RequestParam(name = "aaId") String aaId, Model model) {
        CommonUtils.getLogger().debug("{}:detailPage with aaId({})", CONTROLLER_NAME, aaId);
        validateAaId(aaId, "detailPage");

        AdultAssessment aa = getAdultAssessmentById(aaId);
        model.addAttribute("referaa", aa);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "adultassessments/detail";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_ADULT_SPAWNING)
    @GetMapping(value = "/species")
    public String speciesPage(@RequestParam(name = "aaId") String aaId, Model model) {
        CommonUtils.getLogger().debug("{}:speciesPage with aaId({})", CONTROLLER_NAME, aaId);
        validateAaId(aaId, "speciesPage");

        AdultAssessment aa = getAdultAssessmentById(aaId);
        model.addAttribute("referaa", aa);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());
        return "adultassessments/species";
    }

    private void validateAaId(String aaId, String callerName) {
        if (!CommonUtils.isValidId(aaId)) {
            CommonUtils.getLogger().error(
                "{}:{} aaId({}) is invalid, throw ResponseStatusException", CONTROLLER_NAME, callerName, aaId);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ValidationMessages.INVALID_ID.getName()));
        }
    }

    private AdultAssessment getAdultAssessmentById(String aaId) {
        return aaService.getById(CommonUtils.getLongFromString(aaId, "ID of AdultAssessment"));
    }
}
