package ca.gc.dfo.slims.service.location;

import ca.gc.dfo.slims.constants.RefCodeType;
import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import ca.gc.dfo.slims.domain.repository.location.StationRepository;
import ca.gc.dfo.slims.dto.common.location.StationDTO;
import ca.gc.dfo.slims.service.loaders.LocationLoadService;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;
import ca.gc.dfo.slims.utility.AppMessages;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.slims.utility.ExceptionUtils;
import ca.gc.dfo.slims.validation.location.LocationValidator;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Service
public class StationService {
    private static final String SERVICE_NAME = "StationService";

    @Autowired
    private StationRepository      stationRepository;
    @Autowired
    private BranchLenticService    branchLenticService;
    @Autowired
    private ResourceLoadService    resourceLoadService;
    @Autowired
    private LocationLoadService    locationLoadService;
    @Autowired
    private AppMessages            messages;

    public List<Station> getAll() {
        CommonUtils.getLogger().debug("{}:getAll", SERVICE_NAME);
        return CommonUtils.getReturnList(stationRepository.findAll());
    }

    public Station getById(Long id) {
        CommonUtils.getLogger().debug("{}:getById with id({})", SERVICE_NAME, id);
        return CommonUtils.getIfPresent(stationRepository.findById(id),
            SERVICE_NAME + "getById(Lake)", id, messages);
    }

