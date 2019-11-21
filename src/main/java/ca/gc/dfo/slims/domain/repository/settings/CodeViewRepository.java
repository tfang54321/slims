package ca.gc.dfo.slims.domain.repository.settings;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.gc.dfo.slims.domain.entity.common.CodeView;

public interface CodeViewRepository extends JpaRepository<CodeView, Long>
{
	CodeView findByCodeName(String codeName);
}
