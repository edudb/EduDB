/**
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2012, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 * 
 * 
 * based on work by Simon Levy
 * http://www.cs.wlu.edu/~levy/software/kd/
 */
package data_structures.kDTree;

// Hyper-Point class supporting kDTree class

import dataTypes.DataType;

/**
 * @author  mohamed
 */
class HPoint {

    /**
     * @uml.property  name="coord"
     * @uml.associationEnd  multiplicity="(0 -1)"
     */
    protected DataType[] coord;

    protected HPoint(int n) {
        coord = new DataType[n];
    }

    protected HPoint(DataType[] x) {

        coord = new DataType[x.length];
        for (int i = 0; i < x.length; ++i)
            coord[i] = x[i];
    }

    protected Object clone() {

        return new HPoint(coord);
    }

    protected boolean equals(HPoint p) {

        // seems faster than java.util.Arrays.equals(), which is not
        // currently supported by Matlab anyway
        for (int i = 0; i < coord.length; ++i)
            if (!coord[i].equals(p.coord[i]))
                return false;

        return true;
    }

    protected static double sqrdist(HPoint x, HPoint y) {

        double dist = 0;

        for (int i = 0; i < x.coord.length; ++i) {
            double diff = (x.coord[i].diff(y.coord[i]));
            dist += (diff * diff);
        }

        return dist;

    }

    protected static double eucdist(HPoint x, HPoint y) {

        return Math.sqrt(sqrdist(x, y));
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < coord.length; ++i) {
            s = s + coord[i] + " ";
        }
        return s;
    }

}
