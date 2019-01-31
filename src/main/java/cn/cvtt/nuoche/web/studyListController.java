package cn.cvtt.nuoche.web;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


@Controller
public class studyListController extends  BaseController {
    @RequestMapping("/testCollection")
    @ResponseBody
//    数据和集合的区别：
//            （1）数组长度固定，集合长度不变。
//            （2）内容不同，数组存储的是同一种类型的。
//            （3）元素的数据类型，数组，引用和基本。集合只能存储引用型，集合存对象，长度可变。
//    集合：
//    存储多个元素的：
//            （1）多个元素中不能有相同类型的元素
//（2）按照某种规则排序一下
//（3）针对不同的需求，java提供了不同的集合类，数据结构都不同
//    数据结构：数据的存储方式，需要能存储东西。
//    提取共性的东西，得到一个集合的继承体系结构
//
//    List                                    set
//1、arraylist
//2、vector
//
//
//    collection：
//            (1)添加功能 add,addAll
//            (2)移除功能
//              clear,移除所有，不建议使用
//              remove<>
//              removeAll<>移除一个元素
//            (3)判断，
//              集合中是否包含指定元素，contains<Object c>
//              集合中是否包含指定集合元素,containsAll<Collection c>
//              isEmpty(),
//            (4)获取功能
//              Iterator<E>，
//            (5)长度功能，size()
//            (6)交集功能
//              retainAll
//            (7)把集合转换成数组，toArray()

    public  void  testCollection(){
        //测试不带all的方法
        //创建集合对象，JDK不提供此接口的任何直接实现：它提供了更具体的子接口的实现，如Set和List 。
        //Collection a=new Collction();接口不能实例化
        Collection a=new ArrayList();
        a.add("hello");
        logger.info("[add]a:"+a);
        a.add(" java");
        logger.info("[add]a:"+a);
        //a.clear();
        //logger.info("[clear]a:"+a);
        //a.remove("hello");
        //logger.info("[add]a:"+a);
        //system里判断不动集合，增删动集合
        System.out.println("is collection contains :"+a.contains(" java"));//java会报false，空格+java会报true
        System.out.println("is collection contains happy:"+a.contains("happy"));
        System.out.println("is collection  empty:"+a.isEmpty());
        System.out.println("collection  size:"+a.size());
        a.clear();
        System.out.println("after clear ,is collection  empty:"+a.isEmpty());
        //创建集合1
        Collection test1=new ArrayList();
        test1.add("666");
        test1.add("777");
        Collection test2=new ArrayList();
        test2.add("666");
        test2.add("888");
        //test1.addAll(test2);
        //System.out.println("test addAll, test1 and test2:"+test1+","+test2);//666.888可以重复
        //test1.removeAll(test2);
        //System.out.println("test removeAll,test1 and test2:"+test1+","+test2);//所有666,888都移除了，只要有一个元素移除了，就返回true
        System.out.println("test containsAll, test1 and test2:"+test1.containsAll(test2)+","+test1+","+test2);//只有包含所有的元素才叫包含
        //retain,返回值表示A是否发生或改变
        System.out.println("test retainAll, test1 and test2:"+test1.retainAll(test2)+","+test1+","+test2);//test1只留下共有的，有交集就为true，没交集为false
        System.out.println("");
    }
    //集合的遍历，依次获取每一个元素

    public  void  testToArray(){
        Collection test=new ArrayList();
        test.add("Hello");
        test.add("World666");
        //集合转数组
        Object[] objs =test.toArray();
        for(int x=0;x<objs.length;x++){
            System.out.println("objs[x]:"+objs[x]);
            //System.out.println("objs[x] length:"+objs[x].length());是错误的，因为objs类型是object不是string，没有length方法
            System.out.println("objs[x] length is :"+objs[x].toString().length());
        }
    }
    //集合的遍历，依次获取每一个元素
@Test
    public  void  testIterator(){
        Collection test=new ArrayList();
        test.add("Hello");
        test.add("World666");
        //集合转数组
        Object[] objs =test.toArray();
        for(int x=0;x<objs.length;x++){
            System.out.println("objs[x]:"+objs[x]);
            //System.out.println("objs[x] length:"+objs[x].length());是错误的，因为objs类型是object不是string，没有length方法
            System.out.println("objs[x] length is :"+objs[x].toString().length());
        }
        //Iterator it=test.iterator();//实际返回的肯定是子类对象，这里是多态
//        System.out.println(it.next());//获取元素，并且移动到下一个元素
//        System.out.println(it.next());
        //System.out.println(it.next());此时会报错，no such element
        //因此应该有一个判断，有就获取
//        while(it.hasNext()){
//            String s = (String)it.next();
//            System.out.println(s);
//        }
        test.forEach(item->{
            System.out.println("item is"+(String)item);
        });
        //遍历list的三种方法
        //（1）while
        // (2)for
        // (3)for

    }

}
