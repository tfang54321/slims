package ca.gc.dfo.slims.domain.entity.fishmodule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.domain.entity.common.Specie;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slims_fm_run_nets", uniqueConstraints =
{ @UniqueConstraint(columnNames =
		{ "FISH_MODULE_ID", "RUN_NET_NUMBER" }) })
public class FMRunNet
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "runnet_sequence")
	@SequenceGenerator(name = "runnet_sequence", initialValue = 1, allocationSize = 1, sequenceName = "RUNNET_SEQ")
	private Long			id;
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "FISH_MODULE_ID")
	private FishModule		fishModule;
	
	@Column(name = "RUN_NET_NUMBER")
	private Integer			runNetNumber;
	
	@Column(name = "PERSON_ELECTRO_FISHING")
	private Integer			personElectroFishing;
	
	@Column(name = "PERSON_CATCHING")
	private Integer			personCatching;
	
	@Column(name = "EST_DURATION")
	private Integer			estduration;
	
	@Column(name = "MEASURED_DURATION")
	private String			measuredDuration;
	
	@Column(name = "ELECTRO_FISH_TYPE")
	private String			electroFishType;
	
	@Column(name = "PEAK_VOLT", columnDefinition = "NUMBER(9,2)")
	private Double			peakVolt;
	
	@Column(name = "BURST_RATE", columnDefinition = "NUMBER(9,2)")
	private Double			burstRate;
	
	@Column(name = "SLOW_RATE", columnDefinition = "NUMBER(9,2)")
	private Double			slowRate;
	
	@Column(name = "FAST_RATE", columnDefinition = "NUMBER(9,2)")
	private Double			fastRate;
	
	@Column(name = "SLOW_DUTY", columnDefinition = "NUMBER(9,2)")
	private Double			slowDuty;
	
	@Column(name = "FAST_DUTY", columnDefinition = "NUMBER(9,2)")
	private Double			fastDuty;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "fmRunNet", orphanRemoval = true)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@JsonManagedReference
	private List<Specie>	species	= new ArrayList<Specie>();
	
	public void setSpecies(List<Specie> species)
	{
		this.species.clear();
		this.species.addAll(species);
	}
}
