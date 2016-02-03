package eu.linksmart.gc.utils.gson;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class GsonSerializableParent<ParentClass> extends GsonSerializable<ParentClass> {
    protected void fixChild(GsonSerializableChild child ){
        child.fixParentReference(this);

    }
    protected void fixChildren(Collection<GsonSerializableChild> children){
        for(GsonSerializableChild child: children)
            fixChild(child);

    }
    public static abstract class GsonSerializableChild<ChildClass> extends GsonSerializable<ChildClass> {

        protected void fixParentReference(GsonSerializable parent){
            try {
                Field field  = GsonSerializable.class.getDeclaredField("this$0");
                field.setAccessible(true);
                field.set(this, parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
