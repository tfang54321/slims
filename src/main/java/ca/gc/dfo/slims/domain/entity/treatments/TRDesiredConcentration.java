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
@Table(name = "slims_tr_desiredconcentrations", uniqueConstraints = { @UniqueConstraint(columnNames = {"TREATMENT_ID", "BRANCHLENTIC_ID", "STATION_ID"}) })
public class TRDesiredConcentration
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trdesiredcon_sequence")
	@SequenceGenerator(name = "trdesiredcon_sequence", initialValue = 1, allocationSize = 1, sequenceName = "TRDESIREDCON_SEQ")
	private Long			id;
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "treatment_id")
	private Treatment		treatment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branchlentic_id", nullable = false)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private BranchLentic	branchLentic;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_id", nullable = false)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Station			station;
	
	private String			stationAdjust;
	
	@Column(name = "APP_CONCENTRATION_MIN", columnDefinition = "NUMBER(9,2)")
	private Double			appConcentrationMin;
	
	@Column(name = "APP_CONCENTRATION_MAX", columnDefinition = "NUMBER(9,2)")
	private Double			appConcentrationMax;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double			niclosamide;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchLentic == null) ? 0 : branchLentic.hashCode());
		result = prime * result + ((station == null) ? 0 : station.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TRDesiredConcentration other = (TRDesiredConcentration) obj;
		if (branchLentic == null)
		{
			if (other.branchLentic != null)
				return false;
		}
		else if (!branchLentic.equals(other.branchLentic))
			return false;
		if (station == null)
		{
			if (other.station != null)
				return false;
		}
		else if (!station.equals(other.station))
			return false;
		return true;
	}
	
}
