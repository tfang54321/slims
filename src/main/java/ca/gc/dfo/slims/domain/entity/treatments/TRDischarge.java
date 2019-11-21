package ca.gc.dfo.slims.domain.entity.treatments;

import java.util.Date;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Entity
@Table(name = "slims_tr_discharges", uniqueConstraints = { @UniqueConstraint(columnNames = {"TREATMENT_ID", "BRANCHLENTIC_ID", "STATION_FROM_ID"}) })
public class TRDischarge
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trdischarge_sequence")
	@SequenceGenerator(name = "trdischarge_sequence", initialValue = 1, allocationSize = 1, sequenceName = "TRDISCHARGE_SEQ")
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
	@JoinColumn(name = "station_from_id", nullable = false)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Station			stationFrom;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_to_id")
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Station			stationTo;
	
	private String			stationFromAdjust;
	
	private String			stationToAdjust;
	
	@Column(name = "ANALYSIS_DATE")
	@Temporal(TemporalType.DATE)
	private Date			analysisDate;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double			discharge;

	@Column(name = "FKREF_DISCHARGE_CODE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode			dischargeCode;
	
	@Column(name = "ELAPSED_TIME")
	private String			elapsedTime;
	
	@Column(name = "CUMULATIVE_TIME")
	private String			cumulativeTime;

	@Column(name = "FKREF_FLOW_TIME_CODE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode			flowTimeCode;
	
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
		result = prime * result + ((stationFrom == null) ? 0 : stationFrom.hashCode());
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
		TRDischarge other = (TRDischarge) obj;
		if (branchLentic == null)
		{
			if (other.branchLentic != null)
				return false;
		}
		else if (!branchLentic.equals(other.branchLentic))
			return false;
		if (stationFrom == null)
		{
			if (other.stationFrom != null)
				return false;
		}
		else if (!stationFrom.equals(other.stationFrom))
			return false;
		return true;
	}
	
}
