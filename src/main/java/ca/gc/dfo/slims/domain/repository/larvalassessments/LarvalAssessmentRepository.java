package ca.gc.dfo.slims.domain.repository.larvalassessments;

import ca.gc.dfo.slims.dto.larvalassessments.LarvalAssessmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.larvalassessments.LarvalAssessment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface LarvalAssessmentRepository extends JpaRepository<LarvalAssessment, Long>
{
	
	@Query("SELECT new ca.gc.dfo.slims.dto.larvalassessments.LarvalAssessmentDTO(LA.id, LA.sample.sampleStatus, LA.sample.sampleDate, LA.sample.sampleCode, " + "LA.locationReference.lake.lakeCode, LA.locationReference.lake.nameEn, LA.locationReference.lake.nameFr," + "LA.locationReference.stream.streamCode, LA.locationReference.stream.nameEn, LA.locationReference.stream.nameFr,"
	        + "LA.locationReference.branchLentic.branchLenticCode, LA.locationReference.branchLentic.nameEn, LA.locationReference.branchLentic.nameFr," + "LA.locationReference.stationFrom.stationCode, LA.locationReference.stationFrom.name," + "st.stationCode, st.name) FROM LarvalAssessment LA left outer join LA.locationReference.stationTo st")
	List<LarvalAssessmentDTO> findAllList();
	
	@Query("SELECT new ca.gc.dfo.slims.dto.larvalassessments.LarvalAssessmentDTO(LA.id, LA.sample.sampleStatus, LA.sample.sampleDate, LA.sample.sampleCode, " + "LA.locationReference.lake.lakeCode, LA.locationReference.lake.nameEn, LA.locationReference.lake.nameFr," + "LA.locationReference.stream.streamCode, LA.locationReference.stream.nameEn, LA.locationReference.stream.nameFr,"
			+ "LA.locationReference.branchLentic.branchLenticCode, LA.locationReference.branchLentic.nameEn, LA.locationReference.branchLentic.nameFr," + "LA.locationReference.stationFrom.stationCode, LA.locationReference.stationFrom.name," + "st.stationCode, st.name) FROM LarvalAssessment LA left outer join LA.locationReference.stationTo st "
			+ "WHERE LA.sample.sampleDate >= :fromDate and LA.sample.sampleDate < :toDate")
	List<LarvalAssessmentDTO> findAllList(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.larvalassessments.LarvalAssessmentDTO(LA.id, LA.sample.sampleStatus, LA.sample.sampleDate, LA.sample.sampleCode, " + "LA.locationReference.lake.lakeCode, LA.locationReference.lake.nameEn, LA.locationReference.lake.nameFr," + "LA.locationReference.stream.streamCode, LA.locationReference.stream.nameEn, LA.locationReference.stream.nameFr,"
			+ "LA.locationReference.branchLentic.branchLenticCode, LA.locationReference.branchLentic.nameEn, LA.locationReference.branchLentic.nameFr," + "LA.locationReference.stationFrom.stationCode, LA.locationReference.stationFrom.name," + "st.stationCode, st.name) FROM LarvalAssessment LA left outer join LA.locationReference.stationTo st "
			+ "WHERE LA.sample.sampleDate >= :fromDate and LA.sample.sampleDate < :toDate")
	List<LarvalAssessmentDTO> findAllListAfterYear(@Param("fromDate") Date fromDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.larvalassessments.LarvalAssessmentDTO(LA.id, LA.sample.sampleStatus, LA.sample.sampleDate, LA.sample.sampleCode, " + "LA.locationReference.lake.lakeCode, LA.locationReference.lake.nameEn, LA.locationReference.lake.nameFr," + "LA.locationReference.stream.streamCode, LA.locationReference.stream.nameEn, LA.locationReference.stream.nameFr,"
			+ "LA.locationReference.branchLentic.branchLenticCode, LA.locationReference.branchLentic.nameEn, LA.locationReference.branchLentic.nameFr," + "LA.locationReference.stationFrom.stationCode, LA.locationReference.stationFrom.name," + "st.stationCode, st.name) FROM LarvalAssessment LA left outer join LA.locationReference.stationTo st "
			+ "WHERE LA.sample.sampleDate < :toDate")
	List<LarvalAssessmentDTO> findAllListBeforeYear(@Param("toDate") Date toDate);
	
	LarvalAssessment findFirstByOrderBySample_sampleDateAsc();
	
	@Query(value = "SELECT LAASSESSMENT_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextSequentialNumber();
}
