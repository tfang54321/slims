package ca.gc.dfo.slims.controller.locations;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.service.location.StreamService;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_REACH_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_REACH_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping(value = { ENG_REACH_PATH, FRA_REACH_PATH })
public class ReachController {
    private static final String CONTROLLER_NAME = "ReachController";

    @Autowired
    private StreamService streamService;

    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping
    public String getReachesForStream(@RequestParam(name = "streamId") String streamId, Model model) {
        CommonUtils.getLogger().debug("{}:getReachesForStream with streamId({})", CONTROLLER_NAME, streamId);
        Stream stream = streamService.getById(CommonUtils.getLongFromString(streamId, "ID of Stream"));
        model.addAttribute("referstream", stream);
        model.addAttribute("referlake", stream.getLake());
        return "reaches/list";
    }

}