package ca.gc.dfo.slims.api.fishmodules;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.fishmodule.FMRunNet;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.fishmodules.FMRunnetService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
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
import org.springframework.web.server.ResponseStatusException;

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
public class FMRunnetRestController {
    private static final String REST_API_NAME = "FMRunNetRestController";

    private enum OPER {
        DELETE_BY_ID,
        GET_BY_ID,
        SAVE,
        UPDATE
    }

    @Autowired
    private FMRunnetService    fmRunnetService;
    @Autowired
    private AppMessages        messages;

    /**
     * Get list of FMRunNet by FM_ID
     *
     * @param fmId String of the FM_ID
     * @return list of FMRunNet objects
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_FISH_COLLECTIONS)
    @GetMapping(value = "/runnet/fm/{fm_id}")
    public List<FMRunNet> getAllFMRunNet(@PathVariable("fm_id") String fmId) {
        CommonUtils.getLogger().info("{}:getAllFMRunNet with fm_id({})", REST_API_NAME, fmId);
        return fmRunnetService.getAllByFMId(CommonUtils.getLongFromString(fmId, "fm_id of FMRunNet"));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_FISH_COLLECTIONS)
    @PostMapping(value = "/runnet/add/{fm_id}")
    public ResponseDTO<FMRunNet> saveFMRunnet(@PathVariable("fm_id") String fmId,
                                              @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:saveFMRunnet with fm_id ({}) and formData {}", REST_API_NAME, fmId, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_FMRUNNET_SUCCESS.getName()),
            operateOnFmRunNet(OPER.SAVE, fmId, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_FISH_COLLECTIONS)
    @PutMapping(value = "/runnet/{id}")
    public ResponseDTO<FMRunNet> updateRunnet(@PathVariable(value = "id") String id,
                                              @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:updateRunnet with id ({}) and formData {}", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_FMRUNNET_SUCCESS.getName()),
            operateOnFmRunNet(OPER.UPDATE, id, formData));
    }

    /**
     * Get FMRunNet by ID
     *
     * @param id String of the desired FMRunNet ID
     * @return the FMRunNet object with the specified ID
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_FISH_COLLECTIONS)
    @GetMapping(value = "/runnet/{id}")
    public ResponseDTO<FMRunNet> getFMRunnetById(@PathVariable("id") String id) {
        CommonUtils.getLogger().info("{}:getFMRunnetById with id ({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS, null, operateOnFmRunNet(OPER.GET_BY_ID, id, null));
    }

    /**
     * Delete a FMRunNet by ID
     *
     * @param id String of the to-be-deleted FMRunNet ID
     * @return the FMRunNet object which has been deleted
     */
    @PreAuthorize(SecurityHelper.EL_DELETE_FISH_COLLECTIONS)
    @DeleteMapping(value = "/runnet/delete/{id}")
    public ResponseDTO<Void> deleteRunnet(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteRunnet with id ({})", REST_API_NAME, id);
        operateOnFmRunNet(OPER.DELETE_BY_ID, id, null);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS, messages.get(ResponseMessages.DELETE_FMRUNNET_SUCCESS.getName()), null);
    }

    private FMRunNet operateOnFmRunNet(OPER oper, String id, Map<String, String> formData) {
        FMRunNet fmRunNet = null;
        String paramMsg = "";
        try {
            switch (oper) {
                case DELETE_BY_ID:
                    paramMsg = String.format("ID(%s)", id);
                    fmRunnetService.deleteById(CommonUtils.getLongFromString(id, "ID of FMRunNet"));
                    break;
                case GET_BY_ID:
                    paramMsg = String.format("ID(%s)", id);
                    fmRunNet = fmRunnetService.getById(CommonUtils.getLongFromString(id, "ID of FMRunNet"));
                    break;
                case SAVE:
                    paramMsg = String.format("fm_id(%s) and formData %s", id, formData);
                    fmRunNet = fmRunnetService.saveRunnetFromFormData(id, formData);
                    break;
                case UPDATE:
                    paramMsg = String.format("ID(%s) and formData %s", id, formData);
                    fmRunNet = fmRunnetService.updateRunnetFromFormData(id, formData);
                    break;
            }
        } catch (ResponseStatusException re) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s Got ResponseStatusException for %s with %s",
                    REST_API_NAME, oper.name(), paramMsg), re);
        }
        return fmRunNet;
    }
}