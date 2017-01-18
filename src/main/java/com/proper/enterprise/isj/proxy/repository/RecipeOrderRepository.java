package com.proper.enterprise.isj.proxy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;

public interface RecipeOrderRepository extends MongoRepository<RecipeOrderDocument, String> {

    RecipeOrderDocument getByClinicCode(String clinicCode);
}
