package som.lattice;

import lombok.extern.java.Log;
import som.Constants;

import java.io.Serializable;

@Log
public class Lattice2d implements Serializable, Constants {
    public Points2d pts[][][] = null;

    Lattice2d(boolean createDiffsWins) {
        this.pts = new Points2d[1][lattice_size_x][lattice_size_y];

        for (int i = 0; i < lattice_size_x; i++) {
            for (int j = 0; j < lattice_size_y; j++) {
                this.pts[0][i][j] = new Points2d(createDiffsWins);
            }
        }
    }
}