goog.provide('scenes.Home');


//get requirements
goog.require('lime.Scene');
goog.require('lime.Layer');
goog.require('lime.Circle');
goog.require('lime.Label');
goog.require('lime.Sprite');

goog.require('sprites.Header');
goog.require('sprites.Menu');


scenes.Home = function() {
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
    this.appendChild(menu);

    var iconY = 440;
    var waterIcon = new lime.Sprite();
    waterIcon.setFill('assets/Icon-Water.png')
        .setAnchorPoint(0.5, 0.5)
        .setSize(82, 82)
        .setPosition(110, iconY);
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
        .setPosition(245, iconY);
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

    var londonIcon = new lime.Sprite();
    londonIcon.setFill('assets/Icon-London.png')
        .setAnchorPoint(0.5, 0.5)
        .setSize(82, 82)
        .setPosition(almanac.SCREEN_WIDTH-245, iconY);
    var londonLabel = new lime.Label()
    londonLabel.setAnchorPoint(0.5, 0)
        .setMultiline(true)
        .setPosition(0, londonIcon.getSize().height - 30)
        .setFontColor('#6fac53')
        .setFontSize(20)
        .setFontFamily('HelveticaNeue')
        .setText("London");
    londonIcon.appendChild(londonLabel);
    this.appendChild(londonIcon);

    var santanderIcon = new lime.Sprite();
    santanderIcon.setFill('assets/Icon-Santander.png')
        .setAnchorPoint(0.5, 0.5)
        .setSize(82, 82)
        .setPosition(almanac.SCREEN_WIDTH-110, iconY);
    var santanderLabel = new lime.Label()
    santanderLabel.setAnchorPoint(0.5, 0)
        .setMultiline(true)
        .setPosition(0, santanderIcon.getSize().height - 30)
        .setFontColor('#6fac53')
        .setFontSize(20)
        .setFontFamily('HelveticaNeue')
        .setText("Santander");
    santanderIcon.appendChild(santanderLabel);
    this.appendChild(santanderIcon);
    
    var servicesIcon = new lime.Sprite();
    servicesIcon.setFill('assets/Icon-Services.png')
        .setAnchorPoint(0.5, 0.5)
        .setSize(82, 82)
        .setPosition(almanac.SCREEN_WIDTH/2, iconY);
    var servicesLabel = new lime.Label()
    servicesLabel.setAnchorPoint(0.5, 0)
        .setMultiline(true)
        .setPosition(0, servicesIcon.getSize().height - 30)
        .setFontColor('#6fac53')
        .setFontSize(20)
        .setFontFamily('HelveticaNeue')
        .setText("CNetCoffeeBrower\nMark's iPad\nSomething else\nand 5 more...");
    servicesIcon.appendChild(servicesLabel);
    this.appendChild(servicesIcon);

    var bottom = new lime.Sprite();
    bottom.setSize(520, 131).setFill('assets/Ribbon-OverviewBottom.png')
        .setAnchorPoint(0.5, 1)
        .setPosition(almanac.SCREEN_WIDTH / 2, almanac.SCREEN_HEIGHT);
    this.appendChild(bottom)
}

goog.inherits(scenes.Home, lime.Scene);