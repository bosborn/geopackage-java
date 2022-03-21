package mil.nga.geopackage.features.user;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.ImportGeoPackageTestCase;

/**
 * Test Features from an imported database
 * 
 * @author osbornb
 */
public class FeatureImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public FeatureImportTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testRead() throws SQLException, IOException {

		FeatureUtils.testRead(geoPackage);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testUpdate() throws SQLException, IOException {

		FeatureUtils.testUpdate(geoPackage);

	}

	/**
	 * Test updating with added columns
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testUpdateAddColumns() throws SQLException, IOException {

		FeatureUtils.testUpdateAddColumns(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testCreate() throws SQLException, IOException {

		FeatureUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testDelete() throws SQLException {

		FeatureUtils.testDelete(geoPackage);

	}

	/**
	 * Test Feature DAO primary key modifications and disabling value validation
	 * 
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testPkModifiableAndValueValidation() throws SQLException {

		FeatureUtils.testPkModifiableAndValueValidation(geoPackage);

	}

}
