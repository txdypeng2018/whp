package com.proper.enterprise.isj.proxy.business.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RecipeOrderDocIdContext;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchRecipeOrderDocumentByIdBusiness<M extends RecipeOrderDocIdContext<RecipeOrderDocument> & ModifiedResultBusinessContext<RecipeOrderDocument>>
        implements IBusiness<RecipeOrderDocument, M> {

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(recipeOrderRepository.findOne(ctx.getRecipeOrderDocId()));
    }

}