package ca.gc.dfo.slims.dto.habitat;

import ca.gc.dfo.slims.utility.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HabitatInventoryDTO {

    public HabitatInventoryDTO(Long id,
                               String sampleCode,
                               String transectId,
                               Date inventoryDate,
                               String lakeCode,
                               String lakeNameEn,
                               String lakeNameFr,
                               String streamCode,
                               String streamNameEn,
                               String streamNameFr,
                               String branchLenticCode,
                               String branchLenticNameEn,
                               String branchLenticNameFr,
                               String stationFromCode,
                               String stationFromName,
                               String stationToCode,
                               String stationToName) {
        this.id = id;
        this.sampleCode = sampleCode;
        this.transectId = transectId;
        this.inventoryDate = inventoryDate;
        this.lakeCode = lakeCode;
        this.lakeNameEn = lakeNameEn;
        this.lakeNameFr = lakeNameFr;
        this.streamCode = streamCode;
        this.streamNameEn = streamNameEn;
        this.streamNameFr = streamNameFr;
        this.branchLenticCode = branchLenticCode;
        this.branchLenticNameEn = branchLenticNameEn;
        this.branchLenticNameFr = branchLenticNameFr;
        this.stationFromCode = stationFromCode;
        this.stationFromName = stationFromName;
        this.stationToCode = stationToCode;
        this.stationToName = stationToName;
    }

    private Long id;
    private String sampleCode;
    private String transectId;
    private Date inventoryDate;

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
    private String stationFromCode;
    @JsonIgnore
    private String stationFromName;

    @JsonIgnore
    private String stationToCode;
    @JsonIgnore
    private String stationToName;

    public String getLake() {
        return "(" + lakeCode + ")" + (CommonUtils.isShowFrench() ? lakeNameFr : lakeNameEn);
    }

    public String getStream() {
        return "(" + streamCode + ")" + (CommonUtils.isShowFrench() ? streamNameFr : streamNameEn);
    }

    public String getBranchLentic() {
        return "(" + branchLenticCode + ")" + (CommonUtils.isShowFrench() ? branchLenticNameFr : branchLenticNameEn);
    }

    public String getStationFrom() {
        if (stationFromName == null) {
            stationFromName = "";
        }
        return "(" + stationFromCode + ")" + stationFromName;
    }

    public String getStationTo(){
        if (stationToName == null) {
            stationToName = "";
        }
        return "(" + stationToCode + ")" + stationToName;
    }
}
