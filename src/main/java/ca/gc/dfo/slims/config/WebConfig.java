package ca.gc.dfo.slims.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import ca.gc.dfo.spring_commons.commons_offline_wet.configuration.WebConfigAdapter;
import ca.gc.dfo.spring_commons.commons_offline_wet.i18n.OfflineWETResourceBundleMessageSource;

@Configuration
public class WebConfig extends WebConfigAdapter
{
	@Override
	public MessageSource messageSource() {
		OfflineWETResourceBundleMessageSource  messageSource = 
	                                               new OfflineWETResourceBundleMessageSource();
	    messageSource.setBasenames("messages/business/messages", 
	                               "messages/validation/messages",
	                               "messages/view/messages");
	    messageSource.setDefaultEncoding("UTF-8");

	    return messageSource;
	}
}
