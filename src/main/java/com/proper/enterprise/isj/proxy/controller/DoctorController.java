package com.proper.enterprise.isj.proxy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.SearchNameContext;
import com.proper.enterprise.isj.context.SubjectIdContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.schedule.FetchDoctorByIdBusiness;
import com.proper.enterprise.isj.proxy.business.schedule.FetchDoctorPhotoBusiness;
import com.proper.enterprise.isj.proxy.business.schedule.FetchDoctorsBusiness;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * 查询全院医生列表.
 * Created by think on 2016/8/16 0016.
 */
@RestController
@RequestMapping(path = "/doctors")
@AuthcIgnore
public class DoctorController extends IHosBaseController {

    /**
     * 查询全院医生列表.
     *
     * @param searchName
     *            全文检索条件.
     * @param subjectId
     *            学科ID.
     * @return 医生列表.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, String>>> getDoctors(Integer pageNo, String searchName, String subjectId) {
        return responseOfGet(toolkit.execute(FetchDoctorsBusiness.class, c -> {
            ((PageNoContext<List<Map<String, String>>>) c).setPageNo(pageNo);
            ((SubjectIdContext<List<Map<String, String>>>) c).setSubjectId(subjectId);
            ((SearchNameContext<List<Map<String, String>>>) c).setSearchName(searchName);
        }));
    }

    /**
     * 指定医生个人信息.
     *
     * @param id
     *            医生Id.
     * @return 医生个人信息.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DoctorDocument> getDoctorById(@PathVariable String id) {
        return responseOfGet(
                toolkit.execute(FetchDoctorByIdBusiness.class, c -> ((IdContext<DoctorDocument>) c).setId(id)));
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(path = "/photo", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getDoctorPhoto(String doctorId) throws Exception {
        return responseOfGet(toolkit.execute(FetchDoctorPhotoBusiness.class,
                c -> ((DoctorIdContext<String>) c).setDoctorId(doctorId)));
    }
}
