package cn.deal.component.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
	Properties prop = new Properties();

	public static Properties getProperties() {
		Properties prop = new Properties();
		InputStream in = ConfigUtil.class.getResourceAsStream("/perms/json_path.properties");

		try {
			prop.load(in);
			return prop;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return prop;
	}
}
