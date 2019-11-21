package ca.gc.dfo.slims.service.treatments;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.treatments.TRGranularBayluscide;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondAppInducedMortality;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.TRTFM;
import ca.gc.dfo.slims.domain.repository.treatments.TRSecondaryAppRepository;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.service.location.BranchLenticService;
import ca.gc.dfo.slims.service.location.StationService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.GeoUtmUtils;
import ca.gc.dfo.slims.validation.treatments.TreatmentValidator;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class TRSecondaryAppService {
    private static final String SERVICE_NAME = "TRSecondaryAppService";

    @Autowired
    private TRSecondaryAppRepository    trSecondaryAppRepository;
    @Autowired
    private TRInducedMortalityService   trInducedMortalityService;
    @Autowired
    private ResourceLoadService         resourceLoadService;
    @Autowired
    private BranchLenticService         branchLenticService;
    @Autowired
    private StationService              stationService;
    @Autowired
    private AppMessages                 messages;

    public List<TRSecondaryApplication> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        Iterable<TRSecondaryApplication> trLogistics = trSecondaryAppRepository.findAll();
        if (trLogistics == null)
            return new LinkedList<>();
        return Lists.newArrayList(trLogistics);
    }

    public TRSecondaryApplication getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with TRSecondaryApplication id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(trSecondaryAppRepository.findById(id),
            SERVICE_NAME + "getById(TRSecondaryApplication)", id, messages);
    }

    public TRSecondaryApplication save(TRSecondaryApplication trSecondApp) {
        CommonUtils.getLogger().debug("{}:save TRSecondaryApplication with id({})", SERVICE_NAME, trSecondApp.getId());
        TRSecondaryApplication returnTrSecondApp = null;
        try {
            returnTrSecondApp = trSecondaryAppRepository.save(trSecondApp);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:save TRSecondaryApplication({}) got exception {}",
                SERVICE_NAME, trSecondApp.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_TRSECONDARYAPPLICATION_ERROR.getName()), e);
        }
        return returnTrSecondApp;
    }

    public TRSecondaryApplication updateTrSecondaryAppFromFormData(String id,
                                                                   Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateTrSecondaryAppFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        TRSecondaryApplication secondApp = getById(
            CommonUtils.getLongFromString(id, "ID of treatment secondaryApplication"));

        buildSecondaryAppFromFormData(secondApp, formData);
        return save(secondApp);
    }

    public TRSecondaryApplication updateInducedMortalityFromFormData(String id,
                                                                     Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateInducedMortalityFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        TRSecondaryApplication secondAppDb = getById(
            CommonUtils.getLongFromString(id, "ID of treatment secondaryApplication"));

        TRSecondaryApplication secondApp = buildInducedMortalityFromFormData(secondAppDb, formData);
        secondApp = save(secondApp);
        trInducedMortalityService.updateIMSpeciesFromFormData(secondApp, formData);
        return secondApp;
    }

    // build secondary application from form (secondary app main page)
    private void buildSecondaryAppFromFormData(TRSecondaryApplication secondApp,
                                               Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildSecondaryAppFromFormData start for {} with formData {}",
            SERVICE_NAME, secondApp.getId(), formData.toString());
        TreatmentValidator.validateSecondaryAppFormData(formData, messages);

        if (!StringUtils.isBlank(formData.get("branchToId"))) {
            secondApp.setBranchLenticTo(branchLenticService.getById(
                CommonUtils.getLongFromString(formData.get("branchToId"), "branchToId")));
        }

        if (!StringUtils.isBlank(formData.get("stationToId"))) {
            secondApp.setStationTo(stationService.getById(
                CommonUtils.getLongFromString(formData.get("stationToId"), "stationToId")));
        }

        secondApp.setStationToAdjust(CommonUtils.getStringValue(formData.get("stationToAdjust")));
        secondApp.setDuration(CommonUtils.getStringValue(formData.get("tr_duration")));
        secondApp.setApplicationCode(resourceLoadService.findRefCode("SECONDARY_APPLICATION_CODE",
            CommonUtils.getStringValue(formData.get("SECONDARY_APPLICATION_CODE"))));
        secondApp.setApplicationMethod(resourceLoadService.findRefCode("SECONDARY_APPLICATION_METHOD",
            CommonUtils.getStringValue(formData.get("SECONDARY_APPLICATION_METHOD"))));

        secondApp.setGeoUTM(
            GeoUtmUtils.getGeoUtmFromFormData(formData, "tr_location", resourceLoadService));

        TRTFM trTFM = new TRTFM();
        trTFM.setTfmLPId(CommonUtils.getLongValue(formData.get("TFM_Bars")));
        trTFM.setNumOfBars(CommonUtils.getDoubleValue(formData.get("numof_bars")));
        trTFM.setWeightOfBars(CommonUtils.getDoubleValue(formData.get("weightof_bars")));
        trTFM.setTfmPercAI(resourceLoadService.findRefCode(RefCodeType.TFM_PERCAI.getName(),
            CommonUtils.getStringValue(formData.get("tfm_perc_ai"))));
        trTFM.setLitresUsed(CommonUtils.getDoubleValue(formData.get("litersUsed")));
        trTFM.setTfmKgAI(CommonUtils.getDoubleValue(formData.get("kg_ai")));
        trTFM.setTfmLPId(CommonUtils.getLongValue(formData.get("TFM")));
        secondApp.setTrTFM(trTFM);

        TRGranularBayluscide trGB = new TRGranularBayluscide();
        trGB.setAmountUsed(CommonUtils.getDoubleValue(formData.get("amount_used")));
        trGB.setGbPercAI(resourceLoadService.findRefCode(RefCodeType.GB_PERCAI.getName(),
            CommonUtils.getStringValue(formData.get("gb_perc_ai"))));
        secondApp.setTrGranularBayluscide(trGB);

        CommonUtils.getLogger().debug("{}:buildSecondaryAppFromFormData Finished for TRSecondaryApplication({})",
            SERVICE_NAME, secondApp.getId());
    }

    // build secondary application induced mortality from form
    private TRSecondaryApplication buildInducedMortalityFromFormData(TRSecondaryApplication secondApp,
                                                                     Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildInducedMortalityFromFormData start for {} with formData {}",
            SERVICE_NAME, secondApp.getId(), formData.toString());
        TreatmentValidator.validateInducedMortalityFormData(formData, messages);

        TRSecondAppInducedMortality inducedMortality = new TRSecondAppInducedMortality();
        if (secondApp.getTrSecondAppInducedMortality() != null) {
            inducedMortality.setId(secondApp.getTrSecondAppInducedMortality().getId());
        }

        inducedMortality.setCollectCondition(CommonUtils.getStringValue(formData.get("optradio_colcon")));
        inducedMortality.setLarvae(resourceLoadService.findRefCode("ESTIMATE_CODE",
            CommonUtils.getStringValue(formData.get("larvae_est"))));
        inducedMortality.setTransformers(resourceLoadService.findRefCode("ESTIMATE_CODE",
            CommonUtils.getStringValue(formData.get("transformers_est"))));
        inducedMortality.setAdults(resourceLoadService.findRefCode("ESTIMATE_CODE",
            CommonUtils.getStringValue(formData.get("tr_adults"))));
        inducedMortality.setRemarks(CommonUtils.getStringValue(formData.get("tr_remarks")));
        inducedMortality.setTrSecondaryApplication(secondApp);
        secondApp.setTrSecondAppInducedMortality(inducedMortality);

        CommonUtils.getLogger().debug("{}:buildInducedMortalityFromFormData Finished for TRSecondaryApplication({})",
            SERVICE_NAME, secondApp.getId());
        return secondApp;
    }

}
