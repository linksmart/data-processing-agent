//set main namespace
goog.provide('almanac');


//get requirements
goog.require('lime.Director');
goog.require('lime.Renderer.DOM');

goog.require('scenes.Home');
goog.require('scenes.Data');
goog.require('scenes.Virtualization');
goog.require('scenes.Resources');


almanac.DATA_BASE_URL = 'fixtures';
almanac.SCREEN_WIDTH = 1024 * 1;
almanac.SCREEN_HEIGHT = 768 * 1;
almanac.CURRENT_SCENE = '';

// entrypoint
almanac.start = function() {
    almanac.director = new lime.Director(document.body, almanac.SCREEN_WIDTH, almanac.SCREEN_HEIGHT);
    almanac.director.setRenderer(lime.Renderer.DOM);

    // Prepare for mobile device
    almanac.director.makeMobileWebAppCapable();

    // set current scene active
    almanac.showHome();
}

almanac.showHome = function() {
    if (almanac.CURRENT_SCENE == 'home') {
        return;
    };
    if (!goog.isDef(almanac.homeScene)) {
        almanac.homeScene = new scenes.Home();
    };
    almanac.director.replaceScene(almanac.homeScene);
    almanac.CURRENT_SCENE = 'home'
}

almanac.showData = function() {
    if (almanac.CURRENT_SCENE == 'data') {
        return;
    };
    if (!goog.isDef(almanac.dataScene)) {
        almanac.dataScene = new scenes.Data();
    };
    almanac.director.replaceScene(almanac.dataScene);
    almanac.CURRENT_SCENE = 'data'
}

almanac.showVirtualization = function() {
    if (almanac.CURRENT_SCENE == 'virtualization') {
        return;
    };
    if (!goog.isDef(almanac.virtualizationScene)) {
        almanac.virtualizationScene = new scenes.Virtualization();
    };
    almanac.director.replaceScene(almanac.virtualizationScene);
    almanac.CURRENT_SCENE = 'virtualization'
    almanac.virtualizationScene.loadData();
}

almanac.showResources = function() {
    if (almanac.CURRENT_SCENE == 'resources') {
        return;
    };
    if (!goog.isDef(almanac.resourcesScene)) {
        almanac.resourcesScene = new scenes.Resources();
    };
    almanac.director.replaceScene(almanac.resourcesScene);
    almanac.CURRENT_SCENE = 'resources'
}

//this is required for outside access after code is compiled in ADVANCED_COMPILATIONS mode
goog.exportSymbol('almanac.start', almanac.start);