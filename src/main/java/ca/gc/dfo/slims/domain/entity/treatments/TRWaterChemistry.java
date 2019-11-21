package ca.gc.dfo.slims.domain.entity.treatments;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "slims_tr_waterchemistries", uniqueConstraints = { @UniqueConstraint(columnNames = {"TREATMENT_ID", "BRANCHLENTIC_ID", "STATION_ID"}) })
public class TRWaterChemistry {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trwaterchemistry_sequence")
	@SequenceGenerator(name = "trwaterchemistry_sequence", initialValue=1, allocationSize = 1, sequenceName = "TRWATERCHEM_SEQ")
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

	@Column(name = "ANALYSIS_DATE")
	@Temporal(TemporalType.DATE)
	private Date analysisDate;

	@Column(name = "TIME_OF_SAMPLE")
	private String timeOfSample;

	@Column(name = "STREAM_TEMP")
	private Integer streamTemp;

	@Column(name = "TFM_CONCEN_GT_POINTONE_PERC")
	private Boolean tfmConcenGTPointOnePerc;

	@Column(name = "DISSOLVED_OXYGEN", columnDefinition = "NUMBER(9,2)")
	private Double dissolvedOxygen;

	@Column(columnDefinition = "NUMBER(9,2)")
	private Double ammonia;

	@Column (name = "PH_VALUE", columnDefinition = "NUMBER(9,2)")
	private Double phValue;

	@Column(name = "ALKALINITY_PH", columnDefinition = "NUMBER(9,2)")
	private Double alkalinityPh;

	@Column(name = "FKREF_PREDICT_CHART_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode predictChart;

	@Column(name = "PH_MIN_LETHAL_CONCENTRATION", columnDefinition = "NUMBER(9,2)")
	private Double phMinLethalConcentration;

}
