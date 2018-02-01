package mypro;
import com.sun.java.swing.plaf.windows.WindowsBorders;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.View;

public class Frame1 extends JFrame implements WindowListener{
    Connection con;
    Frame1(Connection con){
        
        this.con=con;
        this.setTitle("Seating Arrangement");
        this.setLayout(null);
        JTextField ttotrec=new JTextField();
        JTextField tcourse=new JTextField();
        JLabel lcourse=new JLabel("Course Code");
        JPanel jp=new JPanel();
        
        //JLabel credits=new JLabel("Developed By Parth Agarwal : RA1511003010326");
        
        JScrollPane js=new JScrollPane(jp);
        JButton bpro=new JButton("PROCEED");
        JButton getRec=new JButton("Get");
        int totcourses=0;
         String sql="select distinct COURSE_CODE from master_mtech ";
        PreparedStatement pst;
        try {
            pst = con.prepareStatement(sql);
            try (ResultSet rs = pst.executeQuery()) {
                while(rs.next()){
                    totcourses++;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error : "+ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
       
        JCheckBox jcb[]=new JCheckBox[totcourses+1];
        lcourse.setBounds(20, 20, 90, 30);
        tcourse.setBounds(120, 20, 150, 30);
        js.setBounds(20,60,500,400);
        jp.setBounds(10, 10, 480, 380);
        bpro.setBounds(230,480,90,30);
        getRec.setBounds(290, 20, 90, 30);
        ttotrec.setBounds(400, 20, 40, 30);
        ttotrec.setEditable(false);
        
       // credits.setBounds(300, 520, 280, 40);
        
        jp.setLayout(new BoxLayout(jp, View.Y_AXIS));
        jp.setBorder(new WindowsBorders.DashedBorder(Color.BLUE));
        jp.setVisible(true);
        js.setVisible(false);
        bpro.setVisible(false);
        
        getRec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jp.revalidate();
                jp.repaint();
                jp.removeAll();
                try {
                    int i=1;
                    String sql1;
                    if (tcourse.getText()==null) {
                        sql1 = "select distinct COURSE_CODE from master_mtech order by COURSE_CODE";
                    } else {
                        sql1 = "select distinct COURSE_CODE from master_mtech where COURSE_CODE like \'"+tcourse.getText()+"%\' order by COURSE_CODE";
                    }
                    PreparedStatement pst1 = con.prepareStatement(sql1);
                    ResultSet rs = pst1.executeQuery();
                    jcb[0]=new JCheckBox("ALL");
                    jp.add(jcb[0]);
                    while(rs.next()){
                        jcb[i]=new JCheckBox(rs.getString(1));
                        jp.add(jcb[i]);
                        i++;
                    }
                    ttotrec.setText(Integer.toString(i-1));
                }catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error : "+ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
                js.setVisible(true);
                bpro.setVisible(true);
            }
        });
        
        bpro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selcount=0;
                List<String> det=new ArrayList<String>(); 
                if(jcb[0].isSelected()){
                    for(int i=1;i<=Integer.parseInt(ttotrec.getText());i++){
                        det.add(jcb[i].getActionCommand());
                    }
                }
                else{
                    for(int i=1;i<=Integer.parseInt(ttotrec.getText());i++){
                        if(jcb[i].isSelected()){
                            det.add(jcb[i].getActionCommand());
                            selcount++;
                        }
                    }
                }
                
                JLabel jlb=new JLabel("Please Wait,Operation In Progress");
                jp.removeAll();
                jp.repaint();
                jp.setLayout(null);
                jlb.setBounds(100,100, 200, 30);
                String path=(JOptionPane.showInputDialog(null, "Enter .xlsx FileName", "File Name", JOptionPane.INFORMATION_MESSAGE));
                jp.add(jlb);
                MyPro mp=new MyPro();
                int abc=Integer.parseInt(ttotrec.getText());
                Boolean flag;
                if(jcb[0].isSelected())
                    flag=mp.writeData(path,abc,det,con);
                else
                    flag=mp.writeData(path, selcount, det,con);
                if(flag){
                    JOptionPane.showMessageDialog(null, "Data Writen", "Success", JOptionPane.INFORMATION_MESSAGE);
                    int opt=JOptionPane.showConfirmDialog(null, "Open Excel File?", "File Launcher", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(opt==JOptionPane.YES_OPTION){
                    try {
                           Desktop.getDesktop().open(new File(path+".xlsx"));
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "Error : "+ex, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                dispose();
                
                new Frame1(con);
            }
        });
        
        this.add(lcourse);
      //  this.add(credits);
        this.add(tcourse);
        this.add(js);
        this.add(bpro);
        this.add(ttotrec);
        this.add(getRec);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
        this.setVisible(true);
        this.setSize(600,600);
        this.setLocation(600, 250);
        
    }
     
    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
         int op = JOptionPane.showConfirmDialog(null, "ARE YOU SURE YOU WANT TO QUIT?", "QUIT", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
