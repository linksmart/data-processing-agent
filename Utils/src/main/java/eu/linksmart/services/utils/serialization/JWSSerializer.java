package eu.linksmart.services.utils.serialization;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 09.10.2017 a researcher of Fraunhofer FIT.
 */
public class JWSSerializer implements Serializer {

    private final Serializer serializer;
    private final JWSSigner  signer ;
    private final KeyAlgorithms algorithmName;
    private final JWSAlgorithm algorithm;
    private final  Curve curve;
    private final int keysize;
    private final static SingingAlgorithms def_sing_algorithm = SingingAlgorithms.RS256;
    private final static KeyAlgorithms def_key_algorithm = KeyAlgorithms.RSA;
    private final static Curve def_curve= Curve.P_256;
    private final static int def_keysize = 1024;
    private final JWSHeader header;

    private PublicKey publicKey;


    public JWSSerializer(Serializer serializer, String key) throws IOException {
        this.serializer = serializer;
        this.algorithmName = def_key_algorithm;
        this.curve =def_curve;
        this.keysize = def_keysize;
        this.algorithm = JWSAlgorithm.parse(def_sing_algorithm.name());
        this.signer = createSigner(key);
        this.header = new JWSHeader.Builder(algorithm).keyID(UUID.randomUUID().toString()).build();
    }
    public JWSSerializer(Serializer serializer) throws IOException {
        this.serializer = serializer;
        this.algorithmName = def_key_algorithm;
        this.algorithm = JWSAlgorithm.parse(def_sing_algorithm.name());
        this.curve = def_curve;
        this.keysize = def_keysize;
        this.signer = createSigner();
        this.header = new JWSHeader.Builder(algorithm).keyID(UUID.randomUUID().toString()).build();
    }
    public JWSSerializer(Serializer serializer, String key, KeyAlgorithms keyAlgorithm) throws IOException {
        this.serializer = serializer;
        this.algorithmName = keyAlgorithm;
        this.algorithm = JWSAlgorithm.parse(def_sing_algorithm.name());
        this.curve = def_curve;
        this.keysize = def_keysize;
        this.signer = createSigner(key);
        this.header = new JWSHeader.Builder(this.algorithm).keyID(UUID.randomUUID().toString()).build();
    }

    public JWSSerializer(Serializer serializer, String key, SingingAlgorithms singingAlgorithms) throws IOException {
        this.serializer = serializer;
        this.algorithm = JWSAlgorithm.parse(singingAlgorithms.name());
        this.algorithmName = fromKeyToSigningAlgorithm(singingAlgorithms);
        this.curve = def_curve;
        this.keysize = def_keysize;
        this.signer = createSigner(key);
        this.header = new JWSHeader.Builder(this.algorithm).keyID(UUID.randomUUID().toString()).build();
    }
    public JWSSerializer(Serializer serializer, String key, String curve) throws IOException {
        this.serializer = serializer;
        this.algorithmName = KeyAlgorithms.EC;
        this.curve = Curve.parse(curve);
        this.algorithm = JWSAlgorithm.parse(SingingAlgorithms.ES256.name());
        this.keysize = def_keysize;
        this.signer = createSigner(key);
        this.header = new JWSHeader.Builder(this.algorithm).keyID(UUID.randomUUID().toString()).build();

    }

    public JWSSerializer(Serializer serializer, String key, int keysize) throws IOException {
        this.serializer = serializer;
        this.algorithmName = KeyAlgorithms.RSA;
        this.algorithm = JWSAlgorithm.parse(def_sing_algorithm.name());
        this.curve = def_curve;
        this.keysize = keysize;
        this.signer = createSigner(key);
        this.header = new JWSHeader.Builder(this.algorithm).keyID(UUID.randomUUID().toString()).build();

    }
    private KeyAlgorithms fromKeyToSigningAlgorithm(SingingAlgorithms sa){
        if(sa.name().contains("RS") || sa.name().contains("PS"))
            return KeyAlgorithms.RSA;
        if(sa.name().contains("ES") )
            return KeyAlgorithms.EC;
        if(sa.name().contains("HS") || sa.name().contains("PS"))
            return KeyAlgorithms.HS;

        return KeyAlgorithms.RSA;
    }
    private PrivateKey getGeneratedPrivateKey() throws IOException {
        PrivateKey privateKey ;
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algorithmName.name());


            KeyPair kp = keyGenerator.genKeyPair();
            switch (algorithmName) {
                case RSA:
                    keyGenerator.initialize(keysize);
                    privateKey = kp.getPrivate();
                    break;
                case EC:
                    keyGenerator.initialize(curve.toECParameterSpec());
                    privateKey = kp.getPrivate();
                    break;
                //  case "HS":
                //     return new MACSigner(key);
                default:
                    throw new NoSuchAlgorithmException("There was no signing algorithmName defined");
            }

            publicKey = kp.getPublic();
        } catch (Exception e) {
            throw new IOException(e);
        }
        return privateKey;

    }
    private byte[] generateSecret(){
        // Generate random 256-bit (32-byte) shared secret
        SecureRandom random = new SecureRandom();
        byte[] sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);
        return sharedSecret;
    }
    private JWSSigner createSigner() throws IOException {
        try {
            switch (algorithmName) {
                case RSA:
                    return new RSASSASigner(getGeneratedPrivateKey());
                case EC:
                    return new ECDSASigner((ECPrivateKey) getGeneratedPrivateKey());
                case HS:
                    return new MACSigner(generateSecret());
                default:
                    throw new NoSuchAlgorithmException("There was no signing algorithmName defined");
            }

        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private JWSSigner createSigner(String key) throws IOException {
        try {
            switch (algorithmName) {
                case RSA:
                    return new RSASSASigner(RSAKey.parse(key));
                case EC:
                    return new ECDSASigner(ECKey.parse(key));
                case HS:
                    return new MACSigner(key);
                default:
                    throw new IOException("There was no signing algorithmName defined");
            }

        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return toString(object).getBytes();
    }

    @Override
    public <T> String toString(T object) throws IOException {
        try {
            JWSObject jwsObject = new JWSObject(header, new Payload(serializer.toString(object)));
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T> void addModule(String name, Class<T> tClass, SerializerMode<T> serializerMode) {
        serializer.addModule(name, tClass, serializerMode);
    }

    @Override
    public <I, C extends I> void addModule(String name, Class<I> tInterface, Class<C> tClass) {
        serializer.addModule(name, tInterface, tClass);
    }


    public PublicKey getPublicKey() {
        return publicKey;
    }
    public String getPublicKeyInBase64String(){
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    @Override
    public void close() {

    }
    public enum KeyAlgorithms{
        RSA,EC,HS
    }
    public enum SingingAlgorithms{
        // RSA
        RS256,
        RS384,
        RS512,
        PS256,
        PS384,
        PS512,
        // EC
        ES256,
        ES384,
        ES512,
        // HM
        HS256,
        HS384,
        HS512
    }
}
