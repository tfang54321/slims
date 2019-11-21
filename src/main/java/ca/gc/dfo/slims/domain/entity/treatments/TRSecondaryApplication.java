package ca.gc.dfo.slims.domain.entity.treatments;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.GeoUTM;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "slims_tr_secondary_apps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"TREATMENT_ID", "BRANCHLENTIC_FROM_ID", "STATION_FROM_ID"})
})
public class TRSecondaryApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trsecondapp_sequence")
    @SequenceGenerator(name = "trsecondapp_sequence", allocationSize = 1, sequenceName = "TRSECONDAPP_SEQ")
    private Long                        id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "treatment_id")
    private Treatment                     treatment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchlentic_from_id", nullable = false)
    @JsonIgnoreProperties(
    { "hibernateLazyInitializer", "handler" })
    private BranchLentic                  branchLenticFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_from_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Station                       stationFrom;

    @Column(name = "STATION_FROM_ADJUST")
    private String                        stationFromAdjust;

    @Column(name = "APP_ID")
    private Long                          applicationId;

    @Column(name = "TREATMENT_DATE")
    @Temporal(TemporalType.DATE)
    private Date                          treatmentDate;

    @Column(name = "TIME_START")
    private String                        timeStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchlentic_to_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private BranchLentic                   branchLenticTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_to_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Station                        stationTo;

    @Column(name = "STATION_TO_ADJUST")
    private String                         stationToAdjust;
    private String                         duration;

    @Column(name = "FKREF_APPLICATION_CODE_ID")
    @Convert(converter = RefCodeLongConverter.class)
    private RefCode                        applicationCode;

    @Column(name = "FKREF_APPLICATION_METHOD_ID")
    @Convert(converter = RefCodeLongConverter.class)
    private RefCode                        applicationMethod;

    @Embedded
    private GeoUTM                         geoUTM;

    @Embedded
    private TRTFM                          trTFM;

    @Embedded
    private TRGranularBayluscide           trGranularBayluscide;

    @OneToOne(
        mappedBy = "trSecondaryApplication", cascade = CascadeType.ALL,
        fetch = FetchType.LAZY, orphanRemoval = true
    )
    @JsonManagedReference
    private TRSecondAppInducedMortality    trSecondAppInducedMortality;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((branchLenticFrom == null) ? 0 : branchLenticFrom.hashCode());
        result = prime * result + ((stationFrom == null) ? 0 : stationFrom.hashCode());
        result = prime * result + ((stationFromAdjust == null) ? 0 : stationFromAdjust.hashCode());
        result = prime * result + ((timeStart == null) ? 0 : timeStart.hashCode());
        result = prime * result + ((treatmentDate == null) ? 0 : treatmentDate.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TRSecondaryApplication other = (TRSecondaryApplication) obj;
        if (branchLenticFrom == null) {
            if (other.branchLenticFrom != null) {
                return false;
            }
        } else if (!branchLenticFrom.equals(other.branchLenticFrom)) {
            return false;
        }
        if (stationFrom == null) {
            if (other.stationFrom != null) {
                return false;
            }
        } else if (!stationFrom.equals(other.stationFrom)) {
            return false;
        }
        if (stationFromAdjust == null) {
            if (other.stationFromAdjust != null) {
                return false;
            }
        } else if (!stationFromAdjust.equals(other.stationFromAdjust)) {
            return false;
        }
        if (timeStart == null) {
            if (other.timeStart != null) {
                return false;
            }
        } else if (!timeStart.equals(other.timeStart)) {
            return false;
        }
        if (treatmentDate == null) {
            if (other.treatmentDate != null) {
                return false;
            }
        } else if (!treatmentDate.equals(other.treatmentDate)) {
            return false;
        }
        if (applicationId == null) {
            return other.applicationId == null;
        }
        return applicationId.equals(other.applicationId);
    }

}
