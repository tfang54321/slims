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
public class TREmulsifiableConcentrate
{
	//@Column(name = "EC_LAMPRICIDE_PRODUCT_ID")
	//private Long	ecLPId;
	
	//@Column(name = "PERC_AI_PER_LITRE", columnDefinition = "NUMBER(9,2)")
	//private Double	percAIPerLitre;
	
	@Column(name = "LITRES_USED", columnDefinition = "NUMBER(9,2)")
	private Double	litresUsed;
		
	//@Column(name = "EC_PERC_AI", columnDefinition = "NUMBER(9,2)")
	//private Double	ecPercAI;

	@Column(name = "FKREF_EC_PERC_AI_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	ecPercAI;
	
	@Column(name = "EC_KG_AI")
	private Double	ecKgAI;
	
}
