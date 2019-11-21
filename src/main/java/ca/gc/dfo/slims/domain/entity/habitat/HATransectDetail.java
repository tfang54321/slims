package ca.gc.dfo.slims.domain.entity.habitat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HATransectDetail
{	
	@Column(name = "DISTANCE_FROM_LEFT_BANK", columnDefinition = "NUMBER(9,2)")
	private Double	distanceFromLeftBank;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double	depth;
	
	private String	habitatType;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((depth == null) ? 0 : depth.hashCode());
		result = prime * result + ((distanceFromLeftBank == null) ? 0 : distanceFromLeftBank.hashCode());
		result = prime * result + ((habitatType == null) ? 0 : habitatType.hashCode());
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
		HATransectDetail other = (HATransectDetail) obj;
		if (depth == null)
		{
			if (other.depth != null)
				return false;
		}
		else if (!depth.equals(other.depth))
			return false;
		if (distanceFromLeftBank == null)
		{
			if (other.distanceFromLeftBank != null)
				return false;
		}
		else if (!distanceFromLeftBank.equals(other.distanceFromLeftBank))
			return false;
		if (habitatType == null)
		{
			if (other.habitatType != null)
				return false;
		}
		else if (!habitatType.equals(other.habitatType))
			return false;
		return true;
	}
	
}
