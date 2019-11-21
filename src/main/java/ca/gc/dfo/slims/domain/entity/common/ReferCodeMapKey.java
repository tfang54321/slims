package ca.gc.dfo.slims.domain.entity.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReferCodeMapKey {

    public ReferCodeMapKey(){

    }

    public ReferCodeMapKey(String codeType, String codeValue){
        this.codeType = codeType;
        this.codeValue = codeValue;
    }

    private String codeType;

    private String codeValue;
}
