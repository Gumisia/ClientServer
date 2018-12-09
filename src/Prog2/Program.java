package Prog2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Scanner;

public class Program {
	
	public static void main(String[] args) {
		startServer();
		startClient();
	}
	
	public static void startClient() {
        (new Thread() {
			@Override
			public void run() {
				try {
					Scanner sc=new Scanner(System.in);

					while (true) {
						
						System.out.println("Wybierz komende:");
						String command=sc.nextLine();
						
						if(command.equals("add") || command.equals("show") || command.startsWith("file")) {
							Socket s = new Socket("localhost", 60016);
							BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
							
							out.write(command);
							out.newLine();
							out.flush();
							
							if(command.startsWith("file")) {
								
								InputStream in = s.getInputStream();
								//ByteArrayOutputStream baos = new ByteArrayOutputStream();
								int byteToBeRead = -1;
								
								
								final File folder = new File("./Pobrane");
								File newFile=new File(folder.getAbsolutePath()+File.separator+new Date().getTime());
								FileOutputStream fs = new FileOutputStream(newFile);
								while((byteToBeRead = in.read())!=-1){
									//System.out.println(byteToBeRead);
									fs.write(byteToBeRead);
								}
								
								fs.flush();
								fs.close();
							}
							
							//s.close();
							
						}else if(command.equals("exit")) {
							break;
						}
						
						Thread.sleep(200);
					}

					sc.close();
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
        }).start();
    }

    public static void startServer() {
        (new Thread() {
            @Override
            public void run() {
                ServerSocket ss;
                try {
                    ss = new ServerSocket(60016);
                    int counter=0;
                    
                    while(true) {
	                    Socket s = ss.accept();
	
	                    BufferedReader in = new BufferedReader(
	                            new InputStreamReader(s.getInputStream()));
	                    String line = null;
	                    
	                    while (!s.isClosed() && (line = in.readLine()) != null) {
	                    	if(line.equals("add")) {
	                    		counter++;
	                    		System.out.println("SERVER: "+line+" counter="+counter);
	                    	}else if(line.equals("show")) {
	                    		System.out.println("SERVER: "+line+" counter="+counter);
	                    	}else if(line.startsWith("file")) {

	                    		//file 0 to pierwszy plik

	                    		int fileNumber=Integer.parseInt(line.split(" ")[1]);
	                    		//in.close();
	                    		sendFileThroughSocket(s, fileNumber);
	                    	}
	                    }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public static void sendFileThroughSocket(Socket s, int fileNumber) {
    	try {
	    	final File folder = new File("./Dane");
	    	File fileToSend=folder.listFiles()[fileNumber];
	        
	    	InputStream in = Files.newInputStream(fileToSend.toPath());
	    	OutputStream out = s.getOutputStream();
			
	    	int count;
	    	byte[] buffer = new byte[8192]; //potega dwojki
	    	while ((count = in.read(buffer)) > 0) //wczytuje plik i wypluwa do socecie
	    	{
	    	  out.write(buffer, 0, count); //2 parametry ostatnie od do ile pobieram
				//[8192][8192]...[450]
	    	}
	    	
	    	out.close();
    	}catch(Exception exc) {
    		System.out.println(exc);
    	}
    }

}
