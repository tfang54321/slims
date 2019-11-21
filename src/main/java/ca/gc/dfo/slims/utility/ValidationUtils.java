package ca.gc.dfo.slims.utility;

import ca.gc.dfo.slims.constants.ValidationMessages;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidationUtils {
    public static final String EXPRESSION_TIME_24H = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";
    public static final String EXPRESSION_TIME_HHHMM = "^([0-9]|[0-9][0-9]|[0-9][0-9][0-9]):[0-5][0-9]$";
    public static final String EXPRESSION_NUMBER_N2DN1 = "^[0-9]{1,2}(\\.[0-9]{0,1})?$";
    public static final String EXPRESSION_NUMBER_N2DN2 = "^[0-9]{1,2}(\\.[0-9]{0,2})?$";
    public static final String EXPRESSION_NUMBER_N3DN1 = "^[0-9]{1,3}(\\.[0-9]{0,1})?$";
    public static final String EXPRESSION_NUMBER_N4DN1 = "^[0-9]{1,4}(\\.[0-9]{0,1})?$";
    public static final String EXPRESSION_NUMBER_N4DN3 = "^[0-9]{1,4}(\\.[0-9]{0,3})?$";
    public static final String EXPRESSION_NUMBER_N5DN3 = "^[0-9]{1,5}(\\.[0-9]{0,3})?$";
    public static final String EXPRESSION_NUMBER_N6DN4 = "^[0-9]{1,6}(\\.[0-9]{0,4})?$";
    public static final String EXPRESSION_NUMBER_N7DN2 = "^[0-9]{1,7}(\\.[0-9]{0,2})?$";

    public static void validateUtmEasting(String utmEasting, AppMessages messages) {
        if (!StringUtils.isBlank(utmEasting)) {
            if (!NumberUtils.isParsable(utmEasting)
                || !utmEasting.matches("^[0-9]{1,6}(\\.[0-9]{0,2})?$")) {
                logAndThrowBadRequestResponseException(
                    messages, ValidationMessages.UTM_EASTING_VALIDATION,
                    String.format("UTMEasting(%s) validation failed due to malformed", utmEasting));
            } else {
                Double utmE = Double.valueOf(utmEasting);
                if (!(utmE <= 750000 && utmE >= 250000)) {
                    logAndThrowBadRequestResponseException(
                        messages, ValidationMessages.UTM_EASTING_VALIDATION,
                        String.format(
                            "UTMEasting(%s) validation failed due to out of range [250000, 750000]", utmEasting));
                }
            }
        }
    }

    private static void logAndThrowBadRequestResponseException(AppMessages appMessages,
                                                               ValidationMessages validationMessages,
                                                               String logMsg) {
        CommonUtils.getLogger().error("{}, throw BadRequest ResponseStatusException", logMsg);
        ExceptionUtils.throwBadRequestResponseException(appMessages, validationMessages);
    }

    public static void validateUtmNorthinging(String utmNorthing, AppMessages messages) {
        if (!StringUtils.isBlank(utmNorthing)) {
            if (!NumberUtils.isParsable(utmNorthing)
                || !utmNorthing.matches(ValidationUtils.EXPRESSION_NUMBER_N7DN2)) {
                logAndThrowBadRequestResponseException(
                    messages, ValidationMessages.UTM_NORTHING_VALIDATION,
                    String.format("UTMNorthing(%s) validation failed due to malformed", utmNorthing));
            } else {
                Double utmN = Double.valueOf(utmNorthing);
                if (!(utmN <= 5500000 && utmN >= 4500000)) {
                    logAndThrowBadRequestResponseException(
                        messages, ValidationMessages.UTM_NORTHING_VALIDATION,
                        String.format(
                            "UTMNorthing(%s) validation failed due to out of range [4500000, 5500000]", utmNorthing));
                }
            }
        }
    }

    public static void validateUTMLatDeg(String utmLatDeg, AppMessages messages) {
        if (!StringUtils.isBlank(utmLatDeg)) {
            if (!NumberUtils.isParsable(utmLatDeg)
                || !utmLatDeg.matches("^([+\\-])?[0-9]{1,2}(\\.[0-9]{0,8})?$")) {
                logAndThrowBadRequestResponseException(
                    messages, ValidationMessages.LATDEG_VALIDATION,
                    String.format("UTMLatDeg(%s) validation failed due to malformed", utmLatDeg));
            } else {
                Double latDeg = Double.valueOf(utmLatDeg);
                if (!(latDeg <= 50 && latDeg >= 40)) {
                    logAndThrowBadRequestResponseException(
                        messages, ValidationMessages.LATDEG_VALIDATION,
                        String.format(
                            "UTMLatDeg(%s) validation failed due to out of range [40, 50]", utmLatDeg));
                }
            }
        }
    }

    public static void validateUTMLongDeg(String utmLongDeg, AppMessages messages) {
        if (!StringUtils.isBlank(utmLongDeg)) {
            if (!NumberUtils.isParsable(utmLongDeg)
                || !utmLongDeg.matches("^([+\\-])?[0-9]{1,3}(\\.[0-9]{0,8})?$")) {
                logAndThrowBadRequestResponseException(
                    messages, ValidationMessages.LONGDEG_VALIDATION,
                    String.format("UTMLongDeg(%s) validation failed due to malformed", utmLongDeg));
            } else {
                Double longDeg = Double.valueOf(utmLongDeg);
                if (longDeg < 0) {
                    if (!(longDeg <= -74 && longDeg >= -95)) {
                        logAndThrowBadRequestResponseException(
                            messages, ValidationMessages.LONGDEG_LOW_VALIDATION,
                            String.format(
                                "UTMLongDeg(%s) validation failed due to out of range [-95, -74]", utmLongDeg));
                    }
                } else if (!(longDeg <= 95 && longDeg >= 74)) {
                    logAndThrowBadRequestResponseException(
                        messages, ValidationMessages.LONGDEG_HIGH_VALIDATION,
                        String.format(
                            "UTMLongDeg(%s) validation failed due to out of range [74, 95]", utmLongDeg));
                }
            }
        }
    }

    public static boolean isIntegerWithMaxLength(String inputStr, int maxLen) {
        return StringUtils.isBlank(inputStr)
            || NumberUtils.isDigits(inputStr)
            && NumberUtils.isParsable(inputStr)
            && inputStr.length() <= maxLen;
    }

    public static boolean isIntegerAndLessThan(String inputStr, int checkNum) {
        return StringUtils.isBlank(inputStr)
            || NumberUtils.isDigits(inputStr)
            && NumberUtils.isParsable(inputStr)
            && Integer.parseInt(inputStr) <= checkNum;
    }

    public static boolean isNumberAndMatchExpression(String inputStr, String regEx) {
        return StringUtils.isBlank(inputStr) || NumberUtils.isParsable(inputStr) && inputStr.matches(regEx);
    }

    public static boolean isNumberAndBetween(String inputStr, double minNum, double maxNum) {
        return StringUtils.isBlank(inputStr)
            || NumberUtils.isParsable(inputStr)
            && !(Double.parseDouble(inputStr) < minNum)
            && !(Double.parseDouble(inputStr) > maxNum);
    }

    public static boolean maxLengthLessThan(String inputStr, int maxLen) {
        return StringUtils.isBlank(inputStr) || inputStr.length() <= maxLen;
    }

    public static void validateTrimMatch(String key,
                                         String value,
                                         String expression,
                                         String caller,
                                         AppMessages messages,
                                         ValidationMessages validationMessages) {
        if (!StringUtils.isBlank(value) && !value.trim().matches(expression)) {
            CommonUtils.getLogger().error("validateTrimMatch({}) found {}({}) does not match format ({})",
                caller, key, value, expression);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }

    /**
     * Validate the "startTime" and "finishTime" values from the passed in formData. When condition does not match,
     * throw BAD request RequestResponseException.
     *
     * When thrown RequestResponseException used "LA" ValidationMessages because they are generic message for the time.
     *
     * @param formData Map which contains the "startTime" and "finishTime"
     * @param messages AppMessage which will be used to get message details when throw RequetResponseException
     * @param caller String of the caller name
     */
    public static void validateStartAndFinishTime(Map<String, String> formData, AppMessages messages, String caller) {
        String startTimeKey = "startTime";
        String startTime = formData.get(startTimeKey);
        String finishTimeKey = "finishTime";
        String finishTime = formData.get(finishTimeKey);
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(finishTime)) {
            CommonUtils.getLogger().error("validateStartAndFinishTime({}) found either {}({}) or {}({}) is empty",
                caller, startTimeKey, startTime, finishTimeKey, finishTime);
//            ExceptionUtils.throwBadRequestResponseException(
//                messages, ValidationMessages.LA_FINISHTIME_STARTTIME_VALIDATION);
        }
        else {
            validateTrimMatch(startTimeKey, startTime, EXPRESSION_TIME_24H,
                    caller, messages, ValidationMessages.LA_STARTTIME_VALIDATION);
            validateTrimMatch(finishTimeKey, finishTime, EXPRESSION_TIME_24H,
                    caller, messages, ValidationMessages.LA_FINISHTIME_VALIDATION);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            try {
                if (format.parse(finishTime).before(format.parse(startTime))) {
                    CommonUtils.getLogger().error("validateStartAndFinishTime({}) found {}({}) is after {}({})",
                            caller, startTimeKey, startTime, finishTimeKey, finishTime);
                    ExceptionUtils.throwBadRequestResponseException(
                            messages, ValidationMessages.LA_FINISHTIME_BEFORE_STARTTIME_VALIDATION);
                }
            } catch (ParseException e) {
                CommonUtils.getLogger().error(
                        "validateStartAndFinishTime({}) failed to parse {}({}) or {}({}) to Date due to {}",
                        caller, startTimeKey, startTime, finishTimeKey, finishTime, e.getStackTrace(), e);
                ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.LA_FINISHTIME_STARTTIME_VALIDATION);
            }
        }
    }

    public static void validateUtmOneAndTwo(Map<String, String> formData, AppMessages messages) {
        validateUtmEasting(formData.get("utm_e01"), messages);
        validateUtmNorthinging(formData.get("utm_n01"), messages);

        validateUtmEasting(formData.get("utm_e02"), messages);
        validateUtmNorthinging(formData.get("utm_n02"), messages);
    }

    public static void validateUtm(Map<String, String> formData, AppMessages messages) {
        validateUtmOneAndTwo(formData, messages);

        validateUtmEasting(formData.get("utm_e03"), messages);
        validateUtmNorthinging(formData.get("utm_n03"), messages);

        validateUtmEasting(formData.get("utm_e04"), messages);
        validateUtmNorthinging(formData.get("utm_n04"), messages);
    }

    public static void validateErrors(Errors errors, String caller) {
        CommonUtils.getLogger().info("{} validateErrors", caller);
        if (errors.hasErrors()) {
            CommonUtils.getLogger().error("{} validateErrors found hasError is true with ({})",
                caller, errors.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining()));
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST,
                errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(",")));
        }
    }
}
