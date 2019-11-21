package ca.gc.dfo.slims.api.settings;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.CodeView;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.common.ReferCodeService;
import ca.gc.dfo.slims.service.settings.CodeViewService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_API_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_API_PATH;

/**
 * @author ZHUY
 *
 */
@RestController
@RequestMapping(value = { ENG_API_PATH, FRA_API_PATH })
public class CodeTableRestController {
    private static final String REST_API_NAME = "CodeTableRestController";

    @Autowired
    private CodeViewService        codeViewService;
    @Autowired
    private ReferCodeService    referCodeService;
    @Autowired
    private AppMessages            messages;

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @GetMapping(value = "/codetables")
    public List<CodeView> getAllCodeViews()
    {
        return codeViewService.getAll();
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @PostMapping(value = "/codetables/add")
    public ResponseDTO<CodeView> saveCodeView(@Valid @RequestBody CodeView codeView, Errors errors) {
        CommonUtils.getLogger().info("{}:saveCodeView with CodeView ({}) and Errors count ({})",
            REST_API_NAME, codeView.toString(), errors.getErrorCount());
        ValidationUtils.validateErrors(errors, REST_API_NAME + ":saveCodeView");
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_CODEVIEW_SUCCESS.getName()),
            codeViewService.save(codeView));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @PutMapping(value = "/codetables/{id}")
    public ResponseDTO<CodeView> updateCodeView(@PathVariable(value = "id") String id,
                                                @RequestBody CodeView updatedCodeView) {
        CommonUtils.getLogger().info(
            "{}:updateCodeView with id({}) and CodeView ({})", REST_API_NAME, id, updatedCodeView.toString());
        return codeViewService.updateCodeView(id, updatedCodeView);
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @GetMapping(value = "/codetables/{id}")
    public ResponseDTO<CodeView> getCodeViewById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getCodeViewById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            codeViewService.getById(CommonUtils.getLongFromString(id, "ID of CodeView")));
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @GetMapping(value = "/refcodes/{codeType}")
    public List<ReferCode> getRefCodesByCodeType(@PathVariable("codeType") String codeType) {
        CommonUtils.getLogger().debug("{}:getRefCodesByCodeType with codeType({})", REST_API_NAME, codeType);
        return referCodeService.getReferCodeByCodeType(codeType);
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @GetMapping(value = "/refcode/{id}")
    public ResponseDTO<ReferCode> getRefCodeById(@PathVariable("id") String id) {
        CommonUtils.getLogger().debug("{}:getRefCodeById with id({})", REST_API_NAME, id);
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            null,
            referCodeService.getById(CommonUtils.getLongFromString(id, "ID of ReferCode")));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @PostMapping(value = "/refcode/add/{codeType}")
    public ResponseDTO<ReferCode> saveRefcode(@PathVariable(value = "codeType") String codeType,
                                              @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:saveRefcode with codeType({}) and formData {}",
            REST_API_NAME, codeType, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.SAVE_REFCODE_SUCCESS.getName()),
            referCodeService.saveReferCodeFromFormData(codeType, formData));
    }

    @PreAuthorize(SecurityHelper.EL_MODIFY_CODE_TABLES)
    @PutMapping(value = "/refcode/{id}")
    public ResponseDTO<ReferCode> updateRefcode(@PathVariable(value = "id") String id,
                                                @RequestParam Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:updateRefcode with id({}) and formData {}",
            REST_API_NAME, id, formData.toString());
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_REFCODE_SUCCESS.getName()),
            referCodeService.updateRefCodeFromFormData(id, formData));
    }
}