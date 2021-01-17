package cn.deal.core.permission.service;


import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import cn.deal.component.exception.BusinessException;
import cn.deal.component.utils.JsonFileUtil;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.ParamValidateUtil;
import cn.deal.core.license.domain.License;
import cn.deal.core.permission.domain.Edition;
import cn.deal.core.permission.domain.Module;
import cn.deal.core.permission.domain.Resource;
import cn.deal.core.permission.domain.Role;

/**
 * 许可版本Version服务
 */
@Service
public class VersionService {

    private static final Logger logger = LoggerFactory.getLogger(VersionService.class);

    private JsonFileUtil jsonFileUtil = new JsonFileUtil();
    
    private List<Role> permissions= new ArrayList<Role>();
    private List<Resource> resources = new ArrayList<>();
    
    private Map<String, List<Module>> editionModulesMap = new HashMap<>();
    private Map<String, Edition> rootEditionsMap = new HashMap<>();
    private List<Edition> visiableEditions = new ArrayList<>();

    @PostConstruct
    public void init() {
    	// 加载角色权限分配数据
        getRolePermissionsFromClasspathFile();

        // 加载许可版本模块配置
        for(String editon: Edition.SUPPORT_EDITIONS) {
        	String resPath = String.format("classpath:perms/AppResource.%s.cfg.json", editon);
        	String resJson = jsonFileUtil.getJsonFromClasspath(resPath);
        	
        	// 加载根模块
        	Edition root = getEditionFromJson(resJson);
        	rootEditionsMap.put(editon, root);
        	
        	// 加载可见版本
            if (Edition.VISIABLE_EDITIONS.contains(editon)) {
            	visiableEditions.add(root);
            }
            
        	// 展开模块
            List<Module> modules = getAllMenuFromVersionConfig(root, new ArrayList<>());
            ParamValidateUtil.validateCollection(modules, "初始化" + editon + "配置文件失败");
            editionModulesMap.put(editon, modules);

            logger.info("加载" + editon + "版本配置文件成功");
        }
        
        ParamValidateUtil.validateMap(editionModulesMap, "初始化版本map出错");
        logger.info("加载所有版本配置文件成功");

        // 加载所有菜单资源
        loadAppResource();
    }

    @SuppressWarnings("unchecked")
	protected void getRolePermissionsFromClasspathFile() {
        Type dataType = new TypeToken<List<Role>>(){}.getType();
        String resPath = "classpath:perms/AppRolePermission.cfg.json";
        String roleJson = jsonFileUtil.getJsonFromClasspath(resPath);
        permissions = (List<Role>) JsonUtil.parseJson(roleJson, dataType);
        ParamValidateUtil.validateCollection(permissions, "初始化角色权限失败");
        logger.info("初始化角色权限成功");
    }

    protected Module getVersionFromJson(String versionStr) {
        Module version = null;
        
        if (StringUtils.isNotBlank(versionStr)) {
            Type dataType = new TypeToken<Module>(){}.getType();
            version = (Module) JsonUtil.parseJson(versionStr, dataType);
        }
        
        return version;
    }

    protected Edition getEditionFromJson(String editionStr) {
        Edition version = null;
        
        if (StringUtils.isNotBlank(editionStr)) {
            Type dataType = new TypeToken<Edition>(){}.getType();
            version = (Edition) JsonUtil.parseJson(editionStr, dataType);
        }
        
        return version;
    }


    protected List<Module> getAllMenuFromVersionConfig(Module version, List<Module> versionList) {
        Assert.notNull(version, "getAllMenuFromVersion->version为空");
        Assert.notNull(versionList, "getAllMenuFromVersion->versionList为空");
        
        addVersionMenuIntoList(version, versionList); // 菜单
        addVersionChildrenIntoList(version, versionList); // 子模块
        addVersionIncludesIntoList(version, versionList); // 附加模块

        return versionList;
    }

    protected void addVersionChildrenIntoList(Module version, List<Module> list) {
        if (!CollectionUtils.isEmpty(version.getChildren())) {
            for (Module childrenVerson : version.getChildren()) {
                if (!versionIdInList(list, childrenVerson)) {
                    list.add(childrenVerson);
                }
                
                if (!CollectionUtils.isEmpty(childrenVerson.getChildren())) {
                    this.getAllMenuFromVersionConfig(childrenVerson, list);
                }
            }
        }
    }

