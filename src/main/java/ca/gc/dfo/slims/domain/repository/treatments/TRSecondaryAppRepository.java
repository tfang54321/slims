package ca.gc.dfo.slims.domain.repository.treatments;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.treatments.TRSecondaryApplication;

public interface TRSecondaryAppRepository extends JpaRepository<TRSecondaryApplication, Long>
{
	
}
