package com.rong360.main;

import com.google.common.io.Resources;
import com.mysql.jdbc.StringUtils;
import com.rong360.rabbitmq.RabbitAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author liuchi
 * @date 2018/4/8
 */
public class Rong360CDC {
    private final static Logger logger = LoggerFactory.getLogger(CdcClient.class);

    public static void main(String[] args) {

        try {
            if(args.length < 1){
                logger.error("invald params");
                return;
            }
            String instance = args[0];
            if (StringUtils.isEmptyOrWhitespaceOnly(instance)){
                logger.error("invald instance");
                return;
            }
            String cluster = "master";
            if (args.length ==2 && !StringUtils.isEmptyOrWhitespaceOnly(args[1]) ){
                cluster = args[1];
            }

            Properties prop = new Properties();
            InputStream in;
            in = new BufferedInputStream
                    (new FileInputStream(Resources.getResource("env.properties").getPath()));
            prop.load(in);
            String etcdHost = prop.getProperty("etcd.host");
            String etcdUsername = prop.getProperty("etcd.username");
            String etcdPassword = prop.getProperty("etcd.password");
            in.close();
            CdcClient client = new CdcClient(etcdHost, etcdUsername, etcdPassword);
            client.setCluster(cluster);
            client.setInstance(instance);
            client.registerMessageListener(new RabbitAPI());
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception:" + e.getMessage());
            System.exit(1);
        }
    }
}