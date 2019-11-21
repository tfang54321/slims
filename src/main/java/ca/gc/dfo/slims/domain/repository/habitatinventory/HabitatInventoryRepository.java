package ca.gc.dfo.slims.domain.repository.habitatinventory;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.gc.dfo.slims.domain.entity.habitat.HabitatInventory;
import ca.gc.dfo.slims.dto.habitat.HabitatInventoryDTO;

public interface HabitatInventoryRepository extends JpaRepository<HabitatInventory, Long>
{
	@Query("SELECT new ca.gc.dfo.slims.dto.habitat.HabitatInventoryDTO(HA.id, HA.sample.sampleCode, HA.transectId, HA.inventoryDate," + "HA.locationReference.lake.lakeCode, HA.locationReference.lake.nameEn, HA.locationReference.lake.nameFr," + "HA.locationReference.stream.streamCode, HA.locationReference.stream.nameEn, HA.locationReference.stream.nameFr,"
	        + "HA.locationReference.branchLentic.branchLenticCode, HA.locationReference.branchLentic.nameEn, HA.locationReference.branchLentic.nameFr," + "HA.locationReference.stationFrom.stationCode, HA.locationReference.stationFrom.name," + "HA.locationReference.stationTo.stationCode, HA.locationReference.stationTo.name) FROM HabitatInventory HA")
	List<HabitatInventoryDTO> findAllList();
	
	@Query("SELECT new ca.gc.dfo.slims.dto.habitat.HabitatInventoryDTO(HA.id, HA.sample.sampleCode, HA.transectId, HA.inventoryDate," + "HA.locationReference.lake.lakeCode, HA.locationReference.lake.nameEn, HA.locationReference.lake.nameFr," + "HA.locationReference.stream.streamCode, HA.locationReference.stream.nameEn, HA.locationReference.stream.nameFr,"
			+ "HA.locationReference.branchLentic.branchLenticCode, HA.locationReference.branchLentic.nameEn, HA.locationReference.branchLentic.nameFr," + "HA.locationReference.stationFrom.stationCode, HA.locationReference.stationFrom.name," + "HA.locationReference.stationTo.stationCode, HA.locationReference.stationTo.name) FROM HabitatInventory HA "
			+ "WHERE HA.inventoryDate >= :fromDate and HA.inventoryDate < :toDate")
	List<HabitatInventoryDTO> findAllList(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.habitat.HabitatInventoryDTO(HA.id, HA.sample.sampleCode, HA.transectId, HA.inventoryDate," + "HA.locationReference.lake.lakeCode, HA.locationReference.lake.nameEn, HA.locationReference.lake.nameFr," + "HA.locationReference.stream.streamCode, HA.locationReference.stream.nameEn, HA.locationReference.stream.nameFr,"
			+ "HA.locationReference.branchLentic.branchLenticCode, HA.locationReference.branchLentic.nameEn, HA.locationReference.branchLentic.nameFr," + "HA.locationReference.stationFrom.stationCode, HA.locationReference.stationFrom.name," + "HA.locationReference.stationTo.stationCode, HA.locationReference.stationTo.name) FROM HabitatInventory HA "
			+ "WHERE HA.inventoryDate >= :fromDate")
	List<HabitatInventoryDTO> findAllListAfterYear(@Param("fromDate") Date fromDate);
	
	@Query("SELECT new ca.gc.dfo.slims.dto.habitat.HabitatInventoryDTO(HA.id, HA.sample.sampleCode, HA.transectId, HA.inventoryDate," + "HA.locationReference.lake.lakeCode, HA.locationReference.lake.nameEn, HA.locationReference.lake.nameFr," + "HA.locationReference.stream.streamCode, HA.locationReference.stream.nameEn, HA.locationReference.stream.nameFr,"
			+ "HA.locationReference.branchLentic.branchLenticCode, HA.locationReference.branchLentic.nameEn, HA.locationReference.branchLentic.nameFr," + "HA.locationReference.stationFrom.stationCode, HA.locationReference.stationFrom.name," + "HA.locationReference.stationTo.stationCode, HA.locationReference.stationTo.name) FROM HabitatInventory HA "
			+ "WHERE HA.inventoryDate < :toDate")
	List<HabitatInventoryDTO> findAllListBeforeYear(@Param("toDate") Date toDate);
	
	HabitatInventory findFirstByOrderByInventoryDateAsc();
	
	@Query(value = "SELECT HABITATINVENTORY_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextSequentialNumber();
}
