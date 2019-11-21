package ca.gc.dfo.slims.api.location;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.Reach;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.location.ReachService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_API_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_API_PATH;

/**
 * @author ZHUY
 *
 */
@RestController
@RequestMapping(value = { ENG_API_PATH, FRA_API_PATH })
public class StationReachRestController {
    private static final String REST_API_NAME = "StationReachRestController";

    @Autowired
    private ReachService    reachService;
    @Autowired
    private AppMessages     messages;

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PutMapping(value = "/stationsreaches")
    public ResponseDTO<Reach> updateReachAssignedStations(
        @RequestParam String reachId,
        @RequestParam(value = "stationIds[]", required = false) Long[] stationIds) {
        CommonUtils.getLogger().info("{}:updateReachAssignedStations with reachId({}) and ({}) of stationIds",
            REST_API_NAME, reachId, stationIds.length);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_ASSIGNED_STATION_SUCCESS.getName()),
            reachService.updateReachAssignedStations(reachId, stationIds));
    }
}