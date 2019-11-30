package smartcity.ldgd.com.mylocation.type;

/**
 * Created by ldgd on 2019/11/30.
 * 功能：
 * 说明：用户信息
 */

public class User {

    private String carNumber;
    private int typeNub;
    private String phone;
    private String shipperCompany;
    private String receivingCompany;


    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getTypeNub() {
        return typeNub;
    }

    public void setTypeNub(int typeNub) {
        this.typeNub = typeNub;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShipperCompany() {
        return shipperCompany;
    }

    public void setShipperCompany(String shipperCompany) {
        this.shipperCompany = shipperCompany;
    }

    public String getReceivingCompany() {
        return receivingCompany;
    }

    public void setReceivingCompany(String receivingCompany) {
        this.receivingCompany = receivingCompany;
    }

    @Override
    public String toString() {
        return "User{" +
                "carNumber='" + carNumber + '\'' +
                ", typeNub=" + typeNub +
                ", phone='" + phone + '\'' +
                ", shipperCompany='" + shipperCompany + '\'' +
                ", receivingCompany='" + receivingCompany + '\'' +
                '}';
    }
}
