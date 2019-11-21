package ca.gc.dfo.slims.domain.repository.adultassessments;

import ca.gc.dfo.slims.dto.adultassessment.AdultAssessmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.adultassessments.AdultAssessment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AdultAssessmentRepository extends JpaRepository<AdultAssessment, Long>
{
	@Query("SELECT new ca.gc.dfo.slims.dto.adultassessment.AdultAssessmentDTO(AA.id, AA.sample.sampleCode, AA.sample.sampleStatus, AA.inspectDate, " +
			"AA.locationReference.lake.lakeCode, AA.locationReference.lake.nameEn, AA.locationReference.lake.nameFr," +
			"AA.locationReference.stream.streamCode, AA.locationReference.stream.nameEn, AA.locationReference.stream.nameFr," +
			"AA.locationReference.branchLentic.branchLenticCode, AA.locationReference.branchLentic.nameEn, AA.locationReference.branchLentic.nameFr," +
			"AA.locationReference.stationFrom.stationCode, AA.locationReference.stationFrom.name) FROM AdultAssessment AA")
	List<AdultAssessmentDTO> findAllList();
	
	@Query("SELECT new ca.gc.dfo.slims.dto.adultassessment.AdultAssessmentDTO(AA.id, AA.sample.sampleCode, AA.sample.sampleStatus, AA.inspectDate, " +
			"AA.locationReference.lake.lakeCode, AA.locationReference.lake.nameEn, AA.locationReference.lake.nameFr," +
			"AA.locationReference.stream.streamCode, AA.locationReference.stream.nameEn, AA.locationReference.stream.nameFr," +
			"AA.locationReference.branchLentic.branchLenticCode, AA.locationReference.branchLentic.nameEn, AA.locationReference.branchLentic.nameFr," +
			"AA.locationReference.stationFrom.stationCode, AA.locationReference.stationFrom.name) FROM AdultAssessment AA " +
			"WHERE AA.inspectDate >= :fromDate and AA.inspectDate < :toDate")
	List<AdultAssessmentDTO> findAllList(@Param("fromDate") Date fromDate,@Param("toDate") Date toDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.adultassessment.AdultAssessmentDTO(AA.id, AA.sample.sampleCode, AA.sample.sampleStatus, AA.inspectDate, " +
			"AA.locationReference.lake.lakeCode, AA.locationReference.lake.nameEn, AA.locationReference.lake.nameFr," +
			"AA.locationReference.stream.streamCode, AA.locationReference.stream.nameEn, AA.locationReference.stream.nameFr," +
			"AA.locationReference.branchLentic.branchLenticCode, AA.locationReference.branchLentic.nameEn, AA.locationReference.branchLentic.nameFr," +
			"AA.locationReference.stationFrom.stationCode, AA.locationReference.stationFrom.name) FROM AdultAssessment AA " +
			"WHERE AA.inspectDate >= :fromDate")
	List<AdultAssessmentDTO> findAllListAfterYear(@Param("fromDate") Date fromDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.adultassessment.AdultAssessmentDTO(AA.id, AA.sample.sampleCode, AA.sample.sampleStatus, AA.inspectDate, " +
			"AA.locationReference.lake.lakeCode, AA.locationReference.lake.nameEn, AA.locationReference.lake.nameFr," +
			"AA.locationReference.stream.streamCode, AA.locationReference.stream.nameEn, AA.locationReference.stream.nameFr," +
			"AA.locationReference.branchLentic.branchLenticCode, AA.locationReference.branchLentic.nameEn, AA.locationReference.branchLentic.nameFr," +
			"AA.locationReference.stationFrom.stationCode, AA.locationReference.stationFrom.name) FROM AdultAssessment AA " +
			"WHERE AA.inspectDate < :toDate")
	List<AdultAssessmentDTO> findAllListBeforeYear(@Param("toDate") Date toDate);

	AdultAssessment findFirstByOrderByInspectDateAsc();

	AdultAssessment findTopByOrderByIdDesc();
	
	@Query(value = "SELECT ADULTASSESSMENT_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextSequentialNumber();
}
