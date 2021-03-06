package mil.nga.geopackage.extension.related;

import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.db.GeoPackageConnection;
import mil.nga.geopackage.extension.related.media.MediaDao;
import mil.nga.geopackage.extension.related.media.MediaTable;
import mil.nga.geopackage.extension.related.simple.SimpleAttributesDao;
import mil.nga.geopackage.extension.related.simple.SimpleAttributesTable;
import mil.nga.geopackage.user.custom.UserCustomDao;
import mil.nga.geopackage.user.custom.UserCustomResultSet;

/**
 * Related Tables extension
 * 
 * @author jyutzler
 * @author osbornb
 * @since 3.0.1
 */
public class RelatedTablesExtension extends RelatedTablesCoreExtension {

	/**
	 * GeoPackage connection
	 */
	private GeoPackageConnection connection;

	/**
	 * Constructor
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * 
	 */
	public RelatedTablesExtension(GeoPackage geoPackage) {
		super(geoPackage);
		connection = geoPackage.getConnection();
	}

	/**
	 * Get a User Custom DAO from a table name
	 * 
	 * @param tableName
	 *            table name
	 * @return user custom dao
	 */
	public UserCustomDao getUserDao(String tableName) {
		return UserCustomDao.readTable(getGeoPackage().getName(), connection,
				tableName);
	}

	/**
	 * Get a User Mapping DAO from an extended relation
	 * 
	 * @param extendedRelation
	 *            extended relation
	 * @return user mapping dao
	 */
	public UserMappingDao getMappingDao(ExtendedRelation extendedRelation) {
		return getMappingDao(extendedRelation.getMappingTableName());
	}

	/**
	 * Get a User Mapping DAO from a table name
	 * 
	 * @param tableName
	 *            mapping table name
	 * @return user mapping dao
	 */
	public UserMappingDao getMappingDao(String tableName) {
		return new UserMappingDao(getUserDao(tableName));
	}

	/**
	 * Get a related media table DAO
	 * 
	 * @param mediaTable
	 *            media table
	 * @return media DAO
	 */
	public MediaDao getMediaDao(MediaTable mediaTable) {
		return getMediaDao(mediaTable.getTableName());
	}

	/**
	 * Get a related media table DAO
	 * 
	 * @param extendedRelation
	 *            extended relation
	 * @return media DAO
	 */
	public MediaDao getMediaDao(ExtendedRelation extendedRelation) {
		return getMediaDao(extendedRelation.getRelatedTableName());
	}

	/**
	 * Get a related media table DAO
	 * 
	 * @param tableName
	 *            media table name
	 * @return media DAO
	 */
	public MediaDao getMediaDao(String tableName) {
		MediaDao mediaDao = new MediaDao(getUserDao(tableName));
		setContents(mediaDao.getTable());
		return mediaDao;
	}

	/**
	 * Get a related simple attributes table DAO
	 * 
	 * @param simpleAttributesTable
	 *            simple attributes table
	 * @return simple attributes DAO
	 */
	public SimpleAttributesDao getSimpleAttributesDao(
			SimpleAttributesTable simpleAttributesTable) {
		return getSimpleAttributesDao(simpleAttributesTable.getTableName());
	}

	/**
	 * Get a related simple attributes table DAO
	 * 
	 * @param extendedRelation
	 *            extended relation
	 * @return simple attributes DAO
	 */
	public SimpleAttributesDao getSimpleAttributesDao(
			ExtendedRelation extendedRelation) {
		return getSimpleAttributesDao(extendedRelation.getRelatedTableName());
	}

	/**
	 * Get a related simple attributes table DAO
	 * 
	 * @param tableName
	 *            simple attributes table name
	 * @return simple attributes DAO
	 */
	public SimpleAttributesDao getSimpleAttributesDao(String tableName) {
		SimpleAttributesDao simpleAttributesDao = new SimpleAttributesDao(
				getUserDao(tableName));
		setContents(simpleAttributesDao.getTable());
		return simpleAttributesDao;
	}

	/**
	 * Get the related id mappings for the base id
	 * 
	 * @param extendedRelation
	 *            extended relation
	 * @param baseId
	 *            base id
	 * @return IDs representing the matching related IDs
	 */
	public List<Long> getMappingsForBase(ExtendedRelation extendedRelation,
			long baseId) {
		return getMappingsForBase(extendedRelation.getMappingTableName(),
				baseId);
	}

	/**
	 * Get the related id mappings for the base id
	 * 
	 * @param tableName
	 *            mapping table name
	 * @param baseId
	 *            base id
	 * @return IDs representing the matching related IDs
	 */
	public List<Long> getMappingsForBase(String tableName, long baseId) {

		List<Long> relatedIds = new ArrayList<>();

		UserMappingDao userMappingDao = getMappingDao(tableName);
		UserCustomResultSet resultSet = userMappingDao.queryByBaseId(baseId);
		try {
			while (resultSet.moveToNext()) {
				UserMappingRow row = userMappingDao.getRow(resultSet);
				relatedIds.add(row.getRelatedId());
			}
		} finally {
			resultSet.close();
		}

		return relatedIds;
	}

	/**
	 * Get the base id mappings for the related id
	 * 
	 * @param extendedRelation
	 *            extended relation
	 * @param relatedId
	 *            related id
	 * @return IDs representing the matching base IDs
	 */
	public List<Long> getMappingsForRelated(ExtendedRelation extendedRelation,
			long relatedId) {
		return getMappingsForRelated(extendedRelation.getMappingTableName(),
				relatedId);
	}

	/**
	 * Get the base id mappings for the related id
	 * 
	 * @param tableName
	 *            mapping table name
	 * @param relatedId
	 *            related id
	 * @return IDs representing the matching base IDs
	 */
	public List<Long> getMappingsForRelated(String tableName, long relatedId) {

		List<Long> baseIds = new ArrayList<>();

		UserMappingDao userMappingDao = getMappingDao(tableName);
		UserCustomResultSet resultSet = userMappingDao
				.queryByRelatedId(relatedId);
		try {
			while (resultSet.moveToNext()) {
				UserMappingRow row = userMappingDao.getRow(resultSet);
				baseIds.add(row.getBaseId());
			}
		} finally {
			resultSet.close();
		}

		return baseIds;
	}

	/**
	 * Determine if the base id and related id mapping exists
	 * 
	 * @param tableName
	 *            mapping table name
	 * @param baseId
	 *            base id
	 * @param relatedId
	 *            related id
	 * @return true if mapping exists
	 * @since 3.2.0
	 */
	public boolean hasMapping(String tableName, long baseId, long relatedId) {
		UserMappingDao userMappingDao = getMappingDao(tableName);
		return userMappingDao.countByIds(baseId, relatedId) > 0;
	}

}
