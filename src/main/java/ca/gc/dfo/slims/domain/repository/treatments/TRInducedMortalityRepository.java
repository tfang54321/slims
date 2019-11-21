package ca.gc.dfo.slims.domain.repository.treatments;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.treatments.TRSecondAppInducedMortality;

public interface TRInducedMortalityRepository extends JpaRepository<TRSecondAppInducedMortality, Long>
{
	
}
