package ca.gc.dfo.slims.validation.treatments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.validation.BaseValidator;
import org.apache.commons.lang3.StringUtils;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.treatments.Treatment;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;

/**
 * @author ZHUY
 *
 */
public class TreatmentValidator extends BaseValidator {
    public static final String KEY_LAKE_ID = "lakeId";
    public static final String KEY_STREAM_ID = "streamId";

    private static final String VALIDATOR_NAME = "TreatmentValidator";

    private static final String EXPRESSION_NUMBER_N3DN2 = "^[0-9]{1,3}(\\.[0-9]{0,2})?$";

    private static final String KEY_NICLO_PERC = "nicloPerc";

    private static final Map<String, ValidationMessages> TR_KEY_MESSAGE_MAP = new HashMap<>();
    static {
        TR_KEY_MESSAGE_MAP.put(KEY_LAKE_ID,                  ValidationMessages.REQUIRED_LAKE_VALIDATION);
        TR_KEY_MESSAGE_MAP.put(KEY_STREAM_ID,                ValidationMessages.REQUIRED_STREAM_VALIDATION);
        TR_KEY_MESSAGE_MAP.put("calendar_days",              ValidationMessages.TR_CALENDARDAYS_VALIDATION);
        TR_KEY_MESSAGE_MAP.put("kilo_treated",               ValidationMessages.TR_KIOTREATED_VALIDATION);
        TR_KEY_MESSAGE_MAP.put("max_crew",                   ValidationMessages.TR_MAXCREW_VALIDATION);
        TR_KEY_MESSAGE_MAP.put("person_days",                ValidationMessages.TR_PERSONDAYS_VALIDATION);
        TR_KEY_MESSAGE_MAP.put("total_discharge",            ValidationMessages.TR_TOTALDISCHARGE_VALIDATION);
        TR_KEY_MESSAGE_MAP.put("REQUIRED_STREAM_VALIDATION", ValidationMessages.TR_START_DATE_VALIDATION);
        TR_KEY_MESSAGE_MAP.put("TREATMENT_METHODOLOGY",      ValidationMessages.TR_METHODOLOGY_VALIDATION);
    }
    private static final List<String> TR_NON_BLANK_LIST = Arrays.asList(
        KEY_LAKE_ID, KEY_STREAM_ID, "treatmentStart", "TREATMENT_METHODOLOGY"
    );
    private static final List<String> TR_FORMATTED_LIST = Arrays.asList(
        "total_discharge", "kilo_treated", "person_days"
    );
    private static final List<String> TR_MAX_LENGTH_2_LIST = Arrays.asList(
        "calendar_days", "max_crew"
    );

    // treatment main page
    public static void validateTrFormData(Treatment tr,
                                          Map<String, String> formData,
                                          AppMessages messages) throws ParseException {
        CommonUtils.getLogger().debug("{}:validateTrFormData with Treatment({}) and formData {}",
            VALIDATOR_NAME, tr.getId(), formData.toString());

        TR_NON_BLANK_LIST.forEach(key -> {
            if (StringUtils.isBlank(formData.get(key))) {
                CommonUtils.getLogger().error("{}:validateTrFormData found value of {} is blank", VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(messages, TR_KEY_MESSAGE_MAP.get(key));
            }
        });

        verifyLocationChanges(tr, formData, messages);

        if (!StringUtils.isBlank(formData.get("treatmentEnd"))) {
            Date treatmentStart = new SimpleDateFormat("yyyy-MM-dd").parse((formData.get("treatmentStart")));
            Date treatmentEnd = new SimpleDateFormat("yyyy-MM-dd").parse((formData.get("treatmentEnd")));

            if (treatmentEnd.before(treatmentStart)) {
                CommonUtils.getLogger().error("{}:validateTrFormData found treatmentEnd date is before treatmentStart");
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.TR_START_END_DATE_VALIDATION);
            }
        }

        TR_FORMATTED_LIST.forEach(key -> {
            if (!ValidationUtils.isNumberAndMatchExpression(formData.get(key), "^[0-9]{1,4}(\\.[0-9]{0,2})?$")) {
                CommonUtils.getLogger().error(
                    "{}:validateTrFormData found value of {} does not align with expected format", VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(messages, TR_KEY_MESSAGE_MAP.get(key));
            }
        });

        TR_MAX_LENGTH_2_LIST.forEach(key -> {
            if (!ValidationUtils.isIntegerWithMaxLength(formData.get(key), 2)) {
                CommonUtils.getLogger().error(
                    "{}:validateTrFormData found value of {} does not match condition of integer with maximum length 2",
                    VALIDATOR_NAME, key);
                ExceptionUtils.throwBadRequestResponseException(messages, TR_KEY_MESSAGE_MAP.get(key));
            }
        });

        String key = "tr_remarks";
        if (!ValidationUtils.maxLengthLessThan(formData.get(key), 250)) {
            CommonUtils.getLogger().error(
                "{}:validateTrFormData found value of {} does not match condition of maximum length less than 250",
                VALIDATOR_NAME, key);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.REMARKS_VALIDATION);
        }
    }

