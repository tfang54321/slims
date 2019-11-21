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
public class ReachRestController {
    private static final String REST_API_NAME = "ReachRestController";

    @Autowired
    private ReachService    reachService;
    @Autowired
    private AppMessages     messages;

    /**
     * @return List of reaches belong to one stream
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/reaches/stream/{stream_id}")
    public List<Reach> getAllReachsForAStream(@PathVariable(value = "stream_id") String streamId) {
        CommonUtils.getLogger().debug("{}:getAllReachsForAStream with streamId({})", REST_API_NAME, streamId);
        return reachService.getAllByStreamId(streamId);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PostMapping(value = "/reaches/add/{stream_id}")
    public ResponseDTO<Reach> saveReach(@PathVariable(value = "stream_id") String streamId,
                                        @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:saveReach with streamId({}) and formData {} ", REST_API_NAME, streamId, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_REACH_SUCCESS.getName()),
            reachService.saveReachFromFormData(streamId, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PutMapping(value = "/reaches/{id}")
    public ResponseDTO<Reach> updateReach(@PathVariable(value = "id") String id,
                                          @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:updateReach with id({}) and formData {} ", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_REACH_SUCCESS.getName()),
            reachService.updateReach(id, formData));
    }

    /**
     * Get reach by Id
     *
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/reaches/{id}")
    public ResponseDTO<Reach> getReachById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getReachById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            reachService.getById(CommonUtils.getLongFromString(id, "ID of Reach")));
    }

    /**
     * Get all reaches by stream Id
     *
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/streams/{id}/reaches")
    public ResponseDTO<List<Reach>> getAllReachByStreamId(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getAllReachByStreamId with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS, null, reachService.getAllByStreamId(id));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_LOCATIONS)
    @DeleteMapping(value = "/reaches/delete/{id}")
    public ResponseDTO<Void> deleteReach(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteReach with id({})", REST_API_NAME, id);
        reachService.deleteById(CommonUtils.getLongFromString(id, "ID of Reach"));
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_REACH_SUCCESS.getName()),
            null);
    }
}