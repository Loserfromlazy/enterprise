package com.yhr.enterprise.service;
import com.yhr.enterprise.entity.PageResult;
import com.yhr.enterprise.pojo.Ad;

import java.util.*;

/**
 * ad业务逻辑层
 */
public interface AdService {


    public List<Ad> findAll();


    public PageResult<Ad> findPage(int page, int size);


    public List<Ad> findList(Map<String, Object> searchMap);


    public PageResult<Ad> findPage(Map<String, Object> searchMap, int page, int size);


    public Ad findById(Integer id);

    public void add(Ad ad);


    public void update(Ad ad);


    public void delete(Integer id);

    public List<Ad> findByPosition(String position);

}
