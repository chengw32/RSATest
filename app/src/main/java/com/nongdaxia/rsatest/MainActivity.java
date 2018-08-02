package com.nongdaxia.rsatest;

import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class MainActivity extends AppCompatActivity {


    String public_key ;
    String private_key ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String ex = Environment.getExternalStorageDirectory().toString();
        Log.e("ex","---"+ex);
         public_key = ex + "/pu.key";
         private_key = ex + "/pr.key";

        //产生随机数 secure 安全的
        SecureRandom secureRandom = new SecureRandom();

        //产生公私钥  私钥是保存服务器用于解密 公钥在客户端加密后的数据传回服务器
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); //RSA 算法
            keyPairGenerator.initialize(2048, secureRandom);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            //拿到公私钥
            PublicKey aPublic = keyPair.getPublic();
            PrivateKey aPrivate = keyPair.getPrivate();

//            公私钥生成写入文件生成文件
            ObjectOutputStream publicOS = new ObjectOutputStream(new FileOutputStream(public_key));
            ObjectOutputStream privateOS = new ObjectOutputStream(new FileOutputStream(private_key));
            //写入文件
            publicOS.writeObject(aPublic);
            privateOS.writeObject(aPrivate);

            //关闭流
            publicOS.close();
            privateOS.close();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        xxx();
    }

    /**
     * Author chen guo wu
     * Time 2018/8/1 0001 下午 2:00
     * Des 将字符串进行加密得到密文
     * */
    private String secure(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        try {
            ObjectInputStream publicIS = new ObjectInputStream(new FileInputStream(public_key));
            Key pk = (Key) publicIS.readObject();
            publicIS.close();


            //公钥加密
            Cipher cipher  = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,pk);//（encrypt 将...译成密码） 将公钥译成密码

            //将字符串转换成bite数组
            byte[] bytes = str.getBytes();

            //将数组加密成密文
            byte[] bytes1 = cipher.doFinal(bytes);

            //用 BASE64Encoder 将密文变成字符串返回
            BASE64Encoder base64Decoder = new BASE64Encoder();
           return  base64Decoder.encode(bytes1) ;

        } catch (IOException e) {
            Log.e("加密后","======   IOException");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e("加密后","======   ClassNotFoundException");
            e.printStackTrace();
        }
        return "";
    }

/**
 * Author chen guo wu
 * Time 2018/8/1 0001 下午 3:47
 * Des 解密密文
 * */
    private String enSecure(String secureSrc) throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        ObjectInputStream privateIS = new ObjectInputStream(new FileInputStream(private_key));
        Key prk = (Key) privateIS.readObject();
        //公钥加密
        Cipher cipher  = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,prk);//（decrypt 解密码） 将私钥解密

        //将密文转成 byte 数组
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] bytes = base64Decoder.decodeBuffer(secureSrc);
        byte[] bytes1 = cipher.doFinal(bytes);
        return new String(bytes1) ;

    }


    public void xxx(){
        try {
            String s = secure("wtf");
            Log.e("加密后","======   "+s);
            try {
                String s1 = enSecure(s);
            Log.e("解密后","======   "+s1);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }


}
