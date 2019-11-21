package ca.gc.dfo.slims.controller.habitatinventory;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.habitat.HabitatInventory;
import ca.gc.dfo.slims.service.habitatinventory.HabitatInventoryService;
import ca.gc.dfo.slims.service.loaders.LocationLoadService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.spring_commons.commons_offline_wet.annotations.Offline;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_HABITAT_INVENTORY_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_HABITAT_INVENTORY_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping({ ENG_HABITAT_INVENTORY_PATH, FRA_HABITAT_INVENTORY_PATH })
public class HIController {
    private static final String CONTROLLER_NAME = "HIController";

    @Autowired
    private HabitatInventoryService    hiService;
    @Autowired
    private ResourceLoadService        resourceLoadService;
    @Autowired
    private LocationLoadService        locationLoadService;
    
    @Offline
    @GetMapping
    public String hiList(Model model) {
        model.addAttribute("hiYear", hiService.getYearList());
        return "habitatinventory/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_HABITAT_INVENTORY)
    @GetMapping(value = "/main")
    public String addOrEditPage(@RequestParam(name = "hiId", required = false) String hiId, Model model) {
        CommonUtils.getLogger().debug("{}:addOrEditPage with hiId({})", CONTROLLER_NAME, hiId);
        if (!StringUtils.isBlank(hiId)) {
            HabitatInventory hi = getHabitatInventoryById(hiId);
            model.addAttribute("referhi", hi);
        }
        model.addAttribute("alllakeslist", locationLoadService.getAllLakes());
        model.addAttribute("allstreamslist", locationLoadService.getAllStreams());
        model.addAttribute("allbrancheslist", locationLoadService.getAllBranchLentics());
        model.addAttribute("allstationslist", locationLoadService.getAllStations());
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "habitatinventory/edit";
    }

    @Offline
    @GetMapping(value = "/main/offline")
    public String addOrEditPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:addOrEditPageOffline)", CONTROLLER_NAME);
        model.addAttribute("alllakeslist", locationLoadService.getAllLakes());
        model.addAttribute("allstreamslist", locationLoadService.getAllStreams());
        model.addAttribute("allbrancheslist", locationLoadService.getAllBranchLentics());
        model.addAttribute("allstationslist", locationLoadService.getAllStations());
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "habitatinventory/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_HABITAT_INVENTORY)
    @GetMapping(value = "/transect")
    public String electrofishingPage(@RequestParam(name = "hiId", required = false) String hiId, Model model) {
        CommonUtils.getLogger().debug("{}:electrofishingPage with hiId({})", CONTROLLER_NAME, hiId);
        if (!StringUtils.isBlank(hiId) && !hiId.contains("#")) {
            HabitatInventory hi = getHabitatInventoryById(hiId);
            model.addAttribute("referhi", hi);
        }
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "habitatinventory/transect";
    }

    @Offline
    @GetMapping(value = "/transect/offline")
    public String electrofishingPageOffline(Model model) {
        CommonUtils.getLogger().debug("{}:electrofishingPageOffline", CONTROLLER_NAME);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "habitatinventory/transect";
    }

    private HabitatInventory getHabitatInventoryById(String hiId) {
        return hiService.getById(CommonUtils.getLongFromString(hiId, "ID of HabitatInventory"));
    }
}
