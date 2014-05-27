package it.ismb.pertlab.pwal.connectors.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.ismb.pertlab.pwal.PwalImpl;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.highcharts.bean.DataBean;
import it.ismb.pertlab.pwal.highcharts.bean.SeriesBean;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

@Service
public class ChartService {

	@Autowired
	private PwalImpl pwal;

	private static SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat();

	//    public DataBean getLineChartData1() {
	//        List<SeriesBean> list = new ArrayList<SeriesBean>();
	//        list.add(new SeriesBean("Tokyo",  new double[] {7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6}));
	//        list.add(new SeriesBean("New York",  new double[] {0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5}));
	//        list.add(new SeriesBean("London", new double[] {3.9, 4.2, 5.7, 8.5, 12.9, 15.2, 15.0, 16.6, 14.2, 10.3, 6.6, 4.8}));
	//
	//        String[] categories = new String[] {"9 Jan '13", "8 Feb '13","5 Mar '13","12 Apr '13","14 May '13","21 Jun '13","30 Jul '13","8 Aug '13","5 Sep '13","17 Oct '13","23 Nov '13","5 Dec '13"};
	////        return new DataBean("chart1-container", "LineChart Title", "Y Values (%)", "Run Dates", Arrays.asList(categories), list);
	//    }

	public DataBean getTemperaturelineChartData(){
		List<SeriesBean> list = new ArrayList<SeriesBean>();
		List<Device> tempDevList = new ArrayList<Device>();
		Collection<Device> devList= pwal.getDevicesList();

		long[] categories=null;

		for(Device d:devList){
			if(DeviceType.THERMOMETER.equals(d.getType() )){
				tempDevList.add(d);
				Thermometer t=(Thermometer) d;
				t.getTemperature();
				list.add(new SeriesBean(t.getType(),  new double [] {t.getTemperature()}));
				categories= new long[] {System.currentTimeMillis()};
			}
		}
		if(categories.length>0){
			return new DataBean("temperature-container", "Temperature Sensor", "Degree Celcius", "Time", Arrays.asList(categories), list);
		}

		else return null;
	}

	public DataBean getTemperatureSplineChartData(){
		List<SeriesBean> list = new ArrayList<SeriesBean>();
		List<Device> tempDevList = new ArrayList<Device>();
		Collection<Device> devList= pwal.getDevicesList();

		long[] categories=null;


		for(Device d:devList){
			if(DeviceType.THERMOMETER.equals(d.getType() )){
				tempDevList.add(d);
				Thermometer t=(Thermometer) d;
				t.getTemperature();

				list.add(new SeriesBean(t.getType(),  new double [] {System.currentTimeMillis(),t.getTemperature()}));
				categories= new long[] {System.currentTimeMillis()};
			}
		}

		if(categories.length>0){
			return new DataBean("pwal:Thermometer", "Temperature Sensor", "Degree Celcius", "Time", Arrays.asList(categories), list);
		}

		else return null;
	}

	public DataBean getAccel3DChartData(){

		List<SeriesBean> list = new ArrayList<SeriesBean>();
		List<Device> accelDevList = new ArrayList<Device>();
		Collection<Device> devList= pwal.getDevicesList();
		long[] categories=null;
		for(Device d:devList){
			if(DeviceType.ACCELEROMETER.equals(d.getType() )){
				accelDevList.add(d);
				Accelerometer a=(Accelerometer) d;

				list.add(new SeriesBean(a.getType(),  new double [] {a.getXAcceleration(),a.getYAcceleration(), a.getZAcceleration()}));
				categories= new long[] {System.currentTimeMillis()};
			}
		}
		
		if(categories.length>0){
			return new DataBean("pwal:Accelerometer", "Temperature Sensor", "Degree Celcius", "Time", Arrays.asList(categories), list);
		}
		else return null;
	}

}
