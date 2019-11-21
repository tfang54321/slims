package ca.gc.dfo.slims.dto.adultassessment;

import ca.gc.dfo.slims.utility.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AdultAssessmentDTO {

    public AdultAssessmentDTO(Long id,
                              String sampleCode,
                              String sampleStatus,
                              Date inspectDate,
                              String lakeCode,
                              String lakeNameEn,
                              String lakeNameFr,
                              String streamCode,
                              String streamNameEn,
                              String streamNameFr,
                              String branchLenticCode,
                              String branchLenticNameEn,
                              String branchLenticNameFr,
                              String stationCode,
                              String stationName) {
        this.id = id;
        this.sampleCode = sampleCode;
        this.sampleStatus = sampleStatus;
        this.inspectDate = inspectDate;
        this.lakeCode = lakeCode;
        this.lakeNameEn = lakeNameEn;
        this.lakeNameFr = lakeNameFr;
        this.streamCode = streamCode;
        this.streamNameEn = streamNameEn;
        this.streamNameFr = streamNameFr;
        this.branchLenticCode = branchLenticCode;
        this.branchLenticNameEn = branchLenticNameEn;
        this.branchLenticNameFr = branchLenticNameFr;
        this.stationCode = stationCode;
        this.stationName = stationName;
    }

    private Long id;
    private String sampleCode;
    private String sampleStatus;
    private Date inspectDate;

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

    @JsonIgnore
    private String branchLenticCode;
    @JsonIgnore
    private String branchLenticNameEn;
    @JsonIgnore
    private String branchLenticNameFr;

    @JsonIgnore
    private String stationCode;
    @JsonIgnore
    private String stationName;

    public String getLake() {
        return "(" + lakeCode + ")" + (CommonUtils.isShowFrench() ? lakeNameFr: lakeNameEn);
    }

    public String getStream() {
        return "(" + streamCode + ")" + (CommonUtils.isShowFrench() ? streamNameFr : streamNameEn);
    }

    public String getBranchLentic() {
        return "(" + branchLenticCode + ")" + (CommonUtils.isShowFrench() ? branchLenticNameFr : branchLenticNameEn);
    }

    public String getStationFrom() {
        if (stationName == null) {
            stationName = "";
        }
        return "(" + stationCode + ")" + stationName;
    }
}
