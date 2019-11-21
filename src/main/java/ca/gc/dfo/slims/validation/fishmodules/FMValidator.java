package ca.gc.dfo.slims.validation.fishmodules;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
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
public class FMValidator {
    private static final String VALIDATOR_NAME = "FMValidator";

    private static final Map<String, ValidationMessages> FM_NON_BLANK_KEY_MESSAGE_MAP = new HashMap<>();
    static {
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("lakeId",        ValidationMessages.REQUIRED_LAKE_VALIDATION);
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("streamId",      ValidationMessages.REQUIRED_STREAM_VALIDATION);
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("branchId",      ValidationMessages.REQUIRED_BRANCHLENTIC_VALIDATION);
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("stationFromId", ValidationMessages.REQUIRED_STATION_FROM_VALIDATION);
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("stationToId",   ValidationMessages.REQUIRED_STATION_TO_VALIDATION);
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("technique",     ValidationMessages.FM_TECHNIQUE_VALIDATION);
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("methodology",   ValidationMessages.FM_METHODOLOGY_VALIDATION);
        FM_NON_BLANK_KEY_MESSAGE_MAP.put("fmOperator1",   ValidationMessages.OPERATOR1_VALIDATION);
    }

    private static final Map<String, ValidationMessages> KEY_MESSAGE_MAP = new HashMap<>();
    static {
        // FMs
        KEY_MESSAGE_MAP.put("containment",    ValidationMessages.FM_CONTAINMENT_VALIDATION);
        KEY_MESSAGE_MAP.put("conductivity",   ValidationMessages.FM_CONDUCTIVITY_VALIDATION);
        KEY_MESSAGE_MAP.put("distanceSurvey", ValidationMessages.FM_DISTANCESURVEY_VALIDATION);
        KEY_MESSAGE_MAP.put("estimatedArea",  ValidationMessages.FM_ESTIMATEDAREA_VALIDATION);
        KEY_MESSAGE_MAP.put("fm_location",    ValidationMessages.LOCATION_DESCRIPTION_VALIDATION);
        KEY_MESSAGE_MAP.put("maxDepth",       ValidationMessages.FM_MAXDEPTH_VALIDATION);
        KEY_MESSAGE_MAP.put("meanDepth",      ValidationMessages.FM_MEANDEPTH_VALIDATION);
        KEY_MESSAGE_MAP.put("meanWidth",      ValidationMessages.FM_MEANWIDTH_VALIDATION);
        KEY_MESSAGE_MAP.put("measuredArea",   ValidationMessages.FM_MEASUREDAREA_VALIDATION);
        KEY_MESSAGE_MAP.put("temperature",    ValidationMessages.FM_TEMPERATURE_VALIDATION);
        // FM_RUNNETs
        KEY_MESSAGE_MAP.put("burst_mode",        ValidationMessages.FM_BURSTMODE_VALIDATION);
        KEY_MESSAGE_MAP.put("est_duration",      ValidationMessages.FM_ESTDURATION_VALIDATION);
        KEY_MESSAGE_MAP.put("fast_duty",         ValidationMessages.FM_FASTDUTY_VALIDATION);
        KEY_MESSAGE_MAP.put("fast_rate",         ValidationMessages.FM_FASTRATE_VALIDATION);
        KEY_MESSAGE_MAP.put("measured_duration", ValidationMessages.FM_MEASUREDDURATION_VALIDATION);
        KEY_MESSAGE_MAP.put("personElecFishing", ValidationMessages.FM_ELECFISHING_VALIDATION);
        KEY_MESSAGE_MAP.put("personCatching",    ValidationMessages.FM_PERSONCATCHING_VALIDATION);
        KEY_MESSAGE_MAP.put("peak_vol",          ValidationMessages.FM_PEAKVOL_VALIDATION);
        KEY_MESSAGE_MAP.put("runnetNumber",      ValidationMessages.FM_RUNNETNUMBER_VALIDATION);
        KEY_MESSAGE_MAP.put("slow_duty",         ValidationMessages.FM_SLOWDUTY_VALIDATION);
        KEY_MESSAGE_MAP.put("slow_rate",         ValidationMessages.FM_SLOWRATE_VALIDATION);
        // FM_HABITATs
        KEY_MESSAGE_MAP.put("depth",    ValidationMessages.FM_HABITATS_DEPTH_VALIDATION);
        KEY_MESSAGE_MAP.put("distance", ValidationMessages.FM_HABITATS_DISTANCE_VALIDATION);
        KEY_MESSAGE_MAP.put("width",    ValidationMessages.FM_HABITATS_WIDTH_VALIDATION);
    }

