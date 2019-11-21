package ca.gc.dfo.slims.utility;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.service.AbstractService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CommonUtils {
    public static final int INT_HTTP_SUCCESS = HttpStatus.OK.value();

    /**
     * @return Logger for logging purpose. Centralize the logger getter here for easier management of the logging
     */
    public static Logger getLogger() {
        return log;
    }

    public static boolean isShowFrench() {
        return LocaleContextHolder.getLocale().getLanguage().toLowerCase().contains("fr");
    }

    public static boolean isAllBlank(String... strings) {
        return Arrays.stream(strings).allMatch(StringUtils::isBlank);
    }

    public static boolean isAnyBlank(String... strings) {
        return Arrays.stream(strings).anyMatch(StringUtils::isBlank);
    }

    /**
     * Get Long from String.
     *
     * @param longString the long value from String
     * @param keyName String of the descriptive name for the longString
     * @return Long value converted from the input longString.
     */
    public static Long getLongFromString(String longString, String keyName) {
        Long longValue = null;
        try {
            longValue = Long.valueOf(longString);
        } catch (NumberFormatException e) {
            String errMsg = String.format("%s with value(%s) is expected to be a numeric string.", keyName, longString);
            getLogger().error(errMsg);
            ExceptionUtils.throwResponseException(HttpStatus.BAD_REQUEST, errMsg);
        }
        return longValue;
    }

    /**
     * Get Long from String.
     *
     * @param intString the int value from String
     * @param keyName String of the description name for the intString
     * @return Integer value converted from the input intString.
     */
    public static Integer getIntFromString(String intString, String keyName) {
        Integer integerValue = 0;
        try {
            integerValue = Integer.valueOf(intString);
        } catch (NumberFormatException e) {
            String errMsg = String.format("%s with value(%s) is expected to be a numeric string.", keyName, intString);
            getLogger().error(errMsg);
            ExceptionUtils.throwResponseException(HttpStatus.BAD_REQUEST, errMsg);
        }
        return integerValue;
    }

    /**
     * Get Integer value from passed in map
     *
     * @param mapData Map to extract the integer value from
     * @param key String of the field name where the integer string value is stored in the Map
     * @param callerName String of the caller name
     * @return 0 if the Map does not contain the integer string value, otherwise return the desired integer value
     */
    public static Integer getIntFromMap(Map<String, String> mapData, String key, String callerName) {
        Integer integerValue = getIntegerValue(mapData.get(key));
        if (integerValue == null) {
            getLogger().debug("{}:getIntFromMap Got null or empty value for {}, return value 0",
                callerName, key);
            return 0;
        }
        return integerValue;
    }

    /**
     * Get returning List based on passed in list.
     *
     * @param dtoList List which is passed in
     * @param <T> Object type
     * @return List of the specified objects which was passed in. If the passed in list is null, return empty list.
     */
    public static <T> List<T> getReturnList(List<T> dtoList) {
        if (dtoList == null) {
            return new LinkedList<>();
        }
        return Lists.newArrayList(dtoList);
    }

    /**
     * Get returning list based on passed in iteratble
     *
     * @param iterable Iterable which is passed in
     * @param <T> Object type
     * @return List of the specified object which was passed in. If the passed iterable is null, reurn empty list.
     */
    public static <T> List<T> getReturnListFromIterable(Iterable<T> iterable) {
        if (iterable == null) {
            return new LinkedList<>();
        }
        return Lists.newArrayList(iterable);
    }

    public static boolean isValidId(String id) {
        return !StringUtils.isBlank(id) && NumberUtils.isParsable(id);
    }

    public static String getObjectCode(String inputStr) {
        if (!StringUtils.isBlank(inputStr) && inputStr.contains("(") && inputStr.contains(")")) {
            return inputStr.substring(inputStr.indexOf("(") + 1, inputStr.indexOf(")"));
        }
        return null;
    }

    public static String getStringValue(String inputStr) {
        if (StringUtils.isBlank(inputStr)) {
            return null;
        }
        return inputStr;
    }

    public static Double getDoubleValue(String inputStr) {
        if (StringUtils.isBlank(inputStr)) {
            return null;
        }
        return Double.valueOf(inputStr);
    }

    public static Long getLongValue(String inputStr) {
        if (StringUtils.isBlank(inputStr)) {
            return null;
        }
        return Long.valueOf(inputStr);
    }

    public static Integer getIntegerValue(String inputStr) {
        return StringUtils.isBlank(inputStr)? null : Integer.valueOf(inputStr);
    }

    public static int getIntValue(String inputStr) {
        return StringUtils.isBlank(inputStr) ? 0 : Integer.valueOf(inputStr);
    }

    public static Date getDateValue(String inputStr) throws ParseException {
        if (StringUtils.isBlank(inputStr)) {
            return null;
        }
        try {
            return (new SimpleDateFormat("yyyy-MM-dd")).parse(inputStr);
        } catch (ParseException e) {
            // log the parse exception here with the details of the inputStr
            // then re-throw the ParseException for general handling by each operation
            log.error("CommonUtils.getDateValue failed to get DATE from {}", inputStr);
            throw e;
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> T getIfPresent(Optional<T> optionalT,
                                     String description,
                                     Long id,
                                     AppMessages messages) {
        if (!optionalT.isPresent()) {
            CommonUtils.getLogger().error("{} could not find with id({})", description, id);
            ExceptionUtils.throwResponseException(
                HttpStatus.NOT_FOUND, messages.get(ResponseMessages.CANNOT_BE_FOUND.getName()));
        }
        return optionalT.get();
    }

    @SuppressWarnings("unchecked")
    public static  <T> List<T> getAllObjectByYear(String yearOp, String year, AbstractService service, String caller) {
        CommonUtils.getLogger().debug("{}->getAllObjectByYear with yearOp({}) and year({})", caller, yearOp, year);
        switch (YearUtils.getYearOperation(yearOp, year)) {
            case EQUAL:
                return service.getAll(year);
            case GTE:
                return service.getAllAfterYear(year);
            case LTE:
                return service.getAllBeforeYear(year);
        }
        return service.getAll();
    }
}
