package ca.gc.dfo.slims.service.location;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.repository.location.BranchLenticRepository;
import ca.gc.dfo.slims.dto.ResponseDTO;
import ca.gc.dfo.slims.service.loaders.LocationLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.validation.location.LocationValidator;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZHUY
 *
 */
@Service
public class BranchLenticService {
    private static final String SERVICE_NAME = "BranchLenticService";

    @Autowired
    private BranchLenticRepository    branchLenticRepository;
    @Autowired
    private LocationLoadService       locationLoadService;
    @Autowired
    private AppMessages               messages;

    public List<BranchLentic> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        return CommonUtils.getReturnList(branchLenticRepository.findAll());
    }

    public BranchLentic getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(branchLenticRepository.findById(id),
            SERVICE_NAME + "getById(BranchLentic)", id, messages);
    }

    public BranchLentic save(BranchLentic branchLentic) {
        CommonUtils.getLogger().info("{}:save with BranchLentic({} - {})",
            SERVICE_NAME, branchLentic.getId(), branchLentic.getShowText());
        LocationValidator.validateBranchLentic(branchLentic, messages);

        BranchLentic returnBranchLentic = null;
        try {
            branchLentic.setBranchLenticCode(branchLentic.getBranchLenticCode().toUpperCase());
            returnBranchLentic = branchLenticRepository.save(branchLentic);
            locationLoadService.addBranchLentic(branchLentic);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save BranchLentic({} - {}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME, branchLentic.getId(), branchLentic.getShowText(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_BRANCHLENTIC_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save BranchLentic({} - {})) due to: {}",
                SERVICE_NAME, branchLentic.getId(), branchLentic.getShowText(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_BRANCHLENTIC_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug("{}:save return saved BranchLentic({} - {})",
            SERVICE_NAME, returnBranchLentic.getId(), returnBranchLentic.getShowText());
        return returnBranchLentic;
    }

    public ResponseDTO<BranchLentic> updateBranchLentic(String id, BranchLentic updatedBranchLenticDetail) {
        CommonUtils.getLogger().info("{}:updateBranchLentic with id({}) and BranchLentic({} - {})",
            SERVICE_NAME, updatedBranchLenticDetail.getId(), updatedBranchLenticDetail.toString());
        BranchLentic branchlentic = getById(CommonUtils.getLongFromString(id, "ID of BranchLentic"));
        branchlentic.setBranchLenticCode(updatedBranchLenticDetail.getBranchLenticCode());
        branchlentic.setNameEn(updatedBranchLenticDetail.getNameEn());
        branchlentic.setNameFr(updatedBranchLenticDetail.getNameFr());
        branchlentic.setDescription(updatedBranchLenticDetail.getDescription());
        
        return new ResponseDTO<>(
            CommonUtils.INT_HTTP_SUCCESS,
            messages.get(ResponseMessages.UPDATE_BRANCHLENTIC_SUCCESS.getName()),
            save(branchlentic));
    }

    /**
     * Get all BranchLentics belong to one specific lake
     *
     */
    public List<BranchLentic> getAllByStreamId(String streamId) {
        CommonUtils.getLogger().debug("{}:getAllByStreamId with streamId({})", SERVICE_NAME, streamId);
        return CommonUtils.getReturnList(
            branchLenticRepository.findAllByStreamId(CommonUtils.getLongFromString(streamId, "ID of Stream")));
    }

    public void deleteById(Long id) {
        CommonUtils.getLogger().info("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            BranchLentic branch = getById(id);
            branchLenticRepository.deleteById(id);
            locationLoadService.removeBranchLentic(branch);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            CommonUtils.getLogger().error("{}:deleteById could not delete BranchLentic by id({}) due to: {}",
                SERVICE_NAME, id, ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_BRANCHLENTIC_ERROR.getName()), ex);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById could not delete BranchLentic by id({}) due to: {}",
                SERVICE_NAME, id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug(
            "{}:deleteById completed successfully for BranchLentic with id({})", SERVICE_NAME, id);
    }
}
