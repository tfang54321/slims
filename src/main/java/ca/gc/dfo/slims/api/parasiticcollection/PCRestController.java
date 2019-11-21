package ca.gc.dfo.slims.api.parasiticcollection;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.parasiticcollection.ParasiticCollection;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.dto.parasiticcollection.ParasiticCollectionDTO;
import ca.gc.dfo.slims.service.parasiticcollection.ParasiticCollectionService;
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

import java.text.ParseException;
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
public class PCRestController {
    private static final String REST_API_NAME = "PCRestController";

    @Autowired
    private ParasiticCollectionService    pcService;
    @Autowired
    private AppMessages                    messages;

    /**
     *
     * @return List of aas
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS)
    @GetMapping(value = "/pcs")
    public List<ParasiticCollectionDTO> getParasiticCollections(@RequestParam String yearOp,
                                                                @RequestParam String year) {
        return CommonUtils.getAllObjectByYear(
            yearOp, year, pcService, REST_API_NAME + "getParasiticCollections");
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_PARASITIC_COLLECTIONS)
    @PostMapping(value = "/pcs/add")
    public ResponseDTO<ParasiticCollection> savePc(@RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:save with formData {}", REST_API_NAME, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_PARASITICCOLLECTION_SUCCESS.getName()),
            pcService.savePcFromFormData(formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_PARASITIC_COLLECTIONS)
    @PutMapping(value = "/pcs/{id}")
    public ResponseDTO<ParasiticCollection> updatePc(@PathVariable(value = "id") String id,
                                                     @RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info(
            "{}:updatePc with id({}) and formData {}", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_PARASITICCOLLECTION_SUCCESS.getName()),
            pcService.updatePcFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_PARASITIC_COLLECTIONS)
    @PutMapping(value = "/pcs/attachments/{id}")
    public ResponseDTO<ParasiticCollection> updatePcAttachments(@PathVariable(value = "id") String id,
                                                                @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:updatePcAttachments with id({}) and formData {}", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_PARASITICCOLLECTION_SUCCESS.getName()),
            pcService.updatePcAttachmentsFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS)
    @GetMapping(value = "/pcs/{id}")
    public ResponseDTO<ParasiticCollection> getParasiticCollectionById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getParasiticCollectionById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            pcService.getById(CommonUtils.getLongFromString(id, "ID of ParasiticCollection")));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_PARASITIC_COLLECTIONS)
    @DeleteMapping(value = "/pcs/delete/{id}")
    public ResponseDTO<Void> deleteParasiticCollection(@PathVariable String id) {
        CommonUtils.getLogger().debug("{}:deleteParasiticCollection with id({})", REST_API_NAME, id);
        pcService.deleteById(CommonUtils.getLongFromString(id, "ID of ParasiticCollection"));
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_PARASITICCOLLECTION_SUCCESS.getName()),
            null);
    }
}