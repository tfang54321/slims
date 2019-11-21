package ca.gc.dfo.slims.controller.locations;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_LAKE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_LAKE_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping(value = { ENG_LAKE_PATH, FRA_LAKE_PATH })
public class LakeController {
    private static final String CONTROLLER_NAME = "LakeController";

    @SuppressWarnings("unused")
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping
    public String lakeList(Model model) {
        CommonUtils.getLogger().debug("{}:lakeList", CONTROLLER_NAME);
        return "lakes/list";
    }

}