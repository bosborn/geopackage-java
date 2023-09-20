package mil.nga.geopackage.dgiwg;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.TestConstants;
import mil.nga.geopackage.TestUtils;
import mil.nga.geopackage.db.GeoPackageDataType;
import mil.nga.geopackage.extension.coverage.CoverageDataTiff;
import mil.nga.geopackage.extension.coverage.GriddedCoverage;
import mil.nga.geopackage.extension.coverage.GriddedCoverageDao;
import mil.nga.geopackage.extension.coverage.GriddedCoverageDataType;
import mil.nga.geopackage.extension.coverage.GriddedCoverageEncodingType;
import mil.nga.geopackage.extension.coverage.GriddedTile;
import mil.nga.geopackage.extension.coverage.GriddedTileDao;
import mil.nga.geopackage.extension.metadata.Metadata;
import mil.nga.geopackage.extension.metadata.MetadataDao;
import mil.nga.geopackage.extension.metadata.MetadataExtension;
import mil.nga.geopackage.extension.metadata.MetadataScopeType;
import mil.nga.geopackage.extension.metadata.reference.MetadataReference;
import mil.nga.geopackage.extension.metadata.reference.MetadataReferenceDao;
import mil.nga.geopackage.extension.metadata.reference.ReferenceScopeType;
import mil.nga.geopackage.extension.related.ExtendedRelation;
import mil.nga.geopackage.extension.related.RelatedTablesExtension;
import mil.nga.geopackage.extension.related.UserMappingDao;
import mil.nga.geopackage.extension.related.UserMappingRow;
import mil.nga.geopackage.extension.related.UserMappingTable;
import mil.nga.geopackage.extension.related.dublin.DublinCoreMetadata;
import mil.nga.geopackage.extension.related.dublin.DublinCoreType;
import mil.nga.geopackage.extension.related.media.MediaDao;
import mil.nga.geopackage.extension.related.media.MediaRow;
import mil.nga.geopackage.extension.related.media.MediaTable;
import mil.nga.geopackage.extension.related.media.MediaTableMetadata;
import mil.nga.geopackage.extension.schema.SchemaExtension;
import mil.nga.geopackage.extension.schema.columns.DataColumns;
import mil.nga.geopackage.extension.schema.columns.DataColumnsDao;
import mil.nga.geopackage.extension.schema.constraints.DataColumnConstraintType;
import mil.nga.geopackage.extension.schema.constraints.DataColumnConstraints;
import mil.nga.geopackage.extension.schema.constraints.DataColumnConstraintsDao;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.features.user.FeatureColumn;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureResultSet;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.io.GeoPackageIOUtils;
import mil.nga.geopackage.srs.SpatialReferenceSystemDao;
import mil.nga.geopackage.tiles.ImageUtils;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.geopackage.tiles.TileGrid;
import mil.nga.geopackage.tiles.matrix.TileMatrix;
import mil.nga.geopackage.tiles.matrix.TileMatrixDao;
import mil.nga.geopackage.tiles.matrixset.TileMatrixSet;
import mil.nga.geopackage.tiles.user.TileDao;
import mil.nga.geopackage.tiles.user.TileResultSet;
import mil.nga.geopackage.tiles.user.TileRow;
import mil.nga.geopackage.tiles.user.TileTableMetadata;
import mil.nga.geopackage.user.custom.UserCustomColumn;
import mil.nga.proj.ProjectionConstants;
import mil.nga.sf.GeometryType;
import mil.nga.sf.LineString;
import mil.nga.sf.Point;
import mil.nga.sf.Polygon;
import mil.nga.sf.proj.GeometryTransform;

/**
 * Creates an example DGIWG GeoPackage file
 * 
 * @author osbornb
 */
public class DGIWGExample {

	private static final boolean FEATURES = true;
	private static final boolean TILES = true;
	private static final boolean NATIONAL_METADATA = true;
	private static final boolean FEATURES_METADATA = true;
	private static final boolean TILES_METADATA = true;
	private static final boolean SCHEMA = true;
	private static final boolean COVERAGE_DATA = true;
	private static final boolean RELATED_TABLES_MEDIA = true;
	private static final boolean RELATED_TABLES_TILES = true;

	private static final String PRODUCER = "NGA";
	private static final String DATA_PRODUCT = "DGIWG-Example";
	private static final String GEOGRAPHIC_COVERAGE_AREA = "Ft-Belvoir";
	private static final int MIN_ZOOM = 15;
	private static final int MAX_ZOOM = 16;
	private static final int MAJOR_VERSION = 1;
	private static final int MINOR_VERSION = 0;