    public Station save(Station station) {
        CommonUtils.getLogger().info(
            "{}:save with Station({} - {})", SERVICE_NAME, station.getId(), station.getShowText());
        station.setStationCode(station.getStationCode().toUpperCase());
        Station returnStation = null;
        try {
            returnStation = stationRepository.save(station);
            locationLoadService.addStation(returnStation);
        } catch (DataIntegrityViolationException e) {
            CommonUtils.getLogger().error(
                "{}:save could not save Station({} - {}) due to DataIntegrityViolationException: {}",
                SERVICE_NAME,  station.getId(), station.getShowText(), e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DUPLICATE_STATION_ERROR.getName()), e);
        } catch (Exception ex) {
            CommonUtils.getLogger().error("{}:save could not save Station({} - {}) due to: {}",
                SERVICE_NAME,  station.getId(), station.getShowText(), ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.SAVE_STATION_ERROR.getName()), ex);
        }

        CommonUtils.getLogger().debug(
            "{}:save return saved Station({} - {})", SERVICE_NAME,  returnStation.getId(), returnStation.getShowText());
        return returnStation;
    }

    public Station saveStationFromFormData(String branchlenticId, Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:saveStationFromFormData with branchlenticId({}) and formData {}",
            SERVICE_NAME, branchlenticId, formData.toString());
        Station station = buildStationFromFormData(formData);
        BranchLentic branchLentic = branchLenticService.getById(CommonUtils.getLongFromString(branchlenticId, "ID of BranchLentic"));
        station.setBranchLentic(branchLentic);
        return save(station);
    }

    public Station updateStationFromFormData(String id, Map<String, String> formData) {
        CommonUtils.getLogger().info("{}:updateStationFromFormData with id({}) and formData {}",
            SERVICE_NAME, id, formData.toString());
        long stationId = CommonUtils.getLongFromString(id, "ID of Station");
        Station station = getById(stationId);
        Station updatedStation = buildStationFromFormData(formData);
        updatedStation.setId(stationId);
        updatedStation.setBranchLentic(station.getBranchLentic());
        return save(updatedStation);
    }

    public void deleteById(Long id) {
        CommonUtils.getLogger().info("{}:deleteById with id({})", SERVICE_NAME, id);
        try {
            Station station = getById(id);
            stationRepository.deleteById(id);
            locationLoadService.removeStation(station);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Station by id({}) due to: {}",
                SERVICE_NAME, id, ex.getMessage(), ex);
            ExceptionUtils.throwResponseException(
                HttpStatus.CONFLICT, messages.get(ResponseMessages.DELETE_STATION_ERROR.getName()), ex);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:deleteById could not delete Station by id({}) due to: {}",
                SERVICE_NAME, id, e.getMessage(), e);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.GENERAL_EXCEPTION.getName()), e);
        }
        CommonUtils.getLogger().debug(
            "{}:deleteById completed successfully for Station with id({})", SERVICE_NAME, id);
    }

    private Station buildStationFromFormData(Map<String, String> formData) {
        CommonUtils.getLogger().debug(
            "{}:buildStationFromFormData with formData {}", SERVICE_NAME, formData.toString());
        LocationValidator.validateStationForm(formData, messages);

        Station station = new Station();
        station.setStationCode(CommonUtils.getStringValue(formData.get("stationCode")));
        station.setDescription(CommonUtils.getStringValue(formData.get("description")));
        station.setLatDeg(CommonUtils.getDoubleValue(formData.get("latDeg")));
        station.setLongDeg(CommonUtils.getDoubleValue(formData.get("longDeg")));
        station.setUtmEasting(CommonUtils.getDoubleValue(formData.get("utmEasting")));
        station.setUtmNorthing(CommonUtils.getDoubleValue(formData.get("utmNorthing")));
        station.setMapDatum(resourceLoadService.findRefCode(
            RefCodeType.MAP_DATUM.getName(), CommonUtils.getStringValue(formData.get("MAP_DATUM"))));
        station.setUtmZone(resourceLoadService.findRefCode(
            RefCodeType.UTM_ZONE.getName(), CommonUtils.getStringValue(formData.get("UTM_ZONE"))));
        station.setUtmBand(resourceLoadService.findRefCode(
            RefCodeType.UTM_BAND.getName(), CommonUtils.getStringValue(formData.get("UTM_BAND"))));

        CommonUtils.getLogger().debug("{}:buildStationFromFormData return built Station({} - {})",
            SERVICE_NAME, station.getId(), station.getShowText());
        return station;
    }

    public List<Station> getAllStationsByBranchLenticId(String branchLenticId) {
        CommonUtils.getLogger().debug("{}:getAllStationsByBranchLenticId with branchLenticId({})",
            SERVICE_NAME, branchLenticId);
        return CommonUtils.getReturnList(
            stationRepository.findAllByBranchLenticId(
                CommonUtils.getLongFromString(branchLenticId, "ID of BranchLentic")));
    }

    public List<StationDTO> getStationDTOsByStreamId(String streamId) {
        CommonUtils.getLogger().debug("{}:getStationDTOsByStreamId with streamId({})", SERVICE_NAME, streamId);
        List<BranchLentic> branchLentics = branchLenticService.getAllByStreamId(streamId);

        List<StationDTO> stationDTOs = new ArrayList<>();
        for (BranchLentic branchLentic : branchLentics) {
            List<Station> theStations = stationRepository.findAllByBranchLenticId(branchLentic.getId());
            List<StationDTO> theStationDTOs = getStationDTOs(branchLentic, theStations);
            
            stationDTOs.addAll(theStationDTOs);
        }
        CommonUtils.getLogger().debug("{}:getStationDTOsByStreamId return ({}) StationDTOs",
            SERVICE_NAME, stationDTOs.size());
        return stationDTOs;
    }

    private List<StationDTO> getStationDTOs(BranchLentic branchLentic, List<Station> stations) {
        CommonUtils.getLogger().debug("{}:getStationDTOs with BranchLentic({} - {}) and {{}) stations",
            SERVICE_NAME, branchLentic.getId(), branchLentic.getShowText(), stations.size());
        List<StationDTO> stationDTOs = new ArrayList<>();
        for (Station station : stations) {
            stationDTOs.add(new StationDTO(null, null, branchLentic, station));
        }
        CommonUtils.getLogger().debug("{}:getStationDTOs return ({}) StationDTOs",
            SERVICE_NAME, stationDTOs.size());
        return stationDTOs;
    }
}
