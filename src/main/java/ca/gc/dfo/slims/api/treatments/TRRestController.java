package ca.gc.dfo.slims.api.treatments;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.treatments.TRPrimaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.Treatment;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.dto.treatments.TreatmentDTO;
import ca.gc.dfo.slims.service.treatments.TRPrimaryAppService;
import ca.gc.dfo.slims.service.treatments.TRSecondaryAppService;
import ca.gc.dfo.slims.service.treatments.TreatmentService;
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
public class TRRestController {
    private static final String ID_OF_TREATMENT = "ID of treatment";
    private static final String REST_API_NAME = "TRRestController";

    private enum TREATMENT_OPERS {
        GET_BY_ID,
        SAVE,
        UPDATE,
        UPDATE_CHEM_ANALYSIS,
        UPDATE_DESIRED_CONCENTRATION,
        UPDATE_DISCHARGES,
        UPDATE_MIN_LETHAL_CONCENTRATION,
        UPDATE_PRIMARY_APP,
        UPDATE_SECONDARY_APP,
        UPDATE_WATER_CHEMS
    }

    private enum TR_SINGLE_SECONDARY_APP_OPERS {
        UPDATE,
        UPDATE_INDUCED_MORTALITY
    }

    @Autowired
    private TreatmentService        trService;
    @Autowired
    private TRPrimaryAppService     trPrimaryAppService;
    @Autowired
    private TRSecondaryAppService   trSecondaryAppService;
    @Autowired
    private AppMessages              messages;

