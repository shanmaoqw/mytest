package com.kakarote.crm9.common.config.solr;

import com.jfinal.plugin.IPlugin;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/7/31 15:48
 * @Version 1.0
 */
public class SolrPlugin implements IPlugin {

    private String username;

    private String password;

    private String host;

    private static HttpSolrClient client;

    public SolrPlugin(String username, String password, String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }

    public static HttpSolrClient getClient(){
        return client;
    }

    @Override
    public boolean start() {
        client = new HttpSolrClient.Builder(host)
            .withConnectionTimeout(10000)
            .withSocketTimeout(60000)
            .build();
        return true;
    }

    @Override
    public boolean stop() {
        if (client != null){
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
