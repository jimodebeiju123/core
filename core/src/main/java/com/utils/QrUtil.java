/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/3/27 0027
 */
package com.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.HashMap;

/**
 * @author ZhangLinFeng
 * @name QrUtil
 * @data 2017/3/27 0027
 */
public class QrUtil {

    private static final Logger logger = LoggerFactory.getLogger(QrUtil.class);


    public static void   createQr(String msg,OutputStream outputStream) throws WriterException {
        try {
            int qrcodeWidth = 300;
            int qrcodeHeight = 300;
            String qrcodeFormat = "png";
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            BitMatrix bitMatrix = new MultiFormatWriter().
                    encode(msg, BarcodeFormat.QR_CODE, qrcodeWidth, qrcodeHeight, hints);
            MatrixToImageWriter.writeToStream(bitMatrix,qrcodeFormat,outputStream);
        } catch (Exception e) {
            logger.error("创建二维码失败！");
        }
    }
}
