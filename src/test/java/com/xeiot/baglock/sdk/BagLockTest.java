package com.xeiot.baglock.sdk;

import com.xeiot.baglock.sdk.model.LockResult;
import com.xeiot.baglock.sdk.model.LogResult;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by wangkun23 on 2019/4/28.
 */
public class BagLockTest {

    private final Logger logger = LoggerFactory.getLogger(BagLockTest.class);

    /**
     * 测试用appkey和秘钥生成并验证
     *
     * @throws DecoderException
     */
    @Test
    public void test() throws DecoderException {
        String mac = "DC2C26142293";
        int sn = 1234567890;
        String userKey = "1234567890abcd";// 最长14个字节

        //1 在安装时生成的箱包锁秘钥(加密和解密使用)
        byte[] appKeyBuf = BaglockUtils.getAppKey(mac, sn, userKey);
        logger.info("appKeyKey {}", Hex.encodeHexString(appKeyBuf).toUpperCase());

        //2 在用户扫码后 获取设备mac地址
        //  调用服务器端生成秘钥返回加密结果,并把加密结果的第一个字节设置为0x02(开锁),最后两个字节设置为crc校验值
        Integer RootCNT = 1006;
        Integer dwTimeStamp = 1545264000;
        String userId = "18938995";// 最长8 个字节
        byte[] cipherBuf = BaglockUtils.getCipher(RootCNT, dwTimeStamp, userId);
        logger.info("未加密 {}", Hex.encodeHexString(cipherBuf).toUpperCase());

        int crc = BagLockAESUtils.crc16(0, cipherBuf);
        byte[] crcBuf = ByteBuffer.allocate(2).putShort((short) crc).array();
        logger.info("未加密之前CRC {}", Hex.encodeHexString(crcBuf).toUpperCase());

        byte[] encrypt = BagLockAESUtils.encrypt(cipherBuf, appKeyBuf);
        logger.info("加密结果 {}", Hex.encodeHexString(encrypt).toUpperCase());


        byte[] decrypt = BagLockAESUtils.decrypt(encrypt, appKeyBuf);
        logger.info("解密结果 {}", Hex.encodeHexString(decrypt).toUpperCase());

        byte[] sendData = ByteBuffer.allocate(20)
                .put((byte) 0x02)
                .put(encrypt)
                .put((byte) 0x00)
                .put(crcBuf)
                .array();
        logger.info("最终发送的秘钥 {}", Hex.encodeHexString(sendData).toUpperCase());
        // 3 微信小程序收到该秘钥后 执行开锁
    }

    /**
     * 测试生成读取开锁日志的密钥
     * @throws DecoderException
     */
    @Test
    public void readLog() throws DecoderException {
        String mac = "DC2C26BEAAC1";
        int sn = 11111;
        String userKey = "98765412546885";// 最长14个字节
        // 开始时间,获取当前秒数，读全部时为 0xFFFFFFFF，即4294967295
        long startTime = System.currentTimeMillis() / 1000;

        //1 在安装时生成的箱包锁秘钥(加密和解密使用)
        byte[] appKeyBuf = BaglockUtils.getAppKey(mac, sn, userKey);
        logger.info("appKey {}", Hex.encodeHexString(appKeyBuf).toUpperCase());

        byte[] readLogBuf = BaglockUtils.getReadLogBuf(startTime);
        logger.info("未加密数据 {}", Hex.encodeHexString(readLogBuf).toUpperCase());

        int crc = BagLockAESUtils.crc16(0, readLogBuf);
        byte[] crcBuf = ByteBuffer.allocate(2).putShort((short) crc).array();
        logger.info("CRC {}", Hex.encodeHexString(crcBuf).toUpperCase());

        byte[] encrypt = BagLockAESUtils.encrypt(readLogBuf, appKeyBuf);
        logger.info("加密结果 {}", Hex.encodeHexString(encrypt).toUpperCase());

        byte[] sendData = ByteBuffer.allocate(20)
                .put((byte) 0x10)
                .put(encrypt)
                .put((byte) 0x00)
                .put(crcBuf)
                .array();
        logger.info("最终发送的数据 {}", Hex.encodeHexString(sendData).toUpperCase());
        // 2 微信小程序收到该秘钥后 查询日志
    }

    /**
     * 测试生成读取开锁日志的密钥
     * @throws DecoderException
     */
    @Test
    public void readLog() throws DecoderException {
        String mac = "DC2C26BEAAC1";
        int sn = 11111;
        String userKey = "98765412546885";// 最长14个字节
        // 开始时间,获取当前秒数，读全部时为 0xFFFFFFFF，即4294967295
        long startTime = System.currentTimeMillis() / 1000;

        //1 在安装时生成的箱包锁秘钥(加密和解密使用)
        byte[] appKeyBuf = BaglockUtils.getAppKey(mac, sn, userKey);
        logger.info("appKey {}", Hex.encodeHexString(appKeyBuf).toUpperCase());

        byte[] readLogBuf = BaglockUtils.getReadLogBuf(startTime);
        logger.info("未加密数据 {}", Hex.encodeHexString(readLogBuf).toUpperCase());

        int crc = BagLockAESUtils.crc16(0, readLogBuf);
        byte[] crcBuf = ByteBuffer.allocate(2).putShort((short) crc).array();
        logger.info("CRC {}", Hex.encodeHexString(crcBuf).toUpperCase());

        byte[] encrypt = BagLockAESUtils.encrypt(readLogBuf, appKeyBuf);
        logger.info("加密结果 {}", Hex.encodeHexString(encrypt).toUpperCase());

        byte[] sendData = ByteBuffer.allocate(20)
                .put((byte) 0x10)
                .put(encrypt)
                .put((byte) 0x00)
                .put(crcBuf)
                .array();
        logger.info("最终发送的数据 {}", Hex.encodeHexString(sendData).toUpperCase());
        // 2 微信小程序收到该秘钥后 查询日志
    }

    /**
     * 解析日志上传
     * @throws DecoderException
     */
    @Test
    public void parseLog() throws DecoderException {
        String data = "82000c5c499602d200000000000000000000e0f2";
        LockResult lockResult = BaglockUtils.parseLockResult(data);
        logger.info("lockResult {}", lockResult);
    }

    /**
     * 解析读取到的日志
     * @throws DecoderException
     */
    @Test
    public void parseReadLog() throws DecoderException {
        String data = "900031333838383838385CD2721100002B67053C";
        LogResult logResult = BaglockUtils.parseLogResult(data);
        logger.info("logResult {}", logResult);
    }
}
