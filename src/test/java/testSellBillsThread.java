public class testSellBillsThread implements Runnable{
    int bills=100;
    @Override
    public void run() {
        while(bills>0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.print(Thread.currentThread().getName()+",");
            System.out.println(Thread.currentThread().getName()+","+"after sell bills number is:" + bills--);
        }
    }
}
