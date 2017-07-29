// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter.models;

public class RawTripSampleBuilder {
    private long timestamp;
    private String vin;
    private String tripId;
    private String tripUuid;
    private String tripUid;
    private int speed;
    private double latitude;
    private double longitude;
    private double accelerationX;
    private double accelerationY;
    private double accelerationZ;

    public RawTripSampleBuilder setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public RawTripSampleBuilder setVin(String vin) {
        this.vin = vin;
        return this;
    }

    public RawTripSampleBuilder setTripId(String tripId) {
        this.tripId = tripId;
        return this;
    }

    public RawTripSampleBuilder setTripUuid(String tripUuid) {
        this.tripUuid = tripUuid;
        return this;
    }

    public RawTripSampleBuilder setTripUid(String tripUid) {
        this.tripUid = tripUid;
        return this;
    }

    public RawTripSampleBuilder setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public RawTripSampleBuilder setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public RawTripSampleBuilder setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public RawTripSampleBuilder setAccelerationX(double accelerationX) {
        this.accelerationX = accelerationX;
        return this;
    }

    public RawTripSampleBuilder setAccelerationY(double accelerationY) {
        this.accelerationY = accelerationY;
        return this;
    }

    public RawTripSampleBuilder setAccelerationZ(double accelerationZ) {
        this.accelerationZ = accelerationZ;
        return this;
    }

    public RawTripSample createRawTripSample() {
        return new RawTripSample(timestamp, vin, tripId, tripUuid, tripUid, speed, latitude, longitude, accelerationX, accelerationY, accelerationZ);
    }
}