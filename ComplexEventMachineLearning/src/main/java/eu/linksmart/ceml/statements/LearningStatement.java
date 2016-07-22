package eu.linksmart.ceml.statements;

import eu.linksmart.ceml.Handlers.LearningListHandler;
import eu.linksmart.ceml.Handlers.LearningMapHandler;
import eu.linksmart.ceml.core.CEMLManager;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.datafusion.JsonSerializable;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class LearningStatement extends EPLStatement implements eu.linksmart.api.event.ceml.LearningStatement {
    @Override
    public CEMLRequest getRequest() {
        return manager;
    }

    @Override
    public void setRequest(CEMLRequest request) {
        manager =request;
    }

    @Override
    public JsonSerializable build() throws Exception {

        if(manager==null||name==null||statement==null)
            throw new Exception("The name, CEMLRequest and statements are mandatory fields!");

        return this;

    }

    private CEMLRequest manager =null;
    public LearningStatement(String name, CEMLManager manager , String statement){
        super(name,statement,new String[]{"default"});

        this.statement =statement;
        this.manager =manager;
        if(manager.getDescriptors().isLambdaTypeDefinition())
            CEHandler = LearningListHandler.class.getCanonicalName();

        CEHandler= LearningMapHandler.class.getCanonicalName();
        this.name =name;
    }

}
