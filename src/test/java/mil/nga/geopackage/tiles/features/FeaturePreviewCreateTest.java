package mil.nga.geopackage.tiles.features;

import java.io.IOException;

import org.junit.Test;

import mil.nga.geopackage.CreateGeoPackageTestCase;

/**
 * Test Feature Preview from a created database
 * 
 * @author osbornb
 */
public class FeaturePreviewCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public FeaturePreviewCreateTest() {

	}

	/**
	 * Test draw
	 * 
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testDraw() throws IOException {

		FeaturePreviewUtils.testDraw(geoPackage);

	}

}
