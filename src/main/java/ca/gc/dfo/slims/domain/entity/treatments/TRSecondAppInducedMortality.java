package ca.gc.dfo.slims.domain.entity.treatments;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.Specie;
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
@Table(name = "slims_tr_secondapp_induced_mortalities")
public class TRSecondAppInducedMortality
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trsecondappmortality_sequence")
	@SequenceGenerator(name = "trsecondappmortality_sequence", initialValue = 1, allocationSize = 1, sequenceName = "TRSECONDAPPMORTALITY_SEQ")
	private Long					id;
	
	@OneToOne
	@MapsId
	@JsonBackReference
	private TRSecondaryApplication	trSecondaryApplication;
	
	private String					collectCondition;

	@Column(name = "FKREF_LARVAE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode					larvae;

	@Column(name = "FKREF_TRANSFORMERS_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode					transformers;

	@Column(name = "FKREF_ADULTS_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode					adults;
	
	private String					remarks;
	
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "trSecondAppInducedMortality", orphanRemoval = true)
	private List<Specie>			species	= new ArrayList<Specie>();
	
	public void setSpecies(List<Specie> species)
	{
		this.species.clear();
		this.species.addAll(species);
	}
}
