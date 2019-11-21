package ca.gc.dfo.slims.utility;

import ca.gc.dfo.slims.constants.ValidationMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;

public class ExceptionUtils {
    public static void logAndRethrowParseException(String errorMessage,
                                                   ParseException parseException) throws ParseException {
        CommonUtils.getLogger().error(errorMessage, parseException);
        throw parseException;
    }

    public static void logAndRethrowResponseStatusException(String errorMessage,
                                                            ResponseStatusException responseStatusException) {
        CommonUtils.getLogger().error(errorMessage, responseStatusException);
        throw responseStatusException;
    }

    public static void throwResponseException(HttpStatus httpStatus, String msg) {
        throw new ResponseStatusException(httpStatus, msg);
    }

    public static void throwResponseException(HttpStatus httpStatus, String msg, Throwable throwable) {
        throw new ResponseStatusException(httpStatus, msg, throwable);
    }

    public static void throwBadRequestResponseException(AppMessages appMessages,
                                                        ValidationMessages validationMessages) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, appMessages.get(validationMessages.getName()));
    }
}
