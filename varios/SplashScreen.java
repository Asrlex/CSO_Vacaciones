package varios.misc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;

import gui.GUI;
 
public class SplashScreen extends JWindow implements Runnable{
     
	private static final long serialVersionUID = 1L;
	private int duration = 0;
	private JWindow current = null;
     
    public SplashScreen(int d) {
        duration = d;
        current = this;
    }
     
    public void run() {
        
       JPanel content = (JPanel)getContentPane();
       content.setBackground(Color.white);
       content.setBorder(BorderFactory.createMatteBorder(4,  4,  4,  4, Color.orange));
       
       int width = 400;
       int height = 400;
       Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
       int x = (screen.width-width)/2;
       int y = (screen.height-height)/2;
       setBounds(x,y,width,height);
       
       JLabel label = new JLabel(new ImageIcon(this.getClass().getResource("/splash.gif")));
       
       JLabel copyrt = new JLabel("CARGANDO...", JLabel.CENTER);
       copyrt.setFont(new Font("Bankia", Font.BOLD, 40));
       content.add(label, BorderLayout.CENTER);
       content.add(copyrt, BorderLayout.NORTH);
       
       JProgressBar barra = new JProgressBar(0, 100);
       barra.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black));
       barra.setBackground(Color.white);
       barra.setForeground(Color.ORANGE);
       barra.setStringPainted(true);
       content.add(barra, BorderLayout.SOUTH);
        
       setVisible(true);
       
       Timer t = new Timer(duration, new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				current.dispose();
			}
       });
       t.start();
       fill(barra);
   }
    
    public void fill(JProgressBar barra){
    	AtomicInteger i = new AtomicInteger(0);
    	Runnable task = () -> { 
    		i.incrementAndGet();
    		barra.setValue(i.get());
    		barra.setString(i.get() + "%");
		};
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(task, 0, duration/100, TimeUnit.MILLISECONDS);
    }
     
    public static void main(String[] args) {
    	FlatLightLaf.install();
    	new Thread(new SplashScreen(4000)).start();
    	new Thread(new GUI()).start();
    }
}