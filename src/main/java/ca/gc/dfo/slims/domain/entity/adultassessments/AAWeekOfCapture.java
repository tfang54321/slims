package ca.gc.dfo.slims.domain.entity.adultassessments;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class AAWeekOfCapture {

	@Column(name = "TAGGING_WEEK")
	private Integer taggingWeek;

	@Column(name = "ADULT_CAPTURED")
	private Integer adultCaptured;

}
