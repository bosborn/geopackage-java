package mil.nga.geopackage.tiles.features;

import java.util.Collection;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.extension.nga.link.FeatureTileTableLinker;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.geopackage.tiles.TileGenerator;
import mil.nga.geopackage.tiles.TileGrid;
import mil.nga.proj.Projection;
import mil.nga.proj.ProjectionConstants;
import mil.nga.sf.proj.GeometryTransform;

/**
 * Creates a set of tiles within a GeoPackage by generating tiles from features
 *
 * @author osbornb
 * @since 1.1.2
 */
public class FeatureTileGenerator extends TileGenerator {

	/**
	 * Feature tiles
	 */
	private final FeatureTiles featureTiles;

	/**
	 * Flag indicating whether the feature and tile tables should be linked
	 */
	private boolean linkTables = true;

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, BoundingBox boundingBox,
			Projection projection) {
		this(geoPackage, tableName, featureTiles, geoPackage, boundingBox,
				projection);
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param zoomLevel
	 *            zoom level
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, int zoomLevel, BoundingBox boundingBox,
			Projection projection) {
		this(geoPackage, tableName, featureTiles, geoPackage, zoomLevel,
				boundingBox, projection);
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param minZoom
	 *            min zoom
	 * @param maxZoom
	 *            max zoom
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 1.2.0
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, int minZoom, int maxZoom,
			BoundingBox boundingBox, Projection projection) {
		this(geoPackage, tableName, featureTiles, geoPackage, minZoom, maxZoom,
				boundingBox, projection);
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param zoomLevels
	 *            zoom levels
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, Collection<Integer> zoomLevels,
			BoundingBox boundingBox, Projection projection) {
		this(geoPackage, tableName, featureTiles, geoPackage, zoomLevels,
				boundingBox, projection);
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param zoomLevels
	 *            zoom levels
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, int[] zoomLevels,
			BoundingBox boundingBox, Projection projection) {
		this(geoPackage, tableName, featureTiles, geoPackage, zoomLevels,
				boundingBox, projection);
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			BoundingBox boundingBox, Projection projection) {
		super(geoPackage, tableName, getBoundingBox(featureGeoPackage,
				featureTiles, boundingBox, projection), projection);
		this.featureTiles = featureTiles;
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param zoomLevel
	 *            zoom level
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			int zoomLevel, BoundingBox boundingBox, Projection projection) {
		super(geoPackage, tableName, zoomLevel,
				getBoundingBox(featureGeoPackage, featureTiles, boundingBox,
						projection),
				projection);
		this.featureTiles = featureTiles;
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param minZoom
	 *            min zoom
	 * @param maxZoom
	 *            max zoom
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 3.2.0
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			int minZoom, int maxZoom, BoundingBox boundingBox,
			Projection projection) {
		super(geoPackage, tableName, minZoom, maxZoom,
				getBoundingBox(featureGeoPackage, featureTiles, boundingBox,
						projection),
				projection);
		this.featureTiles = featureTiles;
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param zoomLevels
	 *            zoom levels
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			Collection<Integer> zoomLevels, BoundingBox boundingBox,
			Projection projection) {
		super(geoPackage, tableName, zoomLevels,
				getBoundingBox(featureGeoPackage, featureTiles, boundingBox,
						projection),
				projection);
		this.featureTiles = featureTiles;
	}

	/**
	 * Constructor
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param zoomLevels
	 *            zoom levels
	 * @param boundingBox
	 *            tiles bounding box
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			int[] zoomLevels, BoundingBox boundingBox, Projection projection) {
		super(geoPackage, tableName, zoomLevels,
				getBoundingBox(featureGeoPackage, featureTiles, boundingBox,
						projection),
				projection);
		this.featureTiles = featureTiles;
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, Projection projection) {
		this(geoPackage, tableName, featureTiles, new int[] {}, null,
				projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param zoomLevel
	 *            zoom level
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, int zoomLevel, Projection projection) {
		this(geoPackage, tableName, featureTiles, zoomLevel, null, projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param minZoom
	 *            min zoom
	 * @param maxZoom
	 *            max zoom
	 * @param projection
	 *            tiles projection
	 * @since 3.2.0
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, int minZoom, int maxZoom,
			Projection projection) {
		this(geoPackage, tableName, featureTiles, minZoom, maxZoom, null,
				projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param zoomLevels
	 *            zoom levels
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, Collection<Integer> zoomLevels,
			Projection projection) {
		this(geoPackage, tableName, featureTiles, zoomLevels, null, projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param zoomLevels
	 *            zoom levels
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, int[] zoomLevels,
			Projection projection) {
		this(geoPackage, tableName, featureTiles, zoomLevels, null, projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			Projection projection) {
		this(geoPackage, tableName, featureTiles, featureGeoPackage,
				new int[] {}, null, projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param zoomLevel
	 *            zoom level
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			int zoomLevel, Projection projection) {
		this(geoPackage, tableName, featureTiles, featureGeoPackage, zoomLevel,
				null, projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param minZoom
	 *            min zoom
	 * @param maxZoom
	 *            max zoom
	 * @param projection
	 *            tiles projection
	 * @since 3.2.0
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			int minZoom, int maxZoom, Projection projection) {
		this(geoPackage, tableName, featureTiles, featureGeoPackage, minZoom,
				maxZoom, null, projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param zoomLevels
	 *            zoom levels
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			Collection<Integer> zoomLevels, Projection projection) {
		this(geoPackage, tableName, featureTiles, featureGeoPackage, zoomLevels,
				null, projection);
	}

	/**
	 * Constructor, find the the bounding box from the feature table
	 *
	 * @param geoPackage
	 *            GeoPackage
	 * @param tableName
	 *            table name
	 * @param featureTiles
	 *            feature tiles
	 * @param featureGeoPackage
	 *            feature GeoPackage if different from the destination
	 * @param zoomLevels
	 *            zoom levels
	 * @param projection
	 *            tiles projection
	 * @since 6.1.3
	 */
	public FeatureTileGenerator(GeoPackage geoPackage, String tableName,
			FeatureTiles featureTiles, GeoPackage featureGeoPackage,
			int[] zoomLevels, Projection projection) {
		this(geoPackage, tableName, featureTiles, featureGeoPackage, zoomLevels,
				null, projection);
	}

	/**
	 * Get the bounding box for the feature tile generator, from the provided
	 * and from the feature table
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @param featureTiles
	 *            feature tiles
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @return bounding box
	 */
	private static BoundingBox getBoundingBox(GeoPackage geoPackage,
			FeatureTiles featureTiles, BoundingBox boundingBox,
			Projection projection) {

		String tableName = featureTiles.getFeatureDao().getTableName();
		boolean manualQuery = boundingBox == null;
		BoundingBox featureBoundingBox = geoPackage.getBoundingBox(projection,
				tableName, manualQuery);
		if (featureBoundingBox != null) {
			if (boundingBox == null) {
				boundingBox = featureBoundingBox;
			} else {
				boundingBox = boundingBox.overlap(featureBoundingBox);
			}
		}

		if (boundingBox != null) {
			boundingBox = featureTiles.expandBoundingBox(boundingBox,
					projection);
		}

		return boundingBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoundingBox getBoundingBox(int zoom) {

		GeometryTransform projectionToWebMercator = GeometryTransform
				.create(projection, ProjectionConstants.EPSG_WEB_MERCATOR);
		BoundingBox webMercatorBoundingBox = boundingBox
				.transform(projectionToWebMercator);

		TileGrid tileGrid = TileBoundingBoxUtils
				.getTileGrid(webMercatorBoundingBox, zoom);
		BoundingBox tileBoundingBox = TileBoundingBoxUtils
				.getWebMercatorBoundingBox(tileGrid.getMinX(),
						tileGrid.getMinY(), zoom);

		BoundingBox expandedBoundingBox = featureTiles
				.expandBoundingBox(webMercatorBoundingBox, tileBoundingBox);

		BoundingBox zoomBoundingBox = expandedBoundingBox
				.transform(projectionToWebMercator.getInverseTransformation());

		return zoomBoundingBox;
	}

	/**
	 * Is the feature table going to be linked with the tile table? Defaults to
	 * true.
	 * 
	 * @return true if tables will be linked upon generation
	 * @since 1.1.6
	 */
	public boolean isLinkTables() {
		return linkTables;
	}

	/**
	 * Set the link tables flag
	 * 
	 * @param linkTables
	 *            link tables flag
	 * @since 1.1.6
	 */
	public void setLinkTables(boolean linkTables) {
		this.linkTables = linkTables;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preTileGeneration() {

		// Link the feature and tile table if they are in the same GeoPackage
		GeoPackage geoPackage = getGeoPackage();
		String featureTable = featureTiles.getFeatureDao().getTableName();
		String tileTable = getTableName();
		if (linkTables && geoPackage.isFeatureTable(featureTable)
				&& geoPackage.isTileTable(tileTable)) {
			FeatureTileTableLinker linker = new FeatureTileTableLinker(
					geoPackage);
			linker.link(featureTable, tileTable);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected byte[] createTile(int z, long x, long y) {

		byte[] tileData = featureTiles.drawTileBytes((int) x, (int) y, z);

		return tileData;
	}

}
