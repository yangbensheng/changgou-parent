package com.changgou.util;

import com.changgou.file.FastDFSFile;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class FastDFSClient {
    /**
     * @author 栗子
     * @Description 初始化FastDFS配置
     * @Date 17:12 2019/8/11
     * @return
     **/
    static {
        String path = "fdfs_client.conf";
        String config_name = new ClassPathResource(path).getPath();
        try {
            ClientGlobal.init(config_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author 栗子
     * @Description 文件上传
     * @Date 17:20 2019/8/11
     * @param fastDFSFile,附件信息
     * @return java.lang.String[]
     **/
    public String[] uploadFile(FastDFSFile fastDFSFile){

        try {
            // 获取文件相关属性
            byte[] file_buff = fastDFSFile.getContent();
            String ext_name = fastDFSFile.getExt();
            NameValuePair[] meta_list = new NameValuePair[1];
            meta_list[0] = new NameValuePair(fastDFSFile.getAuthor());
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、文件上传
            String[] uploadResult = storageClient.upload_file(file_buff, ext_name, meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 文件上传的其实无需封装文件，因为在controller接收附件信息MultipartFile中就已经包含了 start
    /**
     * @author 栗子
     * @Description 文件上传
     * @Date 17:42 2019/8/11
     * @param file_buff 附件
     * @param ext_name 附件扩展名
     * @param des 附件备注
     * @return java.lang.String[]
     **/
    public static String[] uploadFile(byte[] file_buff, String ext_name, String des){

        try {
            NameValuePair[] meta_list = new NameValuePair[1];
            meta_list[0] = new NameValuePair(des);
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、文件上传
            String[] uploadResult = storageClient.upload_file(file_buff, ext_name, meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 文件上传的其实无需封装文件，因为在controller接收附件信息MultipartFile中就已经包含了 end


    /**
     * @author 栗子
     * @Description 获取tracker服务器地址
     * @Date 18:05 2019/8/11
     * @param
     * @return java.lang.String
     **/
    public static String getTrackerUrl(){
        try {
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、获取跟踪服务器地址
            String hostAddress = trackerServer.getInetSocketAddress().getAddress().getHostAddress();
            int port = ClientGlobal.getG_tracker_http_port();
            String url = "http://" + hostAddress + ":" + port;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] downloadFile(String group_name, String remote_filename){
        try {
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、文件下载
            byte[] file_buff = storageClient.download_file(group_name, remote_filename);
            return file_buff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author 栗子
     * @Description 删除附件
     * @Date 18:31 2019/8/11
     * @param group_name
     * @param remote_filename
     * @return void
     **/
    public static void deleteFile(String group_name, String remote_filename){
        try {
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、文件删除
            storageClient.delete_file(group_name, remote_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author 栗子
     * @Description 获取附件信息
     * @Date 18:50 2019/8/11
     * @param group_name
     * @param remote_filename
     * @return org.csource.fastdfs.FileInfo
     **/
    public static FileInfo getFileInfo(String group_name, String remote_filename){
        try {
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、获取文件信息
            FileInfo fileInfo = storageClient.get_file_info(group_name, remote_filename);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author 栗子
     * @Description 获取该组所在的存储服务器信息
     * @Date 19:01 2019/8/11
     * @param groupName
     * @return org.csource.fastdfs.StorageServer
     **/
    public static StorageServer getStorageServerInfo(String groupName){

        try {
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、创建存储服务器客户端
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer, groupName);
            return storeStorage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author 栗子
     * @Description 获取集群下的存储服务器信息
     * @Date 19:12 2019/8/11
     * @param groupName
     * @param filename
     * @return org.csource.fastdfs.ServerInfo[]
     **/
    public static ServerInfo[] getServerInfo(String groupName, String filename){

        try {
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、获取集群下的存储服务器信息
            ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, groupName, filename);
            return serverInfos;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
