public class test2{
    public int add(int a,int b){
        try {
            return a+b;
        }
        catch (Exception e) {
            System.out.println("catch语句块");
        }
        finally{
            System.out.println("finally语句块");
        }
        return 0;
    }
    public static void main(String argv[]){
        //test2 test =new test2();
        //System.out.println("和是："+test.add(9, 34));
       try{
          System.out.println("try");
          throw new Exception();
       }catch (Exception e){
           System.out.println("catch");
       }
        System.out.println("after try catch");
    }

}