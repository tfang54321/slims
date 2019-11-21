package ca.gc.dfo.slims.domain.repository.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;

public interface BranchLenticRepository extends JpaRepository<BranchLentic, Long> {

	List<BranchLentic> findAllByStreamId(Long streamId);

	BranchLentic findByBranchLenticCode(String branchCode);

}
