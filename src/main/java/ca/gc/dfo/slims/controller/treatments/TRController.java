package ca.gc.dfo.slims.controller.treatments;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_TREATMENTS_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_TREATMENTS_PATH;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.treatments.TRPrimaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.Treatment;
import ca.gc.dfo.slims.service.loaders.LocationLoadService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.service.treatments.TRPrimaryAppService;
import ca.gc.dfo.slims.service.treatments.TRSecondaryAppService;
import ca.gc.dfo.slims.service.treatments.TreatmentService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import io.micrometer.core.instrument.util.StringUtils;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping({ ENG_TREATMENTS_PATH, FRA_TREATMENTS_PATH })
public class TRController {
    private static final String CONTROLLER_NAME = "TRController";
    private static final String NAME_PRIMARY_APP_ID = "primaryAppId";
    private static final String NAME_SECONDARY_APP_ID = "secondaryAppId";

    @Autowired
    private TreatmentService        trService;
    @Autowired
    private TRPrimaryAppService        trPrimaryAppService;
    @Autowired
    private TRSecondaryAppService    trSecondaryAppService;
    @Autowired
    private ResourceLoadService        resourceLoadService;
    @Autowired
    private LocationLoadService        locationLoadService;
    @Autowired
    private AppMessages                messages;
    
