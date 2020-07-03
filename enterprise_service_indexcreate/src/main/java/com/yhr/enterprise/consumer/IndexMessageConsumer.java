package com.yhr.enterprise.consumer;

import com.alibaba.fastjson.JSON;
import com.yhr.enterprise.pojo.Sku;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexMessageConsumer implements MessageListener {
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
            List<Sku> list =new ArrayList<>();
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
                list.add(sku);
            }


            // 连接rest接口
            //参数 ：地址，端口，协议
            HttpHost httpHost = new HttpHost("127.0.0.1",9200,"http");
            RestClientBuilder restClientBuilder = RestClient.builder(httpHost);//rest构建器
            RestHighLevelClient restHighLevelClient =new RestHighLevelClient(restClientBuilder);//高级客户端对象
            IndexRequest indexRequest = null;
            //封装请求对象
            BulkRequest bulkRequest =new BulkRequest();//批量
            for (int i = 0; i <list.size() ; i++) {
                System.out.println(list.size());
                indexRequest=new IndexRequest("sku","doc",list.get(i).getId());//索引名称  类型  id
                Map skuMap =new HashMap();
                skuMap.put("id",list.get(i).getId());
                skuMap.put("sn",list.get(i).getSn());
                skuMap.put("name",list.get(i).getName());
                skuMap.put("price",list.get(i).getPrice());
                skuMap.put("num",list.get(i).getNum());
                skuMap.put("alertNum",list.get(i).getAlertNum());
                skuMap.put("image",list.get(i).getImage());
                skuMap.put("images",list.get(i).getImages());
                skuMap.put("weight",list.get(i).getWeight());
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String cteatetime = formatter.format(list.get(i).getCreateTime());
                String updatetime = formatter.format(list.get(i).getUpdateTime());
                skuMap.put("createTime",cteatetime);
                skuMap.put("updateTime",updatetime);
                skuMap.put("spuId",list.get(i).getSpuId());
                skuMap.put("categoryId",list.get(i).getCategoryId());
                skuMap.put("categoryName",list.get(i).getCategoryName());
                skuMap.put("brandName",list.get(i).getBrandName());
                skuMap.put("spec",list.get(i).getSpec());
                skuMap.put("saleNum",list.get(i).getSaleNum());
                skuMap.put("commentNum",list.get(i).getCommentNum());
                skuMap.put("status",list.get(i).getStatus());
                indexRequest.source(skuMap);
                bulkRequest.add(indexRequest);
            }

            //获取执行结果
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            int status = bulkResponse.status().getStatus();
            System.out.println(status);
            restHighLevelClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
