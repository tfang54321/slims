package ca.gc.dfo.slims.service.loaders;

import ca.gc.dfo.slims.domain.entity.common.LakeDistrict;
import ca.gc.dfo.slims.domain.entity.common.LampricideProducts;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.RefCodePair;
import ca.gc.dfo.slims.domain.entity.common.RefCodes;
import ca.gc.dfo.slims.domain.entity.common.ReferCode;
import ca.gc.dfo.slims.domain.entity.common.SpecieCode;
import ca.gc.dfo.slims.domain.repository.common.LakeDistricsRepository;
import ca.gc.dfo.slims.domain.repository.common.LampricideProductsRepository;
import ca.gc.dfo.slims.domain.repository.common.ReferCodeRepository;
import ca.gc.dfo.slims.domain.repository.common.SpecieCodeRepository;
import ca.gc.dfo.slims.utility.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZHUY
 *
 */
@Component
public class ResourceLoadService implements InitializingBean {
    private static final String LOADER_NAME = "ResourceLoadService";

    @Autowired
    private ReferCodeRepository                     referCodeRepository;
    @Autowired
    private SpecieCodeRepository                    specieCodeRepository;
    @Autowired
    private LampricideProductsRepository            lpRepository;
    @Autowired
    private LakeDistricsRepository                  ldRepository;

    private Map<String, RefCodes>                   refcodesMap;
    private List<SpecieCode>                        specieCodes;
    private Map<String, List<LampricideProducts>>   lpsMap;
    private List<LakeDistrict>                      ldsList;

    public ResourceLoadService() {
        this.refcodesMap = new HashMap<>();
        this.specieCodes = new ArrayList<>();
        this.lpsMap = new HashMap<>();
        this.ldsList = new ArrayList<>();
    }

    @Override
    public void afterPropertiesSet() {
        CommonUtils.getLogger().debug("{}:afterPropertiesSet", LOADER_NAME);
        populateRefCodes();
        populateSpecieCodes();
        populateLPs();
        populateLds();
    }

    private void populateRefCodes() {
        CommonUtils.getLogger().debug("{}:populateRefCodes", LOADER_NAME);
        List<ReferCode> allRefCodes = referCodeRepository.findAll();
        loadRefCodesMap(allRefCodes);
    }

    public void reloadRefCodes(String codeType) {
        CommonUtils.getLogger().debug("{}:reloadRefCodes", LOADER_NAME);
        List<ReferCode> allRefCodes = referCodeRepository.findAllByCodeType(codeType);
        refcodesMap.remove(codeType);
        loadRefCodesMap(allRefCodes);
    }

    private void loadRefCodesMap(List<ReferCode> refCodesList) {
        CommonUtils.getLogger().debug("{}:loadRefCodesMap", LOADER_NAME);
        for (ReferCode refCode : refCodesList) {
            if (refcodesMap.containsKey(refCode.getCodeType())) {
                List<RefCodePair> refCodePairList = refcodesMap.get(refCode.getCodeType()).getCodeValues();
                refCodePairList.add(new RefCodePair(
                    refCode.getCodeValue(), refCode.getCodeAbbreviation(),
                    refCode.getCodeMeaningEn(), refCode.getCodeMeaningFr()));
                refCodePairList.sort(Comparator
                    .comparingInt(RefCodePair::getCodeNameInt)
                    .thenComparing(RefCodePair::getCodeName));
                refcodesMap.get(refCode.getCodeType()).setCodeValues(refCodePairList);
            } else {
                List<RefCodePair> refCodePairList = new ArrayList<>();
                refCodePairList.add(new RefCodePair(
                    refCode.getCodeValue(), refCode.getCodeAbbreviation(),
                    refCode.getCodeMeaningEn(), refCode.getCodeMeaningFr()));

                RefCodes refCodes = new RefCodes(refCode.getCodeType(), refCodePairList);
                refcodesMap.put(refCode.getCodeType(), refCodes);
            }
        }

        //Code to ensure that N/A is always the first value in the list for TRAP_CHAMBER
        if(refcodesMap.get("TRAP_CHAMBER") != null){
            List<RefCodePair> codePairList = refcodesMap.get("TRAP_CHAMBER").getCodeValues();

            for(int i = 0; i < codePairList.size(); i++){
                if(codePairList.get(i).getCodeName().equals("N")){
                    codePairList.add(0, codePairList.remove(i));
                }
            }
        }
    }

