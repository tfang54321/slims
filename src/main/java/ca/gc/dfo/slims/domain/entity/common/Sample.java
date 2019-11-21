package ca.gc.dfo.slims.domain.entity.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@Embeddable
public class Sample {

	@Column(name = "SAMPLE_CODE", unique = true)
	private String sampleCode;

	@Column(name = "SAMPLE_DATE")
	@Temporal(TemporalType.DATE)
	private Date sampleDate;

	@Column(name = "SAMPLE_STATUS")
	private String sampleStatus;

}
