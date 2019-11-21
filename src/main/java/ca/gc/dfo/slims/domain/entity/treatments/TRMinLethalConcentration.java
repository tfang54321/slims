package ca.gc.dfo.slims.domain.entity.treatments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slims_tr_minlethalconcentrations", uniqueConstraints = { @UniqueConstraint(columnNames = {"TREATMENT_ID", "BRANCHLENTIC_ID", "STATION_ID"}) })
public class TRMinLethalConcentration {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trminlethalcon_sequence")
	@SequenceGenerator(name = "trminlethalcon_sequence", initialValue = 1, allocationSize = 1, sequenceName = "TRMINLETHALCON_SEQ")
	private Long id;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "treatment_id")
	private Treatment treatment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branchlentic_id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private BranchLentic branchLentic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Station station;

	private String stationAdjust;

	@Column(columnDefinition = "NUMBER(9,2)")
	private Double mlc;

	@Column(columnDefinition = "NUMBER(9,2)")
	private Double niclosamide;

	@Column(columnDefinition = "NUMBER(9,2)")
	private Double exposure;

}
