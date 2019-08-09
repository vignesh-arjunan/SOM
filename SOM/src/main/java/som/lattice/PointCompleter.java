package som.lattice;

import lombok.extern.java.Log;
import som.Constants;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

@Log
public class PointCompleter extends Thread implements Constants {
    private int trigerred_point_index_x = -1;
    private int trigerred_point_index_y = -1;
    private final BlockingQueue<PointCompleter> queue;
    private final int index_x;
    private final int index_y;
    private final Points3d pts[][][];
    private final PointsMask pts_mask[][];
    private final double input[][][][];
    private final double random_mask[][];

    PointCompleter(BlockingQueue<PointCompleter> queue_p, int index_xp, int index_yp, PointsMask pts_mask_p[][],
                   double input_p[][][][], double random_mask_p[][], Points3d pts_p[][][]) {
        queue = queue_p;
        index_x = index_xp;
        index_y = index_yp;
        pts_mask = pts_mask_p;
        input = input_p;
        random_mask = random_mask_p;
        pts = pts_p;
    }

    private boolean checkMaxSum(int max_sum) {

        // LOGGER.info("sum : " + max_sum);
        return max_sum >= z0;
    }

    private void computeWinnersFirst(Lattice3d tmpLt, int indexa, int indexb) {
        for (int k = 0; k < featuremap_size_x; k++) {
            for (int l = 0; l < featuremap_size_y; l++) {
                int temp_index_x1 = 0;
                int temp_index_y1 = 0;
                double temp_diff1 = 0;

                for (int i = 0; i < lattice_size_x; i++) {
                    for (int j = 0; j < lattice_size_y; j++) {
                        double cumulative_wt = 0;

                        for (int z_index = 0; z_index < no_of_lattices; z_index++) {
                            for (int z = 0; z < dimension3_size; z++) {
                                cumulative_wt += this.pts[z_index][i][j].wts[z][k][l];
                            }
                        }

                        tmpLt.pts[0][i][j].diff[k][l] = Math.abs(cumulative_wt - this.input[indexa][indexb][k][l]
                                - this.random_mask[indexa + k][indexb + l]);

                        if ((i == 0) && (j == 0)) {
                            temp_index_x1 = i;
                            temp_index_y1 = j;
                            tmpLt.pts[0][temp_index_x1][temp_index_y1].winners[k][l] = 1;
                            temp_diff1 = tmpLt.pts[0][i][j].diff[k][l];

                            // LOGGER.info("one ");
                        } else {
                            if (tmpLt.pts[0][i][j].diff[k][l] < temp_diff1) {
                                tmpLt.pts[0][temp_index_x1][temp_index_y1].winners[k][l] = 0;
                                temp_index_x1 = i;
                                temp_index_y1 = j;
                                tmpLt.pts[0][temp_index_x1][temp_index_y1].winners[k][l] = 1;
                                temp_diff1 =
                                        tmpLt.pts[0][i][j].diff[k][l];

                                // LOGGER.info("two ");
                            } else {
                                tmpLt.pts[0][i][j].winners[k][l] = 0;

                                // LOGGER.info("three ");
                            }
                        }
                    }
                }
            }
        }
    }

