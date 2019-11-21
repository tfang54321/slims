package ca.gc.dfo.slims.domain.entity.parasiticcollection;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.domain.entity.common.FishIndividual;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Entity
@Table(name = "slims_parasitic_attachments")
public class ParasiticAttachment {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paraattachment_sequence")
	@SequenceGenerator(name = "paraattachment_sequence", initialValue=1, allocationSize = 1, sequenceName = "PARAATTACHMENT_SEQ")
	private Long id;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "paracol_id")
    private ParasiticCollection parasiticCollection;
	
	private Integer idNumber;

	@Column(name = "REF_LAMPREYS_ATTACHED_TO_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode lampreysattachedto;

	@Column(name = "HOST_SPECIES")
	private String hostSpecies;

	@Column(name = "SEA_LAMPREY_SAMPLED")
	private Integer seaLampreySampled;

	@Column(name = "SILVER_LAMPREY_SAMPLED")
	private Integer silverLampreySampled;

	@Column(name = "TOTAL_ATTACHED")
	private Integer totalAttached;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parasiticAttachment", orphanRemoval = true)
	private List<FishIndividual> fishIndividuals = new ArrayList<FishIndividual>();
	
	public void setFishIndividuals(List<FishIndividual> fishIndividuals) {
		this.fishIndividuals.clear();
		this.fishIndividuals.addAll(fishIndividuals);
	}

}
