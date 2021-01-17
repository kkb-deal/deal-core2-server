package cn.deal.component.utils;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.apache.log4j.Logger;

public class RSAUtils {

	private static final Logger logger = Logger.getLogger(RSAUtils.class);

	public static boolean verifySHA256withRSASigature(String pubKey, String sign, String content) {
		try {
			Signature sigEng = Signature.getInstance("SHA256withRSA");
			byte[] pubbyte = Base64Utils.decode(pubKey);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubbyte);
			KeyFactory fac = KeyFactory.getInstance("RSA");
			RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);
			sigEng.initVerify(rsaPubKey);
			sigEng.update(content.getBytes());
			byte[] sign1 = hexToBytes(sign);
			return sigEng.verify(sign1);
		} catch (Exception e) {
			logger.error("error in verify RAS sigature!", e);
			return false;
		}
	}

	public static byte[] hexToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}

		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] bytes = new byte[length];
		String hexDigits = "0123456789abcdef";
		for (int i = 0; i < length; i++) {
			int pos = i * 2; 
			int h = hexDigits.indexOf(hexChars[pos]) << 4; 
			int l = hexDigits.indexOf(hexChars[pos + 1]); 
			if (h == -1 || l == -1) { 
				return null;
			}
			bytes[i] = (byte) (h | l);
		}
		return bytes;
	}

}
