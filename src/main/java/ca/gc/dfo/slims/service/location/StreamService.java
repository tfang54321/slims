package ca.gc.dfo.slims.service.location;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.domain.repository.location.StreamRepository;
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
public class StreamService {
    private static final String SERVICE_NAME = "StreamService";

    @Autowired
    private StreamRepository    streamRepository;
    @Autowired
    private LocationLoadService    locationLoadService;
    @Autowired
    private AppMessages            messages;

    public List<Stream> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        return CommonUtils.getReturnList(streamRepository.findAll());
    }

    public Stream getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(streamRepository.findById(id),
            SERVICE_NAME + "getById(Lake)", id, messages);
    }

    public Stream save(Stream stream) {
        CommonUtils.getLogger().info(
            "{}:save with Stream({} - {})", SERVICE_NAME, stream.getId(), stream.getShowText());
        LocationValidator.validateStream(stream, messages);
        Stream returnStream = null;
        try {
            returnStream = streamRepository.save(stream);
            locationLoadService.addStream(returnStream);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save Stream({} - {}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME,  stream.getId(), stream.getShowText(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_STATION_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save Stream({} - {}) due to: {}",
                SERVICE_NAME,  stream.getId(), stream.getShowText(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_STATION_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug(
            "{}:save return saved Stream({} - {})", SERVICE_NAME,  returnStream.getId(), returnStream.getShowText());
        return returnStream;
    }

    public Stream updateStream(String id, Stream updatedStreamDetail) {
        CommonUtils.getLogger().debug("{}:updateStream with id({}) and Stream({} - {})",
            SERVICE_NAME, id, updatedStreamDetail.getId(), updatedStreamDetail.getShowText());
        Stream stream = getById(CommonUtils.getLongFromString(id, "ID of Stream"));
        stream.setStreamCode(updatedStreamDetail.getStreamCode());
        stream.setNameEn(updatedStreamDetail.getNameEn());
        stream.setNameFr(updatedStreamDetail.getNameFr());
        return save(stream);
    }

    /**
     * Get all streams belong to one specific lake
     *
     */
    public List<Stream> getAllByLakeId(String lakeId) {
        CommonUtils.getLogger().debug("{}:getAllByLakeId with lakeId({}", SERVICE_NAME, lakeId);
        return CommonUtils.getReturnList(
            streamRepository.findAllByLakeId(CommonUtils.getLongFromString(lakeId, "ID of Lake")));
    }

    public void deleteById(Long id) {
        CommonUtils.getLogger().info("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            Stream stream = getById(id);
            streamRepository.deleteById(id);
            locationLoadService.removeStream(stream);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Stream by id({}) due to: {}",
                SERVICE_NAME, id, ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_STREAM_ERROR.getName()), ex);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Stream by id({}) due to: {}",
                SERVICE_NAME, id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug(
            "{}:deleteById completed successfully for Stream with id({})", SERVICE_NAME, id);
    }
}
