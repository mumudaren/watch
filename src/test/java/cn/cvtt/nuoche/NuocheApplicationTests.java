package cn.cvtt.nuoche;

import cn.cvtt.nuoche.facade.IBusinessCallRecordInterface;
import cn.cvtt.nuoche.facade.IProductInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.Name;
import javax.xml.bind.SchemaOutputResolver;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public  class NuocheApplicationTests {
    @Test
public   void testMain(){
    int num=10;
    System.out.println(test(num));
}


public static int test(int b){
    try
    {
        b=b+10;
        return b;
    }catch(RuntimeException e){

    }
    finally {
        b+=10;
        return b;
    }
}
	@Autowired
	IBusinessCallRecordInterface callRecordInterface;

//	@Test
//	public void contextLoads() {
//	  /*  String str=	productInterface.findRegexProduct("nuoche","8");
//		System.out.println(str);*/
//
//		System.out.println(callRecordInterface.findHeard("nuoche","7788944444444"));
//	}
		@Test
	public void testDataOut() {


	}

}
