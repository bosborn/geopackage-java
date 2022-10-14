package mil.nga.geopackage.dgiwg;

/**
 * DGIWG Example Create parts
 * 
 * @author osbornb
 */
public class DGIWGExampleCreate {

	/**
	 * Create features
	 */
	public boolean features = false;

	/**
	 * Create tiles
	 */
	public boolean tiles = false;

	/**
	 * Create the base
	 * 
	 * @return create
	 */
	public static DGIWGExampleCreate base() {
		return new DGIWGExampleCreate();
	}

	/**
	 * Create all parts
	 * 
	 * @return create
	 */
	public static DGIWGExampleCreate all() {
		DGIWGExampleCreate create = base();
		create.features = true;
		create.tiles = true;
		return create;
	}

	/**
	 * Create features
	 * 
	 * @return create
	 */
	public static DGIWGExampleCreate features() {
		DGIWGExampleCreate create = base();
		create.features = true;
		return create;
	}

	/**
	 * Create tiles
	 * 
	 * @return create
	 */
	public static DGIWGExampleCreate tiles() {
		DGIWGExampleCreate create = base();
		create.tiles = true;
		return create;
	}

}