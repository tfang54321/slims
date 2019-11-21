package ca.gc.dfo.slims.domain.entity.common;

import ca.gc.dfo.slims.utility.CommonUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name = "slims_ref_code",
    indexes = @Index(columnList = "CODE_TYPE", name="SLIMS_CODE_TYPE_INDEX"),
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "CODE_TYPE", "CODE_VALUE" })
    })
@Data
public class ReferCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refcode_sequence")
    @SequenceGenerator(name = "refcode_sequence", allocationSize = 1, sequenceName = "REF_CODE_SEQ")
    private Long id;

    @Column(name = "CODE_TYPE")
    private String codeType;

    @Column(name = "CODE_ABBREVIATION")
    private String codeAbbreviation;

    @Column(name = "CODE_VALUE")
    private String codeValue;

    @Column(name = "CODE_MEANING_EN")
    private String codeMeaningEn;

    @Column(name = "CODE_MEANING_FR")
    private String codeMeaningFr;
    
    public String getShowText() {
        return "(" + codeValue + ")" + (CommonUtils.isShowFrench() ? codeMeaningFr : codeMeaningEn);
    }

    public ReferCode(){}

    public ReferCode(String codeType, String codeValue, String codeMeaningEn, String codeMeaningFr){
        this.codeType = codeType;
        this.codeValue = codeValue;
        this.codeMeaningEn = codeMeaningEn;
        this.codeMeaningFr = codeMeaningFr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReferCode other = (ReferCode) obj;
        if (id == null) {
            return other.id == null;
        }
        return id.equals(other.id);
    }
}
