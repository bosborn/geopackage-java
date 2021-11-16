package mil.nga.geopackage.extension.rtree;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.ImportGeoPackageTestCase;

/**
 * Test RTree Extension from an imported database
 * 
 * @author osbornb
 */
public class RTreeIndexExtensionImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public RTreeIndexExtensionImportTest() {

	}

	/**
	 * Test RTree
	 * 
	 * @throws SQLException
	 *             upon error
	 */
	@Test
	public void testRTree() throws SQLException {

		RTreeIndexExtensionUtils.testRTree(geoPackage);

	}

}
