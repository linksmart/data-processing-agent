package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.Application;

/**
 * Created by devasya on 20.08.2015.
 */
public class AlmanacApplication extends Application {
    MqttWrapper mqttWrapper;

    public  MqttWrapper getMqttWrapper(){
        return  mqttWrapper;
    }
    @Override
    public  void onCreate() {
        super.onCreate();
        mqttWrapper = MqttWrapper.init(this);
    }
}
