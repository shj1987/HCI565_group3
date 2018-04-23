import java.util.ArrayList;
import java.util.List;

/**
 * Created by haosun on 4/20/18.
 */
public class BusLine {
    private final String route;
    private final String name;
    private List<BusStop> stops = new ArrayList<>();

    public BusLine(String route, String name) {
        this.route = route;
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public String getName() {
        return name;
    }

    public List<BusStop> getStops() {
        return stops;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("route : " + route + "\n");
        for (BusStop busStop : stops) {
            sb.append(busStop);
        }
        return sb.toString();
    }
}