package com.yhr.enterprise.service;
import com.yhr.enterprise.entity.PageResult;
import com.yhr.enterprise.pojo.Goods;
import com.yhr.enterprise.pojo.Spu;

import java.util.*;

/**
 * spu业务逻辑层
 */
public interface SpuService {


    public List<Spu> findAll();


    public PageResult<Spu> findPage(int page, int size);


    public List<Spu> findList(Map<String, Object> searchMap);


    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size);


    public Spu findById(String id);

    public void add(Spu spu,String specname);


    public void update(Spu spu);


    public void delete(String id);

    public void saveGoods(Goods goods);

    public Goods findGoodsById(String id);

    public void audit(String id,String status,String message);

    public void pull(String id);

    public void put(String id);

    public int putMany(String [] ids);

    public void logicDelete(String id);

    public void unDelete(String id);

}
