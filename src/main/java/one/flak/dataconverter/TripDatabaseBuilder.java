// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter;

import one.flak.dataconverter.models.Trip;
import one.flak.dataconverter.models.TripBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.csvreader.CsvReader;

public class TripDatabaseBuilder {

    public TripDatabase getTripDatabase(String csvPath) throws IOException {
        CsvReader reader = new CsvReader(csvPath, ',');
        // Skip the header row.
        reader.readHeaders();

        // By setting initial capacity we prevent unnecessary resizes while building the Map.
        Map<String, Trip> tripIdMap = new HashMap<>(2000000);
        Map<String, Trip> tripUuidMap = new HashMap<>(2000000);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while(reader.readRecord()) {
            TripBuilder builder = new TripBuilder();
            builder.setId(reader.get(0));
            builder.setUid(reader.get(1));
            builder.setUuid(reader.get(2));
            builder.setVin(reader.get(4));

            String timestamp = reader.get(3);

            LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);

            builder.setStartedAt(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
            Trip trip = builder.createTrip();


            tripUuidMap.put(trip.getUuid(), trip);
            tripIdMap.put(trip.getId(), trip);
        }

        return new TripDatabase(tripIdMap, tripUuidMap);
    }

    public static class TripDatabase {
        private Map<String,Trip> tripIdMap;
        private Map<String,Trip> tripUuidMap;

        public TripDatabase(Map<String,Trip> tripIdMap, Map<String,Trip> tripUuidMap) {
            this.tripIdMap = tripIdMap;
            this.tripUuidMap = tripUuidMap;
        }

        public Trip get(String identifier) {
            if(this.tripIdMap.containsKey(identifier)) {
                return this.tripIdMap.get(identifier);
            }

            return this.tripUuidMap.get(identifier);
        }
    }
}
