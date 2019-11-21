package ca.gc.dfo.slims.dto.parasiticcollection;

import ca.gc.dfo.slims.utility.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ParasiticCollectionDTO {

    public ParasiticCollectionDTO(Long id,
                                  String sampleCode,
                                  String sampleStatus,
                                  Date collectedDate,
                                  String fisherName,
                                  String statisticalDistrict,
                                  String lakeNameEn,
                                  String lakeNameFr) {
        this.id = id;
        this.sampleCode = sampleCode;
        this.sampleStatus = sampleStatus;
        this.collectedDate = collectedDate;
        this.fisherName = fisherName;
        this.statisticalDistrict = statisticalDistrict;
        this.lakeNameEn = lakeNameEn;
        this.lakeNameFr = lakeNameFr;
    }

    private Long                        id;

    private String                      sampleCode;

    private String                      sampleStatus;

    private Date                        collectedDate;

    private String                      fisherName;

    @JsonIgnore
    private String                      statisticalDistrict;
    @JsonIgnore
    private String                      lakeNameEn;
    @JsonIgnore
    private String                      lakeNameFr;

    public String getLakeDistrict(){
        return "(" + statisticalDistrict + ")" + (CommonUtils.isShowFrench() ? lakeNameFr : lakeNameEn);
    }
}
