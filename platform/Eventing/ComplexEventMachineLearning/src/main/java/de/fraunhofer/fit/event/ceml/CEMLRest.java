package de.fraunhofer.fit.event.ceml;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by angel on 13/11/15.
 */
@RestController
public class CEMLRest {
    @RequestMapping(value="/ceml/{objectType}/{objectName}", method= RequestMethod.POST)
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
        }

        return new ResponseEntity<>("Learning Object created with name "+objectName,HttpStatus.CREATED);
    }
    @RequestMapping(value={"/ceml/{objectType}/{objectName} ","/ceml/{objectName} "}, method= RequestMethod.DELETE)
    public ResponseEntity<String> deleteLearningObject(
            @PathVariable("objectName") String objectName
    ) {

            if (!CEML.destroyLearningObject(objectName))
                return new ResponseEntity<>("Error 404 Not Found: The provided learning object was not find", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>("Learning Object deleted with name "+objectName,HttpStatus.CREATED);
    }
    @RequestMapping(value={"/ceml/{objectType}/{objectName} ","/ceml/{objectName} "}, method= RequestMethod.GET)
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
    @RequestMapping(value={"/ceml/{objectType}/{objectName}/{methodName} ","/ceml/{objectName}/{methodName}"}, method= RequestMethod.PUT)
    public ResponseEntity<String> putLearningObject(
            @PathVariable("objectName") String objectName,
            @PathVariable("methodName") String methodName
    ) {
        Object response=null;
        try {
             response = CEML.invoke(objectName,methodName, null);
            if (response==null)
                return new ResponseEntity<>("Error 404 Not Found: The given learning object not found", HttpStatus.NOT_FOUND);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error 404 Not Found: The given learning object type not found", HttpStatus.NOT_FOUND);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error 404 Not Found: The given method of the learning object not found", HttpStatus.NOT_FOUND);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method ", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method ", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                response.toString()
                ,HttpStatus.OK);
    }

}