    protected void addVersionMenuIntoList(Module version, List<Module> list) {
        if (!versionIdInList(list, version)) {
            list.add(version);
        }
        
        if (!CollectionUtils.isEmpty(version.getMenus())) {
            for (Resource resource : version.getMenus()) {
                Module v = new Module();
                v.setId(resource.getId());
                v.setName(resource.getName());
                v.setType(resource.getType());
                v.setMenus(resource.getChildren());
                
                getAllMenuFromVersionConfig(v, list);
            }
        }
    }

    protected boolean versionIdInList(List<Module> list, Module version) {
        Assert.notNull(list, "versionIdInList:list不能为空");
        Assert.notNull(version, "versionIdInList:version不能为空");
        
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        
        for (Module temp : list) {
            if (temp.getId().equalsIgnoreCase(version.getId())) {
                return true;
            }
        }
        
        return false;
    }

    protected void addVersionIncludesIntoList(Module version, List<Module> list) {
        if (!CollectionUtils.isEmpty(version.getIncludes())) {
            for (String otherModulePath : version.getIncludes()) {
                String json = jsonFileUtil.getJsonFromClasspathFile(new ClassPathResource(otherModulePath));
                Module tempVersion = (Module) JsonUtil.parseJson(json, Module.class);
                
                if (tempVersion != null) {
                    getAllMenuFromVersionConfig(tempVersion, list);
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void loadAppResource() {
        String res = jsonFileUtil.getJsonFromClasspath("classpath:perms/AppResource.cfg.js");
        
        Type dataType = new TypeToken<Map>() {}.getType();
		Map<String, Map> map = (Map<String, Map>) JsonUtil.parseJson(res, dataType);
		
        for (String key : map.keySet()) {
			List<Resource> list = (List<Resource>) map.get(key);
            resources.addAll(list);
        }
        
        ParamValidateUtil.validateCollection(resources, "初始化AppResource.cfg.js失败");
        logger.info("初始化AppResource.cfg.js成功");
    }

    

	/**
	 * 处理附加模块
	 * 
	 * @param version
	 * @param resources
	 */
	protected void handleOptionals(Edition version, List<Resource> resources) {
        List<Module> optionals = version.getOptionals();

        if (optionals != null && !optionals.isEmpty()) {
            optionals.forEach(optional -> {
            	handeModule(optional, resources);
            });
        }
    }
	
	/**
	 * 处理模块引用
	 * 
	 * @param module
	 * @param resources
	 */
	protected void handeModule(Module module, List<Resource> resources) {
		String type = module.getType();
		
		if (Module.Type.Package.getValue().equals(type)) {
			// 处理子模块
			handleChildren(module, resources);
			
			// 处理模块包含
			handleIncludes(module, resources);
		} else if (Module.Type.Module.getValue().equals(type)) {
			// 处理模块菜单
			handleModuleMenus(module, resources);
			
			// 处理子模块
			handleChildren(module, resources);
		} else if (Module.Type.ModuleRef.getValue().equals(type)) {
			// 处理模块引用
			handleModuleRef(module, resources);
		}
	}

	/**
	 * 处理模块引用
	 * 
	 * @param module
	 * @param resources
	 */
	protected void handleModuleRef(Module module, List<Resource> resources) {
		String moduleId = module.getId();
		List<Module> versions = module.getVersions();
		
		if (versions!=null && versions.size()>0) {
			versions.forEach(v -> {
		    	String version = v.getId();
		    	String innerModuleId = moduleId + ":" + version;
		    	handleModuleId(innerModuleId, resources);
		    });
		} else {
			handleModuleId(moduleId, resources);
		}
	}

	/**
	 * 处理模块ID引用
	 * 
	 * @param moduleId
	 * @param resources
	 */
	protected void handleModuleId(String moduleId, List<Resource> resources) {
		String resPath = "classpath:modules/" + getModulePathById(moduleId);
        String json = jsonFileUtil.getJsonFromClasspath(resPath);
        Module m = getVersionFromJson(json);
        
        if (m!=null) {
        	handeModule(m, resources);
        }
	}
	
	
	
	/**
	 * 处理模块菜单
	 * 
	 * @param module
	 * @param resources
	 */
	protected void handleModuleMenus(Module module, List<Resource> resources) {
    	List<Resource> menus = module.getMenus();

        if (menus!=null && menus.size()>0) {
	        menus.forEach(menu -> {
	        	handleMenu(menu, menu, module.getChildren(), resources);
	        	mergeToTree(resources, menu);
	        });
        }
    }

	/**
	 * 合并到资源树
	 * 
	 * @param menu
	 */
    protected void mergeToTree(List<Resource> resources, Resource menu) {
    	Optional<Resource> finded = resources.stream()
    			.filter(r->r.getId().equals(menu.getId())).findFirst();
    	
    	if (finded.isPresent()) {
    		mergeToMenu(finded.get(), menu);
    	} else {
    		resources.add(menu);
    	}
	}

    /**
     * 合并到菜单
     * 
     * @param mainMenu
     * @param menu
     */
	protected void mergeToMenu(Resource mainMenu, Resource menu) {
		List<Resource> main = mainMenu.getChildren();
		List<Resource> subs = menu.getChildren();
		
		if (subs!=null && subs.size()>0) {
			subs.forEach(m->{
				mergeToTree(main, m);
			});
		}
	}

	/**
     * 处理单个菜单
     * 
     * @param menu
     * @param topMenu
     * @param children
     * @param resources
     */
    protected void handleMenu(Resource menu, Resource topMenu, List<Module> children, List<Resource> resources) {
    	String type = menu.getType();
    	
    	if (Resource.Type.MENU.getVal().equals(type)) {
    		List<Resource> chilren = menu.getChildren();
    		
    		if (chilren != null && !chilren.isEmpty()) {
    			chilren.forEach(mChild -> {
    				handleMenu(mChild, topMenu, children, resources);
                });
            }
    	} else if (Resource.Type.MENU_ITEM.getVal().equals(type)) {
             if (children != null && !children.isEmpty()) {
                 List<Resource> rl = convertToMenuChildren(children);
                 menu.setChildren(rl);
             }
    	}
    }

    /**
     * 转换模块为资源
     * 
     * @param children
     * @return
     */
	protected List<Resource> convertToMenuChildren(List<Module> children) {
		List<Resource> result = Lists.newArrayList();
		
		children.forEach(child -> {
		     handleChildConvert(child, result);
		});
		
		return result;
	}

	/**
	 * 递归转换子模块
	 * 
	 * @param m
	 * @param result
	 */
	protected void handleChildConvert(Module m, List<Resource> result) {
		Resource resource = new Resource();
		resource.setId(m.getId());
		resource.setType(m.getType());
		resource.setName(m.getName());
		result.add(resource);
		
		List<Module> children = m.getChildren();
		if (children!=null && children.size()>0) {
			children.forEach(child -> {
			     handleChildConvert(child, result);
			});
		}
	}
    
    /**
     * 处理模块包含
     * 
     * @param module
     * @param resources
     */
    protected void handleIncludes(Module module, List<Resource> resources) {
        List<String> includes = module.getIncludes();

        if (includes != null && !includes.isEmpty()) {
            includes.forEach(include -> {
                String json = jsonFileUtil.getJsonFromClasspathFile(new ClassPathResource(include));
                Module subModule = (Module) JsonUtil.parseJson(json, Module.class);
                this.handeModule(subModule, resources);
            });
        }
    }

    /**
     * 处理子模块
     * 
     * @param module
     * @param resources
     */
    protected void handleChildren(Module module, List<Resource> resources) {
        List<Module> children = module.getChildren();

		if (children!=null && children.size()>0) {
			children.forEach(m -> {
            	handeModule(m, resources);
            });
		}
    }
    
    /**
	 * 处理附加模块
	 * 
	 * @param license
	 * @param resources
	 */
	protected void handleOptionals(License license, List<Resource> resources) {
        String includeModules = license.getIncludeModules();

        if (StringUtils.isNotBlank(includeModules)) {
            String[] moduleIds = includeModules.split(",");
            
            for (int i = 0; i < moduleIds.length; i++) {
                handleModuleId(moduleIds[i], resources);
            }
        }
    }
	
	/**
	 * 转换许可为可见模块
	 * 
	 * @param license
	 * @return
	 */
	protected List<Module> license2version(License license) {
        String edition = license.getEdition();
        edition = StringUtils.isBlank(edition) ? License.DEFAULT_VERSION : edition;
        
        List<Module> ultimateVersionList = getEditionModules(Edition.ULTIMAT);
        List<Module> versionList = getEditionModules(edition);
        
        String includeModules = license.getIncludeModules();
        if (StringUtils.isNotBlank(includeModules)) {
            for (String moduleId : includeModules.split(",")) {
            	// 根据模块ID，添加附加模块
                for (Module m : ultimateVersionList) {
                    boolean exist = StringUtils.isNotBlank(moduleId) 
                    		&& moduleId.trim().equalsIgnoreCase(m.getId());
                    if (exist) {
                        getAllMenuFromVersionConfig(m, versionList);
                    }
                }
                
                // 根据模块路径，添加附加模块
                addVersionByModuleId(moduleId, versionList);
            }
        }
        
        return versionList;
    }

	/**
	 * 根据模块ID添加模块
	 * 
	 * @param moduleId
	 * @param versions
	 */
	protected void addVersionByModuleId(String moduleId, List<Module> versions) {
        String resPath = "classpath:modules/" + getModulePathById(moduleId);
        String json = jsonFileUtil.getJsonFromClasspath(resPath);
        Module m = getVersionFromJson(json);

        if (m != null) {
            List<Resource> menus = m.getMenus();
            if (menus != null && !menus.isEmpty()) {
                menus.forEach(menu -> {
                    if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                        List<Resource> children = menu.getChildren();
                        
                        children.forEach(child -> {
                            Module mVersion = new Module();
                            BeanUtils.copyProperties(child, mVersion);
                            versions.add(mVersion);
                        });
                    }
                });
            }
            
            if (m.getChildren() != null && !m.getChildren().isEmpty()) {
                versions.addAll(m.getChildren());
            }
        }

        logger.info("addVersionByModuleId.versions: {}", versions);
    }
	
    /**
	 * 根据模块ID获取模块路径
	 * 
	 * @param moduleId
	 * @return
	 */
	public String getModulePathById(String moduleId) {
		String[] split = moduleId.split(":");
        String path;

        // 未分版本
        if (split.length == 2) {
            path = StringUtils.replace(split[0] + "." + split[1], ".", File.separator) + File.separator + "index.cfg.json";
            // 附加木块分了版本
        } else if (split.length == 3) {
            String ver = StringUtils.equals(split[2], "latest") ? "" : split[2] + '.';
            path = StringUtils.replace(split[0] + "." + split[1], ".", File.separator) + File.separator + "index." + ver + "cfg.json";
        } else {
            logger.warn("ignore moduleId: {}", moduleId);
            path = moduleId;
        }
        
		return path;
	}
	
	/**
	 * 根据许可获取项目的模块
	 * 
	 * @param appId
	 * @param license
	 * @return
	 */
	public List<Module> getAppModulesByLicense(String appId, License license) {
		List<Module> modules = null;
        
        if (license != null) {
            modules = license2version(license);
            logger.info("getAppLicenseModules appId:{}, versions: {}", appId, modules);
        } else {
            logger.info("没有获取到项目license, appId: {}", appId);
            modules = new ArrayList<Module>();
        }
        
		return modules;
	}
	
    /**
     * 根据许可版本获取资源树
     * 
     * @return
     */
	public List<Resource> getResourceTreeByLicense(License license) {
        List<Resource> resources = new ArrayList<>();
        
        Edition edition = rootEditionsMap.get(license.getEdition());
        
        if (edition!=null) {
	        handleIncludes(edition, resources);
	        handleOptionals(license, resources);
        } else {
        	throw new BusinessException("not_support_edition", "不支持的许可版本！");
        }
        
        return resources;
    }

	/**
	 * 获取版本模块列表
	 * 
	 * @return
	 */
    public List<Module> getEditionModules(String edition) {
        return editionModulesMap.get(edition);
    }
    
	/**
	 * 获取角色的权限配置
	 * 
	 * @return
	 */
	public List<Role> getRolePermissions() {
        return permissions;
    }
   
    /**
     * 获取所有可见版本
     * 
     * @return
     */
    public List<Edition> getAllVisiableEditions() {
    	return visiableEditions;
    }
}
