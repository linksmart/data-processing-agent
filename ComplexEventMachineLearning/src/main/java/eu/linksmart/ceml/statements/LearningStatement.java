package eu.linksmart.ceml.statements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.ceml.handlers.DoubleListHandler;
import eu.linksmart.ceml.handlers.DoubleMapLearningHandler;
import eu.linksmart.ceml.handlers.IntegerListHandler;
import eu.linksmart.ceml.handlers.IntegerMapLearningHandler;
import eu.linksmart.ceml.handlers.base.LearningListHandler;
import eu.linksmart.ceml.handlers.base.LearningMapHandler;
import eu.linksmart.ceml.core.CEMLManager;
import eu.almanac.event.datafusion.utils.epl.StatementInstance;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.datafusion.JsonSerializable;

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

    @Override
    public JsonSerializable build() throws Exception {

        if(manager==null||name==null||statement==null)
            throw new Exception("The name, CEMLRequest and statements are mandatory fields!");
        if(manager.getDescriptors().isLambdaTypeDefinition())
            if(manager.getDescriptors().getType()== DataDescriptor.DescriptorTypes.NUMBER)
                CEHandler = DoubleListHandler.class.getCanonicalName();
            else if(manager.getDescriptors().getType()== DataDescriptor.DescriptorTypes.INTEGER)
                CEHandler = IntegerListHandler.class.getCanonicalName();
            else
                CEHandler = eu.linksmart.ceml.handlers.LearningListHandler.class.getCanonicalName();
        else{
            boolean allEqual = true;
            DataDescriptor last = manager.getDescriptors().get(0);
            for (DataDescriptor descriptor:manager.getDescriptors())
                if(!last.equals(descriptor)){
                    allEqual =false;
                    break;
                }
            last = manager.getDescriptors().get(0);
            if(allEqual) {
                if (last.getType() == DataDescriptor.DescriptorTypes.NUMBER)
                    CEHandler = DoubleMapLearningHandler.class.getCanonicalName();
                else if (last.getType() == DataDescriptor.DescriptorTypes.INTEGER)
                    CEHandler = IntegerMapLearningHandler.class.getCanonicalName();
            }else
                CEHandler = eu.linksmart.ceml.handlers.MapLearningHandler.class.getCanonicalName();

        }

        return this;

    }
    @JsonIgnore
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
    public LearningStatement(){
        super();
    }

}
