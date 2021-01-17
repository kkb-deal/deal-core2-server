package cn.deal.core.license.service;

import cn.deal.component.config.ServiceConfig;
import cn.deal.component.utils.Base64Utils;
import cn.deal.component.utils.DateUtils;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.RSAUtils;
import cn.deal.core.license.dao.AppLicenseDao;
import cn.deal.core.license.domain.AppLicense;
import cn.deal.core.license.domain.License;
import cn.deal.core.license.repository.AppLicenseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 项目名称：deal-core-server2
 * 类名称：AppLicenseService
 */
@Service
@CacheConfig(cacheManager = "redis", cacheNames = "license")
public class AppLicenseService {
    private static final Logger log = LoggerFactory.getLogger(AppLicenseService.class);

    @Autowired
    private AppLicenseRepository appLicenseRepository;

    @Autowired
    private AppLicenseDao appLicenseDao;

    @Autowired
    private ServiceConfig serviceConfig;

    private static final String LICENSE_VERSION_TWO = "2";

    /**
     * @param appId
     * @param isExpired
     * @return List<AppLicense>    返回类型
     * @throws Exception
     * @throws Exception
     * @throws Exception
     * @throws
     * @Title: getListAppLicenses
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public List<AppLicense> getListAppLicenses(String appId, Integer isExpired) throws Exception {
        List<AppLicense> list = appLicenseDao.getAppLicenseList(appId);
        log.info("getListAppLicenses.list: {}", list);

        for (AppLicense license : list) {
            if (StringUtils.isBlank(license.getEdition())) {
                license.setEdition(License.DEFAULT_VERSION);
            }
        }

        return getIsExpiredList(list, isExpired);
    }


    /**
     * @param list
     * @param isExpired 0 不过期 1 是已经过期
     * @return List<AppLicense>    返回类型
     * @throws
     * @Title: getIsExpiredList
     * @Description: TODO(根据参数获取过期或者不过期的列表)
     */
    public List<AppLicense> getIsExpiredList(List<AppLicense> list, Integer isExpired) throws Exception {
        List<AppLicense> listIsExpired = new ArrayList<>();

        if (list != null && list.size() > 0) {
            for (AppLicense appLicense : list) {
                Date now = appLicense.getCreatedAt();
                appLicense = getAppLicense(appLicense);
                Date d = calcExpiresTime(appLicense);
                int expired = DateUtils.getExpired(now, d) ? 1 : 0;

                if (isExpired == null || (isExpired != null && isExpired == expired)) {// 未过期
                    listIsExpired.add(appLicense);
                }
            }
        }

        return listIsExpired;
    }


