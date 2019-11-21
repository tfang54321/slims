package ca.gc.dfo.slims.domain.repository.fishmodule;

import ca.gc.dfo.slims.domain.entity.fishmodule.FishModule;
import ca.gc.dfo.slims.dto.fishmodules.FishModuleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FishModuleRepository extends JpaRepository<FishModule, Long> {

    @Query("SELECT new ca.gc.dfo.slims.dto.fishmodules.FishModuleDTO(FM.id, FM.sample.sampleStatus, FM.sample.sampleDate, FM.sample.sampleCode, " +
            "FM.locationReference.lake.lakeCode, FM.locationReference.lake.nameEn, FM.locationReference.lake.nameFr," +
            "FM.locationReference.stream.streamCode, FM.locationReference.stream.nameEn, FM.locationReference.stream.nameFr," +
            "FM.locationReference.branchLentic.branchLenticCode, FM.locationReference.branchLentic.nameEn, FM.locationReference.branchLentic.nameFr," +
            "FM.locationReference.stationFrom.stationCode, FM.locationReference.stationFrom.name," +
            "FM.locationReference.stationTo.stationCode, FM.locationReference.stationTo.name) FROM FishModule FM")
    List<FishModuleDTO> findAllList();
    
    @Query("SELECT new ca.gc.dfo.slims.dto.fishmodules.FishModuleDTO(FM.id, FM.sample.sampleStatus, FM.sample.sampleDate, FM.sample.sampleCode, " +
    		"FM.locationReference.lake.lakeCode, FM.locationReference.lake.nameEn, FM.locationReference.lake.nameFr," +
    		"FM.locationReference.stream.streamCode, FM.locationReference.stream.nameEn, FM.locationReference.stream.nameFr," +
    		"FM.locationReference.branchLentic.branchLenticCode, FM.locationReference.branchLentic.nameEn, FM.locationReference.branchLentic.nameFr," +
    		"FM.locationReference.stationFrom.stationCode, FM.locationReference.stationFrom.name," +
    		"FM.locationReference.stationTo.stationCode, FM.locationReference.stationTo.name) FROM FishModule FM " +
    		"WHERE FM.sample.sampleDate >= :fromDate and FM.sample.sampleDate < :toDate")
    List<FishModuleDTO> findAllList(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
    
    @Query("SELECT new ca.gc.dfo.slims.dto.fishmodules.FishModuleDTO(FM.id, FM.sample.sampleStatus, FM.sample.sampleDate, FM.sample.sampleCode, " +
    		"FM.locationReference.lake.lakeCode, FM.locationReference.lake.nameEn, FM.locationReference.lake.nameFr," +
    		"FM.locationReference.stream.streamCode, FM.locationReference.stream.nameEn, FM.locationReference.stream.nameFr," +
    		"FM.locationReference.branchLentic.branchLenticCode, FM.locationReference.branchLentic.nameEn, FM.locationReference.branchLentic.nameFr," +
    		"FM.locationReference.stationFrom.stationCode, FM.locationReference.stationFrom.name," +
    		"FM.locationReference.stationTo.stationCode, FM.locationReference.stationTo.name) FROM FishModule FM " +
    		"WHERE FM.sample.sampleDate >= :fromDate")
    List<FishModuleDTO> findAllListAfterYear(@Param("fromDate") Date fromDate);
    
    @Query("SELECT new ca.gc.dfo.slims.dto.fishmodules.FishModuleDTO(FM.id, FM.sample.sampleStatus, FM.sample.sampleDate, FM.sample.sampleCode, " +
    		"FM.locationReference.lake.lakeCode, FM.locationReference.lake.nameEn, FM.locationReference.lake.nameFr," +
    		"FM.locationReference.stream.streamCode, FM.locationReference.stream.nameEn, FM.locationReference.stream.nameFr," +
    		"FM.locationReference.branchLentic.branchLenticCode, FM.locationReference.branchLentic.nameEn, FM.locationReference.branchLentic.nameFr," +
    		"FM.locationReference.stationFrom.stationCode, FM.locationReference.stationFrom.name," +
    		"FM.locationReference.stationTo.stationCode, FM.locationReference.stationTo.name) FROM FishModule FM " +
    		"WHERE FM.sample.sampleDate < :toDate")
    List<FishModuleDTO> findAllListBeforeYear(@Param("toDate") Date toDate);

    FishModule findFirstByOrderBySample_sampleDateAsc();
    
    @Query(value = "SELECT FISHMODULE_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getNextSequentialNumber();
}
