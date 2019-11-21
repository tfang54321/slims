package ca.gc.dfo.slims.domain.entity.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import ca.gc.dfo.spring_commons.commons_offline_wet.domain.EAccessUserDetails;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "dateCreated", "dateModified" }, allowGetters = true)
@Entity
@Table(name = "slims_slims_users")
public class SlimsUser implements EAccessUserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slimsusers_sequence")
	@SequenceGenerator(name = "slimsusers_sequence", initialValue=1, allocationSize = 1, sequenceName = "SLIMSUSER_SEQ")
	private Long userId;

	/*
	 * @OneToMany
	 * 
	 * @JoinColumn(name = "user_role") private Set<UserRole> userRoles = new
	 * HashSet<UserRole>();
	 */
	@OneToOne(optional = false, fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "role_id", nullable = false)
	@Audited(withModifiedFlag=true)
	private UserRole userRole;

	@Audited(withModifiedFlag=true)
	@Column(name = "user_name", nullable = false)
	private String ntPrincipal;

	private String firstName;

	private String lastName;

	private String preferredLanguage;

	@Audited(withModifiedFlag=true)
	private Boolean activated;

	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date dateCreated;

	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date dateModified;

	//Added this to satisfy eAccess integration requirements, but it is ultimately not used by SLIMS
	@Transient
	private Long partyId;

	//Added this to satisfy eAccess integration requirements, but it is ultimately not used by SLIMS
	@Transient
	private String email;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (userRole != null) {
			authorities.add(new SimpleGrantedAuthority(userRole.getRolename()));
		}
		return authorities;
	}

	public String getUsername()
	{
		return ntPrincipal;
	}

	public void setUsername(String username)
	{
		this.ntPrincipal = username;
	}

	@Override
	public Boolean getActiveFlagInd()
	{
		return activated != null ? activated : false;
	}

	@Override
	public void setActiveFlagInd(Boolean activeFlagInd)
	{
		this.activated = activeFlagInd;
	}

	public String getFullname()
	{
		String lastName = getLastName();
		String firstName = getFirstName();
		List<String> names = new ArrayList<>();
		if (lastName != null && lastName.trim().length() > 0) names.add(lastName);
		if (firstName != null && firstName.trim().length() > 0) names.add(firstName);
		return String.join(", ", names);
	}
}
