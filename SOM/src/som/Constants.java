package som;

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
public interface Constants {
    int actual_input_size_x  = 80;
    int actual_input_size_y  = 120;
    int input_size_x         = 80;    // 70;
    int input_size_y         = 80;    // 70;
    int translation_factor_x = 0;     // 10;
    int translation_factor_y = 20;    // 30;
    int featuremap_size_x    = 30;
    int featuremap_size_y    = featuremap_size_x;
    int lattice_size_x       = input_size_x - featuremap_size_x + 1;
    int lattice_size_y       = input_size_y - featuremap_size_y + 1;
    int no_of_lattices       = 1;
    int cun_size             = 9;
    int max_tries            = 35;
    int no_of_threads        = 1;
    int dimension3_size      = 1;
    int round                = 64;
    int z0                   = 825;
}


//~ Formatted by Jindent --- http://www.jindent.com
