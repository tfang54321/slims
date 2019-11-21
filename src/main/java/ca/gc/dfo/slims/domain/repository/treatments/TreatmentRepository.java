package ca.gc.dfo.slims.domain.repository.treatments;

import ca.gc.dfo.slims.dto.treatments.TreatmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.treatments.Treatment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TreatmentRepository extends JpaRepository<Treatment, Long>
{
    @Query("SELECT new ca.gc.dfo.slims.dto.treatments.TreatmentDTO(TR.id, TR.sample.sampleCode, TR.sample.sampleStatus," +
            "TR.trLogistics.treatmentStart, TR.trLogistics.treatmentEnd," +
            "TR.lake.lakeCode, TR.lake.nameEn, TR.lake.nameFr, TR.stream.streamCode, TR.stream.nameEn, TR.stream.nameFr) FROM Treatment TR")
	List<TreatmentDTO> findAllList();
    
    @Query("SELECT new ca.gc.dfo.slims.dto.treatments.TreatmentDTO(TR.id, TR.sample.sampleCode, TR.sample.sampleStatus," +
    		"TR.trLogistics.treatmentStart, TR.trLogistics.treatmentEnd," +
    		"TR.lake.lakeCode, TR.lake.nameEn, TR.lake.nameFr, TR.stream.streamCode, TR.stream.nameEn, TR.stream.nameFr) FROM Treatment TR " +
    		"WHERE TR.trLogistics.treatmentStart >= :fromDate and TR.trLogistics.treatmentStart < :toDate")
    List<TreatmentDTO> findAllList(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
    
    @Query("SELECT new ca.gc.dfo.slims.dto.treatments.TreatmentDTO(TR.id, TR.sample.sampleCode, TR.sample.sampleStatus," +
    		"TR.trLogistics.treatmentStart, TR.trLogistics.treatmentEnd," +
    		"TR.lake.lakeCode, TR.lake.nameEn, TR.lake.nameFr, TR.stream.streamCode, TR.stream.nameEn, TR.stream.nameFr) FROM Treatment TR " +
    		"WHERE TR.trLogistics.treatmentStart >= :fromDate")
    List<TreatmentDTO> findAllListAfterYear(@Param("fromDate") Date fromDate);
    
    @Query("SELECT new ca.gc.dfo.slims.dto.treatments.TreatmentDTO(TR.id, TR.sample.sampleCode, TR.sample.sampleStatus," +
    		"TR.trLogistics.treatmentStart, TR.trLogistics.treatmentEnd," +
    		"TR.lake.lakeCode, TR.lake.nameEn, TR.lake.nameFr, TR.stream.streamCode, TR.stream.nameEn, TR.stream.nameFr) FROM Treatment TR " +
    		"WHERE TR.trLogistics.treatmentStart < :toDate")
    List<TreatmentDTO> findAllListBeforeYear(@Param("toDate") Date toDate);

    Treatment findFirstByOrderByTrLogistics_treatmentStartAsc();
    
    @Query(value = "SELECT TREATMENT_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextSequentialNumber();
}
