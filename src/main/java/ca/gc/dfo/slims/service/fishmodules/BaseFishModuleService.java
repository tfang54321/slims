package ca.gc.dfo.slims.service.fishmodules;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.FishIndividual;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class BaseFishModuleService {
    <T> boolean deleteById(JpaRepository<T, Long> jpaRepository, Long id, String caller, AppMessages messages) {
        CommonUtils.getLogger().debug("{} deleteById with id({})", caller, id);
        try {
            jpaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{} deleteById({}) failed due to DataIntegrityViolationException", caller, id, e);
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_FISHMODULE_ERROR.getName()), e);
        } catch (ConstraintViolationException e) {
            CommonUtils.getLogger().error(
                "{} deleteById({}) failed due to ConstraintViolationException", caller, id, e);
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_FISHMODULE_ERROR.getName()), e);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{} deleteById({}) failed due to {}", caller, id, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        return true;
    }

    List<FishIndividual> getFishIndividualsList(Map<String, String> formData,
                                                int i,
                                                ResourceLoadService resourceLoadService,
                                                Specie specie) {
        String fishSpecieName = "fishSpecies";

        String specieName = "specie";
        String numOfindividualsName = "_numOfIndi";

        String specimenName = "specimen";
        String sexName = "fishSex";
        String keptName = "kept";
        String indiLengthName = "indiLen";
        String indiWeightName = "indiWeight";
        String dateMeasuredName = "dateMeasured";
        String individualIdName = "individualId";
        List<FishIndividual> fishIndividuals = new ArrayList<>();

        int numOfIndividuals = CommonUtils.getIntValue(formData.get(specieName + i + numOfindividualsName));
        for (int j = 0; j < numOfIndividuals; j++) {
            if (!StringUtils.isBlank(formData.get(fishSpecieName + i + "_" + indiLengthName + j))
                && !StringUtils.isBlank(formData.get(fishSpecieName + i + "_" + indiWeightName + j))) {
                FishIndividual fishIndividual = new FishIndividual();
                fishIndividual.setSpecimenState(resourceLoadService.findRefCode(
                    RefCodeType.SPECIMEN_STATE.getName(),
                    CommonUtils.getStringValue(formData.get(fishSpecieName + i + "_" + specimenName + j))));
                fishIndividual.setIndividualSex(resourceLoadService.findRefCode(
                    RefCodeType.FISH_SEX.getName(),
                    CommonUtils.getStringValue(formData.get(fishSpecieName + i + "_" + sexName + j))));
                fishIndividual.setIndividualLength(
                    CommonUtils.getDoubleValue(formData.get(fishSpecieName + i + "_" + indiLengthName + j)));
                fishIndividual.setIndividualWeight(
                    CommonUtils.getDoubleValue(formData.get(fishSpecieName + i + "_" + indiWeightName + j)));
                try {
                    fishIndividual.setDateMeasured(
                        CommonUtils.getDateValue(formData.get(fishSpecieName + i + "_" + dateMeasuredName + j)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                fishIndividual.setKept(
                    formData.get(fishSpecieName + i + "_" + keptName + j).equalsIgnoreCase("yes"));

                Long indiId = CommonUtils.getLongValue(formData.get(fishSpecieName + i + "_" + individualIdName + j));
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
