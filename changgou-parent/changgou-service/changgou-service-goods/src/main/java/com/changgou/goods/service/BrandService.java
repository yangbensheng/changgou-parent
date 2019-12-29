package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BrandService {

    /**
     * @author 栗子
     * @Description 查询所有品牌
     * @Date 23:26 2019/8/9
     * @param
     * @return java.util.List<com.changgou.goods.pojo.Brand>
     **/
    List<Brand> findAll();

    /**
     * @author 栗子
     * @Description 根据id查询
     * @Date 23:33 2019/8/9
     * @param id
     * @return com.changgou.goods.pojo.Brand
     **/
    Brand findById(Integer id);

    /**
     * @author 栗子
     * @Description 新增品牌
     * @Date 23:34 2019/8/9
     * @param brand
     * @return void
     **/
    void add(Brand brand);

    /**
     * @author 栗子
     * @Description 更新品牌
     * @Date 23:34 2019/8/9
     * @param brand
     * @return void
     **/
    void update(Brand brand);

    /***
     * 删除品牌
     * @param id
     */
    void delete(Integer id);

    /**
     * @author 栗子
     * @Description 根据条件查询
     * @Date 23:57 2019/8/9
     * @param brand 封装查询条件
     * @return java.util.List<com.changgou.goods.pojo.Brand>
     **/
    List<Brand> findList(Brand brand);

    /**
     * @author 栗子
     * @Description 分页查询
     * @Date 0:14 2019/8/10
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return com.github.pagehelper.PageInfo<com.changgou.goods.pojo.Brand>
     **/
    PageInfo<Brand> findPage(Integer page, Integer size);

    /***
     * 多条件分页查询
     * @param brand
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(Brand brand, int page, int size);

    /***
     * 根据分类ID查询品牌集合
     * @param categoryid:分类ID
     */
    List<Brand> findByCategory(Integer categoryid);
}
