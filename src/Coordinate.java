/**
 * Created by haosun on 4/21/18.
 */
public class Coordinate {
    protected final double point_x;
    protected final double point_y;

    public Coordinate(double point_x, double point_y) {
        this.point_x = point_x;
        this.point_y = point_y;
    }

    public double getPoint_x() {
        return point_x;
    }

    public double getPoint_y() {
        return point_y;
    }

    @Override
    public String toString() {
        return point_x + "," + point_y;
    }
}
