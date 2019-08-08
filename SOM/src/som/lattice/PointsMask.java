package som.lattice;

import som.Constants;

import java.io.Serializable;
import java.util.logging.Logger;

public class PointsMask implements Serializable, Constants {
    private final static Logger LOGGER   = Logger.getLogger(PointsMask.class.getName());
    public boolean              mask[][] = new boolean[featuremap_size_x][featuremap_size_y];
    public boolean              done     = false;
}