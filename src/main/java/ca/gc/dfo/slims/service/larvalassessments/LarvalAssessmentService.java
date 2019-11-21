package ca.gc.dfo.slims.service.larvalassessments;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.gc.dfo.slims.service.AbstractService;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.GeoUtmUtils;
import ca.gc.dfo.slims.utility.LocationUtils;
import ca.gc.dfo.slims.utility.SampleUtils;
import ca.gc.dfo.slims.utility.YearUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LAElectrofishingDetails;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LAGranularBayerDetails;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LAPhysicalChemicalData;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LarvalAssessment;
import ca.gc.dfo.slims.domain.repository.larvalassessments.LarvalAssessmentRepository;
import ca.gc.dfo.slims.dto.larvalassessments.LarvalAssessmentDTO;
import ca.gc.dfo.slims.service.common.SpecieService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.service.location.BranchLenticService;
import ca.gc.dfo.slims.service.location.LakeService;
import ca.gc.dfo.slims.service.location.StationService;
import ca.gc.dfo.slims.service.location.StreamService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.validation.larvalassessments.LAValidator;

/**
 * @author ZHUY
 *
 */
@Service
public class LarvalAssessmentService extends AbstractService {
    private static final String SERVICE_NAME = "LarvalAssessmentService";

    private static final String LA_PREFIX = "LA";
    private static final String            EF_SUFFIX    = "-EF";
    private static final String            GB_SUFFIX    = "-GB";
    private static final String            PC_SUFFIX    = "-PC";
    private static final String            CC_SUFFIX    = "-CC";
    private static final String            FOC_SUFFIX    = "-FOC";
    private static final String            INDIS_SUFFIX    = "-INDIS";

    @Autowired
    private LarvalAssessmentRepository    larvalAssessmentRepository;
    @Autowired
    private ResourceLoadService           resourceLoadService;
    @Autowired
    private LakeService                   lakeService;
    @Autowired
    private StreamService                 streamService;
    @Autowired
    private BranchLenticService           branchLenticService;
    @Autowired
    private StationService                stationService;
    @Autowired
    private SpecieService                 specieService;
    @Autowired
    private AppMessages                   messages;

