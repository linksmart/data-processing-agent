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

    var background = new lime.Sprite();
    background.setSize(almanac.SCREEN_WIDTH, almanac.SCREEN_HEIGHT)
        .setFill('assets/Bg.jpg')
        .setPosition(almanac.SCREEN_WIDTH / 2, almanac.SCREEN_HEIGHT / 2)
        .setAnchorPoint(0.5, 0.5);
    this.appendChild(background);

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
}

goog.inherits(scenes.Data, lime.Scene);