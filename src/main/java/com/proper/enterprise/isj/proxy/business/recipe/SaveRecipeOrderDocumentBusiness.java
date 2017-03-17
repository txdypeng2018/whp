package com.proper.enterprise.isj.proxy.business.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RecipeOrderDocumentContext;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class SaveRecipeOrderDocumentBusiness<M extends RecipeOrderDocumentContext<Object>>
        implements IBusiness<Object, M> {

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    @Override
    public void process(M ctx) throws Exception {
        recipeOrderRepository.save(ctx.getRecipeOrderDocument());
    }

}