package com.proper.enterprise.isj.proxy.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.context.IdsContext;
import com.proper.enterprise.isj.context.InfoContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.TipInfoEntityContext;
import com.proper.enterprise.isj.context.TypeNameContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.tipinfo.PromptTipsDeleteTipInfoBusiness;
import com.proper.enterprise.isj.proxy.business.tipinfo.PromptTipsGetTipInfoBusiness;
import com.proper.enterprise.isj.proxy.business.tipinfo.PromptTipsGetTipsInfoBusiness;
import com.proper.enterprise.isj.proxy.business.tipinfo.PromptTipsSaveTipInfoBusiness;
import com.proper.enterprise.isj.proxy.business.tipinfo.PromptTipsUpdateTipInfoBusiness;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.entity.PromptTipsEntity;

/**
 * 温馨提示Controller
 */
@RestController
@RequestMapping(path = "/prompt")
public class PromptTipsController extends IHosBaseController {

    /**
     * 取得温馨提示列表.
     *
     * @param infoType
     *            温馨提示类型编码,非必填.
     * @param typeName
     *            温馨提示类型名称,非必填.
     * @param info
     *            温馨提示内容,非必填.
     * @param pageNo
     *            当前页码.
     * @param pageSize
     *            每页数量.
     * @return 意见列表.
     * @throws Exception
     *             异常.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/tips")
    public ResponseEntity<PromptTipsEntity> getTipsInfo(@RequestParam(required = false) String infoType,
            @RequestParam(required = false) String typeName, @RequestParam(required = false) String info,
            @RequestParam String pageNo, @RequestParam String pageSize) throws Exception {
        return responseOfGet(toolkit.execute(PromptTipsGetTipsInfoBusiness.class, c -> {
            ((InfoTypeContext<PromptTipsEntity>) c).setInfoType(infoType);
            ((InfoContext<PromptTipsEntity>) c).setInfo(info);
            ((TypeNameContext<PromptTipsEntity>) c).setTypeName(typeName);
            ((PageNoContext<PromptTipsEntity>) c).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<PromptTipsEntity>) c).setPageSize(Integer.parseInt(pageSize));
        }));

    }

    /**
     * 新增温馨提示信息.
     *
     * @param tipInfo
     *            温馨提示对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(path = "/tips", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> saveTipInfo(@RequestBody BaseInfoEntity tipInfo) throws Exception {
        return responseOfPost(toolkit.execute(PromptTipsSaveTipInfoBusiness.class,
                c -> ((TipInfoEntityContext<String>) c).setTipInfoEntity(tipInfo)));
    }

    /**
     * 更新温馨提示信息.
     *
     * @param tipInfo
     *            温馨提示对象.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @PutMapping(path = "/tips", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateTipInfo(@RequestBody BaseInfoEntity tipInfo) throws Exception {
        return responseOfPut(toolkit.execute(PromptTipsUpdateTipInfoBusiness.class,
                c -> ((TipInfoEntityContext<String>) c).setTipInfoEntity(tipInfo)));

    }

    /**
     * 删除温馨提示信息.
     *
     * @param ids
     *            id列表.
     * @return 返回给调用方的应答.
     * @throws Exception 异常.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @DeleteMapping(path = "/tips")
    public ResponseEntity deleteTipInfo(@RequestParam String ids) throws Exception {
        return responseOfDelete(
                toolkit.execute(PromptTipsDeleteTipInfoBusiness.class, c -> ((IdsContext<Boolean>) c).setIds(ids)));
    }

    /**
     * 取得指定温馨提示信息.
     *
     * @param id 提示编号.
     * @return 返回给调用方的应答.
     * @exception Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/tips/{id}")
    public ResponseEntity<BaseInfoEntity> getTipInfo(@PathVariable String id) throws Exception {
        return responseOfGet(
                toolkit.execute(PromptTipsGetTipInfoBusiness.class, c -> ((IdContext<BaseInfoEntity>) c).setId(id)));
    }
}
