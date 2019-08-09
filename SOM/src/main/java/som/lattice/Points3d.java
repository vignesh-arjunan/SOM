package som.lattice;

import lombok.extern.java.Log;
import som.Constants;

import java.io.Serializable;
import java.util.Random;

@Log
public class Points3d implements Serializable, Constants {
    double wts[][][] = null;
    transient public double diff[][] = null;
    transient public int winners[][] = null;

    Points3d(boolean createDiffsWins) {
        if (createDiffsWins) {
            diff = new double[featuremap_size_x][featuremap_size_y];
            winners = new int[featuremap_size_x][featuremap_size_y];
        }

        wts = new double[dimension3_size][featuremap_size_x][featuremap_size_y];

        for (int z = 0; z < dimension3_size; z++) {
            for (int i = 0; i < featuremap_size_x; i++) {
                for (int j = 0; j < featuremap_size_y; j++) {
                    Random rand = new Random();

                    this.wts[z][i][j] = rand.nextDouble() / (no_of_lattices * dimension3_size);

                    // LOGGER.info(this.wts[i][j]);
                }
            }
        }
    }

    Points3d(int cun_size, boolean createDiffsWins, boolean createWts) {
        if (createDiffsWins) {
            diff = new double[featuremap_size_x][featuremap_size_y];
            winners = new int[featuremap_size_x][featuremap_size_y];
        }

        if (createWts) {
            wts = new double[dimension3_size][featuremap_size_x][featuremap_size_y];

            for (int z = 0; z < dimension3_size; z++) {
                for (int i = 0; i < featuremap_size_x; i++) {
                    for (int j = 0; j < featuremap_size_y; j++) {
                        Random rand = new Random();

                        this.wts[z][i][j] = rand.nextDouble() / (no_of_lattices * cun_size * dimension3_size);

                        // LOGGER.info(this.wts[i][j]);
                    }
                }
            }
        }
    }

    Points3d(int cun_size, boolean createDiffsWins, int winners[][]) {
        if (createDiffsWins) {
            diff = new double[featuremap_size_x][featuremap_size_y];
            winners = new int[featuremap_size_x][featuremap_size_y];
        }

        wts = new double[dimension3_size][featuremap_size_x][featuremap_size_y];

        for (int i = 0; i < featuremap_size_x; i++) {
            for (int j = 0; j < featuremap_size_y; j++) {
                if (winners[i][j] == 0) {
                    for (int z = 0; z < dimension3_size; z++) {
                        Random rand = new Random();

                        this.wts[z][i][j] = rand.nextDouble() / (no_of_lattices * cun_size * dimension3_size);

                        // LOGGER.info(this.wts[i][j]);
                    }
                }
            }
        }
    }
}