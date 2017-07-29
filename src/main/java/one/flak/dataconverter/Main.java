// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter;

import one.flak.dataconverter.models.RawTripSample;
import one.flak.dataconverter.models.RawTripSampleBuilder;
import one.flak.dataconverter.models.Trip;
import one.flak.dataconverter.normalizers.GravitationVectorNormalizer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Main {

    public static void main(String[] args) throws IOException {

        Properties props = new Properties();

        try (BufferedReader reader = Files.newBufferedReader(new File(args[0]).toPath())) {
            props.load(reader);
        } catch (IOException e) {
            System.out.println("Could not find properties file.");
            System.exit(1);
        }

        String outputBasePath = props.getProperty("outputBasePath");

        File inputBasePath = new File(props.getProperty("inputBasePath"));
        int startDate = Integer.parseInt(props.getProperty("startDate"));
        int endDate = Integer.parseInt(props.getProperty("endDate"));

        TripDatabaseBuilder.TripDatabase tripDatabase = new TripDatabaseBuilder().getTripDatabase(props.getProperty("tripCSVPath"));

        long start = System.currentTimeMillis();

        List<File> directories = Arrays.asList(inputBasePath.listFiles())
                .stream()
                .filter((e) -> {

                    // this is for the MAC community
                    if (e.getName().equals(".DS_Store")) {
                        return false;
                    }

                    int fileDate = Integer.parseInt(e.getName());

                    boolean isInRange = fileDate >= startDate && fileDate <= endDate;
                    return e.isDirectory() && isInRange;
                })
                .collect(Collectors.toList());

        directories.parallelStream().forEach((dir) -> {
            System.out.println("Processing folder " + dir.getAbsolutePath());

            File[] files = dir.listFiles();

            List<List<File>> partitionedFiles = new ArrayList<>();

            int count = 0;
            List<File> currentFiles = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                count++;
                currentFiles.add(files[i]);
                if (count == 100) {
                    partitionedFiles.add(currentFiles);
                    currentFiles = new ArrayList<>();
                    count = 0;
                }
            }

            partitionedFiles.add(currentFiles);

            partitionedFiles.parallelStream().forEach((partitionFiles) -> {
                // ensure output folder for the current day exists
                String outputPath = outputBasePath + "/" + dir.toPath().getFileName().toString();

                File outputDailyDir = new File(outputPath);

                if (!outputDailyDir.exists()) {
                    outputDailyDir.mkdirs();
                }

                TripSampleWriter writer = new TripSampleWriter(Paths.get(outputPath, dir.toPath().getFileName().toString() + '-' + UUID.randomUUID().toString() + ".csv").toString());
                try {
                    writer.addHeader();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<RawTripSample> tripSamples = partitionFiles.parallelStream().map((tripSampleFile) -> {
                    System.out.println("Processing file " + tripSampleFile.getAbsolutePath());
                    long fileStart = System.currentTimeMillis();

                    List<RawTripSample> samples = readSamplesFromFile(tripSampleFile.toPath(), tripDatabase);

                    long fileEnd = System.currentTimeMillis();

                    System.out.println((fileEnd - fileStart));

                    return samples;
                }).flatMap(Collection::stream) // Flatten the arrays
                        .collect(Collectors.toList());

                writeTripSamples(tripSamples, writer);
                writer.close();
                tripSamples.clear();
            });
        });


        System.out.println("------");

        long end = System.currentTimeMillis();

        System.out.println((end - start));
    }


    private static List<RawTripSample> readSamplesFromFile(Path inputFilePath, TripDatabaseBuilder.TripDatabase tripDatabase) {
        EncodedFileReader fileReader = new EncodedFileReader();
        String rawJsonString = null;

        try {
            rawJsonString = fileReader.readFile(inputFilePath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        if (rawJsonString.isEmpty()) {
            // SKIP THIS FILE
            // use continue in loop
            return new ArrayList<>();
        }

        JsonElement json = new JsonParser().parse(rawJsonString);

        if (!json.isJsonArray()) {
            // handle this shit
            return new ArrayList<>();
        }

        JsonArray tripSamples = json.getAsJsonArray();

        if (tripSamples.size() == 0) {
            // Empty Trip
            return new ArrayList<>();
        }

        JsonObject firstTripSample = tripSamples.get(0).getAsJsonObject();

        String tripId = SafeJsonGetter.getString(firstTripSample.get("trip_id"));

        if (tripId.isEmpty()) {
            tripId = SafeJsonGetter.getString(firstTripSample.get("trip_uuid"));
        }

        Trip trip = tripDatabase.get(tripId);

        if (trip == null) {
            return new ArrayList<>();
        }

        Trip finalTrip = trip;

        // Only trip samples that are below 16G are valid, the sensor can not detect more, therefore if the acceleration
        // exceeds this threshold it is a measurement error.
        List<RawTripSample> validTripSamples = StreamSupport.stream(tripSamples.spliterator(), false).map((element) -> {
            RawTripSampleBuilder rawTripSampleBuilder = new RawTripSampleBuilder();

            JsonObject rawJsonTripSampleObject = element.getAsJsonObject();

            // trip from sqlite db
            // timestamp = (trip.started_at + elapsed_time)
            double elapsedTime = SafeJsonGetter.getDouble(rawJsonTripSampleObject.get("elapsed_time"));

            rawTripSampleBuilder.setTimestamp(finalTrip.getStartedAt().getTime() + (long) (elapsedTime * 1000));

            JsonObject accelerometer = SafeJsonGetter.getJsonObject(rawJsonTripSampleObject.get("accelerometer"));

            rawTripSampleBuilder.setAccelerationX(SafeJsonGetter.getDouble(accelerometer.get("accl_x")));
            rawTripSampleBuilder.setAccelerationY(SafeJsonGetter.getDouble(accelerometer.get("accl_y")));
            rawTripSampleBuilder.setAccelerationZ(SafeJsonGetter.getDouble(accelerometer.get("accl_z")));

            rawTripSampleBuilder.setSpeed(SafeJsonGetter.getInt(rawJsonTripSampleObject.get("speed")));
            rawTripSampleBuilder.setLatitude(SafeJsonGetter.getDouble(rawJsonTripSampleObject.get("latitude")));
            rawTripSampleBuilder.setLongitude(SafeJsonGetter.getDouble(rawJsonTripSampleObject.get("longitude")));
            rawTripSampleBuilder.setTripId(SafeJsonGetter.getString(rawJsonTripSampleObject.get("trip_id")));
            rawTripSampleBuilder.setTripUid(SafeJsonGetter.getString(rawJsonTripSampleObject.get("uid")));
            rawTripSampleBuilder.setTripUuid(SafeJsonGetter.getString(rawJsonTripSampleObject.get("trip_uuid")));

            rawTripSampleBuilder.setVin(finalTrip.getVin());
            return rawTripSampleBuilder.createRawTripSample();
        }).filter(RawTripSample::isValid).collect(Collectors.toList());

        // find trip sample with speed 0
        // in case we do not have such a sample we skip the trip

        Optional<RawTripSample> zeroSpeedSample = validTripSamples.stream().filter((validTripSample) -> {
            return validTripSample.getSpeed() == 0 && validTripSample.hasAcceleration();
        }).findFirst();

        if (!zeroSpeedSample.isPresent()) {
            // No zero speed trip sample found
            return new ArrayList<>();
        }

        GravitationVectorNormalizer gravityNormalizer = new GravitationVectorNormalizer(new Vector3D(zeroSpeedSample.get().getAccelerationX(),
                zeroSpeedSample.get().getAccelerationY(),
                zeroSpeedSample.get().getAccelerationZ()));


        List<RawTripSample> normalizedTripSamples = validTripSamples.stream().map((rawTripSample) -> {
            RawTripSampleBuilder builder = new RawTripSampleBuilder();

            // align acceleration vectors with the z axis
            Vector3D normalizedVector = gravityNormalizer.normalize(new Vector3D(rawTripSample.getAccelerationX(), rawTripSample.getAccelerationY(), rawTripSample.getAccelerationZ()));

            // set normalized vector.
            builder.setAccelerationX(normalizedVector.getX());
            builder.setAccelerationY(normalizedVector.getY());
            builder.setAccelerationZ(normalizedVector.getZ());

            // Copy old data
            builder.setTimestamp(rawTripSample.getTimestamp());
            builder.setVin(rawTripSample.getVin());
            builder.setSpeed(rawTripSample.getSpeed());
            builder.setLatitude(rawTripSample.getLatitude());
            builder.setLongitude(rawTripSample.getLongitude());
            builder.setTripId(rawTripSample.getTripId());
            builder.setTripUid(rawTripSample.getTripUid());
            builder.setTripUuid(rawTripSample.getTripUuid());

            return builder.createRawTripSample();
        }).collect(Collectors.toList());

        return normalizedTripSamples;
    }

    private static void writeTripSamples(List<RawTripSample> tripSamples, TripSampleWriter writer) {
        for (RawTripSample tripSample : tripSamples) {
            writer.write(tripSample);
        }
    }
}