	private static final String FEATURE_TABLE = "nga_features";
	private static final String FEATURE_IDENTIFIER = "NGA Features";
	private static final String FEATURE_DESCRIPTION = "DGIWG Features example";
	private static final String FEATURE_NAME_COLUMN = "name";
	private static final String FEATURE_NUMBER_COLUMN = "number";

	private static final String TILE_TABLE = "nga_tiles";
	private static final String TILE_IDENTIFIER = "NGA Tiles";
	private static final String TILE_DESCRIPTION = "DGIWG Tiles example";

	private static final String COVERAGE_DATA_TABLE = "nga_coverage_data";
	private static final String COVERAGE_DATA_IDENTIFIER = "NGA Coverage Data";
	private static final String COVERAGE_DATA_DESCRIPTION = "DGIWG Coverage Data example";

	private static final String MEDIA_TABLE = "media";
	private static final String MEDIA_MAPPING_TABLE = FEATURE_TABLE + "_"
			+ MEDIA_TABLE;

	private static final String TILE_MAPPING_TABLE = FEATURE_TABLE + "_"
			+ "tiles";

	/**
	 * Main method to create the GeoPackage example file
	 * 
	 * @param args
	 *            arguments
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	public static void main(String[] args) throws IOException, SQLException {

		DGIWGExampleCreate create = DGIWGExampleCreate.base();
		create.features = FEATURES;
		create.tiles = TILES;
		create.nationalMetadata = NATIONAL_METADATA;
		create.featuresMetadata = FEATURES_METADATA;
		create.tilesMetadata = TILES_METADATA;
		create.schema = SCHEMA;
		create.coverage = COVERAGE_DATA;
		create.relatedMedia = RELATED_TABLES_MEDIA;
		create.relatedTiles = RELATED_TABLES_TILES;

		create(getFileName(), create);
	}

	/**
	 * Get the file name
	 * 
	 * @return file name
	 */
	public static GeoPackageFileName getFileName() {

		GeoPackageFileName fileName = new GeoPackageFileName();

		fileName.setProducer(PRODUCER);
		fileName.setDataProduct(DATA_PRODUCT);
		fileName.setGeographicCoverageArea(GEOGRAPHIC_COVERAGE_AREA);
		fileName.setZoomLevelRange(MIN_ZOOM, MAX_ZOOM);
		fileName.setVersion(MAJOR_VERSION, MINOR_VERSION);
		fileName.setCreationDate(new Date());

		return fileName;
	}

