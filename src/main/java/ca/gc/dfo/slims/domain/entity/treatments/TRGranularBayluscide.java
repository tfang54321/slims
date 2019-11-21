package ca.gc.dfo.slims.domain.entity.treatments;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Embeddable
public class TRGranularBayluscide
{	
	//@Column(name = "GB_LAMPRICIDE_PRODUCT_ID")
	//private Long	gbLPId;
	
	@Column(name = "AMOUNT_USED", columnDefinition = "NUMBER(9,2)")
	private Double	amountUsed;
	
	//@Column(name = "GB_PERC_AI", columnDefinition = "NUMBER(9,2)")
	//private Double	gbPercAI;

	@Column(name = "FKREF_GB_PERC_AI_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	gbPercAI;
	
}
