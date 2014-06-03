//set main namespace
goog.provide('sprites.Header');

//get requirements
goog.require('lime.Sprite');
goog.require('lime.Label');

sprites.Header = function() {
    goog.base(this);

    this.setSize(335, 109).setFill('assets/Logo-Almanac.png').setAnchorPoint(0.5, 0.5);

    goog.events.listen(this, ['mousedown', 'touchstart'], function(e) {
        e.swallow(['mouseup', 'touchend'], function() {
            almanac.showHome();
        });
    });
}

goog.inherits(sprites.Header, lime.Sprite);