package ca.gc.dfo.slims.domain.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.Specie;

public interface SpecieRepository extends JpaRepository<Specie, Long> {

	Specie findBySpeciesCodeAndLarvalAssessmentId(String speciesCode, Long larvalAssessmentId);

}
