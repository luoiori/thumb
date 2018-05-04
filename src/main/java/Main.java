import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main  extends JFrame {

    private static String dirPath = getPath();

    public static JTextArea sourcefile1 = new JTextArea();

    private static double quality = 0.9;

    public static void main(String[] args){
        new Main();
    }

    public Main(){
        JPanel panel = new JPanel();
        JButton buttonC1 = new JButton("选择原图片文件夹");
        buttonC1.setBounds(4, 2, 140, 30);

        JLabel j1 = new JLabel("请输入压缩质量");
        j1.setBounds(4, 42, 140, 30);

        JTextField jt = new JTextField("0.9");
        jt.setBounds(100, 42, 70, 30);

        JLabel j2 = new JLabel("(0.1~1之间，数字越大，图片越清晰，空间越大)");
        j2.setBounds(170, 42, 280, 30);

        JButton buttonStart = new JButton("开始压缩");
        buttonStart.setBounds(4, 80, 140, 30);

        panel.add(buttonC1);
        panel.add(j1);
        panel.add(j2);
        panel.add(jt);
        panel.add(buttonStart);

        sourcefile1.setBounds(150, 5, 350, 30);
        sourcefile1.setText(dirPath);
        sourcefile1.setLineWrap(true);
        sourcefile1.setOpaque(false);
        panel.add(sourcefile1);

        JLabel remark = new JLabel("备注:");
        remark.setBounds(10, 112, 30, 30);

        JTextArea info = new JTextArea("导出文件位置在原文件所在位置名为\"压缩\"的文件夹里面。---by Iori");
        info.setLineWrap(true);
        info.setOpaque(false);
        info.setBounds(50, 120, 400, 40);
        panel.add(remark);
        panel.add(info);

        // listener
        buttonC1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(1);
                jfc.setDialogTitle("选择原图片文件夹位置");
                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    dirPath = jfc.getSelectedFile().getPath();
                    Main.sourcefile1.setText(dirPath);
                }
            }
        });

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t = jt.getText();
                if(t!=null && t!=""){
                    try{
                        quality=Double.parseDouble(t);
                    }catch (Exception ex){
                        quality = 0.9;
                    }
                }
                if (dirPath != "" && dirPath != null) {
                    oper(dirPath);
                    int option=JOptionPane.showConfirmDialog(Main.this,"压缩完成","提示",JOptionPane.CLOSED_OPTION);
                    try {
                        Runtime.getRuntime().exec("explorer /e,/root,"+dirPath);
                    } catch (IOException e1) {
                    }
                }else{
                    JOptionPane.showConfirmDialog(Main.this,"请选择原图片位置","提示",JOptionPane.CLOSED_OPTION);
                }
            }
        });

        this.setTitle("图片压缩（for Eve）");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 200);
        double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation((int) (width - this.getWidth()) / 2, (int) (height - this.getHeight()) / 2);
        // layout
        panel.setLayout(null);
        getContentPane().add(panel);
        try {
            this.setIconImage(ImageIO.read(this.getClass().getResource("dating.png")));
        } catch (IOException e) {
        }
        this.setVisible(true);

    }

    public static void oper(String basePath){
        File directory = new File(basePath);
        List<File> files = getAllFiles(directory);
        System.out.println(basePath);
        int countSuccess=0;
        int countFail=0;
        List<String> errors = new ArrayList<String>();
        for(File file:files){
            if(file.getName().endsWith("jar"))continue;
            String result = thumbImg(file,basePath);
            if("1".equalsIgnoreCase(result)){
                countSuccess++;
            }else {
                countFail++;
                errors.add(result);
            }
        }
        System.out.println("压缩成功"+countSuccess+"个");
        System.out.println("压缩失败"+countFail+"个");
        System.out.println("压缩失败列表：");
        int index=1;
        for(String error:errors){
            System.out.println(index+":"+error);
            index++;
        }
    }

    private static String thumbImg(File file, String basePath) {
        String name = file.getAbsolutePath();
        String path = basePath+"\\压缩";
        name = path+name.substring(basePath.length(),name.length());
        String tmp = "";
        for(int i=0;i<name.split("\\\\").length-1;i++){
            tmp += name.split("\\\\")[i];
            if(!new File(tmp).exists()){
                new File(tmp).mkdir();
            }
            tmp+="\\";
        }
        try {
            Thumbnails.of(file).scale(1).outputQuality(quality).outputFormat("jpg").toFile(name);
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
        return "1";
    }

    public static List<File> getAllFiles(File root){
        List<File> files = new ArrayList<File>();
        for(File file : root.listFiles()){
            if("压缩".equalsIgnoreCase(file.getName()))continue;
            if(file.isDirectory()){
                files.addAll(getAllFiles(file));
            }else {
                files.add(file);
            }
        }
        return files;
    }

    public static String getPath() {
        /*URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
            // 截取路径中的jar包名
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);

        // /If this abstract pathname is already absolute, then the pathname
        // string is simply returned as if by the getPath method. If this
        // abstract pathname is the empty abstract pathname then the pathname
        // string of the current user directory, which is named by the system
        // property user.dir, is returned.
        filePath = file.getAbsolutePath();//得到windows下的正确路径
        return filePath;*/
        String name =  new File(".").getAbsolutePath();
        return name.substring(0,name.length()-2);
    }
}
