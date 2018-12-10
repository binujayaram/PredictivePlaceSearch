package com.binujayaram.coverassessment.models;

import java.io.Serializable;

public class Carriers implements Serializable {

    String [] insurance_carriers;

    public String[] getInsurance_carriers() {
        return insurance_carriers;
    }

    public void setInsurance_carriers(String[] insurance_carriers) {
        this.insurance_carriers = insurance_carriers;
    }

}
