package weizberg.earthquake;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import weizberg.earthquake.json.FeatureCollection;

public interface EarthquakeService {

    @GET("/earthquakes/feed/v1.0/summary/1.0_hour.geojson")
    Single<FeatureCollection> oneHour();

}
