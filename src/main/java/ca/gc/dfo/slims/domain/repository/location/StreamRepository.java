package ca.gc.dfo.slims.domain.repository.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.location.Stream;

public interface StreamRepository extends JpaRepository<Stream, Long> {

	List<Stream> findAllByLakeId(Long lakeId);

	Stream findByStreamCode(String streamCode);

}
