package ca.gc.dfo.slims.api.adultassessments;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.adultassessments.AdultAssessment;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.dto.adultassessment.AdultAssessmentDTO;
import ca.gc.dfo.slims.service.adultassessments.AdultAssessmentService;
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
public class AARestController {
    private static final String REST_API_NAME = "AARestController";

    private enum OPER {
        SAVE,
        UPDATE,
        UPDATE_DETAILS,
        UPDATE_SPECIES
    }

    @Autowired
    private AdultAssessmentService    aaService;
    @Autowired
    private AppMessages                messages;

    /**
     * Get all the AdultAssessmentDTO objects which matches the specified year based on the specified year operation
     *
     * @param yearOp String specifies the operation on the year, supported values are "equal", "gte" and "lte"
     * @param year String of the comparison year, when value is "All" returns all the FishModule objects.
     * @return List of AdultAssessmentDTO which are matching the specified condition.
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_ADULT_SPAWNING)
    @GetMapping(value = "/aas")
    public List<AdultAssessmentDTO> getAllAdultAssessments(@RequestParam String yearOp, @RequestParam String year) {
        return CommonUtils.getAllObjectByYear(
            yearOp, year, aaService, REST_API_NAME + "getAllAdultAssessments");
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_ADULT_SPAWNING)
    @PostMapping(value = "/aas/add")
    public ResponseDTO<AdultAssessment> saveAa(@RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:saveAa with {}", REST_API_NAME, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_ADULTASSESSMENT_SUCCESS.getName()),
            operateOnAdultAssessment(OPER.SAVE, null, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_ADULT_SPAWNING)
    @PutMapping(value = "/aas/{id}")
    public ResponseDTO<AdultAssessment> updateAa(@PathVariable(value = "id") String id,
                                                 @RequestParam Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().info("{}:updateAa with id({}) and formData {}", REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_ADULTASSESSMENT_SUCCESS.getName()),
            operateOnAdultAssessment(OPER.UPDATE, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_ADULT_SPAWNING)
    @PutMapping(value = "/aas/detail/{id}")
    public ResponseDTO<AdultAssessment> updateAaDetail(@PathVariable(value = "id") String id,
                                                       @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateAaDetail with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_ADULTASSESSMENT_SUCCESS.getName()),
            operateOnAdultAssessment(OPER.UPDATE_DETAILS, id, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_ADULT_SPAWNING)
    @PutMapping(value = "/aas/species/{id}")
    public ResponseDTO<AdultAssessment> updateAaSpecies(@PathVariable(value = "id") String id,
                                                        @RequestParam Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().info("{}:updateAaSpecies with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_ADULTASSESSMENT_SUCCESS.getName()),
            operateOnAdultAssessment(OPER.UPDATE_SPECIES, id, formData));
    }

    /**
     * Get AdultAssessment by ID
     *
     * @param id String of the AdultAssessment
     * @return ResponseDTO object which contains the get status as well as the matching AdultAssessment object
     */
    @PreAuthorize(SecurityHelper.EL_VIEW_ADULT_SPAWNING)
    @GetMapping(value = "/aas/{id}")
    public ResponseDTO<AdultAssessment> getAdultAssessmentById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getAdultAssessmentById with id ({})", REST_API_NAME, id);

        AdultAssessment aa = null;
        try {
            aa = aaService.getById(CommonUtils.getLongFromString(id, "ID of getAdultAssessmentById"));
        } catch (ResponseStatusException e) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s:getAdultAssessmentById Got ResponseStatusException for request with id(%s)",
                    REST_API_NAME, id), e);
        }

        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS, null, aa);
    }

    /**
     * Delete an AdultAssessment
     *
     * @param id String of the AdultAssessment
     * @return ResponseDTO object which contains the deletion status as well as successful message
     */
    @PreAuthorize(SecurityHelper.EL_DELETE_ADULT_SPAWNING)
    @DeleteMapping(value = "/aas/delete/{id}")
    public ResponseDTO<Void> deleteAdultAssessment(@PathVariable String id) {
        CommonUtils.getLogger().info("{}:deleteAdultAssessment with id ({})", REST_API_NAME, id);

        try {
            aaService.deleteById(CommonUtils.getLongFromString(id, "ID of deleteAdultAssessment"));
        } catch (ResponseStatusException e) {
            ExceptionUtils.logAndRethrowResponseStatusException(
                String.format("%s:deleteAdultAssessment Got ResponseStatusException for request with id(%s)",
                    REST_API_NAME, id), e);
        }

        return new ResponseDTO<>(CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.DELETE_ADULTASSESSMENT_SUCCESS.getName()), null);
    }

    private AdultAssessment operateOnAdultAssessment(OPER oper,
                                                     String id,
                                                     Map<String, String> formData) throws ParseException {
        AdultAssessment adultAssessment = null;
        String paramMsg = oper == OPER.SAVE ?
            formData.toString() :  String.format("id(%s) and formData %s", id, formData);
        try {
            switch (oper) {
                case SAVE:
                    adultAssessment = aaService.saveAaFromFormData(formData);
                    break;
                case UPDATE:
                    adultAssessment = aaService.updateAaFromFormData(id, formData);
                    break;
                case UPDATE_DETAILS:
                    adultAssessment = aaService.updateAaDetailFromFormData(id, formData);
                    break;
                case UPDATE_SPECIES:
                    adultAssessment = aaService.updateAaSpeciesFromFormData(id, formData);
                    break;
            }
        } catch (ParseException pe) {
            ExceptionUtils.logAndRethrowParseException(
                String.format("%s Got ParseException for %s with %s", REST_API_NAME, oper.name(), paramMsg), pe);
        } catch (ResponseStatusException re) {
            ExceptionUtils.logAndRethrowResponseStatusException(String.format(
                "%s Got ResponseStatusException for %s with %s", REST_API_NAME, oper.name(), paramMsg), re);
        }
        return adultAssessment;
    }
}