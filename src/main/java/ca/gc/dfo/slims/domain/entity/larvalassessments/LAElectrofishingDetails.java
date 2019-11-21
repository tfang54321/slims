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
@Table(name = "slims_la_electrofishing_details")
public class LAElectrofishingDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "electrodetail_sequence")
	@SequenceGenerator(name = "electrodetail_sequence", initialValue=1, allocationSize = 1, sequenceName = "ELECTRO_SEQ")
	private Long id;
	
	@OneToOne
    @MapsId
    @JsonBackReference
    private LarvalAssessment larvalAssessment;
	
	@Column(name = "ABP_SETTING_TYPE")
	private String abpSettingType;

	@Column(name = "ABP_PEAK_VOLT", columnDefinition = "NUMBER(9,2)")
	private Double abpPeakVolt;

	@Column(name = "PULSE_RATE_SLOW", columnDefinition = "NUMBER(9,2)")
	private Double pulseRateSlow;

	@Column(name = "PULSE_RATE_FAST", columnDefinition = "NUMBER(9,2)")
	private Double pulseRateFast;

	@Column(name = "DUTY_CYCLE_SLOW", columnDefinition = "NUMBER(9,2)")
	private Double dutyCycleSlow;

	@Column(name = "DUTY_CYCLE_FAST", columnDefinition = "NUMBER(9,2)")
	private Double dutyCycleFast;

	@Column(name = "BURST_RATE", columnDefinition = "NUMBER(9,2)")
	private Double burstRate;

	@Column(name = "SURVEY_DISTANCE", columnDefinition = "NUMBER(10,2)")
	private Double surveyDistance;

	@Column(name = "PERC_AREA_ELECTROFISHED", columnDefinition = "NUMBER(9,2)")
	private Double percAreaElectrofished;

	@Column(name = "AREA_ELECTROFISHED", columnDefinition = "NUMBER(9,2)")
	private Double areaElectrofished;

	@Column(name = "AREA_ELECTROFISHED_SOURCE")
	private String areaElectrofishedSource;

	@Column(name = "TIME_ELECTROFISHED")
	private String timeElectrofished;

	@Column(name = "TIME_ELECTROFISHED_SOURCE")
	private String timeElectrofishedSource;

}
