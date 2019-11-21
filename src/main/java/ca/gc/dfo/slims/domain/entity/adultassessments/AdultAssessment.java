package ca.gc.dfo.slims.domain.entity.adultassessments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slims_adult_assessments")
public class AdultAssessment
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adultassessments_sequence")
	@SequenceGenerator(name = "adultassessments_sequence", initialValue = 1, allocationSize = 1, sequenceName = "ADULTASSESSMENT_SEQ")
	private Long				id;
	
	@Embedded
	private Sample				sample;
	
	@Embedded
	private LocationReference	locationReference;
	
	@Column(name = "INSPECT_DATE")
	@Temporal(TemporalType.DATE)
	private Date				inspectDate;
	
	@Column(name = "TIME_TO_CHECK")
	private String				timeToCheck;
	
	private String				location;

	@Column(name = "FKREF_DEVICE_METHOD_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				deviceMethod;
	
	private Integer				trapNumber;

	@Column(name = "FKREF_OPCODE_INITIAL_OR_REPLACED_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				opcodeInitialOrReplaced;

	@Column(name = "FKREF_ADDITIONAL_OPCODE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				additionalOpcode;

	@Column(name = "FKREF_OPCODE_ON_LEAVING_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				opcodeOnLeaving;
	
	private String				remarks;
	
	@Column(name = "AIR_TEMP", columnDefinition = "NUMBER(9,2)")
	private Double				airTemp;
	
	private Integer				recaptured;
	
	private Integer				marked;
	
	@Column(name = "WEEK_OF_TAGGING")
	private Integer				weekOfTagging;
	
	@Column(name = "WATER_TEMP", columnDefinition = "NUMBER(9,2)")
	private Double				waterTemp;
	
	@Column(name = "WATER_TEMP_MAX", columnDefinition = "NUMBER(9,2)")
	private Double				waterTempMax;
	
	@Column(name = "WATER_TEMP_MIN", columnDefinition = "NUMBER(9,2)")
	private Double				waterTempMin;

	@Column(name="FKREF_DEVICE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				device;
	
	private String				turbidity;

	@Column(name = "FKREF_GAUGE_USED_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				gaugeUsed;
	
	@Column(name = "UP_STREAM", columnDefinition = "NUMBER(9,2)")
	private Double				upStream;
	
	@Column(name = "DOWN_STREAM", columnDefinition = "NUMBER(9,2)")
	private Double				downStream;
	
	@Column(name = "IF_OTHER")
	private String				ifOther;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "adultAssessment", orphanRemoval = true)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@JsonManagedReference
	private List<Specie>		species	= new ArrayList<Specie>();
	
	public void setSpecies(List<Specie> species)
	{
		this.species.clear();
		this.species.addAll(species);
	}
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "SLIMS_AA_WEEKOFCAPTURES", joinColumns = @JoinColumn(name = "AA_ID"))
	private List<AAWeekOfCapture>	aaWeekOfCaptures	= new ArrayList<AAWeekOfCapture>();
	
	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date					dateCreated;
	
	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date					dateModified;
	
}
