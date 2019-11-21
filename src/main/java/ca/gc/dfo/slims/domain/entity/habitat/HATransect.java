package ca.gc.dfo.slims.domain.entity.habitat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slims_ha_transects")
public class HATransect
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transect_sequence")
	@SequenceGenerator(name = "transect_sequence", initialValue = 1, allocationSize = 1, sequenceName = "TRANSECT_SEQ")
	private Long					id;
	
	@OneToOne
	@MapsId
	@JsonBackReference
	private HabitatInventory		habitatInventory;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double					streamWidth;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double					transectSpacing;
	
	@Column(name = "TOTAL_REACH_LENGTH", columnDefinition = "NUMBER(9,2)")
	private Double					totalReachLength;
	
	@Column(name = "EST_DISCHARGE", columnDefinition = "NUMBER(9,2)")
	private Double					estDischarge;
	
	private String					streamCondition;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "SLIMS_HA_TRANSECT_DETAIL", joinColumns = @JoinColumn(name = "TRANSECT_ID"))
	private List<HATransectDetail>	haTransectDetails = new ArrayList<HATransectDetail>();
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer					bedrock;
	
	@Column(name = "HARDPAN_CLAY", columnDefinition = "NUMBER(10,0)")
	private Integer					hardpanClay;
	
	@Column(name = "CLAY_SEDIMENTS", columnDefinition = "NUMBER(10,0)")
	private Integer					claySediments;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer					gravel;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer					rubble;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer					sand;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer					silt;
	
	@Column(name = "SILT_DETRITUS", columnDefinition = "NUMBER(10,0)")
	private Integer					siltDetritus;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer					detritus;
	
	@Column(name = "CUMULATIVE_SPAWNING", columnDefinition = "NUMBER(9,2)")
	private Double					cumulativeSpawning;
	
}