	/**
	 * Test making the base GeoPackage example
	 * 
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testExampleBase() throws IOException, SQLException {
		testExample(DGIWGExampleCreate.base());
	}

	/**
	 * Test making the GeoPackage example with all parts
	 * 
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testExample() throws IOException, SQLException {
		testExample(DGIWGExampleCreate.all());
	}

	/**
	 * Test making the GeoPackage example with features and tiles
	 * 
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testExampleFeaturesAndTiles() throws IOException, SQLException {
		testExample(DGIWGExampleCreate.featuresAndTiles());
	}

	/**
	 * Test making the GeoPackage example with features
	 * 
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testExampleFeatures() throws IOException, SQLException {
		testExample(DGIWGExampleCreate.features());
	}

	/**
	 * Test making the GeoPackage example with tiles
	 * 
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testExampleTiles() throws IOException, SQLException {
		testExample(DGIWGExampleCreate.tiles());
	}

	/**
	 * Test making the GeoPackage example
	 * 
	 * @param create
	 *            create parts
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	private void testExample(DGIWGExampleCreate create)
			throws IOException, SQLException {

		GeoPackageFileName fileName = getFileName();

		GeoPackageFile file = create(fileName, create);

		GeoPackage geoPackage = DGIWGGeoPackageManager.open(file);
		TestCase.assertNotNull(geoPackage);
		geoPackage.close();

		TestCase.assertTrue(file.getFile().delete());
	}

	/**
	 * Create the GeoPackage example file
	 * 
	 * @param fileName
	 *            file name
	 * @param create
	 *            create parts
	 * @return GeoPackage file
	 * @throws IOException
	 *             upon error
	 * @throws SQLException
	 *             upon error
	 */
	private static GeoPackageFile create(GeoPackageFileName fileName,
			DGIWGExampleCreate create) throws IOException, SQLException {

		System.out.println("Creating: " + fileName.getName());
		DGIWGGeoPackage geoPackage = createGeoPackage(fileName);

		System.out.println(
				"National Metadata Extension: " + create.nationalMetadata);
		if (create.nationalMetadata) {
			createNationalMetadataExtension(geoPackage);
		}

		System.out.println("Features: " + create.features);
		if (create.features) {

			createFeatures(geoPackage);

			System.out.println(
					"Features Metadata Extension: " + create.featuresMetadata);
			if (create.featuresMetadata) {
				createFeaturesMetadataExtension(geoPackage);
			}

			System.out.println("Schema Extension: " + create.schema);
			if (create.schema) {
				createSchemaExtension(geoPackage);
			}

			System.out.println(
					"Related Tables Media Extension: " + create.relatedMedia);
			if (create.relatedMedia) {
				createRelatedTablesMediaExtension(geoPackage);
			}

		} else {
			System.out
					.println("Features Metadata Extension: " + create.features);
			System.out.println("Schema Extension: " + create.features);
			System.out.println(
					"Related Tables Media Extension: " + create.features);
		}

		System.out.println("Tiles: " + create.tiles);
		if (create.tiles) {

			createTiles(geoPackage);

			System.out.println(
					"Tiles Metadata Extension: " + create.tilesMetadata);
			if (create.tilesMetadata) {
				createTilesMetadataExtension(geoPackage);
			}

			System.out.println("Coverage Data: " + create.coverage);
			if (create.coverage) {
				createCoverageDataExtension(geoPackage);
			}

			boolean relatedTablesTiles = create.relatedTiles && create.features;
			System.out.println(
					"Related Tables Tiles Extension: " + relatedTablesTiles);
			if (relatedTablesTiles) {
				createRelatedTablesTilesExtension(geoPackage);
			}

		} else {
			System.out.println("Tiles Metadata Extension: " + create.tiles);
			System.out.println("Coverage Data: " + create.tiles);
			System.out
					.println("Related Tables Tiles Extension: " + create.tiles);
		}

		DGIWGValidationErrors errors = geoPackage.validate();
		if (errors.hasErrors()) {
			System.out.println(errors);
		}
		assertTrue(geoPackage.isValid());

		geoPackage.close();
		System.out.println("Created: " + geoPackage.getPath());

		return geoPackage.getFile();
	}

	/**
	 * Create the GeoPackage
	 * 
	 * @param fileName
	 *            file name
	 * @return GeoPackage
	 * @throws IOException
	 *             upon error
	 */
	private static DGIWGGeoPackage createGeoPackage(GeoPackageFileName fileName)
			throws IOException {

		File file = fileName.getFile();
		if (file.exists()) {
			file.delete();
		}

		GeoPackageFile geoPackageFile = DGIWGGeoPackageManager.create(fileName,
				getMetadata(TestConstants.DGIWG_METADATA_1));

		DGIWGGeoPackage geoPackage = DGIWGGeoPackageManager
				.open(geoPackageFile);

		return geoPackage;
	}

	/**
	 * Get the example metadata
	 * 
	 * @param name
	 *            metadata name
	 * @return metadata
	 * @throws IOException
	 *             upon error
	 */
	private static String getMetadata(String name) throws IOException {
		File metadataFile = TestUtils.getTestFile(name);
		String metadata = GeoPackageIOUtils.fileString(metadataFile);
		return metadata;
	}

