package ca.gc.dfo.slims.domain.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@Entity
@Table(name = "slims_common_lake_districts")
public class LakeDistrict
{	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lakedistricts_sequence")
	@SequenceGenerator(name = "lakedistricts_sequence", initialValue = 1, allocationSize = 1, sequenceName = "LAKEDISTRICTS_SEQ")
	private Long	id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lake_id", nullable = false)
	@JsonBackReference
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Lake	lake;
	
	@Column(name = "STATISTICAL_DISTRICT")
	private String	statisticalDistrict;
	
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
			return "(" + statisticalDistrict + ")" + lake.getNameFr();
		}
		
		return "(" + statisticalDistrict + ")" + lake.getNameEn();
	}
}
