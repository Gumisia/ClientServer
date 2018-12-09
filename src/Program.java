import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Scanner;

//public ftp skj pgago

public class Program {

    public static String[][] Nodes = {
            {"localhost", "60001"},
            {"localhost", "60002"}
    };

    public static void main(String[] args) throws Exception {
        //loadConfiguration();
        //startClient();
        //startServer();
        File file=new File("./Dane");
        printMd5List(file);
    }

    public static void startClient(){

//        powinna dzialas na osobnym watku


        new Thread() {

//                @override
                public void run(){

                    Scanner scanner=new Scanner(System.in);

                    while (true){
                        System.out.println("Wybierz komendę");
                        String command=scanner.next();
                        System.out.println("Wybierz numer klienta");
                        int numeKlienta=scanner.nextInt();

                        if(command.equals("add")||command.equals("show")){

                            String ip=Nodes[numeKlienta][0];
                            int numerPortu = Integer.parseInt(Nodes[numeKlienta][1]);

                            try{
                                Socket s = new Socket(ip, numerPortu);

                                OutputStream os = s.getOutputStream();

                                OutputStreamWriter osWriter = new OutputStreamWriter(os);
                                //mozna strina przesłać dlatego opakowujemy

                                BufferedWriter bufferedWriter = new BufferedWriter(osWriter);

                                bufferedWriter.write(command); //wysyla komende
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                s.close();// jak zamnkniemy to juz nie otworzymy

                            }catch (Exception exc){
                                System.out.println(exc.toString());
                            }

                        } else {
                            System.out.println("Niepoprawna komenda");
                        }


                    }

                }
            }.start();
    }

    public static void startServer(){
        new Thread() {
            public void run() {

                try {
                    ServerSocket ss = new ServerSocket(60001);

                    int counter = 1;

                    while (true) {
                        Socket s = ss.accept();
                        InputStream is = s.getInputStream();
                        InputStreamReader isReader = new InputStreamReader(is);
                        BufferedReader bufferedReader = new BufferedReader(isReader);

                        String line = null;
                        while ((line=bufferedReader.readLine())!=null){
                            if(line.equals("add")){
                                counter++;
                            }else if(line.equals("show")){
                                System.out.println(counter);
                            }else {
                                System.out.println("Niepoprawna komenda= "+line);
                            }
                        }


                    }


                }catch (Exception exc){
                    System.out.println(exc.toString());
                }



            }
        }.start();
    }
    public static void printMd5List(File folder) throws Exception {

        for(File fileEntry : folder.listFiles()) {
            if(fileEntry.isDirectory()) {
                printMd5List(fileEntry);
            }else {
                System.out.print(fileEntry.getName());

                MessageDigest md=MessageDigest.getInstance("MD5");

                InputStream is= Files.newInputStream(fileEntry.toPath());
                DigestInputStream dis=new DigestInputStream(is, md);
                while(dis.read()!=-1); //!!!

                byte[] digest=md.digest();

                StringBuffer sb=new StringBuffer();
                for(byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                System.out.println(" "+sb.toString());
            }

        }

    }

}
