// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter.models;

import java.util.Date;

public class Trip {

    private String id;
    private String uid;
    private String uuid;
    private String vin;
    private Date startedAt;

    public Trip(String id, String uid, String uuid, String vin, Date startedAt) {
        this.id = id;
        this.uid = uid;
        this.uuid = uuid;
        this.vin = vin;
        this.startedAt = startedAt;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getVin() {
        return vin;
    }

    public Date getStartedAt() {
        return startedAt;
    }
}
