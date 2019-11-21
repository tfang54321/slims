package ca.gc.dfo.slims.controller.fishmodules;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.fishmodule.FishModule;
import ca.gc.dfo.slims.service.fishmodules.FishModuleService;
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

import static ca.gc.dfo.slims.constants.PagePathes.ENG_FISHMODULE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_FISHMODULE_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping({ ENG_FISHMODULE_PATH, FRA_FISHMODULE_PATH })
public class FMController {
    private static final String CONTROLLER_NAME = "FMController";

    @Autowired
    private FishModuleService    fmService;
    @Autowired
    private ResourceLoadService    resourceLoadService;
    @Autowired
    private LocationLoadService    locationLoadService;
    @Autowired
    private AppMessages            messages;

    @Offline
    @GetMapping
    public String fmList(Model model) {
        CommonUtils.getLogger().debug("{}:fmList", CONTROLLER_NAME);
        model.addAttribute("fmYear", fmService.getYearList());
        return "fishmodules/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_FISH_COLLECTIONS)
    @GetMapping(value = "/main")
    public String addOrEditPage(@RequestParam(name = "fmId", required = false) String fmId, Model model) {
        CommonUtils.getLogger().debug("{}:addOrEditPage with fmId({})", CONTROLLER_NAME, fmId);
        if (CommonUtils.isValidId(fmId)) {
            FishModule fm = getFishModuleById(fmId);
            model.addAttribute("referfm", fm);
        }
        model.addAttribute("alllakeslist", locationLoadService.getAllLakes());
        model.addAttribute("allstreamslist", locationLoadService.getAllStreams());
        model.addAttribute("allbrancheslist", locationLoadService.getAllBranchLentics());
        model.addAttribute("allstationslist", locationLoadService.getAllStations());

        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());

        return "fishmodules/edit";
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

        return "fishmodules/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_FISH_COLLECTIONS)
    @GetMapping(value = "/runnet")
    public String runnetPage(@RequestParam(name = "fmId") String fmId, Model model) {
        CommonUtils.getLogger().debug("{}:runnetPage with fmId({})", CONTROLLER_NAME, fmId);
        if (!CommonUtils.isValidId(fmId)) {
            CommonUtils.getLogger().error("{}:runnetPage got invalid fmId({})", CONTROLLER_NAME, fmId);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.INVALID_ID);
        }

        FishModule fm = getFishModuleById(fmId);
        model.addAttribute("referfm", fm);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());

        return "fishmodules/runnetlist";
    }
    
    @Offline
    @GetMapping(value = "/runnet/offline")
    public String runnetPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:runnetPageOffline", CONTROLLER_NAME);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());
        return "fishmodules/runnetlist";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_FISH_COLLECTIONS)
    @GetMapping(value = "/habitatinventory")
    public String hiPage(@RequestParam(name = "fmId") String fmId, Model model) {
        CommonUtils.getLogger().debug("{}:hiPage with fmId({})", CONTROLLER_NAME, fmId);
        if (!CommonUtils.isValidId(fmId)) {
            CommonUtils.getLogger().error("{}:hiPage got invalid fmId({})", CONTROLLER_NAME, fmId);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.INVALID_ID);
        }

        FishModule fm = getFishModuleById(fmId);
        model.addAttribute("referfm", fm);

        return "fishmodules/habitatinventory";
    }

    @SuppressWarnings("unused")
    @Offline
    @GetMapping(value = "/habitatinventory/offline")
    public String hiPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:hiPageOffline", CONTROLLER_NAME);
        return "fishmodules/habitatinventory";
    }

    private FishModule getFishModuleById(String fmId) {
        return fmService.getById(CommonUtils.getLongFromString(fmId, "ID of FishModule"));
    }
}
