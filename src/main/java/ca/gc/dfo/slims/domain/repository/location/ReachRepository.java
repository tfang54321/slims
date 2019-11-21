package ca.gc.dfo.slims.domain.repository.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.location.Reach;

public interface ReachRepository extends JpaRepository<Reach, Long> {

	List<Reach> findAllByStreamId(Long streamId);

	Reach findByReachCode(String reachCode);

}