	/**
	 * Create features
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 */
	private static void createFeatures(DGIWGGeoPackage geoPackage) {

		CoordinateReferenceSystem crs = CoordinateReferenceSystem.EPSG_4326;

		List<FeatureColumn> columns = new ArrayList<>();
		columns.add(FeatureColumn.createColumn(FEATURE_NAME_COLUMN,
				GeoPackageDataType.TEXT));
		columns.add(FeatureColumn.createColumn(FEATURE_NUMBER_COLUMN,
				GeoPackageDataType.INTEGER));
		GeometryColumns geometryColumns = geoPackage.createFeatures(
				FEATURE_TABLE, FEATURE_IDENTIFIER, FEATURE_DESCRIPTION,
				GeometryType.GEOMETRY, columns, crs);
		long srsId = geometryColumns.getSrsId();

		FeatureDao featureDao = geoPackage.getFeatureDao(geometryColumns);

		Point point = new Point(-77.196736, 38.753370);
		FeatureRow pointRow = featureDao.newRow();
		pointRow.setGeometry(GeoPackageGeometryData.create(srsId, point));
		pointRow.setValue(FEATURE_NAME_COLUMN, "NGA");
		pointRow.setValue(FEATURE_NUMBER_COLUMN, 1);
		featureDao.insert(pointRow);

		LineString line = new LineString();
		line.addPoint(new Point(-77.196650, 38.756501));
		line.addPoint(new Point(-77.196414, 38.755979));
		line.addPoint(new Point(-77.195518, 38.755208));
		line.addPoint(new Point(-77.195303, 38.755272));
		line.addPoint(new Point(-77.195351, 38.755459));
		line.addPoint(new Point(-77.195863, 38.755697));
		line.addPoint(new Point(-77.196328, 38.756069));
		line.addPoint(new Point(-77.196568, 38.756526));
		FeatureRow lineRow = featureDao.newRow();
		lineRow.setGeometry(GeoPackageGeometryData.create(srsId, line));
		lineRow.setValue(FEATURE_NAME_COLUMN, "NGA Visitor Center Road");
		lineRow.setValue(FEATURE_NUMBER_COLUMN, 2);
		featureDao.insert(lineRow);

		Polygon polygon = new Polygon();
		LineString ring = new LineString();
		ring.addPoint(new Point(-77.195299, 38.755159));
		ring.addPoint(new Point(-77.195203, 38.755080));
		ring.addPoint(new Point(-77.195410, 38.754930));
		ring.addPoint(new Point(-77.195350, 38.754884));
		ring.addPoint(new Point(-77.195228, 38.754966));
		ring.addPoint(new Point(-77.195135, 38.754889));
		ring.addPoint(new Point(-77.195048, 38.754956));
		ring.addPoint(new Point(-77.194986, 38.754906));
		ring.addPoint(new Point(-77.194897, 38.754976));
		ring.addPoint(new Point(-77.194953, 38.755025));
		ring.addPoint(new Point(-77.194763, 38.755173));
		ring.addPoint(new Point(-77.194827, 38.755224));
		ring.addPoint(new Point(-77.195012, 38.755082));
		ring.addPoint(new Point(-77.195041, 38.755104));
		ring.addPoint(new Point(-77.195028, 38.755116));
		ring.addPoint(new Point(-77.195090, 38.755167));
		ring.addPoint(new Point(-77.195106, 38.755154));
		ring.addPoint(new Point(-77.195205, 38.755233));
		ring.addPoint(new Point(-77.195299, 38.755159));
		polygon.addRing(ring);
		FeatureRow polygonRow = featureDao.newRow();
		polygonRow.setGeometry(GeoPackageGeometryData.create(srsId, polygon));
		polygonRow.setValue(FEATURE_NAME_COLUMN, "NGA Visitor Center");
		polygonRow.setValue(FEATURE_NUMBER_COLUMN, 3);
		featureDao.insert(polygonRow);

	}

	/**
	 * Create tiles
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @throws IOException
	 *             upon error
	 */
	private static void createTiles(DGIWGGeoPackage geoPackage)
			throws IOException {

		CoordinateReferenceSystem crs = CoordinateReferenceSystem.EPSG_3857;

		BoundingBox informativeBounds = new BoundingBox(-8593967, 4685285,
				-8592745, 4687730);

		TileGrid totalTileGrid = TileBoundingBoxUtils
				.getTileGrid(informativeBounds, MIN_ZOOM);
		BoundingBox extentBounds = TileBoundingBoxUtils
				.getWebMercatorBoundingBox(totalTileGrid, MIN_ZOOM);

		TileMatrixSet tileMatrixSet = geoPackage.createTiles(TILE_TABLE,
				TILE_IDENTIFIER, TILE_DESCRIPTION, informativeBounds, crs,
				extentBounds);

		long matrixWidth = totalTileGrid.getWidth();
		long matrixHeight = totalTileGrid.getHeight();

		geoPackage.createTileMatrices(tileMatrixSet, MIN_ZOOM, MAX_ZOOM,
				matrixWidth, matrixHeight);

		TileDao tileDao = geoPackage.getTileDao(tileMatrixSet);

		TileGrid tileGrid = totalTileGrid;

		final String tilesPath = "tiles/";

		for (int zoom = MIN_ZOOM; zoom <= MAX_ZOOM; zoom++) {

			final String zoomPath = tilesPath + zoom + "/";

			for (long x = tileGrid.getMinX(); x <= tileGrid.getMaxX(); x++) {

				final String xPath = zoomPath + x + "/";

				for (long y = tileGrid.getMinY(); y <= tileGrid
						.getMaxY(); y++) {

					final String yPath = xPath + y + "."
							+ ImageUtils.IMAGE_FORMAT_PNG;

					if (TestUtils.class.getResource("/" + yPath) != null) {

						File tileFile = TestUtils.getTestFile(yPath);

						byte[] tileBytes = GeoPackageIOUtils
								.fileBytes(tileFile);

						TileRow newRow = tileDao.newRow();

						newRow.setZoomLevel(zoom);
						newRow.setTileColumn(x - tileGrid.getMinX());
						newRow.setTileRow(y - tileGrid.getMinY());
						newRow.setTileData(tileBytes);

						tileDao.create(newRow);

					}
				}
			}

			tileGrid = TileBoundingBoxUtils.tileGridZoomIncrease(tileGrid, 1);
		}

	}

