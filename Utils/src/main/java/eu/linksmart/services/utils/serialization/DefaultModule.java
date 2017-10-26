package eu.linksmart.services.utils.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 26.10.2017 a researcher of Fraunhofer FIT.
 */
public class DefaultModule extends SimpleModule implements IOModule {
    public DefaultModule() {
        super();
    }

    public DefaultModule(String name) {
        super(name);
    }

    public DefaultModule(Version version) {
        super(version);
    }

    public DefaultModule(String name, Version version) {
        super(name, version);
    }

    public DefaultModule(String name, Version version, Map<Class<?>, JsonDeserializer<?>> deserializers) {
        super(name, version, deserializers);
    }

    public DefaultModule(String name, Version version, List<JsonSerializer<?>> serializers) {
        super(name, version, serializers);
    }

    public DefaultModule(String name, Version version, Map<Class<?>, JsonDeserializer<?>> deserializers, List<JsonSerializer<?>> serializers) {
        super(name, version, deserializers, serializers);
    }

    @Override
    public Object getTypeId() {
        return super.getTypeId();
    }

    @Override
    public void setSerializers(SimpleSerializers s) {
        super.setSerializers(s);
    }

    @Override
    public void setDeserializers(SimpleDeserializers d) {
        super.setDeserializers(d);
    }

    @Override
    public void setKeySerializers(SimpleSerializers ks) {
        super.setKeySerializers(ks);
    }

    @Override
    public void setKeyDeserializers(SimpleKeyDeserializers kd) {
        super.setKeyDeserializers(kd);
    }

    @Override
    public void setAbstractTypes(SimpleAbstractTypeResolver atr) {
        super.setAbstractTypes(atr);
    }

    @Override
    public void setValueInstantiators(SimpleValueInstantiators svi) {
        super.setValueInstantiators(svi);
    }

    @Override
    public SimpleModule setDeserializerModifier(BeanDeserializerModifier mod) {
        return super.setDeserializerModifier(mod);
    }

    @Override
    public SimpleModule setSerializerModifier(BeanSerializerModifier mod) {
        return super.setSerializerModifier(mod);
    }

    @Override
    protected SimpleModule setNamingStrategy(PropertyNamingStrategy naming) {
        return super.setNamingStrategy(naming);
    }

    @Override
    public SimpleModule addSerializer(JsonSerializer<?> ser) {
        return super.addSerializer(ser);
    }

    @Override
    public <T> SimpleModule addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
        return super.addSerializer(type, ser);
    }

    @Override
    public <T> SimpleModule addKeySerializer(Class<? extends T> type, JsonSerializer<T> ser) {
        return super.addKeySerializer(type, ser);
    }

    @Override
    public <T> SimpleModule addDeserializer(Class<T> type, JsonDeserializer<? extends T> deser) {
        return super.addDeserializer(type, deser);
    }

    @Override
    public SimpleModule addKeyDeserializer(Class<?> type, KeyDeserializer deser) {
        return super.addKeyDeserializer(type, deser);
    }

    @Override
    public <T> SimpleModule addAbstractTypeMapping(Class<T> superType, Class<? extends T> subType) {
        return super.addAbstractTypeMapping(superType, subType);
    }

    @Override
    public SimpleModule addValueInstantiator(Class<?> beanType, ValueInstantiator inst) {
        return super.addValueInstantiator(beanType, inst);
    }

    @Override
    public SimpleModule registerSubtypes(Class<?>... subtypes) {
        return super.registerSubtypes(subtypes);
    }

    @Override
    public SimpleModule registerSubtypes(NamedType... subtypes) {
        return super.registerSubtypes(subtypes);
    }

    @Override
    public SimpleModule setMixInAnnotation(Class<?> targetType, Class<?> mixinClass) {
        return super.setMixInAnnotation(targetType, mixinClass);
    }

    @Override
    public String getModuleName() {
        return super.getModuleName();
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
    }

    @Override
    public Version version() {
        return super.version();
    }
}
