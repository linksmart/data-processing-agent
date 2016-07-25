package eu.linksmart.ceml.handlers;

import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.ceml.handlers.base.LearningListHandler;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 25.07.2016 a researcher of Fraunhofer FIT.
 */
public class IntegerListHandler extends LearningListHandler<Integer,Integer> {
    public IntegerListHandler(LearningStatement<List<Integer>, List<Integer>> statement) {
        super(statement);
    }
}
