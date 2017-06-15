package com.redis.dao;

import dto.UserDto;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.apache.lucene.util.Version;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.util.WordConfTools;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * UserDaoImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring/spring-*.xml"})
public class UserDaoImplTest {

    @Autowired
    private UserDao userDao;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: insert(UserDto user)
     */
    @Test
    public void testInsert() throws Exception {
        UserDto user = new UserDto();
        user.setId(UUID.randomUUID().toString());
        user.setUserName("zlf");
        user.setPassword("qweejcjdajdwajidawuidawduu2321");
        userDao.insert(user);
    }

    /**
     * Method: selectById(String id)
     */
    @Test
    public void testSelectById() throws Exception {
        UserDto user = userDao.selectById("8f81caa0-ccbd-425c-8741-9003fda28d5e");
        System.out.println(user);
    }


    /**
     * Method: selectById(String id)
     */
    @Test
    public void test444() throws  Exception{

     //   WordConfTools.set("dic.path", "classpath:dic/dic.txt");
      //  DictionaryFactory.reload();//更改词典路径之后，重新加载词典
       // 1、构造一个word分析器ChineseWordAnalyzer
        Analyzer analyzer = new  ChineseWordAnalyzer();
        //如果需要使用特定的分词算法，可通过构造函数来指定：
      //  Analyzer analyzer = new ChineseWordAnalyzer(SegmentationAlgorithm.FullSegmentation);
        //如不指定，默认使用双向最大匹配算法：SegmentationAlgorithm.BidirectionalMaximumMatching
        //可用的分词算法参见枚举类：SegmentationAlgorithm

        //2、利用word分析器切分文本
        TokenStream tokenStream = analyzer.tokenStream("myfile", "有两个属性可选：存储和索引。通过,存储属性你可以控制是否对这个Field进行存储");
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
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        
        Document doc = new Document();
        String text = "有两个属性可选：存储和索引。通过,存储属性你可以控制是否对这个Field进行存储";
        doc.add(new Field("myfile", text, TextField.TYPE_STORED));
        iwriter.addDocument(doc);
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("myfile", analyzer);
        Query query = parser.parse("通过");
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
    }

} 
