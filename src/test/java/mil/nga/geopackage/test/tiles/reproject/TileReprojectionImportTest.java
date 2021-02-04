package mil.nga.geopackage.test.tiles.reproject;

import java.io.IOException;

import org.junit.Test;

import mil.nga.geopackage.test.ImportGeoPackageTestCase;

/**
 * Test Tile Reprojection from an imported database
 * 
 * @author osbornb
 */
public class TileReprojectionImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public TileReprojectionImportTest() {

	}

	/**
	 * Test reproject
	 */
	@Test
	public void testReproject() {

		TileReprojectionUtils.testReproject(geoPackage);

	}

	/**
	 * Test reproject replacing the table
	 */
	@Test
	public void testReprojectReplace() {

		TileReprojectionUtils.testReprojectReplace(geoPackage);

	}

	/**
	 * Test reproject of individual zoom levels
	 */
	@Test
	public void testReprojectZoomLevels() {

		TileReprojectionUtils.testReprojectZoomLevels(geoPackage);

	}

	/**
	 * Test reproject of overwriting a zoom level
	 * 
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testReprojectZoomOverwrite() throws IOException {

		TileReprojectionUtils.testReprojectZoomOverwrite(geoPackage);

	}

	/**
	 * Test reproject of overwriting a table
	 */
	@Test
	public void testReprojectOverwrite() {

		TileReprojectionUtils.testReprojectOverwrite(geoPackage);

	}

	/**
	 * Test reproject with zoom level mappings
	 */
	@Test
	public void testReprojectToZoom() {

		TileReprojectionUtils.testReprojectToZoom(geoPackage);

	}

	/**
	 * Test reproject with zoom level matrix and tile length configurations
	 */
	@Test
	public void testReprojectMatrixAndTileLengths() {

		TileReprojectionUtils.testReprojectMatrixAndTileLengths(geoPackage);

	}

	/**
	 * Test reproject with tile optimization
	 */
	@Test
	public void testReprojectOptimize() {

		TileReprojectionUtils.testReprojectOptimize(geoPackage, false);

	}

	/**
	 * Test reproject with tile optimization using world bounds
	 */
	@Test
	public void testReprojectOptimizeWorld() {

		TileReprojectionUtils.testReprojectOptimize(geoPackage, true);

	}

	/**
	 * Test reproject with web mercator tile optimization
	 */
	@Test
	public void testReprojectWebMercator() {

		TileReprojectionUtils.testReprojectWebMercator(geoPackage, false);

	}

	/**
	 * Test reproject with web mercator tile optimization using world bounds
	 */
	@Test
	public void testReprojectWebMercatorWorld() {

		TileReprojectionUtils.testReprojectWebMercator(geoPackage, true);

	}

	/**
	 * Test reproject with platte carre tile optimization
	 */
	@Test
	public void testReprojectPlatteCarre() {

		TileReprojectionUtils.testReprojectPlatteCarre(geoPackage, false);

	}

	/**
	 * Test reproject with platte carre tile optimization using world bounds
	 */
	@Test
	public void testReprojectPlatteCarreWorld() {

		TileReprojectionUtils.testReprojectPlatteCarre(geoPackage, true);

	}

	/**
	 * Test reproject cancel
	 */
	@Test
	public void testReprojectCancel() {

		TileReprojectionUtils.testReprojectCancel(geoPackage);

	}

}