    private void populateSpecieCodes() {
        CommonUtils.getLogger().debug("{}:populateSpecieCodes", LOADER_NAME);
        specieCodes = specieCodeRepository.findAll();
        if (CollectionUtils.isEmpty(this.specieCodes)) {
            CommonUtils.getLogger().info("{}:populateSpecieCodes there is no SpecieCode data exist", LOADER_NAME);
            this.specieCodes = new ArrayList<>(1);
        }
    }

    private void populateLds() {
        CommonUtils.getLogger().debug("{}:populateLds", LOADER_NAME);
        ldsList = ldRepository.findAll();
        if (CollectionUtils.isEmpty(this.ldsList)) {
            CommonUtils.getLogger().info("{}:populateLds there is no lake district data exist", LOADER_NAME);
            this.ldsList = new ArrayList<>(1);
        }
    }

    private void populateLPs() {
        CommonUtils.getLogger().debug("{}:populateLPs", LOADER_NAME);
        List<String> allLPTypes = lpRepository.findDistinctLampricideProductType();
        for (String lpType : allLPTypes) {
            lpsMap.put(lpType, lpRepository.findAllByLampricideProductType(lpType));
        }
    }

    public RefCode findRefCode(String codeType, String codeName) {
        CommonUtils.getLogger().debug(
            "{}:findRefCode with codeType({}) and codeName({})", LOADER_NAME, codeType, codeName);
        if (!StringUtils.isBlank(codeName)) {
            RefCodes refCodes = refcodesMap.get(codeType);
            for (RefCodePair aPair : refCodes.getCodeValues()) {
                if (aPair.getCodeName().equals(codeName)) {
                    CommonUtils.getLogger().debug(
                        "{}:findRefCode found RefCodePair for codeName({}) with codeType({})",
                        LOADER_NAME, codeName, codeType);
                    return new RefCode(codeType, aPair);
                }
            }
        }
        CommonUtils.getLogger().debug("{}:findRefCode did not find", LOADER_NAME);
        return null;
    }

    /**
     * @return the refcodesMap
     */
    public Map<String, RefCodes> getRefcodesMap() {
        return refcodesMap;
    }

    /**
     * @param refcodesMap
     *            the refcodesMap to set
     */
    @SuppressWarnings("unused")
    public void setRefcodesMap(Map<String, RefCodes> refcodesMap) {
        this.refcodesMap = refcodesMap;
    }

    /**
     * @return the specieCodes
     */
    public List<SpecieCode> getSpecieCodes() {
        return specieCodes;
    }

    /**
     * @param specieCodes
     *            the specieCodes to set
     */
    @SuppressWarnings("unused")
    public void setSpecieCodes(List<SpecieCode> specieCodes) {
        this.specieCodes = specieCodes;
    }

    /**
     * @return the lpsMap
     */
    public Map<String, List<LampricideProducts>> getLpsMap() {
        return lpsMap;
    }

    /**
     * @param lpsMap
     *            the lpsMap to set
     */
    @SuppressWarnings("unused")
    public void setLpsMap(Map<String, List<LampricideProducts>> lpsMap) {
        this.lpsMap = lpsMap;
    }

    /**
     * @return the ldsList
     */
    public List<LakeDistrict> getLdsList() {
        return this.ldsList;
    }

    /**
     * @param ldsList
     *            the ldsList to set
     */
    @SuppressWarnings("unused")
    public void setLdsList(List<LakeDistrict> ldsList) {
        this.ldsList = ldsList;
    }
}