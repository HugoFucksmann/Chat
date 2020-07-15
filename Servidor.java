package Chat;
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Servidor  {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MarcoServidor mimarco=new MarcoServidor();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}	
}

class MarcoServidor extends JFrame implements Runnable{ //Runnable para Thread (hilos)
	
	public MarcoServidor(){
		
		setBounds(1200,300,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);
		
		setVisible(true);
		
		//CREAMOS HILO
		Thread mihilo=new Thread(this);
		mihilo.start();
		
		}
	
	@Override
	public void run() {
		
		try {
		
			//construye un socket DE SERVIDOR (param puerto por el cual recibira datos)
			ServerSocket servidor=new ServerSocket(9999);
			
			//---PREPARANDO PARA RECIBIR PAQUETE (NICK,IP,MSJ)--------------------------------
			String nick,ip,mensaje;
			
			ArrayList <String> listaIp=new ArrayList<String>();
			
			PaqueteEnvio paquete_recibido;
			//---------------------------------------------------------------------------------
			
			while(true) { //bucle infinito para que siempre este a la escucha
			
				//a la escucha del puerto(9999) y acepta las conexiones del exterior
				Socket misocket=servidor.accept();
		
				
				//---INTERPRETANDO Y MOSTRANDO EL PAQUETE---------------------------------
				ObjectInputStream paquete_datos=new ObjectInputStream(misocket.getInputStream()); //flujo de datos de entrada (para recoger los datos que vienen de misocket)
				
				paquete_recibido=(PaqueteEnvio) paquete_datos.readObject(); // almacena el texto recibido en el flujo de datos
				
				nick=paquete_recibido.getNick();
				ip=paquete_recibido.getIp();
				mensaje=paquete_recibido.getMensaje();
				
				if(!mensaje.equals(" online")) {
				
					areatexto.append("\n" + nick + ": " + mensaje + " para:" + ip); //escribe el String en areatexto
					
					
					//---SOCKET DEL SERVER AL CLIENTE DESINO---------------------------------
					Socket enviaDestinatario=new Socket(ip,9090);
					
					ObjectOutputStream paqueteReenvio=new ObjectOutputStream(enviaDestinatario.getOutputStream());
					
					paqueteReenvio.writeObject(paquete_recibido);
					
					paqueteReenvio.close();
					enviaDestinatario.close();
					
					misocket.close();	
				
				}else{
					
					//---DETECTA IP ONLINE-------------------------------------------------------
					InetAddress localizacion=misocket.getInetAddress(); // obtiene la ip
					
					String IpRemota=localizacion.getHostAddress(); // almacena la ip del cliente que se conecta (en String)
					
					System.out.println("Online "+IpRemota);
					
					listaIp.add(IpRemota);
					
					paquete_recibido.setIps(listaIp); //agregamos el array al paquete
					
					for(String z:listaIp) { // recorre el array de ip para mostrarlo (manda un paquete a cliente)
						
						System.out.println("Array: "+z);
						
						Socket enviaDestinatario=new Socket(z,9090);
						
						ObjectOutputStream paqueteReenvio=new ObjectOutputStream(enviaDestinatario.getOutputStream());
						
						paqueteReenvio.writeObject(paquete_recibido);
						
						paqueteReenvio.close();
						enviaDestinatario.close();
						
						misocket.close();
					}
					
					
					
				}
				
			}
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	private	JTextArea areatexto;
}
