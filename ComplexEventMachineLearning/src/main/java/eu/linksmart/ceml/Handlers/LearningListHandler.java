package eu.linksmart.ceml.handlers;

import eu.linksmart.api.event.ceml.LearningStatement;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 25.07.2016 a researcher of Fraunhofer FIT.
 */
public class LearningListHandler extends eu.linksmart.ceml.handlers.base.LearningListHandler<Object,Object> {
    public LearningListHandler(LearningStatement<List<Object>, List<Object>> statement) {
        super(statement);
    }
}
