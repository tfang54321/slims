package ca.gc.dfo.slims.domain.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.SpecieCode;

public interface SpecieCodeRepository extends JpaRepository<SpecieCode, Long>
{
	
	SpecieCode findBySpeciesCode(String speciesCode);
	
}
