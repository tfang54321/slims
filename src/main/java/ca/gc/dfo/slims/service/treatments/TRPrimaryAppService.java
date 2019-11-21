package ca.gc.dfo.slims.service.treatments;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.treatments.TREmulsifiableConcentrate;
import ca.gc.dfo.slims.domain.entity.treatments.TRPrimaryApplication;
import ca.gc.dfo.slims.domain.entity.treatments.TRTFM;
import ca.gc.dfo.slims.domain.entity.treatments.TRWettablePowder;
import ca.gc.dfo.slims.domain.repository.treatments.TRPrimaryAppRepository;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class TRPrimaryAppService {
    private static final String SERVICE_NAME = "TRPrimaryAppService";

    @Autowired
    private TRPrimaryAppRepository    trPrimaryAppRepository;
    @Autowired
    private ResourceLoadService        resourceLoadService;
    @Autowired
    private AppMessages                messages;

    public List<TRPrimaryApplication> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        Iterable<TRPrimaryApplication> trLogistics = trPrimaryAppRepository.findAll();
        if (trLogistics == null) {
            return new LinkedList<>();
        }
        return Lists.newArrayList(trLogistics);
    }

    public TRPrimaryApplication getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with TRPrimaryApplication id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(trPrimaryAppRepository.findById(id),
            SERVICE_NAME + "getById(TRPrimaryApplication)", id, messages);
    }

    public TRPrimaryApplication save(TRPrimaryApplication trPriApp) {
        CommonUtils.getLogger().debug("{}:save TRPrimaryApplication with id({})", SERVICE_NAME, trPriApp.getId());
        TRPrimaryApplication returnTrPriApp = null;
        try {
            returnTrPriApp = trPrimaryAppRepository.save(trPriApp);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:save TRPrimaryApplication({}) got exception {}",
                SERVICE_NAME, trPriApp.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_TRPRIMARYAPPLICATION_ERROR.getName()), e);
        }
        return returnTrPriApp;
    }

    public TRPrimaryApplication updateTrPrimaryAppFromFormData(String id,
                                                               Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updateTrPrimaryAppFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        TRPrimaryApplication primApp = getById(
            CommonUtils.getLongFromString(id, "ID of treatment primaryApplication"));

        buildPrimAppFromFormData(primApp, formData);

        List<TRTFM> tfms = getTFMsFromFormdata(formData);
        List<TRWettablePowder> wps = getWPsFromFormdata(formData);
        List<TREmulsifiableConcentrate> ecs = getECsFromFormdata(formData);

        primApp.setTrTFMs(tfms);
        primApp.setTrWettablePowders(wps);
        primApp.setTrEmulsifiableConcentrates(ecs);

        return save(primApp);
    }
    
    private void buildPrimAppFromFormData(TRPrimaryApplication primApp,
                                          Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:buildPrimAppFromFormData start for {} with formData {}",
            SERVICE_NAME, primApp.getId(), formData.toString());

        TreatmentValidator.validatePrimAppFormData(formData, messages);

        primApp.setTreatmentStartDate(CommonUtils.getDateValue(formData.get("trStartDate")));
        primApp.setTreatmentEndDate(CommonUtils.getDateValue(formData.get("trEndDate")));
        primApp.setTimeOn(CommonUtils.getStringValue(formData.get("time_on")));
        primApp.setTimeOff(CommonUtils.getStringValue(formData.get("time_off")));
        primApp.setDuration(CommonUtils.getStringValue(formData.get("tr_duration")));
        primApp.setApplicationCode(resourceLoadService.findRefCode("PRIMARY_APPLICATION_CODE",
            CommonUtils.getStringValue(formData.get("PRIMARY_APPLICATION_CODE"))));
        primApp.setApplicationMethod(resourceLoadService.findRefCode("PRIMARY_APPLICATION_METHOD",
            CommonUtils.getStringValue(formData.get("PRIMARY_APPLICATION_METHOD"))));
        // set mapDatum
        primApp.setGeoUTM(
            GeoUtmUtils.getGeoUtmFromFormData(formData, "tr_location", resourceLoadService));

        CommonUtils.getLogger().debug("{}:buildPrimAppFromFormData Finished for TRPrimaryApplication({})",
            SERVICE_NAME, primApp.getId());
    }
    
    private List<TRTFM> getTFMsFromFormdata(Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getTFMsFromFormdata with formData {}", SERVICE_NAME, formData.toString());
        TreatmentValidator.validateTFMsFormdata(formData, messages);

        String lpName = "tfmLP";
        String literName = "tfmLiterUsed";

        List<TRTFM> tfms = new ArrayList<>();

        int numOfTFMs = getIntFromMap(formData,"numOfTFMs");
        for (int i = 0; i < numOfTFMs; i++) {
            String lpValue = formData.get(lpName + i);
            String literValue = formData.get(literName + i);
            if (!StringUtils.isBlank(lpValue) && !StringUtils.isBlank(literName)) {
                TRTFM trTFM = new TRTFM();
                trTFM.setTfmLPId(CommonUtils.getLongValue(lpValue));
                trTFM.setLitresUsed(CommonUtils.getDoubleValue(literValue));

                tfms.add(trTFM);
            }
        }
        CommonUtils.getLogger().debug("{}:getTFMsFromFormdata Got {} TRTFMs", SERVICE_NAME, tfms.size());
        return tfms;
    }

    private List<TRWettablePowder> getWPsFromFormdata(Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getWPsFromFormdata with formData {}", SERVICE_NAME, formData.toString());
        TreatmentValidator.validateWPsFormdata(formData, messages);

        String kgName = "wpKgUsed";
        String percAIName = "wpPercAI";
        String kgAIName = "wpKgAI";

        List<TRWettablePowder> wps = new ArrayList<>();

        int numOfWPs = getIntFromMap(formData,"numOfWPs");
        for (int i = 0; i < numOfWPs; i++) {
            if (!StringUtils.isBlank(formData.get(kgName + i))
                && !StringUtils.isBlank(formData.get(percAIName + i))) {
                TRWettablePowder wp = new TRWettablePowder();
                wp.setKgUsed(CommonUtils.getDoubleValue(formData.get(kgName + i)));
                wp.setWpPercAI(resourceLoadService.findRefCode(RefCodeType.WP_PERCAI.getName(),
                    CommonUtils.getStringValue(formData.get(percAIName + i))));
                wp.setWpKgAI(CommonUtils.getDoubleValue(formData.get(kgAIName + i)));

                wps.add(wp);
            }
        }
        CommonUtils.getLogger().debug("{}:getWPsFromFormdata Got {} TRWettablePowders", SERVICE_NAME, wps.size());
        return wps;
    }
    
    private List<TREmulsifiableConcentrate> getECsFromFormdata(Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getECsFromFormdata with formData {}", SERVICE_NAME, formData.toString());
        TreatmentValidator.validateECsFormdata(formData, messages);

        String literName = "ecLiterUsed";
        String percAIName = "ecPercAI";
        String kgAIName = "ecKgAI";

        List<TREmulsifiableConcentrate> ecs = new ArrayList<>();

        int numOfECs = getIntFromMap(formData,"numOfECs");
        for (int i = 0; i < numOfECs; i++) {
            if (!StringUtils.isBlank(formData.get(literName + i))
                && !StringUtils.isBlank(formData.get(percAIName + i))) {
                TREmulsifiableConcentrate ec = new TREmulsifiableConcentrate();
                ec.setLitresUsed(CommonUtils.getDoubleValue(formData.get(literName + i)));
                ec.setEcPercAI(resourceLoadService.findRefCode(RefCodeType.EC_PERCAI.getName(),
                    CommonUtils.getStringValue(formData.get(percAIName + i))));
                ec.setEcKgAI(CommonUtils.getDoubleValue(formData.get(kgAIName + i)));

                ecs.add(ec);
            }
        }
        CommonUtils.getLogger().debug("{}:getECsFromFormdata Got {} TREmulsifiableConcentrates",
            SERVICE_NAME, ecs.size());
        return ecs;
    }

    private int getIntFromMap(Map<String, String> data, String key) {
        return CommonUtils.getIntFromMap(data, key, SERVICE_NAME);
    }
}
