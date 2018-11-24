# Install from PostgreSQL repositories
# https://www.postgresql.org/download/linux/ubuntu/
sudo apt install osm2pgrouting postgresql-10-pgrouting postgresql-10-pgrouting-doc postgresql-10-pgrouting-scripts postgis

shp2pgsql -s 31466 UnfälleRLP2017_GK.shp unfaelle > unfaelle.sql
psql -d cycle -U postgres -p 5434 -W -f unfaelle.sql

# Import takes around 30 minutes
osm2pgrouting --f rheinland-pfalz-latest.osm --dbname cycle --username postgres --port 5434 -W password --clean

git clone https://github.com/ManuelB/cycle.git
cd cycle/
mvn archetype:generate -DarchetypeGroupId=com.airhacks -DarchetypeArtifactId=javaee8-essentials-archetype -DarchetypeVersion=0.0.1

alter table ways add column buffer geometry;
# add gist index to buffer
UPDATE ways SET buffer = ST_BUFFER(the_geom, 0.0001);
#UPDATE 1261404
#Query returned successfully in 2 min 12 secs.
SELECT UpdateGeometrySRID('unfaelle','geom',4326);

SELECT COUNT(*) from ways, unfaelle WHERE ST_CONTAINS(ways.buffer, unfaelle.point_wgs84) GROUP BY ways.gid
INSERT INTO ways_accidents SELECT ways.gid as gid, COUNT(*) as accidents from ways, unfaelle WHERE ST_CONTAINS(ways.buffer, unfaelle.point_wgs84) GROUP BY ways.gid;
UPDATE ways SET accidents = ways_accidents.accidents FROM ways_accidents WHERE ways.gid = ways_accidents.way_gid;
