package ca.gc.dfo.slims.domain.entity.common.location;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class LocationDetails {

	private Integer containment;

	private Integer conductivity;

	@Column(columnDefinition = "NUMBER(9,2)")
	private Double temperature;

	@Column(name = "MEAN_DEPTH", columnDefinition = "NUMBER(9,2)")
	private Double meanDepth;
	
	@Column(name = "MEAN_WIDTH", columnDefinition = "NUMBER(9,2)")
	private Double meanWidth;

	@Column(name = "MAX_DEPTH", columnDefinition = "NUMBER(9,2)")
	private Double maxDepth;

	@Column(name = "MEASURED_AREA", columnDefinition = "NUMBER(9,2)")
	private Double measuredArea;
	
	@Column(name = "ESTIMATED_AREA", columnDefinition = "NUMBER(9,2)")
	private Double estimatedArea;

	@Column(name = "DISTANCE_SURVEYED", columnDefinition = "NUMBER(9,2)")
	private Double distanceSurveyed;

}
