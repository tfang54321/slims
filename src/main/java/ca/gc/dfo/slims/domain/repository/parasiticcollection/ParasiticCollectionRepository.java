package ca.gc.dfo.slims.domain.repository.parasiticcollection;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import ca.gc.dfo.slims.dto.parasiticcollection.ParasiticCollectionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.gc.dfo.slims.domain.entity.parasiticcollection.ParasiticCollection;
import org.springframework.data.repository.query.Param;

public interface ParasiticCollectionRepository extends JpaRepository<ParasiticCollection, Long>
{
	@Query("SELECT DISTINCT fisherName FROM ParasiticCollection ORDER BY fisherName")
	List<String> findDistinctFisherName();

	@Query("SELECT new ca.gc.dfo.slims.dto.parasiticcollection.ParasiticCollectionDTO(PC.id, PC.sample.sampleCode," +
			" PC.sample.sampleStatus, PC.collectedDate, PC.fisherName, PC.lakeDistrict.statisticalDistrict, " +
			"PC.lakeDistrict.lake.nameEn, PC.lakeDistrict.lake.nameFr) FROM ParasiticCollection PC")
	List<ParasiticCollectionDTO> findAllList();
	
	@Query("SELECT new ca.gc.dfo.slims.dto.parasiticcollection.ParasiticCollectionDTO(PC.id, PC.sample.sampleCode," +
			" PC.sample.sampleStatus, PC.collectedDate, PC.fisherName, PC.lakeDistrict.statisticalDistrict, " +
			"PC.lakeDistrict.lake.nameEn, PC.lakeDistrict.lake.nameFr) FROM ParasiticCollection PC " +
			"WHERE PC.collectedDate >= :fromDate and PC.collectedDate < :toDate")
	List<ParasiticCollectionDTO> findAllList(@Param("fromDate") Date fromDate,@Param("toDate") Date toDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.parasiticcollection.ParasiticCollectionDTO(PC.id, PC.sample.sampleCode," +
			" PC.sample.sampleStatus, PC.collectedDate, PC.fisherName, PC.lakeDistrict.statisticalDistrict, " +
			"PC.lakeDistrict.lake.nameEn, PC.lakeDistrict.lake.nameFr) FROM ParasiticCollection PC " +
			"WHERE PC.collectedDate >= :fromDate")
	List<ParasiticCollectionDTO> findAllListAfterYear(@Param("fromDate") Date fromDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.parasiticcollection.ParasiticCollectionDTO(PC.id, PC.sample.sampleCode," +
			" PC.sample.sampleStatus, PC.collectedDate, PC.fisherName, PC.lakeDistrict.statisticalDistrict, " +
			"PC.lakeDistrict.lake.nameEn, PC.lakeDistrict.lake.nameFr) FROM ParasiticCollection PC " +
			"WHERE PC.collectedDate < :toDate")
	List<ParasiticCollectionDTO> findAllListBeforeYear(@Param("toDate") Date toDate);

	ParasiticCollection findFirstByOrderByCollectedDateAsc();
	
	@Query(value = "SELECT PARACOLLECTION_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextSequentialNumber();
}
