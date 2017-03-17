package com.proper.enterprise.isj.proxy.service.notx;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.proper.enterprise.isj.context.RegistrationDocIdContext;
import com.proper.enterprise.isj.context.RegistrationDocNumContext;
import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.proxy.business.his.HisCreateRegistrationAndOrderFunction;
import com.proper.enterprise.isj.proxy.business.his.HisCreateRegistrationBusiness;
import com.proper.enterprise.isj.proxy.business.registration.FetchRegistrationDocumentByIdBusiness;
import com.proper.enterprise.isj.proxy.business.registration.FetchRegistrationDocumentByNumBusiness;
import com.proper.enterprise.isj.proxy.business.registration.SaveRegistrationDocumentBusiness;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.DateUtil;

public abstract class AbstractRegistrationService extends AbstractService {

    public AbstractRegistrationService() {
        super();
    }

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 通过ID获取挂号信息.
     *
     * @param id 挂号单ID.
     * @return 获取的挂号单对象.
     */
    public RegistrationDocument getRegistrationDocumentById(String id) {
        return toolkit.execute(FetchRegistrationDocumentByIdBusiness.class, (c) -> {
            ((RegistrationDocIdContext<?>) c).setRegistrationDocumentId(id);
        });
    }

    /**
     * 保存或更新挂号单信息.
     *
     * @param reg 挂号报文.
     * @return 挂号报文.
     */
    public RegistrationDocument saveRegistrationDocument(RegistrationDocument reg) {
        return toolkit.execute(SaveRegistrationDocumentBusiness.class, (c) -> {
            ((RegistrationDocumentContext<?>) c).setRegistrationDocument(reg);
        });
    }

    /**
     * 通过用户ID以及支付状态获取挂号单信息.
     *
     * @param userId 患者ID.
     * @param status 支付状态.
     * @param isAppointment 挂号类别.
     * @return 挂号单信息.
     */
    public List<RegistrationDocument> findRegistrationByCreateUserIdAndPayStatus(String userId, String status,
            String isAppointment) {
        return toolkit.executeRepositoryFunction(RegistrationRepository.class,
                "findByCreateUserIdAndStatusCodeAndIsAppointment", userId, status, isAppointment);
    }

    /**
     * 根据挂号单号查询挂号单.
     *
     * @param num 挂号单号.
     * @return 挂号单.
     */
    public RegistrationDocument getRegistrationDocumentByNum(String num) {
        return toolkit.execute(FetchRegistrationDocumentByNumBusiness.class, (c) -> {
            ((RegistrationDocNumContext<?>) c).setRegistrationDocumentNum(num);
        });
    }

    /**
     * 删除挂号信息.
     *
     * @param reg 挂号信息.
     */
    public void deleteRegistrationDocument(RegistrationDocument reg) {
        toolkit.execute(SaveRegistrationDocumentBusiness.class, (c) -> {
            ((RegistrationDocumentContext<?>) c).setRegistrationDocument(reg);
        });
    }

    /**
     * 通过患者ID查询挂号单信息.
     *
     * @param patientId 患者ID.
     * @return 挂号单列表.
     */
    public List<RegistrationDocument> findRegistrationDocumentList(String patientId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("patientId").is(patientId));
        query.with(new Sort(Sort.Direction.DESC, "apptDate").and(new Sort(Sort.Direction.DESC, "createTime")));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 查询已支付,进行了退号操作的记录
     */
    public List<RegistrationDocument> findAlreadyCancelRegAndRefundErrRegList() {
        Query query = new Query();
        Pattern cancelHisReturnMsgPattern = Pattern.compile("^.*" + ReturnCode.SUCCESS + ".*$",
                Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("statusCode").is(RegistrationStatusEnum.PAID.getValue())
                .and("cancelHisReturnMsg").regex(cancelHisReturnMsgPattern));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 获取订单超时未付款的挂号单.
     *
     * @return 挂号单.
     */
    public List<RegistrationDocument> findOverTimeRegistrationDocumentList(int overTimeMinute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -overTimeMinute);
        Query query = new Query();
        query.addCriteria(Criteria.where("createTime")
                .lte(DateUtil.toString(cal.getTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT)).and("statusCode")
                .is(RegistrationStatusEnum.NOT_PAID.getValue()));
        query.with(new Sort(Sort.Direction.DESC, "apptDate"));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 通过参数查询挂号单信息.
     *
     * @param paramMap 参数集合.
     * @return 查询结果.
     */
    public List<RegistrationDocument> findRegistrationDocumentByStopReg(Map<String, String> paramMap) {
        Query query = new Query();
        query.addCriteria(Criteria.where("doctorId").is(paramMap.get("doctorId")).and("deptId")
                .is(paramMap.get("deptId")).and("regDate").is(paramMap.get("regDate")).and("beginTime")
                .gte(paramMap.get("beginTime")).lte(paramMap.get("endTime")).and("statusCode")
                .in(RegistrationStatusEnum.NOT_PAID.getValue(), RegistrationStatusEnum.PAID.getValue()));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 通过创建挂号单用户ID以及患者身份证号查询挂号信息.
     *
     * @param createUserId 创建挂号单用户ID.
     * @param patientIdCard 患者身份证号.
     * @return 查询结果.
     */
    public List<RegistrationDocument> findRegistrationDocumentByCreateUserIdAndPatientIdCard(String createUserId,
            String patientIdCard) {
        return toolkit.executeRepositoryFunction(RegistrationRepository.class,
                "findRegistrationDocumentByCreateUserIdAndPatientIdCard", createUserId, patientIdCard);
    }

    /**
     * 保存挂号单信息.
     *
     * @param saveReg 保存对象.
     * @param isAppointment 挂号单类型.
     * @return saveReg 保存处理后的带有号点信息的对象.
     * @throws Exception 异常.
     */
    public RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument saveReg, String isAppointment)
            throws Exception {
        return toolkit.executeFunction(HisCreateRegistrationAndOrderFunction.class, saveReg, isAppointment);
    }

    /**
     * 生成挂号单
     *
     * @param reg 挂号请求.
     * @return 挂号单.
     * @throws Exception 异常.
     */
    public RegistrationDocument saveCreateRegistration(RegistrationDocument reg) throws Exception {
        return toolkit.execute(HisCreateRegistrationBusiness.class, (c) -> {
            ((RegistrationDocumentContext<?>) c).setRegistrationDocument(reg);
        });
    }

}