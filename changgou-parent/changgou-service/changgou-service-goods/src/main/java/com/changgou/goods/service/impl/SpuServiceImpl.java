package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/****
 * @Author:传智播客
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired(required = false)
    private SpuMapper spuMapper;

    @Autowired(required = false)
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired(required = false)
    private CategoryMapper categoryMapper;

    @Autowired(required = false)
    private BrandMapper brandMapper;

    /**
     * @author 栗子
     * @Description 逻辑删除
     * @Date 12:12 2019/8/13
     * @param id
     * @return void
     **/
    @Override
    public void logicDelete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if("1".equals(spu.getIsMarketable())){
            // 上架了，不能删除
            throw new RuntimeException("上架了，不能删除");
        }
        spu.setIsDelete("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * @author 栗子
     * @Description 批量上/下架
     * @Date 12:03 2019/8/13
     * @param ids
     * @param isMarketable
     * @return void
     **/
    @Override
    public void isShows(Long[] ids, String isMarketable) {

        // 更新isMarketable值
        Spu spu = new Spu();
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 封装条件
        criteria.andIn("id", Arrays.asList(ids));
        spu.setIsMarketable(isMarketable);
        spuMapper.updateByExampleSelective(spu, example);
    }

    /**
     * @author 栗子
     * @Description 商品上/下
     * @Date 11:58 2019/8/13
     * @param id
     * @param isMarketable
     * @return void
     **/
    @Override
    public void isShow(Long id, String isMarketable) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (!"1".equals(spu.getStatus()) || "1".equals(spu.getIsDelete())){ // 成立
            // 审核未通过  or  已删除
            throw new RuntimeException("不能操作");
        }
        spu.setIsMarketable(isMarketable);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * @author 栗子
     * @Description 商品审核
     * @Date 11:50 2019/8/13
     * @param id
     * @param status 审核状态
     * @return void
     **/
    @Override
    public void audit(Long id, String status) {
//        Spu spu = new Spu();
//        spu.setId(id);
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if ("1".equals(spu.getIsDelete())){
            throw new RuntimeException("已删除的商品无法审核");
        }
        spu.setStatus(status);
        spuMapper.updateByPrimaryKeySelective(spu); // update tb_spu set status = ? where id = id
    }

    /**
     * @author 栗子
     * @Description 商品保存
     * @Date 10:56 2019/8/13
     * @param goods
     * @return void
     **/
    @Override
    public void saveGoods(Goods goods) {
        // 保存spu（商品基本信息）
        Spu spu = goods.getSpu();
        if (spu.getId() == null){   // 新增操作
            long spuId = idWorker.nextId();
            spu.setId(spuId);
            spu.setIsMarketable("0");   // 未上架
            spu.setIsDelete("0");       // 未删除
            spu.setStatus("0");         // 待审核
            spuMapper.insertSelective(spu);
        }else{  // 更新操作
            spu.setStatus("0");         // 待审核
            spuMapper.updateByPrimaryKeySelective(spu);
            // 库存：先删除，在插入
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }

        // 保存sku（库存信息）
        List<Sku> skuList = goods.getSkuList();
        if (skuList != null && skuList.size() > 0){
            for (Sku sku : skuList) {
                long skuId = idWorker.nextId();
                sku.setId(skuId);   // 主键
//                sku.setSpuId(spuId);    // 商品id
                sku.setSpuId(spu.getId());
                // 库存的商品名称 = spu名称 + spu副标题 + 规格名称  + 全网通版4GB+64GB
                //                荣耀10青春版 幻彩渐变 2400万AI自拍
                String name = spu.getName() + " " + spu.getCaption();
                // {"手机屏幕尺寸":"5.5寸","网络":"移动4G","颜色":"白","测试":"学习","机身内存":"32G",
                // "存储":"64G","像素":"300万像素"}
                String spec = sku.getSpec();    // json串
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                if (specMap != null){
                    Set<Map.Entry<String, String>> entries = specMap.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        name += " " + entry.getValue();
                    }
                }
                sku.setName(name);  // 设置sku名称
                sku.setCreateTime(new Date());  // 创建日期
                sku.setUpdateTime(new Date());  // 更新日期
                sku.setCategoryId(spu.getCategory3Id());    // 商品分类的三级id
                sku.setCategoryName(categoryMapper.selectByPrimaryKey(spu.getCategory3Id()).getName());  // 商品分类名称：搜索
                sku.setBrandName(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName()); // 品牌名称
                sku.setStatus("1"); // 正常的
                skuMapper.insertSelective(sku);
            }
        }
    }



