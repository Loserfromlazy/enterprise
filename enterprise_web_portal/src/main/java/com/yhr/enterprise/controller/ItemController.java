package com.yhr.enterprise.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yhr.enterprise.pojo.Goods;
import com.yhr.enterprise.pojo.Sku;
import com.yhr.enterprise.pojo.Spu;
import com.yhr.enterprise.service.CategoryService;
import com.yhr.enterprise.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {
    @Reference
    private SpuService spuService;
    @Reference
    private CategoryService categoryService;
    @Value("${pagePath}")
    private String pagePath;
    @Autowired
    private TemplateEngine engine;

    @GetMapping("/createPage")
    public void createPage(String spuId){
        //1.查询商品信息
        Goods goods=spuService.findGoodsById(spuId);

        //获取商品信息
        Spu spu = goods.getSpu();
        List<Sku> skuList = goods.getSkuList();

        //查询商品分类
        List<String> categoryLisy = new ArrayList<String>();
        categoryLisy.add(categoryService.findById(spu.getCategory1Id()).getName());//一级分类
        categoryLisy.add(categoryService.findById(spu.getCategory2Id()).getName());//二级分类
        categoryLisy.add(categoryService.findById(spu.getCategory3Id()).getName());//三级分类

        //构建sku地址列表
        Map<String,String> urlMap =new HashMap<>();
        for (Sku sku: skuList) {
            if ("1".equals(sku.getStatus())){
                String specjson = JSON.toJSONString(JSON.parseObject(sku.getSpec()), SerializerFeature.MapSortField);
                urlMap.put(specjson,sku.getId()+".html");
            }

        }

        //2.批量生成页面
        for (Sku sku :skuList) {
            //获取上下文和数据模型
            Context context =new Context();
            Map<String,Object> dataModel = new HashMap<>();
            dataModel.put("spu",spu);
            dataModel.put("sku",sku);
            dataModel.put("categoryList",categoryLisy);
            if (sku.getImages()!=null){
                dataModel.put("skuImages",sku.getImages().split(","));//sku图片列表
            }else {
                dataModel.put("skuImages",null);
            }

            //dataModel.put("spuImages",spu.getImages().split(","));//spu图片列表

            Map paraItem = (Map) JSON.parse(spu.getParaItems()) ;//参数列表
            dataModel.put("paraItems",paraItem);
            Map<String,String> specItem=(Map)JSON.parseObject(sku.getSpec());//规格列表
            dataModel.put("specItems",specItem);
            //{"颜色":["金色","黑色","蓝色"]}
            //{"颜色":[{option:'金色',checked:true},{option:'黑色',checked:false}],  ....}
            Map<String,List> specMap=(Map)JSON.parseObject( spu.getSpecItems());//规格和规格选项
            for (String key:specMap.keySet()) {
                List<String> list = specMap.get(key);//["金色","黑色","蓝色"]
                List<Map> mapList=new ArrayList<>();//构建新的集合  {option:'金色',checked:true},{option:'黑色',checked:false}
                for (String value:list) {
                    Map map =new HashMap();
                    map.put("option",value);
                    if (specItem.get(key).equals(value)){
                        map.put("checked",true);
                    }else{
                        map.put("checked",false);
                    }

                    Map<String,String> spec = (Map) JSON.parseObject(sku.getSpec());//当前的sku
                    spec.put(key,value);
                    String specjson = JSON.toJSONString(spec, SerializerFeature.MapSortField);

                    map.put("url",urlMap.get(specjson));
                    mapList.add(map);
                }

                specMap.put(key,mapList);//用新的集合替换原有的集合
            }

            dataModel.put("specMap",specMap);

            context.setVariables(dataModel);
            //准备文件
            String property = System.getProperty("evan.webapp");
            System.out.println(property+pagePath);
            File dir =new File(property+pagePath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File dest =new File(dir, sku.getId()+".html");
            //生成页面
            try {
                PrintWriter writer =new PrintWriter(dest,"UTF-8");
                engine.process("item",context,writer);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }
}
