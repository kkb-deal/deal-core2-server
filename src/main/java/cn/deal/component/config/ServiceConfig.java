package cn.deal.component.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.deal.component.utils.PathUtil;

/**
 * 该服务 依赖的 其它服务 的配置项
 */
@Component
public class ServiceConfig {

	private static PathUtil pathUtil = new PathUtil();
	
	@Value("${oauth2.accessTokenURL}")
	private String accessTokenURL;

	@Value("${oauth2.clientId}")
	private String clientId;

	@Value("${oauth2.clientSecret}")
	private String clientSecret;

	@Value("${oauth2.grantType.clientCredentials}")
	private String grantTypeClientCredentials;

	@Value("${kuick.api.baseurl}")
    private String kuickApiBaseUrl;
	
//	@Value("${kd.rsa.pub}")
	private String kdRsaPub;
	
//	@Value("${kd.rsa.license10}")
	private String kdRsaLicense;
	
	@Value("${kuickuser.api.baseurl}")
	private String kuickUserApiBaseUrl;
	
//	@Value("${kd.rsa.license20}")
    private String kdRsaLicense20;
	
//	@Value("${kd.rsa.license30}")
    private String kdRsaLicense30;

	@Value("${core.server.inner.baseurl}")
	private String coreApiBaseUrl;

	public String getCoreApiBaseUrl() {
		return coreApiBaseUrl;
	}

	public String getKdRsaPub() {
		return kdRsaPub;
	}

	public String getKuickApiBaseUrl() {
		return pathUtil.urlAdapter(kuickApiBaseUrl);
	}

	public String getKdRsaLicense() {
		return kdRsaLicense;
	}

	public String getKdRsaLicense20() {
        return kdRsaLicense20;
    }

    public String getKdRsaLicense30() {
        return kdRsaLicense30;
    }

	public String getKuickUserApiBaseUrl() {
		return pathUtil.urlAdapter(kuickUserApiBaseUrl);
	}

	public String getAccessTokenURL() {
		return accessTokenURL;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getGrantTypeClientCredentials() {
		return grantTypeClientCredentials;
	}
}
