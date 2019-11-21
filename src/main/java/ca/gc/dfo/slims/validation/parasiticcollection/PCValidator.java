package ca.gc.dfo.slims.validation.parasiticcollection;

import ca.gc.dfo.slims.constants.ValidationMessages;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.utility.ValidationUtils;
import ca.gc.dfo.slims.validation.BaseValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZHUY
 *
 */
public class PCValidator extends BaseValidator {
    private static final String VALIDATOR_NAME = "PCValidator";

    private static final String EXPRESSION_NUMBER_N2D3 = "^[0-9]{1,2}(\\.[0-9]{0,3})?$";

    private static final String FORMAT_NUMBER_OF_ATTATCHMENT_INDI = "attachment%d_numOfIndi";

    private static final String KEY_HOST_SPECIE = "hostSpecie";
    private static final String KEY_HOST_TYPE = "hostType";
    private static final String KEY_ID_NUMBER = "idNumber";
    private static final String KEY_INDI_SPECIE_NAME_SUFFIX = "_indiSpecie";
    private static final String KEY_INDI_LENGTH_NAME_PREFIX = "length_";
    private static final String KEY_INDI_WEIGHT_NAME_PREFIX = "weight_";
    private static final String KEY_NUM_OF_ATTACHMENTS = "numOfAttachments";
    private static final String KEY_SEA_LAMP_SAMPLED = "seaLampSampled";
    private static final String KEY_SILVER_LAMP_SAMPLED = "silverLampSampled";
    private static final String KEY_TOTAL_ATTACHED = "totalAttached";


    public static void validatePcFormData(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validatePcFormData";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());

        verifyBlank(formData, "collectedDate",
            methodName, messages, ValidationMessages.PC_COLLECTEDDATE_VALIDATION);
        verifyBlank(formData, "lakeDistrictId",
            methodName, messages, ValidationMessages.PC_LAKEDISTRICT_VALIDATION);
        verifyBlank(formData, "firsherName",
            methodName, messages, ValidationMessages.PC_FISHERNAME_VALIDATION);

        verifyMaxLengthLessThan(formData,"firsherName",45,
            methodName, messages, ValidationMessages.PC_FISHERNAME_VALIDATION);

        verifyIntegerWithMaxLength(formData, "grid_number", 4,
            methodName, messages, ValidationMessages.PC_GRIDNUM_VALIDATION);
        verifyIntegerWithMaxLength(formData, "grid_number_est", 4,
            methodName, messages, ValidationMessages.PC_GRIDNUMEST_VALIDATION);
        verifyIntegerWithMaxLength(formData, "waterDepth", 5,
            methodName, messages, ValidationMessages.PC_MAXDEPTH_VALIDATION);
        verifyIntegerWithMaxLength(formData, "maxDepth", 5,
            methodName, messages, ValidationMessages.PC_MAXDEPTH_VALIDATION);
        verifyIntegerWithMaxLength(formData, "minDepth", 5,
            methodName, messages, ValidationMessages.PC_MINDEPTH_VALIDATION);
        verifyIntegerWithMaxLength(formData, "avgDepth", 5,
            methodName, messages, ValidationMessages.PC_AVGDEPTH_VALIDATION);

        verifyMatchingExpression(formData, "meshSize", EXPRESSION_NUMBER_N2D3,
            methodName, messages, ValidationMessages.PC_MESHSIZE_VALIDATION);
        verifyMatchingExpression(formData, "maxMesh", EXPRESSION_NUMBER_N2D3,
            methodName, messages, ValidationMessages.PC_MAXMESH_VALIDATION);
        verifyMatchingExpression(formData, "minMesh", EXPRESSION_NUMBER_N2D3,
            methodName, messages, ValidationMessages.PC_MINMESH_VALIDATION);
        verifyMatchingExpression(formData, "avgMesh", EXPRESSION_NUMBER_N2D3,
            methodName, messages, ValidationMessages.PC_AVGMESH_VALIDATION);
    }

