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
}

goog.inherits(scenes.Data, lime.Scene);