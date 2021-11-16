package mil.nga.geopackage.extension.nga.index;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.CreateGeoPackageTestCase;

/**
 * Test Feature Table Index from a created database
 * 
 * @author osbornb
 */
public class FeatureTableIndexCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public FeatureTableIndexCreateTest() {

	}

	/**
	 * Test index
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testIndex() throws SQLException, IOException {

		FeatureTableIndexUtils.testIndex(geoPackage);

	}

	/**
	 * Test delete all table indices
	 * 
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testDeleteAll() throws SQLException {

		FeatureTableIndexUtils.testDeleteAll(geoPackage);

	}

	@Override
	public boolean allowEmptyFeatures() {
		return false;
	}

}
