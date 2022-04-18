package mil.nga.geopackage.geom;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.CreateGeoPackageTestCase;

/**
 * Test GeoPackage Geometry Data from a created database
 * 
 * @author osbornb
 */
public class GeoPackageGeometryDataCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GeoPackageGeometryDataCreateTest() {

	}

	/**
	 * Test reading and writing (and comparing) geometry bytes
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testReadWriteBytes() throws SQLException, IOException {

		GeoPackageGeometryDataUtils.testReadWriteBytes(geoPackage);

	}

	/**
	 * Test geometry projection transform
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testGeometryProjectionTransform()
			throws SQLException, IOException {

		GeoPackageGeometryDataUtils.testGeometryProjectionTransform(geoPackage);

	}

	/**
	 * Test insert geometry bytes
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testInsertGeometryBytes() throws SQLException, IOException {

		GeoPackageGeometryDataUtils.testInsertGeometryBytes(geoPackage);

	}

	/**
	 * Test insert header bytes
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testInsertHeaderBytes() throws SQLException, IOException {

		GeoPackageGeometryDataUtils.testInsertHeaderBytes(geoPackage);

	}

	/**
	 * Test insert header and geometry bytes
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testInsertHeaderAndGeometryBytes()
			throws SQLException, IOException {

		GeoPackageGeometryDataUtils
				.testInsertHeaderAndGeometryBytes(geoPackage);

	}

	/**
	 * Test insert bytes
	 * 
	 * @throws SQLException
	 *             upon error
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testInsertBytes() throws SQLException, IOException {

		GeoPackageGeometryDataUtils.testInsertBytes(geoPackage);

	}

}
