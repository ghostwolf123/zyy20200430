package com.zyy.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyy.common.BaseController;
import com.zyy.common.JsonResult;
import com.zyy.common.PageResult;
import com.zyy.system.model.IncomeBill;
import com.zyy.system.model.IncomeType;
import com.zyy.system.model.PaymentType;
import com.zyy.system.service.IncomeBillService;
import com.zyy.system.service.IncomeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收入类型控制器
 *
 * @author 赵钰颖
 */
@RestController
@RequestMapping("${api.version}/income/type")
public class IncomeTypeController extends BaseController {

    @Autowired
    private IncomeTypeService incomeTypeService;
    @Autowired
    private IncomeBillService incomeBillService;

    @GetMapping
    public PageResult<IncomeType> list(@RequestParam(name = "page", defaultValue = "1") int page,
                                       @RequestParam(name = "limit", defaultValue = "20") int limit,
                                       @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        QueryWrapper<IncomeType> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword).or().like("comments", keyword);
        }
        wrapper.eq("user_id", getLoginUserId());
        wrapper.orderByDesc("create_time");
        IPage<IncomeType> incomeTypePage = new Page<>(page, limit);
        incomeTypeService.page(incomeTypePage, wrapper);
        return new PageResult<>(incomeTypePage.getRecords(), incomeTypePage.getTotal());
    }

    @GetMapping("/all")
    public PageResult<IncomeType> list() {
        QueryWrapper<IncomeType> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", getLoginUserId());
        wrapper.orderByDesc("create_time");
        List<IncomeType> list = incomeTypeService.list(wrapper);
        return new PageResult<>(list);
    }

    @PostMapping
    public JsonResult add(IncomeType incomeType) {
        incomeType.setUserId(getLoginUserId());
        if (incomeTypeService.save(incomeType)) {
            return JsonResult.ok();
        } else {
            return JsonResult.error();
        }
    }

    @PutMapping
    public JsonResult update(IncomeType incomeType) {
        if (incomeTypeService.updateById(incomeType)) {
            return JsonResult.ok();
        } else {
            return JsonResult.error();
        }
    }

    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") Long id) {
        incomeBillService.update(new UpdateWrapper<IncomeBill>().eq("income_type_id", id).setSql("income_type_id=null"));
        if (incomeTypeService.removeById(id)) {
            return JsonResult.ok();
        } else {
            return JsonResult.error();
        }
    }
}
