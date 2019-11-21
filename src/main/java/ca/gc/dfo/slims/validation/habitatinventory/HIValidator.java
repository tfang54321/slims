package ca.gc.dfo.slims.validation.habitatinventory;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import ca.gc.dfo.slims.validation.BaseValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author ZHUY
 *
 */
public class HIValidator extends BaseValidator {
    private static final String VALIDATOR_NAME = "HIValidator";

    public static void validateHiTransectFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateHiTransectFormData";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());

        validateTransectDetailsFormData(formData, messages);

        verifyMatchingExpression(formData, "stream_width", ValidationUtils.EXPRESSION_NUMBER_N4DN1,
            methodName, messages, ValidationMessages.HI_STREAM_WIDTH_VALIDATION);
        verifyMatchingExpression(formData, "transect_spacing", ValidationUtils.EXPRESSION_NUMBER_N2DN2,
            methodName, messages, ValidationMessages.HI_TRANSECT_SPACING_VALIDATION);
        verifyMatchingExpression(formData, "est_discharge", ValidationUtils.EXPRESSION_NUMBER_N5DN3,
            methodName, messages, ValidationMessages.HI_EST_DISCHARGE_VALIDATION);
        verifyMatchingExpression(formData, "cumulative_spawning", ValidationUtils.EXPRESSION_NUMBER_N4DN1,
            methodName, messages, ValidationMessages.HI_CUMULATIVE_SPAWNING_VALIDATION);

        verifyIntegerWithMaxLength(formData, "total_reachlen", 8,
            methodName, messages, ValidationMessages.HI_TOTAL_REACHLEN_VALIDATION);

        verifyMaxLengthLessThan(formData, "stream_conditions", 250,
            methodName, messages, ValidationMessages.HI_STREAM_CONDITION_VALIDATION);

        verifyIntegerAndLessThan(formData, "hi_bedrock", 100,
            methodName, messages, ValidationMessages.HI_BEDROCK_VALIDATION);
        verifyIntegerAndLessThan(formData, "hardpan_clay", 100,
            methodName, messages, ValidationMessages.HI_HARDPAN_CLAY_VALIDATION);
        verifyIntegerAndLessThan(formData, "clay_sediments", 100,
            methodName, messages, ValidationMessages.HI_CLAY_SEDIMENTS_VALIDATION);
        verifyIntegerAndLessThan(formData, "hi_gravel", 100,
            methodName, messages, ValidationMessages.HI_GRAVEL_VALIDATION);
        verifyIntegerAndLessThan(formData, "hi_rubble", 100,
            methodName, messages, ValidationMessages.HI_RUBBLE_VALIDATION);
        verifyIntegerAndLessThan(formData, "hi_sand", 100,
            methodName, messages, ValidationMessages.HI_SAND_VALIDATION);
        verifyIntegerAndLessThan(formData, "hi_silt", 100,
            methodName, messages, ValidationMessages.HI_SILT_VALIDATION);
        verifyIntegerAndLessThan(formData, "silt_detritus", 100,
            methodName, messages, ValidationMessages.HI_SILT_DETRITUS_VALIDATION);
        verifyIntegerAndLessThan(formData, "hi_detritus", 100,
            methodName, messages, ValidationMessages.HI_DETRITUS_VALIDATION);

        int bottomTotal = getIntegerFromFormData(formData, "hi_bedrock")
            + getIntegerFromFormData(formData, "hardpan_clay")
            + getIntegerFromFormData(formData, "clay_sediments")
            + getIntegerFromFormData(formData, "hi_gravel")
            + getIntegerFromFormData(formData, "hi_rubble")
            + getIntegerFromFormData(formData, "hi_sand")
            + getIntegerFromFormData(formData, "hi_silt")
            + getIntegerFromFormData(formData, "silt_detritus")
            + getIntegerFromFormData(formData, "hi_detritus");

