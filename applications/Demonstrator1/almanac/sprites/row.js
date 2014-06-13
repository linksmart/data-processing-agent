//set main namespace
goog.provide('sprites.Row');

//get requirements
goog.require('lime.Sprite');
goog.require('lime.Label');

sprites.Row = function(type, name, measured, isHeader) {
    goog.base(this);

    var fontColor = '#5b5b5b';
    var fontSize = 18;
    var h = sprites.Row.DEFAULT_HEIGHT;
    this.setSize(almanac.SCREEN_WIDTH, h)
        .setAnchorPoint(0, 0);

    var label = new lime.Label();
    label.setFontSize(fontSize)
        .setAnchorPoint(0, 0.5)
        .setFontFamily('HelveticaNeue')
        .setFontColor(fontSize)
        .setText(goog.string.truncate(type, 30))
        .setPosition(20, h/2);
    this.appendChild(label);

    label = new lime.Label();
    label.setFontSize(fontSize)
        .setMultiline(false)
        .setAlign("left")
        .setAnchorPoint(0, 0.5)
        .setFontFamily('HelveticaNeue')
        .setFontColor(fontColor)
        .setText(goog.string.truncate(name, 32))
        .setAlign('left')
        .setSize(3*almanac.SCREEN_WIDTH/5, label.getSize().height)
        .setPosition(almanac.SCREEN_WIDTH/4 + 30, h/2);
    if (!isHeader) {
        label.setFontColor('#6fac53');
        label.setFontWeight('bold');
    }
    this.appendChild(label);

    label = new lime.Label();
    label.setFontSize(fontSize)
        .setAnchorPoint(0, 0.5)
        .setFontFamily('HelveticaNeue')
        .setFontColor(fontColor)
        .setText(measured)
        .setAlign('left')
        .setSize(3*almanac.SCREEN_WIDTH/5, label.getSize().height)
        .setPosition(3*almanac.SCREEN_WIDTH/5 + 20, h/2);
    this.appendChild(label);
}

goog.inherits(sprites.Row, lime.Sprite);

sprites.Row.DEFAULT_HEIGHT = 36;
