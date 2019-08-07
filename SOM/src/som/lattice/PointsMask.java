package som.lattice;

//~--- non-JDK imports --------------------------------------------------------

import som.Constants;

//~--- JDK imports ------------------------------------------------------------

/**
 * <p>Title: govinda</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author vignesh
 * @version 1.0
 */
import java.io.Serializable;

import java.util.logging.Logger;

public class PointsMask implements Serializable, Constants {
    private final static Logger LOGGER   = Logger.getLogger(PointsMask.class.getName());
    public boolean              mask[][] = new boolean[featuremap_size_x][featuremap_size_y];
    public boolean              done     = false;
}


//~ Formatted by Jindent --- http://www.jindent.com
