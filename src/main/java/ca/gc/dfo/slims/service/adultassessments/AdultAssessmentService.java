package ca.gc.dfo.slims.service.adultassessments;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.adultassessments.AAWeekOfCapture;
import ca.gc.dfo.slims.domain.entity.adultassessments.AdultAssessment;
import ca.gc.dfo.slims.domain.entity.common.FishIndividual;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import ca.gc.dfo.slims.domain.repository.adultassessments.AdultAssessmentRepository;
import ca.gc.dfo.slims.dto.adultassessment.AdultAssessmentDTO;
import ca.gc.dfo.slims.service.AbstractService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.service.location.BranchLenticService;
import ca.gc.dfo.slims.service.location.LakeService;
import ca.gc.dfo.slims.service.location.StationService;
import ca.gc.dfo.slims.service.location.StreamService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.SampleUtils;
import ca.gc.dfo.slims.utility.YearUtils;
import ca.gc.dfo.slims.validation.adultassessments.AAValidator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class AdultAssessmentService extends AbstractService {
    private static final String SERVICE_NAME = "AdultAssessmentService";

    private static final String AA_PREFIX = "AA";
    private static final String ID_OF_ADULT_ASSESSMENT = "ID of AdultAssessment";
    private static final String KEY_ADULTS_CAPTURED = "adultsCaptured";
    private static final String KEY_NUM_OF_WEEK_CAPTURES = "numOfWeekCaptures";
    private static final String KEY_TAG_WEEK = "tagWeek";

    @Autowired
    private AdultAssessmentRepository    adultAssessmentRepository;
    @Autowired
    private ResourceLoadService          resourceLoadService;
    @Autowired
    private LakeService                  lakeService;
    @Autowired
    private StreamService                streamService;
    @Autowired
    private BranchLenticService          branchLenticService;
    @Autowired
    private StationService               stationService;
    @Autowired
    private AppMessages                   messages;

    @Override
    public List<AdultAssessmentDTO> getAll() {
        return CommonUtils.getReturnList(adultAssessmentRepository.findAllList());
    }

    @Override
    public List<AdultAssessmentDTO> getAll(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(adultAssessmentRepository.findAllList(fromDate, toDate));
    }

    @Override
    public List<AdultAssessmentDTO> getAllAfterYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(adultAssessmentRepository.findAllListAfterYear(fromDate));
    }

    @Override
    public List<AdultAssessmentDTO> getAllBeforeYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(adultAssessmentRepository.findAllListBeforeYear(toDate));
    }

    public List<Integer> getYearList() {
        AdultAssessment aa = adultAssessmentRepository.findFirstByOrderByInspectDateAsc();
        return YearUtils.getYearList(aa == null ? null : aa.getInspectDate(), SERVICE_NAME);
    }

    public AdultAssessment getById(Long id) {
        return CommonUtils.getIfPresent(adultAssessmentRepository.findById(id),
            SERVICE_NAME + "getById(AdultAssessment)", id, messages);
    }

    public AdultAssessment save(AdultAssessment aa) {
        CommonUtils.getLogger().debug("{}:save with AdultAssessment({})", SERVICE_NAME, aa.getId());
        AdultAssessment returnAa = null;
        try {
            returnAa = adultAssessmentRepository.save(aa);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save AdultAssessment({}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME, aa.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.SAVE_ADULTASSESSMENT_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save AdultAssessment({}) due to: {}",
                SERVICE_NAME, aa.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_ADULTASSESSMENT_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug("{}:save return saved AdultAssessment({})", SERVICE_NAME, returnAa.getId());
        return returnAa;
    }

    public AdultAssessment saveAaFromFormData(Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:saveAaFromFormData with formData {}", SERVICE_NAME, formData.toString());
        AdultAssessment aa = buildAaFromFormData(null, formData);
        return save(aa);
    }

    public AdultAssessment updateAaDetailFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateAaDetailFromFormData for id({}) with formData {}",
            SERVICE_NAME, id, formData.toString());
        AdultAssessment aa = getById(CommonUtils.getLongFromString(id, ID_OF_ADULT_ASSESSMENT));
        AdultAssessment newAa = buildAaDetailFromFormData(aa, formData);
        return save(newAa);
    }
    
    public AdultAssessment updateAaSpeciesFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateAaSpeciesFromFormData for id({}) with formData {}",
            SERVICE_NAME, id, formData.toString());
        AdultAssessment aa = getById(CommonUtils.getLongFromString(id, ID_OF_ADULT_ASSESSMENT));
        List<Specie> species = getSpeciesFromFormData(aa, formData);
        aa.setSpecies(species);
        return save(aa);
    }

    public AdultAssessment updateAaFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateAaFromFormData for id({}) with formData {}",
            SERVICE_NAME, id, formData.toString());
        AdultAssessment aa = getById(CommonUtils.getLongFromString(id, ID_OF_ADULT_ASSESSMENT));
        AdultAssessment updatedAa = buildAaFromFormData(aa, formData);
        return save(updatedAa);
    }

    private AdultAssessment buildAaFromFormData(AdultAssessment theAa,
                                                Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:buildAaFromFormData for AdultAssessment({}) with formData {}",
            SERVICE_NAME, theAa == null ? "null" : theAa.getId(), formData.toString());

        AAValidator.validateAaFormData(formData, messages);
        AdultAssessment aa = theAa == null ? new AdultAssessment() : theAa;

        CommonUtils.getLogger().debug("{}:buildAaFromFormData prepare LocationReference", SERVICE_NAME);
        LocationReference locationReference = new LocationReference();

        if (!StringUtils.isBlank(formData.get("lakeId"))) {
            Long lakeId = Long.valueOf(formData.get("lakeId"));
            locationReference.setLake(lakeService.getById(lakeId));
        }
        if (!StringUtils.isBlank(formData.get("streamId"))) {
            Long streamId = Long.valueOf(formData.get("streamId"));
            locationReference.setStream(streamService.getById(streamId));
        }
        if (!StringUtils.isBlank(formData.get("branchId"))) {
            Long branchId = Long.valueOf(formData.get("branchId"));
            locationReference.setBranchLentic(branchLenticService.getById(branchId));
        }
        if (!StringUtils.isBlank(formData.get("stationFromId"))) {
            Long stationFromId = Long.valueOf(formData.get("stationFromId"));
            locationReference.setStationFrom(stationService.getById(stationFromId));
        }

        locationReference.setStationFromAdjust(CommonUtils.getStringValue(formData.get("stationFromAdjust")));

        aa.setLocationReference(locationReference);
        aa.setInspectDate(CommonUtils.getDateValue(formData.get("inspectDate")));
        aa.setTimeToCheck(CommonUtils.getStringValue(formData.get("timeOfCheck")));
        aa.setLocation(CommonUtils.getStringValue(formData.get("aa_location")));

        if (StringUtils.isBlank(formData.get("sampleCode"))) {
            Sample sample = SampleUtils.getSampleFromFormData(
                formData, locationReference.getLake(), locationReference.getStream(),
                adultAssessmentRepository.getNextSequentialNumber(), AA_PREFIX, messages);

            CommonUtils.getLogger().debug("{}:buildAaFromFormData add Sample", SERVICE_NAME);
            aa.setSample(sample);
        }
        CommonUtils.getLogger().debug(
            "{}:buildAaFromFormData return updated AdultAssessment({}) object", SERVICE_NAME, aa.getId());
        return aa;
    }
    
    private AdultAssessment buildAaDetailFromFormData(AdultAssessment theAa,
                                                      Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildAaDetailFromFormData for AdultAssessment({}) with formData {}",
            SERVICE_NAME, theAa.getId(), formData.toString());

        AAValidator.validateDetailFormData(formData, messages);

        theAa.setDeviceMethod(resourceLoadService.findRefCode(RefCodeType.METHOD_CODE.getName(),
            CommonUtils.getStringValue(formData.get("METHOD_CODE"))));
        theAa.setTrapNumber(CommonUtils.getIntegerValue(formData.get("trap_number")));
        theAa.setOpcodeInitialOrReplaced(resourceLoadService.findRefCode(RefCodeType.OPERATING_CODE.getName(),
            CommonUtils.getStringValue(formData.get("opcode_init"))));
        theAa.setAdditionalOpcode(resourceLoadService.findRefCode(RefCodeType.OPERATING_CODE.getName(),
            CommonUtils.getStringValue(formData.get("opcode_additional"))));
        theAa.setOpcodeOnLeaving(resourceLoadService.findRefCode(RefCodeType.OPERATING_CODE.getName(),
            CommonUtils.getStringValue(formData.get("opcode_leaving"))));
        theAa.setRemarks(CommonUtils.getStringValue(formData.get("aa_remarks")));
        theAa.setAirTemp(CommonUtils.getDoubleValue(formData.get("air_temp")));
        theAa.setRecaptured(CommonUtils.getIntegerValue(formData.get("recaptured")));
        theAa.setMarked(CommonUtils.getIntegerValue(formData.get("marked")));
        theAa.setWeekOfTagging(CommonUtils.getIntegerValue(formData.get("week_of_tagging")));
        theAa.setDevice(resourceLoadService.findRefCode(RefCodeType.TEMP_DEVICE.getName(),
            CommonUtils.getStringValue(formData.get("TEMP_DEVICE"))));
        theAa.setWaterTemp(CommonUtils.getDoubleValue(formData.get("water_temp")));
        theAa.setWaterTempMax(CommonUtils.getDoubleValue(formData.get("max_temp")));
        theAa.setWaterTempMin(CommonUtils.getDoubleValue(formData.get("min_temp")));
        theAa.setTurbidity(CommonUtils.getStringValue(formData.get("optradio_turbidity")));
        theAa.setGaugeUsed(resourceLoadService.findRefCode(RefCodeType.GAUGE_TYPE.getName(),
            CommonUtils.getStringValue(formData.get("GAUGE_TYPE"))));
        theAa.setUpStream(CommonUtils.getDoubleValue(formData.get("upstream")));
        theAa.setDownStream(CommonUtils.getDoubleValue(formData.get("downstream")));
        theAa.setIfOther(CommonUtils.getStringValue(formData.get("ifother")));

        List<AAWeekOfCapture> aaWeekOfCaptures = getAAWeekOfCapturesFromFormData(formData);
        theAa.setAaWeekOfCaptures(aaWeekOfCaptures);

        return theAa;
    }
    
    private List<AAWeekOfCapture> getAAWeekOfCapturesFromFormData(Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getAAWeekOfCapturesFromFormData with formData {}",
            SERVICE_NAME, formData.toString());
        AAValidator.validateWeekOfCaptureFormdata(formData, messages);

        List<AAWeekOfCapture> aaWeekOfCaptures = new ArrayList<>();

        String value = formData.get(KEY_NUM_OF_WEEK_CAPTURES);
        if (!StringUtils.isBlank(value)) {
            int numOfWeekCaptures = CommonUtils.getIntValue(value);
            for (int i = 0; i < numOfWeekCaptures; i++) {
                AAWeekOfCapture aaWC = new AAWeekOfCapture();
                value = formData.get(KEY_TAG_WEEK + i);
                if (!StringUtils.isBlank(value)) {
                    aaWC.setTaggingWeek(CommonUtils.getIntegerValue(value));
                }
                value = formData.get(KEY_ADULTS_CAPTURED + i);
                if (!StringUtils.isBlank(value)) {
                    aaWC.setAdultCaptured(CommonUtils.getIntegerValue(value));
                }
                aaWeekOfCaptures.add(aaWC);
            }
        }

        CommonUtils.getLogger().debug("{}:getAAWeekOfCapturesFromFormData Got ({}) weeks captures",
            SERVICE_NAME, aaWeekOfCaptures.size());
        return aaWeekOfCaptures;
    }
    
    private List<Specie> getSpeciesFromFormData(AdultAssessment aa, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData for AdultAssessment ({}) with formData {}",
            SERVICE_NAME, aa.getId(), formData.toString());
        AAValidator.validateSpeciesFormdata(formData, messages);

        String fishSpecieName = "fishSpecies";
        String trapchamberName = "trapChamber";
        String malesName = "males";
        String femalesName = "females";
        String aliveName = "alive";
        String deadName = "dead";
        String totalName = "total";
        String specieIdName = "specieId";

        String specieName = "specie";
        String numOfindividualsName = "_numOfIndi";

        String sexName = "fishSex";
        String indiLengthName = "indiLen";
        String indiWeightName = "indiWeight";
        String spawningConName = "spawningCon";
        String specimenName = "specimen";
        String recaptureName = "recapture";
        String individualIdName = "individualId";

        List<Specie> species = new ArrayList<>();

        int numOfSpecies = CommonUtils.getIntValue(formData.get("numOfSpecies"));

        for (int i = 0; i < numOfSpecies; i++) {
            if (!StringUtils.isBlank(formData.get(fishSpecieName + i))) {
                Specie specie = new Specie();
                specie.setSpeciesCode(CommonUtils.getObjectCode(formData.get(fishSpecieName + i)));
                specie.setTrapChamber(resourceLoadService.findRefCode(RefCodeType.TRAP_CHAMBER.getName(),
                    CommonUtils.getStringValue(formData.get(trapchamberName + i))));
                specie.setFirstTimeCaptureMales(CommonUtils.getIntegerValue(formData.get(malesName + i)));
                specie.setFirstTimeCaptureFemales(CommonUtils.getIntegerValue(formData.get(femalesName + i)));
                specie.setFirstTimeCaptureAlive(CommonUtils.getIntegerValue(formData.get(aliveName + i)));
                specie.setFirstTimeCaptureDead(CommonUtils.getIntegerValue(formData.get(deadName + i)));
                specie.setFirstTimeCaptureTotal(CommonUtils.getIntegerValue(formData.get(totalName + i)));
                
                Long specieId = CommonUtils.getLongValue(formData.get(specieIdName + i));
                if (specieId != null) {
                    specie.setId(specieId);
                }

                List<FishIndividual> fishIndividuals = new ArrayList<>();
                int numOfIndividuals = CommonUtils.getIntValue(formData.get(specieName + i + numOfindividualsName));

                for (int j = 0; j < numOfIndividuals; j++)
                {
                    FishIndividual fishIndividual = new FishIndividual();
                    fishIndividual.setIndividualSex(resourceLoadService.findRefCode(RefCodeType.FISH_SEX.getName(),
                        CommonUtils.getStringValue(formData.get(totalName + i + "_" + sexName + j))));
                    fishIndividual.setIndividualLength(
                        CommonUtils.getDoubleValue(formData.get(totalName + i + "_" + indiLengthName + j)));
                    fishIndividual.setIndividualWeight(
                        CommonUtils.getDoubleValue(formData.get(totalName + i + "_" + indiWeightName + j)));
                    fishIndividual.setSpawningCondition(resourceLoadService.findRefCode(RefCodeType.MATURITY.getName(),
                        CommonUtils.getStringValue(formData.get(totalName + i + "_" + spawningConName + j))));
                    fishIndividual.setSpecimenState(
                        resourceLoadService.findRefCode(RefCodeType.SPECIMEN_STATE.getName(),
                            CommonUtils.getStringValue(formData.get(totalName + i + "_" + specimenName + j))));
                    if (formData.get(totalName + i + "_" + recaptureName + j).equalsIgnoreCase("yes")) {
                        fishIndividual.setRecapture(true);
                    } else {
                        fishIndividual.setRecapture(false);
                    }
                    Long indiId = CommonUtils.getLongValue(formData.get(totalName + i + "_" + individualIdName + j));
                    if (indiId != null) {
                        fishIndividual.setId(indiId);
                    }
                    fishIndividual.setSpecie(specie);
                    fishIndividuals.add(fishIndividual);
                }
                specie.setAdultAssessment(aa);
                specie.setFishIndividuals(fishIndividuals);

                species.add(specie);

            }
        }

        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData got ({}) species", SERVICE_NAME, species.size());
        return species;
    }

    public boolean deleteById(Long id) {
        CommonUtils.getLogger().debug("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            adultAssessmentRepository.deleteById(id);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            CommonUtils.getLogger().error("{}:deleteById could not delete AdultAssessment({}) due to: {}",
                SERVICE_NAME, id, ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_ADULTASSESSMENT_ERROR.getName()), ex);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById could not delete AdultAssessment({}) due to: {}",
                SERVICE_NAME, id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug("{}:deleteById completed successfully for id({})", SERVICE_NAME, id);
        return true;
    }
}
