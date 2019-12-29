package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:Spu业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SpuService {


    /**
     * @author 栗子
     * @Description 逻辑删除
     * @Date 12:11 2019/8/13
     * @param id
     * @return void
     **/
    void logicDelete(Long id);

    /**
     * @author 栗子
     * @Description 批量上/下架
     * @Date 12:03 2019/8/13
     * @param ids
     * @param isMarketable
     * @return void
     **/
    void isShows(Long[] ids, String isMarketable);

    /**
     * @author 栗子
     * @Description 商品上/下
     * @Date 11:58 2019/8/13
     * @param id
     * @param isMarketable
     * @return void
     **/
    void isShow(Long id, String isMarketable);

    /**
     * @author 栗子
     * @Description 商品审核
     * @Date 11:50 2019/8/13
     * @param id
     * @param status 审核状态
     * @return void
     **/
    void audit(Long id, String status);

    /**
     * @author 栗子
     * @Description 商品保存
     * @Date 10:56 2019/8/13
     * @param goods
     * @return void
     **/
    void saveGoods(Goods goods);

    /***
     * Spu多条件分页查询
     * @param spu
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(Spu spu, int page, int size);

    /***
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(int page, int size);

    /***
     * Spu多条件搜索方法
     * @param spu
     * @return
     */
    List<Spu> findList(Spu spu);

    /***
     * 删除Spu
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Spu数据
     * @param spu
     */
    void update(Spu spu);

    /***
     * 新增Spu
     * @param spu
     */
    void add(Spu spu);

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
     Spu findById(Long id);

    /***
     * 查询所有Spu
     * @return
     */
    List<Spu> findAll();
}
