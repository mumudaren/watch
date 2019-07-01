import org.junit.Test;

public class test {
    public static  void main(String args[]){
        int num=10;
        System.out.println(test2(num));
        System.out.println("a+b="+test.test3(9,34));
    }

    public static int test2(int b){
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
    public static int test3(int a,int b){
        try
        {
            return a+b;
        }catch(Exception e){
            System.out.println("catch");
        }
        finally {
            System.out.println("finally");
            return 0;
        }
    }

}
