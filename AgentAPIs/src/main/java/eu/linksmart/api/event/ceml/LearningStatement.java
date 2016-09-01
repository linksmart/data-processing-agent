package eu.linksmart.api.event.ceml;

import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.Statement;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface LearningStatement<ValueType, ReturnValueType, LearningObject> extends Statement,JsonSerializable {
    CEMLRequest<ValueType, ReturnValueType,LearningObject> getRequest();
    void setRequest(CEMLRequest<ValueType, ReturnValueType, LearningObject> request);
}