	/**
	 * Create national metadata extension
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @throws IOException
	 *             upon error
	 */
	private static void createNationalMetadataExtension(
			DGIWGGeoPackage geoPackage) throws IOException {

		geoPackage.createGeoPackageDatasetMetadata(
				DGIWGConstants.NMIS_DEFAULT_URI,
				getMetadata(TestConstants.NMIS_METADATA_1));

	}

	/**
	 * Create features metadata extension
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	private static void createFeaturesMetadataExtension(
			DGIWGGeoPackage geoPackage) throws SQLException, IOException {

		MetadataExtension metadataExtension = new MetadataExtension(geoPackage);

		metadataExtension.createMetadataTable();
		MetadataDao metadataDao = metadataExtension.getMetadataDao();

		metadataExtension.createMetadataReferenceTable();
		MetadataReferenceDao metadataReferenceDao = metadataExtension
				.getMetadataReferenceDao();

		Metadata metadata = new Metadata();
		metadata.setMetadataScope(MetadataScopeType.FEATURE);
		metadata.setStandardUri(DGIWGConstants.DMF_DEFAULT_URI);
		metadata.setMimeType(DGIWGConstants.METADATA_MIME_TYPE);
		metadata.setMetadata(getMetadata(TestConstants.DGIWG_METADATA_2));
		metadataDao.create(metadata);

		MetadataReference reference = new MetadataReference();
		reference.setReferenceScope(ReferenceScopeType.ROW);
		reference.setTableName(FEATURE_TABLE);
		reference.setRowIdValue(1L);
		reference.setMetadata(metadata);
		metadataReferenceDao.create(reference);

	}

	/**
	 * Create tiles metadata extension
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	private static void createTilesMetadataExtension(DGIWGGeoPackage geoPackage)
			throws SQLException, IOException {

		MetadataExtension metadataExtension = new MetadataExtension(geoPackage);

		metadataExtension.createMetadataTable();
		MetadataDao metadataDao = metadataExtension.getMetadataDao();

		metadataExtension.createMetadataReferenceTable();
		MetadataReferenceDao metadataReferenceDao = metadataExtension
				.getMetadataReferenceDao();

		Metadata metadata = new Metadata();
		metadata.setMetadataScope(MetadataScopeType.MODEL);
		metadata.setStandardUri(DGIWGConstants.DMF_DEFAULT_URI);
		metadata.setMimeType(DGIWGConstants.METADATA_MIME_TYPE);
		metadata.setMetadata(getMetadata(TestConstants.DGIWG_METADATA_2));
		metadataDao.create(metadata);

		MetadataReference reference = new MetadataReference();
		reference.setReferenceScope(ReferenceScopeType.TABLE);
		reference.setTableName(TILE_TABLE);
		reference.setMetadata(metadata);
		metadataReferenceDao.create(reference);

	}

	/**
	 * Create schema extension
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @throws SQLException
	 *             upon error
	 */
	private static void createSchemaExtension(DGIWGGeoPackage geoPackage)
			throws SQLException {

		SchemaExtension schemaExtension = new SchemaExtension(geoPackage);

		schemaExtension.createDataColumnConstraintsTable();

		DataColumnConstraintsDao dao = schemaExtension
				.getDataColumnConstraintsDao();

		DataColumnConstraints sampleRange = new DataColumnConstraints();
		sampleRange.setConstraintName("sampleRange");
		sampleRange.setConstraintType(DataColumnConstraintType.RANGE);
		sampleRange.setMin(BigDecimal.ONE);
		sampleRange.setMinIsInclusive(true);
		sampleRange.setMax(BigDecimal.TEN);
		sampleRange.setMaxIsInclusive(true);
		sampleRange.setDescription("sampleRange description");
		dao.create(sampleRange);

		schemaExtension.createDataColumnsTable();

		DataColumnsDao dataColumnsDao = schemaExtension.getDataColumnsDao();

		DataColumns dataColumns = new DataColumns();
		dataColumns.setContents(geoPackage.getTableContents(FEATURE_TABLE));
		dataColumns.setColumnName(FEATURE_NAME_COLUMN);
		dataColumns.setName(FEATURE_TABLE);
		dataColumns.setTitle("Test Title");
		dataColumns.setDescription("Test Description");
		dataColumns.setMimeType("Test MIME Type");
		dataColumns.setConstraintName(sampleRange.getConstraintName());

		dataColumnsDao.create(dataColumns);

	}

