(function (document) {
    'use strict';

    var adminApp = document.querySelector('#adminApp');

    // Scroll page to top and expand header
    adminApp.scrollPageToTop = function () {
        adminApp.$.headerPanelMain.scrollToTop(true);
    };

    adminApp.closeDrawer = function () {
        adminApp.$.paperDrawerPanel.closeDrawer();
    };

    // Main area's paper-scroll-header-panel custom condensing transformation of
    // the appName in the middle-container and the bottom title in the bottom-container.
    // The appName is moved to top and shrunk on condensing. The bottom sub title
    // is shrunk to nothing on condensing.
    window.addEventListener('paper-header-transform', function (e) {
        var appName = Polymer.dom(document).querySelector('#mainToolbar .app-name');
        var middleContainer = Polymer.dom(document).querySelector('#mainToolbar .middle-container');
        var detail = e.detail;
        var heightDiff = detail.height - detail.condensedHeight;
        var yRatio = Math.min(1, detail.y / heightDiff);
        // appName max size when condensed. The smaller the number the smaller the condensed size.
        var maxMiddleScale = 0.50;
        var auxHeight = heightDiff - detail.y;
        var auxScale = heightDiff / (1 - maxMiddleScale);
        var scaleMiddle = Math.max(maxMiddleScale, auxHeight / auxScale + maxMiddleScale);

        // Move/translate middleContainer
        Polymer.Base.transform('translate3d(0,' + yRatio * 100 + '%,0)', middleContainer);

        // Scale middleContainer appName
        Polymer.Base.transform('scale(' + scaleMiddle + ') translateZ(0)', appName);
    });

    // Scroll page to top and expand header
    adminApp.scrollPageToTop = function () {
        adminApp.$.headerPanelMain.scrollToTop(true);
    };

    adminApp.closeDrawer = function () {
        adminApp.$.paperDrawerPanel.closeDrawer();
    };

    window.addEventListener('WebComponentsReady', function () {
        adminApp.$.productEdit.addEventListener("updated", function() {
            adminApp.$.products.$.items.generateRequest();
        });
        adminApp.$.addMachine.addEventListener("updated", function() {
            adminApp.$.machines.$.items.generateRequest();
        });
    });

})(document);

