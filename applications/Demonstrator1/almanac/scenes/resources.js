goog.provide('scenes.Resources');


//get requirements
goog.require('lime.Scene');
goog.require('lime.Layer');
goog.require('lime.Circle');
goog.require('lime.Label');
goog.require('lime.Sprite');

goog.require('sprites.Header');
goog.require('sprites.Menu');


scenes.Resources = function() {
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
    menu.setResourcesSelectedState();
    this.appendChild(menu);

    var iconY = 440;

    var londonIcon = new lime.Sprite();
    londonIcon.setFill('assets/Icon-London.png')
        .setAnchorPoint(0.5, 0.5)
        .setSize(82, 82)
        .setPosition(81, iconY);
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
        .setPosition(almanac.SCREEN_WIDTH - 440, iconY);
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

    var londonMapContainer = new lime.Sprite();
    londonMapContainer.id = "londonMapContainer";
    londonMapContainer.setAnchorPoint(0.5, 0.5)
        .setFill('assets/Map-bg.png')
        .setSize(330, 295)
        .setPosition(310, 545);
    this.appendChild(londonMapContainer);

    var santanderMapContainer = new lime.Sprite();
    santanderMapContainer.setAnchorPoint(0.5, 0.5)
        .setFill('assets/Map-bg.png')
        .setSize(330, 295)
        .setPosition(815, 545);
    this.appendChild(santanderMapContainer);

    var londonMap = document.createElement('div');
    londonMap.id = 'londonMap';
    londonMap.className = 'map';
    londonMapContainer.appendChild(londonMap);

    /*
    var map = L.map('londonMap').setView([51.505, -0.09], 13);
    L.tileLayer('http://{s}.tiles.mapbox.com/v3/oleksandr.idl1bk7k/{z}/{x}/{y}.png', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18
    }).addTo(map);

    console.debug(document.getElementById('londonMap'));
    document.getElementById('londonMap').style['display'] = 'inline';
    londonMapContainer.appendChild(document.getElementById('londonMap'));
    console.debug(document.getElementById('londonMap'));
    */
}

goog.inherits(scenes.Resources, lime.Scene);