package ca.gc.dfo.slims.utility;

import java.util.Locale;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author ZHUY
 *
 */
@Component
public class AppMessages {
    @Autowired
    private MessageSource messageSource;

    public String get(String code) {
        String requestPath = ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        return messageSource.getMessage(
            code,
            null,
            !Strings.isBlank(requestPath) && requestPath.contains("/fra/") ? Locale.FRENCH : Locale.ENGLISH);
    }
}
