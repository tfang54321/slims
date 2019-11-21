package ca.gc.dfo.slims.domain.entity.common;

import ca.gc.dfo.slims.utility.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
public class RefCodePair {

    private String codeName;
    private String valueEn;
    private String valueFr;
    private String abbreviation;

    @Transient
    private String showText;

    public String getShowText() {
        return "(" + codeName + ")" + (CommonUtils.isShowFrench() ? valueFr : valueEn);
    }

    public int getCodeNameInt() {
        return StringUtils.isNumeric(codeName) ? CommonUtils.getIntValue(codeName) : 0;
    }

    public RefCodePair(String codeName, String abbreviation, String valueEn, String valueFr) {
        super();
        this.codeName = codeName;
        this.abbreviation = abbreviation == null ? "" : abbreviation;
        this.valueEn = valueEn == null ? "" : valueEn;
        this.valueFr = valueFr == null ? "" : valueFr;
    }

    @SuppressWarnings("unused")
    public RefCodePair() {
        // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codeName == null) ? 0 : codeName.hashCode());
        result = prime * result + ((valueEn == null) ? 0 : valueEn.hashCode());
        result = prime * result + ((valueFr == null) ? 0 : valueFr.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        RefCodePair other = (RefCodePair) obj;
        if (codeName == null) {
            if (other.codeName != null) {
                return false;
            }
        } else if (!codeName.equals(other.codeName)) {
            return false;
        }
        if (valueEn == null) {
            if (other.valueEn != null) {
                return false;
            }
        } else if (!valueEn.equals(other.valueEn)) {
            return false;
        }
        if (valueFr == null) {
            return other.valueFr == null;
        }
        return valueFr.equals(other.valueFr);
    }

}
