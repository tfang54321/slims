package ca.gc.dfo.slims.controller.locations;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.service.location.BranchLenticService;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_STATION_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_STATION_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping(value = { ENG_STATION_PATH, FRA_STATION_PATH })
public class StationController {
    private static final String CONTROLLER_NAME = "StationController";

    @Autowired
    private BranchLenticService    branchLenticService;
    @Autowired
    private ResourceLoadService    resourceLoadService;

    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping
    public String getStationsForBranchLentic(@RequestParam(name = "branchLenticId") String branchLenticId,
                                             Model model) {
        CommonUtils.getLogger().debug(
            "{}:getStationsForBranchLentic with branchLenticId({})", CONTROLLER_NAME, branchLenticId);
        BranchLentic branchLentic = branchLenticService.getById(
            CommonUtils.getLongFromString(branchLenticId, "ID of BranchLentic"));
        model.addAttribute("referstream", branchLentic.getStream());
        model.addAttribute("referlake", branchLentic.getStream().getLake());
        model.addAttribute("refBranchLentic", branchLentic);
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        return "stations/list";
    }
}