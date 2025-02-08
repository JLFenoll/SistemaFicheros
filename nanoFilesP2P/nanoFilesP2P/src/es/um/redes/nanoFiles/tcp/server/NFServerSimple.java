package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class NFServerSimple {

	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private static final String STOP_SERVER_COMMAND = "fgstop";
	private static final int PORT = 10000;
	private ServerSocket serverSocket = null;

	public NFServerSimple() throws IOException {
		/*
		 * TODO: Crear una direción de socket a partir del puerto especificado
		 */
		InetSocketAddress ServerSocketAdress = new InetSocketAddress(PORT);
		/*
		 * TODO: Crear un socket servidor y ligarlo a la dirección de socket anterior
		 */	
		
		serverSocket = new ServerSocket();
		serverSocket.bind(ServerSocketAdress);
		serverSocket.setReuseAddress(true);
		serverSocket.setSoTimeout(SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS);
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación a menos que se implemente la funcionalidad de
	 * detectar el comando STOP_SERVER_COMMAND (opcional)
	 * 
	 */
	public void run() {
		/*
		 * TODO: Comprobar que el socket servidor está creado y ligado
		 */
		
		if (serverSocket == null) {
			System.err.println("**ERROR**: Failed to run. Server sockect not binded to any port");
			return;
		}else {
			System.out.println("NFServerSimple server running. Listening on "+ serverSocket.getLocalSocketAddress());
		}
		
		/*
		 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
		
		boolean stopServer = false; //Para la implementacion de stopserver
		System.out.println("Enter " + STOP_SERVER_COMMAND + " to stop: ");
		while (!stopServer) {
			/*
			 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
			 * soliciten descargar ficheros
			 */
			try {
				/*
				 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
				 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
				 * hay que pasarle el objeto Socket devuelto por accept (retorna un nuevo socket
				 * para hablar directamente con el nuevo cliente conectado)
				 */
				Socket socket = serverSocket.accept();
				NFServerComm.serveFilesToClient(socket);
			} 
			catch (SocketTimeoutException e) {
				/*
				 * TODO: (Para poder detener el servidor y volver a aceptar comandos).
				 * Establecer un temporizador en el ServerSocket antes de ligarlo, para
				 * comprobar mediante standardInput.ready()) periódicamente si se ha tecleado el
				 * comando "fgstop", en cuyo caso se cierra el socket servidor y se sale del
				 * bucle
				 */
				BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));
				try {
					if(standardInput.ready()) {
						String command = standardInput.readLine();
						if(command.equals(STOP_SERVER_COMMAND)) {
							stopServer = true;
							serverSocket.close();
						}
					}
				} catch (Exception e1) {
					// Esperamos siguiente timeout
				}
			}
			catch (IOException e) {
				System.out.println("* Problem stopping the server *");
			}
		}
		System.out.println("* Server stopped *");
	}
}
