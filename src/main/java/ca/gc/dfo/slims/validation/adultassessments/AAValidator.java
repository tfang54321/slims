package ca.gc.dfo.slims.validation.adultassessments;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import ca.gc.dfo.slims.validation.BaseValidator;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZHUY
 *
 */
public class AAValidator extends BaseValidator {
    private static final String VALIDATOR_NAME = "AAValidator";
    private static final String KEY_AA_LOCATION = "aa_location";

    private static final Map<String, ValidationMessages> KEY_MESSAGE_MAP = ImmutableMap.of(
        "lakeId",        ValidationMessages.REQUIRED_LAKE_VALIDATION,
        "streamId",      ValidationMessages.REQUIRED_STREAM_VALIDATION,
        "branchId",      ValidationMessages.REQUIRED_BRANCHLENTIC_VALIDATION,
        "stationFromId", ValidationMessages.REQUIRED_STATION_FROM_VALIDATION
    );

    private static final Map<String, ValidationMessages> DETAILS_KEY_MESSAGE_MAP = new HashMap<>();
    static {
        DETAILS_KEY_MESSAGE_MAP.put("trap_number", ValidationMessages.AA_TRAPNUMBER_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("recaptured", ValidationMessages.AA_RECAPTURED_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("marked", ValidationMessages.AA_MARKED_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("week_of_tagging", ValidationMessages.AA_WEEKOF_TAGGING_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("aa_remarks", ValidationMessages.REMARKS_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("ifother", ValidationMessages.AA_IFOTHER_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("air_temp", ValidationMessages.AA_AIRTEMP_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("water_temp", ValidationMessages.AA_WATERTEMP_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("max_temp", ValidationMessages.AA_MAXTEMP_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("min_temp", ValidationMessages.AA_MINTEMP_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("upstream", ValidationMessages.AA_UPSTREAM_VALIDATION);
        DETAILS_KEY_MESSAGE_MAP.put("downstream", ValidationMessages.AA_DOWNSTREAM_VALIDATION);
    }

    private static final Map<String, Integer> DETAILS_MAX_LENGTH_MAP = ImmutableMap.of(
        "marked",          4,
        "recaptured",      4,
        "trap_number",     3,
        "week_of_tagging", 2
    );

    private static final Map<String, Integer> DETAILS_MAX_LENGTH_LESS_THAN_MAP = ImmutableMap.of(
        "aa_remarks", 250,
        "ifother",    45
    );

    private static final Map<String, List<Double>> DETAILS_NUMBER_IN_BETWEEN_MAP = ImmutableMap.of(
        "downstream", Arrays.asList(0.1, 1.0),
        "upstream",   Arrays.asList(1.1, 2.0),
        "water_temp", Arrays.asList(3.0, 25.0)
    );

    private static final List<String> DETAILS_FORMAT_LIST = Arrays.asList("air_temp", "max_temp", "min_temp");

    private static final List<String> FISH_INDIVIDUALS = Arrays.asList("males", "females", "alive", "dead");

    public static void validateAaFormData(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateAaFormData with formData {}", VALIDATOR_NAME, formData.toString());
        for (Map.Entry<String, ValidationMessages> anEntry : KEY_MESSAGE_MAP.entrySet()) {
            String key = anEntry.getKey();
            if (StringUtils.isBlank(formData.get(key))) {
                CommonUtils.getLogger().error("{} found formData missing value for {}", VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(messages, anEntry.getValue());
            }
        }
        if (!ValidationUtils.maxLengthLessThan(formData.get(KEY_AA_LOCATION), 250)) {
            CommonUtils.getLogger().error("{} found value of {} does not match condition of less than 250 chars",
                VALIDATOR_NAME, KEY_AA_LOCATION);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.LOCATION_DESCRIPTION_VALIDATION);
        }
    }

    public static void validateSpeciesFormdata(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateSpeciesFormdata";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());
        String numOfString = formData.get("numOfSpecies");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{} got numOfSpecies as({})", methodName, numOfString);

        String fishSpecieName = "fishSpecies";
        String totalName = "total";

        String specieName = "specie";
        String numOfindividualsName = "_numOfIndi";

        String indiLengthName = "_indiLen";
        String indiWeightName = "_indiWeight";

        Set<String> allSpecies = new HashSet<>();
        String key;
        int numOfSpecies = Integer.valueOf(numOfString);
        for (int i = 0; i < numOfSpecies; i++) {
            key = fishSpecieName + i;
            if (StringUtils.isBlank(formData.get(key))) {
                if (numOfSpecies == 1) {
                    return;
                }
                CommonUtils.getLogger().error("{} found empty value for {} when total number of species is {}",
                    methodName, key, numOfSpecies);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.FISHOBSCOLL_SPECIE_VALIDATION);
            }
            allSpecies.add(formData.get(key));

            for (String anIndi : FISH_INDIVIDUALS) {
                verifyIntegerWithMaxLength(formData, anIndi + i, 4,
                    methodName, messages, ValidationMessages.FISHINDIVIDUL_NUMBER_VALIDATION);
            }

            key = specieName + i + numOfindividualsName;
            int numOfIndividuals = CommonUtils.getIntValue(formData.get(key));
            CommonUtils.getLogger().debug("{} [{}] got {} as({})", methodName, i, key, numOfIndividuals);
            for (int j = 0; j < numOfIndividuals; j++) {
                verifyIsNumberAndBetween(
                    formData, totalName + i + indiLengthName + j, 300, 600,
                    methodName, messages, ValidationMessages.AA_SPECIES_LENGTH_VALIDATION);
                verifyIsNumberAndBetween(
                    formData, totalName + i + indiWeightName + j, 100, 500,
                    methodName, messages, ValidationMessages.AA_SPECIES_WEIGHT_VALIDATION);
            }
        }
        int allSpeciesNum = allSpecies.size();
        CommonUtils.getLogger().debug("{}:validateSpeciesFormdata got ({}) species", VALIDATOR_NAME, allSpeciesNum);
        if (allSpeciesNum != numOfSpecies) {
            CommonUtils.getLogger().error(
                "{} found number of all species ({}) does not match numberOfSpecies({}) from formData",
                VALIDATOR_NAME, allSpeciesNum, numOfSpecies);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.FISHOBSCOLL_ALLSPECIES_VALIDATION);
        }
    }

    public static void validateDetailFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateDetailFormData";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());
        DETAILS_MAX_LENGTH_MAP.forEach((key, maxLength) ->
            verifyIntegerWithMaxLength(formData, key, maxLength,
                methodName, messages, DETAILS_KEY_MESSAGE_MAP.get(key)));
        DETAILS_MAX_LENGTH_LESS_THAN_MAP.forEach((key, length) ->
            verifyMaxLengthLessThan(formData, key, length, methodName, messages, DETAILS_KEY_MESSAGE_MAP.get(key)));
        DETAILS_NUMBER_IN_BETWEEN_MAP.forEach((key, numbers) ->
            verifyIsNumberAndBetween(formData, key, numbers.get(0), numbers.get(1),
                methodName, messages, DETAILS_KEY_MESSAGE_MAP.get(key)));
        DETAILS_FORMAT_LIST.forEach(key ->
            verifyMatchingExpression(formData, key, ValidationUtils.EXPRESSION_NUMBER_N2DN1,
                methodName, messages, DETAILS_KEY_MESSAGE_MAP.get(key)));
    }
    
    public static void validateWeekOfCaptureFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validateWeekOfCaptureFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String tagWeekName = "tagWeek";
        String adultsCapturedName = "adultsCaptured";

        int numOfWeekCaptures = Integer.valueOf(formData.get("numOfWeekCaptures"));
        CommonUtils.getLogger().debug(
            "{}:validateWeekOfCaptureFormdata got numOfWeekCaptures as({})", VALIDATOR_NAME, numOfWeekCaptures);
        String key;
        for (int i = 0; i < numOfWeekCaptures; i++) {
            key = tagWeekName + i;
            if (!ValidationUtils.isIntegerWithMaxLength(formData.get(key), 2)) {
                CommonUtils.getLogger().error(
                    "{} found value of {} does not match condition of an integer less than 2", VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.AA_WEEKOF_TAGGING_VALIDATION);
            }
            key = adultsCapturedName + i;
            if (!ValidationUtils.isIntegerWithMaxLength(formData.get(key), 4)) {
                CommonUtils.getLogger().error(
                    "{} found value of {} does not match condition of an integer less than 4", VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.AA_RECAPTURED_VALIDATION);
            }
        }
    }
}