    /**
     * Get all the TreatmentDTO objects which matches the specified year based on the specified year operation
     *
     * @param yearOp String specifies the operation on the year, supported values are "equal", "gte" and "lte"
     * @param year String of the comparison year, when value is "All" returns all the FishModule objects.
     * @return List of TreatmentDTO which are matching the specified condition.
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/treatments")
    public List<TreatmentDTO> getAllTreatments(@RequestParam String yearOp, @RequestParam String year) {
        return CommonUtils.getAllObjectByYear(yearOp, year, trService, REST_API_NAME + "getAllTreatments");
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PostMapping(value = "/treatments/add")
    public ResponseDTO<Treatment> saveTr(@RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:saveTr with formData {}", REST_API_NAME, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.SAVE, null, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/{id}")
    public ResponseDTO<Treatment> updateTr(@PathVariable(value = "id") String id,
                                           @RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:updateTr with id({}) and formData {}", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/primaryapp/{id}")
    public ResponseDTO<Treatment> updateTrPrimaryApp(@PathVariable(value = "id") String id,
                                                     @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateTrPrimaryApp with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE_PRIMARY_APP, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/singleprimaryapp/{id}")
    public ResponseDTO<TRPrimaryApplication> updateTrSinglePrimaryApp(@PathVariable(value = "id") String id,
                                                                      @RequestParam Map<String, String> formData)
        throws ParseException {
        String logPrefix = String.format("%s:updateTrSinglePrimaryApp with id(%s) and formData %s",
            REST_API_NAME, id, formData);
        CommonUtils.getLogger().info(logPrefix);
        TRPrimaryApplication trPrimaryApplication = null;
        try {
            trPrimaryApplication = trPrimaryAppService.updateTrPrimaryAppFromFormData(id, formData);
        } catch (ParseException pe) {
            ExceptionUtils.logAndRethrowParseException(String.format("%s got ParseException", logPrefix), pe);
        } catch (ResponseStatusException re) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s got ResponseStatusException", logPrefix), re);
        }
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            trPrimaryApplication);
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/secondaryapp/{id}")
    public ResponseDTO<Treatment> updateTrSecondaryApp(@PathVariable(value = "id") String id,
                                                       @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateTrSecondaryApp with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE_SECONDARY_APP, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/singlesecondaryapp/{id}")
    public ResponseDTO<TRSecondaryApplication> updateTrSingleSecondaryApp(@PathVariable(value = "id") String id,
                                                                          @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateTrSingleSecondaryApp with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTrSingleSecondaryApp(TR_SINGLE_SECONDARY_APP_OPERS.UPDATE, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/singlesecondaryapp/inducedmortality/{id}")
    public ResponseDTO<TRSecondaryApplication> updateTrSingleSecondaryAppInducedMortality(
        @PathVariable(value = "id") String id,
        @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateTrSinSeconAppInducedMortality with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTrSingleSecondaryApp(TR_SINGLE_SECONDARY_APP_OPERS.UPDATE_INDUCED_MORTALITY, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/desiredcon/{id}")
    public ResponseDTO<Treatment> updateTrDesiredConcentrations(@PathVariable(value = "id") String id,
                                                                @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateTrDesiredConcentrations with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE_DESIRED_CONCENTRATION, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/minlethalcon/{id}")
    public ResponseDTO<Treatment> updateTrMinLethalConcentrations(@PathVariable(value = "id") String id,
                                                                  @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info(
            "{}:updateTrMinLethalConcentrations with id({}) and formData {} ", REST_API_NAME, id, formData);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE_MIN_LETHAL_CONCENTRATION, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/waterchem/{id}")
    public ResponseDTO<Treatment> updateTrWaterChems(@PathVariable(value = "id") String id,
                                                     @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateTrWaterChems with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE_WATER_CHEMS, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/discharge/{id}")
    public ResponseDTO<Treatment> updateTrDischarges(@PathVariable(value = "id") String id,
                                                     @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateTrDischarges with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE_DISCHARGES, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_TREATMENTS)
    @PutMapping(value = "/treatments/chemanalysis/{id}")
    public ResponseDTO<Treatment> updateTrChemAnalysises(@PathVariable(value = "id") String id,
                                                         @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateTrChemAnalysises with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_TREATMENT_SUCCESS.getName()),
            operationOnTreatment(TREATMENT_OPERS.UPDATE_CHEM_ANALYSIS, id, formData));
    }

    /**
     * Get Treatment by ID
     *
     * @param id String of the Treatment
     * @return ResponseDTO object which contains the get status as well as the matching Treatment object
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_TREATMENTS)
    @GetMapping(value = "/treatments/{id}")
    public ResponseDTO<Treatment> getTreatmentById(@PathVariable("id") String id) throws ParseException {
        CommonUtils.getLogger().debug("{}:getTreatmentById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            operationOnTreatment(TREATMENT_OPERS.GET_BY_ID, id, null));
    }

    /**
     * Delete a Treatment
     *
     * @param id String of the Treatment
     * @return ResponseDTO object which contains the deletion status as well as successful message
     */
    @PreAuthorize(SecurityHelper.EL_DELETE_TREATMENTS)
    @DeleteMapping(value = "/treatments/delete/{id}")
    public ResponseDTO<Void> deleteTreatment(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteTreatment with id({})", REST_API_NAME, id);
        try {
            trService.deleteById(CommonUtils.getLongFromString(id, ID_OF_TREATMENT));
        } catch (ResponseStatusException e) {
            ExceptionUtils.logAndRethrowResponseStatusException(String.format(
                "%s:deleteTreatment With id(%s) got ResponseStatusException", REST_API_NAME, id), e);
        }
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_TREATMENT_SUCCESS.getName()),
            null);
    }

    private Treatment operationOnTreatment(TREATMENT_OPERS treatmentOpers,
                                           String id,
                                           Map<String, String> formData) throws ParseException {
        String paramMsg = treatmentOpers == TREATMENT_OPERS.SAVE || treatmentOpers == TREATMENT_OPERS.GET_BY_ID ?
            formData.toString() : String.format("id(%s) and formData %s", id, formData);
        Treatment treatment = null;
        try {
            switch (treatmentOpers) {
                case GET_BY_ID:
                    treatment = trService.getById(CommonUtils.getLongFromString(id, ID_OF_TREATMENT));
                    break;
                case SAVE:
                    treatment = trService.saveTrFromFormData(formData);
                    break;
                case UPDATE:
                    treatment = trService.updateTrFromFormData(id, formData);
                    break;
                case UPDATE_CHEM_ANALYSIS:
                    treatment = trService.updateTrChemAnalysisesFromFormData(id, formData);
                    break;
                case UPDATE_DESIRED_CONCENTRATION:
                    treatment = trService.updateTrDesiredConcentrationsFromFormData(id, formData);
                    break;
                case UPDATE_DISCHARGES:
                    treatment = trService.updateTrDischargesFromFormData(id, formData);
                    break;
                case UPDATE_MIN_LETHAL_CONCENTRATION:
                    treatment = trService.updateTrMinLethalConcentrationsFromFormData(id, formData);
                    break;
                case UPDATE_PRIMARY_APP:
                    treatment = trService.updateTrPrimaryAppsFromFormData(id, formData);
                    break;
                case UPDATE_SECONDARY_APP:
                    treatment = trService.updateTrSecondaryAppsFromFormData(id, formData);
                    break;
                case UPDATE_WATER_CHEMS:
                    treatment = trService.updateTrWaterChemsFromFormData(id, formData);
                    break;
            }
        } catch (ParseException pe) {
            ExceptionUtils.logAndRethrowParseException(
                String.format("%s Got ParseException for %s with %s", REST_API_NAME, treatmentOpers.name(), paramMsg),
                pe);
        } catch (ResponseStatusException re) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s Got ResponseStatusException for %s with %s",
                    REST_API_NAME, treatmentOpers.name(), paramMsg),
                re);
        }
        return treatment;
    }

    private TRSecondaryApplication operationOnTrSingleSecondaryApp(TR_SINGLE_SECONDARY_APP_OPERS opers,
                                                                   String id,
                                                                   Map<String, String> formData) {
        TRSecondaryApplication trSecondaryApplication = null;
        try {
            switch (opers) {
                case UPDATE:
                    trSecondaryApplication = trSecondaryAppService.updateTrSecondaryAppFromFormData(id, formData);
                    break;
                case UPDATE_INDUCED_MORTALITY:
                    trSecondaryApplication = trSecondaryAppService.updateInducedMortalityFromFormData(id, formData);
                    break;
            }
        } catch (ResponseStatusException re) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s Got ResponseStatusException for %s with id(%s) and formData %s",
                    REST_API_NAME, opers.name(), id, formData),
                re);
        }
        return trSecondaryApplication;
    }
}