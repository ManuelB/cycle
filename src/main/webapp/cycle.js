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
        zoom: 12
    });
    var me = this;
    this.map.on('load', function() {
        me.onMapLoad();
    });
};
Cycle.prototype.onMapLoad = function() {
	var me = this;
	this.map.addSource('route', {
		type: 'geojson',
		data: 'http://localhost:8080/cycle/resources/route'
	});
    this.map.addLayer({
        "id": "route",
        "type": "line",
        "source": "route",
        "layout": {
            "line-join": "round",
            "line-cap": "round"
        },
        "paint": {
        	"line-color": "#ff0000",
        	"line-width": 4
        }
    });
};

var cycle;
document.addEventListener("DOMContentLoaded", function(event) {
    cycle = new Cycle();
});