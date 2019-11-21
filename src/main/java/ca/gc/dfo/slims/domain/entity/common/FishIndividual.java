package ca.gc.dfo.slims.domain.entity.common;

import java.util.Date;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;

import ca.gc.dfo.slims.domain.entity.parasiticcollection.ParasiticAttachment;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@Entity
@Table(name = "slims_common_fish_individuals")
public class FishIndividual {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonfishindividual_sequence")
	@SequenceGenerator(name = "commonfishindividual_sequence", initialValue = 1, allocationSize = 1, sequenceName = "COMMONFISHINDIVIDUAL_SEQ")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specie_id")
	@JsonBackReference
	private Specie specie;

	@ManyToOne
	@JoinColumn(name = "para_attachment_id")
	@JsonBackReference
	private ParasiticAttachment parasiticAttachment;

	private String specieCode;

	@Column(name = "INDIVIDUAL_LENGTH", columnDefinition = "NUMBER(9,2)")
	private Double individualLength;

	@Column(name = "INDIVIDUAL_WEIGHT", columnDefinition = "NUMBER(9,2)")
	private Double individualWeight;

	@Column(name = "FKREF_INDIVIDUAL_SEX_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode individualSex;

	@Column(name = "FKREF_INDIVIDUAL_MATURITY_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode individualMaturity;

	@Column(name = "FKREF_SPAWNING_CONDITION_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode spawningCondition;

	@Column(name = "CONDITION_FACTOR", columnDefinition = "NUMBER(9,2)")
	private Double conditionfactor;

	@Column(name = "FKREF_CONDITION_OF_GUT_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode conditionOfGut;

	@Column(name = "FKREF_FULLNESS_OF_GUT_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode fullnessOfGut;

	@Column(name = "FKREF_CONTENTS_OF_GUT_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode contentsOfGut;

	@Column(name = "FKREF_SPECIMEN_STATE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode specimenState;

	@Column(name = "DATE_MEASURED")
	@Temporal(TemporalType.DATE)
	private Date dateMeasured;

	private Boolean recapture;

	private Boolean kept;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conditionOfGut == null) ? 0 : conditionOfGut.hashCode());
		result = prime * result + ((conditionfactor == null) ? 0 : conditionfactor.hashCode());
		result = prime * result + ((contentsOfGut == null) ? 0 : contentsOfGut.hashCode());
		result = prime * result + ((dateMeasured == null) ? 0 : dateMeasured.hashCode());
		result = prime * result + ((fullnessOfGut == null) ? 0 : fullnessOfGut.hashCode());
		result = prime * result + ((individualLength == null) ? 0 : individualLength.hashCode());
		result = prime * result + ((individualMaturity == null) ? 0 : individualMaturity.hashCode());
		result = prime * result + ((individualSex == null) ? 0 : individualSex.hashCode());
		result = prime * result + ((individualWeight == null) ? 0 : individualWeight.hashCode());
		result = prime * result + ((kept == null) ? 0 : kept.hashCode());
		result = prime * result + ((recapture == null) ? 0 : recapture.hashCode());
		result = prime * result + ((spawningCondition == null) ? 0 : spawningCondition.hashCode());
		result = prime * result + ((specieCode == null) ? 0 : specieCode.hashCode());
		result = prime * result + ((specimenState == null) ? 0 : specimenState.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FishIndividual other = (FishIndividual) obj;
		if (conditionOfGut == null) {
			if (other.conditionOfGut != null)
				return false;
		} else if (!conditionOfGut.equals(other.conditionOfGut))
			return false;
		if (conditionfactor == null) {
			if (other.conditionfactor != null)
				return false;
		} else if (!conditionfactor.equals(other.conditionfactor))
			return false;
		if (contentsOfGut == null) {
			if (other.contentsOfGut != null)
				return false;
		} else if (!contentsOfGut.equals(other.contentsOfGut))
			return false;
		if (dateMeasured == null) {
			if (other.dateMeasured != null)
				return false;
		} else if (!dateMeasured.equals(other.dateMeasured))
			return false;
		if (fullnessOfGut == null) {
			if (other.fullnessOfGut != null)
				return false;
		} else if (!fullnessOfGut.equals(other.fullnessOfGut))
			return false;
		if (individualLength == null) {
			if (other.individualLength != null)
				return false;
		} else if (!individualLength.equals(other.individualLength))
			return false;
		if (individualMaturity == null) {
			if (other.individualMaturity != null)
				return false;
		} else if (!individualMaturity.equals(other.individualMaturity))
			return false;
		if (individualSex == null) {
			if (other.individualSex != null)
				return false;
		} else if (!individualSex.equals(other.individualSex))
			return false;
		if (individualWeight == null) {
			if (other.individualWeight != null)
				return false;
		} else if (!individualWeight.equals(other.individualWeight))
			return false;
		if (kept == null) {
			if (other.kept != null)
				return false;
		} else if (!kept.equals(other.kept))
			return false;
		if (recapture == null) {
			if (other.recapture != null)
				return false;
		} else if (!recapture.equals(other.recapture))
			return false;
		if (spawningCondition == null) {
			if (other.spawningCondition != null)
				return false;
		} else if (!spawningCondition.equals(other.spawningCondition))
			return false;
		if (specieCode == null) {
			if (other.specieCode != null)
				return false;
		} else if (!specieCode.equals(other.specieCode))
			return false;
		if (specimenState == null) {
			if (other.specimenState != null)
				return false;
		} else if (!specimenState.equals(other.specimenState))
			return false;
		return true;
	}

}
