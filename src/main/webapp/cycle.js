var Cycle = function() {
    var me = this;
    this.mapBoxToken = 'pk.eyJ1IjoibWFudWVsYjg2IiwiYSI6ImNqaXNydnhoZjIwYW4zcHA4cjV1OTJlbWUifQ.dvyaW4atK652Sor1113oDg';
    this.initMapBox();
};
Cycle.prototype.initMapBox = function() {
    mapboxgl.accessToken = this.mapBoxToken;
    this.map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/dark-v9',
        center: [7.589907, 50.360023],
        zoom: 10
    });
    var me = this;
    this.map.on('load', function() {
        me.onMapLoad();
    });
};
Cycle.prototype.onMapLoad = function() {
};
