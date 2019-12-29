package com.changgou.content.controller;

import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("content")
@CrossOrigin
public class ContentController {
    @Autowired
    private ContentService contentService;

    /***
     * 根据categoryId查询广告集合
     */
    @GetMapping(value = "/list/{categoryId}")
    public Result<List<Content>> findByCategory(@PathVariable("categoryId") Long id){
        //根据分类ID查询广告集合
        List<Content> contents = contentService.findByCategory(id);
        return new Result<List<Content>>(true, StatusCode.OK,"查询成功！",contents);
    }
}
