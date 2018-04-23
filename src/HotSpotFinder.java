import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by haosun on 4/21/18.
 */
public class HotSpotFinder {
    private static final double walkingDistanceIn10Min = 0.1;
    private static final double drivingDistanceIn60Min = 15;
    private static final int reachableBusStops = 10;

    private static double getDistanceFromLatLonInKm(double lat1, double lon1,
                                                    double lat2, double lon2) {
        double R = 6371d; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * R;
//        double earthRadius = 6371.0; // miles (or 6371.0 kilometers)
//        double lat1rad = Math.toRadians(lat1);
//        double lng1rad = Math.toRadians(lon1);
//        double lat2rad = Math.toRadians(lat2);
//        double lng2rad = Math.toRadians(lon2);
//
//        double HalfPi = 1.5707963;
//        double a = HalfPi - lat1rad;
//        double b = HalfPi - lat2rad;
//        double u = a * a + b * b;
//        double v = - 2 * a * b * Math.cos(lng1rad - lng2rad);
//        double c = Math.sqrt(Math.abs(u + v));
//        return earthRadius * c;

    }

    private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // miles (or 6371.0 kilometers)
        double lat1rad = Math.toRadians(lat1);
        double lng1rad = Math.toRadians(lng1);
        double lat2rad = Math.toRadians(lat2);
        double lng2rad = Math.toRadians(lng2);

        double HalfPi = 1.5707963;
        double a = HalfPi - lat1rad;
        double b = HalfPi - lat2rad;
        double u = a * a + b * b;
        double v = - 2 * a * b * Math.cos(lng1rad - lng2rad);
        double c = Math.sqrt(Math.abs(u + v));
        return earthRadius * c;

    }

    private static double deg2rad(double deg) {
        return deg * Math.PI / 180d;
    }

    private static List<? extends Coordinate> getCoordinatesWithinRange(List<? extends Coordinate> coordinates,
                                                                        double lat, double lon, double range) {
        List<Double> latList = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            latList.add(coordinate.getPoint_x());
        }
        int index = Collections.binarySearch(latList, lat);
        if (index < 0) index = ~index;

        int upperBound = index - 1;
        while (upperBound >= 1 && getDistanceFromLatLonInKm(coordinates.get(upperBound).getPoint_x(),
                lon, lat, lon) < range) {
            upperBound--;
        }

        int lowerBound = index;
        while (lowerBound < coordinates.size() - 1 && getDistanceFromLatLonInKm(coordinates.get(lowerBound).getPoint_x(),
                lon, lat, lon) < range) {
            lowerBound++;
        }

        List<Coordinate> result = new LinkedList<>();
        for (int i = Math.max(0, upperBound); i <= Math.min(coordinates.size() - 1, lowerBound); i++) {
            double busStopLat = coordinates.get(i).getPoint_x();
            double busStopLon = coordinates.get(i).getPoint_y();
            double distance = getDistanceFromLatLonInKm(busStopLat, busStopLon, lat, lon);
            if (distance < range) {
                result.add(coordinates.get(i));
            }
        }
        return result;
    }

    private static List<BusStop> getStartingBusStops(List<BusStop> busStopList, double lat, double lon) {
        List<Double> latList = new ArrayList<>();
        for (BusStop busStop : busStopList) {
            latList.add(busStop.getPoint_x());
        }

        int index = Collections.binarySearch(latList, lat);
        if (index < 0) index = ~index;

        int upperBound = index - 1;
        while (upperBound >= 1 && getDistanceFromLatLonInKm(busStopList.get(upperBound).getPoint_x(),
                lon, lat, lon) < walkingDistanceIn10Min) {
            upperBound--;
        }

        int lowerBound = index;
        while (lowerBound < busStopList.size() - 1 && getDistanceFromLatLonInKm(busStopList.get(lowerBound).getPoint_x(),
                lon, lat, lon) < walkingDistanceIn10Min) {
            lowerBound++;
        }

        List<BusStop> result = new LinkedList<>();
        for (int i = upperBound; i <= lowerBound; i++) {
            double busStopLat = busStopList.get(i).getPoint_x();
            double busStopLon = busStopList.get(i).getPoint_y();
            if (getDistanceFromLatLonInKm(busStopLat, busStopLon, lat, lon) < walkingDistanceIn10Min) {
                result.add(busStopList.get(i));
            }
        }
        return result;
    }

    private static Set<BusStop> getReachableBusStops(Map<String, BusLine> busLineMap, List<BusStop> startingBusStops) {
        Set<BusStop> result = new HashSet<>();
        for (BusStop start : startingBusStops) {
            for (String route : start.getRoutes()) {
                if (busLineMap.containsKey(route)) {
                    BusLine busLine = busLineMap.get(route);
                    List<BusStop> busStops = busLine.getStops();
                    int index = Collections.binarySearch(busStops, start);

                    for (int i = 0; i < reachableBusStops; i++) {
                        if (index - i >= 0)
                            result.add(busStops.get(index - i));
                        if (index + i < busStops.size())
                            result.add(busStops.get(index + i));
                    }
                }
            }
        }
        return result;
    }

    private static Set<HotSpot> getReachableHotSpots(List<HotSpot> hotSpots, Set<BusStop> busStops) {
        Set<HotSpot> result = new HashSet<>();
        List<HotSpot> list = null;
        for (BusStop busStop : busStops) {
            list = (List<HotSpot>) getCoordinatesWithinRange(hotSpots, busStop.getPoint_x(), busStop.getPoint_y(), walkingDistanceIn10Min);
            result.addAll(list);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new Date());
        FileParser fileParser = new FileParser();
        String outputFilePath = "../data/out_put_v3.csv";
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));

        Map<String, BusLine> busLineMap = fileParser.getBusLinesFromFile();

        List<BusStop> busStopList = fileParser.getBusStopsFromFile(busLineMap);

        List<HotSpot> hotSpotList = fileParser.getHotSpotsFromFile();

        List<Coordinate> parcelList = fileParser.getParcelFromFile();

        StringBuilder sb = new StringBuilder(1000);

        double max = 0.537;
