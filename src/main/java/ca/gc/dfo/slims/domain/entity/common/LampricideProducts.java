package ca.gc.dfo.slims.domain.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@Entity
@Table(name = "slims_common_lampricide_products")
public class LampricideProducts {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lampricide_products_sequence")
	@SequenceGenerator(name = "lampricide_products_sequence", initialValue = 1, allocationSize = 1, sequenceName = "LAMPRIPRODUCTS_SEQ")
	private Long id;

	@Column(name = "LAMPRICIDE_PRODUCT_TYPE")
	private String lampricideProductType;
	
	@Column(name = "BATCH")
	private String batch;
	
	@Column(name = "COMPANY")
	private String company;
	
	@Column(name = "BATCH_YEAR")
	private String batchYear;
	
	@Column(name = "ACTIVE_INDICATOR")
	private String activeIndicator = "Yes";
	
	@Transient
	private String	showText;
	
	public String getShowText()
	{
		return this.toString();
	}
	
	@Override
	public String toString()
	{
		return batch + "/" + batchYear + "/" + company;
	}

}
