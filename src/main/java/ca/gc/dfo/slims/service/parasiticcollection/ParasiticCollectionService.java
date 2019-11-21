package ca.gc.dfo.slims.service.parasiticcollection;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.FishIndividual;
import ca.gc.dfo.slims.domain.entity.parasiticcollection.ParasiticAttachment;
import ca.gc.dfo.slims.domain.entity.parasiticcollection.ParasiticCollection;
import ca.gc.dfo.slims.domain.repository.parasiticcollection.ParasiticCollectionRepository;
import ca.gc.dfo.slims.dto.parasiticcollection.ParasiticCollectionDTO;
import ca.gc.dfo.slims.service.AbstractService;
import ca.gc.dfo.slims.service.common.LakeDistrictService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.SampleUtils;
import ca.gc.dfo.slims.utility.YearUtils;
import ca.gc.dfo.slims.validation.parasiticcollection.PCValidator;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Slf4j
@Service
public class ParasiticCollectionService extends AbstractService {
    private static final String SERVICE_NAME = "ParasiticCollectionService";

    private final String PC_PREFIX = "PC";

    @Autowired
    private ParasiticCollectionRepository    pcRepository;
    @Autowired
    private ResourceLoadService              resourceLoadService;
    @Autowired
    private LakeDistrictService              ldService;
    @Autowired
    private AppMessages                      messages;

    @Override
    public List<ParasiticCollectionDTO> getAll() {
        return CommonUtils.getReturnList(pcRepository.findAllList());
    }

    @Override
    public List<ParasiticCollectionDTO> getAll(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(pcRepository.findAllList(fromDate, toDate));
    }

