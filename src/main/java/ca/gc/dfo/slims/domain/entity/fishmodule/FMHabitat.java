package ca.gc.dfo.slims.domain.entity.fishmodule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slims_fm_habitat")
public class FMHabitat
{	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fmhabitat_sequence")
	@SequenceGenerator(name = "fmhabitat_sequence", initialValue = 1, allocationSize = 1, sequenceName = "FMHABITAT_SEQ")
	private Long		id;
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "FISH_MODULE_ID")
	private FishModule	fishModule;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double		width;
	
	@Column(columnDefinition = "NUMBER(9,2)")
	private Double		depth;
	
	@Column(name = "DISTANCE_FROM_LAST_TRANSECT", columnDefinition = "NUMBER(9,2)")
	private Double		distanceFromLastTransect;
	
	private String		location;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		bedrock;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		hardpanClay;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		rubble;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		gravel;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		sand;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		siltDetritus;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		claySediments;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		pools;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		riffles;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		eddyLagoon;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		theRun;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		bankOverhang;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		vegetation;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		woodyDebris;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		algae;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		shorelineGrasses;
	
	@Column(columnDefinition = "NUMBER(10,0)")
	private Integer		shorelineTressShrubs;
	
}
