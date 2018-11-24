var Cycle = function() {
    var me = this;
    this.mapBoxToken = 'pk.eyJ1IjoibWFudWVsYjg2IiwiYSI6ImNqaXNydnhoZjIwYW4zcHA4cjV1OTJlbWUifQ.dvyaW4atK652Sor1113oDg';
    this.start = null;
    this.end = null;
    this.startMarker = null;
    this.endMarker = null;
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
    this.routeTypes = ["safe", "normal"];
    var me = this;
    this.map.on('load', function() {
        me.onMapLoad();
    });
    this.map.on('click', function(e) {
    	me.onMapClick(e);
    });
    this.map.on('touchend', function(e) {
    	me.onMapClick(e);
    });
};
Cycle.prototype.onMapLoad = function() {
	var me = this;
	
	// Do nothing for now
};
Cycle.prototype.onMapClick = function(e) {
	var me = this;
	if (this.start === null) {
		this.start = e.lngLat;
		me.addStartMarker();
	} else {
		this.end = e.lngLat;
		me.addEndMarker();
		me.loadRouteLayer();
		this.start = null;
		this.end = null;
	}
};
Cycle.prototype.addStartMarker = function() {
	var me = this;
	if (this.startMarker !== null) {
		this.startMarker.remove();
		this.endMarker.remove();
	}
	
	this.startMarker = new mapboxgl.Marker({draggable: true})
		.setLngLat(this.start).addTo(this.map);
	
	marker.on('dragend', me.loadRouteLayer());
};
Cycle.prototype.addEndMarker = function() {
	this.endMarker = new mapboxgl.Marker({draggable: true})
		.setLngLat(this.end).addTo(this.map);
	
	marker.on('dragend', me.loadRouteLayer());
};
Cycle.prototype.loadRouteLayer = function() {
	var me = this;
	if(this.start && this.end) {
		for(var property in this.routeTypes) {
			var routeType = this.routeTypes[property];
			if(this.map.getLayer("route-layer-"+routeType)) {
				this.map.removeLayer("route-layer-"+routeType);
			}
			if(this.map.getSource('route-source-'+routeType)) {			
				this.map.removeSource("route-source-"+routeType);
			}
			this.map.addSource('route-source-'+routeType, {
				type: 'geojson',
				data: '/cycle/resources/routing/route?startLon=' + this.start.lng + '&startLat=' + this.start.lat + '&endLon=' + this.end.lng + '&endLat=' + this.end.lat+"&route="+routeType
			});
		    this.map.addLayer({
		        "id": "route-layer-"+routeType,
		        "type": "line",
		        "source": "route-source-"+routeType,
		        "layout": {
		            "line-join": "round",
		            "line-cap": "round"
		        },
		        "paint": {
		        	"line-color": routeType == "safe" ? "#0000ff" : "#ffff00",
		        	"line-width": 4
		        }
		    });
		}
	}
};

var cycle;
document.addEventListener("DOMContentLoaded", function(event) {
    cycle = new Cycle();
});