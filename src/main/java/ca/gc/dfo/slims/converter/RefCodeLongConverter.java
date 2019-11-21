package ca.gc.dfo.slims.converter;

import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.ReferCodeMapKey;
import ca.gc.dfo.slims.service.loaders.ConverterLoadService;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Converter
@Component
public class RefCodeLongConverter implements AttributeConverter<RefCode, Long> {
    private static final String CONVERTER_NAME = "RefCodeLongConverter";

    private static ConverterLoadService converterLoadService;

    @Autowired
    public void initConverterLoadService(ConverterLoadService converterLoadService) {
        RefCodeLongConverter.converterLoadService = converterLoadService;
    }

    @Override
    public Long convertToDatabaseColumn(RefCode meta) {
        try {
            Long referCodeId = null;
            if (meta != null) {
                Map<ReferCodeMapKey, Long> referCodeMap = converterLoadService.getReferCodeMap();
                ReferCodeMapKey referCodeMapKey = new ReferCodeMapKey(
                    meta.getCodeType(), meta.getCodePair().getCodeName());
                if (referCodeMap.containsKey(referCodeMapKey)) {
                    referCodeId = referCodeMap.get(referCodeMapKey);
                }
            }
            return referCodeId;
        } catch (Exception e) {
            CommonUtils.getLogger().error(
                "{}:convertToDatabaseColumn Got Exception when converting RefCode({})",
                CONVERTER_NAME, getPrintString(meta), e);
            // or throw an error
        }
        return null;
    }

    private String getPrintString(RefCode refCode) {
        if (refCode == null) {
            return "null RefCode";
        }
        return refCode.getCodeType() + refCode.getCodePair().getShowText();
    }

    @Override
    public RefCode convertToEntityAttribute(Long dbData) {
        try {
            if (converterLoadService !=  null && dbData != null) {
                Map<Long, RefCode> refCodeMap = converterLoadService.getRefCodeMap();
                if (refCodeMap.containsKey(dbData)) {
                    return refCodeMap.get(dbData);
                }
            }
            return null;
        } catch (Exception e) {
            CommonUtils.getLogger().error(
                "{}:convertToEntityAttribute Got Exception when converting Long value ({}}",
                CONVERTER_NAME, dbData, e);
            return null;
        }
    }

}
