package ca.gc.dfo.slims.service.habitatinventory;

import ca.gc.dfo.slims.constants.PageConstants;
import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import ca.gc.dfo.slims.domain.entity.habitat.HATransect;
import ca.gc.dfo.slims.domain.entity.habitat.HATransectDetail;
import ca.gc.dfo.slims.domain.entity.habitat.HabitatInventory;
import ca.gc.dfo.slims.domain.repository.habitatinventory.HabitatInventoryRepository;
import ca.gc.dfo.slims.dto.habitat.HabitatInventoryDTO;
import ca.gc.dfo.slims.service.AbstractService;
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
import ca.gc.dfo.slims.validation.habitatinventory.HIValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
@Slf4j
@Service
public class HabitatInventoryService extends AbstractService {
    private static final String SERVICE_NAME = "HabitatInventoryService";

    private static final String HI_PREFIX = "HI";
    private static final String HI_OFFLINE_ID    = "hiOffLine_Id";
    private static final String TRANSECT_SUFFIX    = "-TRANSECT";

    @Autowired
    private HabitatInventoryRepository   habitatInventoryRepository;
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
    private AppMessages                  messages;

    @Override
    public List<HabitatInventoryDTO> getAll() {
        return CommonUtils.getReturnList(habitatInventoryRepository.findAllList());
    }

    @Override
    public List<HabitatInventoryDTO> getAll(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        Date toDate = new GregorianCalendar(year+1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(habitatInventoryRepository.findAllList(fromDate, toDate));
    }

    @Override
    public List<HabitatInventoryDTO> getAllAfterYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(habitatInventoryRepository.findAllListAfterYear(fromDate));
    }

    @Override
    public List<HabitatInventoryDTO> getAllBeforeYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date toDate = new GregorianCalendar(year+1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(habitatInventoryRepository.findAllListBeforeYear(toDate));
    }

    public List<Integer> getYearList() {
        HabitatInventory ha = habitatInventoryRepository.findFirstByOrderByInventoryDateAsc();
        return YearUtils.getYearList(ha == null ?  null : ha.getSample().getSampleDate(), SERVICE_NAME);
    }

    public HabitatInventory getById(Long id) {
        return CommonUtils.getIfPresent(habitatInventoryRepository.findById(id),
            SERVICE_NAME + "getById(HabitatInventory)", id, messages);
    }

    public HabitatInventory save(HabitatInventory hi) {
        CommonUtils.getLogger().debug("{}:save with HabitatInventory({})", SERVICE_NAME, hi.getId());
        HabitatInventory returnHi = null;
        try {
            returnHi = habitatInventoryRepository.save(hi);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save HabitatInventory({}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME, hi.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.SAVE_HABITATINVENTORY_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save HabitatInventory({}) due to: {}",
                SERVICE_NAME, hi.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_HABITATINVENTORY_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug("{}:save return saved HabitatInventory({})", SERVICE_NAME, returnHi.getId());
        return returnHi;
    }

    public HabitatInventory saveHiFromFormData(Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:saveHiFromFormData with formData {}", SERVICE_NAME, formData.toString());
        HabitatInventory hi = buildHiFromFormData(null, formData);
        return save(hi);
    }

    private HabitatInventory buildHiFromFormData(HabitatInventory theHi,
                                                 Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:buildHiFromFormData for HabitatInventory({}) with formData {}",
            SERVICE_NAME, theHi == null ? "null" : theHi.getId(), formData.toString());

        HIValidator.validateHiFormData(formData, messages);
        HabitatInventory hi = theHi == null ? new HabitatInventory(): theHi;

        hi.setTransectId(CommonUtils.getStringValue(formData.get("transectId")));
        hi.setGeoUTM(
            GeoUtmUtils.getGeoUtmWithTwoUtmFromFormData(formData, "hi_location", resourceLoadService));
        hi.setOperationUnit(resourceLoadService.findRefCode(
            RefCodeType.OPERATING_UNIT.getName(), CommonUtils.getStringValue(formData.get("OPERATING_UNIT"))));
        hi.setHabitatMeasurements(resourceLoadService.findRefCode(
            RefCodeType.HI_MEASUREMENTS.getName(), CommonUtils.getStringValue(formData.get("HI_MEASUREMENTS"))));

        hi.setOperator1(resourceLoadService.findRefCode(
            RefCodeType.OPERATORS.getName(), CommonUtils.getStringValue(formData.get("hiOperator1"))));
        hi.setOperator2(resourceLoadService.findRefCode(
            RefCodeType.OPERATORS.getName(), CommonUtils.getStringValue(formData.get("hiOperator2"))));
        hi.setOperator3(resourceLoadService.findRefCode(
            RefCodeType.OPERATORS.getName(), CommonUtils.getStringValue(formData.get("hiOperator3"))));

        LocationReference locationReference = LocationUtils.getLocationReferenceFromFormData(
            formData, lakeService, streamService, branchLenticService, stationService);
        hi.setLocationReference(locationReference); // set location

        if (StringUtils.isBlank(formData.get("sampleCode"))) {
            hi.setInventoryDate(CommonUtils.getDateValue(formData.get("inventoryDate")));
            hi.setSample(SampleUtils.getSampleFromFormData(
                formData, locationReference.getLake(), locationReference.getStream(),
                habitatInventoryRepository.getNextSequentialNumber(), HI_PREFIX, messages));
        }

        CommonUtils.getLogger().debug(
            "{}:buildHiFromFormData return updated HabitatInventory({}) object", SERVICE_NAME, hi.getId());
        return hi;
    }

    public HabitatInventory updateHiTransectFromFormData(String id,
                                                         Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateHiTransectFromFormData for id({}) with formData {}",
            SERVICE_NAME, id, formData.toString());
        HabitatInventory hi = buildHiTransectFromFormData(getHabitatInventoryById(id), formData);
        return save(hi);
    }

