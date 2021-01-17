package cn.deal.component.utils;

import org.apache.commons.codec.binary.Base64;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Arrays;

/**
 * @Description: aes对称加密解密工具类, 注意密钥不能随机生机, 不同客户端调用可能需要考虑不同Provider,
 * 考虑安卓与IOS不同平台复杂度,简化不使用Provider
 */
public class AESUtils {

    /***默认向量常量**/
//    private static final String IV = "LDBAOuwXizKGNnSR";

    /**
     * 使用PKCS7Padding填充必须添加一个支持PKCS7Padding的Provider
     * 类加载的时候就判断是否已经有支持256位的Provider,如果没有则添加进去
     */
    static {
//        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
//            Security.addProvider(new BouncyCastleProvider());
//        }
    }
    /**
     * @param content base64处理过的字符串
     * @param secretKey    密匙
     * @param ivParameter    密匙
     * @return String    返回类型
     * @Description: 解密 失败将返回NULL
     */
    public static String decodeStr(String content, String secretKey, String ivParameter) throws Exception {
        try {
            System.out.println("待解密内容:" + content);
            byte[] base64DecodeStr = Base64.decodeBase64(content);
            System.out.println("base64DecodeStr:" + Arrays.toString(base64DecodeStr));
            byte[] aesDecode = aesDecode(base64DecodeStr, secretKey, ivParameter);
            System.out.println("aesDecode:" + Arrays.toString(aesDecode));
            if (aesDecode == null) {
                return null;
            }
            String result;
            result = new String(aesDecode, "UTF-8");
            System.out.println("aesDecode result:" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("解密异常");
        }
    }

    /**
     * 解密 128位
     *
     * @param content 解密前的byte数组
     * @param secretKey    密匙
     * @param ivParameter
     * @return result  解密后的byte数组
     * @throws Exception
     */
    private static byte[] aesDecode(byte[] content, String secretKey, String ivParameter) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParameter.getBytes("UTF-8"));
//        创建密码器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
//        初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
//        解密
        return cipher.doFinal(content);
    }

//    /**
//     * 加密 128位
//     *
//     * @param content 需要加密的原内容
//     * @param pkey    密匙
//     * @return
//     */
//    private static byte[] aesEncrypt(String content, String pkey) {
//        try {
//            //SecretKey secretKey = generateKey(pkey);
//            //byte[] enCodeFormat = secretKey.getEncoded();
//            SecretKeySpec skey = new SecretKeySpec(pkey.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");// "算法/加密/填充"
//            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
//            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);//初始化加密器
//            return cipher.doFinal(content.getBytes("UTF-8")); // 加密
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 获得密钥
//     * @param secretKey
//     * @return
//     * @throws Exception
//     */
//    private static SecretKey generateKey(String secretKey) throws Exception {
//        //防止linux下 随机生成key
//        Provider p = Security.getProvider("SUN");
//        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", p);
//        secureRandom.setSeed(secretKey.getBytes());
//        KeyGenerator kg = KeyGenerator.getInstance("AES");
//        kg.init(secureRandom);
//        // 生成密钥
//        return kg.generateKey();
//    }
//
//    /**
//     * @param content 加密前原内容
//     * @param pkey    长度为16个字符,128位
//     * @return base64EncodeStr   aes加密完成后内容
//     * @Description: aes对称加密
//     */
//    private static String aesEncryptStr(String content, String pkey) {
//        byte[] aesEncrypt = aesEncrypt(content, pkey);
//        System.out.println("加密后的byte数组:" + Arrays.toString(aesEncrypt));
//        String base64EncodeStr = Base64.encodeBase64String(aesEncrypt);
//        System.out.println("加密后 base64EncodeStr:" + base64EncodeStr);
//        return base64EncodeStr;
//    }
//    public static void main(String[] args) throws Exception {
//        //明文
//        String content = "17744523642";
//        //密匙
//        String secretKey = "63207064-f6af-430d-898c-d4095329";
//        System.out.println("待加密报文:" + content);
//        System.out.println("密匙:" + secretKey);
//        String aesEncryptStr = aesEncryptStr(content, secretKey);
//        System.out.println("加密报文:" + aesEncryptStr);
//        String decodeStr = decodeStr(aesEncryptStr, secretKey, IV);
//        System.out.println("解密报文:" + decodeStr);
//        System.out.println("加解密前后内容是否相等:" + content.equals(decodeStr));
//    }
}
