package ca.gc.dfo.slims.api.location;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.location.LakeService;
import ca.gc.dfo.slims.service.location.StreamService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_API_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_API_PATH;

/**
 * @author ZHUY
 *
 */
@RestController
@RequestMapping(value = { ENG_API_PATH, FRA_API_PATH })
public class StreamRestController {
    private static final String REST_API_NAME = "StreamRestController";

    @Autowired
    private StreamService    streamService;
    @Autowired
    private LakeService        lakeService;
    @Autowired
    private AppMessages        messages;

    /**
     *
     * @return List of streams belong to one lake
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/streams/lake/{lake_id}")
    public List<Stream> getAllStreamsForALake(@PathVariable(value = "lake_id") String lakeId) {
        CommonUtils.getLogger().debug("{}:getAllStreamsForALake with lakeId({})", REST_API_NAME, lakeId);
        return streamService.getAllByLakeId(lakeId);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PostMapping(value = "/streams/add/{lake_id}")
    public ResponseDTO<Stream> saveStream(@PathVariable(value = "lake_id") String lakeId,
                                          @Valid @RequestBody Stream stream,
                                          Errors errors) {
        CommonUtils.getLogger().info("{}:saveLake with LakeId({}), stream({} - {}) and Errors count({})",
            REST_API_NAME, lakeId, stream.getId(), stream.getShowText(), errors.getErrorCount());
        ValidationUtils.validateErrors(errors, REST_API_NAME + ":saveStream");

        Lake lake = lakeService.getById(CommonUtils.getLongFromString(lakeId, "ID of Lake"));
        stream.setLake(lake);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_STREAM_SUCCESS.getName()),
            streamService.save(stream));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PutMapping(value = "/streams/{id}")
    public ResponseDTO<Stream> updateStream(@PathVariable(value = "id") String id,
                                            @RequestBody Stream updatedStreamDetail) {
        CommonUtils.getLogger().info("{}:updateStream with id({}) and stream({} - {})",
            REST_API_NAME, id, updatedStreamDetail.getId(), updatedStreamDetail.getShowText());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_STREAM_SUCCESS.getName()),
            streamService.updateStream(id, updatedStreamDetail));
    }

    /**
     * Get stream by Id
     *
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/streams/{id}")
    public ResponseDTO<Stream> getStreamById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getStreamById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            streamService.getById(CommonUtils.getLongFromString(id, "ID of Stream")));
    }

    /**
     * Get all streams by lake ID
     *
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/lakes/{id}/streams")
    public ResponseDTO<List<Stream>> getAllStreamByLakeId(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getAllStreamByLakeId with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS, null, streamService.getAllByLakeId(id));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_LOCATIONS)
    @DeleteMapping(value = "/streams/delete/{id}")
    public ResponseDTO<Void> deleteStream(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteStream with id({})", REST_API_NAME, id);
        streamService.deleteById(CommonUtils.getLongFromString(id, "ID of Stream"));
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_STREAM_SUCCESS.getName()),
            null);
    }
}