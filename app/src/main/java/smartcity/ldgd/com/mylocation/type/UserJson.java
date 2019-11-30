package smartcity.ldgd.com.mylocation.type;

/**
 * Created by ldgd on 2019/11/30.
 * 功能：
 * 说明：
 */

public class UserJson {

    private String plateInfo;
    private String lng;
    private String lat;

    public UserJson(String plateInfo, String lng, String lat) {
        this.plateInfo = plateInfo;
        this.lng = lng;
        this.lat = lat;
    }

    public String getPlateInfo() {
        return plateInfo;
    }

    public void setPlateInfo(String plateInfo) {
        this.plateInfo = plateInfo;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
