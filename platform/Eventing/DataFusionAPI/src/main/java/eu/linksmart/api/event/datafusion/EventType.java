package eu.linksmart.api.event.datafusion;

import java.util.Date;

/**
 * Created by angel on 17/11/15.
 */
public interface EventType {
    public void topicDataConstructor(String topic);
    public Date getDate();
    public String getId();
    public String getAttributeId();
    public Object getValue();

}
