goog.provide('scenes.Data');


//get requirements
goog.require('lime.Scene');
goog.require('lime.Layer');
goog.require('lime.Circle');
goog.require('lime.Label');
goog.require('lime.Sprite');

goog.require('sprites.Header');
goog.require('sprites.Menu');


scenes.Data = function() {
    goog.base(this);

    /*
    var background = new lime.Sprite();
    background.setSize(almanac.SCREEN_WIDTH, almanac.SCREEN_HEIGHT)
        .setFill('assets/Bg.jpg')
        .setPosition(almanac.SCREEN_WIDTH / 2, almanac.SCREEN_HEIGHT / 2)
        .setAnchorPoint(0.5, 0.5);
    this.appendChild(background);
    */

    var header = new sprites.Header();
    header.setPosition(almanac.SCREEN_WIDTH / 2.0, 60);
    this.appendChild(header)

    var menu = new sprites.Menu();
    menu.setAnchorPoint(0.5, 0).setPosition(almanac.SCREEN_WIDTH / 2.0, header.getSize().height);
    menu.setDataSelectedState();
    this.appendChild(menu);

    var iconY = 440;

    var waterIcon = new lime.Sprite();
    waterIcon.setFill('assets/Icon-Water.png')
        .setAnchorPoint(0.5, 0.5)
        .setSize(82, 82)
        .setPosition(81, iconY);
    var waterLabel = new lime.Label()
    waterLabel.setAnchorPoint(0.5, 0)
        .setMultiline(true)
        .setPosition(0, waterIcon.getSize().height - 30)
        .setFontColor('#6fac53')
        .setFontSize(20)
        .setFontFamily('HelveticaNeue')
        .setText("Water\nConsumption");
    waterIcon.appendChild(waterLabel);
    this.appendChild(waterIcon);

    var wasteIcon = new lime.Sprite();
    wasteIcon.setFill('assets/Icon-Waste.png')
        .setAnchorPoint(0.5, 0.5)
        .setSize(82, 82)
        .setPosition(almanac.SCREEN_WIDTH - 440, iconY);
    var wasteLabel = new lime.Label()
    wasteLabel.setAnchorPoint(0.5, 0)
        .setMultiline(true)
        .setPosition(0, wasteIcon.getSize().height - 30)
        .setFontColor('#6fac53')
        .setFontSize(20)
        .setFontFamily('HelveticaNeue')
        .setText("Waste\nCapacity");
    wasteIcon.appendChild(wasteLabel);
    this.appendChild(wasteIcon);

    var waterPlotContainer = new lime.Sprite();
    waterPlotContainer.setAnchorPoint(0.5, 0.5)
        .setFill('assets/Map-bg.png')
        .setSize(330, 295)
        .setPosition(310, 545);
    this.appendChild(waterPlotContainer);

    var wastePlotContainer = new lime.Sprite();
    wastePlotContainer.setAnchorPoint(0.5, 0.5)
        .setFill('assets/Map-bg.png')
        .setSize(330, 295)
        .setPosition(815, 545);
    this.appendChild(wastePlotContainer);

    /*
    //----
    var margin = {
            top: 00,
            right: 20,
            bottom: 110,
            left: 40
        },
        width = waterPlotContainer.getSize().width - margin.left - margin.right,
        height = waterPlotContainer.getSize().height - margin.top - margin.bottom;

    var x = d3.scale.ordinal()
        .rangeRoundBands([0, width], .1);

    var y = d3.scale.linear()
        .range([height, 0]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left")
        .ticks(10, "%");

    var d = document.createElement("div");    
    waterPlotContainer.appendChild(d);

    //var svg = d3.select(document.createElementNS('http://www.w3.org/2000/svg', 'svg'))
    var svg = d3.select(d).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    d3.xml("http://energyportal.cnet.se/StorageManagerMdb/REST/IoTEntities", "application/xml", function(error, data) {
        console.log(error);
        console.log(data);
        
        //d3.select("#chart")
        //    .selectAll("div")
        //    .data(xml.documentElement.getElementsByTagName("value"))
        //    .enter().append("div")
        //      .style("width", function(d) { return d.textContent * 10 + "px"; })
        //      .text(function(d) { return d.textContent; });
        //
        console.log(data.getElementsByTagName('IotEntity'));
    });

    d3.tsv("d3js/data.tsv", type, function(error, data) {
        x.domain(data.map(function(d) {
            return d.letter;
        }));
        y.domain([0, d3.max(data, function(d) {
            return d.frequency;
        })]);

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Frequency");

        svg.selectAll(".bar")
            .data(data)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("x", function(d) {
                return x(d.letter);
            })
            .attr("width", x.rangeBand())
            .attr("y", function(d) {
                return y(d.frequency);
            })
            .attr("height", function(d) {
                return height - y(d.frequency);
            });
    });
    */

}

goog.inherits(scenes.Data, lime.Scene);