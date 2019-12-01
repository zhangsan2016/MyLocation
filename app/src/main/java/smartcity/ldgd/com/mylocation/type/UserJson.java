package smartcity.ldgd.com.mylocation.type;

/**
 * Created by ldgd on 2019/11/30.
 * 功能：
 * 说明：
 */

public class UserJson {


    // 车牌号
    private String plateInfo;
    // 经纬度
    private String lng;
    private String lat;
    // 运输类型
    private String dangerType;
    // 联系方式
    private String contact;
    // 货主公司
    private String company;
    // 收货公司
    private String customer;
    // 定位地址
    private String address;

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

    public String getDangerType() {
        return dangerType;
    }

    public void setDangerType(String dangerType) {
        this.dangerType = dangerType;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "UserJson{" +
                "plateInfo='" + plateInfo + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", dangerType='" + dangerType + '\'' +
                ", contact='" + contact + '\'' +
                ", company='" + company + '\'' +
                ", customer='" + customer + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
