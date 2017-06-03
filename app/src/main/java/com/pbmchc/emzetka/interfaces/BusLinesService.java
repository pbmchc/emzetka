package com.pbmchc.emzetka.interfaces;

import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.models.Stop;
import com.pbmchc.emzetka.models.Departure;
import com.pbmchc.emzetka.models.Legend;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Piotrek on 2016-12-16.
 */
public interface BusLinesService {

    @GET("api/lines")
    Call<List<Line>> getBusLines();

    @GET("api/lines")
    Call<List<Line>> getLinesByStop(@Query("stopId") String id,
                                    @Query("isDest") String isDest);

    @GET("api/stops")
    Call<List<Stop>> getStops();

    @GET("api/stops")
    Call<List<Stop>> getBusStopsByLine(@Query("line") String line);

    @GET("api/stops")
    Call<List<Stop>> getRestOfLine(@Query("stoplineId") String stopLineId,
                                   @Query("direction") String direction);

    @GET("api/stops")
    Call<List<Stop>> getNearbyStops(@Query("north") double north,
                                    @Query("south") double south,
                                    @Query("west") double west,
                                    @Query("east") double east);

    @GET("api/stops")
    Call<Stop> getOppositeStop(@Query("name") String name,
                               @Query("sourceId") String sourceId);

    @GET("api/departures")
    Call<List<Departure>> getClosestDepartures(@Query("stop") String stop,
                                               @Query("hoursFrom") String hoursFrom,
                                               @Query("hoursTo") String hoursTo,
                                               @Query("day") int day);

    @GET("api/departures")
    Call<List<Departure>> getDepartures(@Query("stop") String stop,
                                        @Query("day") int day);

    @GET("api/legend")
    Call<List<Legend>> getLegend(@Query("stop") String stop);

}
