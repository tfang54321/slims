/**
 *
 */
package ca.gc.dfo.slims.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ca.gc.dfo.slims.dto.audit.CommonAuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;


import ca.gc.dfo.slims.service.audit.UserAuditService;


/**
 * @author FANGW
 *
 */
@Service
public class AuditServiceImpl implements UserAuditService {


	@Autowired
	private EntityManager entityManager;


	@Override
	@Transactional(readOnly = true)
	public List<CommonAuditDto> getAuditReportsForAllUsers(MessageSource messageSource, Locale locale) {


		String queryAuditForUsers = "SELECT "
				+ " userAudit.rev ,"
				+ "userAudit.revtype ,"
				+ "userAudit.user_name ,"
				+ " userAudit.nt_principal_mod ,"
				+ "userAudit.role_id , "
				+ "userAudit.user_role_mod ,"
				+ "revInfo.created_date ,"
				+ "revInfo.user_Account,"
				+ "role.roleName,"
				+ "userAudit.user_id,"
				+ "userAudit.activated,"
				+"userAudit.activated_mod "
				+ " FROM SLIMS_SLIMS_USERS_AUD userAudit,SLIMS_REVINFO revInfo,SLIMS_USER_ROLES role "
				+ "WHERE userAudit.rev= revInfo.id  and userAudit.role_id=role.role_id order by revInfo.id desc";
		Query query = entityManager.createNativeQuery(queryAuditForUsers);
		return  this.parserAuditRecords(query, messageSource, locale);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommonAuditDto> getUserAuditReport(Long userId, MessageSource messageSource,Locale locale) {

		String queryAuditForSingleUser = "SELECT "
				+ " userAudit.rev ,"
				+ "userAudit.revtype ,"
				+ "userAudit.user_name ,"
				+ " userAudit.nt_principal_mod ,"
				+ "userAudit.role_id , "
				+ "userAudit.user_role_mod ,"
				+ "revInfo.created_date ,"
				+ "revInfo.user_Account,"
				+ "role.roleName,"
				+ "userAudit.user_id ,"
				+ "userAudit.activated,"
				+ "userAudit.activated_mod"
				+ " FROM SLIMS_SLIMS_USERS_AUD userAudit,SLIMS_REVINFO revInfo,SLIMS_USER_ROLES role "
				+ "WHERE userAudit.user_id = :useId and userAudit.rev= revInfo.id  and userAudit.role_id=role.role_id order by revInfo.id desc";
		Query query = entityManager.createNativeQuery(queryAuditForSingleUser);
		query.setParameter("useId", userId);
		return  this.parserAuditRecords(query, messageSource, locale);
	}

	private List<CommonAuditDto>  parserAuditRecords(Query query,MessageSource messageSource, Locale locale) {

		List<Object[]> auditList = query.getResultList();
		List<CommonAuditDto> auditDTOs = new ArrayList<CommonAuditDto>();
		CommonAuditDto auditDto = null;

		String actionName = null;
		if(!CollectionUtils.isEmpty(auditList)) {
			for(Object[] temp :auditList) {

				if (((BigDecimal)temp[1]).equals(new BigDecimal(0))) {
					actionName = messageSource.getMessage("i18n.audit.report.action.add", null, locale);
				}
				if (((BigDecimal)temp[5]).equals(new BigDecimal(1)))  {//role modified
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					auditDto.setNewValue(String.valueOf( temp[8]));
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[7]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.role", null, locale));
					auditDto.setUserID(String.valueOf( temp[9]));
					auditDTOs.add(auditDto);
				}
				if (((BigDecimal)temp[3]).equals(new BigDecimal(1))) {//user name modified
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					auditDto.setNewValue(String.valueOf( temp[2]));
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[7]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.name", null, locale));
					auditDto.setUserID(String.valueOf( temp[9]));
					auditDTOs.add(auditDto);
				}
				
				if (((BigDecimal)temp[11]).equals(new BigDecimal(1))) {//user activate status
					auditDto = new CommonAuditDto();
					auditDto.setRev(String.valueOf( temp[0]));
					if("1".equals(String.valueOf( temp[10]))){
					auditDto.setNewValue(messageSource.getMessage("i18n.user.status.activate", null, locale));
					}else {
						auditDto.setNewValue(messageSource.getMessage("i18n.user.status.inactivate", null, locale));
					}
					auditDto.setActionName(actionName != null? actionName: messageSource.getMessage("i18n.audit.report.action.update", null, locale));
					auditDto.setAuditDate(StringUtils.substringBefore(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setAuditTime(StringUtils.substringAfter(this.convertToCurrentTimeZone(String.valueOf( temp[6]).trim())," ").trim());
					auditDto.setUserName(String.valueOf(temp[7]));
					auditDto.setElementName_audit(messageSource.getMessage("i18n.audit.report.user.status", null, locale));
					auditDto.setUserID(String.valueOf( temp[9]));
					auditDTOs.add(auditDto);
				}
			}
		}
		return auditDTOs;
	}



	public String convertToCurrentTimeZone(String date) {
		String converted_date = "";
		try {

			
			//commoent out for now
//			DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
//			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//			Date tempDate = utcFormat.parse(date);
//
//			DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
//			currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
			
			DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
			
			Date tempDate = currentTFormat.parse(date);

			converted_date =  currentTFormat.format(tempDate);
		}catch (Exception e){ e.printStackTrace();}

		return converted_date;
	}


	//get the current time zone

	public String getCurrentTimeZone(){
		TimeZone tz = Calendar.getInstance().getTimeZone();
		//		System.out.println(tz.getDisplayName());
		return tz.getID();
	}
	@Override
	@Transactional(readOnly = true)
	public List<Long> getAllUserID() {

		String queryAuditForSingleUser = "SELECT distinct userAudit.user_id FROM slims_v21.SLIMS_SLIMS_USERS_AUD userAudit order by userAudit.user_id asc  ";

		Query query = entityManager.createNativeQuery(queryAuditForSingleUser);
		List<Object[]> allUserIds = query.getResultList();


		List<Long> userIds = null;
		if(!CollectionUtils.isEmpty(allUserIds)) {

			userIds = new ArrayList<Long>(allUserIds.size());
			for(Object  temp :allUserIds) {
				userIds.add(Long.valueOf(String.valueOf(temp)));
			}
		}
		return userIds;
	}

}
