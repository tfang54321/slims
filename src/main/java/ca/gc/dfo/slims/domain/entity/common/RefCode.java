package ca.gc.dfo.slims.domain.entity.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
public class RefCode {

	private String codeType;

	private RefCodePair codePair;

	/**
	 * @param codeType
	 * @param codePair
	 */
	public RefCode(String codeType, RefCodePair codePair) {
		super();
		this.codeType = codeType;
		this.codePair = codePair;
	}
	
	/**
	 * 
	 */
	public RefCode() {
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codePair == null) ? 0 : codePair.hashCode());
		result = prime * result + ((codeType == null) ? 0 : codeType.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RefCode other = (RefCode) obj;
		if (codePair == null) {
			if (other.codePair != null)
				return false;
		} else if (!codePair.equals(other.codePair))
			return false;
		if (codeType == null) {
			if (other.codeType != null)
				return false;
		} else if (!codeType.equals(other.codeType))
			return false;
		return true;
	}

}
