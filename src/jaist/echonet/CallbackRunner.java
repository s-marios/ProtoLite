package jaist.echonet;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the logic of executing event listeners for asynchronous
 * answer processing
 * 
 * @author Sioutis Marios
 */
class CallbackRunner implements Runnable {

    private final List<Map.Entry<EchoEventListener, EchonetAnswer>> tocall = new ArrayList<Entry<EchoEventListener, EchonetAnswer>>();

    public void start() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void postJob(EchoEventListener listener, EchonetAnswer answer) {
        synchronized (tocall) {
            tocall.add(new AbstractMap.SimpleEntry(listener, answer));
            tocall.notify();
        }
    }

    @Override
    public void run() {
        Map.Entry<EchoEventListener, EchonetAnswer> entry = null;
        while (true) {
            synchronized (tocall) {
                while (tocall.isEmpty()) {
                    try {
                        tocall.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CallbackRunner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                entry = tocall.remove(0);
            }
            Thread thread = new Thread(new DoTask(entry.getKey(), entry.getValue()));
            thread.start();
        }
    }
    
    class DoTask implements Runnable{

        private final EchoEventListener listener;
        private final EchonetAnswer answer;
        
        DoTask(EchoEventListener listener, EchonetAnswer answer){
            this.listener = listener;
            this.answer = answer;
        }
        @Override
        public void run() {
            listener.processAnswer(answer);
        }    
    }
}
