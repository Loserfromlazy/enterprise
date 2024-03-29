package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.entity.PageResult;
import com.yhr.enterprise.entity.Result;
import com.yhr.enterprise.pojo.Template;
import com.yhr.enterprise.service.TemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/template")
public class TemplateController {

    @Reference
    private TemplateService templateService;

    @GetMapping("/findAll")
    public List<Template> findAll(){
        return templateService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Template> findPage(int page, int size){
        return templateService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Template> findList(@RequestBody Map<String,Object> searchMap){
        return templateService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Template> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  templateService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Template findById(Integer id){
        return templateService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Template template){
        templateService.add(template);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Template template){
        templateService.update(template);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        templateService.delete(id);
        return new Result();
    }

}
