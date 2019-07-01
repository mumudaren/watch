package cn.cvtt.nuoche.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


@RestController
public class studyListController extends  BaseController {
    @RequestMapping("/add")
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
    @RequestMapping("/iterator")
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
        Iterator it=test.iterator();//实际返回的肯定是子类对象，这里是多态
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
    //测试string
    String a="hello"+"world";

//day15 work
//        1:集合的由来?
//        2:集合和数组的区别?
//        3:Collection集合的功能概述?
//        4:Collection集合存储字符串并遍历?(迭代器)
//        5:Collection集合存储自定义对象并遍历?(迭代器)
//        6:List集合的特有功能?
//        7:List集合存储字符串并遍历?(迭代器和普通for)
//        8:List集合存储自定义对象并遍历?(迭代器和普通for)
//        9:并发修改异常出现的原因?解决方案?
//        10:常见的数据结构的特点?
//        栈：
//        队列：
//        数组：
//        链表：
//        11:List集合的子类特点
//        ArrayList:
//        Vector:
//        LinkedList:
//        12:List的三个儿子你准备使用谁?请说明理由。
    //集合的遍历，依次获取每一个元素
@RequestMapping("/coll")
    public  void  testColl(){
        Collection test=new ArrayList();
        test.add("Hi");
        //集合转数组
        Object[] objs =test.toArray();
        for(int x=0;x<objs.length;x++){
            System.out.println("objs[x]:"+objs[x]);
            //System.out.println("objs[x] length:"+objs[x].length());是错误的，因为objs类型是object不是string，没有length方法
            System.out.println("objs[x] length is :"+objs[x].toString().length());
        }
    }


//day16work
//1:List的子类特点
//2:ArrayList练习
//	A:ArrayList存储字符串并遍历
//	B:ArrayList存储自定义对象并遍历
//3:LinkedList练习
//	A:LinkedList存储字符串并遍历
//	B:LinkedList存储自定义对象并遍历
//4:泛型是什么?格式是?好处是?
//5:增强for的格式是?好处是?弊端是?
//6:静态导入的格式是?注意事项是?
//7:可变参数的作用是?格式是?注意事项是?
//8:用下列集合完成代码
//	Collection
//	List
//	ArrayList
//	Vector
//	LinkedList
//
//	存储字符串并遍历
//	存储自定义对象并遍历
//
//	要求加入泛型，并有增强for遍历。
//	代码我会检查的。




//day18 homework
// 1:集合(自己补齐)
//	Collection
//		List
//		Set
//	Map
//
//2:到底使用那种集合(自己补齐)
//
//3:ArrayList,LinkedList,HashSet,HashMap(掌握)
//	存储字符串和自定义对象数据并遍历
//
//4:集合的嵌套遍历(理解)





}