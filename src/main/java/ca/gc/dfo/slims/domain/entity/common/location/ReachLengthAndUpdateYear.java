package ca.gc.dfo.slims.domain.entity.common.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReachLengthAndUpdateYear implements Comparable<ReachLengthAndUpdateYear>
{
	
	private String	reachLength;
	
	private String	updatedYear;
	
	/**
	 * @param reachLength
	 * @param updatedYear
	 */
	public ReachLengthAndUpdateYear(String reachLength, String updatedYear)
	{
		super();
		this.reachLength = reachLength;
		this.updatedYear = updatedYear;
	}
	
	/**
	 * 
	 */
	public ReachLengthAndUpdateYear()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reachLength == null) ? 0 : reachLength.hashCode());
		result = prime * result + ((updatedYear == null) ? 0 : updatedYear.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		ReachLengthAndUpdateYear other = (ReachLengthAndUpdateYear) obj;
		if (reachLength == null)
		{
			if (other.reachLength != null)
				return false;
		}
		else if (!reachLength.equals(other.reachLength))
			return false;
		if (updatedYear == null)
		{
			if (other.updatedYear != null)
				return false;
		}
		else if (!updatedYear.equals(other.updatedYear))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ReachLengthAndUpdateYear ly)
	{
		return updatedYear.compareTo(ly.updatedYear);
	}		
}
