package ca.gc.dfo.slims.validation.location;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.entity.common.location.Reach;
import ca.gc.dfo.slims.domain.entity.common.location.ReachLengthAndUpdateYear;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
public class LocationValidator {
    private static final String VALIDATOR_NAME = "LocationValidator";

    public static void validateLake(Lake lake, AppMessages messages) {
        String logHeader = String.format(
            "%s:validateLake with Lake(%d - %s)", VALIDATOR_NAME, lake.getId(), lake.getShowText());
        CommonUtils.getLogger().debug("{}", logHeader);

        String lakeCode = lake.getLakeCode();
        if (StringUtils.isBlank(lakeCode) || !StringUtils.isAlpha(lakeCode) || lakeCode.length() > 3) {
            CommonUtils.getLogger().error(
                "{} found lakeCode({}) does not match condition of is non-null letter and length greater than 3",
                logHeader, lakeCode);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.LAKE_CODE_VALIDATION);
        }

        validateName(lake.getNameEn(), true, logHeader, messages);
        validateName(lake.getNameFr(), false, logHeader, messages);
    }

    private static void validateName(String name,
                                     boolean isNameEn,
                                     String logHeader,
                                     AppMessages messages) {
        if (!StringUtils.isBlank(name) && name.length() > 45) {
            CommonUtils.getLogger().error(
                "{} found {}({}) does not match condition of is non-null and length is not greater than 45",
                logHeader, isNameEn ? "NameEn" : "NameFr", name);
            ExceptionUtils.throwBadRequestResponseException(
                messages, isNameEn ? ValidationMessages.EN_NAME_VALIDATION : ValidationMessages.FR_NAME_VALIDATION);
        }
    }

    public static void validateStream(Stream stream, AppMessages messages) {
        String logHeader = String.format(
            "%s:validateStream with Stream(%d - %s)", VALIDATOR_NAME, stream.getId(), stream.getShowText());
        CommonUtils.getLogger().debug("{}", logHeader);

        String streamCode = stream.getStreamCode();
        if (StringUtils.isBlank(streamCode) || !NumberUtils.isDigits(streamCode) || streamCode.length() > 4) {
            CommonUtils.getLogger().error(
                "{} found streamCode({}) does not match condition of is digits and length is not greater than 4",
                logHeader, streamCode);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.STREAM_CODE_VALIDATION);
        }

        validateName(stream.getNameEn(), true, logHeader, messages);
        validateName(stream.getNameFr(), false, logHeader, messages);
    }

    public static void validateReach(Reach reach, AppMessages messages) {
        String logHeader = String.format(
            "%s:validateReach with Reach(%d - %s)", VALIDATOR_NAME, reach.getId(), reach.getShowText());
        CommonUtils.getLogger().debug("{}", logHeader);

        String reachCode = reach.getReachCode();
        if (StringUtils.isBlank(reachCode) || !NumberUtils.isDigits(reachCode) || reachCode.length() > 4) {
            CommonUtils.getLogger().error(
                "{} found reachCode({}) does not match condition of is digits and length is not greater than 4",
                logHeader, reachCode);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.REACH_CODE_VALIDATION);
        }

        String reachName = reach.getReachName();
        if (!StringUtils.isBlank(reachName) && reachName.length() > 100) {
            CommonUtils.getLogger().error(
                "{} found reachName({}) does not match condition of length is not greater than 100",
                logHeader, reachName);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.REACH_CODE_VALIDATION);
        }

        if (reach.getLengthAndUpdateYears().size() > 0) {
            List<ReachLengthAndUpdateYear> lenAndYears = reach.getLengthAndUpdateYears();
            if (new HashSet<>(lenAndYears).size() != lenAndYears.size()) {
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.REACH_LENGTH_VALIDATION);
            }
            
            for (ReachLengthAndUpdateYear lenYear : lenAndYears) {
                String reachLen = lenYear.getReachLength();
                if (!NumberUtils.isParsable(reachLen)
                    || reachLen.length() > 8
                    || Double.parseDouble(reachLen) > 999999.9) {
                    CommonUtils.getLogger().error("{} found reachLen({}) does not match condition of " +
                        "length is not greater than 8 and value is not greater than 999999.9", logHeader, reachLen);
                    ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.REACH_LENGTH_VALIDATION);
                }

                String reachYear = lenYear.getUpdatedYear();
                int currentYear = Year.now().getValue();
                if (reachYear.length() != 4
                    || !NumberUtils.isDigits(reachYear)
                    || Integer.parseInt(reachYear) > currentYear) {
                    CommonUtils.getLogger().error("{} found reachYear({}) does not match condition of " +
                        "length is 4 and value is not greater than currentYear({})", logHeader, reachLen, currentYear);
                    ExceptionUtils.throwBadRequestResponseException(
                        messages, ValidationMessages.REACH_UPDATEYEAR_VALIDATION);
                }
            }
        }
    }

    public static void validateBranchLentic(BranchLentic branchLentic, AppMessages messages) {
        String logHeader = String.format("%s:validateBranchLentic with BranchLentic(%d - %s)",
            VALIDATOR_NAME, branchLentic.getId(), branchLentic.getShowText());
        CommonUtils.getLogger().debug("{}", logHeader);

        String blCode = branchLentic.getBranchLenticCode();
        if (StringUtils.isBlank(blCode)
            || !StringUtils.isAlphanumeric(blCode)
            || blCode.length() > 4
            || !StringUtils.isAlpha(blCode.substring(0, 1))) {
            CommonUtils.getLogger().error("{} found branchLenticCode({}) does not match condition of starts with " +
                "letter string and length is not greater than 4", logHeader, blCode);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.BRANCHLENTIC_CODE_VALIDATION);
        }

        validateName(branchLentic.getNameEn(), true, logHeader, messages);
        validateName(branchLentic.getNameFr(), false, logHeader, messages);
    }

    public static void validateStationForm(Map<String, String> formData, AppMessages messages) {
        CommonUtils.getLogger().debug("{}:validateStationForm with formData", VALIDATOR_NAME, formData.toString());

        String key = "stationCode";
        String value = formData.get(key);
        if (StringUtils.isBlank(value) || !StringUtils.isAlphanumeric(value) || value.length() > 4) {
            CommonUtils.getLogger().error("{}:validateStationForm found {}({}) does not match condition of " +
                "letter string and length is not greater than 4", VALIDATOR_NAME, key, value);
            ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.STATION_CODE_VALIDATION);
        }

        key = "description";
        value = formData.get(key);
        if (!StringUtils.isBlank(value) && value.length() > 45) {
            CommonUtils.getLogger().error("{}:validateStationForm found {}({}) does not match condition of " +
                "length is not greater than 45", VALIDATOR_NAME, key, value);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.STATION_DESCRIPTION_VALIDATION);
        }

        ValidationUtils.validateUTMLatDeg(formData.get("latDeg"), messages);
        ValidationUtils.validateUTMLongDeg(formData.get("longDeg"), messages);

        ValidationUtils.validateUtmEasting(formData.get("utmEasting"), messages);
        ValidationUtils.validateUtmNorthinging(formData.get("utmNorthing"), messages);
    }
}
