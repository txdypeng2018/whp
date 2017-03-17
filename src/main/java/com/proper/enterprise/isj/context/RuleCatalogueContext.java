package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RuleCatalogueContext<T> extends BusinessContext<T> {
    String getCatalogue();

    void setCatalogue(String catalogue);
}
