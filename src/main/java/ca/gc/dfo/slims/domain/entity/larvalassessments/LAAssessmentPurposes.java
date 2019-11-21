package ca.gc.dfo.slims.domain.entity.larvalassessments;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "slims_la_assessment_purpose")
public class LAAssessmentPurposes {

    public LAAssessmentPurposes(){ }

    public LAAssessmentPurposes(LarvalAssessment larvalAssessment, Long assessmentPurposeId){
        this.larvalAssessment = larvalAssessment;
        this.assessmentPurposeId = assessmentPurposeId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "la_assessment_purpose_sequence")
    @SequenceGenerator(name = "la_assessment_purpose_sequence", initialValue=1, allocationSize = 1, sequenceName = "LA_ASSESSMENT_PURPOSE_SEQ")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "LARVAL_ASSESSMENT_ID")
    @JsonBackReference
    private LarvalAssessment larvalAssessment;

    @Column(name = "FKREF_ASSESSMENT_PURPOSES_ID")
    private Long assessmentPurposeId;
}
