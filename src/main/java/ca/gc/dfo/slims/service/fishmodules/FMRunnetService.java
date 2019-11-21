package ca.gc.dfo.slims.service.fishmodules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.gc.dfo.slims.utility.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.fishmodule.FMRunNet;
import ca.gc.dfo.slims.domain.entity.fishmodule.FishModule;
import ca.gc.dfo.slims.domain.repository.fishmodule.FMRunNetRepository;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.validation.fishmodules.FMValidator;

/**
 * @author ZHUY
 *
 */
@Service
public class FMRunnetService extends BaseFishModuleService {
    private static final String SERVICE_NAME = "FMRunnetService";

    @Autowired
    private FMRunNetRepository    fmRunNetRepository;
    @Autowired
    private FishModuleService    fmService;
    @Autowired
    private ResourceLoadService    resourceLoadService;
    @Autowired
    private AppMessages            messages;

    public List<FMRunNet> getAllByFMId(Long fm_id) {
        return CommonUtils.getReturnListFromIterable(
            fmRunNetRepository.findByFishModuleIdOrderByRunNetNumberAsc(fm_id));
    }
    
    public FMRunNet getById(Long id) {
        return CommonUtils.getIfPresent(
            fmRunNetRepository.findById(id), SERVICE_NAME + "getById(FMRunNet)", id, messages);
    }
    
    public FMRunNet save(FMRunNet fmRunnet) {
        CommonUtils.getLogger().debug("{}:save FMRunNet({})", SERVICE_NAME, fmRunnet.getId());
        FMRunNet returnFmrunnet = null;
        try {
            returnFmrunnet = fmRunNetRepository.save(fmRunnet);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error("{}:save Can't save: FMRunNet ({}) due to DataIntegrityViolationException",
                SERVICE_NAME, fmRunnet.getId(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_FMRUNNET_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save Can't save: FMRunNet ({}) due to Exception {}",
                SERVICE_NAME, fmRunnet.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_FMRUNNET_ERROR.getName()), ex);
        }

        return returnFmrunnet;
    }
    
    public FMRunNet saveRunnetFromFormData(String fmId, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:saveRunnetFromFormData with fmId({}) and formData {}",
            SERVICE_NAME, fmId, formData.toString());
        FMRunNet fmRunnet = buildRunnetFromFormData(null, formData);
        FishModule fm = fmService.getById(CommonUtils.getLongFromString(fmId, "ID of FishModule"));
        fmRunnet.setFishModule(fm);
        CommonUtils.getLogger().debug("{}:saveRunnetFromFormData set FishModule to FMRunNet({})",
            SERVICE_NAME, fmRunnet.getId());
        fmRunnet = save(fmRunnet);

        List<Specie> species = getSpeciesFromFormData(fmRunnet, formData);
        int speciesSize = species.size();
        CommonUtils.getLogger().debug("{}:saveRunnetFromFormData got ({}) species", SERVICE_NAME, speciesSize);
        if (speciesSize > 0) {
            fmRunnet.setSpecies(species);
            CommonUtils.getLogger().debug("{}:saveRunnetFromFormData set Specie list to FMRunNet({})",
                SERVICE_NAME, fmRunnet.getId());
            fmRunnet = save(fmRunnet);
        }

        CommonUtils.getLogger().debug("{}:saveRunnetFromFormData return saved FMRunNet({})",
            SERVICE_NAME, fmRunnet.getId());
        return fmRunnet;
    }

    public FMRunNet updateRunnetFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateRunnetFromFormData with Id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        FMRunNet fmRunNet = getById(CommonUtils.getLongFromString(id, "ID of FMRunNet"));
        FMRunNet updatedRunNet = buildRunnetFromFormData(fmRunNet, formData);
        return save(updatedRunNet);
    }

    private FMRunNet buildRunnetFromFormData(FMRunNet theRunnet, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildRunnetFromFormData with FMRunNet({}) and formData {}",
            SERVICE_NAME, theRunnet == null ? "null" : theRunnet.getId(), formData.toString());
        FMValidator.validateRunnetFormData(formData, messages);

        FMRunNet fmRunNet = theRunnet == null ? new FMRunNet() : theRunnet;
        fmRunNet.setRunNetNumber(CommonUtils.getIntegerValue(formData.get("runnetNumber")));
        fmRunNet.setPersonElectroFishing(CommonUtils.getIntegerValue(formData.get("personElecFishing")));
        fmRunNet.setPersonCatching(CommonUtils.getIntegerValue(formData.get("personCatching")));
        fmRunNet.setEstduration(CommonUtils.getIntegerValue(formData.get("est_duration")));
        fmRunNet.setMeasuredDuration(CommonUtils.getStringValue(formData.get("measured_duration")));
        fmRunNet.setElectroFishType(CommonUtils.getStringValue(formData.get("optradio_elecsetting")));
        fmRunNet.setPeakVolt(CommonUtils.getDoubleValue(formData.get("peak_vol")));
        fmRunNet.setBurstRate(CommonUtils.getDoubleValue(formData.get("burst_mode")));
        fmRunNet.setSlowRate(CommonUtils.getDoubleValue(formData.get("slow_rate")));
        fmRunNet.setFastRate(CommonUtils.getDoubleValue(formData.get("fast_rate")));
        fmRunNet.setSlowDuty(CommonUtils.getDoubleValue(formData.get("slow_duty")));
        fmRunNet.setFastDuty(CommonUtils.getDoubleValue(formData.get("fast_duty")));
        
        if (theRunnet != null) {
            List<Specie> species = getSpeciesFromFormData(fmRunNet, formData);
            fmRunNet.setSpecies(species);
        }

        CommonUtils.getLogger().debug(
            "{}:buildRunnetFromFormData return built FMRunNet({})", SERVICE_NAME, fmRunNet.getId());
        return fmRunNet;
    }

    private List<Specie> getSpeciesFromFormData(FMRunNet fmRunnet, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData with FMRunNet({}) and formData {}",
            SERVICE_NAME, fmRunnet.getId(), formData.toString());
        FMValidator.validateSpeciesFormdata(formData, messages);

        String fishSpecieName = "fishSpecies";
        String totalCaughtName = "totalCaught";
        String totalObservedName = "totalObserved";
        String specieIdName = "specieId";

        List<Specie> species = new ArrayList<>();
        
        int numOfSpecies = CommonUtils.getIntValue(formData.get("numOfSpecies"));
        for (int i = 0; i < numOfSpecies; i++) {
            if (StringUtils.isBlank(formData.get(fishSpecieName + i))) {
                continue;
            }

            Specie specie = new Specie();
            specie.setSpeciesCode(CommonUtils.getObjectCode(formData.get(fishSpecieName + i)));
            specie.setTotalcaught(CommonUtils.getIntegerValue(formData.get(totalCaughtName + i)));
            specie.setTotalObserved(CommonUtils.getIntegerValue(formData.get(totalObservedName + i)));

            Long specieId = CommonUtils.getLongValue(formData.get(specieIdName + i));
            if (specieId != null) {
                specie.setId(specieId);
            }

            specie.setFishIndividuals(super.getFishIndividualsList(formData, i, resourceLoadService, specie));
            specie.setFmRunNet(fmRunnet);

            species.add(specie);
        }

        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData return ({}) species", SERVICE_NAME, species.size());
        return species;
    }

    public boolean deleteById(Long id) {
        return super.deleteById(fmRunNetRepository, id, SERVICE_NAME, messages);
    }
}
