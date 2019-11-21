package ca.gc.dfo.slims.api.location;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.location.LakeService;
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
public class LakeRestController {
    private static final String REST_API_NAME = "LakeRestController";

    @Autowired
    private LakeService    lakeService;
    @Autowired
    private AppMessages    messages;
    
    /**
     *
     * @return List of Lakes
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/lakes")
    public List<Lake> getAllLakes() {
        CommonUtils.getLogger().debug("{}:getAllLakes", REST_API_NAME);
        return lakeService.getAll();
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PostMapping(value = "/lakes/add")
    public ResponseDTO<Lake> saveLake(@Valid @RequestBody Lake lake, Errors errors) {
        CommonUtils.getLogger().debug("{}:saveLake with Lake({} - {}) and Errors count({}) ",
            REST_API_NAME, lake.getId(), lake.getShowText(), errors.getErrorCount());
        ValidationUtils.validateErrors(errors, REST_API_NAME + ":saveLake");

        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_LAKE_SUCCESS.getName()),
            lakeService.save(lake));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LOCATIONS)
    @PutMapping(value = "/lakes/{id}")
    public ResponseDTO<Lake> updateLake(@PathVariable(value = "id") String id, @RequestBody Lake updatedLakeDetail) {
        CommonUtils.getLogger().info("{}:updateLake with id({}) and lake({} - {})",
            REST_API_NAME, id, updatedLakeDetail.getId(), updatedLakeDetail.getShowText());

        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LAKE_SUCCESS.getName()),
            lakeService.updateLake(id, updatedLakeDetail));
    }

    /**
     * Get lake by Id
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LOCATIONS)
    @GetMapping(value = "/lakes/{id}")
    public ResponseDTO<Lake> getLakeById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getLakeById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            lakeService.getById(CommonUtils.getLongFromString(id, "ID of Lake")));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_LOCATIONS)
    @DeleteMapping(value = "/lakes/delete/{id}")
    public ResponseDTO<Void> deleteLake(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteLake with id({})", REST_API_NAME, id);
        lakeService.deleteById(CommonUtils.getLongFromString(id, "ID of Lake"));
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_LAKE_SUCCESS.getName()),
            null);
    }
}