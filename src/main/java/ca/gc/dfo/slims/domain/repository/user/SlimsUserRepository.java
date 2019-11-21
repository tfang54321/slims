package ca.gc.dfo.slims.domain.repository.user;

import ca.gc.dfo.slims.domain.entity.user.SlimsUser;
import ca.gc.dfo.spring_commons.commons_offline_wet.domain.EAccessUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlimsUserRepository extends EAccessUserRepository<SlimsUser, Long>
{
}
