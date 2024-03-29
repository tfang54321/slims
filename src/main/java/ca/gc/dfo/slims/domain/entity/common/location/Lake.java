package ca.gc.dfo.slims.domain.entity.common.location;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value =
{ "dateCreated", "dateModified", "hibernateLazyInitializer", "handler" }, allowGetters = true)
@Entity
@Table(name = "slims_common_lakes")
public class Lake
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lake_sequence")
	@SequenceGenerator(name = "lake_sequence", initialValue = 1, allocationSize = 1, sequenceName = "LAKE_SEQ")
	private Long	id;
	
	@NotBlank
	@Size(min = 1, max = 3, message = "{lake_code_validation}")
	@Column(name = "LAKE_CODE", unique = true)
	private String	lakeCode;
	
	@NotBlank
	@Size(min = 1, max = 45, message = "{location_enname_validation}")
	@Column(name = "LAKE_NAME_EN")
	private String	nameEn;
	
	@NotBlank
	@Size(min = 1, max = 45, message = "{location_frname_validation}")
	@Column(name = "LAKE_NAME_FR")
	private String	nameFr;
	
	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date	dateCreated;
	
	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date	dateModified;
	
	@Transient
	private String	showText;
	
	public String getShowText()
	{
		return this.toString();
	}
	
	@Override
	public String toString()
	{
		if (LocaleContextHolder.getLocale().getLanguage().toLowerCase().contains("fr"))
		{
			return "(" + lakeCode + ")" + nameFr;
		}
		
		return "(" + lakeCode + ")" + nameEn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lake other = (Lake) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
