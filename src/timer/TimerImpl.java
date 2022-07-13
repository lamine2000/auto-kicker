package timer;


import java.util.Date;
import java.util.TimerTask;

public class TimerImpl extends TimerTask {
    private final String name;

    public TimerImpl(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" "+"the task has executed successfully "+ new Date());
        if(this.name.equalsIgnoreCase("task1")){
            try {
                Thread.sleep( 600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
