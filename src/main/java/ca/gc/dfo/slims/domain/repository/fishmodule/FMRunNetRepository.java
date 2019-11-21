package ca.gc.dfo.slims.domain.repository.fishmodule;

import ca.gc.dfo.slims.domain.entity.fishmodule.FMRunNet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FMRunNetRepository extends JpaRepository<FMRunNet, Long> {
	
	List<FMRunNet> findByFishModuleIdOrderByRunNetNumberAsc(Long fishModuleId);
}