    @Override
    public List<LarvalAssessmentDTO> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        return CommonUtils.getReturnList(larvalAssessmentRepository.findAllList());
    }

    @Override
    public List<LarvalAssessmentDTO> getAll(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        Date toDate = new GregorianCalendar(year+1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(larvalAssessmentRepository.findAllList(fromDate, toDate));
    }

    @Override
    public List<LarvalAssessmentDTO> getAllAfterYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(larvalAssessmentRepository.findAllListAfterYear(fromDate));
    }

    @Override
    public List<LarvalAssessmentDTO> getAllBeforeYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date toDate = new GregorianCalendar(year+1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(larvalAssessmentRepository.findAllListBeforeYear(toDate));
    }

    public List<Integer> getYearList(){
        LarvalAssessment la = larvalAssessmentRepository.findFirstByOrderBySample_sampleDateAsc();
        return YearUtils.getYearList(la == null ?  null : la.getSample().getSampleDate(), SERVICE_NAME);
    }

    public LarvalAssessment getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(larvalAssessmentRepository.findById(id),
            SERVICE_NAME + "getById(LarvalAssessment)", id, messages);
    }

    public LarvalAssessment save(LarvalAssessment la) {
        CommonUtils.getLogger().debug("{}:save with LarvalAssessment({})", SERVICE_NAME, la.getId());
        LarvalAssessment returnLa = null;
        try {
            la.runPrePersist();
            returnLa = larvalAssessmentRepository.save(la);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save LarvalAssessment({}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME, la.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.SAVE_LARVALASSESSMENT_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save LarvalAssessment({}) due to: {}",
                SERVICE_NAME, la.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_LARVALASSESSMENT_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug("{}:save return saved LarvalAssessment({})", SERVICE_NAME, returnLa.getId());
        return returnLa;
    }

    public LarvalAssessment saveLaFromFormData(Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:saveLaFromFormData with formData {}", SERVICE_NAME, formData.toString());
        LarvalAssessment la = buildLaFromFormData(new LarvalAssessment(), formData);
        la = save(la);
        CommonUtils.getLogger().debug(
            "{}:saveLaFromFormData return saved LarvalAssessment({})", SERVICE_NAME, la.getId());
        return la;
    }

    private LarvalAssessment buildLaFromFormData(LarvalAssessment la,
                                                 Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:buildLaFromFormData with LarvalAssessment({}) and formData {}",
            SERVICE_NAME, la.getId(), formData.toString());
        LAValidator.validateLaFormData(formData, messages);

        la.setStartTime(CommonUtils.getStringValue(formData.get("startTime")));
        la.setFinishTime(CommonUtils.getStringValue(formData.get("finishTime")));
        la.setGeoUTM(GeoUtmUtils.getGeoUtmFromFormData(formData, "la_location", resourceLoadService));

        // set survey purpose
        List<RefCode> purposes = new ArrayList<>();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (entry.getKey().contains("purposeCode")) {
                RefCode temp = resourceLoadService.findRefCode(RefCodeType.SURVEY_PURPOSE.getName(), entry.getValue());
                purposes.add(temp);
            }
        }
        la.setLaPurposes(purposes);

        setSurveyMethodology(la, resourceLoadService.findRefCode(
            RefCodeType.SURVEY_METHODOLOGY.getName(), CommonUtils.getStringValue(formData.get("SURVEY_METHODOLOGY"))));

        la.setOperator1(resourceLoadService.findRefCode(
            RefCodeType.OPERATORS.getName(), CommonUtils.getStringValue(formData.get("laOperator1"))));
        la.setOperator2(resourceLoadService.findRefCode(
            RefCodeType.OPERATORS.getName(), CommonUtils.getStringValue(formData.get("laOperator2"))));
        la.setOperator3(resourceLoadService.findRefCode(
            RefCodeType.OPERATORS.getName(), CommonUtils.getStringValue(formData.get("laOperator3"))));

        LocationReference locationReference = LocationUtils.getLocationReferenceFromFormData(
            formData, lakeService, streamService, branchLenticService, stationService);
        la.setLocationReference(locationReference);

        if (StringUtils.isBlank(formData.get("sampleCode"))) {
            Sample sample = SampleUtils.getSampleFromFormData(
                formData, locationReference.getLake(), locationReference.getStream(),
                larvalAssessmentRepository.getNextSequentialNumber(), LA_PREFIX, messages);
            la.setSample(sample);
        }

        CommonUtils.getLogger().debug(
            "{}:buildLaFromFormData return built LarvalAssessment({})", SERVICE_NAME, la.getId());
        return la;
    }

    private void setSurveyMethodology(LarvalAssessment la, RefCode newRefCode) {
        RefCode existingRefCode = la.getSurveyMethodology();
        if (existingRefCode != null) {
            String existingCodePairAbbre = existingRefCode.getCodePair().getAbbreviation();
            if (existingCodePairAbbre.startsWith(LAValidator.METHODOLOGY_START_EF)) {
                if (!newRefCode.getCodePair().getAbbreviation().startsWith(LAValidator.METHODOLOGY_START_EF)) {
                    // methodology changed from Electrofishing, let's clean up existing electrofish record if it exists
                    LAElectrofishingDetails laElectrofishingDetails = la.getLaElectrofishingDetails();
                    if (laElectrofishingDetails != null) {
                        CommonUtils.getLogger().info(
                            "{}:setSurveyMethodology found LarvalAssessment({}) has changed " +
                                "methodology from ({}) to ({}). clean up existing ElectroFishing details.",
                            SERVICE_NAME, la.getId(), existingRefCode.getCodePair().getShowText(),
                            newRefCode.getCodePair().getShowText(), laElectrofishingDetails.getId());
                        la.setLaElectrofishingDetails(null);
                    }
                }
            } else if (existingCodePairAbbre.startsWith(LAValidator.METHODOLOGY_START_GB)) {
                if (!newRefCode.getCodePair().getAbbreviation().startsWith(LAValidator.METHODOLOGY_START_GB)) {
                    // methodology changed from Granular Bayer, let's clean up existing GranualarBayer record if it exists
                    LAGranularBayerDetails laGranularBayerDetails = la.getLaGranularBayerDetails();
                    if (laGranularBayerDetails != null) {
                        CommonUtils.getLogger().info(
                            "{}:setSurveyMethodology found LarvalAssessment({}) has changed " +
                                "methodology from ({}) to ({}). clean up existing Granual Bayer details.",
                            SERVICE_NAME, la.getId(), existingRefCode.getCodePair().getShowText(),
                            newRefCode.getCodePair().getShowText(), laGranularBayerDetails.getId());
                        la.setLaGranularBayerDetails(null);
                    }
                }
            }
        }
        la.setSurveyMethodology(newRefCode);
    }

    public LarvalAssessment updateLaElectroFishingFromFormData(String id,
                                                               Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateLaElectroFishingFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        LarvalAssessment la = buildLaElectroFishingFromFormData(getLaById(id), formData);
        return save(la);
    }

    private LarvalAssessment getLaById(String id) {
        return getById(CommonUtils.getLongFromString(id, "ID of LarvalAssessment"));
    }

    public LarvalAssessment updateLaGranularBayerFromFormData(String id,
                                                              Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateLaGranularBayerFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        LarvalAssessment la = buildLaGranularBayerFromFormData(getLaById(id), formData);
        return save(la);
    }

    public LarvalAssessment updateLaPhysicalChemicalFromFormData(String id,
                                                                 Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateLaPhysicalChemicalFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        LarvalAssessment la = buildLaPhysicalChemicalFromFormData(getLaById(id), formData);
        return save(la);
    }

    public LarvalAssessment updateLaCollectionconFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateLaCollectionconFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        LAValidator.validateLaCollConditionFormData(formData, messages);

        LarvalAssessment la = getLaById(id);
        la.setCollectCondition(CommonUtils.getStringValue(formData.get("optradio_gencon")));
        la.setCollectConditionDetails(CommonUtils.getStringValue(formData.get("condition_detail")));
        la.setEffectiveness(CommonUtils.getStringValue(formData.get("optradio_effectiveness")));
        la.setEffectivenessDetails(CommonUtils.getStringValue(formData.get("effectiveness_detail")));
        return save(la);
    }
    
    public LarvalAssessment updateLaFishobsercolFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateLaFishobsercolFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        LarvalAssessment la = getLaById(id);
        List<Specie> species = getSpeciesFromFormData(la, formData);
        la.setSpecies(species);
        return save(la);
    }
    
    public LarvalAssessment updateLaFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateLaFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        LarvalAssessment la = getLaById(id);
        LarvalAssessment updatedLa = buildLaFromFormData(la, formData);
        return save(updatedLa);
    }

    public List<LarvalAssessment> syncOfflineData(List<Object> objData) throws ParseException {
        CommonUtils.getLogger().debug("{}:syncOfflineData with {} objData", SERVICE_NAME, objData.size());

        Map<String, Map<String, String>> las = new HashMap<>();
        Map<String, Map<String, String>> laEFs = new HashMap<>();
        Map<String, Map<String, String>> laGBs = new HashMap<>();
        Map<String, Map<String, String>> laPCs = new HashMap<>();
        Map<String, Map<String, String>> laCCs = new HashMap<>();
        Map<String, Map<String, String>> laFOCs = new HashMap<>();
        Map<String, Map<String, String>> laIndis = new HashMap<>();

        walkThroughObjData(objData, las, laEFs, laGBs, laPCs, laCCs, laFOCs, laIndis);
        return getLarvalAssessmentList(las, laEFs, laGBs, laPCs, laCCs, laFOCs, laIndis);
    }

    private void walkThroughObjData(List<Object> objData,
                                    Map<String, Map<String, String>> las,
                                    Map<String, Map<String, String>> laEFs,
                                    Map<String, Map<String, String>> laGBs,
                                    Map<String, Map<String, String>> laPCs,
                                    Map<String, Map<String, String>> laCCs,
                                    Map<String, Map<String, String>> laFOCs,
                                    Map<String, Map<String, String>> laIndis) {
        CommonUtils.getLogger().debug("{}:walkThroughObjData walking through {} objData", SERVICE_NAME, objData.size());
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        int index = 0;
        for (Object obj : objData) {
            String jstr = gson.toJson(obj);
            CommonUtils.getLogger().debug(
                "{}:syncOfflineData[()] ************* jstr is: {}", SERVICE_NAME, index++, jstr);
            Map<String, String> objMap = gson.fromJson(jstr, mapType);
            CommonUtils.getLogger().debug(
                "{}:syncOfflineData[()] ************* objMap is: {}", SERVICE_NAME, index, objMap);

            String laOfflineId = objMap.get("laOffLine_Id");
            if (laOfflineId == null) {
                continue;
            }
            if (!laOfflineId.contains(EF_SUFFIX)
                && !laOfflineId.contains(GB_SUFFIX)
                && !laOfflineId.contains(PC_SUFFIX)
                && !laOfflineId.contains(CC_SUFFIX)
                && !laOfflineId.contains(FOC_SUFFIX)
                && !laOfflineId.contains(INDIS_SUFFIX)) {
                las.put(laOfflineId, objMap);
            } else if (laOfflineId.contains(EF_SUFFIX)) {
                laEFs.put(laOfflineId, objMap);
            } else if (laOfflineId.contains(GB_SUFFIX)) {
                laGBs.put(laOfflineId, objMap);
            } else if (laOfflineId.contains(PC_SUFFIX)) {
                laPCs.put(laOfflineId, objMap);
            } else if (laOfflineId.contains(CC_SUFFIX)) {
                laCCs.put(laOfflineId, objMap);
            } else if (laOfflineId.contains(FOC_SUFFIX)) {
                laFOCs.put(laOfflineId, objMap);
            } else if (laOfflineId.contains(INDIS_SUFFIX)) {
                laIndis.put(laOfflineId, objMap);
            }
        }

        CommonUtils.getLogger().debug("{}:walkThroughObjData got {} in map las, {} with suffix({}), " +
                "{} with suffix({}), {} with suffix({}), {} with suffix({}), {} with suffix({}), {} with suffix({})",
            SERVICE_NAME, las.size(), laEFs.size(), EF_SUFFIX, laGBs.size(), GB_SUFFIX, laPCs.size(), PC_SUFFIX,
            laCCs.size(), CC_SUFFIX, laFOCs.size(), FOC_SUFFIX, laIndis.size(), INDIS_SUFFIX);

    }

    private List<LarvalAssessment> getLarvalAssessmentList(Map<String, Map<String, String>> las,
                                                           Map<String, Map<String, String>> laEFs,
                                                           Map<String, Map<String, String>> laGBs,
                                                           Map<String, Map<String, String>> laPCs,
                                                           Map<String, Map<String, String>> laCCs,
                                                           Map<String, Map<String, String>> laFOCs,
                                                           Map<String, Map<String, String>> laIndis)
        throws ParseException {
        CommonUtils.getLogger().debug("{}:getLarvalAssessmentList walking through {} las", SERVICE_NAME, las.size());
        List<LarvalAssessment> syncedLAs = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : las.entrySet()) {
            String laKey = entry.getKey();
            Map<String, String> laFormData = entry.getValue();

            LarvalAssessment la = saveLaFromFormData(laFormData);
            Long laId = la.getId();
            String laIdString = la.getId().toString();

            String key = laKey + EF_SUFFIX;
            if (laEFs.containsKey(key)) {
                la = updateLaElectroFishingFromFormData(laIdString, laEFs.get(key));
            }
            key = laKey + GB_SUFFIX;
            if (laGBs.containsKey(key)) {
                la = updateLaGranularBayerFromFormData(laIdString, laGBs.get(key));
            }
            key = laKey + PC_SUFFIX;
            if (laPCs.containsKey(laKey + PC_SUFFIX)) {
                la = updateLaPhysicalChemicalFromFormData(laIdString, laPCs.get(key));
            }
            key = laKey + CC_SUFFIX;
            if (laCCs.containsKey(laKey + CC_SUFFIX)) {
                la = updateLaCollectionconFromFormData(laIdString, laCCs.get(key));
            }
            key = laKey + FOC_SUFFIX;
            if (laFOCs.containsKey(laKey + FOC_SUFFIX)) {
                la = updateLaFishobsercolFromFormData(laIdString, laFOCs.get(key));
            }

            for(String aKey : laIndis.keySet()) {
                if (aKey.contains(laKey + INDIS_SUFFIX)) {
                    String specieCode = key.substring(aKey.indexOf(INDIS_SUFFIX + "-") + (INDIS_SUFFIX + "-").length());
                    Specie specie = specieService.getBySpeciesCodeAndLarvalAssessmentId(specieCode, laId);
                    specieService.updateLaSpecieFromFormData(specie.getId().toString(), laIndis.get(aKey));
                }
            }

            syncedLAs.add(la);
        }

        CommonUtils.getLogger().debug("{}:getLarvalAssessmentList return {} syncedLAs", SERVICE_NAME, syncedLAs.size());
        return syncedLAs;
    }


    public boolean deleteById(Long id) {
        try {
            larvalAssessmentRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{} deleteById({}) failed due to DataIntegrityViolationException", SERVICE_NAME, id, e);
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_LARVALASSESSMENT_ERROR.getName()), e);
        } catch (ConstraintViolationException e) {
            CommonUtils.getLogger().error(
                "{} deleteById({}) failed due to ConstraintViolationException", SERVICE_NAME, id, e);
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_LARVALASSESSMENT_ERROR.getName()), e);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{} deleteById({}) failed due to {}", SERVICE_NAME, id, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        return true;
    }

    private List<Specie> getSpeciesFromFormData(LarvalAssessment la, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData with LarvalAssessment({} and formData {}",
            SERVICE_NAME, la.getId(), formData.toString());
        LAValidator.validateSpeciesFormdata(formData, messages);

        List<Specie> species = new ArrayList<>();
        String numOfString = formData.get("numOfSpecies");
        if (StringUtils.isBlank(numOfString)) {
            return species;
        }

        CommonUtils.getLogger().debug(
            "{}:getSpeciesFromFormData got numOfSpecies as {}", SERVICE_NAME, numOfString);

        String specieFormName = "fishSpecies";
        String obsrAliveFormName = "obserAlive";
        String obsrDeadFormName = "obserDead";
        String colReleasedFormName = "colReleased";
        String colDeadFormName = "colDead";
        String fishTotalFormName = "fishTotal";
        String specieIdName = "specieId";

        int numOfSpecies = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfSpecies; i++) {
            if (StringUtils.isBlank(formData.get(specieFormName + i))) {
                continue;
            }
            Specie specie = new Specie();
            specie.setSpeciesCode(CommonUtils.getObjectCode(
                CommonUtils.getStringValue(formData.get(specieFormName + i))));
            specie.setObservedAlived(CommonUtils.getIntegerValue(formData.get(obsrAliveFormName + i)));
            specie.setObservedDead(CommonUtils.getIntegerValue(formData.get(obsrDeadFormName + i)));
            specie.setCollectedReleased(CommonUtils.getIntegerValue(formData.get(colReleasedFormName + i)));
            specie.setCollectedDead(CommonUtils.getIntegerValue(formData.get(colDeadFormName + i)));
            specie.setTotal(CommonUtils.getIntegerValue(formData.get(fishTotalFormName + i)));
            specie.setId(CommonUtils.getLongValue(formData.get(specieIdName + i)));

            specie.setLarvalAssessment(la);
            species.add(specie);
        }

        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData return {} species", SERVICE_NAME, species.size());
        return species;
    }

    private LarvalAssessment buildLaElectroFishingFromFormData(LarvalAssessment theLa,
                                                               Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildLaElectroFishingFromFormData with LarvalAssessment({} and formData {}",
            SERVICE_NAME, theLa.getId(), formData.toString());
        LAValidator.validateLaElectroFishingFormData(theLa, formData, messages);

        LAElectrofishingDetails laElectrofish = new LAElectrofishingDetails();
        if (theLa.getLaElectrofishingDetails() != null) {
            laElectrofish.setId(theLa.getLaElectrofishingDetails().getId());
        }

        laElectrofish.setAbpSettingType(CommonUtils.getStringValue(formData.get("optradio_abp")));
        laElectrofish.setAbpPeakVolt(CommonUtils.getDoubleValue(formData.get("peak_vol")));
        laElectrofish.setPulseRateSlow(CommonUtils.getDoubleValue(formData.get("pulse_rate_slow")));
        laElectrofish.setPulseRateFast(CommonUtils.getDoubleValue(formData.get("pulse_rate_fast")));
        laElectrofish.setDutyCycleSlow(CommonUtils.getDoubleValue(formData.get("duty_cycle_slow")));
        laElectrofish.setDutyCycleFast(CommonUtils.getDoubleValue(formData.get("duty_cycle_fast")));
        laElectrofish.setBurstRate(CommonUtils.getDoubleValue(formData.get("burst_rate")));
        laElectrofish.setSurveyDistance(CommonUtils.getDoubleValue(formData.get("tds")));
        laElectrofish.setPercAreaElectrofished(CommonUtils.getDoubleValue(formData.get("pae")));
        laElectrofish.setAreaElectrofished(CommonUtils.getDoubleValue(formData.get("ae")));
        laElectrofish.setAreaElectrofishedSource(CommonUtils.getStringValue(formData.get("optradio_ae")));
        laElectrofish.setTimeElectrofishedSource(CommonUtils.getStringValue(formData.get("optradio_te")));
        laElectrofish.setTimeElectrofished(CommonUtils.getStringValue(formData.get("te")));
        laElectrofish.setLarvalAssessment(theLa);

        theLa.setLaElectrofishingDetails(laElectrofish);

        CommonUtils.getLogger().debug("{}:buildLaElectroFishingFromFormData return built LarvalAssessment({})",
            SERVICE_NAME, theLa.getId());
        return theLa;
    }

    private LarvalAssessment buildLaGranularBayerFromFormData(LarvalAssessment theLa, Map<String, String> formData){
        CommonUtils.getLogger().debug("{}:buildLaGranularBayerFromFormData with LarvalAssessment({} and formData {}",
            SERVICE_NAME, theLa.getId(), formData.toString());
        LAValidator.validateLaGranularBayerFormData(theLa, formData, messages);

        LAGranularBayerDetails laGranularBayer = new LAGranularBayerDetails();
        if (theLa.getLaGranularBayerDetails() != null) {
            laGranularBayer.setId(theLa.getLaGranularBayerDetails().getId());
        }

        laGranularBayer.setPlotLength(CommonUtils.getDoubleValue(formData.get("plot_size_length")));
        laGranularBayer.setPlotWidth(CommonUtils.getDoubleValue(formData.get("plot_size_width")));
        laGranularBayer.setPlotArea(CommonUtils.getDoubleValue(formData.get("area")));
        laGranularBayer.setQuantityUsed(CommonUtils.getDoubleValue(formData.get("quantity_used")));
        laGranularBayer.setTimeToFirstAmmocete(CommonUtils.getDoubleValue(formData.get("tfoa")));
        laGranularBayer.setPersonHours(CommonUtils.getDoubleValue(formData.get("effort_hour")));
        laGranularBayer.setNumBoats(CommonUtils.getIntegerValue(formData.get("effort_boat")));
        laGranularBayer.setLarvalAssessment(theLa);

        theLa.setLaGranularBayerDetails(laGranularBayer);

        CommonUtils.getLogger().debug("{}:buildLaGranularBayerFromFormData return built LarvalAssessment({}",
            SERVICE_NAME, theLa.getId());
        return theLa;
    }

    private LarvalAssessment buildLaPhysicalChemicalFromFormData(LarvalAssessment theLa, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildLaPhysicalChemicalFromFormData with LarvalAssessment({} and formData {}",
            SERVICE_NAME, theLa.getId(), formData.toString());
        LAValidator.validateLaPhysicalChemicalFormData(formData, messages);

        LAPhysicalChemicalData laPhysicalChemicalData = new LAPhysicalChemicalData();
        if (theLa.getLaPhysicalChemicalData() != null) {
            laPhysicalChemicalData.setId(theLa.getLaPhysicalChemicalData().getId());
        }

        laPhysicalChemicalData.setWaterSurfaceTemp(CommonUtils.getDoubleValue(formData.get("surface_temp")));
        laPhysicalChemicalData.setWaterBottomTemp(CommonUtils.getDoubleValue(formData.get("bottom_temp")));
        laPhysicalChemicalData.setConductivity(CommonUtils.getIntegerValue(formData.get("conductivity")));
        laPhysicalChemicalData.setConductivityAt(CommonUtils.getDoubleValue(formData.get("conductivity_temp")));
        laPhysicalChemicalData.setPhValue(CommonUtils.getDoubleValue(formData.get("conductivity_ph")));
        laPhysicalChemicalData.setMeanDepth(CommonUtils.getDoubleValue(formData.get("mean_depth")));
        laPhysicalChemicalData.setMeanStreamWidth(CommonUtils.getDoubleValue(formData.get("mean_stream_width")));
        laPhysicalChemicalData.setDischarge(CommonUtils.getDoubleValue(formData.get("discharge")));
        laPhysicalChemicalData.setMethod(resourceLoadService.findRefCode(
            RefCodeType.DISCHARGE_CODE.getName(), CommonUtils.getStringValue(formData.get("DISCHARGE_CODE"))));
        laPhysicalChemicalData.setTurbidity(CommonUtils.getStringValue(formData.get("optradio_turbidity")));
        laPhysicalChemicalData.setLarvalAssessment(theLa);

        theLa.setLaPhysicalChemicalData(laPhysicalChemicalData);

        CommonUtils.getLogger().debug("{}:buildLaPhysicalChemicalFromFormData return built LarvalAssessment({}",
            SERVICE_NAME, theLa.getId());
        return theLa;
    }
}
