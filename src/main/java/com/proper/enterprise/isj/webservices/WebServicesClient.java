package com.proper.enterprise.isj.webservices;

import com.proper.enterprise.isj.log.service.WSLogService;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.*;
import com.proper.enterprise.isj.webservices.model.res.*;
import com.proper.enterprise.isj.webservices.service.RegSJService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.enums.IntEnum;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.cipher.AES;
import com.proper.enterprise.platform.core.utils.digest.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

@Service
public class WebServicesClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServicesClient.class);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    @Qualifier("hisAES")
    private AES aes;

    @Autowired
    private RegSJService regSJService;

    @Autowired
    private Marshaller marshaller;

    @Autowired
    private Unmarshaller unmarshaller;

    @Autowired
    private WSLogService wsLogService;

    /**
     * 在调用其它接口之前用于测试目标服务网络是否通畅，服务是否处于工作状态、数据库是否处于连接状态.
     *
     * @param hosId 医院ID.
     * @param ip 请求IP.
     * @return 响应模型及网络测试结果对象.
     * @throws Exception 异常.
     */
    public ResModel<NetTestResult> netTest(String hosId, String ip) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        map.put("IP", ip);
        String res = invokeWS("netTest", map);
        return parseEnvelop(res, NetTestResult.class);
    }

    private String invokeWS(String methodName, Map<String, Object> param) throws Exception {
        String funCode = ConfCenter.get("isj.his.funCode." + methodName);
        Method method = RegSJService.class.getDeclaredMethod(methodName, String.class);
        String reqStr = envelopReq(funCode, param);
        LOGGER.trace("Method: {}; regSJService: {}, reqStr: {}", method, regSJService, reqStr);

        long start = System.currentTimeMillis();
        try {
            String result = (String) method.invoke(regSJService, reqStr);
            wsLogService.persistLog(funCode + ":" + methodName, param, reqStr, result,
                    System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            wsLogService.persistLog(funCode + ":" + methodName, param, reqStr, e, System.currentTimeMillis() - start);
            throw e;
        }
    }

    protected String envelopReq(String funCode, Map<String, Object> map) throws IOException {
        Assert.notNull(funCode, "FunCode should not null!");
        Writer writer = new StringWriter();
        ReqModel m = new ReqModel();
        m.setFunCode(funCode);
        m.setUserId(ConfCenter.get("isj.his.userId"));
        m.setReq(map);
        marshaller.marshal(m, new StreamResult(writer));

        String result = writer.toString();
        // 东软接口不识别转义后的形式，故需要将被 marshaller 自动转义的字符替换回去
        result = result.replace("&lt;", "<").replace("&gt;", ">");
        LOGGER.trace("Actual request after envelop is: {}", result);

        return result;
    }

    @SuppressWarnings("unchecked")
    <T> ResModel<T> parseEnvelop(String responseStr, Class<T> clz) throws Exception {
        ResModel<T> resModel = (ResModel<T>) unmarshaller.unmarshal(new StreamSource(new StringReader(responseStr)));
        if (signValid(resModel)) {
            if (resModel.getReturnCode() != ReturnCode.ERROR) {
                byte[] res = aes.decrypt(resModel.getResEncrypted().getBytes(PEPConstants.DEFAULT_CHARSET));
                if (res != null && res.length > 0) {
                    Unmarshaller u = wac.getBean("unmarshall" + clz.getSimpleName(), Unmarshaller.class);
                    T resObj = (T) u.unmarshal(new StreamSource(new ByteArrayInputStream(res)));
                    resModel.setRes(resObj);
                }
            }
            return resModel;
        } else {
            LOGGER.error("Sign is INVALID!! Could NOT parse response!!");
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean signValid(ResModel resModel) {
        Assert.notNull(resModel.getSign());

        String sign = MessageFormat.format(ConfCenter.get("isj.his.template.sign.res"), resModel.getResEncrypted(),
                resModel.getReturnCode().getCode(), resModel.getReturnMsg(), ConfCenter.get("isj.his.aes.key"));

        return resModel.getSign().equalsIgnoreCase(MD5.md5Hex(sign));
    }

    /**
     * 查询医院列表及单个医院的详细信息.
     * <p>
     * 需要获取医院基本信息时调用，平台可通过该接口获取医院的信息更新。
     * </p>
     *
     * @param hosId 医院ID.
     * @return 响应模型及医院信息.
     * @throws Exception 异常.
     */
    public ResModel<HosInfo> getHosInfo(String hosId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        String res = invokeWS("getHosInfo", map);
        return parseEnvelop(res, HosInfo.class);
    }


    /**
     * 平台可通过该接口获取医院科室信息.
     * <p>
     * 当父级 ID（Parent_ID）为 0 时查询所有院区;
     * 通过返回 ID 作为父级 ID 进行递归查询.
     * </p>
     *
     * @param hosId 医院ID.
     * @param parentId 科室ID.
     *            <p>
     *            为 0 时查询所有院区，通过院区 ID 查询所有一级科室信息，通过本科室 ID 查询所有子科室及科室医生
     *            </p>
     * @return 响应模型及科室信息
     * @throws Exception 异常.
     */
    public ResModel<DeptInfo> getDeptInfoByParentID(String hosId, String parentId, String name) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        map.put("PARENT_ID", parentId);
        map.put("NAME", name);
        String res = invokeWS("getDeptInfoByParentID", map);
        return parseEnvelop(res, DeptInfo.class);
    }

    /**
     * 平台通过调用HIS的该接口获取某医生具体的排班信息.
     * <p>
     * 医生ID（DOCTOR_ID）为-1时查询科室ID下所有医生排班。
     * </p>
     *
     * @param hosId 医院ID.
     * @param deptId 科室ID，HIS系统中科室唯一ID.
     * @param doctorId 医生ID，HIS系统中医生唯一ID，为-1时查询科室ID下所有医生排班.
     * @param startDate 排班开始日期，格式：YYYY-MM-DD.
     * @param endDate 排班结束日期，格式：YYYY-MM-DD.
     * @return 响应模型及排班信息对象.
     * @throws Exception 异常.
     */
    public ResModel<RegInfo> getRegInfo(String hosId, String deptId, String doctorId, Date startDate, Date endDate)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        map.put("DEPT_ID", deptId);
        map.put("DOCTOR_ID", doctorId);
        map.put("START_DATE", DateUtil.toDateString(startDate));
        map.put("END_DATE", DateUtil.toDateString(endDate));

        String result = invokeWS("getRegInfo", map);
        return parseEnvelop(result, RegInfo.class);
    }

    /**
     * 用户通过平台进行挂号，生成相应的订单，同时平台调用HIS的接口将订单信息同步到HIS.
     * <p>
     * 对于给本人挂号的，必须要填写患者身份证号码信息，取号的时候需要出示患者的身份证；
     * 对于给子女挂号的，患者身份证件号码允许为空（此种情况适用于患者是孩子还没有身份证件），取号的时候出示大人(挂号人)或者子女(被监护人)的身份证，
     * 都允许取号。 对于给本人及他人挂号的，必须要填写患者姓名、身份证号码、手机号码信息，取号的时候需要出示患者的身份证；
     * 对于给没有身份证小孩挂号的，患者身份证件号码允许为空，必须填写患者姓名、手机号码及监护人身份证号码，取号的时候出示本人(挂号人)或者子女(被监护人
     * )的身份证，都允许取号。 对于锁定号源类型的类型，挂号接口提交时，HIS需要将原来锁定的号源分配给挂号的患者。
     * </p>
     * <p>
     * 挂号接口增加传入医院内部用户ID号， 如果平台方传空，则表示医院在挂号成功后，必须返回此医院内部用户ID号（没有查到用户的需要注册用户后返回）；
     * 如果平台方不为空，则医院根据传入的用户ID去挂号。
     * </p>
     *
     * @param orderRegReq 预约挂号请求对象.
     * @return 响应对象及预约挂号信息.
     * @throws Exception 异常.
     */
    public ResModel<OrderReg> orderReg(OrderRegReq orderRegReq) throws Exception {
        String res = invokeWS("orderReg", toKVStyle(orderRegReq));
        return parseEnvelop(res, OrderReg.class);
    }

    /**
     * 把一个包含 getter 方法的 bean 对象转换为一个 Map.
     * <p>
     * 其中 key 为对象的属性名，value 为属性值。
     * <br/>
     * 当属性值：
     * <ol>
     * <li>为原始数据类型时，直接转换成字符串;</li>
     * <li>为日期类型时，转换成默认格式日期或时间戳;</li>
     * <li>为数值枚举型时，转换成枚举数值字符串;</li>
     * <li>为集合时，将集合中的每个元素递归调用本方法转换成为 Map，再放入至 List 中;</li>
     * <li>为 Map 时，将 key 转换成字符串，value 递归调用本方法.</li>
     * <ol>
     * </p>
     *
     * @param bean 容器对象.
     * @return 容器对象的 key-value 形式表示。若传入的非容器对象则直接返回 null.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Map<String, Object> toKVStyle(Object bean) {
        if (!isBean(bean)) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        Field[] fields = bean.getClass().getDeclaredFields();
        String fieldName;
        Object fieldValue;
        String methodName;
        Object value;
        for (Field field : fields) {
            fieldName = field.getName();
            try {
                methodName = "get" + StringUtil.capitalize(fieldName);
                Method method = bean.getClass().getDeclaredMethod(methodName);
                value = method.invoke(bean);
                if (value == null) {
                    continue;
                }
                if (field.getType().equals(Date.class)) {
                    fieldValue = fieldName.equals("orderTime") ? DateUtil.toTimestamp((Date) value)
                            : DateUtil.toDateString((Date) value);
                } else if (value instanceof IntEnum) {
                    fieldValue = ((IntEnum) value).getCode() + "";
                } else if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    List<Map<String, Object>> list = new ArrayList<>(collection.size());
                    for (Object obj : collection) {
                        if (obj instanceof Map) {
                            // noinspection unchecked
                            list.add(plainMap((Map) obj));
                        } else {
                            list.add(toKVStyle(obj));
                        }
                    }
                    fieldValue = list;
                } else if (value instanceof Map) {
                    // noinspection unchecked
                    fieldValue = plainMap((Map) value);
                } else {
                    fieldValue = value + "";
                }
                params.put(StringUtil.camelToSnake(fieldName).toUpperCase(), fieldValue);
            } catch (Exception e) {
                LOGGER.debug("Not found getter of {}: {}", fieldName, e.getMessage());
            }
        }
        return params;
    }

    private boolean isBean(Object obj) {
        if (obj.getClass().getPackage().getName().startsWith("java")) {
            return false;
        }
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Object> plainMap(Map<Object, Object> map) {
        Map<String, Object> newMap = new HashMap<>(map.size());
        for (Map.Entry entry : map.entrySet()) {
            if (isBean(entry.getValue())) {
                newMap.put(StringUtil.camelToSnake(entry.getKey() + "").toUpperCase(), toKVStyle(entry.getValue()));
            } else {
                newMap.put(StringUtil.camelToSnake(entry.getKey() + "").toUpperCase(), entry.getValue() + "");
            }
        }
        return newMap;
    }

    /**
     * 平台可通过该接口获取医生信息更新，查询排班的医生.
     * <ul>
     * <li>当科室ID（DEPT_ID）为-1时查询所有科室下的医生；</li>
     * <li>当医生ID（DOCTOR_ID）为-1，科室ID（DEPT_ID）不为-1时查询该科室下所有医生；</li>
     * <li>当医生ID（DOCTOR_ID）不为-1时，查询该医生详细信息。</li>
     * </ul>
     *
     * @param hosId 医院ID.
     * @param deptId 科室ID，HIS系统中科室唯一ID，为-1时查所有科室医生.
     * @param doctorId 医生ID，HIS系统中医生唯一ID，为-1时查询该科室下所有医生，否则查指定某个医生信息.
     * @return 结果.
     * @throws Exception 异常.
     */
    public ResModel<DoctorInfo> getDoctorInfo(String hosId, String deptId, String doctorId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        map.put("DEPT_ID", deptId);
        map.put("DOCTOR_ID", doctorId);

        String result = invokeWS("getDoctorInfo", map);
        return parseEnvelop(result, DoctorInfo.class);
    }

    /**
     * 平台调用医院接口获取用户缴费记录，包括待缴费和已缴费的信息.
     * <p>
     * 证件号码或卡号必填其中一项，如果用证件号码查询，医院有多个账户，医院需要根据传入的用户信息刷选出最适合的用户返回。
     * </p>
     *
     * @param req 缴费记录查询请求对象.
     * @return 响应对象及缴费记录列表信息.
     * @throws Exception 异常.
     */
    public ResModel<PayList> getPayList(PayListReq req) throws Exception {
        String res = invokeWS("getPayList", toKVStyle(req));
        return parseEnvelop(res, PayList.class);
    }

    /**
     * 平台调用医院接口获取用户缴费明细记录，包括待缴费和已缴费的信息，涉及医保部分由医院和医保部门结算后返回相应的医保和个人自付金额.
     *
     * @param hosId 医院ID，必填.
     * @param hospPatientId 用户院内ID.
     * @param hospSequence HIS就诊登记号，必填.
     * @return 响应对象及缴费明细列表信息.
     * @throws Exception 异常.
     */
    public ResModel<PayDetailList> getPayDetail(String hosId, String hospPatientId, String hospSequence)
            throws Exception {
        Map<String, Object> map = new HashMap<>(3);
        map.put("HOS_ID", hosId);
        map.put("HOSP_PATIENT_ID", hospPatientId);
        map.put("HOSP_SEQUENCE", hospSequence);

        String result = invokeWS("getPayDetail", map);
        return parseEnvelop(result, PayDetailList.class);
    }

    /**
     * 缴费成功后调用.
     * <p>
     * 用户在支付时，转发服务将支付结果同步到中间服务（支付仅限30分钟内提交的订单）。
     * </p>
     *
     * @param req 请求对象.
     * @return 响应对象及支付信息.
     * @throws Exception 异常.
     */
    public ResModel<PayOrder> payOrder(PayOrderReq req) throws Exception {
        String result = invokeWS("payOrderAPP", toKVStyle(req));
        return parseEnvelop(result, PayOrder.class);
    }

    /**
     * 可以按医院、科室、医生、出诊日期和时段，查询某医生的分时排班信息.
     * <p>
     * 如果TIME_FLAG为具体值时，则查当前传入分时的分时排班；如果TIME_FLAG为-1时查询全天的分时排班。
     * </p>
     *
     * @param hosId 医院ID.
     * @param deptId 科室ID，HIS系统中科室唯一ID.
     * @param doctorId 医生ID，HIS系统中医生唯一ID，为-1时查询科室ID下所有医生排班.
     * @param regDate 出诊日期，格式：YYYY-MM-DD
     * @param timeFlag 时段.
     *            <p>
     *            详见 “时段”，为-1时查询当天所有的分时排班
     *            </p>
     * @return 医生排班分时信息集合.
     * @throws Exception 异常.
     */
    public ResModel<TimeRegInfo> getTimeRegInfo(String hosId, String deptId, String doctorId, Date regDate,
            int timeFlag) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        map.put("DEPT_ID", deptId);
        map.put("DOCTOR_ID", doctorId);
        map.put("REG_DATE", DateUtil.toDateString(regDate));
        map.put("TIME_FLAG", String.valueOf(timeFlag));

        String result = invokeWS("getTimeRegInfo", map);
        return parseEnvelop(result, TimeRegInfo.class);
    }

    /**
     * 医院方发起未支付的挂号订单的取消操作.
     *
     * @param hosId 医院ID.
     * @param orderId 订单号.
     * @param cancelDate 取消时间，格式：YYYY-MM-DD HI24:MI:SS.
     * @param cancelRemark 取消原因.
     * @throws Exception 异常.
     */
    @SuppressWarnings("rawtypes")
    public ResModel cancelReg(String hosId, String orderId, String cancelDate, String cancelRemark) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        map.put("ORDER_ID", orderId);
        map.put("CANCEL_DATE", cancelDate);
        map.put("CANCEL_REMARK", cancelRemark);
        String result = invokeWS("cancelReg", map);
        return (ResModel) unmarshaller.unmarshal(new StreamSource(new StringReader(result)));
    }

    /**
     * 挂号成功后调用。 医院his根据平台订单号（挂号接口有传入）进行挂号订单的支付操作.
     * <p>
     * 用户在支付时，平台将支付结果同步到平台（支付仅限30分钟内提交的订单）。
     * </p>
     *
     * @param req 挂号支付请求.
     * @return 操作结果.
     * @throws Exception 异常.
     */
    public ResModel<PayReg> payReg(PayRegReq req) throws Exception {
        String result = invokeWS("payReg", toKVStyle(req));
        return parseEnvelop(result, PayReg.class);
    }

    /**
     * 用户挂号并支付成功后需要取消挂号并退款时，调用该接口 如果在平台调用医院退款接口，医院订单已经退款成功的情况下，医院应该直接返回退款成功。
     *
     * @param req 退款请求.
     * @return 操作结果.
     * @throws Exception 异常.
     */
    public ResModel<Refund> refund(RefundReq req) throws Exception {
        String result = invokeWS("refund", toKVStyle(req));
        return parseEnvelop(result, Refund.class);
    }

    public ResModel<PayList> getPayDetailAll(PayListReq req) throws Exception {
        String result = invokeWS("getPayDetailAPP", toKVStyle(req));
        return parseEnvelop(result, PayList.class);
    }

    public ResModel<StopRegInfo> stopReg() throws Exception {
        Map<String, Object> map = new HashMap<>();
        String result = invokeWS("stopReg", map);
        return parseEnvelop(result, StopRegInfo.class);
    }

    /**
     * 查询线下退款记录.
     *
     * @param hosId 医院Id.
     * @param startDate 开始时间,格式:yyyy-MM-dd HH24:mm:ss.
     * @param endDate 结束时间(不包括),格式:yyyy-MM-dd HH24:mm:ss.
     * @return 操作结果.
     * @throws Exception 异常.
     */
    public ResModel<RefundByHisInfo> refundByHisToAPP(String hosId, Date startDate, Date endDate) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("HOS_ID", hosId);
        map.put("BEGIN_DATE", DateUtil.toTimestamp(startDate));
        map.put("END_DATE", DateUtil.toTimestamp(endDate));
        String result = invokeWS("refundByHisToAPP", map);
        return parseEnvelop(result, RefundByHisInfo.class);
    }

    /**
     * 当日挂号,将预约挂号和预约的支付参数整合传入
     *
     * @param req 支付请求.
     * @return 操作结果.
     * @throws Exception 异常.
     */
    public ResModel<PayReg> payOrderReg(PayOrderRegReq req) throws Exception {
        String result = invokeWS("payOrderReg", toKVStyle(req));
        return parseEnvelop(result, PayReg.class);
    }

    /**
     * 查询检验检测报告.
     *
     * @param req 查询检验检测报告请求.
     * @return 操作结果.
     * @throws Exception 异常.
     */
    public ResModel<ReportInfo> getCheckOutReportList(ReportListReq req) throws Exception {
        String result = invokeWS("getCheckOutReportList", toKVStyle(req));
        return parseEnvelop(result, ReportInfo.class);
    }

    /**
     * 查询检验检测报告详细信息
     *
     * @param req 查询检验检测报告详细信息请求.
     * @return 操作结果.
     * @throws Exception 异常.
     */
    public ResModel<ReportDetailInfo> getNormalReportInfo(ReportInfoReq req) throws Exception {
        String result = invokeWS("getNormalReportInfo", toKVStyle(req));
        return parseEnvelop(result, ReportDetailInfo.class);
    }


    /**
     * 绑卡
     */
    public ResModel<PatRes> createPat(PatReq req) throws Exception {
        String result = invokeWS("createPat", toKVStyle(req));
        return parseEnvelop(result, PatRes.class);
    }
}