    private static final List<String> MAX_LENGTH_FOUR_LIST = Arrays.asList(
        "conductivity", "measuredArea", "estimatedArea"
    );

    private static final Map<String, String> FM_EXPRESSION_MAP = ImmutableMap.of(
        "temperature","^-?[0-9]{1,2}(\\.[0-9]{0,2})?$",
        "meanDepth",      ValidationUtils.EXPRESSION_NUMBER_N4DN3,
        "meanWidth",      ValidationUtils.EXPRESSION_NUMBER_N4DN3,
        "maxDepth",       ValidationUtils.EXPRESSION_NUMBER_N4DN3,
        "distanceSurvey", ValidationUtils.EXPRESSION_NUMBER_N4DN3
    );

    private static Map<String, Integer> RUN_NET_MAX_LENGTH_MAP = new HashMap<>();
    static {
        RUN_NET_MAX_LENGTH_MAP.put("runnetNumber",      1);
        RUN_NET_MAX_LENGTH_MAP.put("personElecFishing", 2);
        RUN_NET_MAX_LENGTH_MAP.put("personCatching",    2);
        RUN_NET_MAX_LENGTH_MAP.put("est_duration",      5);
        RUN_NET_MAX_LENGTH_MAP.put("peak_vol",          3);
        RUN_NET_MAX_LENGTH_MAP.put("burst_mode",        3);
        RUN_NET_MAX_LENGTH_MAP.put("slow_duty",         2);
        RUN_NET_MAX_LENGTH_MAP.put("fast_duty",         3);
    }

    private static Map<String, String> RUN_NET_NUMBER_FORMAT_MAP = ImmutableMap.of(
        "slow_rate", ValidationUtils.EXPRESSION_NUMBER_N3DN1,
        "fast_rate", ValidationUtils.EXPRESSION_NUMBER_N3DN1
    );

    private static final Map<String, ValidationMessages> INT_LESS_THAN_100_MAP = new HashMap<>();
    static {
        INT_LESS_THAN_100_MAP.put("algae_",         ValidationMessages.HI_ALGAE_VALIDATION);
        INT_LESS_THAN_100_MAP.put("bankOverhang_",  ValidationMessages.HI_OVERHANG_VALIDATION);
        INT_LESS_THAN_100_MAP.put("bedrock_",       ValidationMessages.HI_BEDROCK_VALIDATION);
        INT_LESS_THAN_100_MAP.put("claySediments_", ValidationMessages.HI_CLAY_SEDIMENTS_VALIDATION);
        INT_LESS_THAN_100_MAP.put("eddyLagoon_",    ValidationMessages.HI_EDDYLAGOON_VALIDATION);
        INT_LESS_THAN_100_MAP.put("grasses_",       ValidationMessages.HI_GRASSES_VALIDATION);
        INT_LESS_THAN_100_MAP.put("gravel_",        ValidationMessages.HI_GRAVEL_VALIDATION);
        INT_LESS_THAN_100_MAP.put("hardpanClay_",   ValidationMessages.HI_HARDPAN_CLAY_VALIDATION);
        INT_LESS_THAN_100_MAP.put("pools_",         ValidationMessages.HI_POOLS_VALIDATION);
        INT_LESS_THAN_100_MAP.put("riffles_",       ValidationMessages.HI_RIFFLES_VALIDATION);
        INT_LESS_THAN_100_MAP.put("rubble_",        ValidationMessages.HI_RUBBLE_VALIDATION);
        INT_LESS_THAN_100_MAP.put("run_",           ValidationMessages.HI_RUN_VALIDATION);
        INT_LESS_THAN_100_MAP.put("sand_",          ValidationMessages.HI_SAND_VALIDATION);
        INT_LESS_THAN_100_MAP.put("siltDetritus_",  ValidationMessages.HI_SILT_DETRITUS_VALIDATION);
        INT_LESS_THAN_100_MAP.put("trees_",         ValidationMessages.HI_TREES_VALIDATION);
        INT_LESS_THAN_100_MAP.put("vegetation_",    ValidationMessages.HI_VEGETATION_VALIDATION);
        INT_LESS_THAN_100_MAP.put("woodyDebris_",   ValidationMessages.HI_WOODYDEBRIS_VALIDATION);
    }

