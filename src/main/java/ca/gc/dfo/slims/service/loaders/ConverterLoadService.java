package ca.gc.dfo.slims.service.loaders;

import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.RefCodePair;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import ca.gc.dfo.slims.domain.entity.common.ReferCodeMapKey;
import ca.gc.dfo.slims.domain.repository.common.ReferCodeRepository;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.spring_commons.commons_web.api.Populator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConverterLoadService implements Populator {
    private static final String LOADER_NAME = "ConverterLoadService";

    @Autowired
    private ReferCodeRepository           referCodeRepository;

    private Map<ReferCodeMapKey, Long>    referCodeMap;
    private Map<Long, RefCode>            refCodeMap;

    public ConverterLoadService() {
        CommonUtils.getLogger().debug("{}:ConverterLoadService", LOADER_NAME);
        this.referCodeMap = new HashMap<>();
        this.refCodeMap = new HashMap<>();
    }

    private void populateDataMaps() {
        CommonUtils.getLogger().debug("{}:populateDataMaps", LOADER_NAME);
        List<ReferCode> allRefCodes = referCodeRepository.findAll();
        for (ReferCode refCode : allRefCodes) {
            ReferCodeMapKey referCodeMapKey = new ReferCodeMapKey(refCode.getCodeType(), refCode.getCodeValue());
            if (!referCodeMap.containsKey(referCodeMapKey)) {
                referCodeMap.put(referCodeMapKey, refCode.getId());
                refCodeMap.put(refCode.getId(),
                    new RefCode(refCode.getCodeType(), new RefCodePair(
                        refCode.getCodeValue(), refCode.getCodeAbbreviation(),

                        refCode.getCodeMeaningEn(), refCode.getCodeMeaningFr())));
            }
        }
    }

    @Override
    public void populate() {
        CommonUtils.getLogger().debug("{}:populate", LOADER_NAME);
        this.referCodeMap = new HashMap<>();
        this.refCodeMap = new HashMap<>();
        populateDataMaps();
    }

    public Map<ReferCodeMapKey, Long> getReferCodeMap() {
        return this.referCodeMap;
    }

    @SuppressWarnings("unused")
    public void setReferCodeMap(Map<ReferCodeMapKey, Long> referCodeMap) {
        this.referCodeMap = referCodeMap;
    }

    public Map<Long, RefCode> getRefCodeMap() {
        return this.refCodeMap;
    }

    @SuppressWarnings("unused")
    public void setRefCodeMap(Map<Long, RefCode> refCodeMap) {
        this.refCodeMap = refCodeMap;
    }
}
