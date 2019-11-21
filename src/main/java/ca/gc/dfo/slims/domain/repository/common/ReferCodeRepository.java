package ca.gc.dfo.slims.domain.repository.common;

import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferCodeRepository extends JpaRepository<ReferCode, Long> {

	List<ReferCode> findAllByCodeType(String codeType);

	ReferCode findByCodeTypeEqualsAndCodeValueEquals(String codeType, String codeAbbreviation);
}
