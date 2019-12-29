package com.changgou.goods.controller;

import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * @author 栗子
     * @Description 查询所有品牌
     * @Date 23:32 2019/8/9
     * @param
     * @return entity.Result<java.util.List<com.changgou.goods.pojo.Brand>>
     **/
    @GetMapping
    public Result<List<Brand>> findAll(){
        List<Brand> list = brandService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", list);
    }

    /**
     * @author 栗子
     * @Description 根据id查询
     * @Date 23:39 2019/8/9
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Brand>
     **/
    @GetMapping("/{id}")
    public Result<Brand> findById(@PathVariable(value = "id") Integer id){
        Brand brand = brandService.findById(id);
        return new Result<Brand>(true, StatusCode.OK, "查询成功", brand);
    }

    /**
     * @author 栗子
     * @Description 添加品牌
     * @Date 23:40 2019/8/9
     * @param brand
     * @return entity.Result
     **/
    @PostMapping
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result(true, StatusCode.OK, "保存成功");
    }

    /**
     * @author 栗子
     * @Description 修改品牌
     * @Date 23:42 2019/8/9
     * @param brand
     * @param id
     * @return entity.Result
     **/
    @PutMapping("/{id}")
    public Result update(@RequestBody Brand brand, @PathVariable(value = "id") Integer id){
        // 设置主键
        brand.setId(id);
        brandService.update(brand);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        brandService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * @author 栗子
     * @Description 根据条件查询
     * @Date 0:05 2019/8/10
     * @param brand
     * @return entity.Result<java.util.List<com.changgou.goods.pojo.Brand>>
     **/
    @PostMapping("/search")
    public Result<List<Brand>> findList(@RequestBody(required = false) Brand brand){
        List<Brand> list = brandService.findList(brand);
        return new Result<List<Brand>>(true, StatusCode.OK, "查询成功", list);
    }

    /**
     * @author 栗子
     * @Description 分页查询
     * @Date 0:19 2019/8/10
     * @param page
     * @param size
     * @return entity.Result<com.github.pagehelper.PageInfo<com.changgou.goods.pojo.Brand>>
     **/
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Brand>> search(@PathVariable(value = "page") Integer page,
                                          @PathVariable(value = "size") Integer size){
        PageInfo<Brand> pageInfo = brandService.findPage(page, size);
        return new Result<PageInfo<Brand>>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 分页搜索实现
     * @param brand
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) Brand brand, @PathVariable  int page, @PathVariable  int size){
        //执行搜索
        PageInfo<Brand> pageInfo = brandService.findPage(brand, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 根据分类实现品牌列表查询
     * /brand/category/{id}  分类ID
     */
    @GetMapping(value = "/category/{id}")
    public Result<List<Brand>> findBrandByCategory(@PathVariable(value = "id")Integer categoryId){
        //调用Service查询品牌数据
        List<Brand> categoryList = brandService.findByCategory(categoryId);
        return new Result<List<Brand>>(true,StatusCode.OK,"查询成功！",categoryList);
    }

}
