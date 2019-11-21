package ca.gc.dfo.slims.controller.parasiticcollection;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.parasiticcollection.ParasiticCollection;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.service.parasiticcollection.ParasiticCollectionService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_PARASITIC_COLLECTION_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_PARASITIC_COLLECTION_PATH;

/**
 * @author ZHUY
 *
 */
@Controller
@RequestMapping({ ENG_PARASITIC_COLLECTION_PATH, FRA_PARASITIC_COLLECTION_PATH })
public class PCController {
    private static final String CONTROLLER_NAME = "PCController";

    @Autowired
    private ParasiticCollectionService    pcService;
    @Autowired
    private ResourceLoadService           resourceLoadService;
    @Autowired
    private AppMessages                   messages;

    @PreAuthorize(SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS)
    @GetMapping
    public String pcList(Model model) {
        CommonUtils.getLogger().debug("{}:pcList", CONTROLLER_NAME);
        model.addAttribute("pcYear", pcService.getYearList());
        return "parasiticcollection/list";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS)
    @GetMapping(value = "/main")
    public String addOrEditPage(@RequestParam(name = "pcId", required = false) String pcId, Model model) {
        CommonUtils.getLogger().debug("{}:addOrEditPage with pcId({})", CONTROLLER_NAME, pcId);

        if (CommonUtils.isValidId(pcId)) {
            model.addAttribute("referpc", getParasiticCollectionById(pcId));
        }

        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        model.addAttribute("lakeDistrictslist", resourceLoadService.getLdsList());
        model.addAttribute("allFisherNames", pcService.getAllFisherNames());
        return "parasiticcollection/edit";
    }

    @PreAuthorize(SecurityHelper.EL_VIEW_PARASITIC_COLLECTIONS)
    @GetMapping(value = "/attachments")
    public String mainPage(@RequestParam(name = "pcId") String pcId, Model model) {
        CommonUtils.getLogger().debug("{}:mainPage with pcId({})", CONTROLLER_NAME, pcId);

        if (!CommonUtils.isValidId(pcId)) {
            CommonUtils.getLogger().error(
                "{}:mainPage pcId({}) is invalid, throw ResponseStatusException", CONTROLLER_NAME, pcId);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ValidationMessages.INVALID_ID.getName()));
        }

        model.addAttribute("referpc", getParasiticCollectionById(pcId));
        model.addAttribute("refcodelist", resourceLoadService.getRefcodesMap());
        model.addAttribute("allspeciecodeslist", resourceLoadService.getSpecieCodes());

        return "parasiticcollection/attachments";
    }

    private ParasiticCollection getParasiticCollectionById(String id) {
        return pcService.getById(CommonUtils.getLongFromString(id, "ID of ParasiticCollection"));
    }
}
