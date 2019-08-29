package eu.linksmart.services.event.ceml.statements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.ceml.handlers.ListLearningHandler;
import eu.linksmart.services.event.ceml.handlers.MapLearningHandler;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.services.event.types.StatementInstance;

import java.util.Collections;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class LearningStatement extends StatementInstance implements eu.linksmart.api.event.ceml.LearningStatement {

    public LearningStatement(String name, String statement, List<String> scope) {
        super(name, statement, scope);
        logEventEvery = 1;
    }
    @JsonIgnore
    @Override
    public CEMLRequest getRequest() {
        return manager;
    }
    @JsonIgnore
    @Override
    public void setRequest(CEMLRequest request) {
        manager =request;
    }



    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException{

        if(manager==null||name==null||statement==null)
            throw new StatementException(this.getClass().getName(),this.getClass().getCanonicalName(), "The name, CEMLRequest and statements are mandatory fields for a Statement!");
        try {

            if(manager.getModel().getDataSchema()!=null){
                if(manager.getModel().getDataSchema().getType().equals("array"))
                    CEHandler = ListLearningHandler.class.getCanonicalName();
                else
                    CEHandler = MapLearningHandler.class.getCanonicalName();
            }else{


                CEHandler = MapLearningHandler.class.getCanonicalName();

            }
        }catch (Exception e){
            throw new UnknownUntraceableException(e.getMessage(),e);
        }

        isRegistrable(false);
        return this;

    }
    @JsonIgnore
    private CEMLRequest manager =null;
    public LearningStatement(String name, CEMLManager manager , String statement){
        super(name,statement, Collections.singletonList("local"));

        this.statement =statement;
        this.manager =manager;

        CEHandler= MapLearningHandler.class.getCanonicalName();
        this.name =name;
    }
    public LearningStatement(){
        super();
        logEventEvery = 1;
    }

}
