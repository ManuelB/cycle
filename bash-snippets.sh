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
