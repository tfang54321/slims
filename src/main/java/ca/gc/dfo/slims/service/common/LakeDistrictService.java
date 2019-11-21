package ca.gc.dfo.slims.service.common;

import ca.gc.dfo.slims.domain.entity.common.LakeDistrict;
import ca.gc.dfo.slims.domain.repository.common.LakeDistricsRepository;
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
public class LakeDistrictService {

    @Autowired
    private LakeDistricsRepository    ldRepository;

    @Autowired
    private AppMessages                messages;

    public List<LakeDistrict> getAll() {
        return CommonUtils.getReturnListFromIterable(ldRepository.findAll());
    }

    public LakeDistrict getById(Long id) {
        return CommonUtils.getIfPresent(
            ldRepository.findById(id), "LakeDistrictService:getById(LakeDistrict)", id, messages);
    }
}
