package ca.gc.dfo.slims.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@ResponseBody
public class ListObjectsDTO
{
	private List<Object> objList = new ArrayList<Object>();
}
