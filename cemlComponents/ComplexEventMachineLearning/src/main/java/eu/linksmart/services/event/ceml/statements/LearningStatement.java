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

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class LearningStatement extends StatementInstance implements eu.linksmart.api.event.ceml.LearningStatement {
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
    @JsonIgnore
    @Override
    public EventEnvelope getLastOutput() {
        return null;
    }


    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException{

        if(manager==null||name==null||statement==null)
            throw new StatementException(this.getClass().getName(),this.getClass().getCanonicalName(), "The name, CEMLRequest and statements are mandatory fields for a Statment!");
        try {

            if(manager.getDescriptors().isLambdaTypeDefinition())

                CEHandler = ListLearningHandler.class.getCanonicalName();
            else{


                CEHandler = MapLearningHandler.class.getCanonicalName();

            }
        }catch (Exception e){
            throw new UnknownUntraceableException(e.getMessage(),e);
        }

        toRegister(false);
        return this;

    }
    @JsonIgnore
    private CEMLRequest manager =null;
    public LearningStatement(String name, CEMLManager manager , String statement){
        super(name,statement, Collections.singletonList("local"));

        this.statement =statement;
        this.manager =manager;
        if(manager.getDescriptors().isLambdaTypeDefinition())
            CEHandler = ListLearningHandler.class.getCanonicalName();

        CEHandler= MapLearningHandler.class.getCanonicalName();
        this.name =name;
    }
    public LearningStatement(){
        super();
    }

}
