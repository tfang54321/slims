package ca.gc.dfo.slims.validation;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

abstract public class BaseValidator {

    protected static void verifyIntegerWithMaxLength(Map<String, String> formData,
                                                     String key,
                                                     int maxLen,
                                                     String caller,
                                                     AppMessages messages,
                                                     ValidationMessages validationMessages) {
        String value = formData.get(key);
        if (!ValidationUtils.isIntegerWithMaxLength(value, maxLen)) {
            CommonUtils.getLogger().error(
                "{} found {}({}) does not match condition of integer with maximum length {}",
                caller, key, value, maxLen);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }

    protected static void verifyMatchingExpression(Map<String, String> formData,
                                                   String key,
                                                   String expression,
                                                   String caller,
                                                   AppMessages messages,
                                                   ValidationMessages validationMessages) {
        String value = formData.get(key);
        if (!ValidationUtils.isNumberAndMatchExpression(value, expression)) {
            CommonUtils.getLogger().error(
                "{} found {}({}) does not match condition of number with format({})",
                caller, key, value, expression);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }

    protected static void verifyIntegerAndLessThan(Map<String, String> formData,
                                                   String key,
                                                   int maxLen,
                                                   String caller,
                                                   AppMessages messages,
                                                   ValidationMessages validationMessages) {
        String value = formData.get(key);
        if (!ValidationUtils.isIntegerAndLessThan(value, maxLen)) {
            CommonUtils.getLogger().error(
                "{} found {}({}) does not match condition of integer and less than {})",
                caller, key, value, maxLen);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }

    protected static void verifyMaxLengthLessThan(Map<String, String> formData,
                                                  String key,
                                                  int maxLen,
                                                  String caller,
                                                  AppMessages messages,
                                                  ValidationMessages validationMessages) {
        String value = formData.get(key);
        if (!ValidationUtils.maxLengthLessThan(value, maxLen)) {
            CommonUtils.getLogger().error(
                "{} found {}({}) does not match condition of maximum Length less than {})",
                caller, key, value, maxLen);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }

    protected static void verifyBlank(Map<String, String> formData,
                                      String key,
                                      String caller,
                                      AppMessages messages,
                                      ValidationMessages validationMessages) {
        if (StringUtils.isBlank(formData.get(key))) {
            CommonUtils.getLogger().error("{} found {} value is blank", caller, key);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }

    protected static void verifyIsNumberAndBetween(Map<String, String> formData,
                                                   String key,
                                                   double minNum,
                                                   double maxNum,
                                                   String caller,
                                                   AppMessages messages,
                                                   ValidationMessages validationMessages) {
        String value = formData.get(key);
        if (!ValidationUtils.isNumberAndBetween(value, minNum, maxNum)) {
            CommonUtils.getLogger().error(
                "{} found {}({}) is out of range [{}, {}]", caller, key, value, minNum, maxNum);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }
}
