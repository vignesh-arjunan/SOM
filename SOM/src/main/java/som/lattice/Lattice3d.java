package som.lattice;

import lombok.extern.java.Log;
import som.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

@Log
public class Lattice3d implements Serializable, Constants {
    public transient double input[][][][];
    public transient double temp[][];
    private int trigerred_point_index_x = -1;
    private int trigerred_point_index_y = -1;
    public Points3d pts[][][];
    private transient double random_mask[][];
    private transient JFrame jFrame;
    private transient JPanel latticeVisualizer;
    private transient JComponent startStopButton;
    private transient Image image;
    private int n;
    private int m;
    public PointsMask pts_mask[][];

//    Lattice3d(boolean createDiffsWins) {
//        this.pts = new Points3d[1][lattice_size_x][lattice_size_y];
//        for (int i = 0; i < lattice_size_x; i++) {
//            for (int j = 0; j < lattice_size_y; j++) {
//                this.pts[0][i][j] = new Points3d(createDiffsWins);
//            }
//        }
//    }
//
//    Lattice3d(boolean createDiffsWins, boolean createWts) {
//        this.pts = new Points3d[1][lattice_size_x][lattice_size_y];
//        for (int i = 0; i < lattice_size_x; i++) {
//            for (int j = 0; j < lattice_size_y; j++) {
//                this.pts[0][i][j] = new Points3d(createDiffsWins, createWts);
//            }
//        }
//    }
//
//    Lattice3d(int cun_size, boolean createDiffsWins) {
//        this.pts = new Points3d[1][lattice_size_x][lattice_size_y];
//        for (int i = 0; i < lattice_size_x; i++) {
//            for (int j = 0; j < lattice_size_y; j++) {
//                this.pts[0][i][j] = new Points3d(cun_size, createDiffsWins);
//            }
//        }
//    }

    Lattice3d(int cun_size, boolean createDiffsWins, boolean createWts) {
        this.pts = new Points3d[1][lattice_size_x][lattice_size_y];
        for (int i = 0; i < lattice_size_x; i++) {
            for (int j = 0; j < lattice_size_y; j++) {
                this.pts[0][i][j] = new Points3d(cun_size, createDiffsWins, createWts);
            }
        }
    }

    Lattice3d(int cun_size, boolean createDiffsWins, int winners[][]) {
        this.pts = new Points3d[1][lattice_size_x][lattice_size_y];
        for (int i = 0; i < lattice_size_x; i++) {
            for (int j = 0; j < lattice_size_y; j++) {
                this.pts[0][i][j] = new Points3d(cun_size, createDiffsWins, winners);
            }
        }
    }

