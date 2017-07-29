// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter;

import one.flak.dataconverter.models.RawTripSample;
import com.csvreader.CsvWriter;

import java.io.IOException;

public class TripSampleWriter {

    private CsvWriter writer;

    public TripSampleWriter(String filePath) {
        this.writer = new CsvWriter(filePath);
    }

    public void write(RawTripSample tripSample) {
        String[] record = new String [] {
            Long.toString(tripSample.getTimestamp()),
            tripSample.getTripId(),
            tripSample.getTripUuid(),
            tripSample.getTripUid(),
            Integer.toString(tripSample.getSpeed()),
            tripSample.getVin(),
            Double.toString(tripSample.getAccelerationX()),
            Double.toString(tripSample.getAccelerationY()),
            Double.toString(tripSample.getAccelerationZ()),
            Double.toString(tripSample.getLatitude()),
            Double.toString(tripSample.getLongitude())
        };

        try {
            writer.writeRecord(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHeader() throws IOException {
        writer.writeRecord(csvHeader());
    }

    private String[] csvHeader() {
        String[] record = new String [] {
            "timestamp",
            "tripId",
            "tripUuid",
            "tripUid",
            "speed",
            "vin",
            "accelerationX",
            "accelerationY",
            "accelerationZ",
            "latitude",
            "longitude"
        };

        return record;
    }

    public void close() {
        writer.flush();
        writer.close();
    }
}
