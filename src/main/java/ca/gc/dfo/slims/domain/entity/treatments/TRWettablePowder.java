package ca.gc.dfo.slims.domain.entity.treatments;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class TRWettablePowder
{	
	//@Column(name = "WP_LAMPRICIDE_PRODUCT_ID")
	//private Long	wpLPId;
	
	@Column(name = "KG_USED", columnDefinition = "NUMBER(9,2)")
	private Double	kgUsed;
	
	//@Column(name = "WP_PERC_AI", columnDefinition = "NUMBER(9,2)")
	//private Double	wpPercAI;

	@Column(name = "FKREF_WP_PERC_AI_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	wpPercAI;
	
	@Column(name = "WP_KG_AI")
	private Double	wpKgAI;
	
}
