package ca.gc.dfo.slims.validation.larvalassessments;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LarvalAssessment;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import ca.gc.dfo.slims.validation.BaseValidator;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZHUY
 *
 */
public class LAValidator extends BaseValidator {
    public static final String METHODOLOGY_START_EF = "EF";
    public static final String METHODOLOGY_START_GB = "GB";

    private static final String VALIDATOR_NAME = "LAValidator";
    private static final String KEY_FISH_SPECIES = "fishSpecies";

    private static final Map<String, ValidationMessages> LA_NON_BLANK_KEY_MESSAGE_MAP = new HashMap<>();
    static {
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("lakeId",             ValidationMessages.REQUIRED_LAKE_VALIDATION);
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("streamId",           ValidationMessages.REQUIRED_STREAM_VALIDATION);
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("branchId",           ValidationMessages.REQUIRED_BRANCHLENTIC_VALIDATION);
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("stationFromId",      ValidationMessages.REQUIRED_STATION_FROM_VALIDATION);
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("stationToId",        ValidationMessages.REQUIRED_STATION_TO_VALIDATION);
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("SURVEY_METHODOLOGY", ValidationMessages.LA_METHODOLOGY_VALIDATION);
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("laOperator1",        ValidationMessages.OPERATOR1_VALIDATION);
        LA_NON_BLANK_KEY_MESSAGE_MAP.put("sampleDate",         ValidationMessages.LA_SAMPLEDATE_VALIDATION);
    }

    private static final Map<String, ValidationMessages> SPECIES_MESSAGE_MAP = ImmutableMap.of(
        "obserAlive",  ValidationMessages.FISHOBSCOLL_OBSRALIVE_VALIDATION,
        "obserDead",   ValidationMessages.FISHOBSCOLL_OBSRDEAD_VALIDATION,
        "colReleased", ValidationMessages.FISHOBSCOLL_COLRELEASED_VALIDATION,
        "colDead",     ValidationMessages.FISHOBSCOLL_COLDEAD_VALIDATION
    );

