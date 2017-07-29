// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter.models;

public class RawTripSample {

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

    public static final double THRESHOLD = 16.0 * 9.81;

    public RawTripSample(long timestamp, String vin, String tripId, String tripUuid, String tripUid, int speed, double latitude, double longitude, double accelerationX, double accelerationY, double accelerationZ) {
        this.timestamp = timestamp;
        this.vin = vin;
        this.tripId = tripId;
        this.tripUuid = tripUuid;
        this.tripUid = tripUid;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getVin() {
        return vin;
    }

    public String getTripId() {
        return tripId;
    }

    public String getTripUuid() {
        return tripUuid;
    }

    public String getTripUid() {
        return tripUid;
    }

    public int getSpeed() {
        return speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public double getAccelerationY() {
        return accelerationY;
    }

    public double getAccelerationZ() {
        return accelerationZ;
    }

    public boolean isValid() {
        if((accelerationX > THRESHOLD || accelerationY > THRESHOLD || accelerationZ > THRESHOLD)) {
            return false;
        }

        if(Double.compare(longitude, 0.0) == 0 || Double.compare(longitude, 0.0) == 0) {
            return false;
        }

        return true;
    }

    public boolean hasAcceleration() {
        return Double.compare(accelerationX, 0.0) != 0 || Double.compare(accelerationY, 0.0) != 0 || Double.compare(accelerationZ, 0.0) != 0;
    }

}
