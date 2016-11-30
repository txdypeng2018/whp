package com.proper.enterprise.isj.pay.cmb.service.impl;

import com.proper.enterprise.isj.pay.cmb.constants.CmbConstants;
import com.proper.enterprise.isj.pay.cmb.document.CmbProtocolDocument;
import com.proper.enterprise.isj.pay.cmb.entity.CmbBusinessEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbRefundEntity;
import com.proper.enterprise.isj.pay.cmb.model.*;
import com.proper.enterprise.isj.pay.cmb.repository.CmbPayNoticeRepository;
import com.proper.enterprise.isj.pay.cmb.repository.CmbProtocolNoticeRepository;
import com.proper.enterprise.isj.pay.cmb.repository.CmbProtocolRepository;
import com.proper.enterprise.isj.pay.cmb.repository.CmbRefundRepository;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.http.HttpClient;
import com.wideunique.utils.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 招商银行支付ServiceImpl
 */
@Service
public class CmbServiceImpl implements CmbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmbServiceImpl.class);

    private static String strXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    @Autowired
    Marshaller marshaller;

    @Autowired
    Map<String, Unmarshaller> unmarshallerMap;

    @Autowired
    CmbProtocolRepository cmbRepo;

    @Autowired
    CmbProtocolNoticeRepository cmbNoticeRepo;

    @Autowired
    CmbPayNoticeRepository cmbPayNoticeRepo;

    @Autowired
    CmbRefundRepository cmbRefundRepo;

    /**
     * 获取用户协议信息
     *
     * @param userId
     *        用户ID
     * @return 用户协议信息
     * @throws Exception
     */
    @Override
    public CmbProtocolDocument getUserProtocolInfo(String userId) throws Exception {
        return cmbRepo.findByUserId(userId);
    }

    /**
     * 保存用户协议信息
     *
     * @param userProtocolInfo
     *        用户协议信息
     * @return 用户协议信息
     * @throws Exception
     */
    @Override
    public void saveUserProtocolInfo(CmbProtocolDocument userProtocolInfo) throws Exception {
        cmbRepo.save(userProtocolInfo);
    }

    /**
     * 获取用户协议号
     *
     * @param userId
     *        用户ID
     * @param businessReq
     *        协议对象
     * @return 用户协议号
     * @throws Exception
     */
    @Override
    public boolean saveUserProtocolNo(String userId, BusinessProReq businessReq) throws Exception {
        boolean retValue = false;
        String pNo = "";
        CmbProtocolDocument protocolInfo = cmbRepo.findByUserId(userId);
        if(protocolInfo != null) {
            if(StringUtil.isNotNull(protocolInfo.getProtocolNo())
                    && protocolInfo.getSign().equals(ConfCenter.get("isj.pay.cmb.protocolResSuccess"))) {
                pNo = protocolInfo.getProtocolNo();
            } else {
                pNo = savePNo(userId, protocolInfo);
                retValue = true;
            }
        } else {
            pNo = savePNo(userId, null);
            retValue = true;
        }
        businessReq.setPno(pNo);
        return retValue;
    }

    /**
     * 获取预支付信息
     *
     * @param basicInfo 基本信息
     * @param uoReq 对象
     * @return 结果
     * @throws Exception
     */
    public PayResultRes getPrepayinfo(BasicInfoDocument basicInfo, UnifiedOrderReq uoReq) throws Exception {
        PayResultRes resObj = new PayResultRes();
        if(basicInfo != null) {
            // 客户协议号
            // TODO 暂时定义为: 17位的时间戳 + 3为随机数字
            BusinessProReq tmpReq = new BusinessProReq();
            boolean isProtocal = saveUserProtocolNo(basicInfo.getId(), tmpReq);
            // 交易时间
            String tradeDate = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
            // merchantCode
            String requestXML = "";
            // 自定义请求参数
            StringBuilder requestParams = new StringBuilder();
            requestParams.append("pno=");
            requestParams.append(tmpReq.getPno());
            requestParams.append("|");
            requestParams.append("userid=");
            requestParams.append(basicInfo.getId());
            // writer
            StringWriter writer = new StringWriter();
            // 签约 + 支付
            if(isProtocal) {
                // 生成协议信息
                BusinessProReq businessReq = new BusinessProReq();
                // 交易时间
                businessReq.setTs(tradeDate);
                //------以下字段“签约+支付”时必填----------
                // 用户协议号
                businessReq.setPno(tmpReq.getPno());
                // 协议开通请求流水号
                // TODO 暂时定义为: 17位的时间戳 + 3为随机数字
                businessReq.setSeq(getTimeNo());
                businessReq.setUrl(ConfCenter.get("isj.pay.cmb.protocolUrl"));
                businessReq.setPara(requestParams.toString());
                marshaller.marshal(businessReq, new StreamResult(writer));

                // 已签约并支付
            } else {
                // 生成协议信息
                BusinessNoProReq businessNoProReq = new BusinessNoProReq();
                // 交易时间
                businessNoProReq.setTs(tradeDate);
                // 协议号
                businessNoProReq.setPno(tmpReq.getPno());
                // 请求参数
                businessNoProReq.setPara(requestParams.toString());
                marshaller.marshal(businessNoProReq, new StreamResult(writer));
            }

            requestXML = writer.toString().replace(strXmlHeader, "");
            LOGGER.debug("potocolXML:" + requestXML);
            // 创建订单信息
            uoReq.setMerchantPara(requestParams.toString());
            createOrderInfo(uoReq, requestXML);

            // 取得订单信息
            String orderInfo = getOrderInfo(uoReq, UnifiedOrderReq.class);
            LOGGER.debug(orderInfo);

            // 支付信息字符串
            StringBuilder retSb = new StringBuilder();
            // 请求支付Url
            // TODO 目前填写的是测试环境的URL,切换正式环境时需要进行替换
            retSb.append(ConfCenter.get("isj.pay.cmb.payMentUrl"));
            retSb.append(orderInfo);

            resObj.setPayInfo(retSb.toString());
            resObj.setResultCode("0");
            resObj.setCmbBillNo(uoReq.getBillNo());
            resObj.setCmbDate(uoReq.getDate());

            LOGGER.debug(resObj.getPayInfo());
        }
        return resObj;
    }

    /**
     * 一网通查询单笔交易信息
     *
     * @param payInfo 支付信息对象
     * @return 查询结果
     * @throws Exception
     */
    @Override
    public PayResultRes querySingleResult(CmbPayEntity payInfo) throws Exception {
        PayResultRes resObj = new PayResultRes();
        QuerySingleOrderReq queryReq = new QuerySingleOrderReq();
        QuerySingleOrderHeadReq headReq = new QuerySingleOrderHeadReq();
        QuerySingleOrderBodyReq bodyReq = new QuerySingleOrderBodyReq();
        if (StringUtil.isNotNull(payInfo.getBillNo()) && StringUtil.isNotNull(payInfo.getDate())) {
            // 请求时间,精确到毫秒
            headReq.setTimeStamp(getCmbReqTime());
            // 订单号
            bodyReq.setBillNo(payInfo.getBillNo());
            // 交易日期
            bodyReq.setDate(payInfo.getDate());
            // head
            queryReq.setHead(headReq);
            // body
            queryReq.setBody(bodyReq);

            // hash
            StringWriter writer = new StringWriter();
            marshaller.marshal(queryReq, new StreamResult(writer));
            String preXML = getOriginSign(writer.toString());
            LOGGER.debug("preXML:" + preXML);
            String hash = encrypt(preXML, "SHA-1");
            queryReq.setHash(hash);

            // 生成请求参数
            writer = new StringWriter();
            marshaller.marshal(queryReq, new StreamResult(writer));
            String requestXML = writer.toString().replace(strXmlHeader, "");
            LOGGER.debug("requestXML:" + requestXML);

            ResponseEntity<byte[]> response = HttpClient.get(CmbConstants.CMB_PAY_DIRECT_REQUEST_X + "?Request=" + requestXML);
            QuerySingleOrderRes res = (QuerySingleOrderRes) unmarshallerMap.get("unmarshallQuerySingleOrderRes")
                    .unmarshal(new StreamSource(new ByteArrayInputStream(response.getBody())));

            if(StringUtil.isNull(res.getHead().getCode())) {
                String orderStatus = res.getBody().getStatus();
                // 订单状态
                // 0－已结帐，1－已撤销，2－部分结帐，4－未结帐，7-冻结交易-已经冻结金额已经全部结账 8-冻结交易，冻结金额只结帐了一部分
                // 订单状态
                resObj.setResultCode(orderStatus);
                // 订单金额
                resObj.setAmout(res.getBody().getAmount());
            } else {
                resObj.setResultCode(res.getHead().getCode());
                resObj.setResultMsg(res.getHead().getErrMsg());
            }
        }
        return resObj;
    }

    /**
     * 一网通退款
     *
     * @param refundInfo 支付信息对象
     * @return 退款结果
     * @throws Exception
     */
    @Override
    public CmbRefundEntity saveRefundResult(RefundNoDupBodyReq refundInfo) throws Exception {
        CmbRefundEntity refInfo = new CmbRefundEntity();
        RefundNoDupReq refundReq = new RefundNoDupReq();
        RefundNoDupHeadReq headReq = new RefundNoDupHeadReq();
        RefundNoDupBodyReq bodyReq = new RefundNoDupBodyReq();
        if (StringUtil.isNotNull(refundInfo.getBillNo()) && StringUtil.isNotNull(refundInfo.getDate())
                && StringUtil.isNotNull(refundInfo.getRefundNo()) && StringUtil.isNotNull(refundInfo.getAmount())) {
            // 请求时间,精确到毫秒
            headReq.setTimeStamp(getCmbReqTime());
            // 订单号
            bodyReq.setBillNo(refundInfo.getBillNo());
            // 交易日期
            bodyReq.setDate(refundInfo.getDate());
            // 退款流水号
            // 退款流水号长度小于等于20 ,组成是英文字符与数字。
            bodyReq.setRefundNo(refundInfo.getRefundNo());
            // 退款金额
            bodyReq.setAmount(refundInfo.getAmount());
            // 备注
            bodyReq.setDesc(refundInfo.getDesc());
            // head
            refundReq.setHead(headReq);
            // body
            refundReq.setBody(bodyReq);

            // hash
            StringWriter writer = new StringWriter();
            marshaller.marshal(refundReq, new StreamResult(writer));
            String preXML = getOriginSign(writer.toString());
            LOGGER.debug("preXML:" + preXML);
            String hash = encrypt(preXML, "SHA-1");
            refundReq.setHash(hash);

            // 生成请求参数
            writer = new StringWriter();
            marshaller.marshal(refundReq, new StreamResult(writer));
            String requestXML = writer.toString().replace(strXmlHeader, "");
            LOGGER.debug("requestXML:" + requestXML);

            ResponseEntity<byte[]> response = HttpClient.get(CmbConstants.CMB_PAY_DIRECT_REQUEST_X + "?Request=" + requestXML);
            RefundNoDupRes res = (RefundNoDupRes) unmarshallerMap.get("unmarshallRefundNoDupRes")
                    .unmarshal(new StreamSource(new ByteArrayInputStream(response.getBody())));

            // 退款成功
            if(StringUtil.isNull(res.getHead().getCode())) {
                // 退款状态(0:成功)
                refInfo.setRefundCode("0");
                // 响应信息
                BeanUtils.copyProperties(res.getBody(), refInfo);
                // 请求信息
                // 原订单日期YYYYMMDD
                refInfo.setReqDate(refundInfo.getDate());
                // 原订单号
                refInfo.setReqBillNo(refundInfo.getBillNo());
                // 退款流水号
                refInfo.setReqRefundNo(refundInfo.getRefundNo());
                // 退款金额
                refInfo.setReqAmount(refundInfo.getAmount());
                // 退款备注
                refInfo.setReqDesc(refundInfo.getDesc());
                // 保存退款信息
                cmbRefundRepo.save(refInfo);
            } else {
                // 错误code
                refInfo.setRefundCode(res.getHead().getCode());
                // 错误信息
                refInfo.setRefundCode(res.getHead().getErrMsg());
            }
        }
        return refInfo;
    }

    /**
     * 保存签约异步通知结果
     *
     * @param userId
     *        用户id
     * @param reqObject
     *        通知数据
     * @param businessInfo
     *        业务数据
     * @throws Exception
     */
    @Override
    public void saveBusinessInfo(String userId, JSONObject reqObject, BusinessRes businessInfo) throws Exception {
        CmbBusinessEntity cmbBusinessInfo = new CmbBusinessEntity();
        BeanUtils.copyProperties(businessInfo, cmbBusinessInfo);
        // 用户id
        cmbBusinessInfo.setUserId(userId);
        // 企业网银编号
        cmbBusinessInfo.setNtbnbr(reqObject.getString("NTBNBR"));
        // 功能交易码
        cmbBusinessInfo.setTrscod(reqObject.getString("TRSCOD"));
        // 字段BUSDAT长度
        cmbBusinessInfo.setDatlen(reqObject.getString("DATLEN"));
        // 通讯报文ID
        cmbBusinessInfo.setCommid(reqObject.getString("COMMID"));
        // 业务数据包
        cmbBusinessInfo.setBusdat(reqObject.getString("BUSDAT"));
        // 签名时间
        cmbBusinessInfo.setSigtim(reqObject.getString("SIGTIM"));
        // 签名数据的BASE64编码
        cmbBusinessInfo.setSigdat(reqObject.getString("SIGDAT"));
        // 保存签约异步通知结果
        cmbNoticeRepo.save(cmbBusinessInfo);
    }

    /**
     * 保存支付结果异步通知结果
     *
     * @param payInfo
     *        支付结果异步通知
     * @throws Exception
     */
    @Override
    public void saveCmbPayNoticeInfo(CmbPayEntity payInfo) throws Exception {
        cmbPayNoticeRepo.save(payInfo);
    }

    /**
     * 取得支付结果异步通知对象
     *
     * @param request
     *        请求
     * @return 支付结果异步通知对象
     * @throws Exception
     */
    @Override
    public CmbPayEntity getCmbPayNoticeInfo(HttpServletRequest request) throws Exception {
        CmbPayEntity payInfo = new CmbPayEntity();
        // 支付结果。取值Y(成功)：系统只通知成功交易。
        payInfo.setSucceed(request.getParameter("Succeed"));
        // 商户号，6位长数字，由银行在商户开户时确定
        payInfo.setCoNo(request.getParameter("CoNo"));
        // 定单号(由支付命令送来)
        payInfo.setBillNo(request.getParameter("BillNo"));
        // 订单金额(由支付命令送来)
        payInfo.setAmount(request.getParameter("Amount"));
        // 交易日期(由支付命令送来)
        payInfo.setDate(request.getParameter("Date"));
        // 商户自定义参数(支付接口中MerchantPara送来)
        payInfo.setMerchantPara(request.getParameter("MerchantPara"));
        // 银行通知商户的支付结果消息；
        payInfo.setMsg(request.getParameter("Msg"));
        // 当前订单是否有优惠，Y:有优惠。
        payInfo.setDiscountFlag(request.getParameter("DiscountFlag"));
        // 优惠金额，格式：xxxx.xx
        payInfo.setDiscountAmt(request.getParameter("DiscountAmt"));
        // 银行用自己的Private Key对通知命令的签名
        payInfo.setSignature(request.getParameter("Signature"));
        return payInfo;
    }

    /**
     * 根据银行通知商户的支付结果消息查询是否已经处理过支付结果异步通知
     *
     * @param msg
     *        支付结果
     * @throws Exception
     */
    @Override
    public CmbPayEntity getPayNoticeInfoByMsg(String msg) throws Exception {
        return cmbPayNoticeRepo.findByMsg(msg);
    }

    /**
     * 生成并保存用户协议号
     *
     * @param userId
     *        用户ID
     * @return 用户协议号
     */
    private String savePNo(String userId, CmbProtocolDocument protocolInfo) {
        String pNo = getTimeNo();
        if(protocolInfo != null) {
            protocolInfo.setProtocolNo(pNo);
            protocolInfo.setSign(ConfCenter.get("isj.pay.cmb.protocolResFail"));
            cmbRepo.save(protocolInfo);
        } else {
            CmbProtocolDocument newProtocolInfo = new CmbProtocolDocument();
            newProtocolInfo.setUserId(userId);
            newProtocolInfo.setProtocolNo(pNo);
            newProtocolInfo.setSign(ConfCenter.get("isj.pay.cmb.protocolResFail"));
            cmbRepo.save(newProtocolInfo);
        }
        return pNo;
    }

    /**
     * 创建订单信息
     */
    @Override
    public UnifiedOrderReq createOrderInfo(UnifiedOrderReq uoReq, String strXml) throws Exception {

        // 生成订单号
        // 6位或10位长数字
        // TODO 暂时使用十位随机数字
        uoReq.setBillNo(RandomStringUtils.randomNumeric(10));
        // 日期
        uoReq.setDate(DateUtil.toString(new Date(), "yyyyMMdd"));
        // 超时时间
        uoReq.setExpireTimeSpan(ConfCenter.get("isj.pay.cmb.expireTimeSpan"));
        // 异步通知地址
        uoReq.setMerchantUrl(URLEncoder.encode(ConfCenter.get("isj.pay.cmb.merchantUrl"), "UTF-8"));

        // 商户密钥 测试环境为空， 生产环境见《一网通支付商户服务指南.doc》
        String sKey = ConfCenter.get("isj.pay.cmb.cmbKey"); // TODO 换正式环境时需要进行替换
        // 生成请求校验码
        String strMerchantCode = cmb.MerchantCode.genMerchantCode(sKey, uoReq.getDate(), uoReq.getBranchID(),
                uoReq.getCono(), uoReq.getBillNo(), uoReq.getAmount(), uoReq.getMerchantPara(),
                ConfCenter.get("isj.pay.cmb.merchantUrl"), "", "", "", "", strXml);
        uoReq.setMerchantCode(strMerchantCode);

        return uoReq;
    }

    /**
     * 取得订单信息
     */
    @Override
    public <T> String getOrderInfo(T t, Class<T> clz) throws Exception {
        Field[] fields = clz.getDeclaredFields();
        Set<String> set = new HashSet<>();
        for (Field field : fields) {
            set.add(field.getName());
        }
        StringBuilder sb = new StringBuilder();
        Object value;
        for (String fieldName : set) {
            if (!StringUtil.startsWith(fieldName, "$")) {
                value = clz.getMethod("get" + StringUtil.capitalize(fieldName)).invoke(t);
                if (value != null) {
                    sb.append("&").append(StringUtil.upperCase(fieldName).substring(0, 1))
                            .append(fieldName.substring(1, fieldName.length())).append("=").append(value);
                }
            }
        }
        return sb.deleteCharAt(0).toString();
    }

    /**
     * 生成带有时间戳的20位纯数字编号
     *
     * @return 带有时间戳的20位纯数字编号
     */
    @Override
    public String getTimeNo() {
        Calendar calS = Calendar.getInstance();
        calS.setTime(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtil.toString(calS.getTime(), "yyyyMMddHHmmssSSS"));
        sb.append(RandomStringUtils.randomNumeric(3));
        return sb.toString();
    }

    /**
     * 获取SHA1加密后的字符串
     *
     * @param strSrc 需要加密的摘要
     * @param encName 算法
     * @return SHA1 加密的密文
     * @throws Exception
     */
    @Override
    public String encrypt(String strSrc, String encName) throws Exception {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes(PEPConstants.DEFAULT_CHARSET);
        try {
            if (encName == null || encName.equals("")) {
                encName = "MD5";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Invalid algorithm.");
            return null;
        }
        return strDes;
    }

    private String bytes2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = Integer.toHexString(bts[i] & 0xFF);
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

    /**
     * 获取待签名的字符串
     *
     * @param originSign 字符串
     * @return 待签名的字符串
     */
    @Override
    public String getOriginSign(String originSign) {
        StringBuilder sb = new StringBuilder();
        String sKey = ConfCenter.get("isj.pay.cmb.cmbKey"); // TODO 切换正式环境时需要进行替换
        String origin =  originSign.replace(strXmlHeader, "")
                .replace("<Request>", "").replace("</Request>", "")
                .replace("<Head>", "").replace("</Head>", "")
                .replace("<Body>", "").replace("</Body>", "")
                .replace("<Hash>", "").replace("</Hash>", "");
        return sb.append(sKey).append(origin).toString();
    }

    /**
     * 分析请求时传入的参数
     *
     * @param param 请求参数
     * @return 解析后的请求对象
     */
    @Override
    public JSONObject getParamObj(String param) throws Exception {
        JSONObject paramObj = new JSONObject();
        String[] reqParams = param.split("\\|");
        for(String reqParam : reqParams) {
            String[] deatilParam = reqParam.split("=");
            String value1 = StringUtil.isEmpty(deatilParam[0])? "":deatilParam[0];
            String value2 = StringUtil.isEmpty(deatilParam[1])? "":deatilParam[1];
            paramObj.put(value1, value2);
        }
        return paramObj;
    }
    /**
     * 读取流
     *
     * @param inStream 输入流
     * @return 字节数组
     * @throws Exception
     */
    @Override
    public byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    /**
     * 获取请求发起时间
     *
     * @return 生成的请求发起时间
     * @throws Exception
     */
    private String getCmbReqTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long dateToMs = Long.valueOf(sdf.parse("2000-01-01 00:00:00").getTime());
        Date dateTime = new Date();
        long currentMs = Long.valueOf(dateTime.getTime());
        return String.valueOf(currentMs - dateToMs);
    }
}