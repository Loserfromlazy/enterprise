package com.yhr.enterprise.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yhr.enterprise.pojo.Goods;
import com.yhr.enterprise.pojo.Sku;
import com.yhr.enterprise.pojo.Spu;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageMessageConsumer implements MessageListener {

    @Value("${pagePath}")
    private String pagePath;
    @Autowired
    private TemplateEngine engine;
    @Override
    public void onMessage(Message message) {

            String id= new String(message.getBody());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url="jdbc:mysql://127.0.0.1:3306/enterprise?useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=Asia/Shanghai";
            String username="root";
            String password="manager";
            Connection connection = DriverManager.getConnection(url,username,password);
            String sql_Count="select * from tb_sku where spu_id="+id;
            PreparedStatement preparedStatement =connection.prepareStatement(sql_Count);
            ResultSet rs=preparedStatement.executeQuery();
            //将结果导出为list列表
            List<Sku> skuList =new ArrayList<>();
            while(rs.next()){
                Sku sku = new Sku();
                sku.setId(rs.getString("id"));
                sku.setSn(rs.getString("sn"));
                sku.setName(rs.getString("name"));
                sku.setPrice(rs.getInt("price"));
                sku.setNum(rs.getInt("num"));
                sku.setAlertNum(rs.getInt("alert_num"));
                sku.setImage(rs.getString("image"));
                sku.setImages(rs.getString("images"));
                sku.setWeight(rs.getInt("weight"));
                sku.setCreateTime(rs.getDate("create_time"));
                sku.setUpdateTime(rs.getDate("update_time"));
                sku.setSpuId(rs.getString("spu_id"));
                sku.setCategoryId(rs.getInt("category_id"));
                sku.setCategoryName(rs.getString("category_name"));
                sku.setBrandName(rs.getString("brand_name"));
                sku.setSpec(rs.getString("spec"));
                sku.setSaleNum(rs.getInt("sale_num"));
                sku.setCommentNum(rs.getInt("comment_num"));
                sku.setStatus(rs.getString("status"));
                skuList.add(sku);
            }
            String sql2 = "select * from tb_spu where id="+id;
            ResultSet resultSet = preparedStatement.executeQuery(sql2);
            resultSet.next();
            Spu spu = new Spu();
            spu.setId(resultSet.getString("id"));
            spu.setSn(resultSet.getString("sn"));
            spu.setName(resultSet.getString("name"));
            spu.setCaption(resultSet.getString("caption"));
            spu.setBrandId(resultSet.getInt("brand_id"));
            spu.setCategory1Id(resultSet.getInt("category1_id"));
            spu.setCategory2Id(resultSet.getInt("category2_id"));
            spu.setCategory3Id(resultSet.getInt("category3_id"));
            spu.setTemplateId(resultSet.getInt("template_id"));
            spu.setFreightId(resultSet.getInt("freight_id"));
            spu.setImage(resultSet.getString("image"));
            spu.setImages(resultSet.getString("images"));
            spu.setSaleService(resultSet.getString("sale_service"));
            spu.setIntroduction(resultSet.getString("introduction"));
            spu.setSpecItems(resultSet.getString("spec_items"));
            spu.setParaItems(resultSet.getString("para_items"));
            spu.setSaleNum(resultSet.getInt("sale_num"));
            spu.setCommentNum(resultSet.getInt("comment_num"));
            spu.setIsMarketable(resultSet.getString("is_marketable"));
            spu.setIsEnableSpec(resultSet.getString("is_enable_spec"));
            spu.setIsDelete(resultSet.getString("is_delete"));
            spu.setStatus(resultSet.getString("status"));


            //查询商品分类
            List<String> categoryLisy = new ArrayList<String>();
            String sql3 = "select * from tb_category where id="+spu.getCategory1Id();
            ResultSet resultSet1 = preparedStatement.executeQuery(sql3);
            resultSet1.next();
            categoryLisy.add(resultSet1.getString("name"));//一级分类
            String sql4 = "select * from tb_category where id="+spu.getCategory2Id();
            ResultSet resultSet2 = preparedStatement.executeQuery(sql4);
            resultSet2.next();
            categoryLisy.add(resultSet2.getString("name"));//二级分类
            String sql5 = "select * from tb_category where id="+spu.getCategory3Id();
            ResultSet resultSet3 = preparedStatement.executeQuery(sql5);
            resultSet3.next();
            categoryLisy.add(resultSet3.getString("name"));//三级分类


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

                File dir =new File(pagePath);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
