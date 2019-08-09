package som.view;

import lombok.extern.java.Log;
import som.Constants;
import som.lattice.Lattice3d;

import javax.swing.*;
import java.awt.*;

@Log
public class LatticeVisualizer extends JPanel implements Constants {
    Lattice3d lt = null;

    void setLattice(Lattice3d lt) {
        this.lt = lt;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (this.lt == null) {
            return;
        }

        for (int i = 0; i < lattice_size_x; i++) {
            for (int j = 0; j < lattice_size_y; j++) {
                if (lt.pts_mask[i][j].done) {
                    g.drawLine(i, j, i, j);
                }
            }
        }
    }
}