    public static void validateLaFormData(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateLaFormData with formData {}", VALIDATOR_NAME, formData.toString());
        LA_NON_BLANK_KEY_MESSAGE_MAP.forEach((key, value) -> {
            String formDateValue = formData.get(key);
            if (StringUtils.isBlank(formDateValue)) {
                CommonUtils.getLogger().error(
                    "{}:validateFmFormData found missing value for {}", VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(messages, value);
            }
        });

        ValidationUtils.validateStartAndFinishTime(formData, messages, VALIDATOR_NAME + "validateLaFormData");
        ValidationUtils.validateUtm(formData, messages);

        String key = "la_location";
        String value = formData.get(key);
        if (!ValidationUtils.maxLengthLessThan(value, 250)) {
            CommonUtils.getLogger().error(
                "{}:validateLaFormData found {}({}) does not match condition of less than 250",
                VALIDATOR_NAME, key, value);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.LOCATION_DESCRIPTION_VALIDATION);
        }

        List<String> purposes = new ArrayList<>();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (entry.getKey().contains("purposeCode")) {
                purposes.add(entry.getValue());
            }
        }
        if (purposes.isEmpty()) {
            CommonUtils.getLogger().error("{}:validateLaFormData got empty purpose List, throw BAD Request");
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.LA_SURVEYPURPOSE_VALIDATION);
        }
    }

    public static void validateLaCollConditionFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateLaCollConditionFormData";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());
        verifyPoorOrLength1000(
            formData, "optradio_gencon","optradio_gencon", methodName, messages,
            ValidationMessages.COLLCONDITION_CONDETAIL_VALIDATION);
        verifyPoorOrLength1000(
            formData, "optradio_effectiveness","effectiveness_detail", methodName, messages,
            ValidationMessages.COLLCONDITION_EFFECDETAIL_VALIDATION);
    }

    private static void verifyPoorOrLength1000(Map<String, String> formData,
                                               String key,
                                               String key2,
                                               String caller,
                                               AppMessages messages,
                                               ValidationMessages validationMessagesLength1000) {
        String value = formData.get(key);
        String value2 = formData.get(key2);
        if (StringUtils.isBlank(value2)) {
            if (!StringUtils.isBlank(value) && value.equalsIgnoreCase("poor")) {
                CommonUtils.getLogger().error("{} found {} is blank and {}({}) is poor", caller, key2, key, value);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.COLLCONDITION_POORCON_VALIDATION);
            }
        } else if (value2.length() > 1000) {
            CommonUtils.getLogger().error("{} found {}({}) length is more than 1000", caller, key2, value2);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessagesLength1000);
        }
    }

    public static void validateSpeciesFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validateSpeciesFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String numOfString = formData.get("numOfSpecies");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug(
            "{}:validateSpeciesFormdata got numOfSpecies as ({})", VALIDATOR_NAME, numOfString);

        Set<String> allSpecies = new HashSet<>();
        int numOfSpecies = Integer.valueOf(formData.get("numOfSpecies"));
        for (int i = 0; i < numOfSpecies; i++) {
            String key = KEY_FISH_SPECIES + i;
            String value = formData.get(key);
            if (StringUtils.isBlank(value)) {
                if (numOfSpecies == 1) {
                    return;
                }
                CommonUtils.getLogger().error(
                    "{}:validateSpeciesFormdata found {} has blank value when numOfSpecies is not 1",
                    VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.FISHOBSCOLL_SPECIE_VALIDATION);
            }

            allSpecies.add(value);

            int index = i;
            SPECIES_MESSAGE_MAP.forEach((k, validationMessages) -> verifyIntegerWithMaxLength(
                formData, k + index, 4, "validateSpeciesFormdata", messages, validationMessages));
        }
        if (allSpecies.size() != numOfSpecies) {
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.FISHOBSCOLL_ALLSPECIES_VALIDATION);
        }
    }

    private static boolean verifyLaMethodologyNotStartWith(LarvalAssessment la, String startString) {
        return la == null
            || la.getSurveyMethodology() == null
            || !la.getSurveyMethodology().getCodePair().getAbbreviation().startsWith(startString);
    }

    public static void validateLaElectroFishingFormData(LarvalAssessment la,
                                                        Map<String, String> formData,
                                                        AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateLaElectroFishingFormData";
        CommonUtils.getLogger().debug(
            "{} with LarvalAssessment({}) and formData {}", methodName, la.getId(), formData.toString());

        if (verifyLaMethodologyNotStartWith(la, METHODOLOGY_START_EF)) {
            CommonUtils.getLogger().error(
                "{} found LarvalAssessment({}) did not set to Electrofishing methodology ({})",
                messages, la.getSurveyMethodology().getCodePair().getShowText());
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.ELECTROFISH_METHODOLOGY_VALIDATION);
        }

        verifyIntegerWithMaxLength(formData, "peak_vol", 3, methodName,
            messages, ValidationMessages.ELECTROFISH_PEAKVOLT_VALIDATION);
        verifyIntegerWithMaxLength(formData, "duty_cycle_slow", 3, methodName,
            messages, ValidationMessages.ELECTROFISH_DUTYCYCLESLOW_VALIDATION);
        verifyIntegerWithMaxLength(formData, "duty_cycle_fast", 3, methodName,
            messages, ValidationMessages.ELECTROFISH_DUTYCYCLESFAST_VALIDATION);
        verifyIntegerWithMaxLength(formData, "burst_rate", 3, methodName,
            messages, ValidationMessages.ELECTROFISH_BURSTRATE_VALIDATION);
        verifyIntegerWithMaxLength(formData, "tds", 8, methodName,
            messages, ValidationMessages.ELECTROFISH_SURVEYDISTANCE_VALIDATION);

        String key = "pae";
        String value = formData.get(key);
        if (!ValidationUtils.isIntegerAndLessThan(value, 100)) {
            CommonUtils.getLogger().error(
                "{} found {}({}) does not match condition of integer and less than 100", methodName, key, value);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.ELECTROFISH_AREAPERCENT_VALIDATION);
        }

        verifyMatchingExpression(formData, "pulse_rate_slow", ValidationUtils.EXPRESSION_NUMBER_N2DN1,
            methodName, messages, ValidationMessages.ELECTROFISH_PULSERATESLOW_VALIDATION);
        verifyMatchingExpression(formData, "pulse_rate_fast", ValidationUtils.EXPRESSION_NUMBER_N2DN1,
            methodName, messages, ValidationMessages.ELECTROFISH_PULSERATESFAST_VALIDATION);
        verifyMatchingExpression(formData, "ae", "^[0-9]{1,8}(\\.[0-9]{0,2})?$",
            methodName, messages, ValidationMessages.ELECTROFISH_AREAELECTRO_VALIDATION);

        key = "te";
        value = formData.get(key);
        ValidationUtils.validateTrimMatch(key, value, ValidationUtils.EXPRESSION_TIME_HHHMM,
            methodName, messages, ValidationMessages.ELECTROFISH_TIMEELECTRO_VALIDATION);
    }

    public static void validateLaGranularBayerFormData(LarvalAssessment la,
                                                       Map<String, String> formData,
                                                       AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateLaGranularBayerFormData";
        CommonUtils.getLogger().debug(
            "{} with LarvalAssessment({}) and formData {}", methodName, la.getId(), formData.toString());

        if (verifyLaMethodologyNotStartWith(la, METHODOLOGY_START_GB)) {
            CommonUtils.getLogger().error(
                "{} found LarvalAssessment({}) did not set to Granual Bayer methodology ({})",
                messages, la.getSurveyMethodology().getCodePair().getShowText());
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.GRANULARBAYER_METHODOLOGY_VALIDATION);
        }

        verifyMatchingExpression(formData, "plot_size_length", ValidationUtils.EXPRESSION_NUMBER_N4DN1,
            methodName, messages, ValidationMessages.GRANULARBAYER_PLOTLENGTH_VALIDATION);
        verifyMatchingExpression(formData, "plot_size_width", ValidationUtils.EXPRESSION_NUMBER_N4DN1,
            methodName, messages, ValidationMessages.GRANULARBAYER_PLOTWIDTH_VALIDATION);
        verifyMatchingExpression(formData, "effort_hour", ValidationUtils.EXPRESSION_NUMBER_N2DN2,
            methodName, messages, ValidationMessages.GRANULARBAYER_PERSONHOUR_VALIDATION);

        verifyIntegerWithMaxLength(formData, "tfoa", 2, methodName,
            messages, ValidationMessages.GRANULARBAYER_TOFA_VALIDATION);

        String key = "effort_boat";
        String value = formData.get(key);
        if (!StringUtils.isBlank(value) && (!NumberUtils.isParsable(value) || !NumberUtils.isDigits(value))) {
            CommonUtils.getLogger().error(
                "{} found {}({}) is either not parsable or not digits", methodName, key, value);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.GRANULARBAYER_BOATEFFORT_VALIDATION);
        }
    }

    public static void validateLaPhysicalChemicalFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateLaPhysicalChemicalFormData";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());

        verifyMatchingExpression(formData, "surface_temp", ValidationUtils.EXPRESSION_NUMBER_N2DN1,
            methodName, messages, ValidationMessages.PHYCHEMICAL_TOPTEMP_VALIDATION);
        verifyMatchingExpression(formData, "bottom_temp", ValidationUtils.EXPRESSION_NUMBER_N2DN1,
            methodName, messages, ValidationMessages.PHYCHEMICAL_BOTTOMTEMP_VALIDATION);
        verifyMatchingExpression(formData, "conductivity_temp", ValidationUtils.EXPRESSION_NUMBER_N2DN1,
            methodName, messages, ValidationMessages.PHYCHEMICAL_CONDUCTIVITYTEMP_VALIDATION);
        verifyMatchingExpression(formData, "conductivity_ph", ValidationUtils.EXPRESSION_NUMBER_N2DN2,
            methodName, messages, ValidationMessages.PHYCHEMICAL_CONDUCTIVITYPH_VALIDATION);
        verifyMatchingExpression(formData, "mean_depth", ValidationUtils.EXPRESSION_NUMBER_N2DN1,
            methodName, messages, ValidationMessages.PHYCHEMICAL_MEANDEPTH_VALIDATION);
        verifyMatchingExpression(formData, "mean_stream_width", ValidationUtils.EXPRESSION_NUMBER_N4DN1,
            methodName, messages, ValidationMessages.PHYCHEMICAL_MEANSTREAMWIDTH_VALIDATION);
        verifyMatchingExpression(formData, "discharge", ValidationUtils.EXPRESSION_NUMBER_N4DN3,
            methodName, messages, ValidationMessages.PHYCHEMICAL_DISCHARGE_VALIDATION);

        verifyIntegerWithMaxLength(formData, "conductivity", 4, methodName,
            messages, ValidationMessages.PHYCHEMICAL_CONDUCTIVITY_VALIDATION);
    }
}