    private static void verifyLocationChanges(Treatment tr, Map<String, String> formData, AppMessages messages) {
        if (tr == null) {
            CommonUtils.getLogger().info(
                "{}:verifyLocationChanges found null Treatment. this should not happen!!!", VALIDATOR_NAME);
            return;
        }

        if (tr.getTrPrimaryApplications().isEmpty()
            && tr.getTrSecondaryApplications().isEmpty()
            && tr.getTrWaterChemistries().isEmpty()
            && tr.getTrMinLethalConcentrations().isEmpty()
            && tr.getTrChemicalAnalysises().isEmpty()
            && tr.getDesiredConcentrations().isEmpty()) {
            CommonUtils.getLogger().debug("{}:verifyLocationChanges no sub-record was found for Treatment({}). "
                + "Skip validation as location change should be ok", VALIDATOR_NAME, tr.getId());
            return;
        }

        CommonUtils.getLogger().debug("{}:verifyLocationChanges proceed validation for Treatment({}) "
                + "which has ({}) Primary Applications, ({}) Secondary Applications, ({}) Chemical Analysis, "
                + "({}) Min Lethal Concentrations, ({}) Desired Concentrations and ({}) Water Chemistries",
            VALIDATOR_NAME, tr.getId(), tr.getTrPrimaryApplications().size(), tr.getTrSecondaryApplications().size(),
            tr.getTrChemicalAnalysises().size(), tr.getTrMinLethalConcentrations().size(),
            tr.getDesiredConcentrations().size(), tr.getTrWaterChemistries().size());

        Lake existingLake = tr.getLake();
        if (existingLake != null) {
            Long oldLakeId = existingLake.getId();
            Long newLakeId = CommonUtils.getLongFromString(formData.get(KEY_LAKE_ID), KEY_LAKE_ID);
            if (!oldLakeId.equals(newLakeId)) {
                CommonUtils.getLogger().error(
                    "{}:verifyLocationChanges found changing lake value of Treatment({}) from ({}) to ({})",
                    VALIDATOR_NAME, tr.getId(), oldLakeId, newLakeId);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_LAKE_ID_VALIDATION);
            }
        }

