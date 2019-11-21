package ca.gc.dfo.slims.controller.settings;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.user.SlimsUser;
import ca.gc.dfo.slims.domain.entity.user.UserRole;
import ca.gc.dfo.slims.dto.UserManagementDTO;
import ca.gc.dfo.slims.dto.audit.CommonAuditDto;
import ca.gc.dfo.slims.dto.user.UserListDTO;
import ca.gc.dfo.slims.service.UserService;
import ca.gc.dfo.slims.service.audit.UserAuditService;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.GeneratePdfReport;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_ACTIVATE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_ALL_REPORT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_CREATE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_DATA_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_EDIT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_LIST_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_ROLE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.ENG_USER_MANAGEMENT_SINGLE_REPORT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_ACTIVATE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_ALL_REPORT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_CREATE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_DATA_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_EDIT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_LIST_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_ROLE_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.FRA_USER_MANAGEMENT_SINGLE_REPORT_PATH;
import static ca.gc.dfo.slims.constants.PagePathes.USER_MANAGEMENT_LIST_PATH;

@Controller
public class UserManagementController implements MessageSourceAware, InitializingBean {
	private static final String CONTROLLER_NAME = "UserManagementController";

	@Autowired
	private UserService userService;
	@Autowired
	private UserAuditService userAuditService;

	@Value("${e-access.username-suffix:@ent.dfo-mpo.ca}")
	private String usernameSuffix;

	private List<UserRole> roles;
	private MessageSource messageSource;

	public void setMessageSource(MessageSource messageSource) {
		CommonUtils.getLogger().debug("{}:setMessageSource", CONTROLLER_NAME);
		this.messageSource = messageSource;
	}

	@Override
	public void afterPropertiesSet() {
		List<UserRole> allRoles = userService.getAllRoles();
		CommonUtils.getLogger().debug(
				"{}:afterPropertiesSet got ({}) UserRoles from userService", CONTROLLER_NAME, allRoles.size());
		this.roles = new ArrayList<>();
		UserRole copy;
		for (UserRole role : allRoles) {
			copy = new UserRole();
			BeanUtils.copyProperties(role, copy);
			this.roles.add(copy);
		}
		CommonUtils.getLogger().debug(
				"{}:afterPropertiesSet got ({}) Got {} UserRoles", CONTROLLER_NAME, roles.size());
	}

	@ModelAttribute
	public void populateCommonModel(ModelMap model) {
		CommonUtils.getLogger().debug("{}:populateCommonModel", CONTROLLER_NAME);
		model.addAttribute("roles", this.roles);
	}

