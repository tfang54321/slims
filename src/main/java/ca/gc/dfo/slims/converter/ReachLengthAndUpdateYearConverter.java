package ca.gc.dfo.slims.converter;

import ca.gc.dfo.slims.domain.entity.common.location.ReachLengthAndUpdateYear;
import ca.gc.dfo.slims.utility.CommonUtils;
import com.google.gson.Gson;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ZHUY
 *
 */
@Converter(autoApply = true)
public class ReachLengthAndUpdateYearConverter implements AttributeConverter<List<ReachLengthAndUpdateYear>, String> {
    private static final String CONVERTER_NAME = "ReachLengthAndUpdateYearConverter";

    @Override
    public String convertToDatabaseColumn(List<ReachLengthAndUpdateYear> objList) {
        try {
            Gson gson = new Gson();
            return gson.toJson(objList);
        } catch (Exception e) {
            CommonUtils.getLogger().error(
                "{}:convertToDatabaseColumn Got Exception when converting ({}}",
                CONVERTER_NAME, getPrintString(objList), e);
            // or throw an error
        }
        return null;
    }

    private String getPrintString(List<ReachLengthAndUpdateYear> objList) {
        StringBuilder stringBuilder = new StringBuilder("[");
        if (objList != null) {
            for (ReachLengthAndUpdateYear reachLengthAndUpdateYear : objList) {
                stringBuilder.append("(")
                    .append(reachLengthAndUpdateYear.getReachLength())
                    .append(reachLengthAndUpdateYear.getUpdatedYear())
                    .append(")");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public List<ReachLengthAndUpdateYear> convertToEntityAttribute(String dbData) {
        try {
            Gson gson = new Gson();
            if (dbData == null) {
                dbData = "[]";
            } else if (!dbData.trim().startsWith("[")) {
                String[] lenAndYear = dbData.trim().split("\\s+");
                if (lenAndYear.length <= 1) {
                    dbData = "[]";
                } else {
                    if (lenAndYear[0].equalsIgnoreCase("null")
                        && lenAndYear[1].equalsIgnoreCase("null")) {
                        dbData = "[]";
                    } else {
                        dbData = "[{\"reachLength\":" + lenAndYear[0] + ",\"updatedYear\":" + lenAndYear[1] + "}]";
                    }
                }
            }

            ReachLengthAndUpdateYear[] objArray = gson.fromJson(dbData, ReachLengthAndUpdateYear[].class);
            
            return new ArrayList<>(Arrays.asList(objArray));
        } catch (Exception e) {
            CommonUtils.getLogger().error(
                "{}:convertToEntityAttribute Got Exception when converting String ({}}",
                CONVERTER_NAME, dbData, e);
        }
        return null;
    }
    
}