    @Override
    public List<ParasiticCollectionDTO> getAllAfterYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date fromDate = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(pcRepository.findAllListAfterYear(fromDate));
    }

    @Override
    public List<ParasiticCollectionDTO> getAllBeforeYear(String inputYear) {
        Integer year = Integer.valueOf(inputYear);
        Date toDate = new GregorianCalendar(year + 1, Calendar.JANUARY, 1).getTime();
        return CommonUtils.getReturnList(pcRepository.findAllListBeforeYear(toDate));
    }

    public List<Integer> getYearList() {
        ParasiticCollection pc = pcRepository.findFirstByOrderByCollectedDateAsc();
        return YearUtils.getYearList(pc == null ? null : pc.getCollectedDate(), SERVICE_NAME);
    }

    public ParasiticCollection getById(Long id) {
        return CommonUtils.getIfPresent(pcRepository.findById(id),
            SERVICE_NAME + "getById(ParasiticCollection)", id, messages);
    }

    public List<String> getAllFisherNames() {
        return pcRepository.findDistinctFisherName();
    }

    public ParasiticCollection save(ParasiticCollection pc) {
        CommonUtils.getLogger().debug("{}:save with ParasiticCollection({})", SERVICE_NAME, pc.getId());
        ParasiticCollection returnPc = null;
        try {
            returnPc = pcRepository.save(pc);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save ParasiticCollection({}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME, pc.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.SAVE_PARASITICCOLLECTION_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save ParasiticCollection({}) due to: {}",
                SERVICE_NAME, pc.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_PARASITICCOLLECTION_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug("{}:save return saved ParasiticCollection({})", SERVICE_NAME, returnPc.getId());
        return returnPc;
    }

    public ParasiticCollection savePcFromFormData(Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:savePcFromFormData with formData {}", SERVICE_NAME, formData.toString());
        ParasiticCollection pc = buildPcFromFormData(null, formData);
        return save(pc);
    }

    public ParasiticCollection updatePcAttachmentsFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updatePcAttachmentsFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        ParasiticCollection pc = getById(CommonUtils.getLongFromString(id, "ID of ParasiticCollection"));
        List<ParasiticAttachment> attachments = getAttachmentsFromFormData(pc, formData);
        pc.setParasiticAttachments(attachments);
        return save(pc);
    }

    private ParasiticCollection buildPcFromFormData(ParasiticCollection thePc,
                                                    Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:buildPcFromFormData with ParasiticCollection({}) and formData {}",
            SERVICE_NAME, thePc == null ? null : thePc.getId(), formData.toString());
        PCValidator.validatePcFormData(formData, messages);
        ParasiticCollection pc = thePc == null ? new ParasiticCollection(): thePc;

        pc.setCollectedDate(CommonUtils.getDateValue(formData.get("collectedDate")));
        pc.setFisherName(CommonUtils.getStringValue(formData.get("firsherName")));

        if (!StringUtils.isBlank(formData.get("lakeDistrictId"))) {
            Long ldId = Long.valueOf(formData.get("lakeDistrictId"));
            pc.setLakeDistrict(ldService.getById(ldId));
        }

        pc.setManagementUnit(resourceLoadService.findRefCode(
            RefCodeType.MANAGEMENT_UNIT.getName(), CommonUtils.getStringValue(formData.get("MANAGEMENT_UNIT"))));

        pc.setGridNumber(CommonUtils.getDoubleValue(formData.get("grid_number")));
        pc.setGridNumberEst(CommonUtils.getDoubleValue(formData.get("grid_number_est")));
        pc.setGearType(resourceLoadService.findRefCode(
            RefCodeType.GEAR.getName(), CommonUtils.getStringValue(formData.get("GEAR"))));
        pc.setMeshSize(CommonUtils.getDoubleValue(formData.get("meshSize")));
        pc.setMeshSizeMax(CommonUtils.getDoubleValue(formData.get("maxMesh")));
        pc.setMeshSizeMin(CommonUtils.getDoubleValue(formData.get("minMesh")));
        pc.setMeshSizeAvg(CommonUtils.getDoubleValue(formData.get("avgMesh")));
        pc.setWaterDepth(CommonUtils.getDoubleValue(formData.get("waterDepth")));
        pc.setDepthMax(CommonUtils.getDoubleValue(formData.get("maxDepth")));
        pc.setDepthMin(CommonUtils.getDoubleValue(formData.get("minDepth")));
        pc.setDepthAvg(CommonUtils.getDoubleValue(formData.get("avgDepth")));

        if (StringUtils.isBlank(formData.get("sampleCode"))) {
            pc.setSample(SampleUtils.getSampleFromFormData(
                formData, null, null, pcRepository.getNextSequentialNumber(), PC_PREFIX, messages));
        }

        CommonUtils.getLogger().debug(
            "{}:buildPcFromFormData return built ParasiticCollection({})", SERVICE_NAME, pc.getId());
        return pc;
    }

    private List<ParasiticAttachment> getAttachmentsFromFormData(ParasiticCollection pc,
                                                                 Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:getAttachmentsFromFormData with ParasiticCollection({}) and formData {}",
            SERVICE_NAME, pc.getId(), formData.toString());
        PCValidator.validateAttachmentsFormdata(formData, messages);

        List<ParasiticAttachment> attachments = new ArrayList<>();
        String numOfString = formData.get("numOfAttachments");
        if (StringUtils.isBlank(numOfString)) {
            CommonUtils.getLogger().debug(
                "{}:getAttachmentsFromFormData return empty list of ParasiticAttachment", SERVICE_NAME);
            return attachments;
        }
        CommonUtils.getLogger().debug(
            "{}:getAttachmentsFromFormData got numOfAttachments as ({})", SERVICE_NAME, numOfString);

        String idNumName = "idNumber";
        String hostTypeName = "hostType";
        String hostSpecieName = "hostSpecie";

        String seaLampSampledName = "seaLampSampled";
        String silverLampSampledName = "silverLampSampled";
        String totalAttachedName = "totalAttached";
        String attachmentIdName = "attachmentId";

        String indiSpecieName = "indiSpecie";
        String indiLengthName = "length_";
        String indiWeightName = "weight_";
        String indiSexName = "sex_";
        String indiMaturityName = "maturity_";
        String indiGutConditionName = "gutCondition_";
        String indiGutFullnessName = "gutFullness_";
        String indiGutContentsName = "gutContents_";
        String indiSpecimenName = "specimenState_";
        String indiRecaptureName = "optradioRecap_";
        String individualIdName = "individualId";

        String attachmentName = "attachment";
        String numOfindividualsName = "_numOfIndi";

        CommonUtils.getLogger().debug("{}:getAttachmentsFromFormData walk through attachments", SERVICE_NAME);
        int numOfAttachments = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfAttachments; i++) {
            if (StringUtils.isBlank(formData.get(idNumName + i))) {
                CommonUtils.getLogger().debug(
                    "{}:getAttachmentsFromFormData skip ({}) due to related {} is empty", SERVICE_NAME, i, idNumName);
                continue;
            }
            ParasiticAttachment attachment = new ParasiticAttachment();
            attachment.setIdNumber(CommonUtils.getIntegerValue(formData.get(idNumName + i)));
            attachment.setLampreysattachedto(resourceLoadService.findRefCode(
                RefCodeType.HOST_TYPE.getName(), CommonUtils.getStringValue(formData.get(hostTypeName + i))));
            attachment.setHostSpecies(CommonUtils.getObjectCode(formData.get(hostSpecieName + i)));

            attachment.setSeaLampreySampled(CommonUtils.getIntegerValue(formData.get(seaLampSampledName + i)));
            attachment.setSilverLampreySampled(CommonUtils.getIntegerValue(formData.get(silverLampSampledName + i)));
            attachment.setTotalAttached(CommonUtils.getIntegerValue(formData.get(totalAttachedName + i)));

            Long attachmentId = CommonUtils.getLongValue(formData.get(attachmentIdName + i));
            if (attachmentId != null) {
                attachment.setId(attachmentId);
            }

            List<FishIndividual> fishIndividuals = new ArrayList<>();
            int numOfIndividuals = CommonUtils.getIntValue(formData.get(attachmentName + i + numOfindividualsName));
            for (int j = 0; j < numOfIndividuals; j++) {
                FishIndividual fishIndividual = new FishIndividual();
                fishIndividual.setSpecieCode(CommonUtils.getObjectCode(
                    formData.get(idNumName + i + "_" + indiSpecieName + j)));
                fishIndividual.setIndividualLength(CommonUtils.getDoubleValue(formData.get(indiLengthName + i + j)));
                fishIndividual.setIndividualWeight(CommonUtils.getDoubleValue(formData.get(indiWeightName + i + j)));
                fishIndividual.setIndividualSex(resourceLoadService.findRefCode(
                    RefCodeType.FISH_SEX.getName(), CommonUtils.getStringValue(formData.get(indiSexName + i + j))));
                fishIndividual.setIndividualMaturity(resourceLoadService.findRefCode(RefCodeType.MATURITY.getName(),
                    CommonUtils.getStringValue(formData.get(indiMaturityName + i + j))));
                fishIndividual.setConditionOfGut(resourceLoadService.findRefCode(RefCodeType.GUT_CONDITION.getName(),
                    CommonUtils.getStringValue(formData.get(indiGutConditionName + i + j))));
                fishIndividual.setFullnessOfGut(resourceLoadService.findRefCode(RefCodeType.GUT_FULLNESS.getName(),
                    CommonUtils.getStringValue(formData.get(indiGutFullnessName + i + j))));
                fishIndividual.setContentsOfGut(resourceLoadService.findRefCode(RefCodeType.GUT_CONTENTS.getName(),
                    CommonUtils.getStringValue(formData.get(indiGutContentsName + i + j))));
                fishIndividual.setSpecimenState(resourceLoadService.findRefCode(RefCodeType.SPECIMEN_STATE.getName(),
                    CommonUtils.getStringValue(formData.get(indiSpecimenName + i + j))));
                fishIndividual.setRecapture(
                    formData.get(indiRecaptureName + i + j).equalsIgnoreCase("yes"));
                Long indiId = CommonUtils.getLongValue(formData.get(idNumName + i + "_" + individualIdName + j));
                if (indiId != null) {
                    fishIndividual.setId(indiId);
                }
                fishIndividual.setParasiticAttachment(attachment);
                fishIndividuals.add(fishIndividual);
            }
            attachment.setParasiticCollection(pc);
            attachment.setFishIndividuals(fishIndividuals);

            attachments.add(attachment);
        }

        CommonUtils.getLogger().debug(
            "{}:getAttachmentsFromFormData return {} of ParasiticAttachments", SERVICE_NAME, attachments.size());
        return attachments;
    }

    public ParasiticCollection updatePcFromFormData(String id, Map<String, String> formData) throws ParseException {
        CommonUtils.getLogger().debug("{}:updatePcFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        ParasiticCollection pc = getById(CommonUtils.getLongFromString(id, "ID of ParasiticCollection"));
        ParasiticCollection updatedPc = buildPcFromFormData(pc, formData);
        return save(updatedPc);
    }

    public boolean deleteById(Long id) {
        CommonUtils.getLogger().debug("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            pcRepository.deleteById(id);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            CommonUtils.getLogger().error("{}:deleteById could not delete ParasiticCollection({}) due to: {}",
                SERVICE_NAME, id, ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_PARASITICCOLLECTION_ERROR.getName()), ex);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById could not delete ParasiticCollection({}) due to: {}",
                SERVICE_NAME, id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug("{}:deleteById completed successfully for id({})", SERVICE_NAME, id);
        return true;
    }
}
