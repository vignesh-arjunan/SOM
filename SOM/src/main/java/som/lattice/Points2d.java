package som.lattice;

import lombok.extern.java.Log;
import som.Constants;

import java.io.Serializable;
import java.util.Random;

@Log
public class Points2d implements Serializable, Constants {
    public double wts[][] = null;
    transient public double diff[][] = null;
    transient public int winners[][] = null;

    Points2d(boolean createDiffsWins) {
        if (createDiffsWins) {
            diff = new double[featuremap_size_x][featuremap_size_y];
            winners = new int[featuremap_size_x][featuremap_size_y];
        }

        wts = new double[featuremap_size_x][featuremap_size_y];

        for (int i = 0; i < featuremap_size_x; i++) {
            for (int j = 0; j < featuremap_size_y; j++) {
                Random rand = new Random();

                this.wts[i][j] = rand.nextDouble() / no_of_lattices;

                // LOGGER.info(this.wts[i][j]);
            }
        }
    }

//    Points2d(boolean createDiffsWins, boolean createWts) {
//        if (createDiffsWins) {
//            diff = new double[featuremap_size_x][featuremap_size_y];
//            winners = new int[featuremap_size_x][featuremap_size_y];
//        }
//
//        if (createWts) {
//            wts = new double[featuremap_size_x][featuremap_size_y];
//
//            for (int i = 0; i < featuremap_size_x; i++) {
//                for (int j = 0; j < featuremap_size_y; j++) {
//                    Random rand = new Random();
//
//                    this.wts[i][j] = rand.nextDouble() / no_of_lattices;
//
//                    // LOGGER.info(this.wts[i][j]);
//                }
//            }
//        }
//    }
//
//    Points2d(String cun, boolean createDiffsWins) {
//        if (createDiffsWins) {
//            diff = new double[featuremap_size_x][featuremap_size_y];
//            winners = new int[featuremap_size_x][featuremap_size_y];
//        }
//
//        wts = new double[featuremap_size_x][featuremap_size_y];
//
//        for (int i = 0; i < featuremap_size_x; i++) {
//            for (int j = 0; j < featuremap_size_y; j++) {
//                Random rand = new Random();
//
//                this.wts[i][j] = rand.nextDouble() / (no_of_lattices * cun_size);
//
//                // LOGGER.info(this.wts[i][j]);
//            }
//        }
//    }
//
//    Points2d(String cun, boolean createDiffsWins, boolean createWts) {
//        if (createDiffsWins) {
//            diff = new double[featuremap_size_x][featuremap_size_y];
//            winners = new int[featuremap_size_x][featuremap_size_y];
//        }
//
//        if (createWts) {
//            wts = new double[featuremap_size_x][featuremap_size_y];
//
//            for (int i = 0; i < featuremap_size_x; i++) {
//                for (int j = 0; j < featuremap_size_y; j++) {
//                    Random rand = new Random();
//
//                    this.wts[i][j] = rand.nextDouble() / (no_of_lattices * cun_size);
//
//                    // LOGGER.info(this.wts[i][j]);
//                }
//            }
//        }
//    }
}