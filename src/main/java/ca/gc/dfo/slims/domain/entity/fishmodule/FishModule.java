package ca.gc.dfo.slims.domain.entity.fishmodule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.location.GeoUTM;
import ca.gc.dfo.slims.domain.entity.common.location.LocationDetails;
import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value =
{ "dateCreated", "dateModified" }, allowGetters = true)
@Entity
@Table(name = "slims_fish_modules")
public class FishModule
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fishmodule_sequence")
	@SequenceGenerator(name = "fishmodule_sequence", initialValue = 1, allocationSize = 1, sequenceName = "FISHMODULE_SEQ")
	private Long				id;
	
	@Embedded
	private Sample				sample;
	
	@Column(name = "START_TIME")
	private String				startTime;
	
	@Column(name = "FINISH_TIME")
	private String				finishTime;
	
	@Embedded
	private LocationReference	locationReference;
	
	@Embedded
	private GeoUTM				geoUTM;
	
	@Embedded
	private LocationDetails		locationDetails;
	
	private String				effectiveness;

	@Column(name = "FKREF_FM_PURPOSE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				fmPurpose;

	@Column(name = "FKREF_TECHNIQUE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				technique;

	@Column(name = "FKREF_METHODOLOGY_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				methodology;

	@Column(name = "FKREF_OPERATOR1_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				operator1;

	@Column(name = "FKREF_OPERATOR2_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				operator2;

	@Column(name = "FKREF_OPERATOR3_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				operator3;
	
	//@OneToOne(mappedBy = "fishModule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
	//@JsonManagedReference
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "fishModule", orphanRemoval = true)
	@JsonManagedReference
	private List<FMRunNet>			fmRunNets = new ArrayList<FMRunNet>();
	
	public void setFmRunNets(List<FMRunNet> fmRunNets)
	{
		this.fmRunNets.clear();
		this.fmRunNets.addAll(fmRunNets);
	}
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "fishModule", orphanRemoval = true)
	@JsonManagedReference
	private List<FMHabitat>		fmHabitats	= new ArrayList<FMHabitat>();
	
	public void setFmHabitats(List<FMHabitat> fmHabitats)
	{
		this.fmHabitats.clear();
		this.fmHabitats.addAll(fmHabitats);
	}
	
	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date	dateCreated;
	
	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date	dateModified;
	
}
