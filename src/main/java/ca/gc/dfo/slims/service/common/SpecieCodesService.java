package ca.gc.dfo.slims.service.common;

import ca.gc.dfo.slims.domain.entity.common.SpecieCode;
import ca.gc.dfo.slims.domain.repository.common.SpecieCodeRepository;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZHUY
 *
 */
@Service
public class SpecieCodesService {
    private static final String SERVICE_NAME = "SpecieCodesService";

    @Autowired
    private SpecieCodeRepository specieCodeRepository;

    @Autowired
    private AppMessages          messages;

    public List<SpecieCode> getAll() {
        return CommonUtils.getReturnListFromIterable(specieCodeRepository.findAll());
    }

    public SpecieCode getByCode(String code) {
        return specieCodeRepository.findBySpeciesCode(code);
    }

    public SpecieCode getById(Long id) {
        return CommonUtils.getIfPresent(
            specieCodeRepository.findById(id), SERVICE_NAME + "getById(SpecieCode)", id, messages);
    }

    public SpecieCode save(SpecieCode specieCode) {
        CommonUtils.getLogger().debug("{}:save with SpecieCode({})", SERVICE_NAME, specieCode.getId());
        SpecieCode returnCode = null;
        try {
            returnCode = specieCodeRepository.save(specieCode);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:save could not save SpecieCode({}) due to {}. return null.",
                SERVICE_NAME, specieCode.getShowText(), e.getMessage(), e);
        }
        return returnCode;
    }

}
