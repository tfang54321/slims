package ca.gc.dfo.slims.service.treatments;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.treatments.TRChemicalAnalysis;
import ca.gc.dfo.slims.domain.entity.treatments.TRDesiredConcentration;
import ca.gc.dfo.slims.domain.entity.treatments.TRDischarge;
import ca.gc.dfo.slims.domain.entity.treatments.TRLogistics;
import ca.gc.dfo.slims.domain.entity.treatments.TRMinLethalConcentration;
import ca.gc.dfo.slims.domain.entity.treatments.TRPrimaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondAppInducedMortality;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.TRWaterChemistry;
import ca.gc.dfo.slims.domain.entity.treatments.Treatment;
import ca.gc.dfo.slims.domain.repository.common.SpecieCodeRepository;
import ca.gc.dfo.slims.domain.repository.treatments.TreatmentRepository;
import ca.gc.dfo.slims.dto.treatments.TreatmentDTO;
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
import ca.gc.dfo.slims.validation.treatments.TreatmentValidator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class TreatmentService extends AbstractService {
    private static final String SERVICE_NAME = "TreatmentService";
    private static final String TR_PREFIX = "TR";
    // KEYs of formData
    private static final String KEY_APP_ID = "appId";
    private static final String KEY_BRANCH_ID = "branchId";
    private static final String KEY_NUM_OF_APPS = "numOfApps";
    private static final String KEY_PRIMARY_APP_ID = "primaryAppId";
    private static final String KEY_STATION_FROM_ID = "stationFromId";
    private static final String KEY_STATION_FROM_ADJUST = "stationFromAdjust";
    private static final String KEY_TR_BRANCH = "trBranch";
    private static final String KEY_TR_STATION_FROM = "trStationFrom";
    private static final String KEY_TR_TREAT_DATE = "trTreatDate";
    private static final String KEY_TR_TIME_START = "trTimeStart";
    private static final String KEY_SECOND_APP_ID = "secondAppId";

    @Autowired
    private TreatmentRepository       treatmentRepository;
    @Autowired
    private TRPrimaryAppService       trPrimaryAppService;
    @Autowired
    private TRSecondaryAppService     trSecondaryAppService;
    @Autowired
    private ResourceLoadService       resourceLoadService;
    @Autowired
    private LakeService               lakeService;
    @Autowired
    private StreamService             streamService;
    @Autowired
    private BranchLenticService       branchLenticService;
    @Autowired
    private StationService            stationService;
    @Autowired
    private SpecieCodeRepository      scRepository;
    @Autowired
    private AppMessages               messages;

    @Override
    public List<TreatmentDTO> getAll() {
        return CommonUtils.getReturnList(treatmentRepository.findAllList());
    }

    @Override
    public List<TreatmentDTO> getAll(String inputYear) {
        Integer year = CommonUtils.getIntFromString(inputYear, "year");
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();

        return CommonUtils.getReturnList(treatmentRepository.findAllList(fromDate, toDate));
    }

    @Override
    public List<TreatmentDTO> getAllAfterYear(String inputYear) {
        Integer year = CommonUtils.getIntFromString(inputYear, "year");
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        
        return CommonUtils.getReturnList(treatmentRepository.findAllListAfterYear(fromDate));
    }

    @Override
    public List<TreatmentDTO> getAllBeforeYear(String inputYear) {
        Integer year = CommonUtils.getIntFromString(inputYear, "year");
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(treatmentRepository.findAllListBeforeYear(toDate));
    }

    public List<Integer> getYearList() {
        Treatment tr = treatmentRepository.findFirstByOrderByTrLogistics_treatmentStartAsc();
        return YearUtils.getYearList(tr == null ? null : tr.getTrLogistics().getTreatmentStart(), SERVICE_NAME);
    }

    public Treatment getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with Treatment id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(treatmentRepository.findById(id),
            SERVICE_NAME + "getById(Treatment)", id, messages);
    }

    private Treatment save(Treatment tr) {
        CommonUtils.getLogger().debug("{}:save treatment({})", SERVICE_NAME, tr.getId());
        Treatment savedTreatment = null;
        try {
            savedTreatment = treatmentRepository.save(tr);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error("{}:save Can't save: Treatment ({}) due to DataIntegrityViolationException",
                SERVICE_NAME, tr.getId(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.SAVE_DUPLICATE_TREATMENT_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save Can't save: Treatment ({}) due to Exception {}",
                SERVICE_NAME, tr.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_TREATMENT_ERROR.getName()), ex);
        }
        return savedTreatment;
    }

    public Treatment saveTrFromFormData(Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:saveTrFromFormData with formData {}", SERVICE_NAME, formData.toString());
        Treatment tr = buildTrFromFormData(new Treatment(), formData);
        tr = save(tr);
        return tr;
    }

    // build Treatment from form (treatment main page - Logistics)
    private Treatment buildTrFromFormData(Treatment tr, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:buildTrFromFormData for Treatment({}) with formData {}",
            SERVICE_NAME, tr.getId(), formData.toString());
        TreatmentValidator.validateTrFormData(tr, formData, messages);

        String key = TreatmentValidator.KEY_LAKE_ID;
        Long lakeId = CommonUtils.getLongFromString(formData.get(key), key);
        tr.setLake(lakeService.getById(lakeId));

        key = TreatmentValidator.KEY_STREAM_ID;
        Long streamId = CommonUtils.getLongFromString(formData.get(key), key);
        tr.setStream(streamService.getById(streamId));

        TRLogistics trLogistics = new TRLogistics();
        trLogistics.setTreatmentStart(CommonUtils.getDateValue(formData.get("treatmentStart")));
        trLogistics.setTreatmentEnd(CommonUtils.getDateValue(formData.get("treatmentEnd")));
        trLogistics.setMethodology(resourceLoadService.findRefCode(
            RefCodeType.TREATMENT_METHODOLOGY.getName(),
            CommonUtils.getStringValue(formData.get("TREATMENT_METHODOLOGY"))));
        trLogistics.setTotalDischarge(CommonUtils.getDoubleValue(formData.get("total_discharge")));
        trLogistics.setKilometerTreated(CommonUtils.getDoubleValue(formData.get("kilo_treated")));
        trLogistics.setCalendarDays(CommonUtils.getIntegerValue(formData.get("calendar_days")));
        trLogistics.setAbundanceIndex(resourceLoadService.findRefCode(
            RefCodeType.SL_ABUNDANCE_INDEX.getName(), CommonUtils.getStringValue(formData.get("SL_ABUNDANCE_INDEX"))));
        trLogistics.setRemarks(CommonUtils.getStringValue(formData.get("tr_remarks")));
        trLogistics.setMaxCrewSize(CommonUtils.getIntegerValue(formData.get("max_crew")));
        trLogistics.setPersonDays(CommonUtils.getDoubleValue(formData.get("person_days")));

        if (StringUtils.isBlank(formData.get("sampleCode"))) {
            Sample sample = SampleUtils.getSampleFromFormData(formData, tr.getLake(), tr.getStream(),
                treatmentRepository.getNextSequentialNumber(), TR_PREFIX, messages);
            tr.setSample(sample);
        }

        if (tr.getTrLogistics() != null) {
            trLogistics.setId(tr.getTrLogistics().getId());
        }

        tr.setTrLogistics(trLogistics);
        trLogistics.setTreatment(tr);

        return tr;
    }

    public Treatment updateTrFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateTrFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        Treatment updatedTr = buildTrFromFormData(tr, formData);
        return save(updatedTr);
    }

    private Treatment getTreatmentById(String id) {
        return getById(CommonUtils.getLongFromString(id, "ID of Treatment"));
    }

    // update primary apps
    public Treatment updateTrPrimaryAppsFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateTrPrimaryAppsFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        List<TRPrimaryApplication> primaryApps = getPrimaryAppsFromFormData(tr, formData);
        tr.setTrPrimaryApplications(primaryApps);
        return save(tr);
    }

    // update secondary apps
    public Treatment updateTrSecondaryAppsFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateTrSecondaryAppsFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        List<TRSecondaryApplication> secondApps = getSecondaryAppsFromFormData(tr, formData);
        tr.setTrSecondaryApplications(secondApps);
        return save(tr);
    }
    
    // update desired concentrations
    public Treatment updateTrDesiredConcentrationsFromFormData(String id,
                                                               Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateTrDesiredConcentrationsFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        List<TRDesiredConcentration> desiredCons = getDesiredConcentrationsFromFormdata(tr, formData);
        tr.setDesiredConcentrations(desiredCons);
        return save(tr);
    }

    // update minimum lethal concentrations
    public Treatment updateTrMinLethalConcentrationsFromFormData(String id,
                                                                 Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateTrMinLethalConcentrationsFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        List<TRMinLethalConcentration> minLethalCons = getMinLethalConcentrationsFromFormdata(tr, formData);
        tr.setTrMinLethalConcentrations(minLethalCons);
        return save(tr);
    }

    // update water chemistry
    public Treatment updateTrWaterChemsFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateTrWaterChemsFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        List<TRWaterChemistry> waterChems = getWaterChemsFromFormdata(tr, formData);
        tr.setTrWaterChemistries(waterChems);
        return save(tr);
    }

    // update discharge
    public Treatment updateTrDischargesFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateTrDischargesFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        List<TRDischarge> discharges = getDischargesFromFormdata(tr, formData);
        tr.setTrDischarges(discharges);
        return save(tr);
    }
    
    // update discharge
    public Treatment updateTrChemAnalysisesFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateTrChemAnalysisesFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        Treatment tr = getTreatmentById(id);
        List<TRChemicalAnalysis> chemanalysises = getChemAnalysisesFromFormdata(tr, formData);
        tr.setTrChemicalAnalysises(chemanalysises);
        return save(tr);
    }
    
    private List<TRPrimaryApplication> getPrimaryAppsFromFormData(Treatment tr, Map<String, String> formData) {
        Long trId = tr.getId();
        CommonUtils.getLogger().debug("{}:getPrimaryAppsFromFormData for Treatment({}) with formData {}",
            SERVICE_NAME, trId, formData.toString());

        TreatmentValidator.validatePrimAppsFormdata(formData, messages);

        List<TRPrimaryApplication> primaryApps = new ArrayList<>();
        if (StringUtils.isBlank(formData.get(KEY_NUM_OF_APPS))) {
            CommonUtils.getLogger().debug(
                "{}:getPrimaryAppsFromFormData got empty {}, return empty LIST", SERVICE_NAME, KEY_NUM_OF_APPS);
            return primaryApps;
        }

        int numOfApps = CommonUtils.getIntFromMap(formData, KEY_NUM_OF_APPS, SERVICE_NAME);
        TRPrimaryApplication primApp;
        for (int i = 0; i < numOfApps; i++) {
            if (!StringUtils.isBlank(formData.get(KEY_TR_BRANCH + i))
                && !StringUtils.isBlank(formData.get(KEY_TR_STATION_FROM + i))) {
                Long primAppId = CommonUtils.getLongValue(formData.get(KEY_PRIMARY_APP_ID + i));
                primApp = primAppId == null ? new TRPrimaryApplication() : trPrimaryAppService.getById(primAppId);

                Long appId = CommonUtils.getLongValue(formData.get(KEY_APP_ID + i));
                primApp.setApplicationId(appId);
                Long brId = CommonUtils.getLongValue(formData.get(KEY_BRANCH_ID + i));
                primApp.setBranchLentic(branchLenticService.getById(brId));
                Long sfId = CommonUtils.getLongValue(formData.get(KEY_STATION_FROM_ID + i));
                primApp.setStationFrom(stationService.getById(sfId));
                String sfAdjust = formData.get(KEY_STATION_FROM_ADJUST + i);
                primApp.setStationFromAdjust(sfAdjust);
                primApp.setTreatment(tr);

                primaryApps.add(primApp);
            }
        }

        CommonUtils.getLogger().debug(
            "{}:getPrimaryAppsFromFormData for Treatment({}) returning ({}) TRPrimaryApplications",
            SERVICE_NAME, primaryApps.size());
        return primaryApps;
    }

    private List<TRSecondaryApplication> getSecondaryAppsFromFormData(Treatment tr,
                                                                      Map<String, String> formData)
        throws ParseException {
        CommonUtils.getLogger().debug("{}:getSecondaryAppsFromFormData for Treatment({}) with formData {}",
            SERVICE_NAME, tr.getId(), formData.toString());
        TreatmentValidator.validateSecondaryAppsFormdata(tr, formData, messages);

        List<TRSecondaryApplication> secondaryApps = new ArrayList<>();
        if (StringUtils.isBlank(formData.get(KEY_NUM_OF_APPS))) {
            CommonUtils.getLogger().debug(
                "{}:getSecondaryAppsFromFormData got empty {}, return empty LIST", SERVICE_NAME, KEY_NUM_OF_APPS);
            return secondaryApps;
        }

        int numOfApps = CommonUtils.getIntFromMap(formData, KEY_NUM_OF_APPS, SERVICE_NAME);
        TRSecondaryApplication secondApp;
        for (int i = 0; i < numOfApps; i++) {
            if (!StringUtils.isBlank(formData.get(KEY_TR_BRANCH + i))
                && !StringUtils.isBlank(formData.get(KEY_TR_STATION_FROM + i))) {
                Long secondaryAppId = CommonUtils.getLongValue(formData.get(KEY_SECOND_APP_ID + i));
                secondApp = secondaryAppId == null ?
                    new TRSecondaryApplication() : trSecondaryAppService.getById(secondaryAppId);

                Long appId = CommonUtils.getLongValue(formData.get(KEY_APP_ID + i));
                secondApp.setApplicationId(appId);
                Long brId = CommonUtils.getLongValue(formData.get(KEY_BRANCH_ID + i));
                secondApp.setBranchLenticFrom(branchLenticService.getById(brId));
                Long sfId = CommonUtils.getLongValue(formData.get(KEY_STATION_FROM_ID + i));
                secondApp.setStationFrom(stationService.getById(sfId));
                secondApp.setStationFromAdjust(formData.get(KEY_STATION_FROM_ADJUST + i));
                secondApp.setTreatmentDate(CommonUtils.getDateValue(formData.get(KEY_TR_TREAT_DATE + i)));
                secondApp.setTimeStart(formData.get(KEY_TR_TIME_START + i));
                secondApp.setTreatment(tr);

                secondaryApps.add(secondApp);
            }
        }
        CommonUtils.getLogger().debug(
            "{}:getSecondaryAppsFromFormData for Treatment({}) returning ({}) TRSecondaryApplications",
            SERVICE_NAME, secondaryApps.size());
        return secondaryApps;
    }

    private List<TRDesiredConcentration> getDesiredConcentrationsFromFormdata(Treatment tr,
                                                                              Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getDesiredConcentrationsFromFormdata for Treatment({}) with formData {}",
            SERVICE_NAME, tr.getId(), formData.toString());
        TreatmentValidator.validateDesiredConcentrationsFormdata(formData, messages);
        
        String stationName = "trStation";
        String stationId = "stationId";
        String stationAdjustName = "stationAdjust";
        
        String minName = "appConMin";
        String maxName = "appConMax";
        String nicloPercName = "nicloPerc";
        
        String desiredConIdName = "desiredConId";
        
        List<TRDesiredConcentration> desiredCons = new ArrayList<>();
        
        if (!StringUtils.isBlank(formData.get("numOfCons"))) {
            int numOfCons = CommonUtils.getIntFromMap(formData, "numOfCons", SERVICE_NAME);

            for (int i = 0; i < numOfCons; i++) {
                TRDesiredConcentration desiredCon = new TRDesiredConcentration();

                if (!StringUtils.isBlank(formData.get(KEY_TR_BRANCH + i))
                    && !StringUtils.isBlank(formData.get(stationName + i))) {
                    Long brId = Long.valueOf(formData.get(KEY_BRANCH_ID + i));
                    Long sId = Long.valueOf(formData.get(stationId + i));

                    desiredCon.setBranchLentic(branchLenticService.getById(brId));
                    desiredCon.setStation(stationService.getById(sId));
                    desiredCon.setStationAdjust(formData.get(stationAdjustName + i));

                    desiredCon.setAppConcentrationMin(CommonUtils.getDoubleValue(formData.get(minName + i)));
                    desiredCon.setAppConcentrationMax(CommonUtils.getDoubleValue(formData.get(maxName + i)));
                    desiredCon.setNiclosamide(CommonUtils.getDoubleValue(formData.get(nicloPercName + i)));
                    desiredCon.setId(CommonUtils.getLongValue(formData.get(desiredConIdName + i)));

                    desiredCon.setTreatment(tr);

                    desiredCons.add(desiredCon);
                }
            }
        }
        return desiredCons;
    }

    private List<TRMinLethalConcentration> getMinLethalConcentrationsFromFormdata(Treatment tr,
                                                                                  Map<String, String> formData)  {
        CommonUtils.getLogger().debug("{}:getMinLethalConcentrationsFromFormdata for Treatment({}) with formData {}",
            SERVICE_NAME, tr.getId(), formData.toString());
        TreatmentValidator.validateMinLethalConcentrationsFormdata(formData, messages);

        String stationName = "trStation";
        String stationId = "stationId";
        String stationAdjustName = "stationAdjust";

        String mlcName = "mlc";
        String nicloPercName = "nicloPerc";
        String exposureName = "exposure";
        String minConIdName = "minlethalConId";

        List<TRMinLethalConcentration> minLethalCons = new ArrayList<>();

        if (!StringUtils.isBlank(formData.get("numOfCons"))) {
            int numOfCons = CommonUtils.getIntFromMap(formData, "numOfCons", SERVICE_NAME);

            for (int i = 0; i < numOfCons; i++) {
                TRMinLethalConcentration con = new TRMinLethalConcentration();

                if (!StringUtils.isBlank(formData.get(KEY_TR_BRANCH + i))
                    && !StringUtils.isBlank(formData.get(stationName + i))) {
                    Long brId = Long.valueOf(formData.get(KEY_BRANCH_ID + i));
                    Long sId = Long.valueOf(formData.get(stationId + i));

                    con.setBranchLentic(branchLenticService.getById(brId));
                    con.setStation(stationService.getById(sId));
                    con.setStationAdjust(formData.get(stationAdjustName + i));

                    con.setMlc(CommonUtils.getDoubleValue(formData.get(mlcName + i)));
                    con.setNiclosamide(CommonUtils.getDoubleValue(formData.get(nicloPercName + i)));
                    con.setExposure(CommonUtils.getDoubleValue(formData.get(exposureName + i)));
                    con.setId(CommonUtils.getLongValue(formData.get(minConIdName + i)));

                    con.setTreatment(tr);

                    minLethalCons.add(con);
                }
            }
        }
        CommonUtils.getLogger().debug(
            "{}:getMinLethalConcentrationsFromFormdata returning ({}) TRMinLethalConcentrations",
            SERVICE_NAME, minLethalCons.size());
        return minLethalCons;
    }

    private List<TRWaterChemistry> getWaterChemsFromFormdata(Treatment tr,
                                                             Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:getWaterChemsFromFormdata for Treatment({}) with formData {}",
            SERVICE_NAME, tr.getId(), formData.toString());
        TreatmentValidator.validateWaterChemsFormdata(tr, formData, messages);

        String stationName = "trStation";
        String stationId = "stationId";
        String stationAdjustName = "stationAdjust";

        String sampleDateName = "sampleDate";
        String sampleTimeName = "sampleTime";
        String waterChemIdName = "waterChemId";

        String streamTempName = "streamTemp_";
        String tfmconName = "optradioTfmcon_";
        String disoOxyName = "disolOxy_";
        String ammoniaName = "ammonia_";
        String phName = "ph_";
        String alkaPhName = "alkaPh_";
        String predChartName = "predChart_";
        String phMlcName = "phMlc_";

        List<TRWaterChemistry> waterChems = new ArrayList<>();

        if (!StringUtils.isBlank(formData.get("numOfWaterChems"))) {
            int numOfWaterChems = CommonUtils.getIntFromMap(formData, "numOfWaterChems", SERVICE_NAME);

            for (int i = 0; i < numOfWaterChems; i++) {
                TRWaterChemistry waterChem = new TRWaterChemistry();

                if (!StringUtils.isBlank(formData.get(KEY_TR_BRANCH + i))
                    && !StringUtils.isBlank(formData.get(stationName + i))) {
                    Long brId = Long.valueOf(formData.get(KEY_BRANCH_ID + i));
                    Long sId = Long.valueOf(formData.get(stationId + i));

                    waterChem.setBranchLentic(branchLenticService.getById(brId));
                    waterChem.setStation(stationService.getById(sId));
                    waterChem.setStationAdjust(CommonUtils.getStringValue(formData.get(stationAdjustName + i)));
                    waterChem.setAnalysisDate(CommonUtils.getDateValue(formData.get(sampleDateName + i)));
                    waterChem.setTimeOfSample(CommonUtils.getStringValue(formData.get(sampleTimeName + i)));
                    waterChem.setId(CommonUtils.getLongValue(formData.get(waterChemIdName + i)));

                    waterChem.setStreamTemp(CommonUtils.getIntegerValue(formData.get(streamTempName + i)));

                    if (!StringUtils.isBlank(formData.get(tfmconName + i))) {
                        if (formData.get(tfmconName + i).equalsIgnoreCase("yes")) {
                            waterChem.setTfmConcenGTPointOnePerc(true);
                        } else {
                            waterChem.setTfmConcenGTPointOnePerc(false);
                        }
                    }

                    waterChem.setDissolvedOxygen(CommonUtils.getDoubleValue(formData.get(disoOxyName + i)));
                    waterChem.setAmmonia(CommonUtils.getDoubleValue(formData.get(ammoniaName + i)));
                    waterChem.setPhValue(CommonUtils.getDoubleValue(formData.get(phName + i)));
                    waterChem.setAlkalinityPh(CommonUtils.getDoubleValue(formData.get(alkaPhName + i)));
                    waterChem.setPredictChart(resourceLoadService.findRefCode(
                        RefCodeType.PREDICTION_CHART.getName(),
                        CommonUtils.getStringValue(formData.get(predChartName + i))));
                    waterChem.setPhMinLethalConcentration(CommonUtils.getDoubleValue(formData.get(phMlcName + i)));
                    waterChem.setTreatment(tr);

                    waterChems.add(waterChem);
                }
            }
        }
        CommonUtils.getLogger().debug(
            "{}:getWaterChemsFromFormdata return ({}) TRWaterChemistry", SERVICE_NAME, waterChems.size());
        return waterChems;
    }

    private List<TRDischarge> getDischargesFromFormdata(Treatment tr,
                                                        Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:getDischargesFromFormdata for Treatment({}) with formData {}",
            SERVICE_NAME, tr.getId(), formData.toString());
        TreatmentValidator.validateDischargesFormdata(tr, formData, messages);

        String stationName = "trStation";
        String stationId = "stationId";
        String stationAdjustName = "stationAdjust";
        String sampleDateName = "sampleDate";
        String dischargeIdName = "dischargeId";

        String dischargeName = "discharge_";
        String dischargeCodeName = "dischargeCode_";
        String stationToId = "stationToId_";
        String stationToAdjustName = "stationToAdjust_";
        String elapsedTimeName = "elapsedTime_";
        String cumulativeTimeName = "cumulativeTime_";
        String flowTimeCodeName = "flowTimeCode_";

        List<TRDischarge> discharges = new ArrayList<>();

        if (!StringUtils.isBlank(formData.get("numOfDischarges"))) {
            int numOfDischarges = CommonUtils.getIntFromMap(formData, "numOfDischarges", SERVICE_NAME);

            for (int i = 0; i < numOfDischarges; i++) {
                TRDischarge dis = new TRDischarge();

                if (!StringUtils.isBlank(formData.get(KEY_TR_BRANCH + i))
                    && !StringUtils.isBlank(formData.get(stationName + i))) {
                    Long brId = Long.valueOf(formData.get(KEY_BRANCH_ID + i));
                    Long sId = Long.valueOf(formData.get(stationId + i));

                    dis.setBranchLentic(branchLenticService.getById(brId));
                    dis.setStationFrom(stationService.getById(sId));
                    dis.setStationFromAdjust(CommonUtils.getStringValue(formData.get(stationAdjustName + i)));
                    dis.setAnalysisDate(CommonUtils.getDateValue(formData.get(sampleDateName + i)));
                    dis.setId(CommonUtils.getLongValue(formData.get(dischargeIdName + i)));

                    dis.setDischarge(CommonUtils.getDoubleValue(formData.get(dischargeName + i)));

                    dis.setDischargeCode(resourceLoadService.findRefCode(
                        RefCodeType.DISCHARGE_CODE.getName(),
                        CommonUtils.getStringValue(formData.get(dischargeCodeName + i))));
                    
                    if (!StringUtils.isBlank(formData.get(stationToId + i))) {
                        dis.setStationTo(stationService.getById(Long.valueOf(formData.get(stationToId + i))));
                        dis.setStationToAdjust(CommonUtils.getStringValue(formData.get(stationToAdjustName + i)));
                    }

                    dis.setElapsedTime(CommonUtils.getStringValue(formData.get(elapsedTimeName + i)));
                    dis.setCumulativeTime(CommonUtils.getStringValue(formData.get(cumulativeTimeName + i)));
                    dis.setFlowTimeCode(resourceLoadService.findRefCode(
                        RefCodeType.FLOW_TIME_CODE.getName(),
                        CommonUtils.getStringValue(formData.get(flowTimeCodeName + i))));
                    dis.setTreatment(tr);

                    discharges.add(dis);
                }
            }
        }
        CommonUtils.getLogger().debug(
            "{}:getDischargesFromFormdata return ({}) TRDischarges", SERVICE_NAME, discharges.size());
        return discharges;
    }

    private List<TRChemicalAnalysis> getChemAnalysisesFromFormdata(Treatment tr,
                                                                   Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:getChemAnalysisesFromFormdata for Treatment({}) with formData {}",
            SERVICE_NAME, tr.getId(), formData.toString());
        TreatmentValidator.validateChemicalAnalysisesFormdata(tr, formData, messages);

        String stationName = "trStation";
        String stationId = "stationId";
        String stationAdjustName = "stationAdjust";

        String sampleDateName = "sampleDate";
        String sampleTimeName = "sampleTime";
        String chemAnalysisIdName = "chemAnalysisId";

        String tfmConName = "tfmCon_";
        String niclosamideConName = "niclosamideCon_";

        List<TRChemicalAnalysis> chemAnalysises = new ArrayList<>();

        if (!StringUtils.isBlank(formData.get("numOfChemanalysis"))) {
            int numOfChemanalysis = CommonUtils.getIntFromMap(formData, "numOfChemanalysis", SERVICE_NAME);

            for (int i = 0; i < numOfChemanalysis; i++) {
                TRChemicalAnalysis chemAnaly = new TRChemicalAnalysis();

                if (!StringUtils.isBlank(formData.get(KEY_TR_BRANCH + i))
                    && !StringUtils.isBlank(formData.get(stationName + i))) {
                    Long brId = Long.valueOf(formData.get(KEY_BRANCH_ID + i));
                    Long sId = Long.valueOf(formData.get(stationId + i));

                    chemAnaly.setBranchLentic(branchLenticService.getById(brId));
                    chemAnaly.setStation(stationService.getById(sId));
                    chemAnaly.setStationAdjust(formData.get(stationAdjustName + i));
                    chemAnaly.setAnalysisDate(CommonUtils.getDateValue(formData.get(sampleDateName + i)));
                    chemAnaly.setTimeOfSample(formData.get(sampleTimeName + i));
                    chemAnaly.setId(CommonUtils.getLongValue(formData.get(chemAnalysisIdName + i)));

                    chemAnaly.setConcentrationOfTFM(CommonUtils.getDoubleValue(formData.get(tfmConName + i)));
                    chemAnaly.setConcentrationOfNiclosamide(
                        CommonUtils.getDoubleValue(formData.get(niclosamideConName + i)));

                    chemAnaly.setTreatment(tr);
                    chemAnalysises.add(chemAnaly);
                }
            }
        }
        return chemAnalysises;
    }

    public boolean deleteById(Long id) {
        CommonUtils.getLogger().debug("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            treatmentRepository.deleteById(id);
        } catch (DataIntegrityViolationException de) {
            CommonUtils.getLogger().error(
                "{}:deleteById Can't delete Treatment with id({}) due to DataIntegrityViolationException", id, de);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_TREATMENT_SUCCESS.getName()), de);
        } catch (ConstraintViolationException ce) {
            CommonUtils.getLogger().error(
                "{}:deleteById Can't delete Treatment with id({}) due to ConstraintViolationException", id, ce);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_TREATMENT_SUCCESS.getName()), ce);
        } catch (Exception e) {
            CommonUtils.getLogger().error(
                "{}:deleteById Can't delete Treatment with id({}) due to Exception {}", id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                e instanceof IllegalArgumentException ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR,
                messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug("{}:deleteById completed successfully for id({})", SERVICE_NAME, id);
        return true;
    }

    public List<String> getAllSpeciesSumary(Treatment treatment) {
        List<String> trIMSumary = new ArrayList<>();
        
        Map<String, Integer> returnMap = new HashMap<>();
        for (TRSecondaryApplication secondApp : treatment.getTrSecondaryApplications()) {
            if (secondApp.getTrSecondAppInducedMortality() != null) {
                TRSecondAppInducedMortality trIM = secondApp.getTrSecondAppInducedMortality();
                if (trIM.getSpecies().size() > 0) {
                    for (Specie sp : trIM.getSpecies()) {
                        if (!returnMap.containsKey(sp.getSpeciesCode())) {
                            returnMap.put(sp.getSpeciesCode(), sp.getTotalObserved());
                        } else {
                            returnMap.put(sp.getSpeciesCode(),
                                returnMap.get(sp.getSpeciesCode()) + sp.getTotalObserved());
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : returnMap.entrySet()) {
            String specieCode = entry.getKey();
            Integer count = entry.getValue();
            if (count == null) {
                count = 0;
            }

            String specieStr = scRepository.findBySpeciesCode(specieCode).getShowText();
            String estCodeStr = getEstimateCodeStr(count);

            trIMSumary.add(specieStr + "|" + count.toString() + "|" + estCodeStr);
        }

        return trIMSumary;
    }

    private String getEstimateCodeStr(int inputVal) {
        String codeName = "0";
        if (inputVal > 0 && inputVal <= 10) {
            codeName = "1";
        } else if (inputVal > 10 && inputVal <= 50) {
            codeName = "2";
        } else if (inputVal > 50 && inputVal <= 500) {
            codeName = "3";
        } else if (inputVal > 500 && inputVal <= 1000) {
            codeName = "4";
        } else if (inputVal > 1000 && inputVal <= 10000) {
            codeName = "5";
        } else if (inputVal > 10000) {
            codeName = "6";
        }

        return resourceLoadService.findRefCode(
            RefCodeType.ESTIMATE_CODE.getName(), codeName).getCodePair().getShowText();
    }
}