    private static final Map<String, String> FM_HABITAT_FORMAT_MAP = ImmutableMap.of(
        "depth",    "^[0-9]{0,2}(\\.[0-9]{0,1})?$",
        "distance", "^[0-9]{0,3}(\\.[0-9]{0,1})?$",
        "width",    "^[0-9]{0,5}(\\.[0-9]{0,1})?$"
    );
    public static void validateFmFormData(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateFmFormData with formData {}", VALIDATOR_NAME, formData.toString());
        FM_NON_BLANK_KEY_MESSAGE_MAP.forEach((key, value) -> {
            String formDateValue = formData.get(key);
            if (StringUtils.isBlank(formDateValue)) {
                CommonUtils.getLogger().error(
                    "{}:validateFmFormData found missing value for {}", VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(messages, value);
            }
        });

        ValidationUtils.validateStartAndFinishTime(formData, messages, VALIDATOR_NAME + "validateFmFormData");

        ValidationUtils.validateUtm(formData, messages);

        String key = "fm_location";
        String value = formData.get(key);
        if (!ValidationUtils.maxLengthLessThan(value, 250)) {
            CommonUtils.getLogger().error(
                "{}:validateFmFormData found {}({}) does not match condition of maximum length is less than 250",
                VALIDATOR_NAME, key, value);
            ExceptionUtils.throwBadRequestResponseException(messages, KEY_MESSAGE_MAP.get(key));
        }

        key = "containment";
        value = formData.get(key);
        if (!ValidationUtils.isIntegerAndLessThan(value, 100)) {
            CommonUtils.getLogger().error(
                "{}:validateFmFormData found {}({}) does not match condition of integer and less than 100",
                VALIDATOR_NAME, key, value);
            ExceptionUtils.throwBadRequestResponseException(messages, KEY_MESSAGE_MAP.get(key));
        }

        MAX_LENGTH_FOUR_LIST.forEach(k -> {
            String v = formData.get(k);
            if (!ValidationUtils.isIntegerWithMaxLength(v, 4)) {
                CommonUtils.getLogger().error(
                    "{}:validateFmFormData found {}({}) does not match condition of maximum length is less than 4",
                    VALIDATOR_NAME, k, v);
                ExceptionUtils.throwBadRequestResponseException(messages, KEY_MESSAGE_MAP.get(k));
            }
        });

        FM_EXPRESSION_MAP.forEach((k, expression) -> {
            String v = formData.get(k);
            if (!ValidationUtils.isNumberAndMatchExpression(v, expression)) {
                CommonUtils.getLogger().error("{}:validateFmFormData found {}({}) does not match expected format {())",
                    VALIDATOR_NAME, k, v, expression);
                ExceptionUtils.throwBadRequestResponseException(messages, KEY_MESSAGE_MAP.get(k));
            }
        });
    }

    public static void validateRunnetFormData(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validateRunnetFormData with formData {}", VALIDATOR_NAME, formData.toString());
        RUN_NET_MAX_LENGTH_MAP.forEach((key, maxLength) -> {
            String value = formData.get(key);
            if (!ValidationUtils.isIntegerWithMaxLength(value, maxLength)) {
                CommonUtils.getLogger().error(
                    "{}:validateRunnetFormData found {}({}) does not match condition of maximum length is {}",
                    VALIDATOR_NAME, key, value, maxLength);
                ExceptionUtils.throwBadRequestResponseException(messages, KEY_MESSAGE_MAP.get(key));
            }
        });
        RUN_NET_NUMBER_FORMAT_MAP.forEach((key, expression) -> {
            String value = formData.get(key);
            if (!ValidationUtils.isNumberAndMatchExpression(value, expression)) {
                CommonUtils.getLogger().error(
                    "{}:validateRunnetFormData found {}({}) does not match expected format {())",
                    VALIDATOR_NAME, key, value, expression);
                ExceptionUtils.throwBadRequestResponseException(messages, KEY_MESSAGE_MAP.get(key));
            }
        });

        String key = "measured_duration";
        ValidationUtils.validateTrimMatch(key, formData.get(key), ValidationUtils.EXPRESSION_TIME_HHHMM,
            VALIDATOR_NAME + ";validateRunnetFormData",
            messages, ValidationMessages.FM_MEASUREDDURATION_VALIDATION);
    }

    public static void validateSpeciesFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validateSpeciesFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String numOfString = formData.get("numOfSpecies");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug(
            "{}:validatePrimAppsFormdata got numOfSpecies as({})", VALIDATOR_NAME, numOfString);
        int numOfSpecies = CommonUtils.getIntValue(numOfString);

        String fishSpecieName = "fishSpecies";
        String totalCaughtName = "totalCaught";
        String totalObservedName = "totalObserved";

        String specieName = "specie";
        String numOfindividualsName = "_numOfIndi";

        String indiLengthName = "indiLen";
        String indiWeightName = "indiWeight";

        Set<String> allSpecies = new HashSet<>();

        for (int i = 0; i < numOfSpecies; i++) {
            String key = fishSpecieName + i;
            String value = formData.get(key);
            if (StringUtils.isBlank(value)) {
                if (numOfSpecies == 1) {
                    return;
                }

                CommonUtils.getLogger().error(
                    "{}:validateSpeciesFormdata found {}({}) is blank when numOfSpecies({}) is not 1",
                    VALIDATOR_NAME, key, value, numOfSpecies);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.FISHOBSCOLL_SPECIE_VALIDATION);
            }

            allSpecies.add(value);

            key = totalCaughtName + i;
            value = formData.get(key);
            if (!ValidationUtils.isIntegerWithMaxLength(value, 5)) {
                CommonUtils.getLogger().error(
                    "{}:validateSpeciesFormdata found {}({}) does not match condition of integer with maximum length 5",
                    VALIDATOR_NAME, key, value);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.FISHINDIVIDUL_TOTALCAUGHT_VALIDATION);
            }
            key = totalObservedName + i;
            value = formData.get(key);
            if (!ValidationUtils.isIntegerWithMaxLength(value, 5)) {
                CommonUtils.getLogger().error(
                    "{}:validateSpeciesFormdata found {}({}) does not match condition of integer with maximum length 5",
                    VALIDATOR_NAME, key, value);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.FISHINDIVIDUL_TOTALOBSERVED_VALIDATION);
            }

            key = specieName + i + numOfindividualsName;
            int numOfIndividuals = CommonUtils.getIntValue(formData.get(key));
            CommonUtils.getLogger().debug(
                "{}:validateSpeciesFormdata [{}] got {} as {}, walk through ...",
                VALIDATOR_NAME, i, key, numOfIndividuals);
            for (int j = 0; j < numOfIndividuals; j++) {
                key = fishSpecieName + i + "_" + indiLengthName + j;
                value = formData.get(key);
                String expression = ValidationUtils.EXPRESSION_NUMBER_N4DN1;
                if (!ValidationUtils.isNumberAndMatchExpression(value, expression)) {
                    CommonUtils.getLogger().error(
                        "{}:validateSpeciesFormdata found {}({}) does not match expected format {())",
                        VALIDATOR_NAME, key, value, expression);
                    ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.FISHINDI_LENGTH_VALIDATION);
                }
                key = fishSpecieName + i + "_" + indiWeightName + j;
                value = formData.get(key);
                expression = ValidationUtils.EXPRESSION_NUMBER_N6DN4;
                if (!ValidationUtils.isNumberAndMatchExpression(value, expression)) {
                    CommonUtils.getLogger().error(
                        "{}:validateSpeciesFormdata found {}({}) does not match expected format {())",
                        VALIDATOR_NAME, key, value, expression);
                    ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.FISHINDI_WEIGHT_VALIDATION);
                }
            }
        }

        int allSpeciesSize = allSpecies.size();
        CommonUtils.getLogger().debug("{}:validateSpeciesFormdata got {} species", VALIDATOR_NAME, allSpeciesSize);
        if (allSpeciesSize != numOfSpecies) {
            CommonUtils.getLogger().error(
                "{}:validateSpeciesFormdata processed total species({}) does not match passed in total number({})",
                VALIDATOR_NAME, allSpeciesSize, numOfSpecies);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.FISHOBSCOLL_ALLSPECIES_VALIDATION);
        }
    }
    
    public static void validateFmHabitatsFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validateFmHabitatsFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String numOfString = formData.get("numOfHabitats");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug(
            "{}:validateFmHabitatsFormdata got numOfHabitats as({})", VALIDATOR_NAME, numOfString);
        int numOfHabitats = CommonUtils.getIntValue(numOfString);

        String transectIdName = "transectId";
        String locationName = "location_";

        String key, value;
        for (int i = 0; i < numOfHabitats; i++) {
            key = transectIdName + i;
            value = formData.get(key);
            if (StringUtils.isBlank(value)) {
                if (numOfHabitats == 1) {
                    return;
                }

                CommonUtils.getLogger().error(
                    "{}:validateFmHabitatsFormdata found {}({}) is blank when numOfSpecies({}) is not 1",
                    VALIDATOR_NAME, key, value, numOfHabitats);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.FM_HABITATS_VALIDATION);
            }

            final int index = i;
            FM_HABITAT_FORMAT_MAP.forEach((k, expression) -> {
                String theKey = k + index;
                String theValue = formData.get(theKey);
                if (!ValidationUtils.isNumberAndMatchExpression(theValue, expression)) {
                    CommonUtils.getLogger().error(
                        "{}:validateFmHabitatsFormdata [{}] found {}({}) does not match expected format {())",
                        VALIDATOR_NAME, index, theKey, theValue, expression);
                    ExceptionUtils.throwBadRequestResponseException(messages, KEY_MESSAGE_MAP.get(k));
                }
            });

            key = locationName + i;
            value = formData.get(key);
            if (!ValidationUtils.maxLengthLessThan(value, 250)) {
                CommonUtils.getLogger().error("{}:validateIntegerAndLessThan100 [{}] found {}({}) " +
                        "does not match condition of integer less than 100",
                    VALIDATOR_NAME, index, key, value);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.LOCATION_DESCRIPTION_VALIDATION);
            }

            INT_LESS_THAN_100_MAP.forEach((k, validationMessage) -> {
                String theKey = k + index;
                String theValue = formData.get(theKey);
                if (!ValidationUtils.isIntegerAndLessThan(theValue, 100)) {
                    CommonUtils.getLogger().error("{}:validateIntegerAndLessThan100 [{}] found {}({}) " +
                            "does not match condition of integer less than 100",
                        VALIDATOR_NAME, index, theKey, theValue);
                    ExceptionUtils.throwBadRequestResponseException(messages, validationMessage);
                }
            });
        }
    }
}
