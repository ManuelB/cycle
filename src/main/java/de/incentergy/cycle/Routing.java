package de.incentergy.cycle;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	 * 
	 * @param startLon
	 * @param startLat
	 * @param endLon
	 * @param endLat
	 * @return
	 */
	@GET
	@Path("route")
	public String route(@QueryParam("startLon") Double startLon, @QueryParam("startLat") Double startLat,
			@QueryParam("endLon") Double endLon, @QueryParam("endLat") Double endLat, @QueryParam("route") String route) {
		Point start = geometryFactory.createPoint(new Coordinate(startLon, startLat));
		Point end = geometryFactory.createPoint(new Coordinate(endLon, endLat));

		BigInteger startId = (BigInteger) em
				.createNativeQuery("SELECT id FROM ways_vertices_pgr ORDER BY the_geom <-> :start LIMIT 1")
				.setParameter("start", start).getSingleResult();
		BigInteger endId = (BigInteger) em
				.createNativeQuery("SELECT id FROM ways_vertices_pgr ORDER BY the_geom <-> :end LIMIT 1")
				.setParameter("end", end).getSingleResult();

		return routeVertex(startId, endId, route);
	}

	@GET
	@Path("routeVertex")
	public String routeVertex(@QueryParam("startId") BigInteger startId, @QueryParam("endId") BigInteger endId, @QueryParam("route") String route) {
		String query;
		if(route.equals("safe")) {
			query = query = "SELECT  ST_AsGeoJSON(w.the_geom) /*, seq, '(' || start_vid || ',' || end_vid || ')' AS path_name,\n"
					+ "	                    path_seq AS _path_seq, start_vid AS _start_vid, end_vid AS _end_vid,\n"
					+ "	                    node AS _node, edge AS _edge, pgr.cost AS _cost, lead(agg_cost) over() AS _agg_cost*/\n"
					+ "	                FROM pgr_dijkstra('\n" + "	                    SELECT gid AS id,\n"
					+ "	                        source AS source,\n" + "	                        target AS target,\n"
					+ "	                        cost * (accidents+1) AS cost\n" + "	                         \n"
					+ "	                    FROM ways\n" + "	                     ',\n"
					+ "	                    array[:startId]\\:\\:BIGINT[], array[:endId]\\:\\:BIGINT[], false) as pgr, ways w WHERE pgr.edge = w.gid";;
		} else {
			query = "SELECT  ST_AsGeoJSON(w.the_geom) /*, seq, '(' || start_vid || ',' || end_vid || ')' AS path_name,\n"
				+ "	                    path_seq AS _path_seq, start_vid AS _start_vid, end_vid AS _end_vid,\n"
				+ "	                    node AS _node, edge AS _edge, pgr.cost AS _cost, lead(agg_cost) over() AS _agg_cost*/\n"
				+ "	                FROM pgr_dijkstra('\n" + "	                    SELECT gid AS id,\n"
				+ "	                        source AS source,\n" + "	                        target AS target,\n"
				+ "	                        cost AS cost\n" + "	                         \n"
				+ "	                    FROM ways\n" + "	                     ',\n"
				+ "	                    array[:startId]\\:\\:BIGINT[], array[:endId]\\:\\:BIGINT[], false) as pgr, ways w WHERE pgr.edge = w.gid";
		}
		List<Object> result = em.createNativeQuery(query).setParameter("startId", startId).setParameter("endId", endId)
				.getResultList();
		String joinedGeometries = result.stream()
				.map((Object row) -> ((row instanceof Object[]) ? ((Object[]) row)[0] : row)).map(row -> row.toString())
				.collect(Collectors.joining(", "));
		return "{\n" + "   \"type\": \"GeometryCollection\",\n" + "   \"geometries\": [" + joinedGeometries + "]" + "}";
	}
}
