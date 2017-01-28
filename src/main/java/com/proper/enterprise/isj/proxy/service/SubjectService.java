package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;

import java.util.List;

public interface SubjectService {

    /**
     * 根据院区 id 获得学科列表.
     * <p>
	 * 预约挂号和当日挂号取得的学科列表有差异，根据规则进行过滤
     *</p>
     * @param districtId    院区.
     * @param isAppointment 是否为预约挂号.
     * @return 院区下学科列表，包含一级学科和二级学科.
     */
    List<SubjectDocument> findSubjectDocumentListFromHis(String districtId, boolean isAppointment) throws Exception;

}
