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
@Table(name = "slims_common_specie_codes")
public class SpecieCode
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonspeciecode_sequence")
	@SequenceGenerator(name = "commonspeciecode_sequence", initialValue = 1, allocationSize = 1, sequenceName = "COMMONSPECIECODE_SEQ")
	private Long	id;
	
	@Column(name = "SPECIES_CODE", unique = true)
	private String	speciesCode;
	
	@Column(name = "COMMON_NAME")
	private String	commonName;
	
	@Column(name = "IMSL_SHRTHND")
	private String	imslShrthnd;
	
	@Column(name = "MAXLENGTH")
	private Integer	maxlength;
	
	@Column(name = "MINLENGTH")
	private Integer	minlength;
	
	@Column(name = "MNR_CODE")
	private Integer	mnrCode;
	
	@Column(name = "NCALCULATED")
	private Integer	ncalculated;
	
	@Column(name = "AX10_5", columnDefinition = "NUMBER(9,2)")
	private Double	ax10_5;
	
	@Column(name = "B", columnDefinition = "NUMBER(9,2)")
	private Double	b;
	
	@Column(name = "R2", columnDefinition = "NUMBER(9,2)")
	private Double	r2;
	
	@Column(name = "SCIENTIFIC_NAME")
	private String	scientificName;
	
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
		if(commonName == null)
		{
			commonName = "";
		}
		return "(" + speciesCode + ")" + commonName;
	}
	
}
