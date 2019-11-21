package ca.gc.dfo.slims.domain.entity.common.location;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value =
{ "dateCreated", "dateModified", "hibernateLazyInitializer", "handler" }, allowGetters = true)
@Entity
@Table(name = "slims_common_stations", uniqueConstraints =
{ @UniqueConstraint(columnNames =
		{ "STATION_CODE", "BRANCHLENTIC_ID" }) })
public class Station
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "station_sequence")
	@SequenceGenerator(name = "station_sequence", initialValue = 1, allocationSize = 1, sequenceName = "STATION_SEQ")
	private Long			id;
	
	@Column(name = "STATION_CODE")
	private String			stationCode;
	
	@Column(name = "STATION_NAME")
	private String			name;
	
	private String			description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "branchlentic_id", nullable = false)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private BranchLentic	branchLentic;
	
	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date			dateCreated;
	
	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date			dateModified;
	
	@Column(name = "LAT_DEG", columnDefinition = "NUMBER(9,2)")
	private Double			latDeg;
	
	@Column(name = "LONG_DEG", columnDefinition = "NUMBER(9,2)")
	private Double			longDeg;
	
	@Column(name = "FKREF_MAP_DATUM_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode			mapDatum;
	
	@Column(name = "FKREF_UTM_ZONE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode			utmZone;
	
	@Column(name = "FKREF_UTM_BAND_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode			utmBand;
	
	@Column(name = "UTM_EASTING", columnDefinition = "NUMBER(9,2)")
	private Double			utmEasting;
	
	@Column(name = "UTM_NORTHING", columnDefinition = "NUMBER(9,2)")
	private Double			utmNorthing;
	
	@ManyToMany(mappedBy = "assignedStations")
	@JsonIgnore
	private Set<Reach>		assignedReaches	= new HashSet<>();
	
	@Transient
	private String			showText;
	
	public String getShowText()
	{
		return this.toString();
	}
	
	@Override
	public String toString()
	{
		if (name == null)
		{
			name = "";
		}
		return "(" + stationCode + ")" + name;
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
		Station other = (Station) obj;
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
