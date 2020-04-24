# GeoPackage Java

#### GeoPackage Java Lib ####

The [GeoPackage Libraries](http://ngageoint.github.io/GeoPackage/) were developed at the [National Geospatial-Intelligence Agency (NGA)](http://www.nga.mil/) in collaboration with [BIT Systems](http://www.bit-sys.com/). The government has "unlimited rights" and is releasing this software to increase the impact of government investments by providing developers with the opportunity to take things in new directions. The software use, modification, and distribution rights are stipulated within the [MIT license](http://choosealicense.com/licenses/mit/).

### Pull Requests ###
If you'd like to contribute to this project, please make a pull request. We'll review the pull request and discuss the changes. All pull request contributions to this project will be released under the MIT license.

Software source code previously released under an open source license and then modified by NGA staff is considered a "joint work" (see 17 USC § 101); it is partially copyrighted, partially public domain, and as a whole is protected by the copyrights of the non-government authors and must be released according to the terms of the original open source license.

### About ###

[GeoPackage](http://ngageoint.github.io/geopackage-java/) is a [GeoPackage Library](http://ngageoint.github.io/GeoPackage/) Java implementation of the Open Geospatial Consortium [GeoPackage](http://www.geopackage.org/) [spec](http://www.geopackage.org/spec/).  It is listed as an [OGC GeoPackage Implementation](http://www.geopackage.org/#implementations_nga) by the National Geospatial-Intelligence Agency.

<a href='http://www.opengeospatial.org/resource/products/details/?pid=1625'>
    <img src="https://github.com/ngageoint/GeoPackage/raw/master/docs/images/ogc.gif" height=50>
</a>

The GeoPackage Java library provides the ability to read, create, and edit GeoPackage files. GeoPackage tiles can be imported from or exported to a file system folder structure.

### Usage ###

View the latest [Javadoc](http://ngageoint.github.io/geopackage-java/docs/api/)

```java
// File newGeoPackage = ...;
// File existingGeoPackage = ...;

// Create a new GeoPackage
boolean created = GeoPackageManager.create(newGeoPackage);

// Open a GeoPackage
GeoPackage geoPackage = GeoPackageManager.open(existingGeoPackage);

// GeoPackage Table DAOs
SpatialReferenceSystemDao srsDao = geoPackage.getSpatialReferenceSystemDao();
ContentsDao contentsDao = geoPackage.getContentsDao();
GeometryColumnsDao geomColumnsDao = geoPackage.getGeometryColumnsDao();
TileMatrixSetDao tileMatrixSetDao = geoPackage.getTileMatrixSetDao();
TileMatrixDao tileMatrixDao = geoPackage.getTileMatrixDao();
DataColumnsDao dataColumnsDao = geoPackage.getDataColumnsDao();
DataColumnConstraintsDao dataColumnConstraintsDao = geoPackage.getDataColumnConstraintsDao();
MetadataDao metadataDao = geoPackage.getMetadataDao();
MetadataReferenceDao metadataReferenceDao = geoPackage.getMetadataReferenceDao();
ExtensionsDao extensionsDao = geoPackage.getExtensionsDao();

// Feature and tile tables
List<String> features = geoPackage.getFeatureTables();
List<String> tiles = geoPackage.getTileTables();

// Query Features
FeatureDao featureDao = geoPackage.getFeatureDao(features.get(0));
FeatureResultSet featureResultSet = featureDao.queryForAll();
try{
    while(featureResultSet.moveToNext()){
        FeatureRow featureRow = featureResultSet.getRow();
        GeoPackageGeometryData geometryData = featureRow.getGeometry();
        Geometry geometry = geometryData.getGeometry();
        // ...
    }
}finally{
    featureResultSet.close();
}

// Query Tiles
TileDao tileDao = geoPackage.getTileDao(tiles.get(0));
TileResultSet tileResultSet = tileDao.queryForAll();
try{
    while(tileResultSet.moveToNext()){
        TileRow tileRow = tileResultSet.getRow();
        byte[] tileBytes = tileRow.getTileData();
        // ...
    }
}finally{
    tileResultSet.close();
}

// Index Features
FeatureTableIndex indexer = new FeatureTableIndex(geoPackage, featureDao);
int indexedCount = indexer.index();

// Close database when done
geoPackage.close();
```

### Installation ###

Pull from the [Maven Central Repository](http://search.maven.org/#artifactdetails|mil.nga.geopackage|geopackage|3.5.0|jar) (JAR, POM, Source, Javadoc)

```xml
<dependency>
    <groupId>mil.nga.geopackage</groupId>
    <artifactId>geopackage</artifactId>
    <version>3.5.0</version>
</dependency>
```

### Build ###

[![Build & Test](https://github.com/ngageoint/geopackage-java/workflows/Build%20&%20Test/badge.svg)](https://github.com/ngageoint/geopackage-java/actions?query=workflow%3A%22Build+%26+Test%22)

Build this repository using Eclipse and/or Maven:

    mvn clean install

### Standalone Utilities ###

The jar can be built as standalone (or combined with required dependency jars) to run utilities from the command line.

To build the jar into a standalone jar that includes all dependencies:

    mvn clean install -Pstandalone

#### SQL Exec ####

Executes SQL statements on a SQLite database, including GeoPackages. Download [sqlite-exec.zip](https://github.com/ngageoint/geopackage-java/releases/latest/download/sqlite-exec.zip) and follow the [instructions](script/sqlite-exec/).

Or run against the jar:

    java -jar geopackage-*standalone.jar [-m max_rows] sqlite_file [sql]

#### Tile Writer ####

The tile writer writes tiles from a GeoPackage tile table to the file system.  Images are saved as raw bytes or as a specified format in a z/x/y.ext folder & file structure formatted as GeoPackage, XYZ, or TMS (Tile Map Service).  The GeoPackage format writes a tiles.properties file in the base imagery directory.  The mil.nga.geopackage.io.TileWriter functionality is invokable through code or command line.

To run against the jar:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.TileWriter [-t tile_type] [-i image_format] [-r] geopackage_file tile_table output_directory

Example:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.TileWriter -t tms /path/geopackage.gpkg mytiletable /path/tiles/mytiles

#### Tile Reader ####

The tile reader reads tile images from the file system and saves them into a new or existing GeoPackage in a new tile table. Images structured in a z/x/y.ext folder & file structure formatted as GeoPackage, XYZ, or TMS (Tile Map Service) are saved as raw bytes or as a specified format in a GeoPackage.  The GeoPackage format requires a tiles.properties file in the base imagery directory.  The mil.nga.geopackage.io.TileReader functionality is invokable through code or command line.

To run against the jar:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.TileReader [-i image_format] [-r] input_directory tile_type geopackage_file tile_table

Example:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.TileReader -i png /path/tiles/mytiles xyz /path/geopackage.gpkg mytiletable

GeoPackage Format tiles.properties:

    epsg=
    min_x=
    max_x=
    min_y=
    max_y=
    # Zoom Level Properties:
    # If the file structure is fully populated and represents the matrix width and height, the properties can be omitted
    # If a non top zoom level matrix width and height increase by a factor of 2 with each zoom level, the properties can be omitted for those zoom levels
    zoom_level.{zoom}.matrix_width=
    zoom_level.{zoom}.matrix_height=

#### URL Tile Generator ####

The URL tile generator creates a set of tiles within a GeoPackage by downloading tiles from a tile server using a URL pattern. The URL can contain XYZ, TMS, or WMS substitution variables. Tiles are downloaded from the specified zoom range and the optional bounding box location. Tiles can be compressed into a specified format and quality.

To run against the jar:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.URLTileGen [-f compress_format] [-q compress_quality] [-xyz] [-bbox minLon,minLat,maxLon,maxLat] [-epsg epsg] [-uepsg url_epsg] [-tms] [-replace] [-logCount count] [-logTime time] geopackage_file tile_table url min_zoom max_zoom

Examples:

Note: URLs may need to be encoded

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.URLTileGen -bbox -105.0,39.0,-104.0,40.0 -uepsg 3857 /path/geopackage.gpkg mytiletable http://url/{z}/{x}/{y} 2 18

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.URLTileGen -epsg 3857 -bbox -9136400.0,2790700.0,-9068470.0,2844260.0 /path/geopackage.gpkg mytiletable https://url?service=WMS&request=GetMap&layers=layer&styles=&format=image/png&transparent=true&version=1.3.0&width=256&height=256&crs=EPSG:3857&bbox={minLon},{minLat},{maxLon},{maxLat} 2 18

#### Feature Tile Generator ####

The Feature tile generator creates a set of tiles within a GeoPackage by drawing the tiles from a feature table in the same or different GeoPackage. The input feature table is [indexed](http://ngageoint.github.io/GeoPackage/docs/extensions/geometry-index.html) if not already done or current. Tiles are drawn from the specified zoom range and the optional bounding box location. Tiles can be compressed into a specified format and quality. The tile size and style can be specified including point (radius, color, icon), line (stroke width, color), and polygon (stroke width, color, fill, fill color) attributes.

To run against the jar:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.FeatureTileGen [-m max_features_per_tile] [-f compress_format] [-q compress_quality] [-xyz] [-bbox minLon,minLat,maxLon,maxLat] [-epsg epsg] [-tileWidth width] [-tileHeight height] [-tileScale scale] [-pointRadius radius] [-pointColor color] [-pointIcon image_file] [-iconWidth width] [-iconHeight height] [-centerIcon] [-lineStrokeWidth stroke_width] [-lineColor color] [-polygonStrokeWidth stroke_width] [-polygonColor color] [-fillPolygon] [-polygonFillColor color] [-simplifyGeometries true|false] [-ignoreGeoPackageStyles true|false] [-logCount count] [-logTime time] feature_geopackage_file feature_table tile_geopackage_file tile_table min_zoom max_zoom

Example:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.FeatureTileGen -bbox -105.0,39.0,-104.0,40.0 -pointRadius 3.0 -pointColor magenta -lineStrokeWidth 2.5 -lineColor red -polygonStrokeWidth 1.5 -polygonColor 0,0,255 -fillPolygon -polygonFillColor 0,255,0,80 /path/geopackage1.gpkg myfeaturetable /path/geopackage2.gpkg mytiletable 2 18

#### OGC API Features Generator ####

The OGC API feature generator creates features within a GeoPackage by requesting from an OGC API server. Features are downloaded from the server using a specified collection id.

To run against the jar:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.OAPIFeatureGen [-limit limit] [-bbox minLon,minLat,maxLon,maxLat] [-bbox-proj authority,code] [-time time] [-proj authority,code] [-totalLimit total_limit] [-transactionLimit transaction_limit] [-logCount count] [-logTime time] geopackage_file table_name server_url collection_id

Example:

    java -classpath geopackage-*standalone.jar mil.nga.geopackage.io.OAPIFeatureGen -limit 1000 -bbox 20.0,60.0,22.0,62.0 -time 20190519T140000/20190619T140000 -totalLimit 10000 -transactionLimit 100 /path/geopackage.gpkg myfeaturetable http://url collectionId

### Dependencies ###

#### Remote ####

* [GeoPackage Core Java](https://github.com/ngageoint/geopackage-core-java) (The MIT License (MIT)) - GeoPackage Library
* [TIFF](https://github.com/ngageoint/tiff-java) (The MIT License (MIT)) - Tagged Image File Format Lib
* [OrmLite](http://ormlite.com/) (Open Source License) - Object Relational Mapping (ORM) Library
* [SQLite JDBC](https://bitbucket.org/xerial/sqlite-jdbc) (Apache License, Version 2.0) - SQLiteJDBC library

#### Embedded ####

* [The Android Open Source Project](https://source.android.com/) (Apache License, Version 2.0) - Slightly modified subset of SQLiteQueryBuilder
