package ca.gc.dfo.slims.domain.entity.common;

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

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value =
{ "dateCreated", "dateModified" }, allowGetters = true)
@Entity
@Table(name = "slims_common_code_view")
public class CodeView
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "codeview_sequence")
	@SequenceGenerator(name = "codeview_sequence", initialValue = 1, allocationSize = 1, sequenceName = "CODEVIEW_SEQ")
	private Long	id;
	
	@Column(name = "CODE_NAME", unique = true)
	private String	codeName;
	
	private String	descriptionEn;
	
	private String	descriptionFr;
	
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
			return descriptionFr;
		}
		
		return descriptionEn;
	}

	/**
	 * @param codeName
	 * @param descriptionEn
	 * @param descriptionFr
	 */
	public CodeView(String codeName, String descriptionEn, String descriptionFr)
	{
		super();
		this.codeName = codeName;
		this.descriptionEn = descriptionEn;
		this.descriptionFr = descriptionFr;
	}

	/**
	 * 
	 */
	public CodeView()
	{
		// TODO Auto-generated constructor stub
	}
}