	/**
	 * Create coverage data extension
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @throws SQLException
	 *             upon error
	 */
	private static void createCoverageDataExtension(DGIWGGeoPackage geoPackage)
			throws SQLException {

		TileDao tileTableDao = geoPackage.getTileDao(TILE_TABLE);

		BoundingBox bbox = tileTableDao.getBoundingBox();

		SpatialReferenceSystemDao srsDao = geoPackage
				.getSpatialReferenceSystemDao();
		srsDao.getOrCreateFromEpsg(
				ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM_GEOGRAPHICAL_3D);

		TileTableMetadata metadata = TileTableMetadata.create(
				COVERAGE_DATA_TABLE,
				tileTableDao.getContents().getBoundingBox(), bbox,
				tileTableDao.getSrsId());
		metadata.setIdentifier(COVERAGE_DATA_IDENTIFIER);
		metadata.setDescription(COVERAGE_DATA_DESCRIPTION);
		CoverageDataTiff coverageData = CoverageDataTiff
				.createTileTable(geoPackage, metadata);
		TileDao tileDao = coverageData.getTileDao();
		TileMatrixSet tileMatrixSet = coverageData.getTileMatrixSet();

		GriddedCoverageDao griddedCoverageDao = coverageData
				.getGriddedCoverageDao();

		GriddedCoverage griddedCoverage = new GriddedCoverage();
		griddedCoverage.setTileMatrixSet(tileMatrixSet);
		griddedCoverage.setDataType(GriddedCoverageDataType.FLOAT);
		griddedCoverage.setDataNull((double) Float.MAX_VALUE);
		griddedCoverage
				.setGridCellEncodingType(GriddedCoverageEncodingType.CENTER);
		griddedCoverageDao.create(griddedCoverage);

		GriddedTileDao griddedTileDao = coverageData.getGriddedTileDao();

		long width = 1;
		long height = 2;
		int tileWidth = 4;
		int tileHeight = 4;

		TileMatrixDao tileMatrixDao = geoPackage.getTileMatrixDao();

		TileMatrix tileMatrix = new TileMatrix();
		tileMatrix.setContents(tileMatrixSet.getContents());
		tileMatrix.setMatrixHeight(height);
		tileMatrix.setMatrixWidth(width);
		tileMatrix.setTileHeight(tileHeight);
		tileMatrix.setTileWidth(tileWidth);
		tileMatrix.setPixelXSize(bbox.getLongitudeRange() / width / tileWidth);
		tileMatrix.setPixelYSize(bbox.getLatitudeRange() / height / tileHeight);
		tileMatrix.setZoomLevel(tileTableDao.getMinZoom());
		tileMatrixDao.create(tileMatrix);

		float[][] tilePixels = new float[tileHeight][tileWidth];

		tilePixels[0][0] = 76.0f;
		tilePixels[0][1] = 74.0f;
		tilePixels[0][2] = 70.0f;
		tilePixels[0][3] = 70.0f;
		tilePixels[1][0] = 63.0f;
		tilePixels[1][1] = 71.0f;
		tilePixels[1][2] = 65.0f;
		tilePixels[1][3] = 69.0f;
		tilePixels[2][0] = 56.0f;
		tilePixels[2][1] = 59.0f;
		tilePixels[2][2] = 65.0f;
		tilePixels[2][3] = 70.0f;
		tilePixels[3][0] = 70.0f;
		tilePixels[3][1] = 71.0f;
		tilePixels[3][2] = 70.0f;
		tilePixels[3][3] = 71.0f;

		byte[] imageBytes = coverageData.drawTileData(tilePixels);

		TileRow tileRow = tileDao.newRow();
		tileRow.setTileColumn(0);
		tileRow.setTileRow(0);
		tileRow.setZoomLevel(tileMatrix.getZoomLevel());
		tileRow.setTileData(imageBytes);

		long tileId = tileDao.create(tileRow);

		GriddedTile griddedTile = new GriddedTile();
		griddedTile.setContents(tileMatrixSet.getContents());
		griddedTile.setTableId(tileId);

		griddedTileDao.create(griddedTile);

		tilePixels = new float[tileHeight][tileWidth];

		tilePixels[0][0] = 51.0f;
		tilePixels[0][1] = 71.0f;
		tilePixels[0][2] = 66.0f;
		tilePixels[0][3] = 70.0f;
		tilePixels[1][0] = 50.0f;
		tilePixels[1][1] = 62.0f;
		tilePixels[1][2] = 58.0f;
		tilePixels[1][3] = 67.0f;
		tilePixels[2][0] = 44.0f;
		tilePixels[2][1] = 64.0f;
		tilePixels[2][2] = 61.0f;
		tilePixels[2][3] = 53.0f;
		tilePixels[3][0] = 55.0f;
		tilePixels[3][1] = 43.0f;
		tilePixels[3][2] = 59.0f;
		tilePixels[3][3] = 46.0f;

		imageBytes = coverageData.drawTileData(tilePixels);

		tileRow = tileDao.newRow();
		tileRow.setTileColumn(0);
		tileRow.setTileRow(1);
		tileRow.setZoomLevel(tileMatrix.getZoomLevel());
		tileRow.setTileData(imageBytes);

		tileId = tileDao.create(tileRow);

		griddedTile = new GriddedTile();
		griddedTile.setContents(tileMatrixSet.getContents());
		griddedTile.setTableId(tileId);

		griddedTileDao.create(griddedTile);

	}

