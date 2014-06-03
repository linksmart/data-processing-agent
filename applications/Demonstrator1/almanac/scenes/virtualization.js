goog.provide('scenes.Virtualization');


//get requirements
goog.require('lime.Scene');
goog.require('lime.Layer');
goog.require('lime.Circle');
goog.require('lime.Label');
goog.require('lime.Sprite');

goog.require('sprites.Header');
goog.require('sprites.Menu');


scenes.Virtualization = function() {
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
    menu.setVirtualizationSelectedState();
    this.appendChild(menu);

    var iconY = 440;
    
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
}

goog.inherits(scenes.Virtualization, lime.Scene);