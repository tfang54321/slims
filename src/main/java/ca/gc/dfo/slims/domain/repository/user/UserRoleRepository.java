package ca.gc.dfo.slims.domain.repository.user;

import ca.gc.dfo.slims.domain.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>
{
}
