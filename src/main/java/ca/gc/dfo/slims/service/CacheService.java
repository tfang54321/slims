package ca.gc.dfo.slims.service;

import ca.gc.dfo.spring_commons.commons_offline_wet.services.AbstractCacheService;
import org.springframework.stereotype.Service;

@Service
public class CacheService extends AbstractCacheService {
    public CacheService() {
        super(); //Provide additional paths for the CacheService to ignore if necessary, for now the defaults are fine
    }
}
