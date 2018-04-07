package com.example.test.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.example.test.encrypt.tool.ConvertBytesToBase64;

//RSA 工具类。提供加密，解密，生成密钥对等方法。
public class RSAUtil {
    private static final String TAG = "RSAUtil";
    private static final String Algorithm = "RSA";
//	private static String RSAKeyStore = "E:/RSAKey.txt";
//
//	//生成密钥对
//	private static KeyPair generateKeyPair() throws Exception {
//		try {
//			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(Algorithm,
//					new org.bouncycastle.jce.provider.BouncyCastleProvider());
//			// 这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会降低
//			final int KEY_SIZE = 1024;
//			keyPairGen.initialize(KEY_SIZE, new SecureRandom());
//			KeyPair keyPair = keyPairGen.generateKeyPair();
//			saveKeyPair(keyPair);
//			return keyPair;
//		} catch (Exception e) {
//			throw new Exception(e.getMessage());
//		}
//	}
//
//	private static KeyPair getKeyPair() throws Exception {
//		FileInputStream fis = new FileInputStream(RSAKeyStore);
//		ObjectInputStream oos = new ObjectInputStream(fis);
//		KeyPair kp = (KeyPair) oos.readObject();
//		oos.close();
//		fis.close();
//		return kp;
//	}
//
//	private static void saveKeyPair(KeyPair kp) throws Exception {
//		FileOutputStream fos = new FileOutputStream(RSAKeyStore);
//		ObjectOutputStream oos = new ObjectOutputStream(fos);
//		oos.writeObject(kp);
//		oos.close();
//		fos.close();
//	}
//
//	//生成公钥
//	private static RSAPublicKey generateRSAPublicKey(byte[] modulus,
//			byte[] publicExponent) throws Exception {
//		KeyFactory keyFac = null;
//		try {
//			keyFac = KeyFactory.getInstance(Algorithm,
//					new org.bouncycastle.jce.provider.BouncyCastleProvider());
//		} catch (NoSuchAlgorithmException ex) {
//			throw new Exception(ex.getMessage());
//		}
//
//		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(
//				modulus), new BigInteger(publicExponent));
//		try {
//			return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
//		} catch (InvalidKeySpecException ex) {
//			throw new Exception(ex.getMessage());
//		}
//	}
//
//	//生成私钥
//	private static RSAPrivateKey generateRSAPrivateKey(byte[] modulus,
//			byte[] privateExponent) throws Exception {
//		KeyFactory keyFac = null;
//		try {
//			keyFac = KeyFactory.getInstance(Algorithm,
//					new org.bouncycastle.jce.provider.BouncyCastleProvider());
//		} catch (NoSuchAlgorithmException ex) {
//			throw new Exception(ex.getMessage());
//		}
//
//		RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(
//				modulus), new BigInteger(privateExponent));
//		try {
//			return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
//		} catch (InvalidKeySpecException ex) {
//			throw new Exception(ex.getMessage());
//		}
//	}
//
//	// 通过公钥byte[]将公钥还原，适用于RSA算法
//	private static PublicKey getPublicKey(byte[] keyBytes)
//			throws NoSuchAlgorithmException, InvalidKeySpecException {
//		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance(Algorithm);
//		PublicKey publicKey = keyFactory.generatePublic(keySpec);
//		return publicKey;
//	}
//
//	// 通过私钥byte[]将公钥还原，适用于RSA算法
//	private static PrivateKey getPrivateKey(byte[] keyBytes)
//			throws NoSuchAlgorithmException, InvalidKeySpecException {
//		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance(Algorithm);
//		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
//		return privateKey;
//	}

    //加密
    private static byte[] encrypt(PublicKey pk, byte[] data) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(Algorithm,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            int blockSize = cipher.getBlockSize();
            int outputSize = cipher.getOutputSize(data.length);
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
                    : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize) {
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
                } else {
                    cipher.doFinal(data, i * blockSize, data.length - i * blockSize, raw, i * outputSize);
                }
                i++;
            }
            return raw;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //解密
    private static byte[] decrypt(PrivateKey pk, byte[] raw) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(Algorithm,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(cipher.DECRYPT_MODE, pk);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;

            while (raw.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // 使用N、e值还原公钥
    private static PublicKey getPublicKey(String modulus, String publicExponent, int radix)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus, radix);
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent, radix);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus,
                bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    // 使用N、d值还原私钥
    private static PrivateKey getPrivateKey(String modulus, String privateExponent, int radix)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus, radix);
        BigInteger bigIntPrivateExponent = new BigInteger(privateExponent, radix);
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(bigIntModulus,
                bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    //加密函数
    public static String encodeRSA(RSAKeyData key_data, String src) {
        if (key_data == null) {
            //默认的密钥对
            key_data = new RSAKeyData();
            key_data.public_key = "10001";
            key_data.private_key = "";
            key_data.modulus = "c7f668eccc579bb75527424c21be31c104bb44c921b4788ebc82cddab5042909eaea2dd706431531392d79890f9091e13714285a7e79e9d1836397f847046ef2519c9b65022b48bf157fe409f8a42155734e65467d04ac844dfa0c2ae512517102986ba9b62d67d4c920eae40b2f11c363b218a703467d342faa81719f57e2c3";
            key_data.radix = 16;
        }
        try {
            PublicKey key = getPublicKey(key_data.modulus, key_data.public_key, key_data.radix);
            String rev = encodeURL(new StringBuilder(src).reverse().toString());
            byte[] en_byte = encrypt(key, rev.getBytes());
            String base64 = encodeURL(ConvertBytesToBase64.BytesToBase64String(en_byte));
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
            return "RSA加密失败";
        }
    }

    //URL编码
    private static String encodeURL(String str) {
        String encode_str = str;
        try {
            encode_str = URLEncoder.encode(str, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encode_str;
    }

    //URL解码
    private static String decodeURL(String str) {
        String decode_str = str;
        try {
            decode_str = URLDecoder.decode(str, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decode_str;
    }

    public static class RSAKeyData {
        public String modulus;
        public String public_key;
        public String private_key;
        public int radix;

        public RSAKeyData() {
            modulus = "";
            public_key = "";
            private_key = "";
            radix = 0;
        }
    }

}