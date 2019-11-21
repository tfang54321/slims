package ca.gc.dfo.slims.api.fishmodules;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_API_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_API_PATH;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.dto.fishmodules.FishModuleDTO;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.YearUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.fishmodule.FishModule;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.fishmodules.FishModuleService;
import ca.gc.dfo.slims.utility.AppMessages;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author ZHUY
 *
 */
@RestController
@RequestMapping(value = { ENG_API_PATH, FRA_API_PATH })
public class FMRestController {
    private static final String REST_API_NAME = "FMRestController";

    private enum OPER {
        DELETE_BY_ID,
        GET_BY_ID,
        SAVE,
        UPDATE,
        UPDATE_HABITAT
    }

    @Autowired
    private FishModuleService    fmService;
    @Autowired
    private AppMessages          messages;

    /**
     * Get all the FishModuleDTO objects which matches the specified year based on the specified year operation
     *
     * @param yearOp String specifies the operation on the year, supported values are "equal", "gte" and "lte"
     * @param year String of the comparison year, when value is "All" returns all the FishModule objects.
     * @return List of FishModuleDTO which are matching the specified condition.
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_FISH_COLLECTIONS)
    @GetMapping(value = "/fms")
    public List<FishModuleDTO> getAllFishModules(@RequestParam String yearOp, @RequestParam String year) {
        CommonUtils.getLogger().debug("{}:getAllFishModules with yearOp({}) and year({})", REST_API_NAME, yearOp, year);

        List<FishModuleDTO> fishModuleDtoList = new LinkedList<>();
        try {
            switch (YearUtils.getYearOperation(yearOp, year)) {
                case ALL:
                    fishModuleDtoList = fmService.getAll();
                    break;
                case EQUAL:
                    fishModuleDtoList = fmService.getAll(year);
                    break;
                case GTE:
                    fishModuleDtoList = fmService.getAllAfterYear(year);
                    break;
                case LTE:
                    fishModuleDtoList = fmService.getAllBeforeYear(year);
                    break;
            }
        } catch (ResponseStatusException re) {
            CommonUtils.getLogger().error(
                "{}:getAllFishModules Got ResponseStatusException with yearOp({}) and year({})",
                REST_API_NAME, yearOp, year, re);
            throw re;
        } catch (Exception e) {
            String errorMsg = String.format(
                "%s:getAllFishModules Got ResponseStatusException with yearOp(%s) and year(%s)",
                REST_API_NAME, yearOp, year);
            CommonUtils.getLogger().error(errorMsg, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
        }

        return fishModuleDtoList;
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_FISH_COLLECTIONS)
    @PostMapping(value = "/fms/add")
    public ResponseDTO<FishModule> saveFm(@RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:updateFm with formData {}", REST_API_NAME, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_FISHMODULE_SUCCESS.getName()),
            operateOnFishModule(OPER.SAVE, null, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_FISH_COLLECTIONS)
    @PutMapping(value = "/fms/habitats/{id}")
    public ResponseDTO<FishModule> updateFmHabitats(@PathVariable(value = "id") String id,
                                                    @RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:updateFm with id({}) and formData {}", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_FISHMODULE_SUCCESS.getName()),
            operateOnFishModule(OPER.UPDATE_HABITAT, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_FISH_COLLECTIONS)
    @PutMapping(value = "/fms/{id}")
    public ResponseDTO<FishModule> updateFm(@PathVariable(value = "id") String id,
                                            @RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:updateFm with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_FISHMODULE_SUCCESS.getName()),
            operateOnFishModule(OPER.UPDATE, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_FISH_COLLECTIONS)
    @PostMapping(value = "/fms/sync")
    public ResponseDTO<List<FishModule>> syncFishModuleOfflineData(@RequestBody List<Object> objData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:syncFishModuleOfflineData with {} objects", REST_API_NAME, objData.size());

        List<FishModule> fishModuleList = null;
        try {
            fishModuleList = fmService.syncOfflineData(objData);
        } catch (ParseException pe) {
            ExceptionUtils.logAndRethrowParseException(
                String.format("%s:syncFishModuleOfflineData Got ParseException", REST_API_NAME), pe);
        } catch (ResponseStatusException re) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s:syncFishModuleOfflineData Got ResponseStatusException", REST_API_NAME), re);
        }

        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.OFFLINEDATA_SYNC_SUCCESS.getName()), fishModuleList);
    }

    /**
     * Get FishModule object by ID
     *
     * @param id String of the desired FishModule ID
     * @return the FishModule object with the specified ID
     * @throws ParseException when error occured
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_FISH_COLLECTIONS)
    @GetMapping(value = "/fms/{id}")
    public ResponseDTO<FishModule> getFishModuleById(@PathVariable("id") String id) throws ParseException {
        CommonUtils.getLogger().debug("{}:getFishModuleById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS, null, operateOnFishModule(OPER.GET_BY_ID, id, null));
    }

    /**
     * Delete a FishModule by ID
     *
     * @param id String of the to-be-deleted FishModule ID
     * @return the FishModule object which has been deleted
     * @throws ParseException when error occurred
     */
    @PreAuthorize(SecurityHelper.EL_DELETE_FISH_COLLECTIONS)
    @DeleteMapping(value = "/fms/delete/{id}")
    public ResponseDTO<Void> deleteFishModule(@PathVariable String id) throws ParseException {
        CommonUtils.getLogger().info("{}:deleteFishModule with id({})", REST_API_NAME, id);
        operateOnFishModule(OPER.DELETE_BY_ID, id, null);
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_FISHMODULE_SUCCESS.getName()), null);
    }

    private FishModule operateOnFishModule(OPER oper, String id, Map<String, String> formData) throws ParseException {
        FishModule fishModule = null;
        String paramMsg = "";
        try {
            switch (oper) {
                case DELETE_BY_ID:
                    paramMsg = String.format("id(%s)", id);
                    fmService.deleteById(
                        CommonUtils.getLongFromString(id, String.format("%s %s", REST_API_NAME, oper.name())));
                case GET_BY_ID:
                    paramMsg = String.format("id(%s)", id);
                    fishModule = fmService.getById(
                        CommonUtils.getLongFromString(id, String.format("%s %s", REST_API_NAME, oper.name())));
                    break;
                case SAVE:
                    paramMsg = formData.toString();
                    fishModule = fmService.saveFmFromFormData(formData);
                    break;
                case UPDATE:
                    paramMsg = String.format("id(%s) and formData %s", id, formData);
                    fishModule = fmService.updateFmFromFormData(id, formData);
                    break;
                case UPDATE_HABITAT:
                    paramMsg = String.format("id(%s) and formData %s", id, formData);
                    fishModule = fmService.updateFmHabitatsFromFormData(id, formData);
                    break;
                    default:
            }
        } catch (ParseException pe) {
            ExceptionUtils.logAndRethrowParseException(String.format("%s Got ParseException for %s with %s",
                REST_API_NAME, oper.name(), paramMsg), pe);
        } catch (ResponseStatusException re) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s Got ResponseStatusException for %s with %s",
                    REST_API_NAME, oper.name(), paramMsg), re);
        }
        return fishModule;
    }
}