    public HabitatInventory updateHiFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateHiFromFormData for id({}) with formData {}",
            SERVICE_NAME, id, formData.toString());
        HabitatInventory updatedHi = buildHiFromFormData(getHabitatInventoryById(id), formData);
        return save(updatedHi);
    }

    private HabitatInventory getHabitatInventoryById(String id) {
        return getById(CommonUtils.getLongFromString(id, "ID of HabitatInventory"));
    }

    public List<HabitatInventory> syncOfflineData(List<Object> objData) throws ParseException {
        CommonUtils.getLogger().debug("{}:syncOfflineData with {} objects", SERVICE_NAME, objData.size());

        List<HabitatInventory> syncedHIs = new ArrayList<>();
        
        Map<String, Map<String, String>> his = new HashMap<>();
        Map<String, Map<String, String>> hiTransects = new HashMap<>();
        
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {
            // do nothing
        }.getType();

        CommonUtils.getLogger().debug(
            "{}:syncOfflineData sort Objects to maps of his and hiTransects", SERVICE_NAME);
        for (Object obj : objData) {
            String jstr = gson.toJson(obj);
            Map<String, String> objMap = gson.fromJson(jstr, mapType);
            CommonUtils.getLogger().debug(
                "{}:syncOfflineData got map from object as {}", SERVICE_NAME, objMap.toString());

            String hiOfflineId = objMap.get(HI_OFFLINE_ID);
            if (hiOfflineId == null) {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData skipped as value of {} is null", SERVICE_NAME, HI_OFFLINE_ID);
                continue;
            }
            if (hiOfflineId.contains(TRANSECT_SUFFIX)) {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData added {}({}) to map hiTransects", SERVICE_NAME, HI_OFFLINE_ID, hiOfflineId);
                hiTransects.put(hiOfflineId, objMap);
            } else {
                CommonUtils.getLogger().debug(
                    "{}:syncOfflineData added {}({}) to map his", SERVICE_NAME, HI_OFFLINE_ID, hiOfflineId);
                his.put(hiOfflineId, objMap);
            }
        }

        CommonUtils.getLogger().debug("{}:syncOfflineData walk through map his (size:{})", SERVICE_NAME, his.size());
        for (Map.Entry<String, Map<String, String>> entry : his.entrySet()) {
            String fmKey = entry.getKey();
            Map<String, String> fmFormData = entry.getValue();

            HabitatInventory hi = saveHiFromFormData(fmFormData);
            if (hiTransects.containsKey(fmKey + TRANSECT_SUFFIX)) {
                hi = updateHiTransectFromFormData(hi.getId().toString(), hiTransects.get(fmKey + TRANSECT_SUFFIX));
            }

            syncedHIs.add(hi);
        }

        CommonUtils.getLogger().debug(
            "{}:syncOfflineData return syncedHIs list with {} entries", SERVICE_NAME, syncedHIs.size());
        return syncedHIs;
    }
    
    public boolean deleteById(Long id) {
        CommonUtils.getLogger().debug("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            habitatInventoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            CommonUtils.getLogger().error("{}:deleteById failed to delete HabitatInventory({}) due to " +
                "DataIntegrityViolationException or ConstraintViolationException ({})",
                SERVICE_NAME, id, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_HABITATINVENTORY_ERROR.getName()), e);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById failed to delete HabitatInventory({}) due to {}",
                SERVICE_NAME, id, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }

        return true;
    }

    private HabitatInventory buildHiTransectFromFormData(HabitatInventory hi, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildHiTransectFromFormData with HabitatInventory({}) and formData {}",
            SERVICE_NAME, hi.getId(), formData.toString());

        HIValidator.validateHiTransectFormData(formData, messages);
        
        HATransect haTransect = new HATransect();
        if (hi.getHaTransect() != null) {
            haTransect.setId(hi.getHaTransect().getId());
        }

        haTransect.setStreamWidth(CommonUtils.getDoubleValue(formData.get("stream_width")));
        haTransect.setTransectSpacing(CommonUtils.getDoubleValue(formData.get("transect_spacing")));
        haTransect.setTotalReachLength(CommonUtils.getDoubleValue(formData.get("total_reachlen")));
        haTransect.setEstDischarge(CommonUtils.getDoubleValue(formData.get("est_discharge")));
        haTransect.setStreamCondition(CommonUtils.getStringValue(formData.get("stream_conditions")));

        List<HATransectDetail> haTransDetails = getTransectDetailsFromFormData(formData);
        haTransect.setHaTransectDetails(haTransDetails);

        haTransect.setBedrock(CommonUtils.getIntegerValue(formData.get("hi_bedrock")));
        haTransect.setHardpanClay(CommonUtils.getIntegerValue(formData.get("hardpan_clay")));
        haTransect.setClaySediments(CommonUtils.getIntegerValue(formData.get("clay_sediments")));
        haTransect.setGravel(CommonUtils.getIntegerValue(formData.get("hi_gravel")));
        haTransect.setRubble(CommonUtils.getIntegerValue(formData.get("hi_rubble")));
        haTransect.setSand(CommonUtils.getIntegerValue(formData.get("hi_sand")));
        haTransect.setSilt(CommonUtils.getIntegerValue(formData.get("hi_silt")));
        haTransect.setSiltDetritus(CommonUtils.getIntegerValue(formData.get("silt_detritus")));
        haTransect.setDetritus(CommonUtils.getIntegerValue(formData.get("hi_detritus")));
        haTransect.setCumulativeSpawning(CommonUtils.getDoubleValue(formData.get("cumulative_spawning")));
        haTransect.setHabitatInventory(hi);
        hi.setHaTransect(haTransect);

        CommonUtils.getLogger().debug("{}:buildHiTransectFromFormData return built HabitatInventory({})",
            SERVICE_NAME, hi.getId());
        return hi;
    }

    private List<HATransectDetail> getTransectDetailsFromFormData(Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getTransectDetailsFromFormData with formData {}",
            SERVICE_NAME, formData.toString());

        List<HATransectDetail> haTransDetails = new ArrayList<>();
        String numOfString = formData.get("numOfTransects");
        if (StringUtils.isBlank(numOfString)) {
            return haTransDetails;
        }

        CommonUtils.getLogger().debug(
            "{}:getTransectDetailsFromFormData got numOfTransects as {}", SERVICE_NAME, numOfString);

        String disFromLeftName = "disFromLeftBank";
        String depthName = "depth";
        String hiTypeName = "hitype";

        int numOfTransects = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfTransects; i++) {
            HATransectDetail hiTD = new HATransectDetail();
            String hiTypeValue = formData.get(hiTypeName + i);
            if (!StringUtils.isBlank(hiTypeValue) && !hiTypeValue.equalsIgnoreCase(PageConstants.DEFAULT_SELECT)) {
                hiTD.setHabitatType(CommonUtils.getStringValue(hiTypeValue));
                hiTD.setDistanceFromLeftBank(CommonUtils.getDoubleValue(formData.get(disFromLeftName + i)));
                hiTD.setDepth(CommonUtils.getDoubleValue(formData.get(depthName + i)));

                haTransDetails.add(hiTD);
            }
        }

        CommonUtils.getLogger().debug(
            "{}:getTransectDetailsFromFormData return ({}) HATransectDetail", SERVICE_NAME, haTransDetails.size());
        return haTransDetails;
    }
}
