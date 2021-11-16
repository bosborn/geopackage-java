package mil.nga.geopackage.extension.related.media;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.ImportGeoPackageTestCase;

/**
 * Test Related Media Tables from an imported database
 * 
 * @author osbornb
 */
public class RelatedMediaImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public RelatedMediaImportTest() {

	}

	/**
	 * Test related media tables
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testMedia() throws Exception {

		RelatedMediaUtils.testMedia(geoPackage);

	}

}
