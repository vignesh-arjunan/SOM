package som.lattice;

import lombok.extern.java.Log;
import som.Constants;

import java.io.Serializable;

@Log
public class PointsMask implements Serializable, Constants {
    public boolean mask[][] = new boolean[featuremap_size_x][featuremap_size_y];
    public boolean done = false;
}