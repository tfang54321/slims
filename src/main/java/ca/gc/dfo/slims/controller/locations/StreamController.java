package ca.gc.dfo.slims.controller.locations;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.service.location.LakeService;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_STREAM_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_STREAM_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping(value = { ENG_STREAM_PATH, FRA_STREAM_PATH })
public class StreamController {
    private static final String CONTROLLER_NAME = "StationController";

    @Autowired
    private LakeService lakeService;

    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping
    public String streamListForLake(@RequestParam(name = "lakeId") String lakeId, Model model) {
        CommonUtils.getLogger().debug("{}:streamListForLake with lakeId({})", CONTROLLER_NAME, lakeId);
        Lake lake = lakeService.getById(CommonUtils.getLongFromString(lakeId, "ID of Lake"));
        model.addAttribute("referlake", lake);
        return "streams/list";
    }

}