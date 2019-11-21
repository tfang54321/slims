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
public class TRTFM
{
	@Column(name = "TFM_LAMPRICIDE_PRODUCT_ID")
	private Long	tfmLPId;
	
	@Column(name = "LITRES_USED", columnDefinition = "NUMBER(9,2)")
	private Double	litresUsed;
	
	@Column(name = "NUM_OF_BARS", columnDefinition = "NUMBER(9,2)")
	private Double	numOfBars;
	
	@Column(name = "WEIGHT_OF_BARS", columnDefinition = "NUMBER(9,2)")
	private Double	weightOfBars;
	
	//@Column(name = "TFM_PERC_AI", columnDefinition = "NUMBER(9,2)")
	//private Double	tfmPercAI;

	@Column(name = "FKREF_TFM_PERC_AI_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	tfmPercAI;
	
	@Column(name = "TFM_KG_AI", columnDefinition = "NUMBER(9,2)")
	private Double	tfmKgAI;
		
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tfmLPId == null) ? 0 : tfmLPId.hashCode());
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
		TRTFM other = (TRTFM) obj;
		if (tfmLPId == null)
		{
			if (other.tfmLPId != null)
				return false;
		}
		else if (!tfmLPId.equals(other.tfmLPId))
			return false;
		return true;
	}
	
}
