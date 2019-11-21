package ca.gc.dfo.slims.domain.entity.larvalassessments;

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
@Table(name = "slims_la_physical_chemical_data")
public class LAPhysicalChemicalData
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "laphyche_sequence")
	@SequenceGenerator(name = "laphyche_sequence", initialValue = 1, allocationSize = 1, sequenceName = "LAPHYCHE_SEQ")
	private Long				id;
	
	@OneToOne
	@MapsId
	@JsonBackReference
	private LarvalAssessment	larvalAssessment;
	
	@Column(name = "WATER_SURFACE_TEMP", columnDefinition = "NUMBER(9,2)")
	private Double				waterSurfaceTemp;
	
	@Column(name = "WATER_BOTTOM_TEMP", columnDefinition = "NUMBER(9,2)")
	private Double				waterBottomTemp;
	
	@Column(name = "CONDUCTIVITY")
	private Integer				conductivity;
	
	@Column(name = "CONDUCTIVITY_AT", columnDefinition = "NUMBER(9,2)")
	private Double				conductivityAt;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double				phValue;
	
	@Column(name = "MEAN_DEPTH", columnDefinition = "NUMBER(9,2)")
	private Double				meanDepth;
	
	@Column(name = "MEAN_STREAM_WIDTH", columnDefinition = "NUMBER(9,2)")
	private Double				meanStreamWidth;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double				discharge;

	@Column(name = "FKREF_METHOD_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode				method;
	
	private String				turbidity;
	
}
