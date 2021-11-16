package mil.nga.geopackage;

import java.sql.SQLException;

import org.junit.Test;

/**
 * Test GeoPackage from a created database
 * 
 * @author osbornb
 */
public class GeoPackageCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GeoPackageCreateTest() {

	}

	/**
	 * Test create feature table with metadata
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreateFeatureTableWithMetadata() throws SQLException {

		GeoPackageTestUtils.testCreateFeatureTableWithMetadata(geoPackage);

	}

	/**
	 * Test create feature table with metadata and id column
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreateFeatureTableWithMetadataIdColumn()
			throws SQLException {

		GeoPackageTestUtils
				.testCreateFeatureTableWithMetadataIdColumn(geoPackage);

	}

	/**
	 * Test create feature table with metadata and additional columns
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreateFeatureTableWithMetadataAdditionalColumns()
			throws SQLException {

		GeoPackageTestUtils.testCreateFeatureTableWithMetadataAdditionalColumns(
				geoPackage);

	}

	/**
	 * Test create feature table with metadata, id column, and additional
	 * columns
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreateFeatureTableWithMetadataIdColumnAdditionalColumns()
			throws SQLException {

		GeoPackageTestUtils
				.testCreateFeatureTableWithMetadataIdColumnAdditionalColumns(
						geoPackage);

	}

	/**
	 * Test delete tables
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDeleteTables() throws SQLException {

		GeoPackageTestUtils.testDeleteTables(geoPackage);

	}

	/**
	 * Test bounds
	 * 
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testBounds() throws SQLException {

		GeoPackageTestUtils.testBounds(geoPackage);

	}

	/**
	 * Test vacuum
	 */
	@Test
	public void testVacuum() {

		GeoPackageTestUtils.testVacuum(geoPackage);

	}

	/**
	 * Test table types
	 * 
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testTableTypes() throws SQLException {

		GeoPackageTestUtils.testTableTypes(geoPackage);

	}

}
