import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see #
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        double ullon = params.get("ullon");
        double desiredLonDpp = Math.abs(ullon - params.get("lrlon")) / params.get("w");
        int depth = getDepth(desiredLonDpp);
        results.put("depth", depth);
        QuadTree rasterMap = new QuadTree();
        double ullat = params.get("ullat");
        rasterMap.raster(depth, ullon, ullat, params.get("lrlon"), params.get("lrlat"));
        String[][] toDisplay = rasterMap.displayString();
        results.put("render_grid", toDisplay);
        results.put("raster_ul_lon", rasterMap.getullon());
        results.put("raster_ul_lat", rasterMap.getullat());
        results.put("raster_lr_lon", rasterMap.getlrlon());
        results.put("raster_lr_lat", rasterMap.getlrlat());
        results.put("query_success", true);
        return results;
    }

    public int getDepth(double desiredLonDpp) {
        double lonDpp = Math.abs(122.2119140625 - 122.2998046875) / (256 * 2);
        double lonDppCounter = lonDpp;
        int counter = 0;
        while (lonDppCounter > desiredLonDpp) {
            counter++;
            lonDppCounter = lonDpp / (Math.pow(2, counter - 1));
            if (counter == 7) {
                desiredLonDpp = lonDppCounter + 1;
            }
        }
        return counter;
    }

}
