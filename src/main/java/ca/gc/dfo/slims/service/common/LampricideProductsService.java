package ca.gc.dfo.slims.service.common;

import ca.gc.dfo.slims.domain.entity.common.LampricideProducts;
import ca.gc.dfo.slims.domain.repository.common.LampricideProductsRepository;
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
public class LampricideProductsService {
    private static final String SERVICE_NAME = "LampricideProductsService";

    @Autowired
    private LampricideProductsRepository    lpRepository;

    @Autowired
    private AppMessages                     messages;

    public List<LampricideProducts> getAll() {
        return CommonUtils.getReturnListFromIterable(lpRepository.findAll());
    }

    public LampricideProducts getById(Long id) {
        return CommonUtils.getIfPresent(
            lpRepository.findById(id), SERVICE_NAME + "getById(LampricideProducts)", id, messages);
    }

    public LampricideProducts save(LampricideProducts lp) {
        CommonUtils.getLogger().debug("{}:save with LampricideProducts({})", SERVICE_NAME, lp.getId());
        LampricideProducts returnLp = null;
        try {
            returnLp = lpRepository.save(lp);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:save could not save LampricideProducts({}) due to {}. return null.",
                SERVICE_NAME, lp.getShowText(), e.getMessage(), e);
        }
        return returnLp;
    }
}