    public static void validateAttachmentsFormdata(Map<String, String> formData, AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateAttachmentsFormdata";
        CommonUtils.getLogger().debug("{} with formData {}", methodName, formData.toString());

        String numOfString = formData.get(KEY_NUM_OF_ATTACHMENTS);
        if (StringUtils.isBlank(numOfString)) {
            return;
        }
        CommonUtils.getLogger().debug("{} got {} as ({})", methodName, KEY_NUM_OF_ATTACHMENTS, numOfString);

        Set<String> allAttachments = new HashSet<>();
        Set<String> hostSpecies = new HashSet<>();

        int numOfHostSpecies = 0;

        int numOfAttachments = CommonUtils.getIntValue(numOfString);
        for (int i = 0; i < numOfAttachments; i++) {
            String idNumberKey = KEY_ID_NUMBER + i;
            String idNumberValue = formData.get(idNumberKey);
            if (StringUtils.isBlank(idNumberValue)) {
                if (numOfAttachments == 1) {
                    return;
                }
                CommonUtils.getLogger().error("{} found {}({}) is blank when numOfAttachments({} is not 1",
                    methodName, idNumberKey, idNumberValue, numOfString);
                ExceptionUtils.throwBadRequestResponseException(messages, ValidationMessages.PC_IDNUMBER_VALIDATION);
            }
            allAttachments.add(idNumberValue);

            verifyBlank(formData, KEY_HOST_TYPE + i,
                methodName, messages, ValidationMessages.PC_HOSTTYPE_VALIDATION);

            String hostSpecieValue = formData.get(KEY_HOST_SPECIE + i);
            if (!StringUtils.isBlank(hostSpecieValue)) {
                ++numOfHostSpecies;
                hostSpecies.add(hostSpecieValue.trim());
            }

            verifyIntegerWithMaxLength(formData, KEY_SEA_LAMP_SAMPLED + i, 4,
                methodName, messages, ValidationMessages.PC_SEALAMPREYSAMPLED_VALIDATION);
            verifyIntegerWithMaxLength(formData, KEY_SILVER_LAMP_SAMPLED + i, 4,
                methodName, messages, ValidationMessages.PC_SILVERLAMPREYSAMPLED_VALIDATION);
            verifyIntegerWithMaxLength(formData, KEY_TOTAL_ATTACHED + i, 4,
                methodName, messages, ValidationMessages.PC_TOTALATTACHED_VALIDATION);

            validateIndividuals(formData, i, idNumberKey, messages);
        }

        int allAttachmentsSize = allAttachments.size();
        if (numOfAttachments > 1 && allAttachmentsSize != numOfAttachments) {
            CommonUtils.getLogger().error("{} found all attachment size ({}) does not match passed in {}({})",
                methodName, allAttachments, KEY_NUM_OF_ATTACHMENTS, numOfAttachments);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.PC_ATTACHMENTS_VALIDATION);
        }
        int hostSpeciesSize = hostSpecies.size();
        if (hostSpeciesSize != numOfHostSpecies) {
            CommonUtils.getLogger().error("{} found hostSpecies size ({}) does not match numOfHostSpecies({})",
                methodName, hostSpeciesSize, numOfHostSpecies);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.PC_HOSTSPECIE_VALIDATION);
        }
    }

    private static void validateIndividuals(Map<String, String> formData,
                                            int index,
                                            String idNumberKey,
                                            AppMessages messages) {
        String methodName = VALIDATOR_NAME + ":validateIndividuals";
        CommonUtils.getLogger().debug("{}[{}] with formData {}", methodName, index, formData.toString());

        String numOfStringKey = String.format(FORMAT_NUMBER_OF_ATTATCHMENT_INDI, index);
        String numOfString = formData.get(numOfStringKey);
        if (StringUtils.isBlank(numOfString)) {
            return;
        }

        List<String> allIndividuals = new ArrayList<>();
        CommonUtils.getLogger().debug("{} got {} as ({})", methodName, numOfStringKey, numOfString);
        int numOfIndividuals = CommonUtils.getIntValue(numOfString);
        for (int j = 0; j < numOfIndividuals; j++) {
            String key = idNumberKey + KEY_INDI_SPECIE_NAME_SUFFIX + j;
            String value = formData.get(key);
            if (StringUtils.isBlank(value)) {
                if (numOfIndividuals == 1) {
                    break;
                }
                CommonUtils.getLogger().error("{} found {}({}) is blank", methodName, key, value);
                ExceptionUtils.throwBadRequestResponseException(
                    messages, ValidationMessages.PC_INDISPECIECODE_VALIDATION);
            }
            allIndividuals.add(value);

            verifyMatchingExpression(
                formData, KEY_INDI_LENGTH_NAME_PREFIX + index + j, ValidationUtils.EXPRESSION_NUMBER_N4DN1,
                methodName, messages, ValidationMessages.FISHINDI_LENGTH_VALIDATION);
            verifyMatchingExpression(
                formData, KEY_INDI_WEIGHT_NAME_PREFIX + index + j, ValidationUtils.EXPRESSION_NUMBER_N6DN4,
                methodName, messages, ValidationMessages.FISHINDI_WEIGHT_VALIDATION);
        }

        int allIndSize = allIndividuals.size();
        if (numOfIndividuals > 1 && allIndSize != numOfIndividuals) {
            CommonUtils.getLogger().error("{} found all individual size ({}) does not match passed in {}({})",
                methodName, allIndSize, numOfStringKey, numOfIndividuals);
            ExceptionUtils.throwBadRequestResponseException(
                messages, ValidationMessages.FISHOBSCOLL_ALLSPECIES_VALIDATION);
        }
    }
}
