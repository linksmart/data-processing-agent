package de.fraunhofer.fit.event.ceml;

import com.google.gson.reflect.TypeToken;
import de.fraunhofer.fit.event.ceml.type.requests.builded.DataStructure;
import de.fraunhofer.fit.event.ceml.type.requests.builded.LearningRequest;
import de.fraunhofer.fit.event.ceml.type.requests.builded.Model;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.WindowEvaluator;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import com.google.gson.*;

/**
 * Created by angel on 13/11/15.
 */
@RestController
public class CEMLRest extends Component{

     private Configurator conf = Configurator.getDefaultConfig();
     private LoggerService loggerService = Utils.initDefaultLoggerService(CEML.class);

    private Map<String, LearningRequest> requests = new Hashtable<>();
    private Gson gson = new Gson();

    public CEMLRest() {
        super(CEMLRest.class.getSimpleName(), "Provides a REST API for managing the Learning request", "CEML");
    }


    @RequestMapping(value="/ceml", method= RequestMethod.GET)
    public ResponseEntity<String> getStatus(    ) {

        return new ResponseEntity<>("CEML is running", HttpStatus.OK);
    }

    private ResponseEntity<String> get(String name, String typeRequest){
        String retur;
        try {
            if(requests.containsKey(name))
                switch (typeRequest){
                    case "complete":
                        retur = (new Gson()).toJson(requests.get(name));
                        break;
                    case "data":
                        retur = (new Gson()).toJson(requests.get(name).getData());
                        break;
                    case "evaluation":
                        retur = (new Gson()).toJson(requests.get(name).getEvaluation());
                        break;
                    case "learning":
                        retur = (new Gson()).toJson(requests.get(name).getLeaningStatements());
                        break;
                    case "model":
                        retur = (new Gson()).toJson(requests.get(name).getModel());
                        break;
                    case "deployment":
                        retur = (new Gson()).toJson(requests.get(name).getDeployStatements());
                        break;
                    default:
                        retur = (new Gson()).toJson(requests.get(name));
                }
            else
                return new ResponseEntity<>("Error 404 Not Found: Request with name "+name, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(retur,HttpStatus.OK);
    }
    private ResponseEntity<String> update(String name, String body, String typeRequest){
        Object retur =null;
        try {

            if(requests.containsKey(name)){
                switch (typeRequest){
                    case "":
                        LearningRequest request = (new Gson()).fromJson(body, LearningRequest.class);
                        requests.get(name).reBuild(request);
                        retur = requests.get(name);
                        break;
                    case "evaluation":
                        Evaluator evaluator = (new Gson()).fromJson(body, WindowEvaluator.class);
                        requests.get(name).getEvaluation().reBuild(evaluator);
                        retur = requests.get(name).getEvaluation();
                        break;
                    case "learning":
                        ArrayList<String> learning = (new Gson()).fromJson(body, new TypeToken<ArrayList<String>>(){}.getType());
                        requests.get(name).rebuildLearningStatements(learning);
                        retur = requests.get(name).getLeaningStatements();
                        break;
                    case "model":
                        Model model = (new Gson()).fromJson(body, Model.class);
                        requests.get(name).getModel().reBuild(model);
                        retur = requests.get(name).getModel();
                        break;

                    case "regression":
                      /*TODO: missing features of regression*/
                        break;

                    case "classify":
                        Model mdl =  requests.get(name).getModel();
                        Map input = gson.fromJson(body, new TypeToken<Map<String, Object>>() {}.getType());
                        retur = requests.get(name).getData().getInstances().attribute(LearningHandler.classify(input, requests.get(name)));
                        break;
                    case "deployment":
                        ArrayList<String> deployed = (new Gson()).fromJson(body, new TypeToken<ArrayList<String>>(){}.getType());
                        requests.get(name).rebuildDeploymentStatements(deployed);
                        retur = requests.get(name).getDeployStatements();
                    default:
                        LearningRequest request1 = (new Gson()).fromJson(body, LearningRequest.class);
                        requests.get(name).reBuild(request1);
                        retur = requests.get(name);
                }



            }else {
                loggerService.warn("There is no learning request with name " + name);
                return new ResponseEntity<>("Error 404 Not found: There is no request with given name"+ name, HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if(retur!=null)
            try {

                return new ResponseEntity<>(gson.toJson(retur),HttpStatus.OK);

            }catch (Exception e){
                return new ResponseEntity<>("{\"message\":\"The process was done correctly. Unfortunately, the finally representation in not available!\"}",HttpStatus.MULTI_STATUS);
            }
        else
            return new ResponseEntity<>("{\"message\":\"There was an unknown error!\"}",HttpStatus.INTERNAL_SERVER_ERROR);

    }
    private ResponseEntity<String> create(String name, String body, String requestType){
        String retur ="";
        try {

            switch (requestType){
                case "":
                    LearningRequest request = (new Gson()).fromJson(body, LearningRequest.class);
                    request.setName(name);
                    retur=CEMLFeeder.feedLearningRequest(request);
                    break;
                default:
                    LearningRequest request1 = (new Gson()).fromJson(body, LearningRequest.class);
                    request1.setName(name);
                    retur=CEMLFeeder.feedLearningRequest(request1);
            }

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if(retur!=null)
            try {

                return new ResponseEntity<>(gson.toJson(retur),HttpStatus.ACCEPTED);

            }catch (Exception e){
                return new ResponseEntity<>("{\"message\":\"The process was done correctly. Unfortunately, the finally representation in not available!\"}",HttpStatus.MULTI_STATUS);
            }
        else
            return new ResponseEntity<>("{\"message\":\"There was an unknown error!\"}",HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @RequestMapping(value="/ceml/learn/{name}", method= RequestMethod.GET)
    public ResponseEntity<String> getRequest(
            @PathVariable("name") String name
    ){

        return get(name,"complete");
    }
    @RequestMapping(value="/ceml/learn/{name}/data", method= RequestMethod.GET)
    public ResponseEntity<String> getRequestData(
            @PathVariable("name") String name
    ){
        return get(name, "data");
    }
    @RequestMapping(value="/ceml/learn/{name}/evaluation", method= RequestMethod.GET)
    public ResponseEntity<String> getEvaluation(
            @PathVariable("name") String name
    ){
        return get(name, "evaluation");
    }
    @RequestMapping(value="/ceml/learn/{name}/learning", method= RequestMethod.GET)
    public ResponseEntity<String> getLearning(
            @PathVariable("name") String name
    ){
        return get(name,"learning");
    }

    @RequestMapping(value="/ceml/learn/{name}/deployment", method= RequestMethod.GET)
    public ResponseEntity<String> getDeployment(
            @PathVariable("name") String name
    ){
        return get(name,"deployment");
    }

    @RequestMapping(value="/ceml/learn/{name}/model", method= RequestMethod.GET)
    public ResponseEntity<String> getModel(
            @PathVariable("name") String name
    ){
        return get(name,"model");
    }
    @RequestMapping(value="/ceml/learn/{name}/model/classify", method= RequestMethod.GET)
    public ResponseEntity<String> classifyWithModel(
            @PathVariable("name") String name
    ){
        return get(name,"classify");
    }
    @RequestMapping(value="/ceml/learn/{name}/regression", method= RequestMethod.GET)
    public ResponseEntity<String> predictWithModel(
            @PathVariable("name") String name
    ){
        return get(name,"regression");
    }
    @RequestMapping(value="/ceml/learn/{name}", method=  RequestMethod.POST)
    public ResponseEntity<String> createRequest(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return create(name,body,"");
    }
    @RequestMapping(value="/ceml/learn/{name}", method=  RequestMethod.PUT)
    public ResponseEntity<String> updateRequest(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
      return update(name, body, "");
    }

    @RequestMapping(value="/ceml/learn/{name}/model", method= RequestMethod.PUT)
    public ResponseEntity<String> updateModel(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name, body, "model");
    }

    @RequestMapping(value="/ceml/learn/{name}/evaluation", method=  RequestMethod.PUT)
    public ResponseEntity<String> updateEvaluation(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name, body, "evaluation");
    }

    @RequestMapping(value="/ceml/learn/{name}/learning", method= RequestMethod.PUT)
    public ResponseEntity<String> updateLearning(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name,body,"learning");
    }
    @RequestMapping(value="/ceml/learn/{name}/deployment", method= RequestMethod.PUT)
    public ResponseEntity<String> updateDeployment(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name,body,"deployment");
    }



}
