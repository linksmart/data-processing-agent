package eu.linksmart.api.event.ceml;

import eu.linksmart.api.event.datafusion.Statement;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface LearningStatement<ValueType, ReturnValueType> extends Statement,JsonSerializable {
    CEMLRequest<ValueType, ReturnValueType> getRequest();
}
