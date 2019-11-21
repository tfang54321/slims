package ca.gc.dfo.slims.domain.entity.common.location;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value =
{ "dateCreated", "dateModified", "hibernateLazyInitializer", "handler" }, allowGetters = true)
@Entity
@Table(name = "slims_common_streams", uniqueConstraints =
{ @UniqueConstraint(columnNames =
		{ "STREAM_CODE", "LAKE_ID" }) })
public class Stream
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stream_sequence")
	@SequenceGenerator(name = "stream_sequence", initialValue = 1, allocationSize = 1, sequenceName = "STREAM_SEQ")
	private Long	id;
	
	@NotBlank
	@Digits(fraction = 0, integer = 4)
	@Size(min = 1, max = 4, message = "{stream_code_validation}")
	@Column(name = "STREAM_CODE")
	private String	streamCode;
	
	@NotBlank
	@Size(min = 1, max = 45, message = "{location_enname_validation}")
	@Column(name = "STREAM_NAME_EN")
	private String	nameEn;
	
	@NotBlank
	@Size(min = 1, max = 45, message = "{location_frname_validation}")
	@Column(name = "STREAM_NAME_FR")
	private String	nameFr;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lake_id", nullable = false)
	@JsonBackReference
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Lake	lake;
	
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
			return "(" + streamCode + ")" + nameFr;
		}
		
		return "(" + streamCode + ")" + nameEn;
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
		Stream other = (Stream) obj;
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
