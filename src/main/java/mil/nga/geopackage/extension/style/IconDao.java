package mil.nga.geopackage.extension.style;

import mil.nga.geopackage.extension.related.media.MediaDao;
import mil.nga.geopackage.user.custom.UserCustomDao;
import mil.nga.geopackage.user.custom.UserCustomResultSet;
import mil.nga.geopackage.user.custom.UserCustomRow;

/**
 * Icon DAO for reading icon tables
 * 
 * @author osbornb
 * @since 3.1.1
 */
public class IconDao extends MediaDao {

	/**
	 * Constructor
	 * 
	 * @param dao
	 *            user custom data access object
	 */
	public IconDao(UserCustomDao dao) {
		super(dao, new IconTable(dao.getTable()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IconTable getTable() {
		return (IconTable) super.getTable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IconRow newRow() {
		return new IconRow(getTable());
	}

	/**
	 * Get the icon row from the current result set location
	 * 
	 * @param resultSet
	 *            result set
	 * @return icon row
	 */
	public IconRow getRow(UserCustomResultSet resultSet) {
		return getRow(resultSet.getRow());
	}

	/**
	 * Get a icon row from the user custom row
	 * 
	 * @param row
	 *            custom row
	 * @return icon row
	 */
	public IconRow getRow(UserCustomRow row) {
		return new IconRow(row);
	}

}