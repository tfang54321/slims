package ca.gc.dfo.slims.api.location;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.location.StationService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_API_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_API_PATH;

/**
 * @author ZHUY
 *
 */
@RestController
@RequestMapping(value = { ENG_API_PATH, FRA_API_PATH })
public class StationRestController {
    private static final String REST_API_NAME = "StationRestController";

    @Autowired
    private StationService    stationService;
    @Autowired
    private AppMessages        messages;

    /**
     *
     * @return List of stations belong to one branchlentic
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/stations/branchlentic/{branchlentic_id}")
    public List<Station> getAllStationsForABranchLentic(
        @PathVariable(value = "branchlentic_id") String branchlenticId) {
        CommonUtils.getLogger().debug(
            "{}:getAllReachsForAStream with branchlenticId({})", REST_API_NAME, branchlenticId);
        return stationService.getAllStationsByBranchLenticId(branchlenticId);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PostMapping(value = "/stations/add/{branchlentic_id}")
    public ResponseDTO<Station> saveStation(@PathVariable(value = "branchlentic_id") String branchlenticId,
                                            @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:saveStation with branchlenticId({}) and formData {}",
            REST_API_NAME, branchlenticId, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_STATION_SUCCESS.getName()),
            stationService.saveStationFromFormData(branchlenticId, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PutMapping(value = "/stations/{id}")
    public ResponseDTO<Station> updateStation(@PathVariable(value = "id") String id,
                                              @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:updateStation with id({}) and formData {}", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_STATION_SUCCESS.getName()),
            stationService.updateStationFromFormData(id, formData));
    }

    /**
     * Get station by Id
     *
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/stations/{id}")
    public ResponseDTO<Station> getStationById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getStationById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            stationService.getById(CommonUtils.getLongFromString(id, "ID of Station")));
    }

    /**
     * Get all stations by branchLentic Id
     *
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/branchLentics/{id}/stations")
    public ResponseDTO<List<Station>> getAllStationsByBranchLenticId(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getAllStationsByBranchLenticId with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            stationService.getAllStationsByBranchLenticId(id));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_LOCATIONS)
    @DeleteMapping(value = "/stations/delete/{id}")
    public ResponseDTO<Void> deleteStation(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteStation with id({})", REST_API_NAME, id);
        stationService.deleteById(CommonUtils.getLongFromString(id, "ID of Station"));
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_STATION_SUCCESS.getName()),
            null);
    }
}