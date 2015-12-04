package de.fraunhofer.fit.event.ceml;

import de.fraunhofer.fit.event.ceml.type.requests.builded.DataStructure;
import de.fraunhofer.fit.event.ceml.type.requests.builded.LearningRequest;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import com.google.gson.*;

/**
 * Created by angel on 13/11/15.
 */
@RestController
public class CEMLRest {

     private Configurator conf = Configurator.getDefaultConfig();
     private LoggerService loggerService = Utils.initDefaultLoggerService(CEML.class);
    private Map<String, LearningRequest> requests = new Hashtable<>();
    @RequestMapping(value="/ceml/learningObject/{objectType}/{objectName}", method= RequestMethod.POST)
    public ResponseEntity<String> createLearningObject(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectName") String objectName
    ) {


        try {

            if (!CEML.createLearningObject(objectName,objectType))
                return new ResponseEntity<>("Error 409 Conflict: The given name already exist in the CEML Engine. Please select different name for the Learning Object", HttpStatus.CONFLICT);
        } catch (ClassNotFoundException e) {
            return new ResponseEntity<>("Error 404 Not Found: The give learning object type not found", HttpStatus.NOT_FOUND);
        }catch (InstantiationException |IllegalAccessException e){
            return new ResponseEntity<>("Error 500 Internal Error: Unexpected error crating the Learning Object see error below \n"+e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Error 500 Internal Error: \n"+e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Learning Object created with name "+objectName,HttpStatus.CREATED);
    }
    @RequestMapping(value={"/ceml/learningObject/{objectType}/{objectName} ","/ceml/learningObject/{objectName} "}, method= RequestMethod.DELETE)
    public ResponseEntity<String> deleteLearningObject(
            @PathVariable("objectName") String objectName
    ) {

            if (!CEML.destroyLearningObject(objectName))
                return new ResponseEntity<>("Error 404 Not Found: The provided learning object was not find", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>("Learning Object deleted with name "+objectName,HttpStatus.CREATED);
    }
    @RequestMapping(value={"/ceml/learningObject/{objectType}/{objectName} ","/ceml/learningObject/{objectName} "}, method= RequestMethod.GET)
    public ResponseEntity<String> getLearningObject(
            @PathVariable("objectName") String objectName
    ) {

        Object learningObject = CEML.getLearningObject(objectName);
        if (learningObject==null)
            return new ResponseEntity<>("Error 404 Not Found: The given learning object not found", HttpStatus.NOT_FOUND);

        Method[] methods= learningObject.getClass().getMethods();
        String methodsStr ="";
        if(methods.length>2) {
            for (int i = 0; i < methods.length - 1; i++) {
                methodsStr += methods[i].getName() + ", ";
            }
            methodsStr+=methods[methods.length-1];
        }
        else if(methods.length>0)
            methodsStr=methods[0].getName();


        return new ResponseEntity<>(
                        "Learning object with: \n Name : "+ objectName+
                        "\n Type: "+ learningObject.getClass().getCanonicalName()+
                        "\n With Methods: "+ methodsStr
                ,HttpStatus.OK);
    }
    @RequestMapping(value="/ceml", method= RequestMethod.GET)
    public ResponseEntity<String> getStatus(    ) {

        return new ResponseEntity<>("CEML is running", HttpStatus.OK);
    }
    @RequestMapping(value={"/ceml/learningObject/{objectType}/{objectName}/{methodName} ","/ceml/learningObject/{objectName}/{methodName}"}, method= RequestMethod.PUT)
    public ResponseEntity<String> putLearningObject(
            @PathVariable("objectName") String objectName,
            @PathVariable("methodName") String methodName
            //TODO: ADD body parameters
    ) {
        Object response=null;
        try {
            //TODO: ADD body parameters
             response = CEML.invoke(objectName,methodName, null);
            if (response==null)
                return new ResponseEntity<>("Error 404 Not Found: The given learning object not found", HttpStatus.NOT_FOUND);

        } catch (ClassNotFoundException e) {
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 404 Not Found: The given learning object type not found", HttpStatus.NOT_FOUND);
        } catch (NoSuchMethodException e) {
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 404 Not Found: The given method of the learning object not found", HttpStatus.NOT_FOUND);

        } catch (InvocationTargetException e) {
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method ", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (IllegalAccessException e) {
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                response.toString()
                ,HttpStatus.OK);
    }
    @RequestMapping(value="/ceml/data/structuring/{objectName}", method= RequestMethod.POST)
    public ResponseEntity<String> formatingStructuring(
            @PathVariable("objectName") String objectName,
            @RequestBody() String body
    ){
        DataStructure structure = (new Gson()).fromJson(body, DataStructure.class);
        structure.setName(objectName);
        try {
           if(! CEML.createDataStructure(structure))
               return new ResponseEntity<>("Error 500 Intern Error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return new ResponseEntity<>("Structure of data with name "+objectName+"was created",HttpStatus.OK);
    }
    @RequestMapping(value="/ceml/{name}", method= RequestMethod.POST)
    public ResponseEntity<String> learningRequest(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        String retur ="";
        try {

            LearningRequest request = (new Gson()).fromJson(body, LearningRequest.class);
            request.setName(name);
            retur=CEMLFeeder.feedLearningRequest(request);

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>("Learning request+"+name+" was created and processed",HttpStatus.OK);
    }
    @RequestMapping(value="/ceml/{name}", method= RequestMethod.GET)
    public ResponseEntity<String> learningRequest(
            @PathVariable("name") String name
    ){
        String retur ="";
        try {
            if(requests.containsKey(name))
                retur = (new Gson()).toJson(requests.get(name));
            else
                return new ResponseEntity<>("Error 404 Not Found: Request with name "+name, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(retur,HttpStatus.OK);
    }
}