	/**
	 * Create related tables extension to media
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @throws IOException
	 *             upon error
	 */
	private static void createRelatedTablesMediaExtension(
			DGIWGGeoPackage geoPackage) throws IOException {

		RelatedTablesExtension relatedTables = new RelatedTablesExtension(
				geoPackage);

		MediaTable mediaTable = MediaTable.create(
				MediaTableMetadata.create(MEDIA_TABLE, getAdditionalColumns()));

		UserMappingTable userMappingTable = UserMappingTable
				.create(MEDIA_MAPPING_TABLE, getAdditionalColumns());
		ExtendedRelation relation = relatedTables.addMediaRelationship(
				FEATURE_TABLE, mediaTable, userMappingTable);

		insertRelatedTablesMediaExtensionRows(geoPackage, relation, "NGA%",
				"NGA", "NGA_Logo.png", "image/png", "NGA Logo",
				"http://www.nga.mil");
		insertRelatedTablesMediaExtensionRows(geoPackage, relation, "NGA",
				"NGA", "NGA.jpg", "image/jpeg", "Aerial View of NGA East",
				"http://www.nga.mil");

	}

	/**
	 * Insert related tables media extension rows
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @param relation
	 *            extended relation
	 * @param query
	 *            feature name column query
	 * @param name
	 *            relation name
	 * @param file
	 *            media file name
	 * @param contentType
	 *            media content type
	 * @param description
	 *            relation description
	 * @param source
	 *            relation source
	 */
	private static void insertRelatedTablesMediaExtensionRows(
			DGIWGGeoPackage geoPackage, ExtendedRelation relation, String query,
			String name, String file, String contentType, String description,
			String source) {

		RelatedTablesExtension relatedTables = new RelatedTablesExtension(
				geoPackage);

		FeatureDao featureDao = geoPackage
				.getFeatureDao(relation.getBaseTableName());
		MediaDao mediaDao = relatedTables.getMediaDao(relation);
		UserMappingDao userMappingDao = relatedTables.getMappingDao(relation);

		MediaRow mediaRow = mediaDao.newRow();
		mediaRow.setData(TestUtils.getTestFileBytes(file));
		mediaRow.setContentType(contentType);
		DublinCoreMetadata.setValue(mediaRow, DublinCoreType.DATE, new Date());
		DublinCoreMetadata.setValue(mediaRow, DublinCoreType.DESCRIPTION,
				description);
		DublinCoreMetadata.setValue(mediaRow, DublinCoreType.SOURCE, source);
		DublinCoreMetadata.setValue(mediaRow, DublinCoreType.TITLE, name);
		long mediaRowId = mediaDao.create(mediaRow);

		FeatureResultSet featureResultSet = featureDao
				.queryForLike(FEATURE_NAME_COLUMN, query);
		for (FeatureRow featureRow : featureResultSet) {
			UserMappingRow userMappingRow = userMappingDao.newRow();
			userMappingRow.setBaseId(featureRow.getId());
			userMappingRow.setRelatedId(mediaRowId);
			String featureName = featureRow.getValue(FEATURE_NAME_COLUMN)
					.toString();
			DublinCoreMetadata.setValue(userMappingRow, DublinCoreType.DATE,
					new Date());
			DublinCoreMetadata.setValue(userMappingRow,
					DublinCoreType.DESCRIPTION,
					featureName + " - " + description);
			DublinCoreMetadata.setValue(userMappingRow, DublinCoreType.SOURCE,
					source);
			DublinCoreMetadata.setValue(userMappingRow, DublinCoreType.TITLE,
					featureName + " - " + name);
			userMappingDao.create(userMappingRow);
		}
		featureResultSet.close();
	}

