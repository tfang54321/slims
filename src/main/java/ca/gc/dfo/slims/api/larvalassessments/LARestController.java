package ca.gc.dfo.slims.api.larvalassessments;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LarvalAssessment;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.dto.larvalassessments.LarvalAssessmentDTO;
import ca.gc.dfo.slims.service.common.SpecieService;
import ca.gc.dfo.slims.service.larvalassessments.LarvalAssessmentService;
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
public class LARestController {
    private static final String REST_API_NAME = "LARestController";

    @Autowired
    private LarvalAssessmentService    laService;
    @Autowired
    private SpecieService            specieService;
    @Autowired
    private AppMessages                messages;

    /**
     *
     * @return List of las
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/las")
    public List<LarvalAssessmentDTO> getAllLarvalAssessments(@RequestParam String yearOp, @RequestParam String year) {
        return CommonUtils.getAllObjectByYear(yearOp, year, laService, REST_API_NAME + "getAllLarvalAssessments");
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PostMapping(value = "/las/add")
    public ResponseDTO<LarvalAssessment> saveLa(@RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:saveLa with formData {}", REST_API_NAME, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_LARVALASSESSMENT_SUCCESS.getName()),
            laService.saveLaFromFormData(formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PutMapping(value = "/las/electrofishing/{id}")
    public ResponseDTO<LarvalAssessment> updateLaElectroFishing(@PathVariable(value = "id") String id,
                                                                @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateLaElectroFishing with id({}) formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LARVALASSESSMENT_SUCCESS.getName()),
            laService.updateLaElectroFishingFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PutMapping(value = "/las/granularbayer/{id}")
    public ResponseDTO<LarvalAssessment> updateLaGranularBayer(@PathVariable(value = "id") String id,
                                                               @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateLaGranularBayer with id({}) formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LARVALASSESSMENT_SUCCESS.getName()),
            laService.updateLaGranularBayerFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PutMapping(value = "/las/physicalchemical/{id}")
    public ResponseDTO<LarvalAssessment> updateLaPhysicalChemical(@PathVariable(value = "id") String id,
                                                                  @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateLaPhysicalChemical with id({}) formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LARVALASSESSMENT_SUCCESS.getName()),
            laService.updateLaPhysicalChemicalFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PutMapping(value = "/las/collectioncon/{id}")
    public ResponseDTO<LarvalAssessment> updateLaCollectioncon(@PathVariable(value = "id") String id,
                                                               @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateLaCollectioncon with id({}) formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LARVALASSESSMENT_SUCCESS.getName()),
            laService.updateLaCollectionconFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PutMapping(value = "/las/fishobsercol/{id}")
    public ResponseDTO<LarvalAssessment> updateLaFishobsercol(@PathVariable(value = "id") String id,
                                                              @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateLaFishobsercol with id({}) formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LARVALASSESSMENT_SUCCESS.getName()),
            laService.updateLaFishobsercolFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PutMapping(value = "/las/fishindividual/{id}")
    public ResponseDTO<Specie> updateLaSpecie(@PathVariable(value = "id") String id,
                                              @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateLaSpecie with id({}) formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LARVALASSESSMENT_SUCCESS.getName()),
            specieService.updateLaSpecieFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PutMapping(value = "/las/{id}")
    public ResponseDTO<LarvalAssessment> updateLa(@PathVariable(value = "id") String id,
                                                  @RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:updateLa with id({}) formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_LARVALASSESSMENT_SUCCESS.getName()),
            laService.updateLaFromFormData(id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_LARVAL_ASSESSMENTS)
    @PostMapping(value = "/las/sync")
    public ResponseDTO<List<LarvalAssessment>> syncLAOfflineData(@RequestBody List<Object> objData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:syncLAOfflineData with {} objData", REST_API_NAME, objData.size());
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.OFFLINEDATA_SYNC_SUCCESS.getName()),
            laService.syncOfflineData(objData));
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_LARVAL_ASSESSMENTS)
    @GetMapping(value = "/las/{id}")
    public ResponseDTO<LarvalAssessment> getLarvalAssessmentById(@PathVariable("id") String id) {
        CommonUtils.getLogger().info("{}:getLarvalAssessmentById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            null, laService.getById(CommonUtils.getLongFromString(id, "ID of LarvalAssessment")));
    }

    @PreAuthorize(SecurityHelper.EL_DELETE_LARVAL_ASSESSMENTS)
    @DeleteMapping(value = "/las/delete/{id}")
    public ResponseDTO<Void> deleteLarvalAssessment(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteLarvalAssessment with id({})", REST_API_NAME, id);
        laService.deleteById(CommonUtils.getLongFromString(id, "ID of LarvalAssessment"));
        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_LARVALASSESSMENT_SUCCESS.getName()), null);
    }
    
}