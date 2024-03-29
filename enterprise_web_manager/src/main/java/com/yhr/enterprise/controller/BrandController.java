package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.entity.PageResult;
import com.yhr.enterprise.entity.Result;
import com.yhr.enterprise.pojo.Brand;
import com.yhr.enterprise.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Brand> findPage(int page, int size){
        return brandService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Brand> findList(@RequestBody Map<String,Object> searchMap){
        return brandService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Brand> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  brandService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Brand findById(Integer id){
        return brandService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Brand brand){
        brandService.update(brand);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        brandService.delete(id);
        return new Result();
    }

}
