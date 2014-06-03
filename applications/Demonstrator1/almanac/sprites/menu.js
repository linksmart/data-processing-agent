//set main namespace
goog.provide('sprites.Menu');

//get requirements
goog.require('lime.Sprite');
goog.require('lime.Label');

goog.require('sprites.Button');

sprites.Menu = function() {
    goog.base(this);

    this.setAnchorPoint(0.5, 0.5);
    this.setSize(almanac.SCREEN_WIDTH, 200);

    this.dataButton = new sprites.Button();
    this.virtualButton = new sprites.Button();
    this.resButton = new sprites.Button();
    this.appendChild(this.dataButton);
    this.appendChild(this.virtualButton);
    this.appendChild(this.resButton);

    this.dataButton.activeFill = 'assets/Comp-DataMngm-Active.png';
    this.dataButton.passiveFill = 'assets/Comp-DataMngm-Passive.png';

    this.virtualButton.activeFill = 'assets/Comp-Virt-Active.png';
    this.virtualButton.passiveFill = 'assets/Comp-Virt-Passive.png';

    this.resButton.activeFill = 'assets/Comp-AdaptResrc-Active.png';
    this.resButton.passiveFill = 'assets/Comp-AdaptResrc-Passive.png';

    this.dataButton.setAnchorPoint(0.5, 0)
        .setPosition(-1 * this.dataButton.getSize().width, 0);
    this.dataButton.handler = function() {
        almanac.showData();
    };

    this.virtualButton.setAnchorPoint(0.5, 0)
        .setPosition(0, 0);
    this.virtualButton.handler = function() {
        almanac.showVirtualization();
    };

    this.resButton.setAnchorPoint(0.5, 0)
        .setPosition(this.resButton.getSize().width, 0);
    this.resButton.handler = function() {
        almanac.showResources();
    };

    this.dataArrow = new lime.Sprite();
    this.virtualArrow = new lime.Sprite();
    this.resArrow = new lime.Sprite();
    this.appendChild(this.dataArrow);
    this.appendChild(this.virtualArrow);
    this.appendChild(this.resArrow);

    this.dataArrow.setAnchorPoint(0.5, 0)
        .setSize(335, 55)
        .setPosition(-1 * this.dataButton.getSize().width, this.dataButton.getSize().height)
        .setFill('assets/ArrowUp.png');

    this.virtualArrow.setAnchorPoint(0.5, 0)
        .setSize(335, 55)
        .setPosition(0, this.virtualButton.getSize().height)
        .setFill('assets/ArrowUp.png');

    this.resArrow.setAnchorPoint(0.5, 0)
        .setSize(335, 55)
        .setPosition(this.resButton.getSize().width, this.resButton.getSize().height)
        .setFill('assets/ArrowUp.png');

    this.dataLabel = new lime.Label();
    this.virtualLabel = new lime.Label();
    this.resLabel = new lime.Label();
    this.appendChild(this.dataLabel);
    this.appendChild(this.virtualLabel);
    this.appendChild(this.resLabel);

    this.dataLabel.setAnchorPoint(0.5, 0)
        .setSize(335, 75)
        .setFontColor('#5b5b5b')
        .setFontSize(16)
        .setMultiline(true)
        .setFontFamily('HelveticaNeue-Light')
        .setPosition(-1 * this.dataButton.getSize().width, this.dataArrow.getPosition().y + this.dataArrow.getSize().height + 15)
        .setText("EXEMPLARY\nAPPLICATIONS");
    this.virtualLabel.setAnchorPoint(0.5, 0)
        .setSize(335, 75)
        .setFontColor('#5b5b5b')
        .setFontSize(16)
        .setMultiline(true)
        .setFontFamily('HelveticaNeue-Light')
        .setPosition(0, this.virtualArrow.getPosition().y + this.virtualArrow.getSize().height + 15)
        .setText("REGISTERED\nSERVICES");
    this.resLabel.setAnchorPoint(0.5, 0)
        .setSize(335, 75)
        .setFontColor('#5b5b5b')
        .setFontSize(16)
        .setMultiline(true)
        .setFontFamily('HelveticaNeue-Light')
        .setPosition(this.resButton.getSize().width, this.resArrow.getPosition().y + this.resArrow.getSize().height + 15)    
        .setText("PHYSICAL RESOURCES\nOF SMART CITIES");

    this.resetState();
}

goog.inherits(sprites.Menu, lime.Sprite);

sprites.Menu.prototype.resetState = function() {
    this.dataButton.setActiveState()
    this.virtualButton.setActiveState();
    this.resButton.setActiveState();

    this.dataArrow.setOpacity(1);
    this.virtualArrow.setOpacity(1);
    this.resArrow.setOpacity(1);

    this.dataLabel.setOpacity(1);
    this.virtualLabel.setOpacity(1);
    this.resLabel.setOpacity(1);
};

sprites.Menu.prototype.setDataSelectedState = function() {
    this.dataButton.setActiveState()
    this.virtualButton.setPassiveState();
    this.resButton.setPassiveState();

    this.dataArrow.setOpacity(1);
    this.virtualArrow.setOpacity(0);
    this.resArrow.setOpacity(0);

    this.dataLabel.setOpacity(1);
    this.virtualLabel.setOpacity(0);
    this.resLabel.setOpacity(0);
};

sprites.Menu.prototype.setVirtualizationSelectedState = function() {
    this.dataButton.setPassiveState()
    this.virtualButton.setActiveState();
    this.resButton.setPassiveState();

    this.dataArrow.setOpacity(0);
    this.virtualArrow.setOpacity(1);
    this.resArrow.setOpacity(0);

    this.dataLabel.setOpacity(0);
    this.virtualLabel.setOpacity(1);
    this.resLabel.setOpacity(0);
};

sprites.Menu.prototype.setResourcesSelectedState = function() {
    this.dataButton.setPassiveState()
    this.virtualButton.setPassiveState();
    this.resButton.setActiveState();

    this.dataArrow.setOpacity(0);
    this.virtualArrow.setOpacity(0);
    this.resArrow.setOpacity(1);

    this.dataLabel.setOpacity(0);
    this.virtualLabel.setOpacity(0);
    this.resLabel.setOpacity(1);
};