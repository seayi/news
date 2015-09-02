package org.yxh.news.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class NewsUtil {
	private static MessageDigest mdInst;
	static {
		try {
			mdInst = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String getMD5FromString(String url) {
		byte[] bytes = url.getBytes();
		mdInst.update(bytes);
		byte[] md = mdInst.digest();
		int j = md.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static boolean isNotEmpty(String str) {
		if (str != null && str.length() > 0)
			return true;
		else
			return false;
	}

	/**
	 *
	 * 返回一个整数值,这个值在start~end之间,但不包含start和end<br>
	 * 请注意:只有start小于end才能产生这样的结果<br>
	 * 如果start等于end,则返回start,相当于直接返回start或者end<br>
	 * 如果传来的start大于end,则会抛出异常
	 * 
	 * @param start
	 *            开始值
	 * @param end
	 *            结束值
	 * @return 返回一个在start和end区间的整数值
	 */
	public static int getRandomInteger(int start, int end) {
		if (start > end) {
			throw new RuntimeException("整数区间出现错误");
		}
		if (start == end) {
			return start;
		}
		return start + (int) (Math.random() * (end - start));
	}

	/**
	 * 启动关联应用程序来打开文件。 如果指定的文件是一个目录，则启动当前平台的文件管理器打开它。
	 * 
	 * @param fileName
	 *            文件路径
	 * @throws IOException
	 *             如果指定文件没有关联应用程序，或者关联应用程序无法启动
	 */
	public static void openFile(File fileName) throws IOException {
		Desktop desktop = getDesktop();
		if (desktop != null) {
			if (desktop.isSupported(Desktop.Action.OPEN)) {
				desktop.open(fileName);
			} else {
				throw new IOException("当前平台不支持该open的Action动作!");
			}
		} else {
			throw new IOException("当前平台不支持此类(Desktop)!");
		}
	}

	/**
	 * 返回当前浏览器上下文的 Desktop 实例。一些平台不支持 Desktop API <br>
	 * 可以使用 isDesktopSupported() 方法来确定是否支持当前桌面。
	 * 
	 * @return 返回当前浏览器上下文的 Desktop 实例
	 */
	public static Desktop getDesktop() {
		if (Desktop.isDesktopSupported()) {
			return Desktop.getDesktop();
		}
		return null;
	}

	public static String rsaCrypt(String modeHex, String exponentHex, String messageg)
			throws IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");

		BigInteger m = new BigInteger(modeHex, 16); /* public exponent */
		BigInteger e = new BigInteger(exponentHex, 16); /* modulus */
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);

		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);

		byte[] encryptedContentKey = enc.doFinal(messageg.getBytes("GB2312"));

		return new String(HexBin.encode(encryptedContentKey).toLowerCase());
	}
}
