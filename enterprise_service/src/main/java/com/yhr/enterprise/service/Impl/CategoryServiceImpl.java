package com.yhr.enterprise.service.Impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yhr.enterprise.dao.CategoryMapper;
import com.yhr.enterprise.entity.PageResult;
import com.yhr.enterprise.pojo.Category;
import com.yhr.enterprise.service.CategoryService;
import com.yhr.enterprise.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 返回全部记录
     * @return
     */
    public List<Category> findAll() {
        return categoryMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Category> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectAll();
        return new PageResult<Category>(categorys.getTotal(),categorys.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Category> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Category> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectByExample(example);
        return new PageResult<Category>(categorys.getTotal(),categorys.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Category findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param category
     */
    public void add(Category category) {
        categoryMapper.insert(category);
        saveCategoryTreeToRedis();
    }

    /**
     * 修改
     * @param category
     */
    public void update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
        saveCategoryTreeToRedis();
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        categoryMapper.deleteByPrimaryKey(id);
        saveCategoryTreeToRedis();
    }

    /**
     * 返回分类树
     * @return
     */
    @Override
    public List<Map> findCategoryTree() {
        //从缓存中提取分类
        System.out.println("从缓存中提取数据");
        return (List<Map>) redisTemplate.boundValueOps(CacheKey.CATEGORY_TREE).get();
}


    /**
     * 将商品分类树放入缓存
     */
    @Override
    public void saveCategoryTreeToRedis() {
        //查询商品分类导航
        Example example =new Example(Category.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("isShow","1");
        example.setOrderByClause("seq");
        List<Category> categories = categoryMapper.selectByExample(example);
        List<Map> categoryTree =findByParentId(categories,0);


        //存入redis

        redisTemplate.boundValueOps(CacheKey.CATEGORY_TREE).set(categoryTree);
    }

    /**
     * 根据父级菜单id返回分类
     * @return
     */
    @Override
    public List<Category> findCategoryByParentId(String id) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId",id);
        List<Category> categoryList = categoryMapper.selectByExample(example);
        return categoryList;
    }

    private List<Map> findByParentId(List<Category> categoryList ,Integer parentId){
        List<Map> mapList =new ArrayList<Map>();
        for (Category category:categoryList) {
            if(category.getParentId().equals(parentId)){
                Map map =new HashMap();
                map.put("name",category.getName());
                map.put("menus",findByParentId(categoryList,category.getId()));
                mapList.add(map);
            }
        }
        return mapList;
    }



    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 分类名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 是否显示
            if(searchMap.get("isShow")!=null && !"".equals(searchMap.get("isShow"))){
                criteria.andLike("isShow","%"+searchMap.get("isShow")+"%");
            }
            // 是否导航
            if(searchMap.get("isMenu")!=null && !"".equals(searchMap.get("isMenu"))){
                criteria.andLike("isMenu","%"+searchMap.get("isMenu")+"%");
            }

            // 分类ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 商品数量
            if(searchMap.get("goodsNum")!=null ){
                criteria.andEqualTo("goodsNum",searchMap.get("goodsNum"));
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }
            // 上级ID
            if(searchMap.get("parentId")!=null ){
                criteria.andEqualTo("parentId",searchMap.get("parentId"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }

        }
        return example;
    }

}
