package mil.nga.geopackage.extension.index;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureResultSet;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.features.user.FeatureRowSync;
import mil.nga.sf.GeometryEnvelope;
import mil.nga.sf.proj.Projection;

/**
 * Feature Table Index NGA Extension implementation. This extension is used to
 * index Geometries within a feature table by their minimum bounding box for
 * bounding box queries. This extension is required to provide an index
 * implementation when a SQLite version is used before SpatialLite support
 * (Android).
 * 
 * @author osbornb
 * @since 1.1.0
 */
public class FeatureTableIndex extends FeatureTableCoreIndex {

	/**
	 * Logger
	 */
	private static final Logger log = Logger
			.getLogger(FeatureTableIndex.class.getName());

	/**
	 * Feature DAO
	 */
	private final FeatureDao featureDao;

	/**
	 * Feature Row Sync for simultaneous same row queries
	 */
	private final FeatureRowSync featureRowSync = new FeatureRowSync();

	/**
	 * Constructor
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * @param featureDao
	 *            feature dao
	 */
	public FeatureTableIndex(GeoPackage geoPackage, FeatureDao featureDao) {
		super(geoPackage, featureDao.getTableName(),
				featureDao.getGeometryColumnName());
		this.featureDao = featureDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Projection getProjection() {
		return featureDao.getProjection();
	}

	/**
	 * Close the table index
	 */
	public void close() {
		// Don't close anything, leave the GeoPackage connection open
	}

	/**
	 * Index the feature row. This method assumes that indexing has been
	 * completed and maintained as the last indexed time is updated.
	 *
	 * @param row
	 *            feature row
	 * @return true if indexed
	 */
	public boolean index(FeatureRow row) {
		TableIndex tableIndex = getTableIndex();
		if (tableIndex == null) {
			throw new GeoPackageException(
					"GeoPackage table is not indexed. GeoPackage: "
							+ getGeoPackage().getName() + ", Table: "
							+ getTableName());
		}
		boolean indexed = index(tableIndex, row.getId(), row.getGeometry());

		// Update the last indexed time
		updateLastIndexed();

		return indexed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int indexTable(final TableIndex tableIndex) {

		int count = 0;

		long offset = 0;
		int chunkCount = 0;

		while (chunkCount >= 0) {

			final long chunkOffset = offset;

			try {
				// Iterate through each row and index as a single transaction
				ConnectionSource connectionSource = getGeoPackage()
						.getDatabase().getConnectionSource();
				chunkCount = TransactionManager.callInTransaction(
						connectionSource, new Callable<Integer>() {
							public Integer call() throws Exception {

								FeatureResultSet resultSet = featureDao
										.queryForChunk(chunkLimit, chunkOffset);
								int count = indexRows(tableIndex, resultSet);

								return count;
							}
						});
				if (chunkCount > 0) {
					count += chunkCount;
				}
			} catch (SQLException e) {
				throw new GeoPackageException(
						"Failed to Index Table. GeoPackage: "
								+ getGeoPackage().getName() + ", Table: "
								+ getTableName(),
						e);
			}

			offset += chunkLimit;
		}

		// Update the last indexed time
		if (progress == null || progress.isActive()) {
			updateLastIndexed();
		}

		return count;
	}

	/**
	 * Index the feature rows in the cursor
	 * 
	 * @param tableIndex
	 *            table index
	 * @param resultSet
	 *            feature result
	 * @return count, -1 if no results or canceled
	 */
	private int indexRows(TableIndex tableIndex, FeatureResultSet resultSet) {

		int count = -1;

		try {
			while ((progress == null || progress.isActive())
					&& resultSet.moveToNext()) {
				if (count < 0) {
					count++;
				}
				try {
					FeatureRow row = resultSet.getRow();
					boolean indexed = index(tableIndex, row.getId(),
							row.getGeometry());
					if (indexed) {
						count++;
					}
					if (progress != null) {
						progress.addProgress(1);
					}
				} catch (Exception e) {
					log.log(Level.SEVERE,
							"Failed to index feature. Table: "
									+ tableIndex.getTableName() + ", Position: "
									+ resultSet.getPosition(),
							e);
				}
			}
		} finally {
			resultSet.close();
		}

		return count;
	}

	/**
	 * Delete the index for the feature row
	 *
	 * @param row
	 *            feature row
	 * @return deleted rows, should be 0 or 1
	 */
	public int deleteIndex(FeatureRow row) {
		return deleteIndex(row.getId());
	}

	/**
	 * Get the feature row for the Geometry Index
	 * 
	 * @param geometryIndex
	 *            geometry index
	 * @return feature row
	 */
	public FeatureRow getFeatureRow(GeometryIndex geometryIndex) {

		long geomId = geometryIndex.getGeomId();

		// Get the row or lock for reading
		FeatureRow row = featureRowSync.getRowOrLock(geomId);
		if (row == null) {
			// Query for the row and set in the sync
			try {
				row = featureDao.queryForIdRow(geomId);
			} finally {
				featureRowSync.setRow(geomId, row);
			}
		}

		return row;
	}

	/**
	 * Query for all Features
	 * 
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures() {
		return featureDao.queryIn(queryIdsSQL());
	}

	/**
	 * Query for features
	 * 
	 * @param fieldValues
	 *            field values
	 * 
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(Map<String, Object> fieldValues) {
		return featureDao.queryIn(queryIdsSQL(), fieldValues);
	}

	/**
	 * Count features
	 * 
	 * @param fieldValues
	 *            field values
	 * 
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(Map<String, Object> fieldValues) {
		return featureDao.countIn(queryIdsSQL(), fieldValues);
	}

	/**
	 * Query for features
	 * 
	 * @param where
	 *            where clause
	 * 
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(String where) {
		return featureDao.queryIn(queryIdsSQL(), where);
	}

	/**
	 * Count features
	 * 
	 * @param where
	 *            where clause
	 * 
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(String where) {
		return featureDao.countIn(queryIdsSQL(), where);
	}

	/**
	 * Query for features
	 * 
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * 
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(String where, String[] whereArgs) {
		return featureDao.queryIn(queryIdsSQL(), where, whereArgs);
	}

	/**
	 * Count features
	 * 
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * 
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(String where, String[] whereArgs) {
		return featureDao.countIn(queryIdsSQL(), where, whereArgs);
	}

	/**
	 * Query for Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox) {
		return queryFeatures(boundingBox.buildEnvelope());
	}

	/**
	 * Count the Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox) {
		return countFeatures(boundingBox.buildEnvelope());
	}

	/**
	 * Query for Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param fieldValues
	 *            field values
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox,
			Map<String, Object> fieldValues) {
		return queryFeatures(boundingBox.buildEnvelope(), fieldValues);
	}

	/**
	 * Count the Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param fieldValues
	 *            field values
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox,
			Map<String, Object> fieldValues) {
		return countFeatures(boundingBox.buildEnvelope(), fieldValues);
	}

	/**
	 * Query for Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox,
			String where) {
		return queryFeatures(boundingBox, where, null);
	}

	/**
	 * Count the Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox, String where) {
		return countFeatures(boundingBox, where, null);
	}

	/**
	 * Query for Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox, String where,
			String[] whereArgs) {
		return queryFeatures(boundingBox.buildEnvelope(), where, whereArgs);
	}

	/**
	 * Count the Features within the bounding box, projected correctly
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox, String where,
			String[] whereArgs) {
		return countFeatures(boundingBox.buildEnvelope(), where, whereArgs);
	}

	/**
	 * Query for Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox,
			Projection projection) {
		BoundingBox featureBoundingBox = getFeatureBoundingBox(boundingBox,
				projection);
		return queryFeatures(featureBoundingBox);
	}

	/**
	 * Count the Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox, Projection projection) {
		BoundingBox featureBoundingBox = getFeatureBoundingBox(boundingBox,
				projection);
		return countFeatures(featureBoundingBox);
	}

	/**
	 * Query for Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @param fieldValues
	 *            field values
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox,
			Projection projection, Map<String, Object> fieldValues) {
		BoundingBox featureBoundingBox = getFeatureBoundingBox(boundingBox,
				projection);
		return queryFeatures(featureBoundingBox, fieldValues);
	}

	/**
	 * Count the Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @param fieldValues
	 *            field values
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox, Projection projection,
			Map<String, Object> fieldValues) {
		BoundingBox featureBoundingBox = getFeatureBoundingBox(boundingBox,
				projection);
		return countFeatures(featureBoundingBox, fieldValues);
	}

	/**
	 * Query for Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @param where
	 *            where clause
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox,
			Projection projection, String where) {
		return queryFeatures(boundingBox, projection, where, null);
	}

	/**
	 * Count the Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @param where
	 *            where clause
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox, Projection projection,
			String where) {
		return countFeatures(boundingBox, projection, where, null);
	}

	/**
	 * Query for Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(BoundingBox boundingBox,
			Projection projection, String where, String[] whereArgs) {
		BoundingBox featureBoundingBox = getFeatureBoundingBox(boundingBox,
				projection);
		return queryFeatures(featureBoundingBox, where, whereArgs);
	}

	/**
	 * Count the Features within the bounding box in the provided projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection of the provided bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(BoundingBox boundingBox, Projection projection,
			String where, String[] whereArgs) {
		BoundingBox featureBoundingBox = getFeatureBoundingBox(boundingBox,
				projection);
		return countFeatures(featureBoundingBox, where, whereArgs);
	}

	/**
	 * Query for Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(GeometryEnvelope envelope) {
		return featureDao.queryIn(queryIdsSQL(envelope));
	}

	/**
	 * Count the Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(GeometryEnvelope envelope) {
		return featureDao.countIn(queryIdsSQL(envelope));
	}

	/**
	 * Query for Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param fieldValues
	 *            field values
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(GeometryEnvelope envelope,
			Map<String, Object> fieldValues) {
		return featureDao.queryIn(queryIdsSQL(envelope), fieldValues);
	}

	/**
	 * Count the Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param fieldValues
	 *            field values
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(GeometryEnvelope envelope,
			Map<String, Object> fieldValues) {
		return featureDao.countIn(queryIdsSQL(envelope), fieldValues);
	}

	/**
	 * Query for Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(GeometryEnvelope envelope,
			String where) {
		return queryFeatures(envelope, where, null);
	}

	/**
	 * Count the Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(GeometryEnvelope envelope, String where) {
		return countFeatures(envelope, where, null);
	}

	/**
	 * Query for Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet queryFeatures(GeometryEnvelope envelope,
			String where, String[] whereArgs) {
		return featureDao.queryIn(queryIdsSQL(envelope), where, whereArgs);
	}

	/**
	 * Count the Features within the Geometry Envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return count
	 * @since 3.4.0
	 */
	public int countFeatures(GeometryEnvelope envelope, String where,
			String[] whereArgs) {
		return featureDao.countIn(queryIdsSQL(envelope), where, whereArgs);
	}

}
