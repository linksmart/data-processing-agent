package eu.linksmart.services.utils.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import sun.security.ec.ECPublicKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.crypto.KeyGenerator;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by José Ángel Carvajal on 09.10.2017 a researcher of Fraunhofer FIT.
 */
public class JWSDeserializer implements Deserializer {
    private final Deserializer deserializer;
    private final ConcurrentMap<String,String> keys = new ConcurrentHashMap<>();
    private final ConcurrentMap<String,JWSVerifier> verifiers = new ConcurrentHashMap<>();
    private final String myInstance = UUID.randomUUID().toString();

    public JWSDeserializer() {
        deserializer = new DefaultDeserializer();

    }
    public JWSDeserializer(Deserializer deserializer) {
        this.deserializer = deserializer;

    }
    public JWSDeserializer(String publicKey) {
        deserializer = new DefaultDeserializer();
        setDefaultKey(publicKey);
    }
    public JWSDeserializer(String publicKey, Deserializer deserializer) {
        this.deserializer = deserializer;
        setDefaultKey(publicKey);

    }

    public boolean setDefaultKey(String publicKey){
            return keys.putIfAbsent(myInstance,publicKey)!= null;
    }
    private JWSVerifier getVerifier(String keyName, String algorithmName)throws IOException {
        try {
            if (!verifiers.containsKey(keyName))
                if (algorithmName.substring(0, 2).equals("RS") || algorithmName.substring(0, 1).equals("PS"))
                    verifiers.putIfAbsent(
                            keyName,
                            new RSASSAVerifier(new RSAPublicKeyImpl(Base64.getDecoder().decode(keys.get(keyName))))
                    );
                else if (algorithmName.substring(0, 2).equals("EC"))
                    verifiers.putIfAbsent(
                            keyName,
                            new ECDSAVerifier(ECKey.parse(keys.get(keyName)))
                    );
                else if (algorithmName.substring(0, 2).equals("HS"))
                    verifiers.putIfAbsent(
                            keyName,
                            new MACVerifier(keys.get(keyName))
                    );
                else
                    throw new IOException("Unknown signing algorithm");
        } catch (Exception e) {
            throw new IOException(e);
        }
        return verifiers.get(keyName);
    }
    @Override
    public <T> T parse(String string, Class<T> tClass) throws IOException {
        return parse(string,tClass,myInstance);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        return this.parse(new String(bytes), tClass);
    }
    public <T> T parse(String string, Class<T> tClass, String source) throws IOException {
        try {

            return deserializer.parse(parse(string,source),tClass);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    public String parse(String string, String source) throws IOException {
        try {
            return new String(unpack(string.getBytes(), source));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    public byte[] unpack(byte[] string, String source) throws IOException {
        try {
            //return jwtParser.setSigningKey(keys.get(myInstance)).parseClaimsJws(string).getBody().get();
            JWSObject jwsObject =  JWSObject.parse(new String(string));

            if(!jwsObject.verify(getVerifier(source,jwsObject.getHeader().getAlgorithm().getName())))
                throw new IOException("Message cannot be beatified!");

            return jwsObject.getPayload().toBytes();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    public <T> T deserialize(byte[] bytes, Class<T> tClass, String source) throws IOException {
        return this.parse(new String(bytes), tClass, source);
    }
    @SafeVarargs
    @Override
    public final <I, C extends I> boolean defineClassToInterface(Class<I> tInterface, Class<C>... tClass) {
        return deserializer.defineClassToInterface(tInterface,tClass);
    }

    @Override
    public Object parsePacked(String objectString, TypeReference type) throws IOException, NotImplementedException {
        return deserializer.parsePacked(objectString, type);
    }

    @Override
    public Object deserializePacked(byte[] bytes, TypeReference type) throws IOException, NotImplementedException {
        return deserializer.deserializePacked(bytes,type);
    }

    @Override
    public <T> void addModule(String name, Class<T> tClass, DeserializerMode<T> deserializerMode) {
        deserializer.addModule(name, tClass, deserializerMode);
    }

    @Override
    public <I, C extends I> void addModule(String name, Class<I> tInterface, Class<C> tClass) {
        deserializer.addModule(name, tInterface, tClass);
    }


    public boolean defineClassToInterface(String source, String key) {

        return keys.putIfAbsent(source,key)!=null;
    }


    @Override
    public void close() {

    }

    @Override
    public Object getParser() {
        return deserializer.getParser();
    }
}
