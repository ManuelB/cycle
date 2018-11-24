package com.airhacks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("route")
public class RouteResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String markers() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/markers.geojson")))) {
			String line;
			String result = "";
			while ((line = br.readLine()) != null)
			{
				result += line;
			}
			return result;
		} catch (IOException e) {
			// Do nothing for now
		}
		return "{}";
	}
}
