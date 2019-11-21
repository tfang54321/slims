package ca.gc.dfo.slims.utility;

import ca.gc.dfo.slims.domain.entity.common.location.GeoUTM;
import ca.gc.dfo.slims.service.loaders.ResourceLoadService;

import java.util.Map;

public class GeoUtmUtils {
    private GeoUtmUtils() {
        throw new UnsupportedOperationException("Should not call this constructor");
    }

    public static GeoUTM getGeoUtmFromFormData(Map<String, String> formData,
                                               String specialLocation,
                                               ResourceLoadService resourceLoadService) {
        GeoUTM geoUtm = getGeoUtmWithTwoUtmFromFormData(formData, specialLocation, resourceLoadService);

        geoUtm.setUtmEasting03(CommonUtils.getDoubleValue(formData.get("utm_e03")));
        geoUtm.setUtmNorthing03(CommonUtils.getDoubleValue(formData.get("utm_n03")));
        geoUtm.setUtmEasting04(CommonUtils.getDoubleValue(formData.get("utm_e04")));
        geoUtm.setUtmNorthing04(CommonUtils.getDoubleValue(formData.get("utm_n04")));

        return geoUtm;
    }

    public static GeoUTM getGeoUtmWithTwoUtmFromFormData(Map<String, String> formData,
                                                         String specialLocation,
                                                         ResourceLoadService resourceLoadService) {
        GeoUTM geoUtm = new GeoUTM();

        geoUtm.setMapDatum(resourceLoadService.findRefCode("MAP_DATUM",
            CommonUtils.getStringValue(formData.get("MAP_DATUM"))));
        geoUtm.setUtmZone(resourceLoadService.findRefCode("UTM_ZONE",
            CommonUtils.getStringValue(formData.get("UTM_ZONE"))));

        geoUtm.setUtmEasting01(CommonUtils.getDoubleValue(formData.get("utm_e01")));
        geoUtm.setUtmNorthing01(CommonUtils.getDoubleValue(formData.get("utm_n01")));
        geoUtm.setUtmEasting02(CommonUtils.getDoubleValue(formData.get("utm_e02")));
        geoUtm.setUtmNorthing02(CommonUtils.getDoubleValue(formData.get("utm_n02")));

        geoUtm.setLocation(CommonUtils.getStringValue(formData.get(specialLocation)));
        return geoUtm;
    }
}
