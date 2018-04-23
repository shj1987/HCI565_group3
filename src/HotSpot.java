/**
 * Created by haosun on 4/21/18.
 */
public class HotSpot extends Coordinate {
    private final int ID;
    private final double score;

    public HotSpot(int ID, double point_x, double point_y, double score) {
        super(point_x, point_y);
        this.ID = ID;
        this.score = score;
    }

    public int getID() {
        return ID;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "ID : " + ID + " score : " + score + " coordinates " + point_x + ":" + point_y + "\n";
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HotSpot)) {
            return false;
        }
        HotSpot hotSpot = (HotSpot) obj;
        return ID == hotSpot.ID;
    }
}