	@PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_LIST_PATH, FRA_USER_MANAGEMENT_LIST_PATH}, method = RequestMethod.GET)
	public String userManagementList() {
		CommonUtils.getLogger().debug("{}:userManagementList", CONTROLLER_NAME);
		return "settings/userManagement/list";
	}

	@PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_DATA_PATH, FRA_USER_MANAGEMENT_DATA_PATH}, method = RequestMethod.GET)
	public @ResponseBody
	UserListDTO userManagementData() {
		CommonUtils.getLogger().debug("{}:userManagementList", CONTROLLER_NAME);
		return userService.getUserList();
	}

	@PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
	@RequestMapping(
			value = {ENG_USER_MANAGEMENT_CREATE_PATH, FRA_USER_MANAGEMENT_CREATE_PATH},
			method = RequestMethod.GET)
	public String userManagementCreate(ModelMap model, Locale locale) {
		CommonUtils.getLogger().debug("{}:userManagementCreate", CONTROLLER_NAME);
		UserManagementDTO uaDTO = new UserManagementDTO();
		uaDTO.setActiveFlag(1);
		model.addAttribute("userManagementForm", uaDTO);
		model.addAttribute("infoMessage",
				messageSource.getMessage("page.user_mgmt.read_only_info", null, locale));
		return "settings/userManagement/create";
	}

	@PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
	@RequestMapping(
			value = {ENG_USER_MANAGEMENT_CREATE_PATH, FRA_USER_MANAGEMENT_CREATE_PATH},
			method = RequestMethod.POST)
	public String userManagementCreatePost(UserManagementDTO uaDTO,
			ModelMap model,
			RedirectAttributes redirectAttrs,
			Locale locale) {
		CommonUtils.getLogger().debug(
				"{}:userManagementCreatePost with UserManagementDTO({})", CONTROLLER_NAME, uaDTO.toString());
		String userName = validateUserName(uaDTO.getNtPrincipal());
		if (userName.equals("Invalid")) {
			CommonUtils.getLogger().info("{}:userManagementCreatePost found userName is Invalid", CONTROLLER_NAME);
			model.addAttribute("errorMessage", messageSource.getMessage(
					"page.user_mgmt.invalid_username.format", new Object[]{usernameSuffix}, locale));
			model.addAttribute("userManagementForm", uaDTO);
			return "settings/userManagement/create";
		}

		if (userService.findUserByUsername(userName) != null) {
			CommonUtils.getLogger().info("{}:userManagementCreatePost found user ({})", CONTROLLER_NAME, userName);
			model.addAttribute("errorMessage", messageSource.getMessage(
					"page.user_mgmt.invalid_username.exists", new Object[]{userName}, locale));
			model.addAttribute("userManagementForm", uaDTO);
			return "settings/userManagement/create";
		}

		uaDTO.setNtPrincipal(userName);
		SlimsUser user = userService.addUserForm(uaDTO);
		CommonUtils.getLogger().debug("{}:userManagementCreatePost added user({}) to userService",
				CONTROLLER_NAME, uaDTO.toString());

		redirectAttrs.addFlashAttribute("successMessage", messageSource.getMessage(
				"page.user_mgmt.add_success", new Object[]{user.getNtPrincipal()}, locale));
		String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
		return "redirect:/" + pageLang + USER_MANAGEMENT_LIST_PATH;
	}

	@PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_EDIT_PATH, FRA_USER_MANAGEMENT_EDIT_PATH}, method = RequestMethod.GET)
	public String userManagementEdit(@RequestParam Long id, ModelMap model, Locale locale) {
		CommonUtils.getLogger().debug("{}:userManagementEdit with id({})", CONTROLLER_NAME, id);
		UserManagementDTO uaDTO = new UserManagementDTO();
		SlimsUser user = userService.getUserById(id);
		BeanUtils.copyProperties(user, uaDTO);
		uaDTO.setActiveFlag(user.getActivated() != null && user.getActivated() ? 1 : 0);
		uaDTO.setRole(user.getUserRole().getRoleId().intValue());
		if (uaDTO.getFirstName() == null || uaDTO.getFirstName().equals("")) {
			model.addAttribute("infoMessage", messageSource.getMessage(
					"page.user_mgmt.read_only_info", null, locale));
		}
		model.addAttribute("userManagementForm", uaDTO);
		return "settings/userManagement/edit";
	}

	@PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_EDIT_PATH, FRA_USER_MANAGEMENT_EDIT_PATH}, method = RequestMethod.POST)
	public String userManagementEditPost(UserManagementDTO uaDTO,
			ModelMap model,
			RedirectAttributes redirectAttrs,
			Locale locale) {
		CommonUtils.getLogger().debug(
				"{}:userManagementEditPost with UserManagementDTO({})", CONTROLLER_NAME, uaDTO.toString());
		String userName = validateUserName(uaDTO.getNtPrincipal());
		if (userName.equals("Invalid")) {
			CommonUtils.getLogger().info("{}:userManagementEditPost found userName is Invalid", CONTROLLER_NAME);
			model.addAttribute("errorMessage", messageSource.getMessage(
					"page.user_mgmt.invalid_username.format", new Object[]{usernameSuffix}, locale));
			model.addAttribute("userManagementForm", uaDTO);
			return "settings/userManagement/edit";
		}

		SlimsUser userNameTest = userService.findUserByUsername(userName);
		if (userNameTest != null && !userNameTest.getUserId().equals(uaDTO.getUserId())) {
			CommonUtils.getLogger().info("{}:userManagementEditPost found user ({}) with id({})",
					CONTROLLER_NAME, userName, userNameTest.getUserId());
			model.addAttribute("errorMessage", messageSource.getMessage(
					"page.user_mgmt.invalid_username.exists", new Object[]{userName}, locale));
			model.addAttribute("userManagementForm", uaDTO);
			return "settings/userManagement/edit";
		}

		uaDTO.setNtPrincipal(userName);
		SlimsUser user = userService.editUserForm(uaDTO);
		CommonUtils.getLogger().debug("{}:userManagementEditPost edited user({}) to userService",
				CONTROLLER_NAME, uaDTO.toString());

		redirectAttrs.addFlashAttribute("successMessage", messageSource.getMessage(
				"page.user_mgmt.edit_success", new Object[]{user.getNtPrincipal()}, locale));
		String pageLang = LocaleContextHolder.getLocale().getLanguage().toLowerCase().equals("en") ? "eng" : "fra";
		return "redirect:/" + pageLang + USER_MANAGEMENT_LIST_PATH;
	}

	@PreAuthorize(SecurityHelper.EL_MODIFY_USER_MANAGEMENT)
	@RequestMapping(
			value = {ENG_USER_MANAGEMENT_ACTIVATE_PATH,FRA_USER_MANAGEMENT_ACTIVATE_PATH},
			method = RequestMethod.PATCH,
			params = {"id"},
			produces = "text/plain")
	public @ResponseBody
	String activateDeactivateUser(@RequestParam("id") Long id) {
		CommonUtils.getLogger().debug("{}:activateDeactivateUser with id({})", CONTROLLER_NAME, id);
		String status = "success";
		try {
			userService.toggleUserActive(id);
		} catch (Exception ex) {
			status = "fail";
			if (ex instanceof AccessDeniedException) {
				CommonUtils.getLogger().warn(
						"{}:activateDeactivateUser failed to toggle user({}) active flag due to access denied",
						CONTROLLER_NAME, id);
			} else {
				CommonUtils.getLogger().error(
						"{}:activateDeactivateUser while trying to toggle user({}) active flag, got exception {}",
						CONTROLLER_NAME, id, ex.getMessage(), ex);
			}
		}
		return status;
	}

	@RequestMapping(value = {ENG_USER_MANAGEMENT_ROLE_PATH, FRA_USER_MANAGEMENT_ROLE_PATH}, method = RequestMethod.GET)
	public String userManagementRoles() {
		CommonUtils.getLogger().debug("{}:userManagementRoles", CONTROLLER_NAME);
		//Roles populated in populateCommonModel()
		return "settings/userManagement/roles/list";
	}

	private String validateUserName(String userName) {
		CommonUtils.getLogger().debug("{}:validateUserName with userName({})", CONTROLLER_NAME, userName);
		if (userName.endsWith(usernameSuffix)) {
			if (userName.indexOf('@') == 0) {
				userName = "Invalid";
			}
		} else {
			userName = userName.contains("@") ? "Invalid" : userName + usernameSuffix;
		}
		return userName;
	}

	@PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_SINGLE_REPORT_PATH, FRA_USER_MANAGEMENT_SINGLE_REPORT_PATH}, method = RequestMethod.GET)
	public String searchSingleUserReport(@RequestParam String userID, ModelMap model,Locale locale)
	{

		List<CommonAuditDto> auditReport = null;
		if (!StringUtils.isBlank(userID)) {

			auditReport = userAuditService.getUserAuditReport(Long.valueOf(userID),messageSource,locale);
			if(CollectionUtils.isEmpty(auditReport)) {
				auditReport = new ArrayList<CommonAuditDto>(1);
			}
		}else {
			auditReport = new ArrayList<CommonAuditDto>(1);

		}
		model.addAttribute("userAuditReportList", auditReport);
		model.addAttribute("userID", userID);
		return "audit/singleUserAuditReport";
	}


	@PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_ALL_REPORT_PATH, FRA_USER_MANAGEMENT_ALL_REPORT_PATH}, method = RequestMethod.GET)
	public String getAllUserReport( Model model,Locale locale)
	{
		List<CommonAuditDto> auditReport_all = userAuditService.getAuditReportsForAllUsers(messageSource,locale);;
		if(CollectionUtils.isEmpty(auditReport_all)) {
			auditReport_all = new ArrayList<CommonAuditDto>(1);
		}
		model.addAttribute("allUserAuditReport", auditReport_all);
		return "audit/allUserAuditReport";
	}


	@PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH,FRA_USER_MANAGEMENT_ALL_REPORT_DOWNLOAD_PATH}, method = RequestMethod.GET,
	produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> allUsersAuditReport(Locale locale) {

		List<CommonAuditDto> auditInfo =   userAuditService.getAuditReportsForAllUsers(messageSource,locale);;

		ByteArrayInputStream bis = GeneratePdfReport.allUsersAuditReport(auditInfo,messageSource,locale);

		var headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=allUserAuditingReport.pdf");

		return ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}

	@PreAuthorize(SecurityHelper.EL_VIEW_USER_MANAGEMENT)
	@RequestMapping(value = {ENG_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH,FRA_USER_MANAGEMENT_SINGLE_REPORT_DOWNLOAD_PATH}, method = RequestMethod.GET,
	produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> singleUserAuditReport(@RequestParam String userID,Locale locale) {

		List<CommonAuditDto> auditInfo =   userAuditService.getUserAuditReport(Long.valueOf(userID),messageSource,locale);;

		ByteArrayInputStream bis = GeneratePdfReport.userAuditReport(userID,auditInfo,messageSource,locale);

		var headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=userAuditingReport.pdf");

		return ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}


}
