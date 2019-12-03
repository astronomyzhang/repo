package com.siemens.dasheng.web.util;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * @author wang.liu@siemens.com
 * Created by Liu Wang on 2018/7/19.
 */
@SuppressWarnings({"all"})
public class DefaultEncryptor {

    private static StringEncryptor createDefault() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setKeyObtentionIterations(1000);
        config.setPassword("dpp123456!@#");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPoolSize(1);
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    public static String decrypted(String text){
        String ret  = createDefault().decrypt(text);
        return ret;
    }

}
