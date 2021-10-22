package com.perfect.bizcorelite.Common;

import com.google.gson.annotations.SerializedName;

public class CustomerSearchDetails {
    @SerializedName("Name")
    private String name;
    @SerializedName("CustomerId")
    private String customerid;
    @SerializedName("AccountNumber")
    private String accno;
    @SerializedName("Address")
    private String address;
    @SerializedName("MobileNumber")
    private String mobile;
    @SerializedName("FK_Account")
    private String fkaccnt;
    @SerializedName("Module")
    private String module;
    public String getName() {

        return name;
    }
    public void setName(String name)
    {

        this.name = name;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {

        this.customerid = customerid;
    }

    public String getAccno() {
        return accno;
    }

    public void setAccno(String accno) {
        this.accno = accno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFkaccnt() {
        return fkaccnt;
    }

    public void setFkaccnt(String fkaccnt) {
        this.fkaccnt = fkaccnt;
    }


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

}
