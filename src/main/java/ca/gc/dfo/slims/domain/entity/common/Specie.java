package ca.gc.dfo.slims.domain.entity.common;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.domain.entity.adultassessments.AdultAssessment;
import ca.gc.dfo.slims.domain.entity.fishmodule.FMRunNet;
import ca.gc.dfo.slims.domain.entity.larvalassessments.LarvalAssessment;
import ca.gc.dfo.slims.domain.entity.treatments.TRSecondAppInducedMortality;
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
@Table(name = "slims_common_species")
public class Specie {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonspecie_sequence")
	@SequenceGenerator(name = "commonspecie_sequence", initialValue = 1, allocationSize = 1, sequenceName = "COMMONSPECIE_SEQ")
	private Long id;

	@Column(name = "SPECIES_CODE")
	private String speciesCode;

	@Column(name = "OBSERVED_ALIVED")
	private Integer observedAlived;

	@Column(name = "OBSERVED_DEAD")
	private Integer observedDead;

	@Column(name = "COLLECTED_RELEASED")
	private Integer collectedReleased;

	@Column(name = "COLLECTED_DEAD")
	private Integer collectedDead;

	private Integer total;

	@Column(name = "TOTAL_CAUGHT")
	private Integer totalcaught;

	@Column(name = "TOTAL_OBSERVED")
	private Integer totalObserved;

	@Column(name = "FKREF_TRAP_CHAMBER_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode trapChamber;

	@Column(name = "FIRST_TIME_CAPTURE_MALES")
	private Integer firstTimeCaptureMales;

	@Column(name = "FIRST_TIME_CAPTURE_FEMALES")
	private Integer firstTimeCaptureFemales;

	@Column(name = "FIRST_TIME_CAPTURE_ALIVE")
	private Integer firstTimeCaptureAlive;

	@Column(name = "FIRST_TIME_CAPTURE_DEAD")
	private Integer firstTimeCaptureDead;

	@Column(name = "FIRST_TIME_CAPTURE_TOTAL")
	private Integer firstTimeCaptureTotal;

	@Column(name = "APPROX_OBSERVED")
	private Integer approxObserved;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "specie", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<FishIndividual> fishIndividuals = new ArrayList<FishIndividual>();
	
	public void setFishIndividuals(List<FishIndividual> fishIndividuals) {
		this.fishIndividuals.clear();
		this.fishIndividuals.addAll(fishIndividuals);
	}

	/*public void addFishIndividual(FishIndividual fishIndividual) {
		fishIndividuals.add(fishIndividual);
		fishIndividual.setSpecie(this);
	}

	public void removeFishIndividual(FishIndividual fishIndividual) {
		fishIndividuals.remove(fishIndividual);
		fishIndividual.setSpecie(null);
	}*/

	@ManyToOne
	@JoinColumn(name = "la_id")
	@JsonBackReference
	private LarvalAssessment larvalAssessment;

	@ManyToOne
	@JoinColumn(name = "aa_id")
	@JsonBackReference
	private AdultAssessment adultAssessment;

	@ManyToOne
	@JoinColumn(name = "fm_runnet_id")
	@JsonBackReference
	private FMRunNet fmRunNet;

	@ManyToOne
	@JoinColumn(name = "tr_secondapp_im_id")
	@JsonBackReference
	private TRSecondAppInducedMortality trSecondAppInducedMortality;

}
