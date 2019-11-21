package ca.gc.dfo.slims.service.audit;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import ca.gc.dfo.slims.dto.audit.CommonAuditDto;



/**
 * @author FANGW
 *
 */
@Service
public interface UserAuditService {
		public List<CommonAuditDto> getAuditReportsForAllUsers(MessageSource messageSource, Locale locale);
		public List<CommonAuditDto> getUserAuditReport(Long userId, MessageSource messageSource, Locale locale);
		public List<Long> getAllUserID();
	}
