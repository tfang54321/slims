package ca.gc.dfo.slims.domain.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.LakeDistrict;

public interface LakeDistricsRepository extends JpaRepository<LakeDistrict, Long> {

	
}
