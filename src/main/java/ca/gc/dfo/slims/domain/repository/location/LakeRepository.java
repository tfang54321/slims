package ca.gc.dfo.slims.domain.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.location.Lake;

public interface LakeRepository extends JpaRepository<Lake, Long> {

	Lake findByLakeCode(String lakeCode);

}
