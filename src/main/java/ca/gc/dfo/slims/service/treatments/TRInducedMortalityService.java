package ca.gc.dfo.slims.service.treatments;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.FishIndividual;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondAppInducedMortality;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondaryApplication;
import ca.gc.dfo.slims.domain.repository.treatments.TRInducedMortalityRepository;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class TRInducedMortalityService {
    private static final String SERVICE_NAME = "TRInducedMortalityService";

    @Autowired
    private TRInducedMortalityRepository    trInducedMortalityRepository;
    @Autowired
    private AppMessages                        messages;

    public TRSecondAppInducedMortality save(TRSecondAppInducedMortality trIM) {
        CommonUtils.getLogger().debug("{}:save with TRSecondAppInducedMortality({})", SERVICE_NAME, trIM.getId());
        TRSecondAppInducedMortality returnTrIM = null;
        try {
            returnTrIM = trInducedMortalityRepository.save(trIM);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save failed to save TRSecondAppInducedMortality({}) due to {}",
                SERVICE_NAME, trIM.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST,
                messages.get(ResponseMessages.SAVE_TRSECONDAPPINDUCEDMORTALITY_ERROR.getName()),
                ex);
        }
        CommonUtils.getLogger().debug(
            "{}:save completed successfully for TRSecondAppInducedMortality({})", SERVICE_NAME, returnTrIM.getId());
        return returnTrIM;
    }

    void updateIMSpeciesFromFormData(TRSecondaryApplication secondApp,
                                     Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateIMSpeciesFromFormData for TRSecondaryApplication({}) with formData {}",
            SERVICE_NAME, secondApp.getId(), formData.toString());
        TRSecondAppInducedMortality trIM = secondApp.getTrSecondAppInducedMortality();
        List<Specie> species = getSpeciesFromFormData(trIM, formData);
        trIM.setSpecies(species);
        save(trIM);
    }

    private List<Specie> getSpeciesFromFormData(TRSecondAppInducedMortality trIM, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData for TRSecondAppInducedMortality({}) with formData {}",
            SERVICE_NAME, trIM.getId(), formData.toString());

        String fishSpecieName = "fishSpecies";
        String observeName = "observed";
        String indiLengthName = "indiLen";
        String indiIdName = "indiId";
        String numOfindividualsName = "_numOfIndi";
        String specieName = "specie";
        String specieIdName = "specieId";

        List<Specie> species = new ArrayList<>();
        int numOfSpecies = CommonUtils.getIntValue(formData.get("numOfSpecies"));
        for (int i = 0; i < numOfSpecies; i++) {
            if (!StringUtils.isBlank(formData.get(fishSpecieName + i))) {
                Specie specie = new Specie();
                specie.setSpeciesCode(CommonUtils.getObjectCode(formData.get(fishSpecieName + i)));
                specie.setTotalObserved(CommonUtils.getIntegerValue(formData.get(observeName + i)));
                
                Long specieId = CommonUtils.getLongValue(formData.get(specieIdName + i));
                if(specieId != null) {
                   specie.setId(specieId);
                }
                
                List<FishIndividual> fishIndividuals = new ArrayList<>();
                int numOfIndividuals = CommonUtils.getIntValue(formData.get(specieName + i + numOfindividualsName));
                
                for (int j = 0; j < numOfIndividuals; j++) {
                    FishIndividual fishIndividual = new FishIndividual();
                    fishIndividual.setIndividualLength(
                        CommonUtils.getDoubleValue(formData.get(observeName + i + "_" + indiLengthName + j)));
                    Long indiId = CommonUtils.getLongValue(formData.get(observeName + i + "_" + indiIdName + j));
                    if(indiId != null) {
                        fishIndividual.setId(indiId);
                    }
                    fishIndividual.setSpecie(specie);
                    fishIndividuals.add(fishIndividual);
                }
                specie.setTrSecondAppInducedMortality(trIM);
                specie.setFishIndividuals(fishIndividuals);

                species.add(specie);            
            }
        }

        CommonUtils.getLogger().debug("{}:getSpeciesFromFormData returning ({}) Species", SERVICE_NAME, species.size());
        return species;
    }
}
