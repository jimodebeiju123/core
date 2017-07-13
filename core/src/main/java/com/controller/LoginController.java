/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/3/28 0028
 */
package com.controller;

import com.ehcache.EhcacheService;
import com.redis.UserCacheService;
import com.redis.dao.UserDao;
import com.socket.SocketServerHandler;
import com.utils.QrUtil;
import dto.UserDto;
import net.sf.ehcache.Cache;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

/**
 * @author ZhangLinFeng
 * @name LogionController
 * @data 2017/3/28 0028
 * 登录
 */
@Controller
public class LoginController {

    private Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Resource(name="socketServerHandler")
    private SocketServerHandler socketServerHandler;
    @Resource(name="ehcacheService")
    private EhcacheService ehcacheService;
    @Resource(name="userCacheService")
    private UserCacheService userCacheService;
    @Resource(name="userDao")
    private UserDao userDao;

    @Resource(name="acceptCache")
    private Cache cache;

    @RequestMapping("/gologin")
    public  String  goLogin(){
        return "gologin";
    }




    @RequestMapping("/goTest")
    @ResponseBody
    public  String  goTest() throws Exception{

        // 1、构造一个word分析器ChineseWordAnalyzer
        Analyzer analyzer = new ChineseWordAnalyzer( SegmentationFactory.getSegmentation(SegmentationAlgorithm.BidirectionalMaximumMatching));
        //如果需要使用特定的分词算法，可通过构造函数来指定：
        //  Analyzer analyzer = new ChineseWordAnalyzer(SegmentationAlgorithm.FullSegmentation);
        //如不指定，默认使用双向最大匹配算法：SegmentationAlgorithm.BidirectionalMaximumMatching
        //可用的分词算法参见枚举类：SegmentationAlgorithm

        //2、利用word分析器切分文本
        TokenStream tokenStream = analyzer.tokenStream("myfile", "农业银行股份有限公司武汉分行");
        //准备消费
        tokenStream.reset();
        //开始消费
        while(tokenStream.incrementToken()){
            //词
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
            //词在文本中的起始位置
            OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
            //第几个词
            PositionIncrementAttribute positionIncrementAttribute = tokenStream.getAttribute(PositionIncrementAttribute.class);

            System.out.println(charTermAttribute.toString()+" ("+offsetAttribute.startOffset()+" - "+offsetAttribute.endOffset()+") "+positionIncrementAttribute.getPositionIncrement());
        }
        //消费完毕
        tokenStream.close();

        //  3、利用word分析器建立Lucene索引
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        Document doc = new Document();
        String text = "农业银行股份有限公司武汉分行";
        doc.add(new Field("myfile", text, TextField.TYPE_STORED));
        iwriter.addDocument(doc);

        Document doc1 = new Document();
        String text1 = "有两个属性可选：存储和索引。通过,存储属性你可以控制是否对这个Field进行存储";
        doc1.add(new Field("myfile", text1, TextField.TYPE_STORED));
        iwriter.addDocument(doc1);
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("myfile", analyzer);
        Query query = parser.parse("农行");
        ScoreDoc[] hits = isearcher.search(query, Integer.MAX_VALUE).scoreDocs;
        System.out.println("检索到的词："+hits.length);
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            IndexableField f=hitDoc.getField("myfile");

            System.out.println(f.stringValue());
        }
        ireader.close();
        directory.close();


        return "gologin";
    }

    @RequestMapping("/gologinbywx")
    public  String  goLoginByWx(Model model,String clientId){
        model.addAttribute("clientId",clientId);
        return "gologinbywx";
    }

    @RequestMapping("/dologin")
    @ResponseBody
    public  String    doLogin(String clientId, UserDto user,HttpServletRequest request){
        if(user==null|| StringUtils.isEmpty(clientId)){
            return "error";
        }
        //验证用户名和密码
        UserDto u= userDao.selectById(user.getUserName());
        if(u!=null&&u.getPassword().equals(user.getPassword())){
            logger.info("当前客户验证成功开始执行登录。");
            //执行登录  浏览器跳转
            socketServerHandler.login(clientId,user);
            return "success";
        }

        return "error";

    }


    @RequestMapping("/getQr")
    public  void  getQr(HttpServletRequest request, HttpServletResponse response){
        ServletOutputStream out = null;
        try {
            String uuid= (String) cache.get("login").getObjectValue();
            out = response.getOutputStream();
            QrUtil.createQr("http://119.23.243.79:8088/core/gologinbywx.html?clientId="+uuid,out);
            out.flush();
        } catch (FileNotFoundException e) {
            logger.error("文件读取失败,文件不存在",e);
        } catch (Exception e) {
            logger.error("文件流输出异常",e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
