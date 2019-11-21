package ca.gc.dfo.slims.domain.entity.habitat;

import java.util.Date;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.Sample;
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
@Table(name = "slims_habitat_inventories")
public class HabitatInventory
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habitatinventory_sequence")
	@SequenceGenerator(name = "habitatinventory_sequence", initialValue = 1, allocationSize = 1, sequenceName = "HABITATINVENTORY_SEQ")
	private Long				id;
	
	@Embedded
	private Sample				sample;
	
	private String				transectId;
	
	@Column(name = "INVENTORY_DATE")
	@Temporal(TemporalType.DATE)
	private Date				inventoryDate;
	
	@Embedded
	private LocationReference	locationReference;
	
	@Embedded
	private GeoUTM				geoUTM;

	@Column(name = "FKREF_OPERATION_UNIT_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				operationUnit;

	@Column(name = "FKREF_HABITAT_MEASUREMENTS_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				habitatMeasurements;

	@Column(name = "FKREF_OPERATOR1_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				operator1;

	@Column(name = "FKREF_OPERATOR2_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				operator2;

	@Column(name = "FKREF_OPERATOR3_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				operator3;
	
	@OneToOne(mappedBy = "habitatInventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
	@JsonManagedReference
	private HATransect			haTransect;
	
	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date				dateCreated;
	
	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date				dateModified;
	
}
