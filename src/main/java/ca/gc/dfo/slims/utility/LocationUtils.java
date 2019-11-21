package ca.gc.dfo.slims.utility;

import ca.gc.dfo.slims.domain.entity.common.location.LocationReference;
import ca.gc.dfo.slims.service.location.BranchLenticService;
import ca.gc.dfo.slims.service.location.LakeService;
import ca.gc.dfo.slims.service.location.StationService;
import ca.gc.dfo.slims.service.location.StreamService;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class LocationUtils {
    private LocationUtils() {
        throw new UnsupportedOperationException("Should not call this constructor");
    }

    public static LocationReference getLocationReferenceFromFormData(Map<String, String> formData,
                                                                     LakeService lakeService,
                                                                     StreamService streamService,
                                                                     BranchLenticService branchLenticService,
                                                                     StationService stationService) {
        LocationReference locationReference = new LocationReference();

        if (!StringUtils.isBlank(formData.get("lakeId"))) {
            Long lakeId = CommonUtils.getLongValue(formData.get("lakeId"));
            locationReference.setLake(lakeService.getById(lakeId));
        }
        if (!StringUtils.isBlank(formData.get("streamId"))) {
            Long streamId = CommonUtils.getLongValue(formData.get("streamId"));
            locationReference.setStream(streamService.getById(streamId));
        }
        if (!StringUtils.isBlank(formData.get("branchId"))) {
            Long branchId = CommonUtils.getLongValue(formData.get("branchId"));
            locationReference.setBranchLentic(branchLenticService.getById(branchId));
        }
        if (!StringUtils.isBlank(formData.get("stationFromId"))) {
            Long stationFromId = CommonUtils.getLongValue(formData.get("stationFromId"));
            locationReference.setStationFrom(stationService.getById(stationFromId));
        }
        if (!StringUtils.isBlank(formData.get("stationToId"))) {
            Long stationToId = CommonUtils.getLongValue(formData.get("stationToId"));
            locationReference.setStationTo(stationService.getById(stationToId));
        }

        locationReference.setStationFromAdjust(CommonUtils.getStringValue(formData.get("stationFromAdjust")));
        locationReference.setStationToAdjust(CommonUtils.getStringValue(formData.get("stationToAdjust")));
        return locationReference;
    }
}
