package de.fraunhofer.fit.event.ceml.type.requests.builded;

import de.fraunhofer.fit.event.ceml.type.requests.builded.LearningRequest;
import eu.almanac.event.datafusion.utils.epl.intern.EPLStatement;

/**
 * Created by angel on 26/11/15.
 */
public class LearningStatement extends EPLStatement {


    public LearningRequest getLearningRequest() {
        return learningRequest;
    }

    public void setLearningRequest(LearningRequest learningRequest) {
        this.learningRequest = learningRequest;
    }

    private LearningRequest learningRequest;
    public LearningStatement(String name, LearningRequest learningRequest, String statement){
        this.statement =statement;
        this.learningRequest =learningRequest;
        CEHandler= "de.fraunhofer.fit.event.ceml.LearningHandler";
        this.name =name;
    }
}
