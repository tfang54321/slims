package ca.gc.dfo.slims.domain.repository.common;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ca.gc.dfo.slims.domain.entity.common.LampricideProducts;

public interface LampricideProductsRepository extends JpaRepository<LampricideProducts, Long>
{
	@Query("SELECT DISTINCT lampricideProductType FROM LampricideProducts")
	List<String> findDistinctLampricideProductType();
	
	List<LampricideProducts> findAllByLampricideProductType(String lpType);
}