    public AppLicense getAppLicense(AppLicense appLicense) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = calcExpiresTime(appLicense);
        if (date != null) {
            appLicense.setExpiresTime(sdf.format(date));
        }
        return appLicense;
    }


    /**
     * @param appLicense
     * @return AppLicense    返回类型
     * @throws Exception
     * @throws
     * @Title: getAppLicense
     * @Description: TODO(计算过期时间 ， 接口出去重新计算)
     */
    public License getLicense(AppLicense appLicense) throws Exception {
        Date d = calcExpiresTime(appLicense);
        boolean expired = DateUtils.getExpired(new Date(), d);
        log.info("getLicense expired: {}, expiredAt:{}", expired, d);

        License licen = new License(appLicense, d, expired);
        log.info("getLicense licen: {}", licen);

        return licen;
    }

    protected Date calcExpiresTime(AppLicense appLicense) {

        String expiresTime = appLicense.getExpiresTime().trim();
        log.info("License getLicense expiresTime:" + expiresTime);
        if (expiresTime.endsWith("Y")) {
            return new DateTime(appLicense.getCreatedAt()).plusYears(Integer.parseInt(expiresTime.substring(0, expiresTime.length() - 1))).toDate();
        }

        if (expiresTime.endsWith("M")) {
            return new DateTime(appLicense.getCreatedAt()).plusMonths(Integer.parseInt(expiresTime.substring(0, expiresTime.length() - 1))).toDate();
        }

        if (expiresTime.endsWith("D")) {
            return new DateTime(appLicense.getCreatedAt()).plusDays(Integer.parseInt(expiresTime.substring(0, expiresTime.length() - 1))).toDate();
        }

        if (StringUtils.equals(expiresTime, "INF")) {
            return new DateTime(appLicense.getCreatedAt()).plusYears(100).toDate();
        }

        expiresTime = expiresTime.replaceAll("[+ ]", "T");
        return DateTime.parse(expiresTime).toDate();
    }

    /**
     * @param appId
     * @return License    返回类型
     * @throws Exception
     * @throws
     * @Title: getAppLimits
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
//    @Cacheable(key = "'app-limit:' + #p0")
    public License getAppLimits(String appId) {
        log.info("getAppLimits.appId: {}", appId);
        List<AppLicense> list = appLicenseDao.getAppLicenseList(appId);
        License licen = new License();

        try {
            AppLicense appLicense = findFirstValid(list);

            if (appLicense != null) {
                licen = getLicense(appLicense);
                licen.setCurrentTime(new Date());
            }

            if (licen.getEdition() == null) {
                licen.setEdition(License.DEFAULT_VERSION);
                licen.setIsTrial(Boolean.FALSE);
            }
        } catch(Exception e) {
            log.info("error in getAppLimits: ", e);
        }

        return licen;
    }

    /**
     * 获取第一个有效license
     *
     * @param list
     * @return
     */
    private AppLicense findFirstValid(List<AppLicense> list) throws JsonProcessingException {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        AppLicense appLicense = list.stream().filter(appLicense1 -> {
            Date d = calcExpiresTime(appLicense1);
            boolean expired = DateUtils.getExpired(new Date(), d);
            return !expired;
        }).findFirst().orElse(null);
        if (appLicense == null) {
            return null;
        }
        return checkSign(appLicense) ? appLicense : null;
    }

    /**
     * 校验签名是否正确
     *
     * @param appLicense
     * @return
     */
    private boolean checkSign(AppLicense appLicense) throws JsonProcessingException {
        String publicKey = serviceConfig.getKdRsaPub();

        String sign = appLicense.getSign();
        String expiresTime = appLicense.getExpiresTime();
        String type = appLicense.getType();
        if (type.equals("single")) {
            type = appLicense.getAppId();
        }
        String data = null;

        if (LICENSE_VERSION_TWO.equals(String.valueOf(appLicense.getVersion()))) {
            Map<String, Object> dataMap = Maps.newTreeMap();
            dataMap.put("appId", type);
            dataMap.put("edition", appLicense.getEdition());
            dataMap.put("expiredTime", appLicense.getExpiresTime());

            if (appLicense.getIncludeModules() != null) {
                dataMap.put("includeModules", appLicense.getIncludeModules());
            }

            if (appLicense.getIsTrial() != null) {
                dataMap.put("isTrial", appLicense.getIsTrial() + "");
            }

            dataMap.put("maxAppCount", appLicense.getMaxAppCount());
            dataMap.put("maxAppMemberCount", appLicense.getMaxAppMemberCount());
            dataMap.put("maxBIUserCount", appLicense.getMaxBIUserCount());
            dataMap.put("maxCallerCount", appLicense.getMaxCallerCount());
            dataMap.put("maxDemoMemberCount", appLicense.getMaxDemoMemberCount());
            dataMap.put("maxFileSenderCount", appLicense.getMaxFileSenderCount());
            dataMap.put("maxMailSenderCount", appLicense.getMaxMailSenderCount());
            dataMap.put("maxOfficialAccountsCount", appLicense.getMaxOfficialAccountsCount());
            dataMap.put("maxWebSiteCount", appLicense.getMaxWebSiteCount());

            if (appLicense.getMaxWeixinAppCount() != null) {
                dataMap.put("maxWeixinAppCount", appLicense.getMaxWeixinAppCount());
            }

            if (appLicense.getMaxYouzanShopCount() != null) {
                dataMap.put("maxYouzanShopCount", appLicense.getMaxYouzanShopCount());
            }

            if (appLicense.getMaxLiukeWeixinCount() != null) {
                dataMap.put("maxLiukeWeixinCount", appLicense.getMaxLiukeWeixinCount());
            }

            if (appLicense.getMaxLiukeWeixinMemberCount() != null) {
                dataMap.put("maxLiukeWeixinMemberCount", appLicense.getMaxLiukeWeixinMemberCount());
            }

            data = appLicense.getVersion() + "," + JsonUtil.toJson(dataMap);
        } else {
            data = appLicense.getVersion() + "," + type + "," + expiresTime + ","
                    + appLicense.getMaxAppMemberCount() + "," + appLicense.getMaxOfficialAccountsCount() + ","
                    + appLicense.getMaxWebSiteCount() + "," + appLicense.getMaxAppCount() + ","
                    + appLicense.getMaxCallerCount() + "," + appLicense.getMaxMailSenderCount() + ","
                    + appLicense.getMaxFileSenderCount() + "," + appLicense.getMaxDemoMemberCount() + ","
                    + appLicense.getMaxBIUserCount();


            if (appLicense.getMaxWeixinAppCount() != null) {
                data += "," + appLicense.getMaxWeixinAppCount();
            }

            if (appLicense.getIncludeModules() != null) {
                data += "," + appLicense.getIncludeModules();
            }

            if (appLicense.getIsTrial() != null) {
                data += "," + appLicense.getIsTrial();
            }
        }

        log.info("license verify data: {},  sign: {}, publicKey: {}", data, sign, publicKey);

        boolean verfy = RSAUtils.verifySHA256withRSASigature(publicKey, sign, data);
        log.info("license verify result:" + verfy);
        return verfy;
    }

    @CacheEvict(key = "'app-limit:' + #p0")
    public AppLicense saveApplicense(String appId, String license) {
        log.info("saveApplicense.appId: {}", appId);
        if (license != null) {
            String str = Base64Utils.decodeStr(license);
            log.info("decode license:" + str);

            String[] licens = str.split(",");
            String version = licens[1];
            String sign = str.substring(0, str.indexOf(","));
            log.info("license version:{}, sign: {}", version, sign);

            String data = null;
            data = (str.substring(str.indexOf(",") + 1));
            log.info("license data:" + data);

            String publicKey = serviceConfig.getKdRsaPub();
            log.info("saveApplicense publicKey：" + publicKey);
            boolean verfy = RSAUtils.verifySHA256withRSASigature(publicKey, sign, data);
            log.info("saveApplicense verify result:" + verfy);

            if (verfy) {
                if (LICENSE_VERSION_TWO.equals(version)) {
                    data = str.substring(str.indexOf("{"));
                }

                try {
                    return doSave(appId, licens, sign, data, publicKey, version);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                log.warn("sign verify failed, with appId:{}, license:{}", appId, license);
            }
        }

        return null;
    }


    @SuppressWarnings("unchecked")
    private AppLicense doSave(String appId, String[] licens, String sign, String data, String publicKey, String version)
            throws IOException {

        //这里是为了兼容license版本1
        if (LICENSE_VERSION_TWO.equals(version)) {
            Map<String, Object> licenseParamMap = JsonUtil.fromJson(data, Map.class);
            String appIdParam = (String) licenseParamMap.get("appId");
            if (appIdParam != null && (appIdParam.equals(appId) || appIdParam.equals("all"))) {
                AppLicense appLicense = new AppLicense();
                appLicense.setAppId(appId);
                appLicense.setSign(licens[0]);
                appLicense.setVersion(Integer.valueOf(licens[1]));

                if (appIdParam.equals("all")) {
                    appLicense.setType("all");
                } else {
                    appLicense.setType("single");
                }

                appLicense.setExpiresTime((String) licenseParamMap.get("expiredTime"));
                appLicense.setEdition((String) licenseParamMap.get("edition"));
                appLicense.setMaxAppMemberCount((int) licenseParamMap.get("maxAppMemberCount"));
                appLicense.setMaxOfficialAccountsCount((int) licenseParamMap.get("maxOfficialAccountsCount"));
                appLicense.setMaxWebSiteCount((int) licenseParamMap.get("maxWebSiteCount"));
                appLicense.setMaxAppCount((int) licenseParamMap.get("maxAppCount"));
                appLicense.setMaxCallerCount((int) licenseParamMap.get("maxCallerCount"));
                appLicense.setMaxMailSenderCount((int) licenseParamMap.get("maxMailSenderCount"));
                appLicense.setMaxFileSenderCount((int) licenseParamMap.get("maxFileSenderCount"));
                appLicense.setMaxDemoMemberCount((int) licenseParamMap.get("maxDemoMemberCount"));
                appLicense.setMaxBIUserCount((int) licenseParamMap.get("maxBIUserCount"));

                if (licenseParamMap.get("maxWeixinAppCount") != null) {
                    appLicense.setMaxWeixinAppCount((int) licenseParamMap.get("maxWeixinAppCount"));
                }

                if (licenseParamMap.get("maxYouzanShopCount") != null) {
                    appLicense.setMaxYouzanShopCount((int) licenseParamMap.get("maxYouzanShopCount"));
                }

                if (licenseParamMap.get("maxLiukeWeixinCount") != null) {
                    appLicense.setMaxLiukeWeixinCount((int) licenseParamMap.get("maxLiukeWeixinCount"));
                }

                if (licenseParamMap.get("maxLiukeWeixinMemberCount") != null) {
                    appLicense.setMaxLiukeWeixinMemberCount((int) licenseParamMap.get("maxLiukeWeixinMemberCount"));
                }

                if (licenseParamMap.get("includeModules") != null) {
                    appLicense.setIncludeModules((String) licenseParamMap.get("includeModules"));
                }
                if (licenseParamMap.get("isTrial") != null) {
                    appLicense.setIsTrial(new Boolean((String) licenseParamMap.get("isTrial")));
                }

                appLicense.setCreatedAt(new Date());
                appLicenseDao.insert(appLicense);
                return appLicense;
            }
        } else {
            String appID = licens[2];
            if (appID != null && (appID.equals(appId) || appID.equals("all"))) {
                AppLicense appLicense = new AppLicense();
                appLicense.setAppId(appId);
                appLicense.setSign(licens[0]);
                appLicense.setVersion(Integer.valueOf(licens[1]));

                if (appID.equals("all")) {
                    appLicense.setType("all");
                } else {
                    appLicense.setType("single");
                }

                if (licens[3] != null) {
                    appLicense.setExpiresTime(licens[3]);
                }

                appLicense.setMaxAppMemberCount(Integer.valueOf(licens[4]));
                appLicense.setMaxOfficialAccountsCount(Integer.valueOf(licens[5]));
                appLicense.setMaxWebSiteCount(Integer.valueOf(licens[6]));
                appLicense.setMaxAppCount(Integer.valueOf(licens[7]));
                appLicense.setMaxCallerCount(Integer.valueOf(licens[8]));
                appLicense.setMaxMailSenderCount(Integer.valueOf(licens[9]));
                appLicense.setMaxFileSenderCount(Integer.valueOf(licens[10]));
                appLicense.setMaxDemoMemberCount(Integer.valueOf(licens[11]));
                appLicense.setMaxBIUserCount(Integer.valueOf(licens[12]));
                appLicense.setCreatedAt(new Date());
                appLicenseDao.insert(appLicense);
                return appLicense;

            }
        }

        return null;
    }

    /**
     * @param appId
     * @param appLicenseId
     * @return String    返回类型
     * @throws
     * @Title: deleteAppLicense
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    @CacheEvict(key = "'app-limit:' + #p0")
    public String deleteAppLicense(String appId, String appLicenseId) {
        log.info("deleteAppLicense.appId: {}", appId);

        String isD = "{\"status\": 1, \"msg\": \"删除成功\"}";

        try {
            int count = appLicenseRepository.countById(appLicenseId);
            if (count > 0)
                appLicenseDao.deleteById(appLicenseId);
            else
                isD = "{\"status\":0,\"msg\":\"删除失败\"}";
        } catch (Exception e) {
            isD = "{\"status\":0,\"msg\":\"删除失败\"}";
        }

        log.info("-deleteAppLicense---" + isD);

        return isD;
    }

    /**
     * 批量删除
     *
     * @param appId
     * @param ids
     */
    @CacheEvict(key = "'app-limit:' + #appId")
    @Transactional(rollbackFor = Exception.class)
    public int deleteAppLicenses(String appId, String[] ids) {
        return appLicenseRepository.deleteBatchByIds(appId, Lists.newArrayList(ids));
    }
}
