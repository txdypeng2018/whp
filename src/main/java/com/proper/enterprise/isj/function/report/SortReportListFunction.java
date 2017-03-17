package com.proper.enterprise.isj.function.report;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SortReportListFunction implements IFunction<List<MedicalReportsDocument>> {

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MedicalReportsDocument> execute(Object... params) throws Exception {
        return getSortedReportList((List<MedicalReportsDocument>)params[0]);
    }
    /**
     * 按照时间倒序排序.
     *
     * @param retList
     *            报告列表.
     * @return retList.
     */
    public static List<MedicalReportsDocument> getSortedReportList(List<MedicalReportsDocument> retList) {
        // 按照时间倒序
        if (retList.size() > 0) {
            Collections.sort(retList, new Comparator<MedicalReportsDocument>() {
                @Override
                public int compare(MedicalReportsDocument doc1, MedicalReportsDocument doc2) {
                    if (StringUtil.isEmpty(doc1.getExaminationDate())) {
                        return 1;
                    } else if (StringUtil.isEmpty(doc2.getExaminationDate())) {
                        return -1;
                    } else if (StringUtil.isEmpty(doc1.getExaminationDate())
                            && StringUtil.isEmpty(doc2.getExaminationDate())) {
                        return 0;
                    } else {
                        return doc1.getExaminationDate().compareTo(doc2.getExaminationDate());
                    }
                }
            });
            Collections.reverse(retList);
        }
        return retList;
    }
    

}