package ca.gc.dfo.slims.domain.entity.larvalassessments;

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
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.Specie;
import ca.gc.dfo.slims.domain.entity.common.location.GeoUTM;
import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value =
{ "dateCreated", "dateModified" }, allowGetters = true)
@Entity
@Table(name = "slims_larval_assessments")
public class LarvalAssessment
{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "laassessments_sequence")
	@SequenceGenerator(name = "laassessments_sequence", initialValue = 1, allocationSize = 1, sequenceName = "LAASSESSMENT_SEQ")
	private Long					id;

	// main
	@Embedded
	private Sample					sample;

	@Column(name = "START_TIME")
	private String					startTime;

	@Column(name = "FINISH_TIME")
	private String					finishTime;

	@Embedded
	private LocationReference		locationReference;

	@Embedded
	private GeoUTM					geoUTM;

	@Transient
//	@Column(name = "ASSESSMENT_PURPOSES")
	private List<RefCode>			laPurposes;

	@Column(name = "FKREF_SURVEY_METHODOLOGY_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode					surveyMethodology;

	@Column(name = "FKREF_OPERATOR1_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode					operator1;

	@Column(name = "FKREF_OPERATOR2_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode					operator2;

	@Column(name = "FKREF_OPERATOR3_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode					operator3;

	// electrofishing details
	@OneToOne(mappedBy = "larvalAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
	@JsonManagedReference
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private LAElectrofishingDetails	laElectrofishingDetails;

	// granular bayer details
	@OneToOne(mappedBy = "larvalAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
	@JsonManagedReference
	private LAGranularBayerDetails	laGranularBayerDetails;

	// physical chemical data
	@OneToOne(mappedBy = "larvalAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
	@JsonManagedReference
	private LAPhysicalChemicalData	laPhysicalChemicalData;

	private String					collectCondition;

	private String					collectConditionDetails;

	private String					effectiveness;

	private String					effectivenessDetails;

	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "larvalAssessment", orphanRemoval = true)
	private List<Specie>			species	= new ArrayList<Specie>();

	public void setSpecies(List<Specie> species)
	{
		this.species.clear();
		this.species.addAll(species);
	}

	// assessmentPurposes
	@OneToMany(mappedBy = "larvalAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JsonManagedReference
	@JsonIgnoreProperties(
			{ "hibernateLazyInitializer", "handler" })
	private List<LAAssessmentPurposes>	laAssessmentPurposes = new ArrayList<LAAssessmentPurposes>();

	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date			dateCreated;

	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date			dateModified;

	@PostLoad
	private void postLoadLA(){
		RefCodeLongConverter refCodeLongConverter = new RefCodeLongConverter();
		laPurposes = new ArrayList<>();
		for(LAAssessmentPurposes laPurp : laAssessmentPurposes){
			laPurposes.add(refCodeLongConverter.convertToEntityAttribute(laPurp.getAssessmentPurposeId()));
		}
	}

	private void prePersistLA(){
		RefCodeLongConverter refCodeLongConverter = new RefCodeLongConverter();
		List<LAAssessmentPurposes> laAPList =  new ArrayList<>();
		Boolean found = false;

		for (RefCode refCode : this.laPurposes)
		{
			Long referCodeId = refCodeLongConverter.convertToDatabaseColumn(refCode);
			for(LAAssessmentPurposes laAssessmentPurpose : this.laAssessmentPurposes){
				if(laAssessmentPurpose.getAssessmentPurposeId().equals(referCodeId)){
					laAPList.add(laAssessmentPurpose);
					found = true;
					break;
				}
			}
			if(!found){
				laAPList.add(new LAAssessmentPurposes(this, referCodeId));
			}
			found = false;
		}
		this.laAssessmentPurposes.clear();
		this.laAssessmentPurposes.addAll(laAPList);
	}

	public void runPrePersist(){
		prePersistLA();
	}

	@Transient
	private List<String>	purposeCodeNames	= new ArrayList<String>();
	
	public List<String> getPurposeCodeNames()
	{
		for (RefCode refCode : laPurposes)
		{
			purposeCodeNames.add(refCode.getCodePair().getCodeName());
		}
		
		return purposeCodeNames;
	}
	
}