    private void useCunningLattice(Lattice3d tmpLt, int indexa, int indexb) {
        Lattice3d newlt[] = new Lattice3d[2];

        newlt[0] = new Lattice3d(cun_size, true, false);

        // LOGGER.info("after creating newlt");

        for (int z_index = 0; z_index < no_of_lattices; z_index++) {
            double new_wt[][][] = new double[dimension3_size][featuremap_size_x][featuremap_size_y];

            for (int cun_index = 1; cun_index <= cun_size; cun_index++) {
                newlt[1] = new Lattice3d(cun_size, false, tmpLt.pts[0][indexa][indexb].winners);

                for (int k = 0; k < featuremap_size_x; k++) {
                    for (int l = 0; l < featuremap_size_y; l++) {
                        if (tmpLt.pts[0][indexa][indexb].winners[k][l] == 1) {

                            // LOGGER.info("*");
                            continue;
                        }

                        int temp_index_x = 0;
                        int temp_index_y = 0;
                        double temp_diff = 0;

                        for (int i = 0; i < lattice_size_x; i++) {
                            for (int j = 0; j < lattice_size_y; j++) {
                                double cumulative_wt = 0;

                                for (int z = 0; z < dimension3_size; z++) {
                                    cumulative_wt += newlt[1].pts[0][i][j].wts[z][k][l];
                                }

                                newlt[0].pts[0][i][j].diff[k][l] = Math.abs(cumulative_wt
                                        - ((this.input[indexa][indexb][k][l]
                                        + this.random_mask[indexa + k][indexb + l]) / (no_of_lattices * cun_size)));

                                if ((i == 0) && (j == 0)) {
                                    temp_index_x = i;
                                    temp_index_y = j;
                                    newlt[0].pts[0][temp_index_x][temp_index_y].winners[k][l] = 1;
                                    temp_diff =
                                            newlt[0].pts[0][i][j].diff[k][l];

                                    // LOGGER.info("one ");
                                } else {
                                    if (newlt[0].pts[0][i][j].diff[k][l] < temp_diff) {
                                        newlt[0].pts[0][temp_index_x][temp_index_y].winners[k][l] = 0;
                                        temp_index_x = i;
                                        temp_index_y = j;
                                        newlt[0].pts[0][temp_index_x][temp_index_y].winners[k][l] = 1;
                                        temp_diff =
                                                newlt[0].pts[0][i][j].diff[k][l];

                                        // LOGGER.info("two ");
                                    } else {
                                        newlt[0].pts[0][i][j].winners[k][l] = 0;

                                        // LOGGER.info("three ");
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < lattice_size_x; i++) {
                            for (int j = 0; j < lattice_size_y; j++) {
                                if ((newlt[0].pts[0][i][j].winners[k][l] == 1)
                                        && (tmpLt.pts[0][indexa][indexb].winners[k][l] == 0)) {

                                    // LOGGER.info("setting wt, ");
                                    for (int z = 0; z < dimension3_size; z++) {
                                        new_wt[z][k][l] += newlt[1].pts[0][i][j].wts[z][k][l];
                                    }
                                }
                            }
                        }
                    }
                }

                newlt[1] = null;
                System.gc();
            }

            for (int k = 0; k < featuremap_size_x; k++) {
                for (int l = 0; l < featuremap_size_y; l++) {
                    if (tmpLt.pts[0][indexa][indexb].winners[k][l] == 0) {
                        for (int z = 0; z < dimension3_size; z++) {
                            this.pts[z_index][indexa][indexb].wts[z][k][l] = new_wt[z][k][l];
                        }
                    }
                }
            }
        }
    }

    private void computeSumsEds(Lattice3d tmpLt, int indexa, int indexb, int ltSums[][], double ltEds[][]) {
        for (int k = 0; k < featuremap_size_x; k++) {
            for (int l = 0; l < featuremap_size_y; l++) {
                int temp_index_x1 = 0;
                int temp_index_y1 = 0;
                double temp_diff1 = 0;

                for (int i = 0; i < lattice_size_x; i++) {
                    for (int j = 0; j < lattice_size_y; j++) {
                        double cumulative_wt = 0;

                        for (int z_index = 0; z_index < no_of_lattices; z_index++) {
                            for (int z = 0; z < dimension3_size; z++) {
                                cumulative_wt += this.pts[z_index][i][j].wts[z][k][l];
                            }
                        }

                        tmpLt.pts[0][i][j].diff[k][l] = Math.abs(cumulative_wt - this.input[indexa][indexb][k][l]
                                - this.random_mask[indexa + k][indexb + l]);
                        ltEds[i][j] += tmpLt.pts[0][i][j].diff[k][l];

                        if ((i == 0) && (j == 0)) {
                            temp_index_x1 = i;
                            temp_index_y1 = j;
                            tmpLt.pts[0][temp_index_x1][temp_index_y1].winners[k][l] = 1;
                            ltSums[temp_index_x1][temp_index_y1]++;
                            temp_diff1 = tmpLt.pts[0][i][j].diff[k][l];
                        } else {
                            if (tmpLt.pts[0][i][j].diff[k][l] < temp_diff1) {
                                tmpLt.pts[0][temp_index_x1][temp_index_y1].winners[k][l] = 0;
                                ltSums[temp_index_x1][temp_index_y1]--;
                                temp_index_x1 = i;
                                temp_index_y1 = j;
                                tmpLt.pts[0][temp_index_x1][temp_index_y1].winners[k][l] = 1;
                                ltSums[temp_index_x1][temp_index_y1]++;
                                temp_diff1 = tmpLt.pts[0][i][j].diff[k][l];
                            } else {
                                tmpLt.pts[0][i][j].winners[k][l] = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    private void completePoint() {
        boolean point_completed = false;
        boolean computedWinnersFirst = false;
        int no_of_tries = 0;
        Lattice3d tmpLt = new Lattice3d(cun_size, true, false);

        trigerred_point_index_x = 0;
        trigerred_point_index_y = 0;

        while (!(point_completed)) {

            // added for computing winners before hand starts
            if (!(computedWinnersFirst)) {
                computeWinnersFirst(tmpLt, index_x, index_y);
                computedWinnersFirst = true;

                // LOGGER.info("first winners computed");
            }

            // added for computing winners before hand ends
            // LOGGER.info("before creating newlt");
            if (no_of_tries == max_tries) {
                log.info("Aborting point : " + index_x + " " + index_y + " - shall try later");

                break;
            }

            no_of_tries++;
            useCunningLattice(tmpLt, index_x, index_y);

            int ltSums[][] = new int[lattice_size_x][lattice_size_y];
            double ltEds[][] = new double[lattice_size_x][lattice_size_y];
            int max_sum1 = 0;
            double min_ed1 = 0.0;

            computeSumsEds(tmpLt, index_x, index_y, ltSums, ltEds);

            for (int i = 0; i < lattice_size_x; i++) {
                for (int j = 0; j < lattice_size_y; j++) {
                    if ((i == 0) && (j == 0)) {
                        trigerred_point_index_x = i;
                        trigerred_point_index_y = j;
                        max_sum1 = ltSums[i][j];
                        min_ed1 = ltEds[i][j];

                        // LOGGER.info("one ");
                    } else if (ltSums[i][j] > max_sum1) {
                        trigerred_point_index_x = i;
                        trigerred_point_index_y = j;
                        max_sum1 = ltSums[i][j];
                        min_ed1 = ltEds[i][j];

                        // LOGGER.info("two ");
                    } else if ((ltSums[i][j] == max_sum1) && (ltEds[i][j] < min_ed1)) {
                        trigerred_point_index_x = i;
                        trigerred_point_index_y = j;
                        min_ed1 = ltEds[i][j];

                        // LOGGER.info("three ");
                    } else {

                        // LOGGER.info("four ");
                    }
                }
            }

            if (!checkMaxSum(max_sum1 - 10)) {
                log.info("Not Setting max sum: " + max_sum1);
            } else {

                // int overlap_count = 0;
                // for (int k = 0; k < featuremap_size_x; k++) {
                // for (int l = 0; l < featuremap_size_y; l++) {
                // if (this.pts[0][indexa][indexb].winners[k][l] == 1) {
                // if (this.pts_mask[indexa][indexb].mask[k][l]) {
                // overlap_count++;
                // }
                // }
                // }
                // }
                // LOGGER.info("Setting max sum: " + max_sum1 + ", overlap_count: " + overlap_count);
                log.info("Setting max sum: " + max_sum1);
                log.info(trigerred_point_index_x + " " + trigerred_point_index_y);
                this.pts_mask[index_x][index_y].done = true;
                point_completed = true;
            }

            System.gc();
        }
    }

    @Override
    public void run() {
        log.info("Thread started for " + this.index_x + ", " + this.index_y);
        completePoint();
        queue.remove(this);
    }
}