//    @Override
//    public void saveGoods(Goods goods) {
//        // 保存spu（商品基本信息）
//        Spu spu = goods.getSpu();
//
//        long spuId = idWorker.nextId();
//        spu.setId(spuId);
//        spu.setIsMarketable("0");   // 未上架
//        spu.setIsDelete("0");       // 未删除
//        spu.setStatus("0");         // 待审核
//        spuMapper.insertSelective(spu);
//        // 保存sku（库存信息）
//        List<Sku> skuList = goods.getSkuList();
//        if (skuList != null && skuList.size() > 0){
//            for (Sku sku : skuList) {
//                long skuId = idWorker.nextId();
//                sku.setId(skuId);   // 主键
//                sku.setSpuId(spuId);    // 商品id
//                // 库存的商品名称 = spu名称 + spu副标题 + 规格名称  + 全网通版4GB+64GB
//                //                荣耀10青春版 幻彩渐变 2400万AI自拍
//                String name = spu.getName() + " " + spu.getCaption();
//                // {"手机屏幕尺寸":"5.5寸","网络":"移动4G","颜色":"白","测试":"学习","机身内存":"32G",
//                // "存储":"64G","像素":"300万像素"}
//                String spec = sku.getSpec();    // json串
//                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
//                if (specMap != null){
//                    Set<Map.Entry<String, String>> entries = specMap.entrySet();
//                    for (Map.Entry<String, String> entry : entries) {
//                        name += " " + entry.getValue();
//                    }
//                }
//                sku.setName(name);  // 设置sku名称
//                sku.setCreateTime(new Date());  // 创建日期
//                sku.setUpdateTime(new Date());  // 更新日期
//                sku.setCategoryId(spu.getCategory3Id());    // 商品分类的三级id
//                sku.setCategoryName(categoryMapper.selectByPrimaryKey(spu.getCategory3Id()).getName());  // 商品分类名称：搜索
//                sku.setBrandName(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName()); // 品牌名称
//                sku.setStatus("1"); // 正常的
//                skuMapper.insertSelective(sku);
//            }
//        }
//    }

    /**
     * Spu条件+分页查询
     * @param spu 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu){
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     * @param spu
     * @return
     */
    public Example createExample(Spu spu){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(spu!=null){
            // 主键
            if(!StringUtils.isEmpty(spu.getId())){
                    criteria.andEqualTo("id",spu.getId());
            }
            // 货号
            if(!StringUtils.isEmpty(spu.getSn())){
                    criteria.andEqualTo("sn",spu.getSn());
            }
            // SPU名
            if(!StringUtils.isEmpty(spu.getName())){
                    criteria.andLike("name","%"+spu.getName()+"%");
            }
            // 副标题
            if(!StringUtils.isEmpty(spu.getCaption())){
                    criteria.andEqualTo("caption",spu.getCaption());
            }
            // 品牌ID
            if(!StringUtils.isEmpty(spu.getBrandId())){
                    criteria.andEqualTo("brandId",spu.getBrandId());
            }
            // 一级分类
            if(!StringUtils.isEmpty(spu.getCategory1Id())){
                    criteria.andEqualTo("category1Id",spu.getCategory1Id());
            }
            // 二级分类
            if(!StringUtils.isEmpty(spu.getCategory2Id())){
                    criteria.andEqualTo("category2Id",spu.getCategory2Id());
            }
            // 三级分类
            if(!StringUtils.isEmpty(spu.getCategory3Id())){
                    criteria.andEqualTo("category3Id",spu.getCategory3Id());
            }
            // 模板ID
            if(!StringUtils.isEmpty(spu.getTemplateId())){
                    criteria.andEqualTo("templateId",spu.getTemplateId());
            }
            // 运费模板id
            if(!StringUtils.isEmpty(spu.getFreightId())){
                    criteria.andEqualTo("freightId",spu.getFreightId());
            }
            // 图片
            if(!StringUtils.isEmpty(spu.getImage())){
                    criteria.andEqualTo("image",spu.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(spu.getImages())){
                    criteria.andEqualTo("images",spu.getImages());
            }
            // 售后服务
            if(!StringUtils.isEmpty(spu.getSaleService())){
                    criteria.andEqualTo("saleService",spu.getSaleService());
            }
            // 介绍
            if(!StringUtils.isEmpty(spu.getIntroduction())){
                    criteria.andEqualTo("introduction",spu.getIntroduction());
            }
            // 规格列表
            if(!StringUtils.isEmpty(spu.getSpecItems())){
                    criteria.andEqualTo("specItems",spu.getSpecItems());
            }
            // 参数列表
            if(!StringUtils.isEmpty(spu.getParaItems())){
                    criteria.andEqualTo("paraItems",spu.getParaItems());
            }
            // 销量
            if(!StringUtils.isEmpty(spu.getSaleNum())){
                    criteria.andEqualTo("saleNum",spu.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(spu.getCommentNum())){
                    criteria.andEqualTo("commentNum",spu.getCommentNum());
            }
            // 是否上架,0已下架，1已上架
            if(!StringUtils.isEmpty(spu.getIsMarketable())){
                    criteria.andEqualTo("isMarketable",spu.getIsMarketable());
            }
            // 是否启用规格
            if(!StringUtils.isEmpty(spu.getIsEnableSpec())){
                    criteria.andEqualTo("isEnableSpec",spu.getIsEnableSpec());
            }
            // 是否删除,0:未删除，1：已删除
            if(!StringUtils.isEmpty(spu.getIsDelete())){
                    criteria.andEqualTo("isDelete",spu.getIsDelete());
            }
            // 审核状态，0：未审核，1：已审核，2：审核不通过
            if(!StringUtils.isEmpty(spu.getStatus())){
                    criteria.andEqualTo("status",spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }
}
