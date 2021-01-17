package cn.deal.component.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class TokenUtil {
	
	private static final String HTTP_HEADER_VAL_PREFIX_ACCESS_TOKEN = "Bearer ";
    private static final int HTTP_HEADER_VAL_PREFIX_ACCESS_TOKEN_LENGTH = HTTP_HEADER_VAL_PREFIX_ACCESS_TOKEN.length();

	protected static String getAccessTokenByAuthorization(String authorization) throws Exception {
		if (StringUtils.isNotBlank(authorization) && authorization.startsWith(HTTP_HEADER_VAL_PREFIX_ACCESS_TOKEN)) {
			return authorization.substring(HTTP_HEADER_VAL_PREFIX_ACCESS_TOKEN_LENGTH, authorization.length());
		} else {
			throw new Exception("Authorized failed.");
		}
	}
	/**
	 * 获取token
	 * @param request
	 * @return
	 */
	public static String getToken(HttpServletRequest request) {

		String accessToken = request.getParameter("access_token");
		if (StringUtils.isNotBlank(accessToken)) {
			return accessToken;
		}

		String authorization = request.getHeader("authorization");
		if (StringUtils.isNotBlank(authorization)) {
			String[] pieces = authorization.split(" ");
			if (pieces == null || pieces.length != 2) {
				return null;
			}
			
			if (!"bearer".equals(pieces[0].toLowerCase())) {
				return null;
			}
			
			return pieces[1];
		}
		
		return null;
	}
}
