package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.AirQualirySensor;
import it.ismb.pertlab.pwal.api.devices.model.DewPointSensor;
import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.FlowMeter;
import it.ismb.pertlab.pwal.api.devices.model.HumiditySensor;
import it.ismb.pertlab.pwal.api.devices.model.LightSensor;
import it.ismb.pertlab.pwal.api.devices.model.OxyMeter;
import it.ismb.pertlab.pwal.api.devices.model.PhMeter;
import it.ismb.pertlab.pwal.api.devices.model.PressureSensor;
import it.ismb.pertlab.pwal.api.devices.model.Resistance;
import it.ismb.pertlab.pwal.api.devices.model.Semaphore;
import it.ismb.pertlab.pwal.api.devices.model.SittingsCounter;
import it.ismb.pertlab.pwal.api.devices.model.TransitsCounter;
import it.ismb.pertlab.pwal.api.devices.model.Semaphore.State;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.VehicleCounter;
import it.ismb.pertlab.pwal.api.devices.model.VehicleSpeed;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 * 
 */
public class App
{
    public static void main(String[] args)
    {
        System.out.println("Pwal start");
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                new String[]
                { "applicationContext-pwal.xml" });
        Pwal p = (Pwal) ctx.getBean("PWAL");
        // CnetDataPusher cnet = new CnetDataPusher(5);
        // p.addPwalDeviceListener(cnet);
        Scanner input = new Scanner(System.in);
        String command;
        do
        {
            command = input.nextLine();
            switch (command)
            {
            case "L":
                System.out.println("Devices list:");
                int i = 0;
                for (Device k : p.getDevicesList())
                {
                    System.out.println((i++) + ") pwalId: " + k.getPwalId()
                            + " id: " + k.getId() + " type: " + k.getType());
                }
                break;
            case "Q":
                System.out.println("Inserisci l'indice da interrogare:");
                command = input.nextLine();
                Device de = (Device) p.getDevicesList().toArray()[Integer
                        .parseInt(command)];
                switch (de.getType())
                {
                case DeviceType.THERMOMETER:
                    Thermometer t = (Thermometer) de;
                    System.out.println("This is a thermometer id=" + de.getId()
                            + "  temp=" + t.getTemperature());
                    break;
                case DeviceType.OXYGEN_METER:
                    OxyMeter om = (OxyMeter) de;
                    System.out.println("This is an oxymeter: saturation="
                            + om.getSaturation());
                    break;
                case DeviceType.SEMAPHORE:
                    System.out
                            .println("Digita state per interrogare il semaforo o GREEN, YELLOW, RED per forzare il valore");
                    command = input.nextLine();
                    Semaphore s = (Semaphore) de;
                    if (command.equals("state"))
                    {
                        System.out.println("Stato del semaforo: "
                                + s.getState());
                    }
                    else
                    {
                        s.setState(State.valueOf(command));
                    }
                    break;
                case DeviceType.VEHICLE_COUNTER:
                    VehicleCounter vc = (VehicleCounter) de;
                    System.out.println("This is a Vehicle counter sensor. "
                            + "Id: " + vc.getId() + ", Latitude: "
                            + vc.getLocation().getLat() + ", Longitude: "
                            + vc.getLocation().getLon() + ", NetworkType: "
                            + vc.getNetworkType() + ", Count: " + vc.getCount()
                            + ", Occupancy: " + vc.getOccupancy() + "%");
                    break;
                case DeviceType.VEHICLE_SPEED:
                    VehicleSpeed vs = (VehicleSpeed) de;
                    System.out.println("This is a Vehicle speed sensor. "
                            + "Id: " + vs.getId() + ", Latitude: "
                            + vs.getLocation().getLat() + ", Longitude: "
                            + vs.getLocation().getLon() + ", AverageSpeed: "
                            + vs.getAverageSpeed() + "Km/h" + ", NetworkType: "
                            + vs.getNetworkType() + ", Count: " + vs.getCount()
                            + ", MedianSpeed: " + vs.getMedianSpeed() + "Km/h"
                            + ", Occupancy: " + vs.getOccupancy() + "%");
                    break;
                case DeviceType.FILL_LEVEL_SENSOR:
                    FillLevel fl = (FillLevel) de;
                    System.out.println("This is a fill level sensor" + "Id: "
                            + fl.getId() + ", NetworkType "
                            + fl.getNetworkType() + ", Level:" + fl.getLevel()
                            + ", Depth: " + fl.getDepth());
                    break;
                case DeviceType.RESISTANCE:
                    Resistance r = (Resistance) de;
                    System.out.println("This is a resistance" + " ohm: "
                            + r.getOhm());
                    break;
                case DeviceType.FLOW_METER_SENSOR:
                    FlowMeter f = (FlowMeter) de;
                    System.out
                            .println(String
                                    .format("This is a flow meter. Id: %s, NetworkType: %s, Flow: %f UpdatedAt: %s ExpiresAt: %s",
                                            f.getId(), f.getNetworkType(),
                                            f.getFlow(),
                                            f.getUpdatedAt(),
                                            f.getExpiresAt()));
                    break;
                case DeviceType.PH_METER:
                    PhMeter ph = (PhMeter) de;
                    System.out
                            .println(String
                                    .format("This is a ph meter. Id: %s, NetworkType: %s, Ph: %f UpdatedAt: %s ExpiresAt: %s",
                                            ph.getId(), ph.getNetworkType(),
                                            ph.getPh(),
                                            ph.getUpdatedAt(),
                                            ph.getExpiresAt()));
                    break;
                case DeviceType.TRANSITS_COUNTER:
                    TransitsCounter tc = (TransitsCounter) de;
                    System.out
                            .println(String
                                    .format("This is a transits counter. PWAL id: %s, Id: %s, NetworkType: %s, Transits: %d UpdatedAt: %s ExpiresAt: %s",
                                            tc.getPwalId(), tc.getId(),
                                            tc.getNetworkType(),
                                            tc.getTransitCount(),
                                            tc.getUpdatedAt(),
                                            tc.getExpiresAt()));
                    break;
                case DeviceType.SITTINGS_COUNTER:
                    SittingsCounter sc = (SittingsCounter) de;
                    System.out
                            .println(String
                                    .format("This is a sittings counter. PWAL id: %s, Id: %s, NetworkType: %s, Sittings: %d UpdatedAt: %s ExpiresAt: %s",
                                            sc.getPwalId(), sc.getId(),
                                            sc.getNetworkType(),
                                            sc.getSittingsCount(),
                                            sc.getUpdatedAt(),
                                            sc.getExpiresAt()));
                    break;
                case DeviceType.PRESSURE_SENSOR:
                    PressureSensor ps = (PressureSensor) de;
                    System.out
                            .println(String
                                    .format("This is a pressure sensor. PWAL id: %s, Id: %s, NetworkType: %s, Pressure: %f UpdatedAt: %s ExpiresAt: %s",
                                            ps.getPwalId(), ps.getId(),
                                            ps.getNetworkType(),
                                            ps.getPressure(),
                                            ps.getUpdatedAt(),
                                            ps.getExpiresAt()));
                    break;
                case DeviceType.AIR_QUALITY_SENSOR:
                    AirQualirySensor aq = (AirQualirySensor) de;
                    System.out
                            .println(String
                                    .format("This is an air quality sensor. PWAL id: %s, Id: %s, NetworkType: %s, CO2 level: %f UpdatedAt: %s ExpiresAt: %s",
                                            aq.getPwalId(), aq.getId(),
                                            aq.getNetworkType(),
                                            aq.getCO2Level(),
                                            aq.getUpdatedAt(),
                                            aq.getExpiresAt()));
                    break;
                case DeviceType.DEW_POINT_SENSOR:
                    DewPointSensor dp = (DewPointSensor) de;
                    System.out
                            .println(String
                                    .format("This is a dew point temperature sensor. PWAL id: %s, Id: %s, NetworkType: %s, DewPoint: %f UpdatedAt: %s ExpiresAt: %s",
                                            dp.getPwalId(), dp.getId(),
                                            dp.getNetworkType(),
                                            dp.getDewPointTemperature(),
                                            dp.getUpdatedAt(),
                                            dp.getExpiresAt()));
                    break;
                case DeviceType.HUMIDITY_SENSOR:
                    HumiditySensor hu = (HumiditySensor) de;
                    System.out
                            .println(String
                                    .format("This is a humidity sensor. PWAL id: %s, Id: %s, NetworkType: %s, Humidity: %f UpdatedAt: %s ExpiresAt: %s",
                                            hu.getPwalId(), hu.getId(),
                                            hu.getNetworkType(),
                                            hu.getHumidity(),
                                            hu.getUpdatedAt(),
                                            hu.getExpiresAt()));
                    break;
                case DeviceType.LIGHT_SENSOR:
                    LightSensor ls = (LightSensor) de;
                    System.out
                            .println(String
                                    .format("This is a light sensor. PWAL id: %s, Id: %s, NetworkType: %s, Lumen: %f UpdatedAt: %s ExpiresAt: %s",
                                            ls.getPwalId(), ls.getId(),
                                            ls.getNetworkType(), ls.getLight(),
                                            ls.getUpdatedAt(),
                                            ls.getExpiresAt()));
                    break;
                default:
                    break;
                }
            }

        }
        while (!command.equals("exit"));
        input.close();
        ((ConfigurableApplicationContext) ctx).close();
    }
}