        if (bottomTotal != 100) {
            CommonUtils.getLogger().error("{} found bottomTotal({}) is not 100", methodName, bottomTotal);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.HI_BOTTOM_VALIDATION);
        }
    }

    private static Integer getIntegerFromFormData(Map<String, String> formData, String key) {
        Integer result = CommonUtils.getIntegerValue(formData.get(key));
        return result == null ? 0 : result;
    }

    private static void validateTransectDetailsFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateTransectDetailsFormData";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());

        String numOfString = formData.get("numOfTransects");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{} got numOfTransects as {}", methodName, numOfString);

        String disFromLeftName = "disFromLeftBank";
        String depthName = "depth";
        String hiTypeName = "hitype";

        double previousDisFromLeftBank = 0.0;
        double currentDisFromLeftBank;

        int numOfTransects = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfTransects; i++) {
            String hiTypeValue = formData.get(hiTypeName + i);
            if (StringUtils.isBlank(hiTypeValue)) {
                continue;
            }
            verifyIntegerAndLessThan(formData, hiTypeName + i, 100,
                methodName, messages, ValidationMessages.HI_TYPE_VALIDATION);

            String key = disFromLeftName + i;
            String disFromLeftValue = formData.get(key);
            if (!StringUtils.isBlank(disFromLeftValue)) {
                String expression = "^[0-9]{1,4}(\\.[0-9]{0,4})?$";
                if (!ValidationUtils.isNumberAndMatchExpression(disFromLeftValue, expression)) {
                    CommonUtils.getLogger().error(
                        "{} found {}({}) does not match format {}", methodName, key, disFromLeftValue, expression);
                    ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.HI_DISTANCE_LEFTBANK_VALIDATION);
                }
                currentDisFromLeftBank = Double.parseDouble(disFromLeftValue);
                if (i == 0) {
                    previousDisFromLeftBank = currentDisFromLeftBank;
                } else {
                    if (currentDisFromLeftBank < previousDisFromLeftBank) {
                        CommonUtils.getLogger().error(
                            "{} found {}({}) is less than previous value({})",
                            methodName, key, currentDisFromLeftBank, previousDisFromLeftBank);
                        ExceptionUtils.throwBadRequestResponseException(
                            messages, ValidationMessages.HI_DISTANCE_LEFTBANK_VALIDATION);
                    }
                    previousDisFromLeftBank = currentDisFromLeftBank;
                }
            }

            verifyMatchingExpression(formData, depthName + i, ValidationUtils.EXPRESSION_NUMBER_N2DN1,
                methodName, messages, ValidationMessages.HI_DEPTH_VALIDATION);
        }
    }
    
    public static void validateHiFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateHiFormData";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());

        verifyBlank(formData, "inventoryDate",
            methodName, messages, ValidationMessages.HI_INVENTORYDATE_VALIDATION);
        verifyBlank(formData, "lakeId",
            methodName, messages, ValidationMessages.REQUIRED_LAKE_VALIDATION);
        verifyBlank(formData, "streamId",
            methodName, messages, ValidationMessages.REQUIRED_STREAM_VALIDATION);
        verifyBlank(formData, "branchId",
            methodName, messages, ValidationMessages.REQUIRED_BRANCHLENTIC_VALIDATION);
        verifyBlank(formData, "stationFromId",
            methodName, messages, ValidationMessages.REQUIRED_STATION_FROM_VALIDATION);
        verifyBlank(formData, "stationToId",
            methodName, messages, ValidationMessages.REQUIRED_STATION_TO_VALIDATION);
        verifyBlank(formData, "hiOperator1",
            methodName, messages, ValidationMessages.HI_OPERATOR1_VALIDATION);

        ValidationUtils.validateUtmOneAndTwo(formData, messages);

        verifyMaxLengthLessThan(formData, "hi_location", 250,
            methodName, messages, ValidationMessages.LOCATION_DESCRIPTION_VALIDATION);
    }

}
