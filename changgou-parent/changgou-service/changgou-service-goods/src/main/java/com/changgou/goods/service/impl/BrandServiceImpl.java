package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired(required = false)
    private BrandMapper brandMapper;

    /**
     * @author 栗子
     * @Description 查询所有品牌
     * @Date 23:29 2019/8/9
     * @param
     * @return java.util.List<com.changgou.goods.pojo.Brand>
     **/
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * @author 栗子
     * @Description 根据id查询
     * @Date 23:36 2019/8/9
     * @param id
     * @return com.changgou.goods.pojo.Brand
     **/
    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * @author 栗子
     * @Description 新增品牌
     * @Date 23:37 2019/8/9
     * @param brand
     * @return void
     **/
    @Override
    public void add(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    /**
     * @author 栗子
     * @Description 更新品牌
     * @Date 23:37 2019/8/9
     * @param brand
     * @return void
     **/
    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Integer id){
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * @author 栗子
     * @Description 根据条件查询
     * @Date 23:57 2019/8/9
     * @param brand
     * @return java.util.List<com.changgou.goods.pojo.Brand>
     **/
    @Override
    public List<Brand> findList(Brand brand) {
        // 封装查询条件
        Example example = createExample(brand);
        List<Brand> brands = brandMapper.selectByExample(example);
        return brands;
    }

    // 封装查询条件
    private Example createExample(Brand brand) {
        Example example = new Example(Brand.class);
        if (brand != null){
            // 封装查询条件
            Example.Criteria criteria = example.createCriteria();
            // 名称模糊查询
            if (!StringUtils.isEmpty(brand.getName())){
                criteria.andLike("name", "%"+brand.getName()+"%");
            }
            // 根据首字母查询
            if (!StringUtils.isEmpty(brand.getLetter())){
                criteria.andEqualTo("letter", brand.getLetter());
            }
            // 根据排序信息查询
            if (!StringUtils.isEmpty(brand.getSeq())){
                criteria.andEqualTo("seq", brand.getSeq());
            }
        }
        return example;
    }

    /**
     * @author 栗子
     * @Description 分页查询
     * @Date 0:14 2019/8/10
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return com.github.pagehelper.PageInfo<com.changgou.goods.pojo.Brand>
     **/
    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size) {
        // 设置分页条件
        PageHelper.startPage(page, size);
        List<Brand> brands = brandMapper.selectAll();
        return new PageInfo<Brand>(brands);
    }

    @Override
    public PageInfo<Brand> findPage(Brand brand, int page, int size) {
        // 设置分页条件
        PageHelper.startPage(page, size);
        // 设置查询条件
        Example example = createExample(brand);
        List<Brand> brands = brandMapper.selectByExample(example);
        return new PageInfo<Brand>(brands);
    }

    /***
     * 根据分类ID查询品牌集合
     * @param categoryid:分类ID
     * @return
     */
    @Override
    public List<Brand> findByCategory(Integer categoryid) {
        //1.查询当前分类所对应的所有品牌信息
        //2.根据品牌ID查询对应的品牌集合

        //自己创建DAO实现查询
        return brandMapper.findByCategory(categoryid);
    }


}
