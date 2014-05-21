package it.ismb.pertlab.pwal.connectors.gui;

import it.ismb.pertlab.pwal.connectors.rest.ChartService;

import com.sample.highcharts.bean.DataBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HighChartsController {

    @Autowired
    ChartService chartService;


    @RequestMapping(value="charts", method=RequestMethod.GET)
    public String showCharts() {
        return "gui";
    }

    @RequestMapping(value="/linechart1", method=RequestMethod.GET)
    @ResponseBody
    public DataBean showLineChart1() {
        return chartService.getLineChartData1();
    }

    @RequestMapping(value="/linechart2", method=RequestMethod.GET)
    @ResponseBody
    public DataBean showLineChart2() {
        return chartService.getLineChartData2();
    }


    @RequestMapping(value="/linechart3", method=RequestMethod.GET)
    @ResponseBody
    public DataBean showLineChart3() {
        return chartService.getLineChartData3();
    }


}
