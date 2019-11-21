package ca.gc.dfo.slims.api.location;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.location.BranchLenticService;
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
public class BranchLenticRestController {
    private static final String REST_API_NAME = "BranchLenticRestController";

    @Autowired
    private BranchLenticService  branchLenticService;
    @Autowired
    private StreamService        streamService;
    @Autowired
    private AppMessages          messages;

    /**
     * @return List of branchLentics belong to one stream
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/branchLentics/stream/{stream_id}")
    public List<BranchLentic> getAllBranchLenticsForAStream(@PathVariable(value = "stream_id") String streamId) {
        CommonUtils.getLogger().debug("{}:getAllBranchLenticsForAStream with streamId({})", REST_API_NAME, streamId);
        return branchLenticService.getAllByStreamId(streamId);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PostMapping(value = "/branchLentics/add/{stream_id}")
    public ResponseDTO<BranchLentic> saveBranchLentic(@PathVariable(value = "stream_id") String streamId,
                                                      @Valid @RequestBody BranchLentic branchLentic,
                                                      Errors errors) {
        CommonUtils.getLogger().info(
            "{}:saveBranchLentic with streamId({}), BranchLentic({} - {}) and Errors count({})",
            REST_API_NAME, streamId, branchLentic.getId(), branchLentic.getShowText(), errors.getErrorCount());
        ValidationUtils.validateErrors(errors, REST_API_NAME + ":saveBranchLentic");

        Stream stream = streamService.getById(CommonUtils.getLongFromString(streamId, "ID of Stream"));
        branchLentic.setStream(stream);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_BRANCHLENTIC_SUCCESS.getName()),
            branchLenticService.save(branchLentic));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PutMapping(value = "/branchLentics/{id}")
    public ResponseDTO<BranchLentic> updateBranchLentic(@PathVariable(value = "id") String id,
                                                        @RequestBody BranchLentic updatedBranchLenticDetail) {
        CommonUtils.getLogger().info("{}:updateBranchLentic with id({}) and BranchLentic({} - {})",
            REST_API_NAME, id, updatedBranchLenticDetail.getId(), updatedBranchLenticDetail.getShowText());
        return branchLenticService.updateBranchLentic(id, updatedBranchLenticDetail);
    }

    /**
     * Get branchLentic by Id
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/branchLentics/{id}")
    public ResponseDTO<BranchLentic> getBranchLenticById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getBranchLenticById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            branchLenticService.getById(CommonUtils.getLongFromString(id, "ID of BranchLentic")));
    }

    /**
     * Get branchLentic by stream Id
     *
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "streams/{id}/branchLentics")
    public ResponseDTO<List<BranchLentic>> getAllBranchLenticsByStreamId(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getAllBranchLenticsByStreamId with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS, null, branchLenticService.getAllByStreamId(id));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_LOCATIONS)
    @DeleteMapping(value = "/branchLentics/delete/{id}")
    public ResponseDTO<Void> deleteBranchLentic(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteBranchLentic with id({})", REST_API_NAME, id);
        branchLenticService.deleteById(CommonUtils.getLongFromString(id, "ID of BranchLentic"));
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_BRANCHLENTIC_SUCCESS.getName()),
            null);
    }
}