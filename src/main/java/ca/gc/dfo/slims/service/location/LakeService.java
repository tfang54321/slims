package ca.gc.dfo.slims.service.location;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.repository.location.LakeRepository;
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
public class LakeService {
    private static final String SERVICE_NAME = "LakeService";

    @Autowired
    private LakeRepository        lakeRepository;
    @Autowired
    private LocationLoadService   locationLoadService;
    @Autowired
    private AppMessages           messages;

    public List<Lake> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        return CommonUtils.getReturnList(lakeRepository.findAll());
    }

    public Lake getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(lakeRepository.findById(id),
            SERVICE_NAME + "getById(Lake)", id, messages);
    }

    public Lake save(Lake lake) {
        CommonUtils.getLogger().info("{}:save with Lake({} - {})", SERVICE_NAME, lake.getId(), lake.getShowText());
        LocationValidator.validateLake(lake, messages);
        Lake returnLake = null;
        try {
            lake.setLakeCode(lake.getLakeCode().toUpperCase());
            returnLake = lakeRepository.save(lake);
            locationLoadService.addLake(returnLake);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save Lake({} - {}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME,  lake.getId(), lake.getShowText(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_LAKE_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save Lake({} - {}) due to: {}",
                SERVICE_NAME,  lake.getId(), lake.getShowText(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_LAKE_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug(
            "{}:save return saved Lake({} - {})", SERVICE_NAME,  returnLake.getId(), returnLake.getShowText());
        return returnLake;
    }

    public Lake updateLake(String id, Lake updatedLakeDetail) {
        CommonUtils.getLogger().info("{}:updateLake with id({}) and Lake({} - {})",
            SERVICE_NAME, id, updatedLakeDetail.getId(), updatedLakeDetail.getShowText());
        Lake lake = getById(CommonUtils.getLongFromString(id, "ID of Lake"));
        lake.setLakeCode(updatedLakeDetail.getLakeCode());
        lake.setNameEn(updatedLakeDetail.getNameEn());
        lake.setNameFr(updatedLakeDetail.getNameFr());
        return save(lake);
    }

    public void deleteById(Long id) {
        CommonUtils.getLogger().info("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            Lake lake = getById(id);
            lakeRepository.deleteById(id);
            locationLoadService.removeLake(lake);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Lake by id({}) due to: {}",
                SERVICE_NAME, id, ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_LAKE_ERROR.getName()), ex);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Lake by id({}) due to: {}",
                SERVICE_NAME, id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug(
            "{}:deleteById completed successfully for Lake with id({})", SERVICE_NAME, id);
    }
}
