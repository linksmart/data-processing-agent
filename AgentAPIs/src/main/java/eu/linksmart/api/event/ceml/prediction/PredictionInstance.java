package eu.linksmart.api.event.ceml.prediction;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.services.utils.configuration.Configurator;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 25.01.2016 a researcher of Fraunhofer FIT.
 */
public class PredictionInstance<T> implements Prediction<T> {
    static {
        EventBuilder tmp = new PredictionBuilder();
        EventBuilder.registerBuilder(PredictionInstance.class,tmp);
        EventBuilder.registerBuilder(Prediction.class,tmp);
    }
    private static String DEFAULT_TOPIC = "ceml/output" ;
    private String predictedBy="none";
    private String predictionID = UUID.randomUUID().toString();
    protected Double certaintyDegree;
    protected boolean acceptedPrediction;
    private T prediction;
    protected Object originalInput;
    protected Collection<EvaluationMetric> evaluations = null;
    protected Date madeAt = new Date();
    private transient Configurator conf = Configurator.getDefaultConfig();



    public PredictionInstance(T prediction, Object input ,String predictedBy, Collection<EvaluationMetric> evaluations) {
        this.predictedBy = predictedBy;
        this.evaluations = evaluations;
        this.prediction =prediction;
        this.certaintyDegree = calculateDegreeCertainty();
        this.acceptedPrediction = certaintyDegree>= 1.0;
        originalInput =input;


    }
    public PredictionInstance() {
        this.acceptedPrediction = false;
        this.certaintyDegree =Double.NEGATIVE_INFINITY;
        this.evaluations = null;
        this.prediction =null;
    }


    public String getPredictionID() {
        return predictionID;
    }
    @Override
    public Double getCertaintyDegree() {
        return certaintyDegree;
    }

    public  void setCertaintyDegree(double certaintyDegree) {
        this.certaintyDegree = certaintyDegree;
        acceptedPrediction = certaintyDegree >=1;
    }

    public boolean isAcceptedPrediction() {
        return acceptedPrediction;
    }

    public void setAcceptedPrediction(boolean acceptedPrediction) {
        this.acceptedPrediction = acceptedPrediction;
    }

    @Override
    public Collection<EvaluationMetric> getEvaluationMetrics() {
        return evaluations;
    }

    @Override
    public void setEvaluationMetrics(Collection<EvaluationMetric> evaluations) {
        this.evaluations = evaluations;
        certaintyDegree = calculateDegreeCertainty();
        this.acceptedPrediction = certaintyDegree>= 1.0;
    }


    public String getPredictedBy() {
        return predictedBy;
    }

    public void setPredictedBy(String predictedBy) {
        this.predictedBy = predictedBy;
    }

    @Override
    public T getPrediction() {
        return prediction;
    }
    @Override
    public Object getOriginalInput() {
        return originalInput;
    }
    @Override
    public void setOriginalInput(Object originalInput) {
        this.originalInput = originalInput;
    }

    @Override
    public Date getMadeAt() {
        return madeAt;
    }

    @Override
    public void setMadeAt(Date date) {
        madeAt =date;
    }

    @Override
    public void topicDataConstructor(String topic) {
        // nothing
    }

    @Override
    public Date getDate() {
        return getMadeAt();
    }

    @Override
    public String getId() {
        return getPredictedBy();
    }

    @Override
    public String getAttributeId() {
        return getPredictionID();
    }

    @Override
    public T getValue() {
        return getPrediction();
    }

    @Override
    public void setDate(Date time) {
        setMadeAt(time);

    }

    @Override
    public void setId(String id) {
        setPredictedBy(id);

    }

    @Override
    public void setAttributeId(String id) {
        predictionID = id;
    }

    @Override
    public void setValue(T value) {
        prediction = value;

    }

    @Override
    public String getClassTopic() {
        return DEFAULT_TOPIC;
    }

    @Override
    public void setClassTopic(String topic) {
        DEFAULT_TOPIC = topic;
    }

    @Override
    public String getURL() {
        return null; // will never be used
    }

    @Override
    public void setURL(String URL) {// will never be used

    }

    @Override
    public Map<String, Object> getAdditionalData() {
        Map<String, Object> addidtionalValues = new HashMap<>();
        addidtionalValues.put("evaluations",this.getEvaluationMetrics());
        addidtionalValues.put("input",this.getOriginalInput());

        return addidtionalValues;
    }

    @Override
    public void setAdditionalData(Map<String, Object> additionalData) {
        setEvaluationMetrics((Collection<EvaluationMetric>) additionalData.get("evaluations"));
        setOriginalInput(additionalData.get("input"));

    }

    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        return this;
    }

    @Override
    public void destroy() throws Exception {
        // nothing
    }
}
