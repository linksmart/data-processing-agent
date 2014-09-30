package it.ismb.pertlab.pwal.api.events.pubsub.publisher;

import it.ismb.pertlab.pwal.api.events.pubsub.PWALEventDispatcher;

import com.mycila.event.Publisher;
import com.mycila.event.Topic;

public class PWALEventPublisher implements Publisher
{
    private Topic[] topics;

    public PWALEventPublisher()
    {    }

    @Override
    public Topic[] getTopics()
    {
        return this.topics;
    }

    public void setTopics(String[] topics)
    {
        this.topics = Topic.topics(topics);
    }

    @Override
    public synchronized void publish(Object arg0)
    {
        for (Topic t : this.getTopics())
        {
            PWALEventDispatcher.getInstance().getDispatcher().publish(t, arg0);
        }
    }
}
