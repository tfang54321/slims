package ca.gc.dfo.slims.domain.entity.common.location;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*
 * @Entity
 * 
 * @Table(name = "slims_common_location_references", uniqueConstraints = {
 * 
 * @UniqueConstraint(columnNames = { "LAKE_ID", "STREAM_ID", "BRANCHLENTIC_ID"
 * }) })
 */
@Embeddable
public class LocationReference {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lake_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Lake lake;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stream_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Stream stream;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branchlentic_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private BranchLentic branchLentic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_from_id", nullable = true)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Station stationFrom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_to_id", nullable = true)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Station stationTo;

	@Column(name = "STATION_FROM_ADJUST")
	private String stationFromAdjust;

	@Column(name = "STATION_TO_ADJUST")
	private String stationToAdjust;
}