	/**
	 * Create related tables extension to tiles
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 */
	private static void createRelatedTablesTilesExtension(
			DGIWGGeoPackage geoPackage) {

		RelatedTablesExtension relatedTables = new RelatedTablesExtension(
				geoPackage);

		UserMappingTable userMappingTable = UserMappingTable
				.create(TILE_MAPPING_TABLE, getAdditionalColumns());
		ExtendedRelation relation = relatedTables.addTilesRelationship(
				FEATURE_TABLE, TILE_TABLE, userMappingTable);

		UserMappingDao userMappingDao = relatedTables.getMappingDao(relation);

		FeatureDao featureDao = geoPackage
				.getFeatureDao(relation.getBaseTableName());
		TileDao tileDao = geoPackage.getTileDao(relation.getRelatedTableName());

		BoundingBox boundingBox = tileDao.getBoundingBox();

		GeometryTransform transform = GeometryTransform
				.create(featureDao.getProjection(), tileDao.getProjection());

		FeatureResultSet featureResultSet = featureDao.queryForAll();
		for (FeatureRow featureRow : featureResultSet) {

			String featureName = featureRow.getValue(FEATURE_NAME_COLUMN)
					.toString();

			BoundingBox geometryBoundingBox = featureRow.getGeometry()
					.getOrBuildBoundingBox();
			BoundingBox geometryTransform = geometryBoundingBox
					.transform(transform);

			for (long zoom = tileDao.getMinZoom(); zoom <= tileDao
					.getMaxZoom(); zoom++) {

				TileMatrix tileMatrix = tileDao.getTileMatrix(zoom);
				long width = tileMatrix.getMatrixWidth();
				long height = tileMatrix.getMatrixHeight();

				TileGrid tileGrid = TileBoundingBoxUtils.getTileGrid(
						boundingBox, width, height, geometryTransform);

				TileResultSet tileResultSet = tileDao.queryByTileGrid(tileGrid,
						zoom);

				for (TileRow tileRow : tileResultSet) {

					UserMappingRow userMappingRow = userMappingDao.newRow();
					userMappingRow.setBaseId(featureRow.getId());
					userMappingRow.setRelatedId(tileRow.getId());
					DublinCoreMetadata.setValue(userMappingRow,
							DublinCoreType.DATE, new Date());
					DublinCoreMetadata.setValue(userMappingRow,
							DublinCoreType.DESCRIPTION,
							"Zoom level " + zoom + " tile");
					DublinCoreMetadata.setValue(userMappingRow,
							DublinCoreType.SOURCE, "http://www.nga.mil");
					DublinCoreMetadata.setValue(userMappingRow,
							DublinCoreType.TITLE, featureName);
					userMappingDao.create(userMappingRow);
				}
				tileResultSet.close();

			}

		}
		featureResultSet.close();

	}

	/**
	 * Get additional related tables columns
	 * 
	 * @return additional columns
	 */
	private static List<UserCustomColumn> getAdditionalColumns() {

		List<UserCustomColumn> additionalColumns = new ArrayList<>();

		additionalColumns.add(UserCustomColumn.createColumn(
				DublinCoreType.DATE.getName(), GeoPackageDataType.DATETIME));
		additionalColumns.add(UserCustomColumn.createColumn(
				DublinCoreType.DESCRIPTION.getName(), GeoPackageDataType.TEXT));
		additionalColumns.add(UserCustomColumn.createColumn(
				DublinCoreType.SOURCE.getName(), GeoPackageDataType.TEXT));
		additionalColumns.add(UserCustomColumn.createColumn(
				DublinCoreType.TITLE.getName(), GeoPackageDataType.TEXT));

		return additionalColumns;
	}

}
