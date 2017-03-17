package com.proper.enterprise.isj.order.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.MapParamsObjectValueContext;
import com.proper.enterprise.isj.context.OrderNumContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.order.business.OrderInfoGetOrderByIdBusiness;
import com.proper.enterprise.isj.order.business.OrderInfoRecipeOrderBusiness;

/**
 * 订单.
 * Created by think on 2016/9/5 0005.
 */
@RestController
@RequestMapping(path = "/orders")
public class OrderInfoController extends IHosBaseController {

    @SuppressWarnings("unchecked")
    @RequestMapping(path = "/{orderNum}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String orderNum) {
        return responseOfGet((Map<String, Object>) toolkit.execute(OrderInfoGetOrderByIdBusiness.class,
                c -> ((OrderNumContext<Map<String, Object>>) c).setOrderNum(orderNum)));
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> recipeOrder(@RequestBody Map<String, Object> recipeMap)
            throws Exception {
       

        return responseOfPost((Map<String, String>) toolkit.execute(OrderInfoRecipeOrderBusiness.class,
                c -> ((MapParamsObjectValueContext<Map<String, String>>) c).setMapParams(recipeMap)));

    }
}
