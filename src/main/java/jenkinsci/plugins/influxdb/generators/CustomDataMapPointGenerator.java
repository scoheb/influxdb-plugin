package jenkinsci.plugins.influxdb.generators;

import hudson.model.Run;
import jenkinsci.plugins.influxdb.renderer.MeasurementRenderer;
import org.influxdb.dto.Point;

import java.util.*;

public class CustomDataMapPointGenerator extends AbstractPointGenerator {

    public static final String BUILD_TIME = "build_time";

    private final Run<?, ?> build;
    private final String customPrefix;
    Map<String, Map<String, Object>> customDataMap;
    Map<String, Map<String, String>> customDataMapTags;

    public CustomDataMapPointGenerator(MeasurementRenderer<Run<?,?>> projectNameRenderer, String customPrefix,
                                       Run<?, ?> build, long timestamp, Map<String, Map<String, Object>> customDataMap,
                                       Map<String, Map<String, String>> customDataMapTags) {
        super(projectNameRenderer, timestamp);
        this.build = build;
        this.customPrefix = customPrefix;
        this.customDataMap = customDataMap;
        this.customDataMapTags = customDataMapTags;
    }

    public boolean hasReport() {
        return (customDataMap != null && customDataMap.size() > 0);
    }

    public Point[] generate() {
        List<Point> customPoints = new ArrayList<Point>();
        Set<String> customKeys = customDataMap.keySet();

        for (String key : customKeys) {
            Point.Builder pointBuilder = buildPoint(measurementName(key), customPrefix, build)
                    .fields(customDataMap.get(key));

            if (customDataMapTags != null) {
                Map<String, String> customTags = customDataMapTags.get(key);
                if (customTags != null) {
                    if (customTags.size() > 0){
                        pointBuilder = pointBuilder.tag(customTags);
                    }
                }
            }

            Point point = pointBuilder.build();

            customPoints.add(point);
        }
        return customPoints.toArray(new Point[customPoints.size()]);
    }

}
