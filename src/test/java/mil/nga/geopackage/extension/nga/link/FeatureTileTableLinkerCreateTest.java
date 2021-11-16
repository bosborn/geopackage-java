package mil.nga.geopackage.extension.nga.link;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.CreateGeoPackageTestCase;

/**
 * Test Feature Tile Table Linker from a created database
 * 
 * @author osbornb
 */
public class FeatureTileTableLinkerCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public FeatureTileTableLinkerCreateTest() {

	}

	/**
	 * Test link
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testLink() throws SQLException {

		FeatureTileTableLinkerUtils.testLink(geoPackage);

	}

}
