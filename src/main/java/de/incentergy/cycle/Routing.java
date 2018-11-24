package de.incentergy.cycle;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

@Stateless
@Path("routing")
public class Routing {
	
	@PersistenceContext
	EntityManager em;
	
	GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
	
	/**
	 * http://localhost:8080/cycle/resources/routing/route?startLon=213&startLat=123&endLon=123213&endLat=3123
	 * @param startLon
	 * @param startLat
	 * @param endLon
	 * @param endLat
	 * @return
	 */
	@GET
	@Path("route")
	public String route(@QueryParam("startLon") Double startLon, @QueryParam("startLat") Double startLat, @QueryParam("endLon") Double endLon, @QueryParam("endLat") Double endLat) {
		Point start = geometryFactory.createPoint(new Coordinate(startLon, startLat));
		Point end = geometryFactory.createPoint(new Coordinate(endLon, endLat));
		
		BigInteger startId = (BigInteger) em.createNativeQuery("SELECT id FROM ways_vertices_pgr ORDER BY the_geom <-> :start LIMIT 1").setParameter("start", start).getSingleResult();
		BigInteger endId = (BigInteger) em.createNativeQuery("SELECT id FROM ways_vertices_pgr ORDER BY the_geom <-> :end LIMIT 1").setParameter("end", end).getSingleResult();
		
		List<Object> result = em.createNativeQuery("SELECT seq, '(' || start_vid || ',' || end_vid || ')' AS path_name,\n" + 
				"	                    path_seq AS _path_seq, start_vid AS _start_vid, end_vid AS _end_vid,\n" + 
				"	                    node AS _node, edge AS _edge, cost AS _cost, lead(agg_cost) over() AS _agg_cost\n" + 
				"	                FROM pgr_dijkstra('\n" + 
				"	                    SELECT gid AS id,\n" + 
				"	                        source AS source,\n" + 
				"	                        target AS target,\n" + 
				"	                        cost AS cost\n" + 
				"	                         \n" + 
				"	                    FROM ways\n" + 
				"	                     ',\n" + 
				"	                    array[:startId]\\:\\:BIGINT[], array[:endId]\\:\\:BIGINT[], false)").setParameter("startId", startId).setParameter("endId", endId).getResultList();
		
		return "{\n" + 
				"   \"type\": \"FeatureCollection\",\n" + 
				"   \"features\": [\n" + 
				"       {\n" + 
				"           \"type\": \"Feature\",\n" + 
				"           \"id\": \"id0\",\n" + 
				"           \"geometry\": {\n" + 
				"               \"type\": \"LineString\",\n" + 
				"               \"coordinates\": [\n" + 
				"                   [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]\n" + 
				"               ]\n" + 
				"           },\n" + 
				"           \"properties\": {\n" + 
				"               \"prop0\": \"value0\",\n" + 
				"               \"prop1\": \"value1\"\n" + 
				"           }\n" + 
				"       },\n" + 
				"       {\n" + 
				"           \"type\": \"Feature\",\n" + 
				"           \"id\": \"id1\",\n" + 
				"           \"geometry\": {\n" + 
				"               \"type\": \"Polygon\",\n" + 
				"               \"coordinates\": [\n" + 
				"                   [\n" + 
				"                       [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]\n" + 
				"                   ]\n" + 
				"               ]\n" + 
				"           },\n" + 
				"           \"properties\": {\n" + 
				"               \"prop0\": \"value0\",\n" + 
				"               \"prop1\": \"value1\"\n" + 
				"           }\n" + 
				"       }\n" + 
				"   ]\n" + 
				"}";
	}
}
