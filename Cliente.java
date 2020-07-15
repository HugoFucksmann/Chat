package Chat;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class Cliente {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		MarcoCliente mimarco=new MarcoCliente();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
}


class MarcoCliente extends JFrame{
	
	public MarcoCliente(){
		
		setBounds(600,300,320,350);
		
		setTitle("ColoChat :)");
				
		LaminaMarcoCliente milamina=new LaminaMarcoCliente();
		
		add(milamina);
		
		setVisible(true);
		
		addWindowListener(new EnvioOnline()); // al abrir la ventana se ejecuta metodo EnvioOnline (para detectar ip)
		}	
}

// para enviar la ip al servidor cuando se abre la ventana (ver DETECTA IP ONLINE en servidor)
class EnvioOnline extends WindowAdapter{
	
	public void windowOpened(WindowEvent e) {
		
		try {
			
			Socket misocket=new Socket(InetAddress.getLocalHost(),9999);
			
			PaqueteEnvio datos=new PaqueteEnvio();
			
			datos.setMensaje(" online");
			
			ObjectOutputStream paquete_datos=new ObjectOutputStream(misocket.getOutputStream()); //flujo de datos
			
			paquete_datos.writeObject(datos);
			
			misocket.close();
			
		}catch(Exception e2) {
			System.out.println("Errorrrr");
		}
	}
}


class LaminaMarcoCliente extends JPanel implements Runnable{
	
	public LaminaMarcoCliente(){
		
		String nick_usuario=JOptionPane.showInputDialog("Nick:");
		
		JLabel n_nick=new JLabel("Nick:");
		add(n_nick);
		
		nick=new JLabel();
		nick.setText(nick_usuario);
		add(nick);
	
		JLabel texto=new JLabel("Online:");
		add(texto);
		
		ip=new JComboBox();
		/*ip.addItem("Opcion1");
		ip.addItem("Opcion2");
		ip.addItem("Opcion3");*/
		
		//ip.addItem("192.168.0.12");
		//ip.addItem("192.168.0.48");
		
		add(ip);
		
		campochat=new JTextArea(12,25);
		add(campochat);
	
		campo1=new JTextField(20);
	
		add(campo1);		
	
		miboton=new JButton("Enviar");
		EnviaTexto mievento=new EnviaTexto();
		miboton.addActionListener(mievento);
		
		add(miboton);	
		
		Thread mihilo=new Thread(this);
		mihilo.start();
	}
	
	//*1 clase interna para evento
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			campochat.append("\n" + campo1.getText()); //para que aparezca lo que uno escribe
			
		//PUENTE(SOCKET) Y FLUJO DE DATOS(DATAOUTPUTSTREAM) --------------------------------
			try {
				//1 creamos socket o puente (ip+puerto) IP LOCAL: APUNTA AL SERVIDOR
				Socket misocket=new Socket(InetAddress.getLocalHost(),9999);
				
				
				//2 flujo de datos (param socket que es por donde va a pasar los datos)
				/* DataOutputStream flujo_salida=new DataOutputStream(misocket.getOutputStream());
			
				//3 en el flujo de datos va a viajar lo que hay en campo1(String)
				flujo_salida.writeUTF(campo1.getText());
				
				//4 cerrar el flujo(siempre)
				flujo_salida.close(); */
				
				
				//---PREPARANDO EL PAQUETE CON LOS DATOS A ENVIAR------------------------------
				PaqueteEnvio datos=new PaqueteEnvio();
				
				datos.setNick(nick.getText()); //almacena lo que hay en el textField nick en datos
				datos.setIp(ip.getSelectedItem().toString());
				datos.setMensaje(campo1.getText());
				//----------------------------------------------
				//---FLUJO DE DATOS----------------------------------------------------------------------------
				ObjectOutputStream paquete_datos=new ObjectOutputStream(misocket.getOutputStream());
				
				paquete_datos.writeObject(datos);
				
				misocket.close();
				//--------------------------------------------------------------------
				
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		//-------------------------------------------------------------------------------
		}
		
	}
	
	@Override
	public void run() {
		
		try {
			
			//---PREPARANDO PARA RECIBIR PAQUETE------------------------------------
			ServerSocket servidor_cliente=new ServerSocket(9090);
			Socket cliente;
			
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				
				cliente=servidor_cliente.accept();
				
				ObjectInputStream flujoentrada=new ObjectInputStream(cliente.getInputStream());
				
				paqueteRecibido=(PaqueteEnvio) flujoentrada.readObject();
				
				if(!paqueteRecibido.getMensaje().equals(" online")) { // en caso de que ya este conectado
					
					campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
					
				}else {
					//Mostrar arrayList con IPs-----------------------------------------------------------------------
					//campochat.append("\n" + paqueteRecibido.getIps());
					
					ArrayList<String> IpsMenu=new ArrayList<String>();
					
					IpsMenu=paqueteRecibido.getIps(); //almacena las ips en el array
					
					ip.removeAllItems(); // borra las ips (luego vuelve a cargar la lista actualizada), evita duplicidad
					
					for(String z: IpsMenu) { // agrega las ips en el desplegable
						
						ip.addItem(z);
					}
				}
				
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	
	private JTextField campo1;
	private JComboBox ip;
	private JLabel nick;
	private JTextArea campochat;
	private JButton miboton;	
}

class PaqueteEnvio implements Serializable{ // Obj con los datos a enviar (el nick, la ip y el msj) (serializable para que convierta en bytes el obj para poder ser enviado
	
	private String nick, ip, mensaje;
	private ArrayList<String> Ips;

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
}


