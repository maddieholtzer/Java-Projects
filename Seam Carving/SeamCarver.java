/**
 * Created by Matt on 4/25/2017.
 */
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
    }

    //current pictures
    public Picture picture() {
        Picture x = new Picture(this.picture);
        return x;
    }
    // width of current picture
    public int width() {
        return this.picture.width();
    }
    //height of current picture
    public int height() {
        return this.picture.height();
    }
    //energy of pixel at column x and row y
    public double energy(int x, int y) {
        Color left;
        Color right;
        Color up;
        Color down;
        if (x == 0) {
            left = this.picture.get(width() - 1, y);
            if (width() == 1) {
                right = this.picture.get(x, y);
            } else {
                right = this.picture.get(x + 1, y);
            }
        } else if (x == width() - 1) {
            left = this.picture.get(x - 1, y);
            right = this.picture.get(0, y);
        } else {
            left = this.picture.get(x - 1, y);
            right = this.picture.get(x + 1, y);
        }
        if (y == 0) {
            if (height() == 1) {
                up = this.picture.get(x, y);
                down = this.picture.get(x, y);
            } else {
                up = this.picture.get(x, height() - 1);
                down = this.picture.get(x, y + 1);
            }
        } else if (y == height() - 1) {
            up = this.picture.get(x, y - 1);
            down = this.picture.get(x, 0);
        } else {
            up = this.picture.get(x, y - 1);
            down = this.picture.get(x, y + 1);
        }
        double rx = left.getRed() - right.getRed();
        double bx = left.getBlue() - right.getBlue();
        double gx = left.getGreen() - right.getGreen();
        double ry = up.getRed() - down.getRed();
        double by = up.getBlue() - down.getBlue();
        double gy = up.getGreen() - down.getGreen();
        double dx = rx * rx + bx * bx + gx * gx;
        double dy = ry * ry + by * by + gy * gy;
        return dx + dy;
    }
    //sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] shortestenergy = new double[width()][height()];
        double[][] actualenergy = new double[width()][height()];
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                actualenergy[i][j] = energy(i, j);
            }
        }
        for (int j = 0; j < width(); j++) {
            for (int i = 0; i < height(); i++) {
                if (j == 0) {
                    shortestenergy[j][i] = actualenergy[j][i];
                } else {
                    double leftup = -1;
                    double leftdown = -1;
                    if (i > 0) {
                        leftup = shortestenergy[j - 1][i - 1];
                    }
                    if (i < height() - 1) {
                        leftdown = shortestenergy[j - 1][i + 1];
                    }
                    double left = shortestenergy[j - 1][i];
                    if (leftup == -1 && leftdown == -1) {
                        shortestenergy[j][i] = actualenergy[j][i] + left;
                    } else if (leftup == -1) {
                        if (left < leftdown) {
                            shortestenergy[j][i] = actualenergy[j][i] + left;
                        } else {
                            shortestenergy[j][i] = actualenergy[j][i] + leftdown;
                        }
                    } else if (leftdown == -1) {
                        if (left < leftup) {
                            shortestenergy[j][i] = actualenergy[j][i] + left;
                        } else {
                            shortestenergy[j][i] = actualenergy[j][i] + leftup;
                        }
                    } else if (leftup <= left && leftup <= leftdown) {
                        shortestenergy[j][i] = actualenergy[j][i] + leftup;
                    } else if (left <= leftup && left <= leftdown) {
                        shortestenergy[j][i] = actualenergy[j][i] + left;
                    } else {
                        shortestenergy[j][i] = actualenergy[j][i] + leftdown;
                    }
                }
            }
        }
        int[] shortestpath = new int[width()];
        int currentCol = 0;
        double minRow = shortestenergy[width() - 1][0];
        for (int j = width() - 1; j > -1; j--) {
            for (int i = 0; i < height(); i++) {
                if (j == width() - 1) {
                    double temp = shortestenergy[j][i];
                    if (temp < minRow) {
                        minRow = temp;
                        currentCol = i;
                    }
                } else {
                    if (!(i < shortestpath[j + 1] - 1) && !(i > shortestpath[j + 1] + 1)) {
                        double temp = shortestenergy[j][i];
                        if (temp <= minRow) {
                            minRow = temp;
                            currentCol = i;
                        }
                    }
                }
            }
            shortestpath[j] = currentCol;
        }
        return shortestpath;
    }
    //sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] shortestenergy = new double[width()][height()];
        double[][] actualenergy = new double[width()][height()];
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                actualenergy[i][j] = energy(i, j);
            }
        }
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                if (j == 0) {
                    shortestenergy[i][j] = actualenergy[i][j];
                } else {
                    double upleft = -1;
                    double upright = -1;
                    if (i > 0) {
                        upleft = shortestenergy[i - 1][j - 1];
                    }
                    if (i < width() - 1) {
                        upright = shortestenergy[i + 1][j - 1];
                    }
                    double up = shortestenergy[i][j - 1];
                    if (upleft == -1 && upright == -1) {
                        shortestenergy[i][j] = actualenergy[i][j] + up;
                    } else if (upleft == -1) {
                        if (up < upright) {
                            shortestenergy[i][j] = actualenergy[i][j] + up;
                        } else {
                            shortestenergy[i][j] = actualenergy[i][j] + upright;
                        }
                    } else if (upright == -1) {
                        if (up < upleft) {
                            shortestenergy[i][j] = actualenergy[i][j] + up;
                        } else {
                            shortestenergy[i][j] = actualenergy[i][j] + upleft;
                        }
                    } else if (upleft <= up && upleft <= upright) {
                        shortestenergy[i][j] = actualenergy[i][j] + upleft;
                    } else if (up <= upleft && up <= upright) {
                        shortestenergy[i][j] = actualenergy[i][j] + up;
                    } else {
                        shortestenergy[i][j] = actualenergy[i][j] + upright;
                    }
                }
            }
        }
        int[] shortestpath = new int[height()];
        int currentCol = 0;
        double minRow = shortestenergy[0][height() - 1];
        for (int j = height() - 1; j > -1; j--) {
            for (int i = 0; i < width(); i++) {
                if (j == height() - 1) {
                    double temp = shortestenergy[i][j];
                    if (temp < minRow) {
                        minRow = temp;
                        currentCol = i;
                    }
                } else {
                    if (!(i < shortestpath[j + 1] - 1) && !(i > shortestpath[j + 1] + 1)) {
                        double temp = shortestenergy[i][j];
                        if (temp <= minRow) {
                            minRow = temp;
                            currentCol = i;
                        }
                    }
                }
            }
            shortestpath[j] = currentCol;
        }
        return shortestpath;
    }
    //remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        this.picture = SeamRemover.removeHorizontalSeam(picture(), seam);
    }
    //remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        this.picture = SeamRemover.removeVerticalSeam(picture(), seam);
    }
}
