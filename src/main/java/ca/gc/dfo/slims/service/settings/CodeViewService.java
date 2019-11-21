package ca.gc.dfo.slims.service.settings;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.CodeView;
import ca.gc.dfo.slims.domain.repository.settings.CodeViewRepository;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZHUY
 *
 */
@Service
public class CodeViewService {
    private static final String SERVICE_NAME = "CodeViewService";

    @Autowired
    private CodeViewRepository    codeViewRepository;
    @Autowired
    private AppMessages            messages;

    public List<CodeView> getAll() {
        return CommonUtils.getReturnList(codeViewRepository.findAll());
    }

    public CodeView getById(Long id) {
        return CommonUtils.getIfPresent(codeViewRepository.findById(id),
            SERVICE_NAME + "getById(CodeView)", id, messages);
    }

    public CodeView save(CodeView codeView) {
        CommonUtils.getLogger().debug(
            "{}:save with CodeView({} {})", SERVICE_NAME, codeView.getId(), codeView.getShowText());
        CodeView returnCodeView = null;
        try {
            codeView.setCodeName(codeView.getCodeName().toUpperCase());
            returnCodeView = codeViewRepository.save(codeView);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save CodeView({}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME, codeView.getId(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_CODEVIEW_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save CodeView({}) due to: {}",
                SERVICE_NAME, codeView.getId(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_CODEVIEW_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug("{}:save return saved CodeView({})", SERVICE_NAME, returnCodeView.getId());
        return returnCodeView;
    }

    public ResponseDTO<CodeView> updateCodeView(String id, CodeView updatedCodeView) {
        CommonUtils.getLogger().debug(
            "{}:updateCodeView with id({}) and CodeView({})", SERVICE_NAME, updatedCodeView.getShowText());
        CodeView codeView = getById(CommonUtils.getLongFromString(id, "ID of CodeView"));
        codeView.setDescriptionEn(updatedCodeView.getDescriptionEn());
        codeView.setDescriptionFr(updatedCodeView.getDescriptionFr());

        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_CODEVIEW_SUCCESS.getName()),
            save(codeView));
    }
}
