package ca.gc.dfo.slims.dto.common.location;

import ca.gc.dfo.slims.domain.entity.common.location.BranchLentic;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.entity.common.location.Station;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
public class StationDTO {

    private Lake parentLake;
    private Stream parentStream;
    private BranchLentic parentBranchLentic;
    private Station station;

    /**
     * Constructor
     * @param parentLake Lake of the parent
     * @param parentStream Stream of the parent
     * @param parentBranchLentic BranchLentic of the parent
     * @param station Station object
     */
    public StationDTO(Lake parentLake, Stream parentStream, BranchLentic parentBranchLentic, Station station) {
        super();
        this.parentLake = parentLake;
        this.parentStream = parentStream;
        this.parentBranchLentic = parentBranchLentic;
        this.station = station;
    }

}
