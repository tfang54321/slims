package ca.gc.dfo.slims.controller.settings;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.repository.common.ReferCodeRepository;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_CODE_TABLES_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_CODE_TABLES_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping(value = { ENG_CODE_TABLES_PATH, FRA_CODE_TABLES_PATH })
public class RefCodeController {
    private static final String CONTROLLER_NAME = "RefCodeController";

    @Autowired
    ReferCodeRepository        referCodeRepository;

    @SuppressWarnings("unused")
    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @GetMapping
    public String allCodeViewList(Model model) {
        CommonUtils.getLogger().debug("{}:allCodeViewList", CONTROLLER_NAME);
        return "settings/codetable";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_CODE_TABLES)
    @GetMapping(value = "/detail")
    public String refcodePage(@RequestParam(name = "codeName") String codeName, Model model) {
        CommonUtils.getLogger().debug("{}:refcodePage", CONTROLLER_NAME);
        model.addAttribute("referCodeType", codeName);
        return "settings/editRefCode";
    }
}