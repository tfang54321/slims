package ca.gc.dfo.slims.domain.entity.common.location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.gc.dfo.slims.converter.ReachLengthAndUpdateYearConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value =
{ "dateCreated", "yearUpdated" }, allowGetters = true)
@Entity
@Table(name = "slims_common_reaches", uniqueConstraints =
{ @UniqueConstraint(columnNames =
		{ "REACH_CODE", "STREAM_ID" }) })
public class Reach
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reach_sequence")
	@SequenceGenerator(name = "reach_sequence", initialValue = 1, allocationSize = 1, sequenceName = "REACH_SEQ")
	private Long							id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "stream_id", nullable = false)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Stream							stream;
		
	@ManyToMany(cascade ={ CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "slims_common_reach_station", joinColumns = @JoinColumn(name = "reach_id"), inverseJoinColumns = @JoinColumn(name = "station_id"))
	@JsonIgnore
	private Set<Station>					assignedStations	= new HashSet<>();
	
	@Column(name = "REACH_CODE")
	private String							reachCode;
	
	@Size(max = 100, message = "{reach_name_validation}")
	@Column(name = "REACH_NAME")
	private String							reachName;
	
	@Column(name = "LENGTH_AND_UPDATEYEAR", columnDefinition = "VARCHAR2 (4000)")
	@Convert(converter = ReachLengthAndUpdateYearConverter.class)
	private List<ReachLengthAndUpdateYear>	lengthAndUpdateYears = new ArrayList<ReachLengthAndUpdateYear>();
	
	@Transient
	private String							showText;
	
	public String getShowText()
	{
		if (reachName == null)
		{
			reachName = "";
		}
		return "(" + reachCode + ")" + reachName;
	}
	
	@Transient
	private List<Long> assignedStationIds = new ArrayList<Long>();
	
	public List<Long> getAssignedStationIds()
	{
		for(Station station: this.assignedStations)
		{
			this.assignedStationIds.add(station.getId());
		}
		
		return this.assignedStationIds;
	}
	
	public void assignStation(Station station)
	{
		assignedStations.add(station);
		station.getAssignedReaches().add(this);
	}
	
	public void unassignStation(Station station)
	{
		assignedStations.remove(station);
		station.getAssignedReaches().remove(this);
	}
	
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
		Reach other = (Reach) obj;
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
