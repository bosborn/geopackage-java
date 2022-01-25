package mil.nga.geopackage.features.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.db.CoreSQLUtils;
import mil.nga.geopackage.features.index.FeatureIndexManager;
import mil.nga.proj.Projection;
import mil.nga.sf.GeometryEnvelope;
import mil.nga.sf.proj.GeometryTransform;

/**
 * Performs manual brute force queries against feature rows. See
 * {@link FeatureIndexManager} for performing indexed queries.
 * 
 * @author osbornb
 * @since 3.1.0
 */
public class ManualFeatureQuery {

	/**
	 * Feature DAO
	 */
	private final FeatureDao featureDao;

	/**
	 * Query single chunk limit
	 */
	protected int chunkLimit = 1000;

	/**
	 * Query range tolerance
	 */
	protected double tolerance = .00000000000001;

	/**
	 * Constructor
	 *
	 * @param featureDao
	 *            feature DAO
	 */
	public ManualFeatureQuery(FeatureDao featureDao) {
		this.featureDao = featureDao;
	}

	/**
	 * Get the feature DAO
	 * 
	 * @return feature DAO
	 */
	public FeatureDao getFeatureDao() {
		return featureDao;
	}

	/**
	 * Get the SQL query chunk limit
	 * 
	 * @return chunk limit
	 */
	public int getChunkLimit() {
		return chunkLimit;
	}

	/**
	 * Set the SQL query chunk limit
	 * 
	 * @param chunkLimit
	 *            chunk limit
	 */
	public void setChunkLimit(int chunkLimit) {
		this.chunkLimit = chunkLimit;
	}

	/**
	 * Get the query range tolerance
	 * 
	 * @return query range tolerance
	 */
	public double getTolerance() {
		return tolerance;
	}

	/**
	 * Set the query range tolerance
	 * 
	 * @param tolerance
	 *            query range tolerance
	 */
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Query for features
	 * 
	 * @return feature results
	 * @since 3.4.0
	 */
	public FeatureResultSet query() {
		return featureDao.query();
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct) {
		return featureDao.query(distinct);
	}

	/**
	 * Query for features
	 * 
	 * @param columns
	 *            columns
	 * 
	 * @return feature results
	 * @since 3.5.0
	 */
	public FeatureResultSet query(String[] columns) {
		return featureDao.query(columns);
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * 
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct, String[] columns) {
		return featureDao.query(distinct, columns);
	}

	/**
	 * Get the count of features
	 *
	 * @return count
	 */
	public int count() {
		return featureDao.count();
	}

	/**
	 * Get the count of features with non null geometries
	 *
	 * @return count
	 */
	public int countWithGeometries() {
		return featureDao.count(
				CoreSQLUtils.quoteWrap(featureDao.getGeometryColumnName())
						+ " IS NOT NULL");
	}

	/**
	 * Get a count of results
	 * 
	 * @param column
	 *            count column name
	 * @return count
	 * @since 4.0.0
	 */
	public int countColumn(String column) {
		return featureDao.countColumn(column);
	}

	/**
	 * Get a count of results
	 * 
	 * @param distinct
	 *            distinct column values
	 * @param column
	 *            count column name
	 * @return count
	 * @since 4.0.0
	 */
	public int count(boolean distinct, String column) {
		return featureDao.count(distinct, column);
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
	public FeatureResultSet query(Map<String, Object> fieldValues) {
		return query(false, fieldValues);
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param fieldValues
	 *            field values
	 * 
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct,
			Map<String, Object> fieldValues) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.query(distinct, where, whereArgs);
	}

	/**
	 * Query for features
	 * 
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * 
	 * @return feature results
	 * @since 3.5.0
	 */
	public FeatureResultSet query(String[] columns,
			Map<String, Object> fieldValues) {
		return query(false, columns, fieldValues);
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * 
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct, String[] columns,
			Map<String, Object> fieldValues) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.query(distinct, columns, where, whereArgs);
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
	public int count(Map<String, Object> fieldValues) {
		return count(false, null, fieldValues);
	}

