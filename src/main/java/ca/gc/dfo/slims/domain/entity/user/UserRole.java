package ca.gc.dfo.slims.domain.entity.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@Entity
@Table(name = "slims_user_roles")
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userroles_sequence")
	@SequenceGenerator(name = "userroles_sequence", initialValue=1, allocationSize = 1, sequenceName = "USERROLE_SEQ")
	private Long roleId;

	@Audited
	private String rolename;

}
