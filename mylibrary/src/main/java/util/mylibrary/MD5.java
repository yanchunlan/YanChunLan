package util.mylibrary;

import java.security.MessageDigest;

/**
 * 
*
 */
public class MD5 {
	public final static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

//	public static void main(String[] args) {
		/*
		 * sign=md5(input_charset=GBK&partner=1900000113&total_fee=1&key=
		 * e82573dc7e6136ba414f2e2affbe39fa)
		 */
		// String
		// ss="input_charset=GBK&partner=1900000113&total_fee=1&key=e82573dc7e6136ba414f2e2affbe39fa";
//		String ss = "input_charset=GBK&partner=1900000109&total_fee=1&key=8934e7d15453e97507ef794cf7b0519d";
//		System.out.println(MD5.md5("123456"));
		// System.out.println(MD5.md5("加密"));
//	}
}