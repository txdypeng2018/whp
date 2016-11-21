package com.proper.enterprise.isj.webservices.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace = "http://tempuri.org/")
public interface RegSJService {

    @WebMethod(operationName = "CancelReg")
    @WebResult(name = "CancelRegResult", targetNamespace = "http://tempuri.org/")
    String cancelReg(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);


    @WebMethod(operationName = "GetDeptInfoByParentID")
    @WebResult(name = "GetDeptInfoByParentIDResult", targetNamespace = "http://tempuri.org/")
    String getDeptInfoByParentID(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetDoctorInfo")
    @WebResult(name = "GetDoctorInfoResult", targetNamespace = "http://tempuri.org/")
    String getDoctorInfo(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetHosInfo")
    @WebResult(name = "GetHosInfoResult", targetNamespace = "http://tempuri.org/")
    String getHosInfo(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetPayDetail")
    @WebResult(name = "GetPayDetailResult", targetNamespace = "http://tempuri.org/")
    String getPayDetail(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetPayList")
    @WebResult(name = "GetPayListResult", targetNamespace = "http://tempuri.org/")
    String getPayList(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetRegInfo")
    @WebResult(name = "GetRegInfoResult", targetNamespace = "http://tempuri.org/")
    String getRegInfo(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetTimeRegInfo")
    @WebResult(name = "GetTimeRegInfoResult", targetNamespace = "http://tempuri.org/")
    String getTimeRegInfo(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "HisInterface")
    @WebResult(name = "HisInterfaceResult", targetNamespace = "http://tempuri.org/")
    String hisInterface(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "NetTest")
    @WebResult(name = "NetTestResult", targetNamespace = "http://tempuri.org/")
    String netTest(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "OrderReg")
    @WebResult(name = "OrderRegResult", targetNamespace = "http://tempuri.org/")
    String orderReg(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);



    @WebMethod(operationName = "PayOrderAPP")
    @WebResult(name = "PayOrderAPPResult", targetNamespace = "http://tempuri.org/")
    String payOrderAPP(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "PayReg")
    @WebResult(name = "PayRegResult", targetNamespace = "http://tempuri.org/")
    String payReg(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "QueryRegRecord")
    @WebResult(name = "QueryRegRecordResult", targetNamespace = "http://tempuri.org/")
    String queryRegRecord(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "Refund")
    @WebResult(name = "RefundResult", targetNamespace = "http://tempuri.org/")
    String refund(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "StopReg")
    @WebResult(name = "StopRegResult", targetNamespace = "http://tempuri.org/")
    String stopReg(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetPayDetailAPP")
    @WebResult(name = "GetPayDetailAPPResult", targetNamespace = "http://tempuri.org/")
    String getPayDetailAPP(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "RefundByHisToAPP")
    @WebResult(name = "RefundByHisToAPPResult", targetNamespace = "http://tempuri.org/")
    String refundByHisToAPP(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "PayOrderReg")
    @WebResult(name = "PayOrderRegResult", targetNamespace = "http://tempuri.org/")
    String payOrderReg(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetCheckOutReportList")
    @WebResult(name = "GetCheckOutReportListResult", targetNamespace = "http://tempuri.org/")
    String getCheckOutReportList(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "GetNormalReportInfo")
    @WebResult(name = "GetNormalReportInfoResult", targetNamespace = "http://tempuri.org/")
    String getNormalReportInfo(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);

    @WebMethod(operationName = "CreatePat")
    @WebResult(name = "CreatePatResult", targetNamespace = "http://tempuri.org/")
    String createPat(@WebParam(name = "xml", targetNamespace = "http://tempuri.org/") String xml);
}
