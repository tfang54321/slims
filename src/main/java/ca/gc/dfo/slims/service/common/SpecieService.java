package ca.gc.dfo.slims.service.common;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.domain.entity.common.FishIndividual;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.repository.common.SpecieRepository;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.validation.species.SpecieValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class SpecieService {
    private static final String SERVICE_NAME = "SpecieService";

    @Autowired
    private SpecieRepository    specieRepository;
    @Autowired
    private ResourceLoadService resourceLoadService;
    @Autowired
    private AppMessages         messages;

    public Specie getBySpeciesCodeAndLarvalAssessmentId(String speciesCode, Long larvalAssessmentId) {
        return specieRepository.findBySpeciesCodeAndLarvalAssessmentId(speciesCode, larvalAssessmentId);
    }

    public Specie getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(
            specieRepository.findById(id), SERVICE_NAME + "getById(Specie)", id, messages);
    }

    public Specie save(Specie specie) {
        CommonUtils.getLogger().debug("{}:save with specie({})", SERVICE_NAME, specie.getId());
        Specie returnSpecie = null;
        try {
            returnSpecie = specieRepository.save(specie);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:save could not save SpecieCode({}) due to {}. return null.",
                SERVICE_NAME, specie.getId(), e.getMessage(), e);
        }
        return returnSpecie;
    }

    public Specie updateLaSpecieFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateLaSpecieFromFormData for id({}) with formData {}",
            SERVICE_NAME, id, formData.toString());
        Specie specie = getById(CommonUtils.getLongFromString(id, "ID of Specie"));
        List<FishIndividual> fishIndividuals = getFishIndividualsFromFormData(specie, formData);
        specie.setFishIndividuals(fishIndividuals);
        return save(specie);
    }

    private List<FishIndividual> getFishIndividualsFromFormData(Specie specie, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getFishIndividualsFromFormData for Specie({}) with formData {}",
            SERVICE_NAME, specie.getId(), formData.toString());
        SpecieValidator.validateFishIndividualsFormdata(formData, messages);

        String fishLengthFormName = "fishLength";
        String fishWeightFormName = "fishWeight";
        String specimenFormName = "specimen";
        String conFactorFormName = "conFactor";
        String fishSexFormName = "fishSex";
        String recaptureFormName = "recapture";
        String individualIdName = "individualId";

        List<FishIndividual> fishIndividuals = new ArrayList<>();
        int numOfIndividuals = Integer.valueOf(formData.get("numOfIndividuals"));
        for (int i = 0; i < numOfIndividuals; i++) {
            String fishLengthForm = formData.get(fishLengthFormName + i);
            String fishWeightForm = formData.get(fishWeightFormName + i);
            if (!StringUtils.isBlank(fishLengthForm)
                && !StringUtils.isBlank(fishWeightForm)
                && !fishLengthForm.trim().equals("0")
                && !fishWeightForm.trim().equals("0")) {

                FishIndividual fishIndividual = new FishIndividual();
                fishIndividual.setIndividualLength(CommonUtils.getDoubleValue(fishLengthForm));
                fishIndividual.setIndividualWeight(CommonUtils.getDoubleValue(fishWeightForm));

                fishIndividual.setSpecimenState(
                    resourceLoadService.findRefCode(
                        RefCodeType.SPECIMEN_STATE.getName(),
                        CommonUtils.getStringValue(formData.get(specimenFormName + i))));

                fishIndividual.setConditionfactor(
                    CommonUtils.getDoubleValue(formData.get(conFactorFormName + i)));

                fishIndividual.setIndividualSex(
                    resourceLoadService.findRefCode(
                        RefCodeType.FISH_SEX.getName(),
                        CommonUtils.getStringValue(formData.get(fishSexFormName + i))));

                fishIndividual.setRecapture(
                    formData.get(recaptureFormName + i).equalsIgnoreCase("yes"));

                Long indiId = CommonUtils.getLongValue(formData.get(individualIdName + i));
                if (indiId != null) {
                    fishIndividual.setId(indiId);
                }

                fishIndividual.setSpecie(specie);

                fishIndividuals.add(fishIndividual);
            }
        }
        return fishIndividuals;
    }
}
