goog.provide('scenes.Virtualization');


goog.require('goog.net.XhrIo');

goog.require('lime.Scene');
goog.require('lime.Layer');
goog.require('lime.Circle');
goog.require('lime.Label');
goog.require('lime.Sprite');
goog.require('lime.ui.Scroller');

goog.require('sprites.Header');
goog.require('sprites.Menu');
goog.require('sprites.Row');

scenes.Virtualization = function() {
    goog.base(this);

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
        .setPosition(almanac.SCREEN_WIDTH / 2, iconY);
    this.appendChild(servicesIcon);

    var tableHeader = new sprites.Row("TYPE", "NAME", "LAST MEASUREMENT", true);
    tableHeader.setAnchorPoint(0, 0)
        .setSize(almanac.SCREEN_WIDTH, sprites.Row.DEFAULT_HEIGHT * 2)
        .setPosition(0, servicesIcon.getPosition().y + 4);
    this.appendChild(tableHeader)

    this.scroll = new lime.ui.Scroller().setAnchorPoint(0, 0)
        .setSize(almanac.SCREEN_WIDTH, almanac.SCREEN_HEIGHT - servicesIcon.getPosition().y + servicesIcon.getSize().height / 2 - sprites.Row.DEFAULT_HEIGHT*2.5)
        .setPosition(0, tableHeader.getPosition().y + tableHeader.getSize().height / 2)
        .setDirection(lime.ui.Scroller.Direction.VERTICAL);
    this.appendChild(this.scroll);

    this.scroll.scrollTo(0);
}

goog.inherits(scenes.Virtualization, lime.Scene);

scenes.Virtualization.prototype.loadData = function() {
    var localThis = this;
    goog.net.XhrIo.send(almanac.DATA_BASE_URL + "/IoTEntities.json", function(e) {
        var xhr = e.target;
        var obj = xhr.getResponseJson();
        var arr = obj.IoTEntity
        if (arr.length == 0) {
            return;
        }
        for (var i = 0; i < arr.length; i++) {
            var measured = "N/A";
            if (goog.isDef(arr[i].Properties[0].IoTStateObservation[0])) {
                measured = arr[i].Properties[0].IoTStateObservation[0].PhenomenonTime;
                measured = moment(measured).format('MMMM Do YYYY, hh:mm:ss Z');
            }
            var row = new sprites.Row(arr[i].TypeOf[0], arr[i].Name, measured);
            if (i % 2 == 0) {
                row.setFill('#ffffff');
            };
            row.setPosition(0, i*sprites.Row.DEFAULT_HEIGHT);
            localThis.scroll.appendChild(row);
        };
    });
};
