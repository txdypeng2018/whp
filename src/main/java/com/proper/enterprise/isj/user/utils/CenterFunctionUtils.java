package com.proper.enterprise.isj.user.utils;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeRefundDetailDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.enums.FeedbackEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.user.model.enums.MemberRelationEnum;
import com.proper.enterprise.isj.user.model.enums.SexTypeEnum;
import com.proper.enterprise.isj.webservices.model.enmus.RegLevel;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/8/25 0025.
 */
public class CenterFunctionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CenterFunctionUtils.class);

    public final static String PHONE_NULL = "请输入电话号码";

    public final static String PHONE_ERROR = "请输入有效电话号码";

    public final static String PHONE_EXIST = "手机号已被注册过，请直接登录";

    public final static String PHONE_NONEXIST = "该手机号不存在";

    public final static String PHONE_VOID = "手机号码无效";

    public final static String IDCARD_NULL = "身份证号不能为空";

    public final static String IDCARD_ERROR = "身份证号输入有误";

    public final static String IDCARD_X_ERROR = "身份证号中的x请输入大写";

    public final static String IDCARD_SEX_ERROR = "身份证号与性别不符";

    public final static String IDCARD_EXIST = "该身份证已添加";

    public final static String VERIFICATIONCODE_VOID = "验证码无效";

    public final static String VERIFICATIONCODE_ERROR = "请输入正确的验证码";

    public final static String VERIFICATIONCODE_SEND_ERROR = "发送失败";

    public final static String VERIFICATIONCODE_NULL = "请输入验证码";

    public final static String USER_SAVE_ERROR = "保存用户信息失败";

    public final static String PASSWORD_MODIFY_ERROR = "密码修改失败";

    public final static String PASSWORD_NULL = "请输入密码";

    public final static String NAME_NULL = "请输入姓名";

    public final static String PARAMETER_NULL = "参数获取失败";

    public final static String NOWPASSWORD_NULL = "请输入当前密码";

    public final static String NEWPASSWORD_NULL = "请输入新密码";

    public final static String CONFIRMPASSWORD_NULL = "请输入确认新密码";

    public final static String NEWPASSWORD_DIFFERENT = "两次密码输入不一致";

    public final static String NOWPASSWORD_ERROR = "当前密码不正确";

    public final static String LOGIN_ERROR = "登录失败";

    public final static String SEX_MALE = "男";

    public final static String SEX_FEMALE = "女";

    public final static String SEX_SECRET = "保密";

    public final static String SEX_OTHERS = "其他";

    public final static String MEMBER_FATHER = "爸爸";

    public final static String MEMBER_MOTHER = "妈妈";

    public final static String MEMBER_MATE = "配偶";

    public final static String MEMBER_SON = "儿子";

    public final static String MEMBER_DAUGHTER = "女儿";

    public final static String MEMBER_BROTHER = "兄弟";

    public final static String MEMBER_SISTER = "姐妹";

    public final static String MEMBER_KIN = "朋友";

    public final static String MEMBER_FRIEND = "亲属";

    public final static String MEMBER_OTHER = "其他";

    public final static String MEMBER_ME = "我";

    public final static String ORDER_NON_PAY_ERR = "有未付款的挂号单,不能进行挂号";

    public final static String MEDICALNUM_NON_ERR = "患者没有门诊号";

    public final static String ORDER_SAVE_ERR = "生成订单失败";

    public final static String ORDER_ALREADY_PAID_ERR = "订单已经支付,不能重复支付";

    public final static String ORDER_NON_DATA_ERR = "没有要交费的订单";

    public final static String ORDER_NON_RECIPE_ERR = "缴费订单生成失败,请重新选择要缴费流水号,进行支付";

    public final static String ORDER_DIFF_RECIPE_ERR = "缴费金额与当前待支付金额不符,请重新核对缴费单";

    public final static String ORDER_PAGE_OVERTIME_ERR = "挂号界面超时,请重新选择科室进行挂号";

    public final static String ORDER_OVERTIME_INVALID = "订单已失效";

    public final static String ORDER_CANCEL_OVERTIME_MSG = "超时未支付，订单自动取消";

    public final static String ORDER_CANCEL_MANUAL_MSG = "手动退号";

    public final static String ORDER_CANCEL_PLATFORM_MSG = "平台退号";

    public final static String ORDER_CANCEL_STOPREG_MSG = "该医生已经停诊";

    public final static String ORDER_CANCEL_SYS_MSG = "系统异常,平台发起退号";

    public final static String VERIFICATIONCODE_MSG = "验证码:%s，十五分钟内有效。为确保您的信息安全，牢记后请删除此短信";

    public final static String HIS_DATALINK_ERR = "获取数据出现异常";

    public final static String USER_APP_TIME_ERR = "您的手机时间异常,请更正";

    public final static String SEARCH_CONDITION_NULL_ERROR = "请输入查询条件";

    public final static String APP_SYSTEM_ERR = "系统异常";

    public final static String APP_PARAM_ERR = "参数错误";

    public final static String APP_INFOTYPE_ERR = "温馨提示类型重复";

    public final static String REG_STATUS_NOT_PAID = "未支付";

    public final static String REG_STATUS_PAID = "已支付";

    public final static String REG_STATUS_FINISH = "已完成";

    public final static String REG_STATUS_REFUND = "已退费";

    public final static String REG_STATUS_SUSPEND_MED = "已停诊";

    public final static String REG_STATUS_CANCEL = "已取消";

    public final static String REG_GET_DATA_NULL = "未查到相应挂号单";

    public final static String REG_IS_ABSENCE_ERROR = "选择的号点已经被占,请重新选择其他号点";

    public final static String APP_PACS_REPORT_ERR = "暂未查询出结果,请稍后再试!";


    public final static String REG_TEMP_STOP = "暂停挂号";


    public final static String RECIPE_STATUS_NOT_PAID = "未支付";

    public final static String RECIPE_STATUS_PAID = "已支付";

    public final static String RECIPE_STATUS_REFUND = "已退款";

    public final static String REG_STATUS_EXCHANGE_CLOSED = "交易关闭";

    public final static String REG_STATUS_REFUND_FAIL = "退费失败";

    public final static String MSG_REGISTRATION_SUCCESS = "你已成功挂号";

    public final static String MSG_REGISTRATION_ORDER_SUCCESS = "挂号成功！";

    public final static String MSG_REFUND_SUCCESS = "费用已退还至相应的支付平台";

    public final static String MSG_RECIPE_SUCCESS = "缴费成功";

    public final static String FORM_DATA_ERR = "信息填写错误";

    public final static String LOGIN_INVALID_ERR = "登录失效,请重新登录";

    public final static String CANCELREG_ISTODAY_ERR = "退号失败";

    public final static String CANCELREG_OLD_ORDER = "当前挂号单不能在APP中退号,请在意见反馈中提供S开头的单号,或者支付平台的订单号";

    public final static String PATIENTINFO_GET_ERR = "未查到就诊人信息";

    public final static String PATIENTINFO_MEDICALNUM_NULL_ERR = "当前就诊人未绑定病历号,请到用户界面进行绑卡操作";

    public final static String USERINFO_LOGIN_ERR = "登录异常,请重新登录";

    public final static String ORDERREG_REFUND_ERR = "挂号退费失败";

    public final static String SCHEDULE_MISTAKE_REGDATE_GT_ONE = "排班日期相同出现不同挂号级别";

    public final static String SCHEDULE_MISTAKE_DEPT_NON_DISTRICT = "排班科室没有院区信息";

    public final static String REGLEVEL_1 = "普通";
    public final static String REGLEVEL_2 = "副教授";
    public final static String REGLEVEL_3 = "教授";
    public final static String REGLEVEL_4 = "名专家15";
    public final static String REGLEVEL_5 = "名专家30";
    public final static String REGLEVEL_6 = "名专家50";
    public final static String REGLEVEL_7 = "急诊";
    public final static String REGLEVEL_8 = "开药";
    public final static String REGLEVEL_9 = "老年证普通";
    public final static String REGLEVEL_A = "副教授咨询";
    public final static String REGLEVEL_B = "教授咨询";
    public final static String REGLEVEL_C = "老年证副教";
    public final static String REGLEVEL_D = "干诊(老年证)";
    public final static String REGLEVEL_E = "疗程内挂号";
    public final static String REGLEVEL_F = "透析医保";
    public final static String REGLEVEL_G = "老年证正教";
    public final static String REGLEVEL_H = "普通(特困减)";
    public final static String REGLEVEL_I = "副教(特困减)";
    public final static String REGLEVEL_J = "教授(特困减)";
    public final static String REGLEVEL_K = "急诊(特困减)";
    public final static String REGLEVEL_L = "婴儿结石";
    public final static String REGLEVEL_M = "VIP特需服务";
    public final static String REGLEVEL_N = "VIP特需复诊";
    public final static String REGLEVEL_O = "省级干诊";
    public final static String REGLEVEL_P = "婴儿结石正";
    public final static String REGLEVEL_Q = "婴儿结石副";
    public final static String REGLEVEL_R = "奶粉(咨询)";
    public final static String REGLEVEL_S = "奶粉副(咨询)";
    public final static String REGLEVEL_T = "奶粉正(咨询)";
    public final static String REGLEVEL_U = "老年证名专家";
    public final static String REGLEVEL_V = "老年证VIP";
    public final static String REGLEVEL_W = "院工离休";
    public final static String REGLEVEL_X = "老年正教咨询";
    public final static String REGLEVEL_Y = "老年副教咨询";
    public final static String REGLEVEL_Z = "特需服务";



    /*-----------------缓存的名称---------------*/
    public final static String CACHE_NAME_PEP_TEMP = "pep-temp";

    public final static String CACHE_NAME_PEP_TEMP_60 = "pep-temp_60";

    public final static String CACHE_NAME_PEP_TEMP_120 = "pep-temp_120";

    public final static String CACHE_NAME_PEP_TEMP_600 = "pep-temp_600";

    public final static String CACHE_NAME_PEP_TEMP_900 = "pep-temp_900";

    public final static String CACHE_NAME_PEP_TEMP_2592000 = "pep-temp_2592000";
    /*-----------------缓存的KEY值---------------*/
    //验证码
    public final static String CACHE_KEY_PHONE_VERIFICATIONCODE = "cache_key_phone_verificationcode";
    //超时挂号
    public final static String CACHE_KEY_REGITRATION_SCHEDULER_TASK = "cache_key_regitration_scheduler_task";
    //挂号订单
    public final static String CACHE_KEY_REG_ORDERID_SET = "cache_key_reg_orderid_set";
    //缴费订单
    public final static String CACHE_KEY_RECIPE_ORDERID_SET = "cache_key_recipe_orderid_set";
    //医生分时排班信息
    public final static String CACHE_KEY_DOCTOR_TIME_REG_SET = "cache_key_doctor_time_reg";
    //停诊
    public final static String CACHE_KEY_STOP_REG_SET = "cache_key_stop_reg_set";

    //医生排班信息
    public final static String CACHE_KEY_DOCTOR_SCHEDULING_SET = "cache_key_doctor_scheduling_set";

    //医生信息
    public final static String CACHE_KEY_DOCTOR_PHOTO_MAP = "cache_key_doctor_photo_map";

    //pacs报告信息
    public final static String CACHE_KEY_REPORT_PHOTO_MAP = "cache_key_report_photo_map";

    //检验报告列表报告信息
    public final static String CACHE_KEY_REPORT_LIST_MAP = "cache_key_report_list_map";

    //检验报告列表报告详细信息
    public final static String CACHE_KEY_REPORT_INFO_MAP = "cache_key_report_info_map";

    //pacs报告列表信息
    public final static String CACHE_KEY_REPORT_PACS_LIST_MAP = "cache_key_report_pacs_list_map";

    //缴费
    public final static String CACHE_KEY_PATIENT_RECIPE_SET = "cache_key_patient_recipe_set";

    /**
     * 验证码倒计时分钟
     */
    public final static Integer VERIFICATIONCODE_COUNTDOWN = 15;

    /**
     * 分页每页条数
     */
    public final static Integer DEFAULT_PAGE_NUM = 10;

    /**
     * 订单倒计时分钟
     */
    public final static Integer ORDER_COUNTDOWN = 30;

    /**
     * 排班最大天数
     */
    public final static Integer SCHEDULING_MAXADD_DAY = 30;

    /**
     * 排班最大天数
     */
    public final static Integer PAYMENT_MAX_DAY = 30;

    /**
     * 允许退号提前天数(大于)
     */
    public final static Integer CANCEL_ORDER_BEFORE_NUM = 1;

    public final static String UNREPLAY = "未反馈";

    public final static String REPLAYED = "已反馈";

    /*----------职称---------------------*/
    public final static String DOCTOR_TITLE_ZHURENYISHI="主任医师";

    public final static String DOCTOR_TITLE_FUZHURENYISHI="副主任医师";

    public final static String DOCTOR_TITLE_ZHUZHIYISHI="主治医师";

    public final static String DOCTOR_TITLE_YISHI="医师";

    public final static String DOCTOR_TITLE_JIANXIYISHI="见习医师";

    public final static String DOCTOR_TITLE_WU="无";

    public static int getDoctorTitleSeq(String doctorTitleName){
        int seq = 99;
        if(StringUtil.isNotEmpty(doctorTitleName)){
            if(doctorTitleName.equals(DOCTOR_TITLE_ZHURENYISHI)){
                seq = 1;
            }else if(doctorTitleName.equals(DOCTOR_TITLE_FUZHURENYISHI)){
                seq = 2;
            }else if(doctorTitleName.equals(DOCTOR_TITLE_ZHUZHIYISHI)){
                seq = 3;
            }else if(doctorTitleName.equals(DOCTOR_TITLE_YISHI)){
                seq = 4;
            }else if(doctorTitleName.equals(DOCTOR_TITLE_JIANXIYISHI)){
                seq = 5;
            }else if(doctorTitleName.equals(DOCTOR_TITLE_WU)){
                seq = 6;
            }
        }
        return seq;
    }

    public static Set<String> getDoctorTitleSet(){
        Set<String> titleSet = new HashSet<>();
        titleSet.add(DOCTOR_TITLE_ZHURENYISHI);
        titleSet.add(DOCTOR_TITLE_FUZHURENYISHI);
        titleSet.add(DOCTOR_TITLE_ZHUZHIYISHI);
        titleSet.add(DOCTOR_TITLE_YISHI);
        titleSet.add(DOCTOR_TITLE_JIANXIYISHI);
        return titleSet;
    }

    public static String getPushMsgContent(SendPushMsgEnum pushType, Object pushObj) throws Exception {
        StringBuilder content = new StringBuilder();
        RegistrationDocument reg = null;
        RecipeOrderDocument recipe = null;
        RecipeRefundDetailDocument refundDetail = null;
        DecimalFormat df = new DecimalFormat("0.00");
        switch (pushType) {
            case REG_PAY_SUCCESS:
                reg = (RegistrationDocument) pushObj;
                content.append(CenterFunctionUtils.MSG_REGISTRATION_ORDER_SUCCESS);
                content.append("\n");
                content.append("单号:").append(reg.getNum()).append("\n");
                content.append("病历号：").append(reg.getClinicNum());
                content.append("\n");
                content.append("患者姓名：").append(reg.getPatientName());
                content.append("\n");
                String icard = reg.getPatientIdCard().substring(0, 6).concat("********")
                        .concat(reg.getPatientIdCard().substring(14));
                content.append("身份证号：").append(icard);
                content.append("\n");
                content.append("看诊日期：").append(reg.getRegDate());
                content.append("\n");
                content.append("看诊时间：").append(reg.getBeginTime());
                content.append("\n");
                content.append("看诊科室：").append(reg.getDept());
                content.append("\n");
                content.append("看诊医生：").append(reg.getDoctor());
                content.append("\n");
                if (StringUtil.isNotEmpty(reg.getRegLevelName())) {
                    content.append("看诊级别：").append(reg.getRegLevelName());
                } else {
                    content.append("看诊级别：");
                }
                content.append("\n");
                BigDecimal bigDecimal = new BigDecimal(reg.getAmount());
                content.append("挂号金额：").append(df.format(bigDecimal.divide(new BigDecimal("100")))).append(" 元");
                content.append("\n");
                content.append("看诊序号：");
                RegistrationOrderHisDocument payHis = reg.getRegistrationOrderHis();
                if(StringUtil.isNotEmpty(reg.getRegNum())){
                    content.append(reg.getRegNum());
                }else{
                    content.append(payHis.getHospSerialNum());
                }
//                content.append("\n");
//                content.append("科室地址：").append(reg.getRoomName());
                content.append("\n");
                if (StringUtil.isNotEmpty(payHis.getHospRemark())) {
                    content.append("备注：").append(payHis.getHospRemark());
                    content.append("\n");
                }
                if (reg.getIsAppointment().equals(String.valueOf(1))) {
                    content.append("预约挂号需要持身份证到窗口取号，儿童需要携带户口本取号。");
                }
                break;
            case REG_OVERTIME_CANCEL:
                reg = (RegistrationDocument) pushObj;
                content.append("挂号超时通知！").append("\n");
                content.append("单号：").append(reg.getNum()).append("\n");
                content.append("看诊日期：").append(reg.getRegDate()).append("\n");
                content.append("看诊时间：").append(reg.getBeginTime()).append("\n");
                content.append("看诊科室：").append(reg.getDept()).append("\n");
                content.append("看诊医生：").append(reg.getDoctor()).append("\n");
                content.append("就诊人：").append(reg.getPatientName()).append("\n");
                content.append(CenterFunctionUtils.ORDER_CANCEL_OVERTIME_MSG);
                break;
            case STOP_REG_PLATFORM:
                reg = (RegistrationDocument) pushObj;
                content.append("停诊通知！").append("\n");
                content.append("停诊医生：").append(reg.getDoctor()).append("\n");
                content.append("停诊科室：").append(reg.getDept()).append("\n");
                content.append("停诊日期：").append(reg.getRegDate()).append("\n");
                content.append("停诊开始时间：").append(reg.getBeginTime()).append("\n");
                content.append("停诊结束时间：").append(reg.getEndTime()).append("\n");
                content.append("就诊人：").append(reg.getPatientName()).append("\n");
                content.append("预约挂号可以在APP上进行退号,或者到窗口申请退号");
                break;
            case RECIPE_PAY_SUCCESS:
                recipe = (RecipeOrderDocument) pushObj;
                content.append("诊间缴费成功通知！").append("\n");
                content.append("门诊流水号:").append(recipe.getClinicCode()).append("\n");
                List<RecipePaidDetailDocument> paidList = recipe.getRecipePaidDetailList();
                if (paidList.size() > 0) {
                    BigDecimal recipeBig = new BigDecimal(
                            recipe.getRecipePaidDetailList().get(paidList.size() - 1).getAmount());
                    content.append("缴费金额:").append(recipeBig.divide(new BigDecimal("100"))).append(" 元").append("\n");
                }
                if (StringUtil.isNotEmpty(recipe.getPatientName())) {
                    content.append("就诊人:").append(recipe.getPatientName());
                }
                break;
            case RECIPE_PAID_FAIL:
                recipe = (RecipeOrderDocument) pushObj;
                content.append("诊间缴费退费通知！").append("\n");
                content.append("门诊流水号:").append(recipe.getClinicCode()).append("\n");
                if (StringUtil.isNotEmpty(recipe.getPatientName())) {
                    content.append("就诊人:").append(recipe.getPatientName()).append("\n");
                }
                content.append("已将缴费金额退还到相应支付平台");
                break;
            case RECIPE_PAID_REFUND_FAIL:
                recipe = (RecipeOrderDocument) pushObj;
                content.append("诊间缴费退费通知！").append("\n");
                content.append("门诊流水号：").append(recipe.getClinicCode()).append("\n");
                content.append("您的诊间缴费我们会尽快核对,预计3-5个工作日内返回支付平台");
                break;
            case REG_REFUND_SUCCESS:
                reg = (RegistrationDocument) pushObj;
                content.append("退号退费通知！").append("\n");
                content.append("单号:").append(reg.getNum()).append("\n");
                content.append("看诊日期：").append(reg.getRegDate()).append("\n");
                content.append("看诊时间：").append(reg.getBeginTime()).append("\n");
                content.append("看诊科室：").append(reg.getDept()).append("\n");
                content.append("看诊医生：").append(reg.getDoctor()).append("\n");
                content.append("就诊人：").append(reg.getPatientName()).append("\n");
                content.append(MSG_REFUND_SUCCESS);
                break;

            case RECIPE_REFUND_SUCCESS:
                refundDetail = (RecipeRefundDetailDocument) pushObj;
                content.append("诊间缴费退费通知！").append("\n");
                content.append("门诊流水号：").append(refundDetail.getClinicCode()).append("\n");
                content.append("处方号：").append(refundDetail.getRecipeNo()).append("\n");
                content.append("退费项目：").append(refundDetail.getName()).append("\n");
                content.append("退款金额：")
                        .append(df.format(
                                new BigDecimal(refundDetail.getCost().replace("-", "")).divide(new BigDecimal("100")))).append(" 元")
                        .append("\n");
                content.append(MSG_REFUND_SUCCESS);
                break;
            case RECIPE_REFUND_FAIL:
                refundDetail = (RecipeRefundDetailDocument) pushObj;
                content.append("诊间缴费退费失败！").append("\n");
                content.append("门诊流水号：").append(refundDetail.getClinicCode()).append("\n");
                content.append("处方号：").append(refundDetail.getRecipeNo()).append("\n");
                content.append("退费项目：").append(refundDetail.getName()).append("\n");
                content.append("退款金额：")
                        .append(df.format(
                                new BigDecimal(refundDetail.getCost().replace("-", "")).divide(new BigDecimal("100")))).append(" 元")
                        .append("\n");
                content.append("您的诊间缴费我们会尽快核对,预计3-5个工作日内返回支付平台");
                break;
            case REG_PAY_HIS_RETURNMSG:
                reg = (RegistrationDocument) pushObj;
                content.append("挂号失败！").append("\n");
                content.append("挂号费用已经返还到相应支付平台").append("\n");
                content.append("挂号科室：").append(reg.getDept()).append("\n");
                content.append("挂号医生：").append(reg.getDoctor()).append("\n");
                if(reg.getRegistrationOrderHis()!=null&&StringUtil.isNotEmpty(reg.getRegistrationOrderHis().getClientReturnMsg())){
                    content.append("失败原因：").append(reg.getRegistrationOrderHis().getClientReturnMsg());
                }
                break;
            case REG_PAY_HIS_RETURNERR_MSG:
                reg = (RegistrationDocument) pushObj;
                content.append("退费失败！").append("\n");
                content.append("单号：").append(reg.getNum()).append("\n");
                content.append("挂号医生：").append(reg.getDoctor()).append("\n");
                content.append("失败原因：").append(reg.getRegistrationOrderHis().getClientReturnMsg());
                break;
            case REG_TODAY_NOT_PAY_HIS_MSG:
                reg = (RegistrationDocument) pushObj;
                content.append("交易关闭通知！").append("\n");
                content.append("单号:").append(reg.getNum()).append("\n");
                content.append("看诊日期：").append(reg.getRegDate()).append("\n");
                content.append("看诊时间：").append(reg.getBeginTime()).append("\n");
                content.append("看诊科室：").append(reg.getDept()).append("\n");
                content.append("看诊医生：").append(reg.getDoctor()).append("\n");
                content.append("就诊人：").append(reg.getPatientName()).append("\n");
                content.append("当日挂号需要直接缴费,未缴费直接关闭交易,需要重新进行挂号");
                break;
            default:
                break;
        }
        return content.toString();
    }

    /**
     * 获得挂号级别code对应的中文名称
     */
    public static Map<String, String> getRegLevelNameMap() {
        Map<String, String> regLevelNameMap = new HashMap<>();
        try {
            CenterFunctionUtils cfUtile = new CenterFunctionUtils();
            Field field = null;
            RegLevel[] vals = RegLevel.values();
            for (RegLevel val : vals) {
                field = cfUtile.getClass().getField("REGLEVEL_" + val.getCode());
                regLevelNameMap.put(val.getCode(), field.get(cfUtile).toString());
            }
        } catch (Exception e) {
            LOGGER.debug("获得挂号级别code对应的中文名称", e);
        }
        return regLevelNameMap;
    }

    /**
     * 获取医院ID
     *
     * @return 医院Id
     */
    public static String getHosId() {
        return ConfCenter.get("isj.his.hosId");
    }

    /**
     * 将错误以字符串形式抛到前台
     *
     * @param errMsg
     *            错误消息
     * @return
     */
    public static ResponseEntity setTextResponseEntity(String errMsg, HttpStatus httpStatus) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity(errMsg, responseHeaders, httpStatus);
    }

    /**
     * 生成挂号单号或(订单号/退单号) 1:挂号单,2:订单
     */
    public synchronized static String createRegOrOrderNo(int category) {
        String num = "";
        switch (category) {
            case 1:
                num = DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS");
                break;
            case 2:
                num = new StringBuilder(DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS"))
                        .append(RandomStringUtils.randomNumeric(5)).toString();
                break;
            default:
                num = DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS");
                break;
        }
        return num;
    }

    /**
     * 封装性别
     */
    public static Map<String, String> getSexMap() {
        Map<String, String> sexMap = new HashMap<String, String>();
        sexMap.put(String.valueOf(SexTypeEnum.MALE.getCode()), SEX_MALE);
        sexMap.put(String.valueOf(SexTypeEnum.FEMALE.getCode()), SEX_FEMALE);
        // sexMap.put(String.valueOf(SexTypeEnum.SECRET.getCode()), SEX_SECRET);
        // sexMap.put(String.valueOf(SexTypeEnum.OTHERS.getCode()), SEX_OTHERS);
        return sexMap;
    }

    /**
     * 反馈封装
     */
    public static List<Map<String, String>> getFeedbackListMap() {
        List<Map<String, String>> feedbackList = new ArrayList<>();
        Map<String, String> feedbackMap = new HashMap<>();
        feedbackMap.put("code", FeedbackEnum.UNREPLAY.getValue());
        feedbackMap.put("name", UNREPLAY);
        feedbackList.add(feedbackMap);
        feedbackMap = new HashMap<>();
        feedbackMap.put("code", FeedbackEnum.REPLAYED.getValue());
        feedbackMap.put("name", REPLAYED);
        feedbackList.add(feedbackMap);
        return feedbackList;
    }

    public static Map<String, Map<String, String>> getSexCodeMap() {
        Map<String, Map<String, String>> sMap = new HashMap<>();
        Map<String, String> tempMap = null;
        Map<String, String> sexMap = getSexMap();
        Iterator<Map.Entry<String, String>> sexIter = sexMap.entrySet().iterator();
        while (sexIter.hasNext()) {
            Map.Entry<String, String> entry = sexIter.next();
            tempMap = new HashMap<>();
            tempMap.put("sexCode", entry.getKey());
            tempMap.put("name", entry.getValue());
            sMap.put(entry.getKey(), tempMap);
        }
        return sMap;
    }

    /**
     * 家庭关系映射
     *
     * @return
     */
    public static Map<String, Map<String, String>> getFamilyMenberTypeMap(String onlineUserSexCode) {

        MemberRelationEnum[] relArr = MemberRelationEnum.values();
        Map<String, Map<String, String>> relMap = new HashMap<>();
        Map<String, String> tempMap = null;
        String imgUrl = "";
        String sexCode = "";
        String name = "";
        for (MemberRelationEnum memberRelationEnum : relArr) {
            tempMap = new HashMap<>();
            switch (memberRelationEnum) {
                case FATHER:
                    name = MEMBER_FATHER;
                    sexCode = String.valueOf(SexTypeEnum.MALE.getCode());
                    imgUrl = "./assets/images/pic_old_man.png";
                    break;
                case MOTHER:
                    name = MEMBER_MOTHER;
                    sexCode = String.valueOf(SexTypeEnum.FEMALE.getCode());
                    imgUrl = "./assets/images/pic_old_woman.png";
                    break;
                case MATE:
                    name = MEMBER_MATE;
                    if (onlineUserSexCode.equals(String.valueOf(SexTypeEnum.MALE.getCode()))) {
                        sexCode = String.valueOf(SexTypeEnum.FEMALE.getCode());
                        imgUrl = "./assets/images/pic_woman.png";
                    } else {
                        sexCode = String.valueOf(SexTypeEnum.MALE.getCode());
                        imgUrl = "./assets/images/pic_man.png";
                    }
                    break;
                case SON:
                    name = MEMBER_SON;
                    sexCode = String.valueOf(SexTypeEnum.MALE.getCode());
                    imgUrl = "./assets/images/pic_boy.png";
                    break;
                case DAUGHTER:
                    name = MEMBER_DAUGHTER;
                    sexCode = String.valueOf(SexTypeEnum.FEMALE.getCode());
                    imgUrl = "./assets/images/pic_girl.png";
                    break;
                case BROTHER:
                    name = MEMBER_BROTHER;
                    sexCode = String.valueOf(SexTypeEnum.MALE.getCode());
                    imgUrl = "./assets/images/pic_man.png";
                    break;
                case SISTER:
                    name = MEMBER_SISTER;
                    sexCode = String.valueOf(SexTypeEnum.FEMALE.getCode());
                    imgUrl = "./assets/images/pic_woman.png";
                    break;
                case KIN:
                    name = MEMBER_KIN;
                    sexCode = "";
                    imgUrl = "./assets/images/pic_man.png";
                    break;
                case FRIEND:
                    name = MEMBER_FRIEND;
                    sexCode = "";
                    imgUrl = "./assets/images/pic_man.png";
                    break;
                case OTHER:
                    name = MEMBER_OTHER;
                    sexCode = "";
                    imgUrl = "./assets/images/pic_man.png";
                    break;
                default:
                    imgUrl = "";
                    sexCode = "";
                    name = "";
                    break;
            }
            if (StringUtil.isEmpty(name)) {
                continue;
            }
            tempMap.put("code", memberRelationEnum.getValue());
            tempMap.put("name", name);
            tempMap.put("sexCode", sexCode);
            tempMap.put("img", imgUrl);
            relMap.put(memberRelationEnum.getValue(), tempMap);
        }
        return relMap;
    }

    public static String getRecipeItemStatusName(String itemStatusCode){
        String name = "";
        switch (Integer.parseInt(itemStatusCode)) {
            case 0:
                name = RECIPE_STATUS_NOT_PAID;
                break;
            case 1:
                name = RECIPE_STATUS_PAID;
                break;
            case 2:
                name = RECIPE_STATUS_REFUND;
                break;
            case 6:
                break;
            default:
                name = "";
                break;
        }
        return name;
    }

    public static String getRegistrationStatusName(String regStatusCode) {
        String name = "";

        switch (Integer.parseInt(regStatusCode)) {
            case 0:
                name = REG_STATUS_NOT_PAID;
                break;
            case 1:
                name = REG_STATUS_PAID;
                break;
            case 2:
                name = REG_STATUS_FINISH;
                break;
            case 6:
                name = REG_STATUS_REFUND;
                break;
            case 7:
                name = REG_STATUS_SUSPEND_MED;
                break;
            case 8:
                name = REG_STATUS_CANCEL;
                break;
            case 9:
                name = REG_STATUS_EXCHANGE_CLOSED;
                break;
            case 10:
                name= REG_STATUS_REFUND_FAIL;
                break;
            default:
                name = REG_STATUS_NOT_PAID;
        }
        return name;
    }

    /**
     * 根据原有院区id获取院区名称
     *
     * @param districtId
     * @return
     */
    public static String convertHisDisId2SubjectId(String districtId) {
        String subId = "";
        if(StringUtil.isNotEmpty(districtId)){
            switch (Integer.parseInt(districtId)) {
                case 1:
                    subId = String.valueOf("1207");
                    break;
                case 2:
                    subId = String.valueOf("1221");
                    break;
                case 6:
                    subId = String.valueOf("1222");
                    break;
                default:
                    subId = "";
            }
        }
        return subId;
    }

    /**
     * 根据院区获得操作员Id
     */
    public static String convertDistrictId2OperatorId(String districtId) {
        String subId = "";
        switch (Integer.parseInt(districtId)) {
            case 1207:
                subId = String.valueOf("900008");
                break;
            case 1221:
                subId = String.valueOf("900009");
                break;
            case 1222:
                subId = String.valueOf("900013");
                break;
            default:
                subId = "";
        }
        return subId;
    }

    /**
     * 预约时间
     *
     * @param reg
     * @return
     */
    public static boolean checkRegCanBack(RegistrationDocument reg) {
        boolean canBack = false;
        Date dt = DateUtil.toDate(reg.getRegDate());
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.toDate(DateUtil.toDateString(new Date())));
        if (reg.getIsAppointment().equals(String.valueOf(1))
                && reg.getStatusCode().equals(RegistrationStatusEnum.SUSPEND_MED.getValue())) {
            if (dt.compareTo(cal.getTime()) > 0) {
                canBack = true;
            }
        } else {
            if (reg.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
                canBack = true;
            } else if (reg.getStatusCode().equals(RegistrationStatusEnum.PAID.getValue())) {
                cal.add(Calendar.DAY_OF_MONTH, CANCEL_ORDER_BEFORE_NUM);
                if (dt.compareTo(cal.getTime()) > 0) {
                    canBack = true;
                }
            }
        }
        return canBack;
    }

}
