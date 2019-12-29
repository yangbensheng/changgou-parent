package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.wwjd.starter.canal.annotation.CanalEventListener;
import com.wwjd.starter.canal.annotation.ListenPoint;
import com.wwjd.starter.canal.annotation.content.DeleteListenPoint;
import com.wwjd.starter.canal.annotation.content.InsertListenPoint;
import com.wwjd.starter.canal.annotation.content.UpdateListenPoint;
import com.wwjd.starter.canal.annotation.table.AlertTableListenPoint;
import com.wwjd.starter.canal.annotation.table.CreateIndexListenPoint;
import com.wwjd.starter.canal.annotation.table.CreateTableListenPoint;
import com.wwjd.starter.canal.annotation.table.DropTableListenPoint;
import com.wwjd.starter.canal.client.core.CanalMsg;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

@CanalEventListener
public class CanalDataEventListener {

    @Autowired(required = false)
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 自定义监听器
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content"},
            eventType = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    public void onEventContent(CanalMsg canalMsg, CanalEntry.RowChange rowChange){
        // 获取分类id
        String categoryId = getColumnValue(rowChange, "category_id");
        // 通过分类id查询广告列表
        Result<List<Content>> result = null;
        try {
            result = contentFeign.list(Long.parseLong(categoryId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // 存入redis
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(result.getData()));
    }

    // 获取对应列的值
    private String getColumnValue(CanalEntry.RowChange rowChange, String columnName) {
        List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
        for (CanalEntry.RowData rowData : rowDatasList) {
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList) {
                if(column.getName().equals(columnName)){
                    return column.getValue();
                }
            }
        }
        return null;
    }


    @InsertListenPoint
    public void onEventInsertData(CanalMsg canalMsg, CanalEntry.RowChange rowChange) {
        System.out.println("======================注解方式（新增数据操作）==========================");
        List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
        for (CanalEntry.RowData rowData : rowDatasList) {
            String sql = "use " + canalMsg.getSchemaName() + ";\n";
            StringBuffer colums = new StringBuffer();
            StringBuffer values = new StringBuffer();
            rowData.getAfterColumnsList().forEach((c) -> {
                colums.append(c.getName() + ",");
                values.append("'" + c.getValue() + "',");
            });


            sql += "INSERT INTO " + canalMsg.getTableName() + "(" + colums.substring(0, colums.length() - 1) + ") VALUES(" + values.substring(0, values.length() - 1) + ");";
            System.out.println(sql);
        }
        System.out.println("\n======================================================");

    }

    @UpdateListenPoint
    public void onEventUpdateData(CanalMsg canalMsg, CanalEntry.RowChange rowChange) {
        System.out.println("======================注解方式（更新数据操作）==========================");
        List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
        for (CanalEntry.RowData rowData : rowDatasList) {

            String sql = "use " + canalMsg.getSchemaName() + ";\n";
            StringBuffer updates = new StringBuffer();
            StringBuffer conditions = new StringBuffer();
            rowData.getAfterColumnsList().forEach((c) -> {
                if (c.getIsKey()) {
                    conditions.append(c.getName() + "='" + c.getValue() + "'");
                } else {
                    updates.append(c.getName() + "='" + c.getValue() + "',");
                }
            });
            sql += "UPDATE " + canalMsg.getTableName() + " SET " + updates.substring(0, updates.length() - 1) + " WHERE " + conditions;
            System.out.println(sql);
        }
        System.out.println("\n======================================================");
    }

    @DeleteListenPoint
    public void onEventDeleteData(CanalEntry.RowChange rowChange, CanalMsg canalMsg) {

        System.out.println("======================注解方式（删除数据操作）==========================");
        List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
        for (CanalEntry.RowData rowData : rowDatasList) {

            if (!CollectionUtils.isEmpty(rowData.getBeforeColumnsList())) {
                String sql = "use " + canalMsg.getSchemaName() + ";\n";

                sql += "DELETE FROM " + canalMsg.getTableName() + " WHERE ";
                StringBuffer idKey = new StringBuffer();
                StringBuffer idValue = new StringBuffer();
                for (CanalEntry.Column c : rowData.getBeforeColumnsList()) {
                    if (c.getIsKey()) {
                        idKey.append(c.getName());
                        idValue.append(c.getValue());
                        break;
                    }


                }

                sql += idKey + " =" + idValue + ";";
                System.out.println(sql);
            }
            System.out.println("\n======================================================");

        }
    }

    @CreateTableListenPoint
    public void onEventCreateTable(CanalEntry.RowChange rowChange) {
        System.out.println("======================注解方式（创建表操作）==========================");
        System.out.println("use " + rowChange.getDdlSchemaName() + ";\n" + rowChange.getSql());
        System.out.println("\n======================================================");
    }

    @DropTableListenPoint
    public void onEventDropTable(CanalEntry.RowChange rowChange) {
        System.out.println("======================注解方式（删除表操作）==========================");
        System.out.println("use " + rowChange.getDdlSchemaName() + ";\n" + rowChange.getSql());
        System.out.println("\n======================================================");
    }

    @AlertTableListenPoint
    public void onEventAlertTable(CanalEntry.RowChange rowChange) {
        System.out.println("======================注解方式（修改表信息操作）==========================");
        System.out.println("use " + rowChange.getDdlSchemaName() + ";\n" + rowChange.getSql());
        System.out.println("\n======================================================");
    }

    @CreateIndexListenPoint
    public void onEventCreateIndex(CanalMsg canalMsg,CanalEntry.RowChange rowChange){
        System.out.println("======================注解方式（创建索引操作）==========================");
        System.out.println("use " + canalMsg.getSchemaName()+ ";\n" + rowChange.getSql());
        System.out.println("\n======================================================");

    }


}
