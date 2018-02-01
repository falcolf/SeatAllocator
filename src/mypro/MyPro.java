package mypro;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class MyPro {
    public static void main(String[] args) {
        Connection con=null;
        try{
              Class.forName("oracle.jdbc.driver.OracleDriver");
              con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","anony11");
            }catch(ClassNotFoundException | SQLException se){
                JOptionPane.showMessageDialog(null, "Error : "+se, "Error", JOptionPane.ERROR_MESSAGE);
            }
        new Frame1(con);
    }
    
    XSSFWorkbook wb;
    XSSFSheet sheet;
    PreparedStatement pst;
    
    void preWrite(){
        wb=new XSSFWorkbook();
        sheet=wb.createSheet("new");
        Row row=sheet.createRow(0);
        Cell cell0=row.createCell(0);
        cell0.setCellValue("YEAR");
        Cell cell1=row.createCell(1);
        cell1.setCellValue("COURSE_CODE");
        Cell cell2=row.createCell(2);
        cell2.setCellValue("FACULTY_ID");
        Cell cell[]=new Cell[40];
        for(int i=0;i<30;i++){
            cell[i]=row.createCell(i+3);
            cell[i].setCellValue("REGISTRATION NUMBER");
        }
    }

    Boolean writeData(String fname, int tr, List<String> det,Connection con) {
        Cell cell[]=new Cell[40];
        preWrite();
        Row r;
        Cell c;
        int rown=1;
        String sql;
        try{
            int index=0;
            while(index<tr){
                String cons=det.get(index);
                sql="select distinct FACULTY_ID___NAME from master_mtech where COURSE_CODE=\'"+cons+"\'";
                pst=con.prepareStatement(sql);
                try (ResultSet rrs = pst.executeQuery()) {
                    while(rrs.next()){
                        String cons1=rrs.getString(1);
                        if(cons1 == null)
                            sql="select * from master_mtech where COURSE_CODE=\'"+cons+"\' and FACULTY_ID___NAME is NULL";
                        else
                            sql="select * from master_mtech where COURSE_CODE=\'"+cons+"\' and FACULTY_ID___NAME=\'"+cons1+"\'";
                        pst=con.prepareStatement(sql);
                        int rcount;
                        try (ResultSet rrss = pst.executeQuery()) {
                            r=sheet.createRow(rown);
                            cell[0]=r.createCell(0);
                            cell[1]=r.createCell(1);
                            cell[2]=r.createCell(2);
                            cell[1].setCellValue(cons);
                            cell[2].setCellValue(cons1);
                            int k=3;
                            rcount = 0;
                            while(rrss.next()){
                                cell[0].setCellValue(rrss.getString(7));
                                cell[k]=r.createCell(k);
                                cell[k].setCellValue(rrss.getString(2));
                                k++;
                                rcount++;
                                if(k>32){
                                    rown++;
                                    r=sheet.createRow(rown);
                                    cell[0]=r.createCell(0);
                                    cell[1]=r.createCell(1);
                                    cell[2]=r.createCell(2);
                                    cell[1].setCellValue(cons);
                                    cell[2].setCellValue(cons1);
                                    cell[0].setCellValue(rrss.getString(7));
                                    k=3;
                                }
                            }
                        }
                        rown++;
                        if(rcount%30==0)
                            rown--;
                    }
                }
                index++;
                }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error : "+ex, "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(ex);
            return false;
        }
        for(int i=0;i<35;i++)
            sheet.autoSizeColumn(i);
      try(FileOutputStream fout=new FileOutputStream(fname+".xlsx")){
            wb.write(fout);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error : "+ex, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
