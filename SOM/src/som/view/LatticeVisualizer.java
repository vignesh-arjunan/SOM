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
import java.awt.Graphics;

import java.util.logging.Logger;

import javax.swing.JPanel;

public class LatticeVisualizer extends JPanel implements Constants {
    private final static Logger LOGGER = Logger.getLogger(LatticeVisualizer.class.getName());
    Lattice3d                   lt     = null;

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


//~ Formatted by Jindent --- http://www.jindent.com