    @GetMapping
    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    public String trList(Model model) {
        CommonUtils.getLogger().debug("{}:trList ", CONTROLLER_NAME);
        model.addAttribute("trYear", trService.getYearList());
        return "treatments/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/main")
    public String addOrEditPage(@RequestParam(name = "trId", required = false) String trId, Model model) {
        CommonUtils.getLogger().debug("{}:addOrEditPage ", CONTROLLER_NAME);
        if (!StringUtils.isBlank(trId)) {
            Treatment tr = trService.getById(CommonUtils.getLongFromString(trId, "Treatment ID"));
            setReferTrInModel(model, tr);
        }
        model.addAttribute("alllakeslist", locationLoadService.getAllLakes());
        model.addAttribute("allstreamslist", locationLoadService.getAllStreams());
        setRefcodeListInModel(model);
        return "treatments/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = { "/applications/primaryapp", "/applications" })
    public String primaryAppPage(@RequestParam(name = "trId") String trId, Model model) {
        CommonUtils.getLogger().debug("{}:primaryAppPage ", CONTROLLER_NAME);

        Treatment tr = getTreatmentById(trId, "primaryAppPage");
        setReferTrInModel(model, tr);
        setBranchListAndStationListInModel(model, tr);
        return "treatments/primaryapp";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/applications/primaryapp/singleapp")
    public String primarySingleAppPage(@RequestParam(name = "trId") String trId,
                                       @RequestParam(name = "primaryAppId") String primaryAppId,
                                       Model model) {
        String methodName = "primarySingleAppPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        validateId(primaryAppId, NAME_PRIMARY_APP_ID, methodName);

        Treatment tr = getTreatmentById(trId, methodName);

        TRPrimaryApplication trPriApp = trPrimaryAppService.getById(
            CommonUtils.getLongFromString(primaryAppId, NAME_PRIMARY_APP_ID));

        setReferTrInModel(model, tr);
        model.addAttribute("referTrPrimApp", trPriApp);

        setRefcodeListInModel(model);
        model.addAttribute("lpslist", resourceLoadService.getLpsMap());
        
        return "treatments/primarysingleapp";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/applications/secondaryapp")
    public String secondaryAppPage(@RequestParam(name = "trId") String trId, Model model) {
        String methodName = "secondaryAppPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        Treatment tr = getTreatmentById(trId, methodName);
        setReferTrInModel(model, tr);
        setBranchListAndStationListInModel(model, tr);
        return "treatments/secondaryapp";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/applications/secondaryapp/singleapp")
    public String secondarySingleAppPage(@RequestParam(name = "trId") String trId,
                                         @RequestParam(name = "secondaryAppId") String secondaryAppId,
                                         Model model) {
        String methodName = "secondarySingleAppPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        validateId(secondaryAppId, NAME_SECONDARY_APP_ID, methodName);

        Treatment tr = getTreatmentById(trId, methodName);
        TRSecondaryApplication trSecondApp = getTrSecondaryApplicationById(secondaryAppId);

        setReferTrInModel(model, tr);
        model.addAttribute("referTrSecondApp", trSecondApp);

        setBranchListAndStationListInModel(model, tr);

        setRefcodeListInModel(model);
        model.addAttribute("lpslist", resourceLoadService.getLpsMap());
        return "treatments/secondarysingleapp";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/applications/secondaryapp/singleapp/inducedmortality")
    public String inducedMortalityPage(@RequestParam(name = "trId") String trId,
                                       @RequestParam(name = "secondaryAppId") String secondaryAppId,
                                       Model model) {
        String methodName = "inducedMortalityPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        validateId(secondaryAppId, NAME_SECONDARY_APP_ID, methodName);

        Treatment tr = getTreatmentById(trId, methodName);
        TRSecondaryApplication trSecondApp = getTrSecondaryApplicationById(secondaryAppId);

        setReferTrInModel(model, tr);
        model.addAttribute("referTrSecondApp", trSecondApp);

        setRefcodeListInModel(model);
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());
        return "treatments/inducedmortality";
    }

    private TRSecondaryApplication getTrSecondaryApplicationById(String secondaryAppId) {
        return trSecondaryAppService.getById(CommonUtils.getLongFromString(secondaryAppId, NAME_SECONDARY_APP_ID));
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = { "/analysis/desiredcon", "/analysis" })
    public String desiredconPage(@RequestParam(name = "trId") String trId, Model model) {
        String methodName = "desiredconPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        Treatment tr = getTreatmentById(trId, methodName);
        setReferTrInModel(model, tr);
        setBranchListAndStationListInModel(model, tr);
        return "treatments/desiredcon";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/analysis/minlethalcon")
    public String minlethalconPage(@RequestParam(name = "trId") String trId, Model model) {
        String methodName = "minlethalconPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        Treatment tr = getTreatmentById(trId, methodName);
        setReferTrInModel(model, tr);
        setBranchListAndStationListInModel(model, tr);
        return "treatments/minlethalcon";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/analysis/waterchem")
    public String waterchemPage(@RequestParam(name = "trId") String trId, Model model) {
        String methodName = "waterchemPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        Treatment tr = getTreatmentById(trId, methodName);
        setReferTrInModel(model, tr);
        setRefcodeListInModel(model);
        setBranchListAndStationListInModel(model, tr);
        return "treatments/waterchem";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/analysis/discharge")
    public String dischargePage(@RequestParam(name = "trId") String trId, Model model) {
        String methodName = "dischargePage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        Treatment tr = getTreatmentById(trId, methodName);
        setReferTrInModel(model, tr);
        setRefcodeListInModel(model);
        setBranchListAndStationListInModel(model, tr);
        return "treatments/discharge";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/analysis/chemanalysis")
    public String chemanalysisPage(@RequestParam(name = "trId") String trId, Model model) {
        String methodName = "chemanalysisPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        Treatment tr = getTreatmentById(trId, methodName);
        setReferTrInModel(model, tr);
        setBranchListAndStationListInModel(model, tr);
        return "treatments/chemanalysis";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/summary")
    public String summaryPage(@RequestParam(name = "trId") String trId, Model model) {
        String methodName = "summaryPage";
        CommonUtils.getLogger().debug("{}:{} ", CONTROLLER_NAME, methodName);
        Treatment tr = getTreatmentById(trId, methodName);
        tr.setTotalSpecies(trService.getAllSpeciesSumary(tr));
        setReferTrInModel(model, tr);
        return "treatments/summary";
    }

    private void validateId(String id, String idDescription, String callerName) {
        if (!CommonUtils.isValidId(id)) {
            CommonUtils.getLogger().error("{}:{} got invalid {}({})", CONTROLLER_NAME, callerName, idDescription, id);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ValidationMessages.INVALID_ID.getName()));
        }
    }

    private Treatment getTreatmentById(String trId, String callerName) {
        String idDescription = "Treatment ID";
        validateId(trId, idDescription, callerName);
        return trService.getById(CommonUtils.getLongFromString(trId, idDescription));
    }

    private void setReferTrInModel(Model model, Treatment tr) {
        model.addAttribute("refertr", tr);
    }

    private void setBranchListAndStationListInModel(Model model, Treatment tr) {
        model.addAttribute("referbranchlist",
            locationLoadService.getAllBranchLentics().get(tr.getStream().getId().toString()));
        model.addAttribute("allstationslist", locationLoadService.getAllStations());
    }

    private void setRefcodeListInModel(Model model) {
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
    }
}