	/**
	 * Count features
	 * 
	 * @param column
	 *            count column name
	 * @param fieldValues
	 *            field values
	 * 
	 * @return count
	 * @since 4.0.0
	 */
	public int count(String column, Map<String, Object> fieldValues) {
		return count(false, column, fieldValues);
	}

	/**
	 * Count features
	 * 
	 * @param distinct
	 *            distinct column values
	 * @param column
	 *            count column name
	 * @param fieldValues
	 *            field values
	 * 
	 * @return count
	 * @since 4.0.0
	 */
	public int count(boolean distinct, String column,
			Map<String, Object> fieldValues) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.count(distinct, column, where, whereArgs);
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
	public FeatureResultSet query(String where) {
		return featureDao.query(where);
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * 
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct, String where) {
		return featureDao.query(distinct, where);
	}

	/**
	 * Query for features
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * 
	 * @return feature results
	 * @since 3.5.0
	 */
	public FeatureResultSet query(String[] columns, String where) {
		return featureDao.query(columns, where);
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * 
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct, String[] columns,
			String where) {
		return featureDao.query(distinct, columns, where);
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
	public int count(String where) {
		return featureDao.count(where);
	}

	/**
	 * Count features
	 * 
	 * @param column
	 *            count column name
	 * @param where
	 *            where clause
	 * 
	 * @return count
	 * @since 4.0.0
	 */
	public int count(String column, String where) {
		return featureDao.count(column, where);
	}

	/**
	 * Count features
	 * 
	 * @param distinct
	 *            distinct column values
	 * @param column
	 *            count column name
	 * @param where
	 *            where clause
	 * 
	 * @return count
	 * @since 4.0.0
	 */
	public int count(boolean distinct, String column, String where) {
		return featureDao.count(distinct, column, where);
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
	public FeatureResultSet query(String where, String[] whereArgs) {
		return featureDao.query(where, whereArgs);
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * 
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct, String where,
			String[] whereArgs) {
		return featureDao.query(distinct, where, whereArgs);
	}

	/**
	 * Query for features
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * 
	 * @return feature results
	 * @since 3.5.0
	 */
	public FeatureResultSet query(String[] columns, String where,
			String[] whereArgs) {
		return featureDao.query(columns, where, whereArgs);
	}

	/**
	 * Query for features
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * 
	 * @return feature results
	 * @since 4.0.0
	 */
	public FeatureResultSet query(boolean distinct, String[] columns,
			String where, String[] whereArgs) {
		return featureDao.query(distinct, columns, where, whereArgs);
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
	public int count(String where, String[] whereArgs) {
		return featureDao.count(where, whereArgs);
	}

	/**
	 * Count features
	 * 
	 * @param column
	 *            count column name
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * 
	 * @return count
	 * @since 4.0.0
	 */
	public int count(String column, String where, String[] whereArgs) {
		return featureDao.count(column, where, whereArgs);
	}

	/**
	 * Count features
	 * 
	 * @param distinct
	 *            distinct column values
	 * @param column
	 *            count column name
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * 
	 * @return count
	 * @since 4.0.0
	 */
	public int count(boolean distinct, String column, String where,
			String[] whereArgs) {
		return featureDao.count(distinct, column, where, whereArgs);
	}

	/**
	 * Manually build the bounds of the feature table
	 * 
	 * @return bounding box
	 */
	public BoundingBox getBoundingBox() {

		GeometryEnvelope envelope = null;

		long offset = 0;
		boolean hasResults = true;

		String[] columns = new String[] { featureDao.getGeometryColumnName() };

		while (hasResults) {

			hasResults = false;

			FeatureResultSet resultSet = featureDao.queryForChunk(columns,
					chunkLimit, offset);
			try {
				while (resultSet.moveToNext()) {
					hasResults = true;

					FeatureRow featureRow = resultSet.getRow();
					GeometryEnvelope featureEnvelope = featureRow
							.getGeometryEnvelope();
					if (featureEnvelope != null) {

						if (envelope == null) {
							envelope = featureEnvelope;
						} else {
							envelope = envelope.union(featureEnvelope);
						}

					}
				}
			} finally {
				resultSet.close();
			}

			offset += chunkLimit;
		}

		BoundingBox boundingBox = null;
		if (envelope != null) {
			boundingBox = new BoundingBox(envelope);
		}

		return boundingBox;
	}

	/**
	 * Manually build the bounds of the feature table in the provided projection
	 * 
	 * @param projection
	 *            desired projection
	 * @return bounding box
	 */
	public BoundingBox getBoundingBox(Projection projection) {
		BoundingBox boundingBox = getBoundingBox();
		if (boundingBox != null && projection != null) {
			GeometryTransform projectionTransform = GeometryTransform
					.create(featureDao.getProjection(), projection);
			boundingBox = boundingBox.transform(projectionTransform);
		}
		return boundingBox;
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @return results
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox) {
		return query(false, boundingBox);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox) {
		return query(distinct, boundingBox.buildEnvelope());
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox) {
		return query(false, columns, boundingBox);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox) {
		return query(distinct, columns, boundingBox.buildEnvelope());
	}

	/**
	 * Manually count the rows within the bounding box
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @return count
	 */
	public long count(BoundingBox boundingBox) {
		return count(boundingBox.buildEnvelope());
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox,
			Map<String, Object> fieldValues) {
		return query(false, boundingBox, fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox, Map<String, Object> fieldValues) {
		return query(distinct, boundingBox.buildEnvelope(), fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox, Map<String, Object> fieldValues) {
		return query(false, columns, boundingBox, fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox, Map<String, Object> fieldValues) {
		return query(distinct, columns, boundingBox.buildEnvelope(),
				fieldValues);
	}

	/**
	 * Manually count the rows within the bounding box
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param fieldValues
	 *            field values
	 * @return count
	 * @since 3.4.0
	 */
	public long count(BoundingBox boundingBox,
			Map<String, Object> fieldValues) {
		return count(boundingBox.buildEnvelope(), fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox,
			String where) {
		return query(false, boundingBox, where);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox, String where) {
		return query(distinct, boundingBox, where, null);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox, String where) {
		return query(false, columns, boundingBox, where);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox, String where) {
		return query(distinct, columns, boundingBox, where, null);
	}

	/**
	 * Manually count the rows within the bounding box
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @return count
	 * @since 3.4.0
	 */
	public long count(BoundingBox boundingBox, String where) {
		return count(boundingBox, where, null);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox,
			String where, String[] whereArgs) {
		return query(false, boundingBox, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox, String where, String[] whereArgs) {
		return query(distinct, boundingBox.buildEnvelope(), where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox, String where, String[] whereArgs) {
		return query(false, columns, boundingBox, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounding box
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox, String where, String[] whereArgs) {
		return query(distinct, columns, boundingBox.buildEnvelope(), where,
				whereArgs);
	}

	/**
	 * Manually count the rows within the bounding box
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
	public long count(BoundingBox boundingBox, String where,
			String[] whereArgs) {
		return count(boundingBox.buildEnvelope(), where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @return results
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox,
			Projection projection) {
		return query(false, boundingBox, projection);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox, Projection projection) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return query(distinct, featureBoundingBox);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox, Projection projection) {
		return query(false, columns, boundingBox, projection);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox, Projection projection) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return query(distinct, columns, featureBoundingBox);
	}

	/**
	 * Manually count the rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @return count
	 */
	public long count(BoundingBox boundingBox, Projection projection) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return count(featureBoundingBox);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox,
			Projection projection, Map<String, Object> fieldValues) {
		return query(false, boundingBox, projection, fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox, Projection projection,
			Map<String, Object> fieldValues) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return query(distinct, featureBoundingBox, fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox, Projection projection,
			Map<String, Object> fieldValues) {
		return query(false, columns, boundingBox, projection, fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox, Projection projection,
			Map<String, Object> fieldValues) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return query(distinct, columns, featureBoundingBox, fieldValues);
	}

	/**
	 * Manually count the rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param fieldValues
	 *            field values
	 * @return count
	 * @since 3.4.0
	 */
	public long count(BoundingBox boundingBox, Projection projection,
			Map<String, Object> fieldValues) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return count(featureBoundingBox, fieldValues);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox,
			Projection projection, String where) {
		return query(false, boundingBox, projection, where);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox, Projection projection, String where) {
		return query(distinct, boundingBox, projection, where, null);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox, Projection projection, String where) {
		return query(false, columns, boundingBox, projection, where);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox, Projection projection, String where) {
		return query(distinct, columns, boundingBox, projection, where, null);
	}

	/**
	 * Manually count the rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @return count
	 * @since 3.4.0
	 */
	public long count(BoundingBox boundingBox, Projection projection,
			String where) {
		return count(boundingBox, projection, where, null);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(BoundingBox boundingBox,
			Projection projection, String where, String[] whereArgs) {
		return query(false, boundingBox, projection, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			BoundingBox boundingBox, Projection projection, String where,
			String[] whereArgs) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return query(distinct, featureBoundingBox, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			BoundingBox boundingBox, Projection projection, String where,
			String[] whereArgs) {
		return query(false, columns, boundingBox, projection, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounding box in the provided
	 * projection
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			BoundingBox boundingBox, Projection projection, String where,
			String[] whereArgs) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return query(distinct, columns, featureBoundingBox, where, whereArgs);
	}

	/**
	 * Manually count the rows within the bounding box in the provided
	 * projection
	 * 
	 * @param boundingBox
	 *            bounding box
	 * @param projection
	 *            projection
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return count
	 * @since 3.4.0
	 */
	public long count(BoundingBox boundingBox, Projection projection,
			String where, String[] whereArgs) {
		BoundingBox featureBoundingBox = featureDao
				.projectBoundingBox(boundingBox, projection);
		return count(featureBoundingBox, where, whereArgs);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @return results
	 */
	public ManualFeatureQueryResults query(GeometryEnvelope envelope) {
		return query(false, envelope);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param envelope
	 *            geometry envelope
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			GeometryEnvelope envelope) {
		return query(distinct, envelope.getMinX(), envelope.getMinY(),
				envelope.getMaxX(), envelope.getMaxY());
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			GeometryEnvelope envelope) {
		return query(false, columns, envelope);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			GeometryEnvelope envelope) {
		return query(distinct, columns, envelope.getMinX(), envelope.getMinY(),
				envelope.getMaxX(), envelope.getMaxY());
	}

	/**
	 * Manually count the rows within the geometry envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @return count
	 */
	public long count(GeometryEnvelope envelope) {
		return count(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(),
				envelope.getMaxY());
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(GeometryEnvelope envelope,
			Map<String, Object> fieldValues) {
		return query(false, envelope, fieldValues);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param envelope
	 *            geometry envelope
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			GeometryEnvelope envelope, Map<String, Object> fieldValues) {
		return query(distinct, envelope.getMinX(), envelope.getMinY(),
				envelope.getMaxX(), envelope.getMaxY(), fieldValues);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			GeometryEnvelope envelope, Map<String, Object> fieldValues) {
		return query(false, columns, envelope, fieldValues);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			GeometryEnvelope envelope, Map<String, Object> fieldValues) {
		return query(distinct, columns, envelope.getMinX(), envelope.getMinY(),
				envelope.getMaxX(), envelope.getMaxY(), fieldValues);
	}

	/**
	 * Manually count the rows within the geometry envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param fieldValues
	 *            field values
	 * @return count
	 * @since 3.4.0
	 */
	public long count(GeometryEnvelope envelope,
			Map<String, Object> fieldValues) {
		return count(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(),
				envelope.getMaxY(), fieldValues);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(GeometryEnvelope envelope,
			String where) {
		return query(false, envelope, where);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			GeometryEnvelope envelope, String where) {
		return query(distinct, envelope, where, null);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			GeometryEnvelope envelope, String where) {
		return query(false, columns, envelope, where);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			GeometryEnvelope envelope, String where) {
		return query(distinct, columns, envelope, where, null);
	}

	/**
	 * Manually count the rows within the geometry envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @return count
	 * @since 3.4.0
	 */
	public long count(GeometryEnvelope envelope, String where) {
		return count(envelope, where, null);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(GeometryEnvelope envelope,
			String where, String[] whereArgs) {
		return query(false, envelope, where, whereArgs);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct,
			GeometryEnvelope envelope, String where, String[] whereArgs) {
		return query(distinct, envelope.getMinX(), envelope.getMinY(),
				envelope.getMaxX(), envelope.getMaxY(), where, whereArgs);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns,
			GeometryEnvelope envelope, String where, String[] whereArgs) {
		return query(false, columns, envelope, where, whereArgs);
	}

	/**
	 * Manually query for rows within the geometry envelope
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			GeometryEnvelope envelope, String where, String[] whereArgs) {
		return query(distinct, columns, envelope.getMinX(), envelope.getMinY(),
				envelope.getMaxX(), envelope.getMaxY(), where, whereArgs);
	}

	/**
	 * Manually count the rows within the geometry envelope
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
	public long count(GeometryEnvelope envelope, String where,
			String[] whereArgs) {
		return count(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(),
				envelope.getMaxY(), where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @return results
	 */
	public ManualFeatureQueryResults query(double minX, double minY,
			double maxX, double maxY) {
		return query(false, minX, minY, maxX, maxY);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, double minX,
			double minY, double maxX, double maxY) {
		return query(distinct, minX, minY, maxX, maxY, null, null);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns, double minX,
			double minY, double maxX, double maxY) {
		return query(false, columns, minX, minY, maxX, maxY);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			double minX, double minY, double maxX, double maxY) {
		return query(distinct, columns, minX, minY, maxX, maxY, null, null);
	}

	/**
	 * Manually count the rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @return count
	 */
	public long count(double minX, double minY, double maxX, double maxY) {
		return query(minX, minY, maxX, maxY).count();
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(double minX, double minY,
			double maxX, double maxY, Map<String, Object> fieldValues) {
		return query(false, minX, minY, maxX, maxY, fieldValues);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, double minX,
			double minY, double maxX, double maxY,
			Map<String, Object> fieldValues) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return query(distinct, minX, minY, maxX, maxY, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns, double minX,
			double minY, double maxX, double maxY,
			Map<String, Object> fieldValues) {
		return query(false, columns, minX, minY, maxX, maxY, fieldValues);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param fieldValues
	 *            field values
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			double minX, double minY, double maxX, double maxY,
			Map<String, Object> fieldValues) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return query(distinct, columns, minX, minY, maxX, maxY, where,
				whereArgs);
	}

	/**
	 * Manually count the rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param fieldValues
	 *            field values
	 * @return count
	 * @since 3.4.0
	 */
	public long count(double minX, double minY, double maxX, double maxY,
			Map<String, Object> fieldValues) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return count(minX, minY, maxX, maxY, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(double minX, double minY,
			double maxX, double maxY, String where) {
		return query(false, minX, minY, maxX, maxY, where);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, double minX,
			double minY, double maxX, double maxY, String where) {
		return query(distinct, minX, minY, maxX, maxY, where, null);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns, double minX,
			double minY, double maxX, double maxY, String where) {
		return query(false, columns, minX, minY, maxX, maxY, where);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			double minX, double minY, double maxX, double maxY, String where) {
		return query(distinct, columns, minX, minY, maxX, maxY, where, null);
	}

	/**
	 * Manually count the rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @return count
	 * @since 3.4.0
	 */
	public long count(double minX, double minY, double maxX, double maxY,
			String where) {
		return count(minX, minY, maxX, maxY, where, null);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where args
	 * @return results
	 * @since 3.4.0
	 */
	public ManualFeatureQueryResults query(double minX, double minY,
			double maxX, double maxY, String where, String[] whereArgs) {
		return query(false, minX, minY, maxX, maxY, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where args
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, double minX,
			double minY, double maxX, double maxY, String where,
			String[] whereArgs) {
		return query(distinct, featureDao.getColumnNames(), minX, minY, maxX,
				maxY, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where args
	 * @return results
	 * @since 3.5.0
	 */
	public ManualFeatureQueryResults query(String[] columns, double minX,
			double minY, double maxX, double maxY, String where,
			String[] whereArgs) {
		return query(false, columns, minX, minY, maxX, maxY, where, whereArgs);
	}

	/**
	 * Manually query for rows within the bounds
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where args
	 * @return results
	 * @since 4.0.0
	 */
	public ManualFeatureQueryResults query(boolean distinct, String[] columns,
			double minX, double minY, double maxX, double maxY, String where,
			String[] whereArgs) {

		List<Long> featureIds = new ArrayList<>();

		long offset = 0;
		boolean hasResults = true;

		minX -= tolerance;
		maxX += tolerance;
		minY -= tolerance;
		maxY += tolerance;

		String[] queryColumns = featureDao.getIdAndGeometryColumnNames();

		while (hasResults) {

			hasResults = false;

			FeatureResultSet resultSet = featureDao.queryForChunk(distinct,
					queryColumns, where, whereArgs, chunkLimit, offset);
			try {
				while (resultSet.moveToNext()) {
					hasResults = true;

					FeatureRow featureRow = resultSet.getRow();
					GeometryEnvelope envelope = featureRow
							.getGeometryEnvelope();
					if (envelope != null) {

						double minXMax = Math.max(minX, envelope.getMinX());
						double maxXMin = Math.min(maxX, envelope.getMaxX());
						double minYMax = Math.max(minY, envelope.getMinY());
						double maxYMin = Math.min(maxY, envelope.getMaxY());

						if (minXMax <= maxXMin && minYMax <= maxYMin) {
							featureIds.add(featureRow.getId());
						}

					}
				}
			} finally {
				resultSet.close();
			}

			offset += chunkLimit;
		}

		ManualFeatureQueryResults results = new ManualFeatureQueryResults(
				featureDao, columns, featureIds);

		return results;
	}

	/**
	 * Manually count the rows within the bounds
	 * 
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where args
	 * @return count
	 * @since 3.4.0
	 */
	public long count(double minX, double minY, double maxX, double maxY,
			String where, String[] whereArgs) {
		return query(minX, minY, maxX, maxY, where, whereArgs).count();
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param limit
	 *            chunk limit
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(int limit) {
		return featureDao.queryForChunk(limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(int limit, long offset) {
		return featureDao.queryForChunk(limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String orderBy, int limit) {
		return featureDao.queryForChunk(orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String orderBy, int limit,
			long offset) {
		return featureDao.queryForChunk(orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param limit
	 *            chunk limit
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, int limit) {
		return featureDao.queryForChunk(distinct, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, int limit,
			long offset) {
		return featureDao.queryForChunk(distinct, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String orderBy,
			int limit) {
		return featureDao.queryForChunk(distinct, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String orderBy,
			int limit, long offset) {
		return featureDao.queryForChunk(distinct, orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, int limit) {
		return featureDao.queryForChunk(columns, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, int limit,
			long offset) {
		return featureDao.queryForChunk(columns, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String orderBy,
			int limit) {
		return featureDao.queryForChunk(columns, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String orderBy,
			int limit, long offset) {
		return featureDao.queryForChunk(columns, orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			int limit) {
		return featureDao.queryForChunk(distinct, columns, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			int limit, long offset) {
		return featureDao.queryForChunk(distinct, columns, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			String orderBy, int limit) {
		return featureDao.queryForChunk(distinct, columns, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			String orderBy, int limit, long offset) {
		return featureDao.queryForChunk(distinct, columns, orderBy, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(Map<String, Object> fieldValues,
			int limit) {
		return queryForChunk(false, fieldValues, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(Map<String, Object> fieldValues,
			int limit, long offset) {
		return queryForChunk(false, fieldValues, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(Map<String, Object> fieldValues,
			String orderBy, int limit) {
		return queryForChunk(false, fieldValues, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(Map<String, Object> fieldValues,
			String orderBy, int limit, long offset) {
		return queryForChunk(false, fieldValues, orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct,
			Map<String, Object> fieldValues, int limit) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, where, whereArgs, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct,
			Map<String, Object> fieldValues, int limit, long offset) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, where, whereArgs, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct,
			Map<String, Object> fieldValues, String orderBy, int limit) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, where, whereArgs, orderBy,
				limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct,
			Map<String, Object> fieldValues, String orderBy, int limit,
			long offset) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, where, whereArgs, orderBy,
				limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns,
			Map<String, Object> fieldValues, int limit) {
		return queryForChunk(false, columns, fieldValues, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns,
			Map<String, Object> fieldValues, int limit, long offset) {
		return queryForChunk(false, columns, fieldValues, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns,
			Map<String, Object> fieldValues, String orderBy, int limit) {
		return queryForChunk(false, columns, fieldValues, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns,
			Map<String, Object> fieldValues, String orderBy, int limit,
			long offset) {
		return queryForChunk(false, columns, fieldValues, orderBy, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			Map<String, Object> fieldValues, int limit) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, columns, where, whereArgs,
				limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			Map<String, Object> fieldValues, int limit, long offset) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, columns, where, whereArgs,
				limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			Map<String, Object> fieldValues, String orderBy, int limit) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, columns, where, whereArgs,
				orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param fieldValues
	 *            field values
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			Map<String, Object> fieldValues, String orderBy, int limit,
			long offset) {
		String where = featureDao.buildWhere(fieldValues.entrySet());
		String[] whereArgs = featureDao.buildWhereArgs(fieldValues.values());
		return featureDao.queryForChunk(distinct, columns, where, whereArgs,
				orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(String where, int limit) {
		return featureDao.queryForChunk(where, new String[] {}, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(String where, int limit,
			long offset) {
		return featureDao.queryForChunk(where, new String[] {}, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String where, String orderBy,
			int limit) {
		return featureDao.queryForChunk(where, new String[] {}, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String where, String orderBy,
			int limit, long offset) {
		return featureDao.queryForChunk(where, new String[] {}, orderBy, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(boolean distinct, String where,
			int limit) {
		return featureDao.queryForChunk(distinct, where, new String[] {},
				limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(boolean distinct, String where,
			int limit, long offset) {
		return featureDao.queryForChunk(distinct, where, new String[] {}, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String where,
			String orderBy, int limit) {
		return featureDao.queryForChunk(distinct, where, new String[] {},
				orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String where,
			String orderBy, int limit, long offset) {
		return featureDao.queryForChunk(distinct, where, new String[] {},
				orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(String[] columns, String where,
			int limit) {
		return featureDao.queryForChunk(columns, where, new String[] {}, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(String[] columns, String where,
			int limit, long offset) {
		return featureDao.queryForChunk(columns, where, new String[] {}, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String where,
			String orderBy, int limit) {
		return featureDao.queryForChunk(columns, where, new String[] {},
				orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String where,
			String orderBy, int limit, long offset) {
		return featureDao.queryForChunk(columns, where, new String[] {},
				orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(boolean distinct,
			String[] columns, String where, int limit) {
		return featureDao.queryForChunk(distinct, columns, where,
				new String[] {}, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunkWhere(boolean distinct,
			String[] columns, String where, int limit, long offset) {
		return featureDao.queryForChunk(distinct, columns, where,
				new String[] {}, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			String where, String orderBy, int limit) {
		return featureDao.queryForChunk(distinct, columns, where,
				new String[] {}, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			String where, String orderBy, int limit, long offset) {
		return featureDao.queryForChunk(distinct, columns, where,
				new String[] {}, orderBy, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String where, String[] whereArgs,
			int limit) {
		return featureDao.queryForChunk(where, whereArgs, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String where, String[] whereArgs,
			int limit, long offset) {
		return featureDao.queryForChunk(where, whereArgs, limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String where, String[] whereArgs,
			String orderBy, int limit) {
		return featureDao.queryForChunk(where, whereArgs, orderBy, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String where, String[] whereArgs,
			String orderBy, int limit, long offset) {
		return featureDao.queryForChunk(where, whereArgs, orderBy, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String where,
			String[] whereArgs, int limit) {
		return featureDao.queryForChunk(distinct, where, whereArgs, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String where,
			String[] whereArgs, int limit, long offset) {
		return featureDao.queryForChunk(distinct, where, whereArgs, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String where,
			String[] whereArgs, String orderBy, int limit) {
		return featureDao.queryForChunk(distinct, where, whereArgs, orderBy,
				limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String where,
			String[] whereArgs, String orderBy, int limit, long offset) {
		return featureDao.queryForChunk(distinct, where, whereArgs, orderBy,
				limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String where,
			String[] whereArgs, int limit) {
		return featureDao.queryForChunk(columns, where, whereArgs, limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String where,
			String[] whereArgs, int limit, long offset) {
		return featureDao.queryForChunk(columns, where, whereArgs, limit,
				offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String where,
			String[] whereArgs, String orderBy, int limit) {
		return featureDao.queryForChunk(columns, where, whereArgs, orderBy,
				limit);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(String[] columns, String where,
			String[] whereArgs, String orderBy, int limit, long offset) {
		return featureDao.queryForChunk(columns, where, whereArgs, orderBy,
				limit, offset);
	}

	/**
	 * Query for features, starting at the offset and returning no more than the
	 * limit
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * 
	 * @return feature results
	 * @since 6.1.3
	 */
	public FeatureResultSet queryForChunk(boolean distinct, String[] columns,
			String where, String[] whereArgs, String orderBy, int limit,
			long offset) {
		return featureDao.queryForChunk(distinct, columns, where, whereArgs,
				orderBy, limit, offset);
	}

	/**
	 * Manually query for rows within the geometry envelope, starting at the
	 * offset and returning no more than the limit
	 * 
	 * WARNING: This method must iterate from the 0 offset each time, is
	 * extremely inefficient, and not recommended for use
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param envelope
	 *            geometry envelope
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * @return results
	 * @since 6.1.3
	 */
	public ManualFeatureQueryResults queryForChunk(boolean distinct,
			String[] columns, GeometryEnvelope envelope, String where,
			String[] whereArgs, String orderBy, int limit, long offset) {
		return queryForChunk(distinct, columns, envelope.getMinX(),
				envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY(),
				where, whereArgs, orderBy, limit, offset);
	}

	/**
	 * Manually query for rows within the bounds, starting at the offset and
	 * returning no more than the limit
	 * 
	 * WARNING: This method must iterate from the 0 offset each time, is
	 * extremely inefficient, and not recommended for use
	 * 
	 * @param distinct
	 *            distinct rows
	 * @param columns
	 *            columns
	 * @param minX
	 *            min x
	 * @param minY
	 *            min y
	 * @param maxX
	 *            max x
	 * @param maxY
	 *            max y
	 * @param where
	 *            where clause
	 * @param whereArgs
	 *            where args
	 * @param orderBy
	 *            order by
	 * @param limit
	 *            chunk limit
	 * @param offset
	 *            chunk query offset
	 * @return results
	 * @since 6.1.3
	 */
	public ManualFeatureQueryResults queryForChunk(boolean distinct,
			String[] columns, double minX, double minY, double maxX,
			double maxY, String where, String[] whereArgs, String orderBy,
			int limit, long offset) {

		int index = 0;
		List<Long> featureIds = new ArrayList<>();

		long localOffset = 0;
		boolean hasResults = true;

		minX -= tolerance;
		maxX += tolerance;
		minY -= tolerance;
		maxY += tolerance;

		String[] queryColumns = featureDao.getIdAndGeometryColumnNames();

		while (hasResults) {

			hasResults = false;

			FeatureResultSet resultSet = featureDao.queryForChunk(distinct,
					queryColumns, where, whereArgs, chunkLimit, localOffset);
			try {
				while (resultSet.moveToNext()) {
					hasResults = true;

					FeatureRow featureRow = resultSet.getRow();
					GeometryEnvelope envelope = featureRow
							.getGeometryEnvelope();
					if (envelope != null) {

						double minXMax = Math.max(minX, envelope.getMinX());
						double maxXMin = Math.min(maxX, envelope.getMaxX());
						double minYMax = Math.max(minY, envelope.getMinY());
						double maxYMin = Math.min(maxY, envelope.getMaxY());

						if (minXMax <= maxXMin && minYMax <= maxYMin) {
							if (offset <= index) {
								featureIds.add(featureRow.getId());
								if (featureIds.size() >= limit) {
									break;
								}
							}
							index++;
						}

					}
				}
			} finally {
				resultSet.close();
			}

			localOffset += chunkLimit;
		}

		ManualFeatureQueryResults results = new ManualFeatureQueryResults(
				featureDao, columns, featureIds);

		return results;
	}

}
