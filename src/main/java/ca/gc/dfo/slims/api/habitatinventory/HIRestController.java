package ca.gc.dfo.slims.api.habitatinventory;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.habitat.HabitatInventory;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.dto.habitat.HabitatInventoryDTO;
import ca.gc.dfo.slims.service.habitatinventory.HabitatInventoryService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class HIRestController {
    private static final String REST_API_NAME = "HIRestController";

    @Autowired
    private HabitatInventoryService    hiService;
    @Autowired
    private AppMessages                messages;

    /**
     *
     * @return List of hi
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_HABITAT_INVENTORY)
    @GetMapping(value = "/his")
    public List<HabitatInventoryDTO> getAllHabitatInventories(@RequestParam String yearOp, @RequestParam String year) {
        return CommonUtils.getAllObjectByYear(
            yearOp, year, hiService, REST_API_NAME + "getAllHabitatInventories");
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_HABITAT_INVENTORY)
    @PostMapping(value = "/hi/add")
    public ResponseDTO<HabitatInventory> saveHabitatInventory(@RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:saveHabitatInventory with formData {}", REST_API_NAME,  formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_HABITATINVENTORY_SUCCESS.getName()),
            hiService.saveHiFromFormData(formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_HABITAT_INVENTORY)
    @PostMapping(value = "/hi/sync")
    public ResponseDTO<List<HabitatInventory>> syncHabitatInventoryOfflineData(@RequestBody List<Object> objData)
        throws ParseException {
        CommonUtils.getLogger().info(
            "{}:syncHabitatInventoryOfflineData with {} objData", REST_API_NAME,  objData.size());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.OFFLINEDATA_SYNC_SUCCESS.getName()),
            hiService.syncOfflineData(objData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_HABITAT_INVENTORY)
    @PutMapping(value = "/hi/transect/{id}")
    public ResponseDTO<HabitatInventory> updateHiTransect(@PathVariable(value = "id") String id,
                                                          @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateHiTransect with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_HABITATINVENTORY_SUCCESS.getName()),
            hiService.updateHiTransectFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_HABITAT_INVENTORY)
    @PutMapping(value = "/hi/{id}")
    public ResponseDTO<HabitatInventory> updateHabitatInventory(@PathVariable(value = "id") String id,
                                                                @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateHabitatInventory with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_HABITATINVENTORY_SUCCESS.getName()),
            hiService.updateHiFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_HABITAT_INVENTORY)
    @GetMapping(value = "/hi/{id}")
    public ResponseDTO<HabitatInventory> getHabitatInventoryById(@PathVariable("id") String id) {
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            hiService.getById(CommonUtils.getLongFromString(id, "ID of HabitatInventory")));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_HABITAT_INVENTORY)
    @DeleteMapping(value = "/hi/delete/{id}")
    public ResponseDTO<Void> deleteHabitatInventory(@PathVariable String id) {
        CommonUtils.getLogger().info("delete HabitatInventory with id: " + id);
        hiService.deleteById(CommonUtils.getLongFromString(id, "ID of HabitatInventory"));
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_HABITATINVENTORY_SUCCESS.getName()),
            null);
    }
    
}