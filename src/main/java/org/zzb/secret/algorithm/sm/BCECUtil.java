package org.zzb.secret.algorithm.sm;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
/**
 * @author zzb
 * @version 1.0
 * @description:
 * @date 2024年4月14日11:15:13
 */
public class BCECUtil extends GmBaseUtil {
    private static final String ALGO_NAME_EC = "EC";

    private static final String PEM_STRING_PUBLIC = "PUBLIC KEY";

    private static final String PEM_STRING_ECPRIVATEKEY = "EC PRIVATE KEY";

    public static AsymmetricCipherKeyPair generateKeyPair(ECDomainParameters domainParameters, SecureRandom random) {
        ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(domainParameters, random);
        ECKeyPairGenerator keyGen = new ECKeyPairGenerator();
        keyGen.init((KeyGenerationParameters) keyGenerationParams);
        return keyGen.generateKeyPair();
    }

    public static int getCurveLength(ECKeyParameters ecKey) {
        return getCurveLength(ecKey.getParameters());
    }

    public static int getCurveLength(ECDomainParameters domainParams) {
        return (domainParams.getCurve().getFieldSize() + 7) / 8;
    }

    public static ECPrivateKeyParameters createEcPrivateKey(BigInteger d, ECDomainParameters domainParameters) {
        return new ECPrivateKeyParameters(d, domainParameters);
    }

    public static ECPublicKeyParameters createEcPublicKey(BigInteger x, BigInteger y, ECCurve curve, ECDomainParameters domainParameters) {
        byte[] xBytes = x.toByteArray();
        byte[] yBytes = y.toByteArray();
        return createEcPublicKey(xBytes, yBytes, curve, domainParameters);
    }

    public static ECPublicKeyParameters createEcPublicKey(String xHex, String yHex, ECCurve curve, ECDomainParameters domainParameters) {
        byte[] xBytes = ByteUtils.fromHexString(xHex);
        byte[] yBytes = ByteUtils.fromHexString(yHex);
        return createEcPublicKey(xBytes, yBytes, curve, domainParameters);
    }

    public static ECPublicKeyParameters createEcPublicKey(byte[] xBytes, byte[] yBytes, ECCurve curve, ECDomainParameters domainParameters) {
        byte uncompressedFlag = 4;
        byte[] encodedPubKey = new byte[1 + xBytes.length + yBytes.length];
        encodedPubKey[0] = 4;
        System.arraycopy(xBytes, 0, encodedPubKey, 1, xBytes.length);
        System.arraycopy(yBytes, 0, encodedPubKey, 1 + xBytes.length, yBytes.length);
        return new ECPublicKeyParameters(curve.decodePoint(encodedPubKey), domainParameters);
    }

    public static byte[] convertEcPriKeyToPkcs8Der(ECPrivateKeyParameters priKey, ECPublicKeyParameters pubKey) {
        ECDomainParameters domainParams = priKey.getParameters();
        ECParameterSpec spec = new ECParameterSpec(domainParams.getCurve(), domainParams.getG(), domainParams.getN(), domainParams.getH());
        BCECPublicKey publicKey = null;
        if (pubKey != null)
            publicKey = new BCECPublicKey("EC", pubKey, spec, BouncyCastleProvider.CONFIGURATION);
        BCECPrivateKey privateKey = new BCECPrivateKey("EC", priKey, publicKey, spec, BouncyCastleProvider.CONFIGURATION);
        return privateKey.getEncoded();
    }

    public static String convertPkcs8DerEcPriKeyToPem(byte[] encodedKey) throws IOException {
        return convertDerEcDataToPem("EC PRIVATE KEY", encodedKey);
    }

    public static byte[] convertPemToPkcs8DerEcPriKey(String pemString) throws IOException {
        return convertPemToDerEcData(pemString);
    }

    public static byte[] convertEcPriKeyToPkcs1Der(ECPrivateKeyParameters priKey, ECPublicKeyParameters pubKey) throws IOException {
        byte[] pkcs8Bytes = convertEcPriKeyToPkcs8Der(priKey, pubKey);
        PrivateKeyInfo pki = PrivateKeyInfo.getInstance(pkcs8Bytes);
        ASN1Encodable encodable = pki.parsePrivateKey();
        ASN1Primitive primitive = encodable.toASN1Primitive();
        return primitive.getEncoded();
    }

    public static byte[] convertEcPubKeyToX509Der(ECPublicKeyParameters pubKey) {
        ECDomainParameters domainParams = pubKey.getParameters();
        ECParameterSpec spec = new ECParameterSpec(domainParams.getCurve(), domainParams.getG(), domainParams.getN(), domainParams.getH());
        BCECPublicKey publicKey = new BCECPublicKey("EC", pubKey, spec, BouncyCastleProvider.CONFIGURATION);
        return publicKey.getEncoded();
    }

    public static String convertX509DerEcPubKeyToPem(byte[] encodedKey) throws IOException {
        return convertDerEcDataToPem("PUBLIC KEY", encodedKey);
    }

    public static byte[] convertPemToX509DerEcPubKey(String pemString) throws IOException {
        return convertPemToDerEcData(pemString);
    }

    public static ECPrivateKeyParameters convertPkcs1DerToEcPriKey(byte[] encodedKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        BCECPrivateKey privateKey = convertPKCS8ToECPrivateKey(encodedKey);
        ECParameterSpec ecParameterSpec = privateKey.getParameters();
        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN(), ecParameterSpec.getH());
        return new ECPrivateKeyParameters(privateKey.getD(), ecDomainParameters);
    }

    public static BCECPrivateKey convertPKCS8ToECPrivateKey(byte[] pkcs8Key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PKCS8EncodedKeySpec peks = new PKCS8EncodedKeySpec(pkcs8Key);
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        return (BCECPrivateKey) kf.generatePrivate(peks);
    }

    public static ECPublicKeyParameters convertX509DerToEcPubKey(byte[] x509Bytes) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        BCECPublicKey publicKey = convertX509ToECPublicKey(x509Bytes);
        ECParameterSpec ecParameterSpec = publicKey.getParameters();
        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN(), ecParameterSpec.getH());
        return new ECPublicKeyParameters(publicKey.getQ(), ecDomainParameters);
    }

    public static BCECPublicKey convertX509ToECPublicKey(byte[] x509Bytes) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec eks = new X509EncodedKeySpec(x509Bytes);
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        return (BCECPublicKey) kf.generatePublic(eks);
    }

    private static String convertDerEcDataToPem(String type, byte[] encodedData) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut));
        try {
            PemObject pemObj = new PemObject(type, encodedData);
            pWrt.writeObject((PemObjectGenerator) pemObj);
        } finally {
            pWrt.close();
        }
        return new String(bOut.toByteArray());
    }

    private static byte[] convertPemToDerEcData(String pemString) throws IOException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(pemString.getBytes());
        PemReader pRdr = new PemReader(new InputStreamReader(bIn));
        try {
            PemObject pemObject = pRdr.readPemObject();
            return pemObject.getContent();
        } finally {
            pRdr.close();
        }
    }
}
