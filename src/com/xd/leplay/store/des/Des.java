package com.xd.leplay.store.des;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.util.Base64;

/**
 * DES加密介绍 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
 * 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
 * 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现 。
 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
 */
public class Des
{
	public Des()
	{
	}

	// 测试
	public static void main(String args[])
	{
		/*
		 * // 待加密内容 String str = "测试内容"; // 密码，长度要是8的倍数 String password =
		 * "9588028820109132570743325311898426347857298773549468758875018579537757772163084478873699447306034466200616411960574122434059469100235892702736860872901247123456"
		 * ;
		 * 
		 * String result = Des.encrypt(str.getBytes(), password);
		 * System.out.println("加密后：" + result); // 直接将如上内容解密 try { String
		 * decryResult = Des.decrypt(result, password);
		 * System.out.println("解密后：" + decryResult); } catch (Exception e1) {
		 * e1.printStackTrace(); }
		 */
		byte[] key = "12345678".getBytes();

		byte[] iv = "11111111".getBytes();

		System.out.print("=============CBC mode===========");

		byte[] data = Des.CBCEncrypt("hello world!".getBytes(), key, iv);

		System.out.println(new String(Des.CBCDecrypt(data, key, iv)));
	}

	/**
	 * 加密
	 * 
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 */
	public static String encrypt(byte[] datasource, String password)
	{
		try
		{
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv2 = new IvParameterSpec(password.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, securekey, iv2);// IV的方式
			// 用密匙初始化Cipher对象
			// cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return Base64.encodeToString(cipher.doFinal(datasource),
					Base64.DEFAULT);
		} catch (Throwable e)
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static String decrypt(String src, String password) throws Exception
	{
		// // DES算法要求有一个可信任的随机数源
		// SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		IvParameterSpec iv = new IvParameterSpec(password.getBytes());
		AlgorithmParameterSpec paramSpec = iv;
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");		
		cipher.init(Cipher.DECRYPT_MODE, securekey, paramSpec);
		byte[] encrypted1 = Base64.decode(src, Base64.DEFAULT);
		return new String(cipher.doFinal(encrypted1));

	}

	/**
	 * 
	 * 加密函数
	 * 
	 * 
	 * 
	 * @param data
	 * 
	 *            加密数据
	 * 
	 * @param key
	 * 
	 *            密钥
	 * 
	 * @return 前往加密后的数据
	 */

	public static byte[] CBCEncrypt(byte[] data, byte[] key, byte[] iv)
	{

		try
		{

			// 从原始密钥数据树立DESKeySpec对象

			DESKeySpec dks = new DESKeySpec(key);

			// 树立一个密匙工厂，然后用它把DESKeySpec转换成

			// 一个SecretKey对象

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			SecretKey secretKey = keyFactory.generateSecret(dks);

			// Cipher对象实践完成加密操作

			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

			// 若采用NoPadding方式，data长度必需是8的倍数

			// Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");

			// 用密匙原始化Cipher对象

			IvParameterSpec param = new IvParameterSpec(iv);

			cipher.init(Cipher.ENCRYPT_MODE, secretKey, param);

			// 执行加密操作

			byte encryptedData[] = cipher.doFinal(data);

			return encryptedData;

		} catch (Exception e)
		{

			System.err.println("DES算法，加密数据出错!");

			e.printStackTrace();

		}

		return null;

	}

	/**
	 * 
	 * 解密函数
	 * 
	 * 
	 * 
	 * @param data
	 * 
	 *            解密数据
	 * 
	 * @param key
	 * 
	 *            密钥
	 * 
	 * @return 前往解密后的数据
	 */

	public static byte[] CBCDecrypt(byte[] data, byte[] key, byte[] iv)
	{

		try
		{

			// 从原始密匙数据树立一个DESKeySpec对象

			DESKeySpec dks = new DESKeySpec(key);

			// 树立一个密匙工厂，然后用它把DESKeySpec对象转换成

			// 一个SecretKey对象

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			SecretKey secretKey = keyFactory.generateSecret(dks);

			// using DES in CBC mode

			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

			// 若采用NoPadding方式，data长度必需是8的倍数

			// Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");

			// 用密匙原始化Cipher对象

			IvParameterSpec param = new IvParameterSpec(iv);

			cipher.init(Cipher.DECRYPT_MODE, secretKey, param);

			// 正式执行解密操作

			byte decryptedData[] = cipher.doFinal(data);

			return decryptedData;

		} catch (Exception e)
		{

			System.err.println("DES算法，解密出错。");

			e.printStackTrace();

		}

		return null;

	}
}
