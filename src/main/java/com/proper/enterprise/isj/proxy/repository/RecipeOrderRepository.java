package com.proper.enterprise.isj.proxy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;

import java.util.List;

public interface RecipeOrderRepository extends MongoRepository<RecipeOrderDocument, String> {

    RecipeOrderDocument getByClinicCode(String clinicCode);

    List<RecipeOrderDocument> findByPatientIdOrderByCreateTimeDesc(String patientId);
}
