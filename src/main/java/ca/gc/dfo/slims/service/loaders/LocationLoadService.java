package ca.gc.dfo.slims.service.loaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.gc.dfo.slims.utility.CommonUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import ca.gc.dfo.slims.domain.repository.location.BranchLenticRepository;
import ca.gc.dfo.slims.domain.repository.location.LakeRepository;
import ca.gc.dfo.slims.domain.repository.location.StationRepository;
import ca.gc.dfo.slims.domain.repository.location.StreamRepository;

/**
 * @author ZHUY
 *
 */
@SuppressWarnings("SuspiciousMethodCalls")
@Component
public class LocationLoadService implements InitializingBean {
    private static final String LOADER_NAME = "LocationLoadService";

    @Autowired
    private LakeRepository                          lakeRepository;
    @Autowired
    private StreamRepository                        streamRepository;
    @Autowired
    private BranchLenticRepository                  branchLenticRepository;
    @Autowired
    private StationRepository                       stationRepository;

    private static List<Lake>                       allLakes            = new ArrayList<>();
    private static Map<String, List<Stream>>        allStreams          = new HashMap<>();
    private static Map<String, List<BranchLentic>>  allBranchLentics    = new HashMap<>();
    private static Map<String, List<Station>>       allStations         = new HashMap<>();

    public LocationLoadService() {
        // do nothing
    }

    @Override
    public void afterPropertiesSet() {
        CommonUtils.getLogger().debug("{}:afterPropertiesSet", LOADER_NAME);
        populateLocations();
    }

    private void populateLocations() {
        CommonUtils.getLogger().debug("{}:populateLocations", LOADER_NAME);
        allLakes = lakeRepository.findAll();

        for (Lake lake : allLakes) {
            List<Stream> streams = streamRepository.findAllByLakeId(lake.getId());
            allStreams.put(lake.getId().toString(), streams);
        }

        for (Stream stream : streamRepository.findAll()) {
            List<BranchLentic> branchLentics = branchLenticRepository.findAllByStreamId(stream.getId());
            allBranchLentics.put(stream.getId().toString(), branchLentics);
        }
        
        for (BranchLentic branchLentic : branchLenticRepository.findAll()) {
            List<Station> stations = stationRepository.findAllByBranchLenticId(branchLentic.getId());
            allStations.put(branchLentic.getId().toString(), stations);
        }
    }

    public void addLake(Lake lake) {
        CommonUtils.getLogger().debug("{}:addLake", LOADER_NAME);
        if (!allLakes.contains(lake)) {
            allLakes.add(lake);
            if (!allStreams.containsKey(lake.getId())) {
                allStreams.put(lake.getId().toString(), new ArrayList<>());
            }
        }
    }

    public void removeLake(Lake lake) {
        CommonUtils.getLogger().debug("{}:removeLake", LOADER_NAME);
        if (allLakes.contains(lake)) {
            allLakes.remove(lake);
            allStreams.remove(lake.getId());
        }
    }

    public void addStream(Stream stream) {
        CommonUtils.getLogger().debug("{}:addStream", LOADER_NAME);
        String key = stream.getLake().getId().toString();
        if (!allStreams.containsKey(key)) {
            allStreams.put(key, new ArrayList<>());
        }
        allStreams.get(key).add(stream);
        allBranchLentics.put(stream.getId().toString(), new ArrayList<>());
    }

    public void removeStream(Stream stream) {
        CommonUtils.getLogger().debug("{}:removeStream", LOADER_NAME);
        String key = stream.getLake().getId().toString();
        if (allStreams.containsKey(key)) {
            allStreams.get(key).remove(stream);
            allBranchLentics.remove(stream.getId().toString());
        }
    }

    public void addBranchLentic(BranchLentic branch) {
        CommonUtils.getLogger().debug("{}:addBranchLentic", LOADER_NAME);
        String key = branch.getStream().getId().toString();
        if (!allBranchLentics.containsKey(key)) {
            allBranchLentics.put(key, new ArrayList<>());
        }
        allBranchLentics.get(key).add(branch);
        allStations.put(branch.getId().toString(), new ArrayList<>());
    }

    public void removeBranchLentic(BranchLentic branch) {
        CommonUtils.getLogger().debug("{}:removeBranchLentic", LOADER_NAME);
        String key = branch.getStream().getId().toString();
        if (allBranchLentics.containsKey(key)) {
            allBranchLentics.get(key).remove(branch);
            allStations.remove(branch.getId().toString());
        }
    }

    public void addStation(Station station) {
        CommonUtils.getLogger().debug("{}:addStation", LOADER_NAME);
        String key = station.getBranchLentic().getId().toString();
        if (!allStations.containsKey(key)) {
            allStations.put(key, new ArrayList<>());
        }
        allStations.get(key).add(station);
    }

    public void removeStation(Station station) {
        CommonUtils.getLogger().debug("{}:removeStation", LOADER_NAME);
        String key = station.getBranchLentic().getId().toString();
        if (allStations.containsKey(key)) {
            allStations.get(key).remove(station);
        }
    }

    /**
     * @return the allLakes
     */
    public List<Lake> getAllLakes() {
        return allLakes;
    }

    /**
     * @param allLakes
     *            the allLakes to set
     */
    @SuppressWarnings("unused")
    public static void setAllLakes(List<Lake> allLakes) {
        LocationLoadService.allLakes = allLakes;
    }

    /**
     * @return the allStreams
     */
    public Map<String, List<Stream>> getAllStreams() {
        return allStreams;
    }

    /**
     * @param allStreams the allStreams to set
     */
    @SuppressWarnings("unused")
    public static void setAllStreams(Map<String, List<Stream>> allStreams) {
        LocationLoadService.allStreams = allStreams;
    }

    /**
     * @return the allBranchLentics
     */
    public Map<String, List<BranchLentic>> getAllBranchLentics() {
        return allBranchLentics;
    }

    /**
     * @param allBranchLentics the allBranchLentics to set
     */
    @SuppressWarnings("unused")
    public static void setAllBranchLentics(Map<String, List<BranchLentic>> allBranchLentics) {
        LocationLoadService.allBranchLentics = allBranchLentics;
    }

    /**
     * @return the allStations
     */
    public Map<String, List<Station>> getAllStations() {
        return allStations;
    }

    /**
     * @param allStations the allStations to set
     */
    public static void setAllStations(Map<String, List<Station>> allStations) {
        LocationLoadService.allStations = allStations;
    }

}