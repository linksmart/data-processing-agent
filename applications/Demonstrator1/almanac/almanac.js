//set main namespace
goog.provide('almanac');


//get requirements
goog.require('lime.Director');
goog.require('lime.Scene');
goog.require('lime.Layer');
goog.require('lime.Circle');
goog.require('lime.Label');
goog.require('lime.transitions.Dissolve');
goog.require('lime.animation.Spawn');
goog.require('lime.animation.FadeTo');
goog.require('lime.animation.ScaleTo');
goog.require('lime.animation.MoveTo');

goog.require('scenes.Home');
goog.require('scenes.Data');
goog.require('scenes.Virtualization');
goog.require('scenes.Resources');


almanac.SCREEN_WIDTH = 1024 * 1;
almanac.SCREEN_HEIGHT = 768 * 1;
almanac.CURRENT_SCENE = '';

// entrypoint
almanac.start = function() {
    almanac.director = new lime.Director(document.body, almanac.SCREEN_WIDTH, almanac.SCREEN_HEIGHT);

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