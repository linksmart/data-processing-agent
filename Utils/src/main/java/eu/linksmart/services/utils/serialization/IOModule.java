package eu.linksmart.services.utils.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.io.Serializable;

/**
 * Created by José Ángel Carvajal on 26.10.2017 a researcher of Fraunhofer FIT.
 */
public interface IOModule extends Versioned, Serializable {
    Object getTypeId();

    void setSerializers(SimpleSerializers s);

    void setDeserializers(SimpleDeserializers d);

    void setKeySerializers(SimpleSerializers ks);

    void setKeyDeserializers(SimpleKeyDeserializers kd);

    void setAbstractTypes(SimpleAbstractTypeResolver atr);

    void setValueInstantiators(SimpleValueInstantiators svi);

    SimpleModule setDeserializerModifier(BeanDeserializerModifier mod);

    SimpleModule setSerializerModifier(BeanSerializerModifier mod);

    SimpleModule addSerializer(JsonSerializer<?> ser);

    <T> SimpleModule addSerializer(Class<? extends T> type, JsonSerializer<T> ser);

    <T> SimpleModule addKeySerializer(Class<? extends T> type, JsonSerializer<T> ser);

    <T> SimpleModule addDeserializer(Class<T> type, JsonDeserializer<? extends T> deser);

    SimpleModule addKeyDeserializer(Class<?> type, KeyDeserializer deser);

    <T> SimpleModule addAbstractTypeMapping(Class<T> superType, Class<? extends T> subType);

    SimpleModule addValueInstantiator(Class<?> beanType, ValueInstantiator inst);

    SimpleModule registerSubtypes(Class<?>... subtypes);

    SimpleModule registerSubtypes(NamedType... subtypes);

    SimpleModule setMixInAnnotation(Class<?> targetType, Class<?> mixinClass);

    String getModuleName();

    void setupModule(Module.SetupContext context);

    @Override
    Version version();
}
