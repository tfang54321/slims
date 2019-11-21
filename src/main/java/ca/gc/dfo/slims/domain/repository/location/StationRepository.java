package ca.gc.dfo.slims.domain.repository.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.location.Station;

public interface StationRepository extends JpaRepository<Station, Long> {

	Station findByStationCode(String stationCode);

	List<Station> findStationByIdIn(List<Long> stationIds);

	List<Station> findAllByBranchLenticId(Long branchLenticId);

}
