package ca.gc.dfo.slims.service.common;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import ca.gc.dfo.slims.domain.repository.common.ReferCodeRepository;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReferCodeService {
    private static final String SERVICE_NAME = "ReferCodeService";

    @Autowired
    private ReferCodeRepository        referCodeRepository;
    @Autowired
    private ResourceLoadService        resourceLoadService;
    @Autowired
    private AppMessages                messages;

    public List<ReferCode> getAll() {
        return CommonUtils.getReturnListFromIterable(referCodeRepository.findAll());
    }

    public ReferCode getById(Long id) {
        return CommonUtils.getIfPresent(
            referCodeRepository.findById(id), SERVICE_NAME + "getById(ReferCode)", id, messages);
    }

    public List<ReferCode> getReferCodeByCodeType(String codeType) {
        return referCodeRepository.findAllByCodeType(codeType);
    }

    public ReferCode save(ReferCode referCode) {
        CommonUtils.getLogger().debug("{}:save with ReferCode({})", SERVICE_NAME, referCode.getId());
        ReferCode returnRefCode = null;
        try {
            returnRefCode = referCodeRepository.save(referCode);
            resourceLoadService.reloadRefCodes(referCode.getCodeType());
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save would not save ReferCode({}) due to DataIntegrityViolationException",
                SERVICE_NAME, referCode.getShowText(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_REFCODE_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error(
                "{}:save would not save ReferCode({}) due to {}",
                SERVICE_NAME, referCode.getShowText(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_LAKE_ERROR.getName()), ex);
        }        
        return returnRefCode;
    }

    public ReferCode saveReferCodeFromFormData(String codeType, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:saveReferCodeFromFormData with codeType({}) and formDate {}",
            SERVICE_NAME, codeType, formData.toString());
        ReferCode referCode = buildRefCodeFromFormData(codeType, formData);
        return save(referCode);
    }

    private ReferCode buildRefCodeFromFormData(String codeType, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildRefCodeFromFormData with codeType({}) and formDate {}",
            SERVICE_NAME, codeType, formData.toString());
        //RefCodeValidator.validateRefCodeForm(formData, messages);

        ReferCode referCode = new ReferCode();
        referCode.setCodeType(codeType);
        referCode.setCodeValue(CommonUtils.getStringValue(formData.get("codeValue")));
        referCode.setCodeMeaningEn(CommonUtils.getStringValue(formData.get("desEn")));
        referCode.setCodeMeaningFr(CommonUtils.getStringValue(formData.get("desFr")));
        referCode.setCodeAbbreviation(CommonUtils.getStringValue(formData.get("codeAbb")));
        return referCode;
    }

    public ReferCode updateRefCodeFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateRefCodeFromFormData with id({}) and formDate {}",
            SERVICE_NAME, id, formData.toString());
        long referCodeId = CommonUtils.getLongFromString(id, "ID of ReferCode");
        ReferCode referCode = getById(referCodeId);
        ReferCode updatedReferCode = buildRefCodeFromFormData(null, formData);
        updatedReferCode.setId(referCodeId);
        updatedReferCode.setCodeType(referCode.getCodeType());
        return save(updatedReferCode);
    }
}
