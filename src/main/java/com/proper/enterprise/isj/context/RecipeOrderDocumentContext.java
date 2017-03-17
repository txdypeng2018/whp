package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RecipeOrderDocumentContext<T> extends BusinessContext<T> {
    RecipeOrderDocument getRecipeOrderDocument();

    void setRecipeOrderDocument(RecipeOrderDocument doc);
}
