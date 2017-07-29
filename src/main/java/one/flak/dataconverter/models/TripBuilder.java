// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter.models;

import java.util.Date;

public class TripBuilder {
    private String id;
    private String uid;
    private String uuid;
    private String vin;
    private Date startedAt;

    public TripBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TripBuilder setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public TripBuilder setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public TripBuilder setVin(String vin) {
        this.vin = vin;
        return this;
    }

    public TripBuilder setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
        return this;
    }

    public Trip createTrip() {
        return new Trip(id, uid, uuid, vin, startedAt);
    }
}