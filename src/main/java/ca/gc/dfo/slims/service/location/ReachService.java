package ca.gc.dfo.slims.service.location;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.Reach;
import ca.gc.dfo.slims.domain.entity.common.location.ReachLengthAndUpdateYear;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.domain.repository.location.ReachRepository;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.validation.location.LocationValidator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class ReachService {
    private static final String SERVICE_NAME = "ReachService";

    @Autowired
    private ReachRepository    reachRepository;
    @Autowired
    private StreamService      streamService;
    @Autowired
    private StationService     stationService;
    @Autowired
    private AppMessages        messages;

    public List<Reach> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        return CommonUtils.getReturnList(reachRepository.findAll());
    }

    public Reach getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with id({})", SERVICE_NAME, id);
        Reach returnReach = CommonUtils.getIfPresent(
            reachRepository.findById(id),SERVICE_NAME + "getById(Reach)", id, messages);
        returnReach.getLengthAndUpdateYears().sort(Collections.reverseOrder());
        return returnReach;
    }

    public Reach save(Reach reach) {
        CommonUtils.getLogger().info("{}:save with Reach({} - {})", SERVICE_NAME, reach.getId(), reach.getShowText());
        LocationValidator.validateReach(reach, messages);

        Reach returnReach = null;
        try {
            returnReach = reachRepository.save(reach);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save Reach({} - {}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME,  reach.getId(), reach.getShowText(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_REACH_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save Reach({} - {}) due to: {}",
                SERVICE_NAME,  reach.getId(), reach.getShowText(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_REACH_ERROR.getName()), ex);
        }
        return returnReach;
    }

    public Reach updateReach(String id, Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:updateReach with id({}( and formData {}", SERVICE_NAME, id, formData.toString());
        Reach reach = getById(CommonUtils.getLongFromString(id, "ID of Reach"));
        Reach updatedReach = buildReachFromFormData(formData);
        reach.setReachName(updatedReach.getReachName());
        reach.setLengthAndUpdateYears(updatedReach.getLengthAndUpdateYears());
        return save(reach);
    }

    /**
     * Get all Reaches belong to one specific lake
     *
     */
    public List<Reach> getAllByStreamId(String streamId) {
        CommonUtils.getLogger().debug("{}:getAllByStreamId with streamId({})", SERVICE_NAME, streamId);
        return CommonUtils.getReturnList(
            reachRepository.findAllByStreamId(CommonUtils.getLongFromString(streamId, "ID of Stream")));
    }

    public void deleteById(Long id) {
        CommonUtils.getLogger().info("{}:deleteById with id({})", SERVICE_NAME, id);
        Reach reach = getById(id);
        if (reach.getAssignedStations() != null && reach.getAssignedStations().size() > 0) {
            CommonUtils.getLogger().error(
                "{}:deleteById found there are still station assigned to the to be deleted Reach({})",
                SERVICE_NAME, id);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.REACH_DELETE_FAILED_ASSIGNED_STATIONS.getName()));
        }

        try {
            reachRepository.deleteById(id);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Reach by id({}) due to: {}",
                SERVICE_NAME, id, ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_REACH_ERROR.getName()), ex);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Reach by id({}) due to: {}",
                SERVICE_NAME, id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug(
            "{}:deleteById completed successfully for Reach with id({})", SERVICE_NAME, id);
    }

    public Reach saveReachFromFormData(String streamId, Map<String, String> formData) {
        CommonUtils.getLogger().info(
            "{}:saveReachFromFormData with streamId({}) and formData {}", SERVICE_NAME, streamId, formData.toString());
        Reach reach = buildReachFromFormData(formData);
        Stream stream = streamService.getById(CommonUtils.getLongFromString(streamId, "ID of Stream"));
        reach.setStream(stream);
        return save(reach);
    }

    private Reach buildReachFromFormData(Map<String, String> formData) {
        CommonUtils.getLogger().debug("{}:buildReachFromFormData with formData {}", SERVICE_NAME, formData.toString());

        Reach reach = new Reach();
        reach.setReachCode(CommonUtils.getStringValue(formData.get("reachCode")));
        reach.setReachName(CommonUtils.getStringValue(formData.get("reachName")));
        
        List<ReachLengthAndUpdateYear> reachLengthAndUpdateYears = new ArrayList<>();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String theKey = entry.getKey();
            String value = entry.getValue();
            if (theKey.equalsIgnoreCase("reachCode")
                || theKey.equalsIgnoreCase("reachName")
                || StringUtils.isBlank(value)) {
                continue;
            }

            if (theKey.contains("editLength") && !theKey.contains("year")) {
                String yearValue = CommonUtils.getStringValue(formData.get(theKey + "year"));
                if (yearValue != null) {
                    ReachLengthAndUpdateYear reachLengthAndUpdateYear = new ReachLengthAndUpdateYear();
                    reachLengthAndUpdateYear.setReachLength(value);
                    reachLengthAndUpdateYear.setUpdatedYear(yearValue);

                    CommonUtils.getLogger().debug("{}:buildReachFromFormData add updateYear({}) and reachLength({})",
                        SERVICE_NAME, value, yearValue);
                    reachLengthAndUpdateYears.add(reachLengthAndUpdateYear);
                }
            }
        }
        reachLengthAndUpdateYears.sort(Collections.reverseOrder());
        reach.setLengthAndUpdateYears(reachLengthAndUpdateYears);

        CommonUtils.getLogger().debug("{}:buildReachFromFormData return built Reach({} - {})",
            SERVICE_NAME, reach.getId(), reach.getShowText());
        return reach;
    }

    public Reach updateReachAssignedStations(String reachId, Long[] stationIds) {
        CommonUtils.getLogger().debug("{}:updateReachAssignedStations with reachId({}) and ({} stationIds",
            SERVICE_NAME, reachId, stationIds.length);
        Reach reach = getById(CommonUtils.getLongFromString(reachId, "ID of Reach"));
        CommonUtils.getLogger().debug("{}:updateReachAssignedStations got Reach({} - {})",
            SERVICE_NAME, reach.getId(), reach.getShowText());
        if (stationIds.length > 0) {
            reach.getAssignedStations().clear();
            for (Long stationId : stationIds) {
                Station station = stationService.getById(stationId);
                CommonUtils.getLogger().debug("{}:updateReachAssignedStations assign Station({} - {})",
                    SERVICE_NAME, station.getId(), station.getShowText());
                reach.assignStation(station);
            }
        } else {
            CommonUtils.getLogger().debug("{}:updateReachAssignedStations set empty assigned stations", SERVICE_NAME);
            reach.setAssignedStations(new HashSet<>());
        }
        return save(reach);
    }
}
