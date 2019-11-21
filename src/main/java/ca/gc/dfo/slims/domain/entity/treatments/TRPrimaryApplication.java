package ca.gc.dfo.slims.domain.entity.treatments;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.GeoUTM;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "slims_tr_primary_apps", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "TREATMENT_ID", "BRANCHLENTIC_ID", "STATION_FROM_ID" })
})
public class TRPrimaryApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trprimapp_sequence")
    @SequenceGenerator(name = "trprimapp_sequence", allocationSize = 1, sequenceName = "TRPRIMAPP_SEQ")
    private Long                             id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "treatment_id")
    private Treatment                        treatment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchlentic_id")
    @JsonIgnoreProperties(
    { "hibernateLazyInitializer", "handler" })
    private BranchLentic                     branchLentic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_from_id")
    @JsonIgnoreProperties(
    { "hibernateLazyInitializer", "handler" })
    private Station                           stationFrom;

    @Column(name = "STATION_FROM_ADJUST")
    private String                            stationFromAdjust;

    @Column(name = "APP_ID")
    private Long                              applicationId;

    @Column(name = "TREATMENT_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date                              treatmentStartDate;

    @Column(name = "TREATMENT_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date                              treatmentEndDate;

    @Column(name = "TIME_ON")
    private String                            timeOn;

    @Column(name = "TIME_OFF")
    private String                            timeOff;

    private String                            duration;

    @Column(name = "FKREF_APPLICATION_CODE_ID")
    @Convert(converter = RefCodeLongConverter.class)
    private RefCode                           applicationCode;

    @Column(name = "FKREF_APPLICATION_METHOD_ID")
    @Convert(converter = RefCodeLongConverter.class)
    private RefCode                           applicationMethod;

    @Embedded
    private GeoUTM                            geoUTM;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "SLIMS_TR_PRIMAPP_TMFS", joinColumns = @JoinColumn(name = "PRIMAPP_ID"))
    private List<TRTFM>                        trTFMs                        = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "SLIMS_TR_PRIMAPP_WETTABLEPOWDERS", joinColumns = @JoinColumn(name = "PRIMAPP_ID"))
    private List<TRWettablePowder>            trWettablePowders            = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "SLIMS_TR_PRIMAPP_EMULSIFIABLECONCENTRATES", joinColumns = @JoinColumn(name = "PRIMAPP_ID"))
    private List<TREmulsifiableConcentrate>    trEmulsifiableConcentrates    = new ArrayList<>();

}
