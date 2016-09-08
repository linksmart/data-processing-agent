package eu.linksmart.api.event.ceml.data;

import eu.linksmart.api.event.types.JsonSerializable;

import java.util.Date;
import java.util.List;

/**
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
/**
 * This interfaces represent a description of a set of data that a model will get for learning/training, evaluating or predicting.
 * The description consist on two parts, the Input and the Target.
 * The Input is the set of descriptors that provides the description for prediction and partially for learning and evaluation. The Input describe how the input data looks like.
 * The Target aka Ground Truth is the set of descriptors that provides the description for partially for learning and evaluation. The Target is describe how the output data looks like.
 * The TotalInput is the combination of the Input + Target = TotalInput
 *
 * Summary of Predicting, learning, and evaluating  process
 *
 * For Learning: Learn(MeasuredInput, MeasuredTarget)= void     =side effect=> Model Learn
 * For Predicting: Predict(MeasuredInput) = Prediction  =side effect=> None
 * For Evaluating: Evaluate(MeasuredInput, MeasuredTarget, Prediction) =side effect=> Assessment of the state of the Model is updated
 *
 * The structure of MeasuredInput and Prediction are the same, and is defined in the TargetDescriptors
 * The structure of MeasuredInput is defined in the InputDescriptors
 *
 * There are two kids of DataDescriptors(s), the nominal and the lambda. The nominal DataDescriptors are define one-by-one by a DataDescriptor.
 * The lambda DataDescriptors is described by the himself. In this case the DataDescriptors is also a DataDescriptor (please notice the 's' at the end of the words).
 * In a lambda DataDescriptors the DataDescriptor(s) are generated with anonymous names based on the definition of the DataDescriptors.
 * The lambda DataDescriptors is used when the Input and Target are uniform collections/map/etc. of the same type whiteout entities by the own.
 * e.g. The last 100 temperatures of the deice x.
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.1.1
 * @see eu.linksmart.api.event.components.CEPEngine
 * @see eu.linksmart.api.event.types.JsonSerializable
 * @see eu.linksmart.api.event.ceml.CEMLRequest
 * @see eu.linksmart.api.event.ceml.prediction.Prediction
 * @see eu.linksmart.api.event.ceml.data.DataDescriptor
 *
 * */
public interface DataDescriptors extends List<DataDescriptor>,DataDescriptor, JsonSerializable{

    /**
     * Creates an lambda DataDescriptors of the reference implementation of a DataDefinition
     *
     * @param name given to the description
     * @param inputSize size of the Input as int
     * @param targetSize size of the Target as int
     * @param type type of all DataDescriptor(s) as DescriptorTypes
     *
     * @return a instance of a lambda DataDescriptors implemented by DataDefinition.
     *
     * @see eu.linksmart.api.event.ceml.data.DataDefinition
     * @see eu.linksmart.api.event.ceml.data.DataDescriptor.DescriptorTypes
     *
     * */
    public static DataDescriptors factory(String name,int inputSize,int targetSize, DescriptorTypes type){
        return new DataDefinition(name,inputSize,targetSize, type);
    }
    /**
     * Creates an DataDescriptors of the reference implementation of a DataDefinition.
     * The definition of each DataDescriptor must be provided.
     *
     * @param definitions series of definitions. Each DataDescriptor contains if is Target or Input.
     *
     * @return a instance of a DataDescriptors implemented by DataDefinition.
     *
     * @see eu.linksmart.api.event.ceml.data.DataDefinition
     * @see eu.linksmart.api.event.ceml.data.DataDescriptor.DescriptorTypes
     *
     * */
    public static DataDescriptors factory(DataDescriptor... definitions){
        return new DataDefinition(definitions);
    }

    /**
     * @return the DataDescriptor(s) that describe the Target
     * */
    public List<DataDescriptor> getTargetDescriptors();
    /**
     * @return the DataDescriptor(s) that describe the Input
     * */
    public List<DataDescriptor> getInputDescriptors();
    /**
     * Provide DataDescription located i-esm position of the Targets.
     *
     * @param i is the index of the selected DataDescriptor
     *
     * @return the DataDescriptor of index i that describe the Target
     * */
    public DataDescriptor getTargetDescriptor(int i) throws Exception;
    /**
     * Provide DataDescription located i-esm position of the Input.
     *
     * @param i is the index of the selected DataDescriptor
     *
     * @return the DataDescriptor of index i that describe the Input
     * */
    public DataDescriptor getInputDescriptor(int i) throws Exception;
    /**
     * @return the amount of DataDescriptor(s) that describe the Target and Input
     * */
    public int getTotalInputSize();
    /**
     * @return the amount of DataDescriptor(s) that describe the Input
     * */
    public int getInputSize();
    /**
     * @return the amount of DataDescriptor(s) that describe the Target
     * */
    public int getTargetSize();
    /**
     * @return if this DataDescriptors is a lambda DataDescriptor.
     * */
    public boolean isLambdaTypeDefinition();

}
