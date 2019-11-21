package ca.gc.dfo.slims.domain.entity.treatments;

import java.util.Date;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import com.fasterxml.jackson.annotation.JsonBackReference;

import ca.gc.dfo.slims.domain.entity.common.RefCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Entity
@Table(name = "slims_tr_logistics")
public class TRLogistics
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trlogistics_sequence")
	@SequenceGenerator(name = "trlogistics_sequence", initialValue = 1, allocationSize = 1, sequenceName = "TRLOGISTICS_SEQ")
	private Long		id;
	
	@OneToOne
	@MapsId
	@JsonBackReference
	private Treatment	treatment;
	
	@Column(name = "TREATMENT_START")
	@Temporal(TemporalType.DATE)
	private Date		treatmentStart;
	
	@Column(name = "TREATMENT_END")
	@Temporal(TemporalType.DATE)
	private Date		treatmentEnd;

	@Column(name = "FKREF_METHODOLOGY_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode		methodology;
	
	@Column(name = "TOTAL_DISCHARGE", columnDefinition = "NUMBER(9,2)")
	private Double		totalDischarge;
	
	@Column(name = "KILOMETER_TREATED", columnDefinition = "NUMBER(9,2)")
	private Double		kilometerTreated;
	
	@Column(name = "CALENDAR_DAYS")
	private Integer		calendarDays;

	@Column(name = "FKREF_ABUNDANCE_INDEX_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode		abundanceIndex;
	
	private String		remarks;
	
	@Column(name = "MAX_CREW_SIZE")
	private Integer		maxCrewSize;
	
	@Column(name = "PERSON_DAYS", columnDefinition = "NUMBER(9,2)")
	private Double		personDays;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		TRLogistics other = (TRLogistics) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