//        double min = 1, max = 0;
//        int[] statistics = new int[110];
//        double sum = 0;

        int counter = 0;
        int size = parcelList.size();
        for (Coordinate parcel : parcelList) {
            if (counter++ % 200 == 0) {
                System.out.println(counter * 100 / size);
            }

            sb.setLength(0);
            List<BusStop> startingBusStops = (List<BusStop>) HotSpotFinder.getCoordinatesWithinRange(busStopList, parcel.getPoint_x(),
                    parcel.getPoint_y(), walkingDistanceIn10Min);

            Set<BusStop> reachableBusStops = HotSpotFinder.getReachableBusStops(busLineMap, startingBusStops);

            List<HotSpot> hotSpotsInDrivingDistance = (List<HotSpot>) HotSpotFinder.getCoordinatesWithinRange(hotSpotList, parcel.getPoint_x(),
                    parcel.getPoint_y(), drivingDistanceIn60Min);

            Set<HotSpot> reachableHotSpots = HotSpotFinder.getReachableHotSpots(hotSpotList, reachableBusStops);

            double reachableIndex = (double) (reachableHotSpots.size()) / (double) (hotSpotsInDrivingDistance.size()) / max * 100;
            //double reachableIndex = (double) (reachableHotSpots.size()) / (double) (hotSpotsInDrivingDistance.size());

            sb.append(parcel).append(',').append(reachableIndex).append('\n');
            bw.write(sb.toString());

//            sum += reachableIndex;
//            min = Math.min(min, reachableIndex);
//            max = Math.max(max, reachableIndex);
//            statistics[reachableHotSpots.size() / 50]++;
        }

        bw.close();

//        System.out.println(min);
//        System.out.println(max);
//        System.out.println(sum / parcelList.size());
//        System.out.println(new Date());
//        for (int i = 0; i < statistics.length; i++) {
//            System.out.println(50 * i + ":" + statistics[i]);
//        }


//        FileParser fileParser = new FileParser();
//        List<HotSpot> hotSpotList = fileParser.getHotSpotsFromFile();
//        int counter = 0;
//        for (HotSpot hotSpot : hotSpotList) {
//            double distance = HotSpotFinder.getDistanceFromLatLonInKm(hotSpot.point_x, hotSpot.point_y, -87.634378, 41.893561);
//            if (distance < drivingDistanceIn60Min) {
//                System.out.println(hotSpot + ":" + distance);
//                counter++;
//            }
//        }
//        System.out.println(counter);


//        List<HotSpot> hotSpotsInDrivingDistance = (List<HotSpot>) HotSpotFinder.getCoordinatesWithinRange(hotSpotList, -87.634394,
//                41.711137, drivingDistanceIn60Min);
//        System.out.println(hotSpotsInDrivingDistance.size());
    }
}
