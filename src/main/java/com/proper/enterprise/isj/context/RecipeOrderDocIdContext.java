package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RecipeOrderDocIdContext<T> extends BusinessContext<T> {
    String getRecipeOrderDocId();

    void setRecipeOrderDocId(String recipeOrderDocId);
}
