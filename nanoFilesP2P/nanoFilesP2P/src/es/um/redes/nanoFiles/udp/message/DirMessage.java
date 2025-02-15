package es.um.redes.nanoFiles.udp.message;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	private static final String FIELDNAME_NICKNAME = "nickname";
	private static final String FIELDNAME_SERVERNAME = "servername";
	private static final String FIELDNAME_SESSIONKEY = "sessionkey";
	private static final String FIELDNAME_USERLIST = "userlist";
	private static final String FIELDNAME_REGISTERSERVERPORT = "serverport";
	private static final String FIELDNAME_SERVERIP = "serverip";
	
	/*
	 * TODO: Definir de manera simbólica los nombres de todos los campos que pueden
	 * aparecer en los mensajes de este protocolo (formato campo:valor)
	 */	


	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/*
	 * TODO: Crear un atributo correspondiente a cada uno de los campos de los
	 * diferentes mensajes de este protocolo.
	 */
	private String nickname = null;
	private int sessionKey = es.um.redes.nanoFiles.udp.client.DirectoryConnector.INVALID_SESSION_KEY;
	private String userList = null;
	private int serverPort = -1;
	private String serverName = null;
	private String IP = null;

	public DirMessage(String op) {
		operation = op;
	}

	/*
	 * TODO: Crear diferentes constructores adecuados para construir mensajes de
	 * diferentes tipos con sus correspondientes argumentos (campos del mensaje)
	 */

	public String getOperation() {
		return operation;
	}
	
	public void setSessionKey(int sk) {
		sessionKey = sk;
	}
	
	public int getSessionKey() {

		return sessionKey;
	}
	
	public void setNickname(String nick) {

		nickname = nick;
	}

	public String getNickname() {
		return nickname;
	}
	
	public void setUserList(String lista) {
		userList = lista;
	}
	public String getUserList() {
		return userList;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getServername() {
		return serverName;
	}
	
	public void setServerName(String servername) {
		serverName = servername;
	}
	
	public String getServerIP() {
		return IP;
	}
	
	public void setServerIP(String ip) {
		IP = ip;
	}
	
	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: Usar un bucle para parsear el mensaje línea a línea, extrayendo para
		 * cada línea el nombre del campo y el valor, usando el delimitador DELIMITER, y
		 * guardarlo en variables locales.
		 */

		//System.out.println("DirMessage read from socket:");
		//System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		// Local variables to save data during parsing
		DirMessage m = null;



		for (String line : lines) {
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
			case FIELDNAME_OPERATION: {
				assert (m == null);
				m = new DirMessage(value); 
				break;
			}
			
			case FIELDNAME_NICKNAME: {
				assert (m == null);
				m.setNickname(value); 
				break;
			}
			
			case FIELDNAME_SESSIONKEY: {
				assert (m == null);
				int res = Integer.parseInt(value);
				m.setSessionKey(res);
				break;
			}
			
			case FIELDNAME_USERLIST: {
				assert (m == null);
				m.setUserList(value);
				break;
			}
			
			case FIELDNAME_REGISTERSERVERPORT: {
				assert(m == null);
				int port = Integer.parseInt(value);
				m.setServerPort(port);
				break;
			}
			case FIELDNAME_SERVERNAME: {
				assert(m == null);
				m.setServerName(value);
				break;
			}	
			case FIELDNAME_SERVERIP: {
				assert(m == null);
				m.setServerIP(value);
				break;
			}

			default:
				System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
				System.err.println("Message was:\n" + message);
				System.exit(-1);
			}
		}

		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
		/*
		 * TODO: En función del tipo de mensaje, crear una cadena con el tipo y
		 * concatenar el resto de campos necesarios usando los valores de los atributos
		 * del objeto.
		 */
		if(nickname != null) {
			sb.append(FIELDNAME_NICKNAME + DELIMITER + getNickname() + END_LINE);
		}
		if(sessionKey != es.um.redes.nanoFiles.udp.client.DirectoryConnector.INVALID_SESSION_KEY) {
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + getSessionKey() + END_LINE);
		}
		if(userList != null) {
			sb.append(FIELDNAME_USERLIST + DELIMITER + getUserList().toString() + END_LINE);
		}
		if(serverPort != es.um.redes.nanoFiles.udp.client.DirectoryConnector.INVALID_SERVERPORT) {
			sb.append(FIELDNAME_REGISTERSERVERPORT + DELIMITER + getServerPort() + END_LINE);
		}
		if(serverName != null) {
			sb.append(FIELDNAME_SERVERNAME + DELIMITER + getServername() + END_LINE);
		}
		if(IP != null) {
			sb.append(FIELDNAME_SERVERIP + DELIMITER + getServerIP() + END_LINE);
		}
		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
		
	}
}