    public Lattice3d(JFrame fr_p, JPanel latticeVisualizer_p, JComponent startStopButton_p) throws IOException {
        this.pts = new Points3d[no_of_lattices][lattice_size_x][lattice_size_y];
        this.jFrame = fr_p;
        this.latticeVisualizer = latticeVisualizer_p;
        this.startStopButton = startStopButton_p;
        File f = new File(System.getProperty("user.dir") + File.separator + "vignesh_index_right");
        this.temp = new double[actual_input_size_x][actual_input_size_y];
        try (FileInputStream fis = new FileInputStream(f); DataInputStream dis = new DataInputStream(fis)) {
            for (int index1_p = 0; index1_p < actual_input_size_y; index1_p++) {
                for (int index2_p = 0; index2_p < actual_input_size_x; index2_p++) {
                    this.temp[index2_p][index1_p] = dis.readInt();
                    int quo = ((int) this.temp[index2_p][index1_p] + 1) / round;
                    int rem = ((int) this.temp[index2_p][index1_p] + 1) % round;
                    if (rem > round / 2) {
                        this.temp[index2_p][index1_p] = quo * round + round - 1;
                    } else {
                        this.temp[index2_p][index1_p] = quo * round - 1;
                    }

                    if (this.temp[index2_p][index1_p] == -1) {
                        this.temp[index2_p][index1_p] = 0;
                    }
                }
            }
        }

        this.input = new double[lattice_size_x][lattice_size_y][featuremap_size_x][featuremap_size_y];

        for (int i = translation_factor_x; i < lattice_size_x + translation_factor_x; i++) {
            for (int j = translation_factor_y; j < lattice_size_y + translation_factor_y; j++) {
                for (int k = 0; k < featuremap_size_x; k++) {
                    for (int l = 0; l < featuremap_size_x; l++) {
                        this.input[i - translation_factor_x][j - translation_factor_y][k][l] = this.temp[i + k][j + l] / (255 * 2);
                    }
                }
            }
        }

        pts_mask = new PointsMask[lattice_size_x][lattice_size_y];

        for (int z = 0; z < no_of_lattices; z++) {
            for (int i = 0; i < lattice_size_x; i++) {
                for (int j = 0; j < lattice_size_y; j++) {
                    if (z == 0) {
                        this.pts[z][i][j] = new Points3d(true);
                        this.pts_mask[i][j] = new PointsMask();
                    } else {
                        this.pts[z][i][j] = new Points3d(false);
                    }
                }
            }
        }

        int temp_data[] = new int[input_size_x * input_size_y];
        for (int i = translation_factor_x; i < input_size_x + translation_factor_x; i++) {
            for (int j = translation_factor_y; j < input_size_y + translation_factor_y; j++) {
                temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] = (int) this.temp[i][j];
                temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] = (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)]) | (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] << 8) | (temp_data[(j - translation_factor_y)
                        * input_size_x + (i - translation_factor_x)] << 16 | 0xFF000000);
            }
        }
        MemoryImageSource mis = new MemoryImageSource(input_size_x, input_size_y, temp_data, 0, input_size_x);
        if (this.jFrame != null) {
            this.image = this.jFrame.createImage(mis);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        log.log(Level.INFO, "{0}", Runtime.getRuntime().totalMemory());
        log.info(System.getProperty("user.dir"));
        Lattice3d lattice = null;
        File f = new File(System.getProperty("user.dir") + File.separator + "status");
        if (f.exists()) {
            File file = new File(System.getProperty("user.dir") + File.separator + "lattice");
            try (FileInputStream fis1 = new FileInputStream(file.getAbsolutePath()); ObjectInputStream ois1 = new ObjectInputStream(fis1)) {
                lattice = (Lattice3d) ois1.readObject();
            }
            File file1 = new File(System.getProperty("user.dir") + File.separator + "vignesh_index_right");
            try (FileInputStream fis2 = new FileInputStream(file1); DataInputStream dis2 = new DataInputStream(fis2)) {
                lattice.temp = new double[actual_input_size_x][actual_input_size_y];

                for (int index1 = 0; index1 < actual_input_size_y; index1++) {
                    for (int index2 = 0; index2 < actual_input_size_x; index2++) {
                        lattice.temp[index2][index1] = dis2.readInt();
                        int quo = ((int) lattice.temp[index2][index1] + 1) / round;
                        int rem = ((int) lattice.temp[index2][index1] + 1) % round;
                        if (rem > round / 2) {
                            lattice.temp[index2][index1] = quo * round + round - 1;
                        } else {
                            lattice.temp[index2][index1] = quo * round - 1;
                        }

                        if (lattice.temp[index2][index1] == -1) {
                            lattice.temp[index2][index1] = 0;
                        }
                    }
                }
            }

            lattice.input = new double[lattice_size_x][lattice_size_y][featuremap_size_x][featuremap_size_y];

            for (int i = translation_factor_x; i < lattice_size_x + translation_factor_x; i++) {
                for (int j = translation_factor_y; j < lattice_size_y + translation_factor_y; j++) {
                    for (int k = 0; k < featuremap_size_x; k++) {
                        for (int l = 0; l < featuremap_size_x; l++) {
                            lattice.input[i - translation_factor_x][j - translation_factor_y][k][l] = lattice.temp[i + k][j + l] / (255 * 2);
                        }
                    }
                }
            }

            int temp_data[] = new int[input_size_x * input_size_y];
            for (int i = translation_factor_x; i < input_size_x + translation_factor_x; i++) {
                for (int j = translation_factor_y; j < input_size_y + translation_factor_y; j++) {
                    temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] = (int) lattice.temp[i][j];
                    temp_data[(j - translation_factor_y) * input_size_x
                            + (i - translation_factor_x)] = (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)]) | (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] << 8) | (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] << 16 | 0xFF000000);
                }
            }

            for (int i = 0; i < lattice_size_x; i++) {
                for (int j = 0; j < lattice_size_y; j++) {
                    lattice.pts[0][i][j].diff = new double[featuremap_size_x][featuremap_size_y];
                    lattice.pts[0][i][j].winners = new int[featuremap_size_x][featuremap_size_y];
                }
            }
            log.info("Lattice Created from file");
        } else {
            lattice = new Lattice3d(null, null, null);
            log.info("New Lattice Created");
        }

        lattice.orderAndConvergeSOM();
    }

    public void orderAndConvergeSOM() throws IOException, InterruptedException {
        boolean completed = false;
        PointsMask pts_mask_temp[][] = new PointsMask[lattice_size_x][lattice_size_y];

        this.random_mask = new double[input_size_x][input_size_y];
        Random randomForMask = new Random(9);
        for (int i = 0; i < input_size_x; i++) {
            for (int j = 0; j < input_size_y; j++) {
                this.random_mask[i][j] = randomForMask.nextDouble() / 2;
            }
        }

        while (true) {

            int succeeded_count = 0;

            if (m % 2 == 1) {
                succeeded_count = verifyLattice(pts_mask_temp);
                if (succeeded_count == -1) {
                    return;
                } else if (succeeded_count != (lattice_size_x * lattice_size_y)) {
                    completed = false;
                }
            }

            if (m % 2 == 0) {
                completed = buildLattice(pts_mask_temp, completed, succeeded_count);
                if (!completed) {
                    break;
                }
            }
            if (startStopButton != null) {
                this.startStopButton.setEnabled(true);
            }
        }

    }

    private boolean buildLattice(PointsMask pts_mask_temp[][], boolean completedInput, int succeeded_count) throws IOException, InterruptedException {
        File f = new File(System.getProperty("user.dir") + File.separator + "report");
        if (m != 0) {
            if (f.exists() || !(completedInput)) {
                if (f.exists()) {
                    f.delete();
                }
            } else {
                createFinalLattice(pts_mask_temp);
                return false;
            }
        }

        if (startStopButton != null) {
            this.startStopButton.setEnabled(false);
        }

        while (!completedInput) {
            completedInput = true;
            if (getjFrame() != null) {
                this.getjFrame().repaint(0);
            }

            Random rand1 = new Random();

            BlockingQueue<PointCompleter> queue = new ArrayBlockingQueue<>(no_of_threads);

            for (int indexa_seq = 0; indexa_seq < lattice_size_x; indexa_seq++) {
                for (int indexb_seq = 0; indexb_seq < lattice_size_y; indexb_seq++) {
                    int indexa = rand1.nextInt(lattice_size_x);
                    int indexb = rand1.nextInt(lattice_size_y);
                    //long t1 = System.currentTimeMillis();

                    if (this.pts_mask[indexa][indexb].done) {
                        //LOGGER.info("In Continue");
                        continue;
                    }

                    PointCompleter pointCompleter = new PointCompleter(queue, indexa, indexb, pts_mask, input, random_mask, pts);
                    queue.put(pointCompleter);
                    pointCompleter.start();

                    if (getjFrame() != null) {
                        this.getjFrame().setTitle(Integer.toString(indexa) + " " + Integer.toString(indexb) + ", m = " + Integer.toString(m) + " ----------- " + Integer.toString((succeeded_count * 100) / (lattice_size_x * lattice_size_y)) + "% completed");
                    }
                    if (latticeVisualizer != null) {
                        this.latticeVisualizer.getGraphics().drawImage(getImage(), lattice_size_x + 10, 0, this.latticeVisualizer);
                        this.latticeVisualizer.getGraphics().drawRect(lattice_size_x + 10 + indexa - 1, indexb - 1, featuremap_size_x + 1, featuremap_size_y + 1);
                    }
                }
                // createTempLattice();
            }

            List<PointCompleter> list = new ArrayList<>(queue);
            for (PointCompleter pointCompleter : list) {
                pointCompleter.join();
            }
            createTempLattice();

            aa:
            for (int indexa_seq = 0; indexa_seq < lattice_size_x; indexa_seq++) {
                for (int indexb_seq = 0; indexb_seq < lattice_size_y; indexb_seq++) {
                    if (!this.pts_mask[indexa_seq][indexb_seq].done) {
                        completedInput = false;
                        break aa;
                    }
                }
            }

        }
        this.m++;
        return completedInput;
    }

    private int verifyLattice(PointsMask pts_mask_temp[][]) throws IOException {

        int succeeded_count = 0;
        for (int i = 0; i < lattice_size_x; i++) {
            for (int j = 0; j < lattice_size_y; j++) {
                pts_mask_temp[i][j] = new PointsMask();
            }
        }

        for (int index1 = 0; index1 < lattice_size_x; index1++) {
            for (int index2 = 0; index2 < lattice_size_y; index2++) {

                if (getjFrame() != null) {
                    this.getjFrame().setTitle(Integer.toString(index1) + " " + Integer.toString(index2) + ", m = " + Integer.toString(m));
                }
                if (latticeVisualizer != null) {
                    this.latticeVisualizer.getGraphics().drawImage(getImage(), lattice_size_x + 10, 0, this.latticeVisualizer);
                    this.latticeVisualizer.getGraphics().drawRect(lattice_size_x + 10 + index1 - 1, index2 - 1, featuremap_size_x + 1, featuremap_size_y + 1);
                }
                int ltSums[][] = new int[lattice_size_x][lattice_size_y];
                double ltEds[][] = new double[lattice_size_x][lattice_size_y];
                int max_sum = 0;
                double min_ed = 0.0;

                for (int k = 0; k < featuremap_size_x; k++) {
                    for (int l = 0; l < featuremap_size_y; l++) {

                        int temp_index_x = 0;
                        int temp_index_y = 0;
                        double temp_diff = 0;

                        for (int i = 0; i < lattice_size_x; i++) {
                            for (int j = 0; j < lattice_size_y; j++) {

                                double cumulative_wt = 0;
                                for (int z_index = 0; z_index < no_of_lattices; z_index++) {
                                    for (int z = 0; z < dimension3_size; z++) {
                                        cumulative_wt += this.pts[z_index][i][j].wts[z][k][l];
                                    }
                                }

                                this.pts[0][i][j].diff[k][l] = Math.abs(cumulative_wt - this.input[index1][index2][k][l] - this.random_mask[index1 + k][index2 + l]);

                                //LOGGER.info(this.pts[0][i][j].diff[k][l] + " ");
                                ltEds[i][j] += this.pts[0][i][j].diff[k][l];

                                if (i == 0 && j == 0) {
                                    temp_index_x = i;
                                    temp_index_y = j;
                                    this.pts[0][temp_index_x][temp_index_y].winners[k][l] = 1;
                                    ltSums[temp_index_x][temp_index_y]++;
                                    temp_diff = this.pts[0][i][j].diff[k][l];
                                } else if (this.pts[0][i][j].diff[k][l] < temp_diff) {
                                    this.pts[0][temp_index_x][temp_index_y].winners[k][l] = 0;
                                    ltSums[temp_index_x][temp_index_y]--;
                                    temp_index_x = i;
                                    temp_index_y = j;
                                    this.pts[0][temp_index_x][temp_index_y].winners[k][l] = 1;
                                    ltSums[temp_index_x][temp_index_y]++;
                                    temp_diff = this.pts[0][i][j].diff[k][l];
                                } else {
                                    this.pts[0][i][j].winners[k][l] = 0;
                                }

                            }
                        }
                    }
                }

                for (int i = 0; i < lattice_size_x; i++) {
                    for (int j = 0; j < lattice_size_y; j++) {

                        if (i == 0 && j == 0) {
                            this.trigerred_point_index_x = i;
                            this.trigerred_point_index_y = j;
                            max_sum = ltSums[i][j];
                            min_ed = ltEds[i][j];
                            //LOGGER.info("one ");
                        } else if (ltSums[i][j] > max_sum) {
                            this.trigerred_point_index_x = i;
                            this.trigerred_point_index_y = j;
                            max_sum = ltSums[i][j];
                            min_ed = ltEds[i][j];
                            //LOGGER.info("two ");
                        } else if (ltSums[i][j] == max_sum && ltEds[i][j] < min_ed) {
                            this.trigerred_point_index_x = i;
                            this.trigerred_point_index_y = j;
                            min_ed = ltEds[i][j];
                            //LOGGER.info("three ");
                        } else {
                            //LOGGER.info("four ");
                        }
                    }
                }

                log.log(Level.INFO, "{0} {1}", new Object[]{max_sum, min_ed});

                if ((this.trigerred_point_index_x != index1)
                        || (this.trigerred_point_index_y != index2)
                        || !checkMaxSum(max_sum)) {

                    log.log(Level.INFO, "{0}", this.trigerred_point_index_x);
                    //noinspection SuspiciousNameCombination
                    log.log(Level.INFO, "{0}", this.trigerred_point_index_y);
                    File f = new File(System.getProperty("user.dir") + File.separator + "report");
                    if (!f.exists()) {
                        f.createNewFile();
                    }
                    try (FileOutputStream fos = new FileOutputStream(f.getAbsolutePath(), true); PrintWriter pw = new PrintWriter(fos)) {

                        pw.println("OH GOD " + 0 + " : " + n + " " + m + " " + this.trigerred_point_index_x + " " + this.trigerred_point_index_y + " " + index1 + " " + index2);
                    }
                    this.pts_mask[index1][index2].done = false;
                } else {
                    log.info("*");
                    succeeded_count++;
                }

                if (this.pts_mask[index1][index2].done && this.latticeVisualizer != null) {
                    this.latticeVisualizer.getGraphics().drawLine(index1, index2, index1, index2);
                }

                //LOGGER.info(this.trigerred_point_index_x + " " + this.trigerred_point_index_y + " ");
                //LOGGER.info(index1 + " " + index2);

                if (this.pts_mask[index1][index2].done) {

                    if (latticeVisualizer != null) {
                        this.latticeVisualizer.getGraphics().clearRect(lattice_size_x + 10 + index1 - 1, index2 - 1, featuremap_size_x + 2, featuremap_size_y + 2);
                        this.latticeVisualizer.getGraphics().drawRect(lattice_size_x + 10 + index1 - 1, index2 - 1, featuremap_size_x + 1, featuremap_size_y + 1);
                    }
                    for (int k = 0; k < featuremap_size_x; k++) {
                        for (int l = 0; l < featuremap_size_y; l++) {

                            if (this.pts[0][this.trigerred_point_index_x][this.trigerred_point_index_y].winners[k][l] == 1) {
                                if (latticeVisualizer != null) {
                                    this.latticeVisualizer.getGraphics().drawLine(lattice_size_x + 10 + index1 + k, index2 + l, lattice_size_x + 10 + index1 + k, index2 + l);
                                }
                                pts_mask_temp[index1][index2].mask[k][l] = true;
                            }
                        }
                    }

                    //Thread.sleep(1000);

                }

                if (latticeVisualizer != null) {
                    this.latticeVisualizer.getGraphics().clearRect(lattice_size_x + 10 + index1 - 1, index2 - 1, featuremap_size_x + 2, featuremap_size_y + 2);
                }
                n++;
                if ((n) % (lattice_size_x * lattice_size_y) == 0 && n > 0) {
                    if (latticeVisualizer != null) {
                        this.latticeVisualizer.getGraphics().clearRect(0, 0, lattice_size_x, lattice_size_y);
                    }
                    this.m++;
                    n = 0;
                }

                //int totalSum = 0;
                //double totalDiff = 0;
                //for (int i = 0; i < lattice_size_x; i++) {
                //for (int j = 0; j < lattice_size_y; j++) {
                //totalSum += ltSums[i][j];
                //totalDiff += ltEds[i][j];
                //LOGGER.info(ltSums[i][j] + " ");
                //}
                //}
                //LOGGER.info("totalSum " + totalSum);
                //LOGGER.info("totalDiff " + totalDiff);

            }
        }

        return succeeded_count;

    }

    @Override
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        log.info(".");
    }

    private void createTempLattice() throws IOException {
        File f_lattice = new File(System.getProperty("user.dir") + File.separator + "lattice");
        if (!f_lattice.exists()) {
            f_lattice.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(f_lattice.getAbsolutePath(), false);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        }
        log.info("Saved");
        log.info("Created Status");
        File f1 = new File(System.getProperty("user.dir") + File.separator + "status");
        if (!f1.exists()) {
            f1.createNewFile();
        }
    }

    private void createFinalLattice(PointsMask pts_mask_temp[][]) throws IOException {

        for (int i = 0; i < lattice_size_x; i++) {
            for (int j = 0; j < lattice_size_y; j++) {

                int winner_count = 0;

                bb:
                for (int k = 0; k < featuremap_size_x; k++) {
                    for (int l = 0; l < featuremap_size_y; l++) {

                        if (pts_mask_temp[i][j].mask[k][l]) {

                            winner_count++;
                            this.pts_mask[i][j].mask[k][l] = true;

                            if (checkMaxSum(winner_count)) {
                                break bb;
                            }

                        }

                    }
                }

                log.info("winner count  at pt : " + i + " " + j + ", is : " + winner_count);

            }
        }

        Lattice2d finalLattice = new Lattice2d(false);

        for (int k = 0; k < featuremap_size_x; k++) {
            for (int l = 0; l < featuremap_size_y; l++) {

                for (int i = 0; i < lattice_size_x; i++) {
                    for (int j = 0; j < lattice_size_y; j++) {

                        double cumulative_wt = 0;
                        for (int z = 0; z < dimension3_size; z++) {
                            for (int z_index = 0; z_index < no_of_lattices; z_index++) {
                                cumulative_wt += this.pts[z_index][i][j].wts[z][k][l];
                            }
                        }
                        //LOGGER.info("wts : " + cumulative_wt);
                        finalLattice.pts[0][i][j].wts[k][l] = cumulative_wt;

                    }
                }

            }
        }

        File final_lattice = new File(System.getProperty("user.dir") + File.separator + "lattice.final");
        if (!final_lattice.exists()) {
            final_lattice.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(final_lattice.getAbsolutePath(), false); ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(this.input);
            oos.writeObject(this.pts_mask);
            oos.writeObject(finalLattice.pts[0]);
        }
        log.info("Saved Final Lattice");
    }

    boolean checkMaxSum(int max_sum) {
        //LOGGER.info("sum : " + max_sum);
        return max_sum >= z0;
    }

    /**
     * @return the jFrame
     */
    public JFrame getjFrame() {
        return jFrame;
    }

    /**
     * @param jFrame the jFrame to set
     */
    public void setjFrame(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    /**
     * @return the image
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
    }
}
