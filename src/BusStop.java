import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by haosun on 4/20/18.
 */
public class BusStop extends Coordinate implements Comparable<BusStop> {
    private final int systemStop;
    private final String direction;
    private List<String> routes = new ArrayList<>();
    private final String publicName;

    public BusStop(int systemStop, String direction, String[] routes,
                   String publicName, double point_x, double point_y) {
        super(point_x, point_y);
        this.systemStop = systemStop;
        this.direction = direction;
        Collections.addAll(this.routes, routes);
        this.publicName = publicName;
    }

    public int getSystemStop() {
        return systemStop;
    }

    public String getDirection() {
        return direction;
    }

    public List<String> getRoutes() {
        return routes;
    }

    public String getPublicName() {
        return publicName;
    }

    @Override
    public int hashCode() {
        return systemStop;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BusStop))
            return false;
        BusStop busStop = (BusStop) obj;
        return systemStop == busStop.getSystemStop();
    }

    @Override
    public int compareTo(BusStop o) {
        return this.systemStop == o.systemStop ? 0 :
                this.systemStop < o.systemStop ? -1 : 1;
    }

    @Override
    public String toString() {
        return "system stop : " + systemStop + " coordinates " + point_x + ":" + point_y + "\n";
    }
}
