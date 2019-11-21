package ca.gc.dfo.slims.domain.repository.treatments;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.treatments.TRPrimaryApplication;

public interface TRPrimaryAppRepository extends JpaRepository<TRPrimaryApplication, Long> {

	TRPrimaryApplication findByTreatmentIdAndBranchLenticIdAndStationFromId(Long trId, Long brId, Long sfId);
}
