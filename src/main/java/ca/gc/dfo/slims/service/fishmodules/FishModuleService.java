package ca.gc.dfo.slims.service.fishmodules;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.location.LocationDetails;
import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import ca.gc.dfo.slims.domain.entity.fishmodule.FMHabitat;
import ca.gc.dfo.slims.domain.entity.fishmodule.FishModule;
import ca.gc.dfo.slims.domain.repository.fishmodule.FishModuleRepository;
import ca.gc.dfo.slims.dto.fishmodules.FishModuleDTO;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.service.location.BranchLenticService;
import ca.gc.dfo.slims.service.location.LakeService;
import ca.gc.dfo.slims.service.location.StationService;
import ca.gc.dfo.slims.service.location.StreamService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.GeoUtmUtils;
import ca.gc.dfo.slims.utility.LocationUtils;
import ca.gc.dfo.slims.utility.SampleUtils;
import ca.gc.dfo.slims.utility.YearUtils;
import ca.gc.dfo.slims.validation.fishmodules.FMValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class FishModuleService extends BaseFishModuleService {
    private static final String SERVICE_NAME = "FishModuleService";
    private static final String FM_PREFIX        = "FM";
    private static final String FM_OFFLINE_ID    = "fmOffLine_Id";
    private static final String RUNNET_SUFFIX    = "-RUNNET";
    private static final String HABITATS_SUFFIX  = "-HABITATS";

    @Autowired
    private FishModuleRepository    fmRepository;
    @Autowired
    private FMRunnetService            fmRunnetService;
    @Autowired
    private ResourceLoadService        resourceLoadService;
    @Autowired
    private LakeService                lakeService;
    @Autowired
    private StreamService            streamService;
    @Autowired
    private BranchLenticService        branchLenticService;
    @Autowired
    private StationService            stationService;
    @Autowired
    private AppMessages                messages;

    public List<FishModuleDTO> getAll() {
        return CommonUtils.getReturnList(fmRepository.findAllList());
    }

    public List<FishModuleDTO> getAll(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(fmRepository.findAllList(fromDate, toDate));
    }

    public List<FishModuleDTO> getAllAfterYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(fmRepository.findAllListAfterYear(fromDate));
    }

    public List<FishModuleDTO> getAllBeforeYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(fmRepository.findAllListBeforeYear(toDate));
    }

    public List<Integer> getYearList() {
        FishModule fm = fmRepository.findFirstByOrderBySample_sampleDateAsc();
        return YearUtils.getYearList(fm == null ?  null : fm.getSample().getSampleDate(), SERVICE_NAME);
    }

    public FishModule getById(Long id) {
        return CommonUtils.getIfPresent(
            fmRepository.findById(id), SERVICE_NAME + "getById(FishModule)", id, messages);
    }

    public FishModule save(FishModule fm) {
        CommonUtils.getLogger().debug("{}:save with FishModule({})", SERVICE_NAME, fm.getId());
        FishModule returnFm = null;
        try {
            returnFm = fmRepository.save(fm);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save FishModule({}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME, fm.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.SAVE_FISHMODULE_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save FishModule({}) due to: {}",
                SERVICE_NAME, fm.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_FISHMODULE_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug("{}:save return saved FishModule({})", SERVICE_NAME, returnFm.getId());
        return returnFm;
    }

    public FishModule saveFmFromFormData(Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:saveFmFromFormData with formData {}", SERVICE_NAME, formData.toString());
        FishModule fm = buildFmFromFormData(new FishModule(), formData);
        fm = save(fm);
        CommonUtils.getLogger().debug("{}:saveFmFromFormData return saved FishModule({})", SERVICE_NAME, fm.getId());
        return fm;
    }

    public List<FishModule> syncOfflineData(List<Object> objData) throws ParseException {
        CommonUtils.getLogger().debug("{}:syncOfflineData with {} Objects", SERVICE_NAME, objData.size());
        List<FishModule> syncedFMs = new ArrayList<>();

        Map<String, Map<String, String>> fms = new HashMap<>();
        Map<String, Map<String, String>> fmRunnets = new HashMap<>();
        Map<String, Map<String, String>> fmHabitats = new HashMap<>();

        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {
            // do nothing
        }.getType();

        CommonUtils.getLogger().debug(
            "{}:syncOfflineData sort Objects to maps of fms, fmRunnet and fmHabitats", SERVICE_NAME);
        for (Object obj : objData) {
            String jstr = gson.toJson(obj);
            Map<String, String> objMap = gson.fromJson(jstr, mapType);
            CommonUtils.getLogger().debug(
                "{}:syncOfflineData got map from object as {}", SERVICE_NAME, objMap.toString());

            String fmOfflineId = objMap.get(FM_OFFLINE_ID);
            if (fmOfflineId == null) {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData skipped as value of {} is null", SERVICE_NAME, FM_OFFLINE_ID);
                continue;
            }
            if (!fmOfflineId.contains(RUNNET_SUFFIX)
                && !fmOfflineId.contains(HABITATS_SUFFIX)) {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData added {}({}) to map fms", SERVICE_NAME, FM_OFFLINE_ID, fmOfflineId);
                fms.put(fmOfflineId, objMap);
            } else if (fmOfflineId.contains(RUNNET_SUFFIX)) {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData added {}({}) to map fmRunnets", SERVICE_NAME, FM_OFFLINE_ID, fmOfflineId);
                fmRunnets.put(fmOfflineId, objMap);
            } else if (fmOfflineId.contains(HABITATS_SUFFIX)) {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData added {}({}) to map fmHabitats", SERVICE_NAME, FM_OFFLINE_ID, fmOfflineId);
                fmHabitats.put(fmOfflineId, objMap);
            }
        }

        CommonUtils.getLogger().debug("{}:syncOfflineData walk through map fms (size:{})", SERVICE_NAME, fms.size());
        for (Map.Entry<String, Map<String, String>> entry : fms.entrySet()) {
            String fmKey = entry.getKey();
            Map<String, String> fmFormData = entry.getValue();

            FishModule fm = saveFmFromFormData(fmFormData);
            String fmIdString = fm.getId().toString();

            CommonUtils.getLogger().debug(
                "{}:syncOfflineData walk through map fmRunnets for FishModule({})", SERVICE_NAME, fmIdString);
            for (Map.Entry<String, Map<String, String>> runnetEntry : fmRunnets.entrySet()) {
                if (runnetEntry.getKey().contains(fmKey + RUNNET_SUFFIX)) {
                    CommonUtils.getLogger().debug(
                        "{}:syncOfflineData save FmRunnet for FishModule({})", SERVICE_NAME, fmIdString);
                    fmRunnetService.saveRunnetFromFormData(fmIdString, runnetEntry.getValue());
                }
            }

            if (fmHabitats.containsKey(fmKey + HABITATS_SUFFIX)) {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData save FmHabitat for FishModule({})", SERVICE_NAME, fmIdString);
                fm = updateFmHabitatsFromFormData(fmIdString, fmHabitats.get(fmKey + HABITATS_SUFFIX));
            }

            syncedFMs.add(fm);
        }

        CommonUtils.getLogger().debug(
            "{}:syncOfflineData return syncedFMs list with {} entries", SERVICE_NAME, syncedFMs.size());
        return syncedFMs;
    }

    public FishModule updateFmFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug(
            "{}:updateFmFromFormData with id({}) and formData {}", SERVICE_NAME, id, formData.toString());
        FishModule fm = getById(CommonUtils.getLongFromString(id, "ID of FishModule"));
        FishModule updatedFm = buildFmFromFormData(fm, formData);
        return save(updatedFm);
    }

    public FishModule updateFmHabitatsFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug(
            "{}:updateFmHabitatsFromFormData with id({}) and formData {}", SERVICE_NAME, id, formData.toString());
        FishModule fm = getById(CommonUtils.getLongFromString(id, "ID of FishModule"));
        List<FMHabitat> fmHabitats = getFmHabitatsFromFormData(fm, formData);
        fm.setFmHabitats(fmHabitats);
        return save(fm);
        
    }

    private FishModule buildFmFromFormData(FishModule fm, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:buildFmFromFormData with FishModule({}) and formData {}",
            SERVICE_NAME, fm.getId(), formData.toString());
        FMValidator.validateFmFormData(formData, messages);

        LocationReference locationReference = LocationUtils.getLocationReferenceFromFormData(
            formData, lakeService, streamService, branchLenticService, stationService);
        fm.setLocationReference(locationReference);

        fm.setGeoUTM(GeoUtmUtils.getGeoUtmFromFormData(formData, "fm_location", resourceLoadService));

        LocationDetails locDetails = new LocationDetails();
        locDetails.setContainment(CommonUtils.getIntegerValue(formData.get("containment")));
        locDetails.setConductivity(CommonUtils.getIntegerValue(formData.get("conductivity")));
        locDetails.setTemperature(CommonUtils.getDoubleValue(formData.get("temperature")));
        locDetails.setMeanDepth(CommonUtils.getDoubleValue(formData.get("meanDepth")));
        locDetails.setMeanWidth(CommonUtils.getDoubleValue(formData.get("meanWidth")));
        locDetails.setMaxDepth(CommonUtils.getDoubleValue(formData.get("maxDepth")));
        locDetails.setMeasuredArea(CommonUtils.getDoubleValue(formData.get("measuredArea")));
        locDetails.setEstimatedArea(CommonUtils.getDoubleValue(formData.get("estimatedArea")));
        locDetails.setDistanceSurveyed(CommonUtils.getDoubleValue(formData.get("distanceSurvey")));

        fm.setLocationDetails(locDetails);

        fm.setStartTime(CommonUtils.getStringValue(formData.get("startTime")));
        fm.setFinishTime(CommonUtils.getStringValue(formData.get("finishTime")));
        fm.setEffectiveness(CommonUtils.getStringValue(formData.get("optradio_effectiveness")));

        fm.setFmPurpose(resourceLoadService.findRefCode(RefCodeType.FM_SURVEY_PURPOSE.getName(),
            CommonUtils.getStringValue(formData.get("surveyPurpose"))));
        fm.setTechnique(resourceLoadService.findRefCode(RefCodeType.FM_TECHNIQUE.getName(),
            CommonUtils.getStringValue(formData.get("technique"))));
        fm.setMethodology(resourceLoadService.findRefCode(RefCodeType.FM_METHODOLOGY.getName(),
            CommonUtils.getStringValue(formData.get("methodology"))));

        fm.setOperator1(resourceLoadService.findRefCode(RefCodeType.OPERATORS.getName(),
            CommonUtils.getStringValue(formData.get("fmOperator1"))));
        fm.setOperator2(resourceLoadService.findRefCode(RefCodeType.OPERATORS.getName(),
            CommonUtils.getStringValue(formData.get("fmOperator2"))));
        fm.setOperator3(resourceLoadService.findRefCode(RefCodeType.OPERATORS.getName(),
            CommonUtils.getStringValue(formData.get("fmOperator3"))));

        if (StringUtils.isBlank(formData.get("sampleCode"))) {
            Sample sample = SampleUtils.getSampleFromFormData(
                formData, locationReference.getLake(), locationReference.getStream(),
                fmRepository.getNextSequentialNumber(), FM_PREFIX, messages);
            fm.setSample(sample);
        } else {
            Date newSampleDate = CommonUtils.getDateValue(formData.get("sampleDate"));
            if (fm.getSample().getSampleDate() != newSampleDate) {
                fm.getSample().setSampleDate(newSampleDate);
            }
        }

        CommonUtils.getLogger().debug("{}:buildFmFromFormData return built FishModule({})", SERVICE_NAME, fm.getId());
        return fm;
    }

    private List<FMHabitat> getFmHabitatsFromFormData(FishModule fm, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getFmHabitatsFromFormData for FishModule({}) with formData {}",
            SERVICE_NAME, fm.getId(), formData.toString());
        FMValidator.validateFmHabitatsFormdata(formData, messages);
        List<FMHabitat> fmHabitats = new ArrayList<>();
        String numOfString = formData.get("numOfHabitats");
        if (StringUtils.isBlank(numOfString)) {
            return fmHabitats;
        }

        CommonUtils.getLogger().debug(
            "{}:getFmHabitatsFromFormData got numOfHabitats as {}", SERVICE_NAME, numOfString);
        String transectIdName = "transectId";
        String widthName = "width";
        String depthName = "depth";
        String distanceName = "distance";
        String habitatIdName = "habitatId";

        String locationName = "location_";
        String bedrockName = "bedrock_";
        String hardpanName = "hardpanClay_";
        String rubbleName = "rubble_";
        String gravelName = "gravel_";
        String sandName = "sand_";
        String siltDetritusName = "siltDetritus_";
        String sedimentsName = "claySediments_";
        String poolsName = "pools_";
        String rifflesName = "riffles_";
        String eddyName = "eddyLagoon_";
        String runName = "run_";
        String overhangName = "bankOverhang_";
        String vegetationName = "vegetation_";
        String woodyName = "woodyDebris_";
        String algaeName = "algae_";
        String grassesName = "grasses_";
        String treesName = "trees_";

        int numOfHabitats = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfHabitats; i++) {
            if (StringUtils.isBlank(formData.get(transectIdName + i))) {
                continue;
            }

            FMHabitat fmHabitat = new FMHabitat();
            fmHabitat.setWidth(CommonUtils.getDoubleValue(formData.get(widthName + i)));
            fmHabitat.setDepth(CommonUtils.getDoubleValue(formData.get(depthName + i)));
            fmHabitat.setDistanceFromLastTransect(CommonUtils.getDoubleValue(formData.get(distanceName + i)));
            fmHabitat.setId(CommonUtils.getLongValue(formData.get(habitatIdName + i)));

            fmHabitat.setLocation(CommonUtils.getStringValue(formData.get(locationName + i)));
            fmHabitat.setBedrock(CommonUtils.getIntegerValue(formData.get(bedrockName + i)));
            fmHabitat.setHardpanClay(CommonUtils.getIntegerValue(formData.get(hardpanName + i)));
            fmHabitat.setRubble(CommonUtils.getIntegerValue(formData.get(rubbleName + i)));
            fmHabitat.setGravel(CommonUtils.getIntegerValue(formData.get(gravelName + i)));
            fmHabitat.setSand(CommonUtils.getIntegerValue(formData.get(sandName + i)));
            fmHabitat.setSiltDetritus(CommonUtils.getIntegerValue(formData.get(siltDetritusName + i)));
            fmHabitat.setClaySediments(CommonUtils.getIntegerValue(formData.get(sedimentsName + i)));
            fmHabitat.setPools(CommonUtils.getIntegerValue(formData.get(poolsName + i)));
            fmHabitat.setRiffles(CommonUtils.getIntegerValue(formData.get(rifflesName + i)));
            fmHabitat.setEddyLagoon(CommonUtils.getIntegerValue(formData.get(eddyName + i)));
            fmHabitat.setTheRun(CommonUtils.getIntegerValue(formData.get(runName + i)));
            fmHabitat.setBankOverhang(CommonUtils.getIntegerValue(formData.get(overhangName + i)));
            fmHabitat.setVegetation(CommonUtils.getIntegerValue(formData.get(vegetationName + i)));
            fmHabitat.setWoodyDebris(CommonUtils.getIntegerValue(formData.get(woodyName + i)));
            fmHabitat.setAlgae(CommonUtils.getIntegerValue(formData.get(algaeName + i)));
            fmHabitat.setShorelineGrasses(CommonUtils.getIntegerValue(formData.get(grassesName + i)));
            fmHabitat.setShorelineTressShrubs(CommonUtils.getIntegerValue(formData.get(treesName + i)));

            fmHabitat.setFishModule(fm);
            fmHabitats.add(fmHabitat);
        }
        CommonUtils.getLogger().debug(
            "{}:getFmHabitatsFromFormData return ({}) FMHabitat", SERVICE_NAME, fmHabitats.size());
        return fmHabitats;
    }

    public boolean deleteById(Long id) {
        return super.deleteById(fmRepository, id, SERVICE_NAME, messages);
    }
}
