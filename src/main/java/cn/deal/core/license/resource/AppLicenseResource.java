package cn.deal.core.license.resource;

import cn.deal.component.exception.BusinessException;
import cn.deal.core.permission.domain.Edition;
import cn.deal.core.permission.service.VersionService;
import cn.deal.core.license.domain.AppLicense;
import cn.deal.core.license.domain.License;
import cn.deal.core.license.service.AppLicenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 项目名称：deal-core-server2
 * 类名称：AppLicenseResource
 */
@RestController
@Api(value = "项目license", description = "项目license", tags = {"项目license"}, produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class AppLicenseResource {

    private static final Logger log = LoggerFactory.getLogger(AppLicenseResource.class);

    @Autowired
    private AppLicenseService appLicenseService;

    @Autowired
    private VersionService versionService;

    /**
     * @param appId
     * @param isExpired
     * @return List<AppLicense>    返回类型
     * @throws Exception
     * @throws
     * @Title: getLicenses
     * @Description: TODO(获取项目license列表)
     */
    @ApiOperation(value = "获取项目license列表", notes = "获取项目license列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "app_id", value = "项目id"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "is_expired", value = "是否过期"),
    })
    @RequestMapping(value = "/app/{app_id}/licenses", method = RequestMethod.GET)
    public List<AppLicense> getLicenses(@PathVariable("app_id") String appId, @RequestParam(value = "is_expired", required = false) Integer isExpired) throws Exception {
        List<AppLicense> result = appLicenseService.getListAppLicenses(appId, isExpired);
        log.info("getLicenses.result: {}", result);
        return result;
    }

    /**
     * @param appId
     * @return License    返回类型
     * @throws
     * @Title: getLicenses
     * @Description: TODO(获取项目限制)
     */
    @ApiOperation(value = "获取项目限制", notes = "获取项目限制")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
    @RequestMapping(value = "/app/{app_id}/limits", method = RequestMethod.GET)
    public License getLicenses(@PathVariable("app_id") String appId) {
        License license = null;

        try {
            license = appLicenseService.getAppLimits(appId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return license;
    }

    @ApiOperation(value = "获取系统支持的版本", notes = "获取系统支持的版本")
    @GetMapping("/license/editions")
    public List<Edition> editions() {
        return versionService.getAllVisiableEditions();
    }

    /**
     * @param appId
     * @param license
     * @return License    返回类型
     * @throws
     * @Title: saveLicense
     * @Description: TODO(给项目添加license)
     */
    @ApiOperation(value = "给项目添加license", notes = "给项目添加license")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "license", value = "license", required = true),
    })
    @RequestMapping(value = "/app/{app_id}/licenses", method = RequestMethod.POST)
    public AppLicense saveLicense(@PathVariable("app_id") String appId, @RequestParam("license") String license) {
        AppLicense listcense = null;
        if (license != null && !"".equals(license)) {
            listcense = appLicenseService.saveApplicense(appId, license);
        }
        return listcense;
    }

    /**
     * @param appId
     * @param licenseId
     * @return String    返回类型
     * @throws
     * @Title: deleteLicense
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    @ApiOperation(value = "删除license", notes = "删除license")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "license_id", value = "license_id", required = true),
    })
    @RequestMapping(value = "/app/{app_id}/license/{license_id}", method = RequestMethod.DELETE)
    public String deleteLicense(@PathVariable("app_id") String appId, @PathVariable("license_id") String licenseId) {
        return appLicenseService.deleteAppLicense(appId, licenseId);
    }

    @ApiOperation(value = "批量删除license", notes = "批量删除license")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "license_ids", value = "license_ids", required = true),
    })
    @DeleteMapping(value = "/app/{app_id}/licenses")
    public Integer deleteLicenses(@PathVariable("app_id") String appId, @RequestParam("license_ids") String licenseIds) {
        if (StringUtils.isBlank(licenseIds)) {
            throw new BusinessException("Not_Blank", "license_ids不能为空");
        }
        String[] ids = licenseIds.split(",");
        return appLicenseService.deleteAppLicenses(appId, ids);
    }
}
