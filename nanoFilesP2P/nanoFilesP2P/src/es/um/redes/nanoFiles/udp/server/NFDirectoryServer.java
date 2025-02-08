package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFDirectoryServer {	
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/**
	 * Estructura para guardar los nicks de usuarios registrados, y clave de sesión
	 * <key,valor>
	 */
	private HashMap<String, Integer> nicks;
	/**
	 * Estructura para guardar las claves de sesión y sus nicks de usuario asociados
	 * 
	 */
	private HashMap<Integer, String> sessionKeys;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */
	//map de la sessionKey y el puerto del servidor <ssessionKey, serverPort>
	private HashMap<Integer, InetSocketAddress> Servers;

	


	/**
	 * Generador de claves de sesión aleatorias (sessionKeys)
	 */
	Random random = new Random();
	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín UDP) Inicializar el atributo socket: Crear un socket UDP
		 * ligado al puerto especificado por el argumento directoryPort en la máquina
		 * local,
		 */
		
		socket = new DatagramSocket(DIRECTORY_PORT);
		
		/*
		 * TODO: (Boletín UDP) Inicializar el resto de atributos de esta clase
		 * (estructuras de datos que mantiene el servidor: nicks, sessionKeys, etc.)
		 */

		nicks = new HashMap<String, Integer>();
		sessionKeys = new  HashMap<Integer, String>();
		Servers = new HashMap<Integer, InetSocketAddress>();

		if (NanoFiles.testMode) {
			if (socket == null || nicks == null || sessionKeys == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public void run() throws IOException {
		byte[] receptionBuffer = null;
		InetSocketAddress clientAddr = null;
		int dataLength = -1;
		DirMessage res = null;
		/*
		 * TODO: (Boletín UDP) Crear un búfer para recibir datagramas y un datagrama
		 * asociado al búfer
		 */
		receptionBuffer = new byte[DirMessage.PACKET_MAX_SIZE];
		DatagramPacket packetFromClient = new DatagramPacket(receptionBuffer, receptionBuffer.length);



		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio

			// TODO: (Boletín UDP) Recibimos a través del socket un datagrama
			
			socket.receive(packetFromClient);

			// TODO: (Boletín UDP) Establecemos dataLength con longitud del datagrama
			// recibido
			
			dataLength = packetFromClient.getLength();

			// TODO: (Boletín UDP) Establecemos 'clientAddr' con la dirección del cliente,
			// obtenida del
			// datagrama recibido

			clientAddr = (InetSocketAddress) packetFromClient.getSocketAddress();


			if (NanoFiles.testMode) {
				if (receptionBuffer == null || clientAddr == null || dataLength < 0) {
					System.err.println("NFDirectoryServer.run: code not yet fully functional.\n"
							+ "Check that all TODOs have been correctly addressed!");
					System.exit(-1);
				}
			}
			System.out.println("Directory received datagram from " + clientAddr + " of size " + dataLength + " bytes");

			// Analizamos la solicitud y la procesamos
			if (dataLength > 0) {
				String messageFromClient = null;
				/*
				 * TODO: (Boletín UDP) Construir una cadena a partir de los datos recibidos en
				 * el buffer de recepción
				 */

				messageFromClient = new String(packetFromClient.getData(), 0, packetFromClient.getLength());

				if (NanoFiles.testMode) { // En modo de prueba (mensajes en "crudo", boletín UDP)
					System.out.println("[testMode] Contents interpreted as " + dataLength + "-byte String: \""
							+ messageFromClient + "\"");
					/*
					 * TODO: (Boletín UDP) Comprobar que se ha recibido un datagrama con la cadena
					 * "login" y en ese caso enviar como respuesta un mensaje al cliente con la
					 * cadena "loginok". Si el mensaje recibido no es "login", se informa del error
					 * y no se envía ninguna respuesta.
					 */
					double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}
					
					if(messageFromClient.equals("login")) {
						String messageToClient = new String("loginok");
						byte[] dataToClient = messageToClient.getBytes();
						DatagramPacket packetToClient = new DatagramPacket(dataToClient,dataToClient.length, clientAddr);
						socket.send(packetToClient);
					}else {
						System.err.println("Unexpected message received");
						System.exit(-1);
					}


				} else { // Servidor funcionando en modo producción (mensajes bien formados)

					// Vemos si el mensaje debe ser ignorado por la probabilidad de descarte
					double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}

					//System.out.println("Contents interpreted as " + dataLength + "-byte \nString: \""
					//		+ messageFromClient + "\"");
					
					/*if(messageFromClient.startsWith("login&")) {
						String[] parteString = messageFromClient.split("\\&");
						String username = parteString[1].toString();
						if(!nicks.containsKey(username)) {
							int sesionKey = random.nextInt(10000);
							nicks.put(username, sesionKey);
							sessionKeys.put(sesionKey,username);
							String messageToClient = new String("loginok&"+sesionKey);
							byte[] dataToClient = messageToClient.getBytes();
							DatagramPacket packetToClient = new DatagramPacket(dataToClient,dataToClient.length, clientAddr);
							socket.send(packetToClient);
						}else {
							String messageToClient = new String("login_failed:-1");
							byte[] dataToClient = messageToClient.getBytes();
							DatagramPacket packetToClient = new DatagramPacket(dataToClient,dataToClient.length, clientAddr);
							socket.send(packetToClient);
						}
						
					}else {
						System.err.println("Unexpected message received");
						System.exit(-1);
					}*/
					
					/*
					 * TODO: Construir String partir de los datos recibidos en el datagrama. A
					 * continuación, imprimir por pantalla dicha cadena a modo de depuración.
					 * Después, usar la cadena para construir un objeto DirMessage que contenga en
					 * sus atributos los valores del mensaje (fromString).
					 */
					DirMessage msg = DirMessage.fromString(messageFromClient);
					res =  buildResponseFromRequest(msg, clientAddr);
					String messageToClient = res.toString();
					byte[] dataToClient = messageToClient.getBytes();
					DatagramPacket packetToClient = new DatagramPacket(dataToClient,dataToClient.length, clientAddr);
					socket.send(packetToClient);
					
					
					/*
					 * TODO: Llamar a buildResponseFromRequest para construir, a partir del objeto
					 * DirMessage con los valores del mensaje de petición recibido, un nuevo objeto
					 * DirMessage con el mensaje de respuesta a enviar. Los atributos del objeto
					 * DirMessage de respuesta deben haber sido establecidos con los valores
					 * adecuados para los diferentes campos del mensaje (operation, etc.)
					 */
					
					
					/*
					 * TODO: Convertir en string el objeto DirMessage con el mensaje de respuesta a
					 * enviar, extraer los bytes en que se codifica el string (getBytes), y
					 * finalmente enviarlos en un datagrama
					 */



				}
			} else {
				System.err.println("Directory ignores EMPTY datagram from " + clientAddr);
			}

		}
	}

	private DirMessage buildResponseFromRequest(DirMessage msg, InetSocketAddress clientAddr) {
		/*
		 * TODO: Construir un DirMessage con la respuesta en función del tipo de mensaje
		 * recibido, leyendo/modificando según sea necesario los atributos de esta clase
		 * (el "estado" guardado en el directorio: nicks, sessionKeys, servers,
		 * files...)
		 */
		String operation = msg.getOperation();
		DirMessage response = null;
		System.out.println("Receive " + operation + " request from " + clientAddr.toString());
		switch (operation) {
		case DirMessageOps.OPERATION_LOGIN: {
			String username = msg.getNickname();
			/*
			 * TODO: Comprobamos si tenemos dicho usuario registrado (atributo "nicks"). Si
			 * no está, generamos su sessionKey (número aleatorio entre 0 y 1000) y añadimos
			 * el nick y su sessionKey asociada. NOTA: Puedes usar random.nextInt(10000)
			 * para generar la session key
			 */
			if(!nicks.containsKey(username)) {
				int sesionKey = random.nextInt(10000);
				nicks.put(username, sesionKey);
				sessionKeys.put(sesionKey,username);
				response = new DirMessage(DirMessageOps.OPERATION_LOGIN_OK);
				response.setSessionKey(sesionKey);
				System.out.println("* Client " + clientAddr.toString() + " succesfully registered by " + username);
			}else {
				response = new DirMessage(DirMessageOps.OPERATION_LOGIN_FAIL);
				System.out.println("* Client" + clientAddr.toString() + " failed to register by " + username);
			}
			
			/*
			 * TODO: Construimos un mensaje de respuesta que indique el éxito/fracaso del
			 * login y contenga la sessionKey en caso de éxito, y lo devolvemos como
			 * resultado del método.
			 */
			
			
				
  			/*
			 * TODO: Imprimimos por pantalla el resultado de procesar la petición recibida
			 * (éxito o fracaso) con los datos relevantes, a modo de depuración en el
			 * servidor
			 */
			break;
		}
		case DirMessageOps.OPERATION_USERLIST: {
			int sessionKey = msg.getSessionKey();
			String lista = "";
			if(sessionKeys.containsKey(sessionKey)) {
				response = new DirMessage(DirMessageOps.OPERATION_USERLIST_OK);
				for (String valor : sessionKeys.values()) {
					lista += valor+ ", ";
				}
				if (!lista.isEmpty()) {
		            lista = lista.substring(0, lista.length() - 2);
		        }
				String lista2 = "] Servers: [";
				Set<Integer> matchKeys = new HashSet<>();
		        for (Map.Entry<Integer, InetSocketAddress> entry : Servers.entrySet()) {
		            if (sessionKeys.containsKey(entry.getKey())) {
		                matchKeys.add(entry.getKey());
		            }
		        }
		        for (int key : matchKeys) {
		        	if(sessionKeys.containsKey(key)) {
		        		lista2 += sessionKeys.get(key) + ", ";
		        	}
		        }
		        if (!lista2.isEmpty()) {
		            lista2 = lista2.substring(0, lista2.length() - 2);
		        }
		        if (lista2.isEmpty()) {
		        	response.setUserList(lista);
		        }else {
		        	lista += lista2;
					response.setUserList(lista);
		        }
				System.out.println("* Client " + clientAddr.toString() + " obtained userlist");
			}else {
				response = new DirMessage(DirMessageOps.OPERATION_USERLIST_FAIL);
				System.out.println("* Client " + clientAddr.toString() + " failed to obtain userlist");	
			}
			break;
		}
		case DirMessageOps.OPERATION_LOGOUT: {
			int sessionKey = msg.getSessionKey();
			String username = sessionKeys.get(sessionKey);
			if(sessionKeys.containsKey(sessionKey)) {
				nicks.remove(username);
				sessionKeys.remove(sessionKey);
				response = new DirMessage(DirMessageOps.OPERATION_LOGOUT_OK);
				System.out.println("* Client " + clientAddr.toString() + " succesfully deregistered by " + username);
			}else {
				response = new DirMessage(DirMessageOps.OPERATION_LOGOUT_FAIL);
				System.out.println("* Client " + clientAddr.toString() + " failed to deregister by " + username);
			}
			break;
		}
		case DirMessageOps.OPERATION_REGISTERSERVERPORT: { 
			int sessionKey = msg.getSessionKey();
			int serverPort = msg.getServerPort();
			String username = sessionKeys.get(sessionKey);
			InetAddress IP = clientAddr.getAddress();
			if(sessionKeys.containsKey(sessionKey)) {
				InetSocketAddress add = new InetSocketAddress(IP, serverPort);
				Servers.put(sessionKey, add);
				response = new DirMessage(DirMessageOps.OPERATION_REGISTERSERVERPORT_OK);
				System.out.println("* Client " + clientAddr.toString() + "(" + username + ")" + " succesfully registered as server");
			}else {
				response = new DirMessage(DirMessageOps.OPERATION_REGISTERSERVERPORT_FAIL);
				System.out.println("* Client " + clientAddr.toString() + "(" + username + ")" + " failed registered as server");
			}
			break;
		}
		case DirMessageOps.OPERATION_UNREGISTERSERVERPORT: { 
			int sessionKey = msg.getSessionKey();
			String username = sessionKeys.get(sessionKey);
			if(sessionKeys.containsKey(sessionKey)) {
				Servers.remove(sessionKey);
				response = new DirMessage(DirMessageOps.OPERATION_UNREGISTERSERVERPORT_OK);
				System.out.println("* Client " + clientAddr.toString() + "(" + username + ")" + " succesfully deregistered as server");
			}else {
				response = new DirMessage(DirMessageOps.OPERATION_UNREGISTERSERVERPORT_FAIL);
				System.out.println("* Client " + clientAddr.toString() + "(" + username + ")" + " failed registered as server");
			}
			break;
		}
		case DirMessageOps.OPERATION_LOOKUPSERVERADD: { 
			String serverName = msg.getServername();
			int sessionKey_user = msg.getSessionKey();
			int sessionKey_server = nicks.get(serverName);
			if(sessionKeys.containsKey(sessionKey_user) && Servers.containsKey(sessionKey_server)) {
				response = new DirMessage(DirMessageOps.OPERATION_LOOKUPSERVERADD_OK);
				InetSocketAddress serverAdd = Servers.get(sessionKey_server);
				int port = serverAdd.getPort();
				response.setServerPort(port);
				String IP = serverAdd.getAddress().getHostAddress();
				System.out.println("IP: " + IP);
				response.setServerIP(IP);
				System.out.println("* Client " + clientAddr.toString() + "(" + sessionKeys.get(sessionKey_user)+ ")" + " succesfully obtained server address ("
													+ serverName + ")");
			}else {
				response = new DirMessage(DirMessageOps.OPERATION_LOOKUPSERVERADD_FAIL);
				System.out.println("* Client " + clientAddr.toString() + "(" + sessionKeys.get(sessionKey_user)+ ")" + " failed obtained server address ("
													+ serverName + ")");
			}
			break;
		}
		default:
			System.out.println("Unexpected message operation: \"" + operation + "\"");
		}
		System.out.println("Sent " + operation + " response to " + clientAddr.toString());
		return response;
		
	}
}
