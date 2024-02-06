package mil.nga.geopackage.extension.schema.columns;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.ImportGeoPackageTestCase;

/**
 * Test Data Columns from an imported database
 * 
 * @author osbornb
 */
public class DataColumnsImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public DataColumnsImportTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		DataColumnsUtils.testRead(geoPackage, null);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		DataColumnsUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		DataColumnsUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		DataColumnsUtils.testDelete(geoPackage);

	}

	/**
	 * Test create column titles
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testColumnTitles() throws SQLException {

		DataColumnsUtils.testColumnTitles(geoPackage);

	}

	/**
	 * Test save and load schema
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testSaveLoadSchema() throws SQLException {

		DataColumnsUtils.testSaveLoadSchema(geoPackage);

	}

}
