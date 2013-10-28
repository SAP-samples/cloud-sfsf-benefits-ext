package com.sap.hana.cloud.samples.benefits.api.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.sap.hana.cloud.samples.benefits.persistence.model.Benefit;
import com.sap.hana.cloud.samples.benefits.persistence.model.OrderDetails;

public class BenefitsOrderItemBean {

    @Expose
    public BenefitDetailsBean benefitDetails;

    @Expose
    public List<BenefitItemBean> benefitItems = new ArrayList<>();

    public void initBenefitDetails(Benefit benefit) {
        this.benefitDetails = BenefitDetailsBean.get(benefit);
    }

    public void addBenefitItem(OrderDetails orderItem) {
        this.benefitItems.add(BenefitItemBean.get(orderItem));
    }

}
