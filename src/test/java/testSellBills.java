public class testSellBills {
    public static  void main(String args[]){
        testSellBillsThread sellBillsThread=new testSellBillsThread();
        Thread a=new Thread(sellBillsThread);
        Thread b=new Thread(sellBillsThread);
        Thread c=new Thread(sellBillsThread);
        a.setName("sellA");
        b.setName("sellB");
        c.setName("sellC");
        a.start();
        b.start();
        c.start();
    }

}
