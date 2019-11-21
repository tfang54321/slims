package ca.gc.dfo.slims.domain.entity.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
public class RefCodes
{
	
	private String				codeType;
	
	private List<RefCodePair>	codeValues	= new ArrayList<RefCodePair>();
	
	/**
	 * @param codeType
	 * @param codeValues
	 */
	public RefCodes(String codeType, List<RefCodePair> codeValues)
	{
		super();
		this.codeType = codeType;
		this.codeValues = codeValues;
	}
	
	public RefCodes()
	{
		
	}
	
}
