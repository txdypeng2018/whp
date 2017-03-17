package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.TypeContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.navinfo.SubjectGetListBusiness;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * 科室.
 * Created by think on 2016/8/16 0016. 学科列表
 */
@RestController
@RequestMapping(path = "/subjects")
public class SubjectController extends IHosBaseController {

    /**
     * 取得学科列表
     *
     * @param districtId 院区ID（没有时学科合并显示）
     * @param type 挂号类别，2 为预约挂号，1 为当日挂号
     * @return 学科列表
     */
    @SuppressWarnings("unchecked")
    @AuthcIgnore
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<SubjectDocument>> getSubject(String districtId, @RequestParam String type) {
        return responseOfGet((List<SubjectDocument>) toolkit.execute(SubjectGetListBusiness.class, c -> {
            ((TypeContext<List<SubjectDocument>>) c).setType(type);
            ((DistrictIdContext<List<SubjectDocument>>) c).setDistrictId(districtId);
        }));
    }
}
