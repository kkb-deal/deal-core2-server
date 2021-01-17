package cn.deal.core.meta.resource;

import cn.deal.component.exception.BusinessException;
import cn.deal.core.meta.domain.AppDomain;
import cn.deal.core.meta.service.AppDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0")
public class AppDomainResource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppDomainService appDomainService;

    @Value("${deal.domain}")
    private String KUICK_DOMAIN;

    private static final String DOMAIN_REGEX = "[.]";

    private static final String DOMAIN_SUB_FIX = ".";


    @GetMapping("/app/{app_id}/domain")
    public AppDomain getAppDomain(@PathVariable("app_id") String appId) throws Exception {
        AppDomain domain = appDomainService.getAppDomainByAppId(appId);
        logger.info("get app domain by appId: {}, domain: {}", appId, domain);
        return domain;
    }

    @PutMapping("/app/{app_id}/domain")
    public AppDomain putAppDomain(@PathVariable("app_id") String appId,
                                  @RequestParam(value = "domain", required = true) String domain) throws Exception {
        String starts = KUICK_DOMAIN.split(DOMAIN_REGEX)[0] + DOMAIN_SUB_FIX;
        if (!domain.startsWith(starts)) {
            throw new BusinessException("invalid_domain", "必须以'" + starts + "'开头");
        }
        AppDomain appDomain = appDomainService.addAppDomainByAppId(appId, domain);
        logger.info("put app domain by appId: {}, domain: {}", appId, appDomain);
        return appDomain;
    }

    @PostMapping("/app/{app_id}/domain/checks")
    public AppDomain check(@PathVariable("app_id") String appId) {
        AppDomain appDomain = appDomainService.checkAppDomainByAppId(appId);
        logger.info("check app domain by appId: {}, domain: {}", appId, appDomain);
        return appDomain;
    }

    @PutMapping("/app/{app_id}/domain/status")
    public AppDomain changeStatus(@PathVariable("app_id") String appId,
                                  @RequestParam(value = "status", required = true) int status) {
        AppDomain appDomain = appDomainService.changeStatusByAppId(appId, status);
        logger.info("change app domain status by appId: {}, domain: {}", appId, appDomain);
        return appDomain;
    }
}
