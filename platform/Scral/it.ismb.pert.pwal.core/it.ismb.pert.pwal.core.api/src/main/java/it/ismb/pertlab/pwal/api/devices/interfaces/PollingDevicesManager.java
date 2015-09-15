/*
 * PWAL -Network-level Data Publisher
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.pwal.api.devices.interfaces;

import it.ismb.pertlab.pwal.api.devices.polling.DataUpdatePublisher;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.api.devices.polling.MaximumCommonPollingTimeTask;
import it.ismb.pertlab.pwal.api.devices.polling.PWALPollingTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * A Device Manager with network polling support.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * 
 */
public abstract class PollingDevicesManager<T> extends DevicesManager implements
        DataUpdatePublisher<T>
{
    // the set of subscribers to low-level data updates, mainly for
    // polling-based technologies
    protected HashMap<String, Set<DataUpdateSubscription<T>>> lowLevelDataSubscriptions;

    // the number of active subscriptions
    protected int nActiveSubscriptions;

    // the current polling time in milliseconds
    protected int pollingTimeMillis;

    // the default polling time in milliseconds
    protected int basePollingTimeMillis;

    // the required time tolerance
    protected int timeTolerancePercentage;

    // the minimum polling time in milliseconds
    protected int minimumPollingTimeMillis;

    // the automatic polling time update flag
    protected boolean autoPollingTimeUpdate;

    // the poller service
    protected ScheduledExecutorService poller;

    // the polling task
    @SuppressWarnings("rawtypes")
    protected PWALPollingTask pollingTask;

    // the future task execution promise that allows handling the polling
    // process
    protected ScheduledFuture<?> futureRun;

    public PollingDevicesManager()
    {
        super();

        // initialize the set of subscribers to low-level data updates
        this.lowLevelDataSubscriptions = new HashMap<String, Set<DataUpdateSubscription<T>>>();

        // asks implementing classes to set the base polling time
        this.setBasePollingTimeMillis();

        // asks implementing classes to set the minimum allowed polling time
        this.setMinimumPollingTimeMillis();

        // asks implementing classes to set the minimum allowed polling time
        this.setTimeTolerancePercentage();

        // defaults to the base polling time milliseconds
        this.pollingTimeMillis = basePollingTimeMillis;

        // auto polling time on by default
        this.autoPollingTimeUpdate = true;

        // build the poller
        //this.poller = Executors.newSingleThreadScheduledExecutor();
    }

    // force sub classes to set a base polling time in milliseconds
    protected abstract void setBasePollingTimeMillis();

    // force sub classes to set a base polling time in milliseconds
    protected abstract void setMinimumPollingTimeMillis();

    // force sub classes to set a base polling time in milliseconds
    protected abstract void setTimeTolerancePercentage();

    /***********************************************************
     * 
     * Handle Network Data Subscriptions
     * 
     ***********************************************************/

    @Override
    public boolean addSubscription(DataUpdateSubscription<T> subscription)
    {
        // the operation result as boolean
        boolean added = false;

        // synchronized write to the subscription set
        synchronized (this.lowLevelDataSubscriptions)
        {
            // debug
            log.info("Adding subscription for:" + subscription.getlUID()
                    + " time: " + subscription.getDeliveryTimeMillis());

            // check if the subscription lUID exists
            Set<DataUpdateSubscription<T>> subscriptionBucket = this.lowLevelDataSubscriptions
                    .get(subscription.getlUID());

            // if the bucket does not exists, create it
            if (subscriptionBucket == null )
            {
                // create the set of subscriptions associated to the lower id,
                // they can in fact be possibly more than one per low id.
                subscriptionBucket = new HashSet<DataUpdateSubscription<T>>();

                // store the set
                this.lowLevelDataSubscriptions.put(subscription.getlUID(),
                        subscriptionBucket);
            }

            // replace existing subscription for the same device

            // debug
            log.info("Valid subscription");

            // add the new subscription
            if (subscriptionBucket.contains(subscription))
            {
                // update the subscription
                // apparently this part seems crazy, but actually, as
                // subscriptions are considered equal if they refer to the same
                // device, without considering the desired delivery time, this
                // allows updating the delivery time quickly.
                subscriptionBucket.remove(subscription);
            }
            else
            {
                // increment the number of active subscriptions only if they
                // were not already registered.
                this.nActiveSubscriptions++;
            }

            // add the subscription
            subscriptionBucket.add(subscription);

            // debug
            log.info("More than one subscription");

            // re-compute the polling time
            if (this.autoPollingTimeUpdate)
                this.computeMaximumCommonPollingTime();

            // successful addition
            added = true;
        }

        return added;
    }
    
    protected void computeMaximumCommonPollingTime()
    {
    	this.computeMaximumCommonPollingTime(false);
    }

    /**
     * the maximum common polling time computation algorithm, to be implemented
     * by extending classes. A common simplistic solution is provided by default
     * and find the greatest common divisor.
     */
    protected void computeMaximumCommonPollingTime(boolean forceUpdate)
    {
        // build the set of polling times
        int pollingTimesMillis[] = new int[this.nActiveSubscriptions];

        // extract the polling times
        int i = 0;
        for (Set<DataUpdateSubscription<T>> subscriptions : this.lowLevelDataSubscriptions
                .values())
        {
            for (DataUpdateSubscription<T> subscription : subscriptions)
            {
                // extract and store the desired polling time
                pollingTimesMillis[i] = subscription.getDeliveryTimeMillis();

                // increment the array index
                i++;
            }
        }

        // create the maximum common polling time computation task
        MaximumCommonPollingTimeTask mcpTask = new MaximumCommonPollingTimeTask(
                pollingTimesMillis);

        // actually the subsequent set of instructions perform a
        // synchronous call to the maximum polling time computation method, but
        // in a future like fashion.

        // prepare the task to run in a separate thread
        FutureTask<Integer> futureMCP = new FutureTask<Integer>(mcpTask);

        // run the task
        futureMCP.run();

        // wait the task end for setting up the base polling time
        try
        {
            int newPollingTime = (int) Math.round(futureMCP.get()
                    * (this.timeTolerancePercentage / 100.0));
            log.info("Computed polling time:" + newPollingTime);
            if (newPollingTime != this.pollingTimeMillis)
            {
                // set the current polling time with a minimum value set by the
                // currently set minimum polling time
                this.pollingTimeMillis = Math.max(newPollingTime,
                        this.minimumPollingTimeMillis);

                // debug
                log.info("Updating polling time to" + this.pollingTimeMillis
                        + "and restarting the poller...");

                // updates the global polling time
                this.updatePollingTime();
            }
            else if(forceUpdate)
            {
            	this.updatePollingTime();
            }

        }
        catch (InterruptedException | ExecutionException e)
        {
            DevicesManager.log
                    .warn("Error while computing the maximum common polling time for devices, defaulting to the base value defined in the manager implementation",
                            e);

            this.pollingTimeMillis = Math.max(
                    (int) Math.round(this.basePollingTimeMillis
                            * (this.timeTolerancePercentage / 100.0)),
                    this.minimumPollingTimeMillis);

            // debug
            log.info("Updating polling time to" + this.pollingTimeMillis
                    + "and restarting the poller...");
            this.updatePollingTime();
        }
    }

    protected abstract void updatePollingTime();

    @Override
    public boolean removeSubscription(DataUpdateSubscription<T> subscription)
    {
        // the result flag
        boolean removed = false;

        // get the subscription bucket associated to te given lUID
        Set<DataUpdateSubscription<T>> subscriptionBucket = this.lowLevelDataSubscriptions
                .get(subscription.getlUID());

        // remove the given subscription
        removed = subscriptionBucket.remove(subscription);

        if (removed)
        {
            // //decrease the number of active subscriptions
            this.nActiveSubscriptions--;

            // if the bucket is empty, remove also the bucket
            if (subscriptionBucket.isEmpty())
            {
                this.lowLevelDataSubscriptions.remove(subscription.getlUID());
            }
        }

        return removed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.ismb.pertlab.pwal.api.devices.events.network.DataUpdatePublisher#
     * listSubscriptions()
     */
    @Override
    public Set<DataUpdateSubscription<T>> listSubscriptions(String lUID)
    {
        // TODO Auto-generated method stub
        return this.lowLevelDataSubscriptions.get(lUID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.ismb.pertlab.pwal.api.devices.events.network.DataUpdatePublisher#
     * getSubscription(java.lang.String)
     */
    @Override
    public Set<DataUpdateSubscription<T>> getSubscriptions(String lUID)
    {
        return this.lowLevelDataSubscriptions.get(lUID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.ismb.pertlab.pwal.api.devices.events.network.DataUpdatePublisher#
     * getActiveSubscriptionsSize()
     */
    @Override
    public int getActiveSubscriptionsSize()
    {
        // TODO Auto-generated method stub
        return this.nActiveSubscriptions;
    }

    /**
     * Enables / disables automatic computation of polling times
     * 
     * @param enabled
     */
    public void setAutoPollingUpdate(boolean enabled)
    {
        if ((!this.autoPollingTimeUpdate) && (enabled))
        {
            this.computeMaximumCommonPollingTime(true);

        }

        this.autoPollingTimeUpdate = enabled;
    }

    /***********************************************************/
    public int getPollingTimeMillis()
    {
        return pollingTimeMillis;
    }
    
    @Override
    public void stop()
    {
        Set<DataUpdateSubscription<T>> toRemove = new HashSet<DataUpdateSubscription<T>>();
        for (List<Device> deviceList : devicesDiscovered.values())
        {
            for (Device device : deviceList)
            {
                for (DataUpdateSubscription<T> subscription : this.lowLevelDataSubscriptions
                        .get(device.getId()))
                    toRemove.add(subscription);
            }
        }
        for (DataUpdateSubscription<T> dataUpdateSubscription : toRemove)
        {
            this.removeSubscription(dataUpdateSubscription);
        }
        if ((this.futureRun != null) && (!this.futureRun.isCancelled()))
        {
            //cancel the polling task
            this.futureRun.cancel(false);            
        }
        super.stop();
    }
    
    @Override
    public void start()
    {
        this.pollingTimeMillis = 0;
        super.start();
    }
}
