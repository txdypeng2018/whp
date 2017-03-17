package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.context.*;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.registration.*;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.doctor.RegisterDoctorDocument;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 挂号.
 * Created by think on 2016/8/16 0016.
 */

@RestController
@RequestMapping(path = "/register")
public class RegisterController extends IHosBaseController {

    /**
     * 取得就诊退号说明
     *
     * @return 就诊退号说明
     */
    @AuthcIgnore
    @RequestMapping(path = "/visitAndBacknumDesc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> getVisitAndBacknumDesc() {
        return responseOfGet(toolkit.execute(RegisterGetVisitAndBacknumDescBusiness.class, c -> {
        }));
    }

    /**
     * 取得挂号须知信息
     *
     * @return 挂号须知信息
     */
    @AuthcIgnore
    @RequestMapping(path = "/agreement", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAgreement() {
        return responseOfGet(toolkit.execute(RegisterGetAgreementBusiness.class, c -> {
        }));
    }

    /**
     * 取得预约挂号提示信息
     *
     * @return 预约挂号提示信息
     */
    @AuthcIgnore
    @RequestMapping(path = "/apptPrompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getApptPrompt() {
        return responseOfGet(toolkit.execute(RegisterGetApptPromptBusiness.class, c -> {
        }));

    }

    /**
     * 取得当日挂号提示信息
     *
     * @return 当日挂号提示信息
     */
    @AuthcIgnore
    @RequestMapping(path = "/todayPrompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTodayPrompt() {
        return responseOfGet(toolkit.execute(RegisterGetTodayPromptBusiness.class, c -> {
        }));
    }

    /**
     * 取得医生挂号信息
     *
     * @param id 医生ID
     * @param date 挂号时间
     * @return 医生挂号信息
     */
    @AuthcIgnore
    @RequestMapping(path = "/doctor", method = RequestMethod.GET)
    public ResponseEntity<RegisterDoctorDocument> getRegisterDoctor(@RequestParam String id,
            @RequestParam String date) {
        return responseOfGet(toolkit.execute(RegisterGetDoctorBusiness.class, (c) -> {
            ((IdContext<?>) c).setId(id);
            ((DateContext<?>) c).setDate(date);
        }));
    }

    /**
     * 取得指定挂号单详细
     *
     * @return 指定挂号单详细
     */
    @RequestMapping(path = "/registrations", method = RequestMethod.GET)
    public ResponseEntity<List<RegistrationDocument>> getUserRegistrations(String memberId, String viewTypeId) throws Exception {
        return responseOfGet(toolkit.execute(RegisterGetUserRegistrationsBusiness.class, (c) -> {
            ((MemberIdContext<?>) c).setMemberId(memberId);
            ((ViewTypeIdContext<?>) c).setViewTypeId(viewTypeId);
        }));
    }

    /**
     * 取得指定挂号单详细
     *
     * @return 指定挂号单详细
     */
    @RequestMapping(path = "/registrations/registration", method = RequestMethod.GET)
    public ResponseEntity<RegistrationDocument> getRegistration(@RequestParam String id) {
        return responseOfGet(toolkit.execute(RegisterGetUserRegistrationsBusiness.class, (c) -> {
            ((IdContext<?>) c).setId(id);
        }));
    }

    /**
     * 添加挂号单.
     *
     * @return 返回的应答.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(path = "/registrations/registration", method = RequestMethod.POST)
    public ResponseEntity addRegistration(@RequestBody RegistrationDocument reg) throws Exception {
        Object res = toolkit.execute(RegisterCtrlAddRegistrationBusiness.class,
                ctx -> ((RegistrationDocumentContext) ctx).setRegistrationDocument(reg));
        return new ResponseEntity(res, HttpStatus.CREATED);
    }

    /**
     * 退号
     *
     * @param regMap 注册信息.
     * @return 应答.
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(path = "/registrations/registration/back", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity cancelRegistration(@RequestBody Map<String, String> regMap) {
        return toolkit.execute(RegisterCancelRegistrationBusiness.class, (c) -> {
            ((MapParamsContext<?>) c).setParams(regMap);
        });
    }

}
