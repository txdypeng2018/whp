package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface ScheDoctorDocumentContext<T> extends BusinessContext<T> {
    ScheDoctorDocument getScheDoctorDocument();

    void setScheDoctorDocument(ScheDoctorDocument scheDoctorDocument);
}
