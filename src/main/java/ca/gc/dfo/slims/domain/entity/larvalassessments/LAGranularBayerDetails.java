package ca.gc.dfo.slims.domain.entity.larvalassessments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slims_la_granularbayer_details")
public class LAGranularBayerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "granularbayer_sequence")
	@SequenceGenerator(name = "granularbayer_sequence", initialValue=1, allocationSize = 1, sequenceName = "GRANULARBAYER_SEQ")
	private Long id;
	
	@OneToOne
    @MapsId
    @JsonBackReference
    private LarvalAssessment larvalAssessment;

	@Column(name = "PLOT_LENGTH", columnDefinition = "NUMBER(9,2)")
	private Double plotLength;

	@Column(name = "PLOT_WIDTH", columnDefinition = "NUMBER(9,2)")
	private Double plotWidth;

	@Column(name = "PLOT_AREA", columnDefinition = "NUMBER(10,2)")
	private Double plotArea;

	@Column(name = "QUANTITY_USED", columnDefinition = "NUMBER(9,2)")
	private Double quantityUsed;

	@Column(name = "TIME_TO_FIRST_AMMOCETE", columnDefinition = "NUMBER(9,2)")
	private Double timeToFirstAmmocete;

	@Column(name = "PERSON_HOURS", columnDefinition = "NUMBER(9,2)")
	private Double personHours;

	@Column(name = "NUM_BOATS")
	private Integer numBoats;

}