        Stream existingStream = tr.getStream();
        if (existingStream != null) {
            Long oldStreamId = existingStream.getId();
            Long newStreamId = CommonUtils.getLongFromString(formData.get(KEY_STREAM_ID), KEY_STREAM_ID);
            if (!oldStreamId.equals(newStreamId)) {
                CommonUtils.getLogger().error(
                    "{}:verifyLocationChanges found changing stream value of Treatment({}) from ({}) to ({})",
                    VALIDATOR_NAME, tr.getId(), oldStreamId, newStreamId);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_STREAM_ID_VALIDATION);
            }
        }
    }

    // treatment primary apps list page
    public static void validatePrimAppsFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validatePrimAppsFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String numOfString = formData.get("numOfApps");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug(
            "{}:validatePrimAppsFormdata got numOfApps as({})", VALIDATOR_NAME, numOfString);
        String keyTrBranch = "trBranch";
        String keyTrStationFrom = "trStationFrom";

        int numOfApps = Integer.valueOf(numOfString);
        String trBranch, trStationFrom;
        for (int i = 0; i < numOfApps; i++) {
            trBranch = formData.get(keyTrBranch + i);
            trStationFrom = formData.get(keyTrStationFrom + i);
            if (StringUtils.isBlank(trBranch) && StringUtils.isBlank(trStationFrom)) {
                if (numOfApps == 1) {
                    return;
                }
            }

            if (StringUtils.isBlank(trBranch) || StringUtils.isBlank(trStationFrom)) {
                CommonUtils.getLogger().error(
                    "{}:validatePrimAppsFormdata found [{}] trBranch({} or trStationFrom({}) is blank",
                    VALIDATOR_NAME, i, trBranch, trStationFrom);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_PRIMARY_VALIDATION);
            }
        }
    }
    
    // treatment secondary apps list page
    public static void validateSecondaryAppsFormdata(Treatment tr,
                                                     Map<String, String> formData,
                                                     AppMessages messages) throws ParseException {
        CommonUtils.getLogger().debug("{}:validateSecondaryAppsFormdata for Treatment({}) with formData {}",
            VALIDATOR_NAME, tr.getId(), formData.toString());
        String numOfString = formData.get("numOfApps");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug(
            "{}:validateSecondaryAppsFormdata got numOfApps as({})", VALIDATOR_NAME, numOfString);
        String branchName = "trBranch";
        String stationFromName = "trStationFrom";
        String treatDateName = "trTreatDate";
        String timeStartName = "trTimeStart";

        int numOfApps = Integer.valueOf(numOfString);
        for (int i = 0; i < numOfApps; i++) {
            String trBranch = formData.get(branchName + i);
            String trStationFrom = formData.get(stationFromName + i);
            String trTreatDate = formData.get(treatDateName + i);
            String trTimeStartKey = timeStartName + i;
            String trTimeStart = formData.get(trTimeStartKey);

            if (CommonUtils.isAllBlank(trBranch, trStationFrom, trTreatDate, trTimeStart)) {
                if (numOfApps == 1) {
                    return;
                }
            }
            if (CommonUtils.isAnyBlank(trBranch, trStationFrom, trTreatDate, trTimeStart)) {
                CommonUtils.getLogger().debug("{}:validateSecondaryAppsFormdata found one of the following" +
                        " is blank: trBranch({}), trStationFrom({}), trTreatDate({}),trTimeStart({})",
                    VALIDATOR_NAME, trBranch, trStationFrom, trTreatDate, trTimeStart);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.TR_SECONDAPP_VALIDATION);
            }

            validateTreatmentDate(tr, formData, treatDateName + i, messages);

            ValidationUtils.validateTrimMatch(trTimeStartKey, trTimeStart, ValidationUtils.EXPRESSION_TIME_24H,
                VALIDATOR_NAME + "validateSecondaryAppsFormdata",
                messages, ValidationMessages.TR_SECONDAPP_TIMESTART_VALIDATION);
        }
    }

    private static void validateTreatmentDate(Treatment tr,
                                              Map<String, String> formData,
                                              String key,
                                              AppMessages messages) throws ParseException {
        CommonUtils.getLogger().debug("{}:validateTreatmentDate for Treatment({}) with key({}) and formData {}",
            VALIDATOR_NAME, tr.getId(), key, formData.toString());
        String treatmentDateString = formData.get(key);
        if (StringUtils.isBlank(treatmentDateString)) {
            return;
        }

        Date treatmentDate = new SimpleDateFormat("yyyy-MM-dd").parse((treatmentDateString));
        Date trTreatmentStart = tr.getTrLogistics().getTreatmentStart();
        Date trTreatmentEnd = tr.getTrLogistics().getTreatmentEnd();
        if (treatmentDate.before(trTreatmentStart)
            || (trTreatmentEnd != null && treatmentDate.after(trTreatmentEnd))) {
            CommonUtils.getLogger().error("{}:validateTreatmentDate found treatmentDate({}) is out of range of" +
                    " existing Treatment logistics range [{}, {}]",
                VALIDATOR_NAME, treatmentDateString, trTreatmentStart.toString(), trTreatmentEnd.toString());
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_APP_START_END_DATE_MESSAGE);
        }
    }

    // single primary app edit page
    public static void validatePrimAppFormData(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validatePrimAppFormData with formData {}", VALIDATOR_NAME, formData.toString());

        String key = "time_on";
        ValidationUtils.validateTrimMatch(key, formData.get(key),"^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            VALIDATOR_NAME + "validatePrimAppFormData",
            messages, ValidationMessages.TR_PRIMAPP_TIMEON_VALIDATION);
        key = "time_off";
        ValidationUtils.validateTrimMatch(key, formData.get(key),"^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            VALIDATOR_NAME + "validatePrimAppFormData",
            messages, ValidationMessages.TR_PRIMAPP_TIMEOFF_VALIDATION);

        key = "PRIMARY_APPLICATION_CODE";
        if (StringUtils.isBlank(formData.get(key))) {
            CommonUtils.getLogger().error(
                "{}:validatePrimAppFormData found value of key({}) is blank", VALIDATOR_NAME, key);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_APPLICATIONCODE_VALIDATION);
        }

        ValidationUtils.validateUtm(formData, messages);

        key = "tr_location";
        if (!ValidationUtils.maxLengthLessThan(formData.get(key), 250)) {
            CommonUtils.getLogger().error("{}:validatePrimAppFormData found value of key({}) does not match " +
                "condition of maximum length less than 250", VALIDATOR_NAME, key);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.LOCATION_DESCRIPTION_VALIDATION);
        }
    }

    // single primary app TFM section
    public static void validateTFMsFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateTFMsFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String lpName = "tfmLP";
        String literName = "tfmLiterUsed";

        int numOfTFMs = Integer.valueOf(formData.get("numOfTFMs"));
        CommonUtils.getLogger().debug("{}:validateTFMsFormdata got numOfTFMs as({})", VALIDATOR_NAME, numOfTFMs);
        for (int i = 0; i < numOfTFMs; i++) {
            String lpKey = lpName + i;
            String lpValue = formData.get(lpKey);
            String literKey = literName + i;
            String literValue = formData.get(literKey);
            boolean isBlankLp = StringUtils.isBlank(lpValue);
            boolean isBlankLiter = StringUtils.isBlank(literValue);
            if (isBlankLp && isBlankLiter) {
                continue;
            }
            if (isBlankLp || isBlankLiter) {
                CommonUtils.getLogger().error("{}:validateTFMsFormdata found either {}({}) or {}({}) is blank",
                    VALIDATOR_NAME, lpKey, lpValue, literKey, literValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_PRIMTFM_VALIDATION);
            }
            verifyNoneNullNumberMatchExpression(literKey, literValue, ValidationUtils.EXPRESSION_NUMBER_N7DN2,
                "validateTFMsFormdata", messages, ValidationMessages.TR_TFM_LITERUSED_VALIDATION);
        }
    }

    private static void verifyNoneNullNumberMatchExpression(String key,
                                                            String value,
                                                            String expression,
                                                            String caller,
                                                            AppMessages messages,
                                                            ValidationMessages validationMessages) {
        if (!StringUtils.isBlank(value) && !ValidationUtils.isNumberAndMatchExpression(value, expression)) {
            CommonUtils.getLogger().error("{}:{} found {}({}) does not match the expected format ({})",
                VALIDATOR_NAME, caller, key, value, expression);
            ExceptionUtils.throwBadRequestResponseException(messages, validationMessages);
        }
    }

    // single primary app WP section
    public static void validateWPsFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateWPsFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String kgName = "wpKgUsed";
        String percAIName = "wpPercAI";
        
        int numOfWPs = Integer.valueOf(formData.get("numOfWPs"));
        CommonUtils.getLogger().debug("{}:validateWPsFormdata got numOfWPs as({})", VALIDATOR_NAME, numOfWPs);
        for (int i = 0; i < numOfWPs; i++) {
            String kgKey = kgName + i;
            String kgValue = formData.get(kgKey);
            String percAiKey = percAIName + i;
            String percAiValue = formData.get(percAiKey);
            boolean isBlankKg = StringUtils.isBlank(kgValue);
            boolean isBlankPercAi = StringUtils.isBlank(percAiValue);
            if (isBlankKg && isBlankPercAi) {
                continue;
            }
            if (isBlankKg || isBlankPercAi) {
                CommonUtils.getLogger().error("{}:validateWPsFormdata found either {}({}) or {}({}) is blank",
                    VALIDATOR_NAME, kgKey, kgValue, percAiKey, percAiValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_PRIMWP_VALIDATION);
            }
            verifyNoneNullNumberMatchExpression(kgKey, kgValue, ValidationUtils.EXPRESSION_NUMBER_N7DN2,
                "validateWPsFormdata", messages, ValidationMessages.TR_PRIMWP_KGUSED_VALIDATION);
            verifyNoneNullNumberMatchExpression(percAiKey, percAiValue, ValidationUtils.EXPRESSION_NUMBER_N2DN1,
                "validateWPsFormdata", messages, ValidationMessages.TR_PERCAI_VALIDATION);
        }
        
    }
    
    // single primary app EC section
    public static void validateECsFormdata(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateECsFormdata with formData {}", VALIDATOR_NAME, formData.toString());
        String literName = "ecLiterUsed";
        String percAIName = "ecPercAI";

        int numOfECs = Integer.valueOf(formData.get("numOfECs"));
        CommonUtils.getLogger().debug("{}:validateECsFormdata got numOfECs as({})", VALIDATOR_NAME, numOfECs);
        for (int i = 0; i < numOfECs; i++) {
            String literKey = literName + i;
            String literValue = formData.get(literKey);
            String percAiKey = percAIName + i;
            String percAiValue = formData.get(percAiKey);
            boolean isBlankLiter = StringUtils.isBlank(literValue);
            boolean isBlankPercAi = StringUtils.isBlank(percAiValue);
            if (isBlankLiter && isBlankPercAi) {
                continue;
            }
            if (isBlankLiter || isBlankPercAi) {
                CommonUtils.getLogger().error("{}:validateWPsFormdata found either {}({} or {}({}) is blank",
                    VALIDATOR_NAME, literKey, literValue, percAiKey, percAiValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_PRIMEC_VALIDATION);
            }
            verifyNoneNullNumberMatchExpression(literKey, literValue, ValidationUtils.EXPRESSION_NUMBER_N7DN2,
                "validateWPsFormdata", messages, ValidationMessages.TR_PRIMEC_LITERUSED_VALIDATION);
            verifyNoneNullNumberMatchExpression(percAiKey, percAiValue, ValidationUtils.EXPRESSION_NUMBER_N2DN1,
                "validateWPsFormdata", messages, ValidationMessages.TR_PERCAI_VALIDATION);
        }
    }
    
    // single secondary app edit page
    public static void validateSecondaryAppFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = "validateSecondaryAppFormData";
        CommonUtils.getLogger().debug("{}:{} with formData {}", VALIDATOR_NAME, methodName, formData.toString());

        String key = "branchToId";
        if (StringUtils.isBlank(formData.get(key))) {
            CommonUtils.getLogger().error("{}:{} found value of {} is blank", VALIDATOR_NAME, methodName, key);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_SECONDAPP_VALIDATION);
        }

        key = "tr_duration";
        String value = formData.get(key);
        if (StringUtils.isBlank(value)) {
            CommonUtils.getLogger().error("{}:{} found value of {} is blank", VALIDATOR_NAME, methodName, key);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_DURATION_VALIDATION);
        }

        ValidationUtils.validateTrimMatch(key, value, ValidationUtils.EXPRESSION_TIME_HHHMM,
            VALIDATOR_NAME + methodName, messages, ValidationMessages.TR_DURATION_VALIDATION);

        key = "SECONDARY_APPLICATION_CODE";
        if (StringUtils.isBlank(formData.get(key))) {
            CommonUtils.getLogger().error("{}:{} found value of {} is blank", VALIDATOR_NAME, methodName, key);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_APPLICATIONCODE_VALIDATION);
        }

        ValidationUtils.validateUtm(formData, messages);

        key = "tr_location";
        value = formData.get(key);
        if (!ValidationUtils.maxLengthLessThan(value, 250)) {
            CommonUtils.getLogger().error("{}:{} found {}({}) does not match condition of maximum length less than 250",
                VALIDATOR_NAME, methodName, key, value);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.LOCATION_DESCRIPTION_VALIDATION);
        }

        key = "numof_bars";
        value = formData.get(key);
        String expression = EXPRESSION_NUMBER_N3DN2;
        if (!ValidationUtils.isNumberAndMatchExpression(value, expression)) {
            CommonUtils.getLogger().error("{}:{} found {}({}) does not match format ({})",
                VALIDATOR_NAME, methodName, key, value, expression);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_TFM_NUMBARS_VALIDATION);
        }

        key = "weightof_bars";
        value = formData.get(key);
        expression = "^[0-9]{1}(\\.[0-9]{0,3})?$";
        if (!ValidationUtils.isNumberAndMatchExpression(value, expression)) {
            CommonUtils.getLogger().error("{}:{} found {}({}) does not match format ({})",
                VALIDATOR_NAME, methodName, key, value, expression);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_TFM_WEIGHTOFBAR_VALIDATION);
        }

        key = "litersUsed";
        verifyMatchingExpression(formData, key, ValidationUtils.EXPRESSION_NUMBER_N7DN2,
            methodName, messages, ValidationMessages.TR_TFM_LITERUSED_VALIDATION);
        key = "amount_used";
        verifyMatchingExpression(formData, key, ValidationUtils.EXPRESSION_NUMBER_N7DN2,
            methodName, messages, ValidationMessages.TR_AMOUNTUSED_VALIDATION);
        key = "gb_perc_ai";
        verifyMatchingExpression(formData, key, ValidationUtils.EXPRESSION_NUMBER_N2DN1,
            methodName, messages, ValidationMessages.TR_PERCAI_VALIDATION);
    }

    // single secondary app induced mortality page
    public static void validateInducedMortalityFormData(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug(
            "{}:validateInducedMortalityFormData with formData {}", VALIDATOR_NAME, formData.toString());

        String key = "tr_adults";
        String value = formData.get(key);
        if (!ValidationUtils.isIntegerWithMaxLength(value, 4)) {
            CommonUtils.getLogger().error("{}:validateInducedMortalityFormData found {}({}) does not match " +
                "condition of integer with maximum length 4", VALIDATOR_NAME, key, value);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_NUMOFADULTS_VALIDATION);
        }
        key = "tr_remarks";
        value = formData.get(key);
        if (!ValidationUtils.maxLengthLessThan(value, 250)) {
            CommonUtils.getLogger().error("{}:validateInducedMortalityFormData found {}({}) does not match " +
                "condition of maximum length less than 250", VALIDATOR_NAME, key, value);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.REMARKS_VALIDATION);
        }
    }

    // analysis desired concentrations
    public static void validateDesiredConcentrationsFormdata(Map<String, String> formData, AppMessages messages) {
        String methodName = "validateDesiredConcentrationsFormdata";
        CommonUtils.getLogger().debug("{}:{} with formData {}", VALIDATOR_NAME, methodName, formData.toString());

        String numOfString = formData.get("numOfCons");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{}:{} got numOfCons as({})", VALIDATOR_NAME, methodName, numOfString);
        String branchName = "trBranch";
        String stationName = "trStation";

        String minName = "appConMin";
        String maxName = "appConMax";

        int numOfCons = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfCons; i++) {
            String branchKey = branchName + i;
            String branchValue = formData.get(branchKey);
            String stationKey = stationName + i;
            String stationValue = formData.get(stationKey);
            if (CommonUtils.isAllBlank(branchValue, stationValue)) {
                if (numOfCons == 1) {
                    return;
                }

                CommonUtils.getLogger().error("{}:{} found empty value for {} and {} when numOfCons is not 1",
                    VALIDATOR_NAME, methodName, branchKey, stationKey);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_DEDIREDCON_VALIDATION);
            }

            if (CommonUtils.isAnyBlank(branchValue, stationValue)) {
                CommonUtils.getLogger().error("{}:{} found either {}({}) or {}({}) has empty value",
                    VALIDATOR_NAME, methodName, branchKey, branchValue, stationKey, stationValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_DEDIREDCON_VALIDATION);
            }

            verifyMatchingExpression(formData, minName + i, EXPRESSION_NUMBER_N3DN2,
                methodName, messages, ValidationMessages.TR_MINAPPCON_VALIDATION);
            verifyMatchingExpression(formData, maxName + i, EXPRESSION_NUMBER_N3DN2,
                methodName, messages, ValidationMessages.TR_MAXAPPCON_VALIDATION);
            verifyIsNumberAndBetween(formData, KEY_NICLO_PERC + i, 0, 100,
                methodName, messages, ValidationMessages.TR_NICLOPERC_VALIDATION);
        }
    }

    // analysis MLCs
    public static void validateMinLethalConcentrationsFormdata(Map<String, String> formData, AppMessages messages) {
        String methodName = "validateMinLethalConcentrationsFormdata";
        CommonUtils.getLogger().debug("{}:{} with formData {}", VALIDATOR_NAME, methodName, formData.toString());
        String numOfString = formData.get("numOfCons");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{}:{} got numOfCons as({})", VALIDATOR_NAME, methodName, numOfString);
        String branchName = "trBranch";
        String stationName = "trStation";

        String mlcName = "mlc";
        String exposureName = "exposure";

        int numOfCons = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfCons; i++) {
            String branchKey = branchName + i;
            String branchValue = formData.get(branchKey);
            String stationKey = stationName + i;
            String stationValue = formData.get(stationKey);
            if (CommonUtils.isAllBlank(branchValue, stationValue)) {
                if (numOfCons == 1) {
                    return;
                }

                CommonUtils.getLogger().error("{}:{} found empty value for {} and {} when numOfCons is not 1",
                    VALIDATOR_NAME, methodName, branchKey, stationKey);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_MLC_VALIDATION);
            }

            if (CommonUtils.isAnyBlank(branchValue, stationValue)) {
                CommonUtils.getLogger().error("{}:{} found either {}({}) or {}({}) has empty value",
                    VALIDATOR_NAME, methodName, branchKey, branchValue, stationKey, stationValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_MLC_VALIDATION);
            }

            verifyMatchingExpression(formData, mlcName + i, ValidationUtils.EXPRESSION_NUMBER_N3DN1,
                methodName, messages, ValidationMessages.TR_MLCVALUE_VALIDATION);
            verifyIsNumberAndBetween(formData, KEY_NICLO_PERC + i, 0, 100,
                methodName, messages, ValidationMessages.TR_NICLOPERC_VALIDATION);
            verifyIntegerWithMaxLength(formData, exposureName + i,2,
                methodName, messages, ValidationMessages.TR_EXPOSURETIME_VALIDATION);
        }
    }
    
    // analysis water chemistry
    public static void validateWaterChemsFormdata(Treatment tr, Map<String, String> formData,
                                                  AppMessages messages) throws ParseException {
        String methodName = "validateWaterChemsFormdata";
        CommonUtils.getLogger().debug("{}:{} with formData {}", VALIDATOR_NAME, methodName, formData.toString());
        String numOfString = formData.get("numOfWaterChems");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{}:{} got numOfWaterChems as({})", VALIDATOR_NAME, methodName, numOfString);
        String branchName = "trBranch";
        String stationName = "trStation";
        String sampleDateName = "sampleDate";
        String sampleTimeName = "sampleTime";

        String streamTempName = "streamTemp_";
        String disoOxyName = "disolOxy_";
        String ammoniaName = "ammonia_";
        String phName = "ph_";
        String alkaPhName = "alkaPh_";
        String phMlcName = "phMlc_";

        int numOfWaterChems = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfWaterChems; i++) {
            String branchKey = branchName + i;
            String branchValue = formData.get(branchKey);
            String stationKey = stationName + i;
            String stationValue = formData.get(stationKey);
            String sampleDateKey = sampleDateName + i;
            String sampleDateValue = formData.get(sampleDateKey);
            String sampleTimeKey = sampleTimeName + i;
            String sampleTimeValue = formData.get(sampleTimeKey);
            if (CommonUtils.isAllBlank(branchValue, stationValue, sampleDateValue, sampleTimeValue)) {
                if (numOfWaterChems == 1) {
                    return;
                }
                CommonUtils.getLogger().error(
                    "{}:{} found empty value {}({}), {}({}, {}({}) and {}({} when numerOfWaterChems is not 1",
                    VALIDATOR_NAME, methodName, branchKey, branchValue, stationKey, stationValue,
                    sampleDateKey, sampleDateValue, sampleTimeKey, sampleTimeValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_WATERCHEM_VALIDATION);
            }
            if (CommonUtils.isAnyBlank(branchValue, stationValue, sampleDateValue, sampleTimeValue)) {
                CommonUtils.getLogger().error("{}:{} found empty value of either {}({}), or {}({}, or {}({}), " +
                        "or {}({}", VALIDATOR_NAME, methodName, branchKey, branchValue,
                    stationKey, stationValue, sampleDateKey, sampleDateValue, sampleTimeKey, sampleTimeValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_WATERCHEM_VALIDATION);
            }

            validateTreatmentDate(tr, formData, sampleDateName + i, messages);

            String key = streamTempName + i;
            verifyMatchingExpression(formData, key, "^-?[0-9]{0,2}$",
                methodName, messages, ValidationMessages.TR_STREAMTEMP_VALIDATION);
            key = disoOxyName + i;
            verifyMatchingExpression(formData, key, "^[0-9]{0,3}(\\.[0-9]{0,1})?$",
                methodName, messages, ValidationMessages.TR_DISOXYGEN_VALIDATION);
            key = ammoniaName + i;
            verifyMatchingExpression(formData, key, "^[0-9]{0,3}(\\.[0-9]{0,1})?$",
                methodName, messages, ValidationMessages.TR_AMMONIA_VALIDATION);
            key = alkaPhName + i;
            verifyMatchingExpression(formData, key, "^[0-9]{0,3}(\\.[0-9]{0,1})?$",
                methodName, messages, ValidationMessages.TR_ALKALINITYPH_VALIDATION);
            key = phMlcName + i;
            verifyMatchingExpression(formData, key, "^[0-9]{0,2}(\\.[0-9]{0,1})?$",
                methodName, messages, ValidationMessages.TR_PHMLC_VALIDATION);

            key = phName + i;
            verifyIsNumberAndBetween(formData, key, 0, 99.99,
                methodName, messages, ValidationMessages.TR_PH_VALIDATION);
        }
    }

    // analysis discharge
    public static void validateDischargesFormdata(Treatment tr,
                                                  Map<String, String> formData,
                                                  AppMessages messages) throws ParseException {
        String methodName = "validateDischargesFormdata";
        CommonUtils.getLogger().debug("{}:{} with Treatment({}) and formData {}",
            VALIDATOR_NAME, methodName, tr.getId(), formData.toString());
        String numOfString = formData.get("numOfDischarges");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{}:{} got numOfDischarges as({})", VALIDATOR_NAME, methodName, numOfString);
        String branchName = "trBranch";
        String stationName = "trStation";
        String sampleDateName = "sampleDate";

        String dischargeName = "discharge_";
        String elapsedTimeName = "elapsedTime_";
        String cumulativeTimeName = "cumulativeTime_";

        int numOfDischarges = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfDischarges; i++) {
            String branchKey = branchName + i;
            String branchValue = formData.get(branchKey);
            String stationKey = stationName + i;
            String stationValue = formData.get(stationKey);
            String sampleDateKey = sampleDateName + i;
            String sampleDateValue = formData.get(sampleDateKey);
            if (CommonUtils.isAllBlank(branchValue, stationValue, sampleDateValue)) {
                if (numOfDischarges == 1) {
                    return;
                }
                CommonUtils.getLogger().error(
                    "{}:{} found empty value {}({}), {}({} and {}({} when numOfDischarges is not 1",
                    VALIDATOR_NAME, methodName, branchKey, branchValue,
                    stationKey, stationValue, sampleDateKey, sampleDateValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_DISCHARGE_VALIDATION);
            }
            if (CommonUtils.isAnyBlank(branchValue, stationValue, sampleDateValue)) {
                CommonUtils.getLogger().error("{}:{} found empty value of either {}({}), or {}({}, or {}({})",
                    VALIDATOR_NAME, methodName, branchKey, branchValue,
                    stationKey, stationValue, sampleDateKey, sampleDateValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_DISCHARGE_VALIDATION);
            }

            validateTreatmentDate(tr, formData, sampleDateName + i, messages);

            String key = dischargeName + i;
            verifyMatchingExpression(formData, key, ValidationUtils.EXPRESSION_NUMBER_N5DN3,
                methodName, messages, ValidationMessages.TR_DISCHARGEVALUE_VALIDATION);

            key = elapsedTimeName + i;
            ValidationUtils.validateTrimMatch(key, formData.get(key), ValidationUtils.EXPRESSION_TIME_24H,
                VALIDATOR_NAME + methodName, messages, ValidationMessages.TR_ELAPSEDTIME_VALIDATION);

            key = cumulativeTimeName + i;
            ValidationUtils.validateTrimMatch(key, formData.get(key), ValidationUtils.EXPRESSION_TIME_HHHMM,
                VALIDATOR_NAME + methodName, messages, ValidationMessages.TR_CUMULATIVETIME_VALIDATION);
        }
    }

    // analysis chemical analysis
    public static void validateChemicalAnalysisesFormdata(Treatment tr,
                                                          Map<String, String> formData,
                                                          AppMessages messages) throws ParseException {
        String methodName = "validateChemicalAnalysisesFormdata";
        CommonUtils.getLogger().debug("{}:{} with Treatment({}) and formData {}",
            VALIDATOR_NAME, methodName, tr.getId(), formData.toString());
        String numOfString = formData.get("numOfChemanalysis");
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        CommonUtils.getLogger().debug("{}:{} got numOfChemanalysis as({})", VALIDATOR_NAME, methodName, numOfString);
        String branchName = "trBranch";
        String stationName = "trStation";

        String sampleDateName = "sampleDate";
        String sampleTimeName = "sampleTime";

        String tfmConName = "tfmCon_";
        String niclosamideConName = "niclosamideCon_";

        int numOfChemanalysis = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfChemanalysis; i++) {
            String branchKey = branchName + i;
            String branchValue = formData.get(branchKey);
            String stationKey = stationName + i;
            String stationValue = formData.get(stationKey);
            String sampleDateKey = sampleDateName + i;
            String sampleDateValue = formData.get(sampleDateKey);
            String sampleTimeKey = sampleTimeName + i;
            String sampleTimeValue = formData.get(sampleTimeKey);
            if (CommonUtils.isAllBlank(branchValue, stationValue, sampleDateValue, sampleTimeValue)) {
                if (numOfChemanalysis == 1) {
                    return;
                }
                CommonUtils.getLogger().error(
                    "{}:{} found empty value {}({}), {}({} and {}({} when numOfChemanalysis is not 1",
                    VALIDATOR_NAME, methodName, branchKey, branchValue,
                    stationKey, stationValue, sampleDateKey, sampleDateValue);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.TR_CHEMANALYSIS_VALIDATION);
            }
            if (CommonUtils.isAnyBlank(branchValue, stationValue, sampleDateValue, sampleTimeValue)) {
                CommonUtils.getLogger().error("{}:{} found empty value of either {}({}), or {}({}, or {}({})",
                    VALIDATOR_NAME, methodName, branchKey, branchValue,
                    stationKey, stationValue, sampleDateKey, sampleDateValue);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.TR_CHEMANALYSIS_VALIDATION);
            }

            validateTreatmentDate(tr, formData, sampleDateName + i, messages);

            String key = tfmConName + i;
            verifyMatchingExpression(formData, key, EXPRESSION_NUMBER_N3DN2,
                methodName, messages, ValidationMessages.TR_TFMCON_VALIDATION);
            key = niclosamideConName + i;
            verifyMatchingExpression(formData, key, EXPRESSION_NUMBER_N3DN2,
                methodName, messages, ValidationMessages.TR_NICLOCON_VALIDATION);
        }
    }
}
