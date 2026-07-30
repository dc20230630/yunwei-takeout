package com.yunwei.pojo.vo;

import lombok.Data;

/**
 * 后端预支付成功后返回给小程序的调起支付参数。
 */
@Data
public class OrderPaymentVO {

    private String timeStamp;

    private String nonceStr;

    private String packageStr;

    private String signType;

    private String paySign;
}
