//set main namespace
goog.provide('sprites.Button');

//get requirements
goog.require('lime.Sprite');

sprites.Button = function() {
    goog.base(this);

    this.setSize(335, 145).setFill('#ff0000').setAnchorPoint(0.5, 0.5);

    this.activeFill = '#ff0000';
    this.passiveFill = '#00ff00';

    this.handler = function() {
        console.debug("Handler not defined!");
    }

    goog.events.listen(this, ['mousedown', 'touchstart'], function(e) {
        e.swallow(['mouseup', 'touchend'], function() {
            this.handler();
        });
    });
}

goog.inherits(sprites.Button, lime.Sprite);

sprites.Button.prototype.setActiveState = function() {
    this.setFill(this.activeFill);
};

sprites.Button.prototype.setPassiveState = function() {
    this.setFill(this.passiveFill);
};