package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yhr.enterprise.entity.PageResult;
import com.yhr.enterprise.entity.Result;
import com.yhr.enterprise.pojo.Goods;
import com.yhr.enterprise.pojo.Spu;
import com.yhr.enterprise.service.SkuService;
import com.yhr.enterprise.service.SpuService;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;
    @Reference
    private SkuService skuService;

    @GetMapping("/findAll")
    public List<Spu> findAll(){
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size){
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String,Object> searchMap){
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  spuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Spu findById(String id){
        return spuService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Spu spu, String specname) throws UnsupportedEncodingException {
        String param = new String(specname.getBytes("ISO8859-1"), "UTF-8");
        System.out.println(param);
        spuService.add(spu,param);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Spu spu){
        spuService.update(spu);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        spuService.delete(id);
        return new Result();
    }

    @PostMapping("/save")
    public Result saveGoods(@RequestBody Goods goods){
        spuService.saveGoods(goods);
        return new Result();
    }
    @GetMapping("/findGoodsById")
    public Goods findGoodById(String id){
        return spuService.findGoodsById(id);
    }

    @PostMapping("/audit")
    public Result audit(@RequestBody Map<String ,String> map){
        spuService.audit(map.get("spuId"),map.get("status"),map.get("message"));
        return new Result();
    }
    @GetMapping("/pull")
    public Result pull(String id){
        spuService.pull(id);
        return new Result();
    }
    @GetMapping("/put")
    public Result put(String id){
        spuService.put(id);
        return new Result();
    }
    @GetMapping("/putMany")
    public Result putMant(String [] ids){
        int count = spuService.putMany(ids);
        return new Result(0,"上架"+count+"个商品");
    }
    @GetMapping("/logicDelete")
    public Result logicDelete(String id){
        spuService.logicDelete(id);
        return  new Result();
    }
    @GetMapping("/unDelete")
    public Result unDelete(String id){
        spuService.unDelete(id);
        return new Result();
    }


}
