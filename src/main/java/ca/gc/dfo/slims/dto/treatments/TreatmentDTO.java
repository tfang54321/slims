package ca.gc.dfo.slims.dto.treatments;

import ca.gc.dfo.slims.utility.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TreatmentDTO {

    public TreatmentDTO(Long id,
                        String sampleCode,
                        String sampleStatus,
                        Date treatmentStart,
                        Date treatmentEnd,
                        String lakeCode,
                        String lakeNameEn,
                        String lakeNameFr,
                        String streamCode,
                        String streamNameEn,
                        String streamNameFr) {
        this.id = id;
        this.sampleCode = sampleCode;
        this.sampleStatus = sampleStatus;
        this.treatmentStart = treatmentStart;
        this.treatmentEnd = treatmentEnd;
        this.lakeCode = lakeCode;
        this.lakeNameEn = lakeNameEn;
        this.lakeNameFr = lakeNameFr;
        this.streamCode = streamCode;
        this.streamNameEn = streamNameEn;
        this.streamNameFr = streamNameFr;
    }

    private Long id;
    private String sampleCode;
    private String sampleStatus;
    private Date treatmentStart;
    private Date treatmentEnd;

    @JsonIgnore
    private String lakeCode;
    @JsonIgnore
    private String lakeNameEn;
    @JsonIgnore
    private String lakeNameFr;

    @JsonIgnore
    private String streamCode;
    @JsonIgnore
    private String streamNameEn;
    @JsonIgnore
    private String streamNameFr;

    public String getLake() {
        return "(" + lakeCode + ")" + (CommonUtils.isShowFrench() ? lakeNameFr : lakeNameEn);
    }

    public String getStream() {
        return "(" + streamCode + ")" + (CommonUtils.isShowFrench() ? streamNameFr : streamNameEn);
    }
}
