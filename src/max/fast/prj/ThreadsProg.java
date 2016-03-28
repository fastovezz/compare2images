package max.fast.prj;

/**
 * Created by maks on 8/7/14.
 */
public class ThreadsProg {
    public static void main(String[] args) throws InterruptedException{
        Runnable coordinator = new CoordinatorRunnable();
        Thread threadCoordinator = new Thread(coordinator);
        threadCoordinator.start();
//        threadCoordinator.join();
    }

    public static class PrintRunable implements Runnable {
        private String whatToPrint;
        private int msToSleep;

        PrintRunable(String whatToPrint, int msToSleep) {
            this.whatToPrint = whatToPrint;
            this.msToSleep = msToSleep;
        }

        @Override
        public void run() {
            for (int i = 0; i < 11; i++) {
                try {
                    Thread.sleep(msToSleep);
                    System.out.println(whatToPrint);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class CoordinatorRunnable implements Runnable {
        @Override
        public void run() {
            for (int k = 0; k < 11; k++) {
                // A + B
                Runnable printerA = new PrintRunable("A   -", 100);
                Thread threadA = new Thread(printerA);
                threadA.start();
                Runnable printerB = new PrintRunable("-   B", 100);
                Thread threadB = new Thread(printerB);
                threadB.start();

                Runnable printerC = new PrintRunable("C", 100);
                Thread threadC = new Thread(printerC);
                threadC.start();
                try {
                    threadA.join();
                    threadB.join();
                    threadC.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // C
//                System.out.println("-----");
//                Runnable printerC = new PrintRunable("C", 100);
//                printerC.run();
//                System.out.println("-----");

            }
        }
    }
}