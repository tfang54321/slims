package ca.gc.dfo.slims.validation.species;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;

/**
 * @author ZHUY
 *
 */
public class SpecieValidator {
    private static final String VALIDATOR_NAME = "SpecieValidator";

    private static final String EXPRESSION_NUMBER_N8DN4 ="^[0-9]{1,8}(\\.[0-9]{0,4})?$";
    private static final String KEY_FISH_LENGTH = "fishLength";
    private static final String KEY_FISH_WEIGHT = "fishWeight";
    private static final String KEY_NUM_OF_INDIVIDUALS = "numOfIndividuals";

    public static void validateFishIndividualsFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateFishIndividualsFormdata with formData {}",
            VALIDATOR_NAME, formData.toString());

        String numOfString = formData.get(KEY_NUM_OF_INDIVIDUALS);
        if (Strings.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{}:validateFishIndividualsFormdata got numOfAttachments as ({})",
            VALIDATOR_NAME, numOfString);
        int numOfIndividuals = Integer.valueOf(numOfString);
        for (int i = 0; i < numOfIndividuals; i++) {
            String fishLengthFormKey = KEY_FISH_LENGTH + i;
            String fishLengthFormValue = formData.get(fishLengthFormKey);
            String fishWeightFormKey = KEY_FISH_WEIGHT + i;
            String fishWeightFormValue = formData.get(fishWeightFormKey);
            if ((!StringUtils.isBlank(fishLengthFormValue) && StringUtils.isBlank(fishWeightFormValue))
                || (StringUtils.isBlank(fishLengthFormValue) && !StringUtils.isBlank(fishWeightFormValue))) {
                CommonUtils.getLogger().error(
                    "{}:validateFishIndividualsFormdata found either {}({}) or {}({}) has blank value",
                    VALIDATOR_NAME, fishLengthFormKey, fishLengthFormValue, fishWeightFormKey, fishWeightFormValue);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.FISHINDI_LENGTHWEIGHT_VALIDATION);
            }

            if (!StringUtils.isBlank(fishLengthFormValue) && !StringUtils.isBlank(fishWeightFormValue)) {
                if (fishLengthFormValue.trim().equals("0")
                    || !NumberUtils.isParsable(fishLengthFormValue)
                    || !NumberUtils.isDigits(fishLengthFormValue)
                    || fishLengthFormValue.length() > 4) {
                    CommonUtils.getLogger().error("{}:validateFishIndividualsFormdata found {}({}) does not match " +
                            "condition of digits with length greater than 4",
                        VALIDATOR_NAME, fishLengthFormKey, fishLengthFormValue);
                    ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.FISHINDI_LENGTH_VALIDATION);
                }

                if (!ValidationUtils.isNumberAndMatchExpression(fishWeightFormValue, EXPRESSION_NUMBER_N8DN4)) {
                    CommonUtils.getLogger().error(
                        "{}:validateFishIndividualsFormdata found either {}({})does not match format ({})",
                        VALIDATOR_NAME, fishWeightFormKey, fishWeightFormValue, EXPRESSION_NUMBER_N8DN4);
                    ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.FISHINDI_WEIGHT_VALIDATION);
                }
            }
        }
    }

}
