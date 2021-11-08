package mil.nga.geopackage.test.tiles;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.ImportGeoPackageTestCase;

/**
 * Test URL Tile Generator from an imported database
 * 
 * @author osbornb
 */
public class UrlTileGeneratorImportTest extends ImportGeoPackageTestCase {

	@Override
	public void geoPackageSetUp() throws Exception {
		super.geoPackageSetUp();

		UrlTileGeneratorUtils.checkUrl();
	}

	/**
	 * Constructor
	 */
	public UrlTileGeneratorImportTest() {

	}

	@Test
	public void testGenerateTiles() throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTiles(geoPackage);

	}

	@Test
	public void testGenerateTilesCompress() throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTilesCompress(geoPackage);

	}

	@Test
	public void testGenerateTilesCompressQuality()
			throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTilesCompressQuality(geoPackage);

	}

	@Test
	public void testGenerateTilesXYZ() throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTilesXYZ(geoPackage);

	}

	@Test
	public void testGenerateTilesBounded() throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTilesBounded(geoPackage);

	}

	@Test
	public void testGenerateTilesXYZBounded() throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTilesXYZBounded(geoPackage);

	}

	@Test
	public void testGenerateTilesRandom() throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTilesRandom(geoPackage);

	}

	@Test
	public void testGenerateTilesCompressQualityPng()
			throws SQLException, IOException {

		UrlTileGeneratorUtils.testGenerateTilesCompressQualityPng(geoPackage);

	}

}
