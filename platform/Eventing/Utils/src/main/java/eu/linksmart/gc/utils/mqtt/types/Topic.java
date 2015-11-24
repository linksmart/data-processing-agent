package eu.linksmart.gc.utils.mqtt.types;



import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by José Ángel Carvajal on 28.08.2015 a researcher of Fraunhofer FIT.
 */
public class Topic {
    protected final String topic;
    protected Set<String> knownAcceptedTopics = new HashSet<String>(),knownIgnoreTopics = new HashSet<String>();
    protected String[] splitTopic;
    protected Pattern wildTopic;
    public boolean isWild() {
        return isWild;
    }

    public String getTopic() {
        return topic;
    }

    protected final boolean isWild;

    private Topic(){
        topic =null;
        isWild =false;
    }

    public Topic(String topic){
        this.topic = topic;
        isWild = (topic.contains("#")||topic.contains("+"));
        splitTopic = topic.split("/");
        String ptr= topic.replace("/","/").replace("+","[^/]+").replace("/#","(/.+|/)?+$");
        wildTopic = Pattern.compile(ptr);

    }
    @Override
    public String toString(){
        return topic;
    }

    public boolean cmp(String cmpTopic){

        if(cmpTopic.equals(this.topic))
            return true;
        else if (!isWild)
            return false;


        if(knownAcceptedTopics.contains(cmpTopic))
            return true;
        else if (knownIgnoreTopics.contains(cmpTopic))
            return false;


        boolean equal = wildTopic.matcher(cmpTopic).matches();

        if(equal)
            knownAcceptedTopics.add(cmpTopic);
        else
            knownIgnoreTopics.add(cmpTopic);

        return equal;
    }
    public boolean equals(String cmpTopic){
       return cmp(cmpTopic);

    }
   public boolean equals(Topic topic){
      return cmp(topic.toString());
   }

    @Override
    public boolean equals(Object topic){
        if (!(topic instanceof Topic)&&!(topic instanceof String))
            return false;
        if(this == topic)
            return true;

        return cmp(topic.toString());

    }

    @Override
    public int hashCode(){
        return topic.hashCode();
    }


}
