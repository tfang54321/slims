package ca.gc.dfo.slims.controller.home;

import javax.servlet.http.HttpServletRequest;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.user.SlimsUser;
import ca.gc.dfo.slims.dto.AccountDTO;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.spring_commons.commons_offline_wet.annotations.Offline;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.PathLocaleChangeInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.gc.dfo.spring_commons.commons_offline_wet.controllers.AbstractTemplateController;

import static ca.gc.dfo.slims.constants.PagePathes.*;

/**
 * @author ZHUY
 *
 */
@Controller
public class HomeController extends AbstractTemplateController {
    private static final String CONTROLLER_NAME = "HomeController";

    @Offline
    @RequestMapping(
        method = RequestMethod.GET,
        value = {ENG_SETTINGS_PATH, FRA_SETTINGS_PATH, ENG_USER_MANAGEMENT_PATH, FRA_USER_MANAGEMENT_PATH})
    public String specificPanelNodes(ModelMap model, HttpServletRequest request) {
        CommonUtils.getLogger().debug(
            "{}:specificPanelNodes with request({})", CONTROLLER_NAME, request.getServletPath());
        return panelNodes(model, request);
    }

    @RequestMapping(
        method = RequestMethod.GET,
        value = {
            ENG_LOCATIONS_PATH, FRA_LOCATIONS_PATH, ENG_RESEARCH_AND_ACTIVITIES_PATH, FRA_RESEARCH_AND_ACTIVITIES_PATH
        })
    public String specificPanelNodesOnlineOnly(ModelMap model, HttpServletRequest request) {
        CommonUtils.getLogger().debug(
            "{}:specificPanelNodesOnlineOnly with request({})", CONTROLLER_NAME, request.getServletPath());
        return panelNodes(model, request);
    }

    @RequestMapping(value = {ENG_MYACCOUNT_PATH, FRA_MYACCOUNT_PATH}, method = RequestMethod.GET)
    public String getMyAccount(ModelMap model) {
        CommonUtils.getLogger().debug("{}:getMyAccount with request({})", CONTROLLER_NAME);
        AccountDTO account = new AccountDTO();
        SlimsUser user = (SlimsUser) SecurityHelper.getUserDetails();
        account.setUsername(user.getUsername());
        account.setFullname(user.getFullname());
        account.setRole(user.getUserRole().getRolename());
        model.addAttribute("account", account);
        return "account";
    }

    @RequestMapping(value = {ENG_HELP_PATH, FRA_HELP_PATH}, method = RequestMethod.GET)
    public String getHelp()
    {
        return "help";
    }
}
