package som.view;

//~--- non-JDK imports --------------------------------------------------------

import som.Constants;

import som.lattice.Lattice3d;

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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;

import java.io.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class Mapper implements Constants, Runnable {
    private final static Logger LOGGER            = Logger.getLogger(Mapper.class.getName());
    private int                 width             = 400;
    private int                 height            = 200;
    public JFrame               frame             = new JFrame();
    private GridLayout          mainLayout        = new GridLayout(1, 2);
    private GridLayout          gridLayout1       = new GridLayout(1, 1);
    private GridLayout          gridLayout2       = new GridLayout(1, 1);
    public JButton              startStopButton   = new JButton();
    public LatticeVisualizer    latticeVisualizer = new LatticeVisualizer();
    private JPanel              jPanel2           = new JPanel();
    public Lattice3d            lt                = null;
    public boolean              startStopFlag;

    public Mapper() throws Exception {
        jbInit();
    }

    public static void main(String[] args) throws Exception {
        LOGGER.log(Level.INFO, "{0}", Runtime.getRuntime().totalMemory());
        LOGGER.info(System.getProperty("user.dir"));
        new Mapper();
    }

    private void jbInit() throws Exception {
        frame.getContentPane().setLayout(mainLayout);
        frame.getContentPane().setBackground(Color.white);
        frame.setTitle("Mapper");
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                this_windowClosing(e);
            }
            @Override
            public void windowActivated(WindowEvent e) {
                this_windowActivated(e);
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
                this_windowDeactivated(e);
            }
            @Override
            public void windowOpened(WindowEvent e) {
                this_windowOpened(e);
            }
        });
        latticeVisualizer.setLayout(gridLayout1);
        jPanel2.setLayout(gridLayout2);
        startStopButton.setBackground(Color.lightGray);
        startStopButton.setText("START");
        startStopButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    jButton1_actionPerformed(e);
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(Mapper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        latticeVisualizer.setBackground(Color.white);
        frame.getContentPane().add(latticeVisualizer);
        frame.getContentPane().add(jPanel2);
        jPanel2.add(startStopButton);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (this.height > screenSize.height) {
            this.height = screenSize.height;
        }

        if (this.width > screenSize.width) {
            this.width = screenSize.width;
        }

        frame.setSize(width, height);
        frame.setLocation((screenSize.width - this.width) / 2, (screenSize.height - this.height) / 2);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    void this_windowClosing(WindowEvent e) {
        System.exit(0);
    }

    void this_windowActivated(WindowEvent e) {
        frame.repaint(0);

        // LOGGER.info("Activated");
    }

    void this_windowDeactivated(WindowEvent e) {
        frame.repaint(0);

        // LOGGER.info("Deactivated");
    }

    void this_windowOpened(WindowEvent e) {
        frame.repaint(0);
    }

    void jButton1_actionPerformed(ActionEvent e) throws IOException, ClassNotFoundException {
        if (!this.startStopFlag) {
            File f = new File(System.getProperty("user.dir") + File.separator + "status");

            synchronized (this) {
                if (f.exists()) {
                    File file = new File(System.getProperty("user.dir") + File.separator + "lattice");

                    try (FileInputStream fis1 = new FileInputStream(file.getAbsolutePath());
                        ObjectInputStream ois1 = new ObjectInputStream(fis1)) {
                        this.lt = (Lattice3d) ois1.readObject();
                    }

                    File file1 = new File(System.getProperty("user.dir") + File.separator + "vignesh_index_right");

                    try (FileInputStream fis2 = new FileInputStream(file1);
                        DataInputStream dis2 = new DataInputStream(fis2)) {
                        this.lt.temp = new double[actual_input_size_x][actual_input_size_y];

                        for (int index1 = 0; index1 < actual_input_size_y; index1++) {
                            for (int index2 = 0; index2 < actual_input_size_x; index2++) {
                                this.lt.temp[index2][index1] = dis2.readInt();

                                int quo = ((int) this.lt.temp[index2][index1] + 1) / round;
                                int rem = ((int) this.lt.temp[index2][index1] + 1) % round;

                                if (rem > round / 2) {
                                    this.lt.temp[index2][index1] = quo * round + round - 1;
                                } else {
                                    this.lt.temp[index2][index1] = quo * round - 1;
                                }

                                if (this.lt.temp[index2][index1] == -1) {
                                    this.lt.temp[index2][index1] = 0;
                                }
                            }
                        }
                    }

                    this.lt.input = new double[lattice_size_x][lattice_size_y][featuremap_size_x][featuremap_size_y];

                    for (int i = translation_factor_x; i < lattice_size_x + translation_factor_x; i++) {
                        for (int j = translation_factor_y; j < lattice_size_y + translation_factor_y; j++) {
                            for (int k = 0; k < featuremap_size_x; k++) {
                                for (int l = 0; l < featuremap_size_x; l++) {
                                    this.lt.input[i - translation_factor_x][j - translation_factor_y][k][l] =
                                        this.lt.temp[i + k][j + l] / (255 * 2);
                                }
                            }
                        }
                    }

                    int temp_data[] = new int[input_size_x * input_size_y];

                    for (int i = translation_factor_x; i < input_size_x + translation_factor_x; i++) {
                        for (int j = translation_factor_y; j < input_size_y + translation_factor_y; j++) {
                            temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] =
                                (int) this.lt.temp[i][j];
                            temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)] =
                                (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)])
                                | (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)]
                                   << 8) | (temp_data[(j - translation_factor_y) * input_size_x + (i - translation_factor_x)]
                                            << 16 | 0xFF000000);
                        }
                    }

                    MemoryImageSource mis = new MemoryImageSource(input_size_x, input_size_y, temp_data, 0,
                                                input_size_x);

                    this.lt.setImage(frame.createImage(mis));
                    this.lt.setjFrame(frame);

                    for (int i = 0; i < lattice_size_x; i++) {
                        for (int j = 0; j < lattice_size_y; j++) {
                            this.lt.pts[0][i][j].diff    = new double[featuremap_size_x][featuremap_size_y];
                            this.lt.pts[0][i][j].winners = new int[featuremap_size_x][featuremap_size_y];
                        }
                    }

                    LOGGER.info("Lattice Created from file");
                } else {
                    this.lt = new Lattice3d(frame, latticeVisualizer, startStopButton);
                    LOGGER.info("New Lattice Created");
                }

                this.startStopFlag = true;
                this.startStopButton.setText("STOP");
                new Thread(this).start();
                frame.repaint(0);
            }
        } else {
            this.startStopFlag = false;
            this.startStopButton.setText("START");
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                this.latticeVisualizer.setLattice(this.lt);
                this.lt.orderandconvergeSOM();

                File f = new File(System.getProperty("user.dir") + File.separator + "lattice");

                if (!f.exists()) {
                    f.createNewFile();
                }

                try (FileOutputStream fos = new FileOutputStream(f.getAbsolutePath(), false);
                    ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(lt);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            this.startStopFlag = false;
            this.startStopButton.setText("START");
            LOGGER.info("Saved");
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
