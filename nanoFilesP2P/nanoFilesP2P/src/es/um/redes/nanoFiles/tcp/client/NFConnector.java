package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	private DataInputStream dis;
	private DataOutputStream dos;


	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO Se crea el socket a partir de la dirección del servidor (IP, puerto). La
		 * creación exitosa del socket significa que la conexión TCP ha sido
		 * establecida.
		 */
		
		socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
		
		/*
		 * TODO Se crean los DataInputStream/DataOutputStream a partir de los streams de
		 * entrada/salida del socket creado. Se usarán para enviar (dos) y recibir (dis)
		 * datos del servidor.
		 */

		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());

	}

	/**
	 * Método para descargar un fichero a través del socket mediante el que estamos
	 * conectados con un peer servidor.
	 * 
	 * @param targetFileHashSubstr Subcadena del hash del fichero a descargar
	 * @param file                 El objeto File que referencia el nuevo fichero
	 *                             creado en el cual se escribirán los datos
	 *                             descargados del servidor
	 * @return Verdadero si la descarga se completa con éxito, falso en caso
	 *         contrario.
	 * @throws IOException Si se produce algún error al leer/escribir del socket.
	 */
	public boolean downloadFile(String targetFileHashSubstr, File file) throws IOException {
		boolean downloaded = false;
		PeerMessage messageRequest = null;
		PeerMessage messageResponse = null;
		/*
		int send = 45;
		int receive = 0;
		
		dos.writeInt(send);
		System.out.println("sending: " + send);
		receive = dis.readInt();
		System.err.println("Receive: " + receive);
		Scanner scanner = new Scanner(System.in);
        
        System.out.println("Presiona Enter para terminar...");
        scanner.nextLine(); // Espera a que el usuario pulse Enter
        
        System.out.println("Programa terminado.");
        */
		/*
		 * TODO: Construir objetos PeerMessage que modelen mensajes con los valores
		 * adecuados en sus campos (atributos), según el protocolo diseñado, y enviarlos
		 * al servidor a través del "dos" del socket mediante el método
		 * writeMessageToOutputStream.
		*/ 
		
		messageRequest = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOADFROM_CODE, 
											targetFileHashSubstr.length(), targetFileHashSubstr);
		messageRequest.writeMessageToOutputStream(dos);
		System.out.println("Requesting a file download...");
		/*
		 * TODO: Recibir mensajes del servidor a través del "dis" del socket usando
		 * PeerMessage.readMessageFromInputStream, y actuar en función del tipo de
		 * mensaje recibido, extrayendo los valores necesarios de los atributos del
		 * objeto (valores de los campos del mensaje).
		 */
		/*
		 * TODO: Para escribir datos de un fichero recibidos en un mensaje, se puede
		 * crear un FileOutputStream a partir del parámetro "file" para escribir cada
		 * fragmento recibido (array de bytes) en el fichero mediante el método "write".
		 * Cerrar el FileOutputStream una vez se han escrito todos los fragmentos.
		 */
					
		
		
		messageResponse = PeerMessage.readMessageFromInputStream( dis);
		byte opcode = messageResponse.getOpcode();
		switch (opcode) {
		case (PeerMessageOps.OPCODE_DOWNLOADFROMOK_CODE): {
			String totalHash = messageResponse.getCodeHash();
			messageResponse = PeerMessage.readMessageFromInputStream(dis);
			long totalDataLen = messageResponse.getContentFileLen();
			byte[] totalData = new byte[(int)totalDataLen];
			int cont = 0;
			while(messageResponse.getIndice() < messageResponse.getContentFileLen()) {
				byte[] data = messageResponse.getContentFile();
				System.arraycopy(data, 0, totalData, cont, data.length);
				cont = cont + data.length;
				messageResponse = PeerMessage.readMessageFromInputStream(dis);
			}
			if (messageResponse.getIndice() == messageResponse.getContentFileLen()) {
				byte[] datos = messageResponse.getContentFile();
				System.arraycopy(datos, 0, totalData, cont, datos.length);
			}
			File f = new File(file.getName());
			if (!f.exists()) {
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(totalData);
				fos.close();
			}
			System.out.println("File downloaded to: " + f.getAbsolutePath());
			
			if(totalHash.equals(FileDigest.computeFileChecksumString(file.getPath()))){
				System.out.println("* Successful download");
				downloaded = true;
			}else {
				System.out.println("* File do not match");
				downloaded = false;
			}
			break;
		}
		case (PeerMessageOps.OPCODE_DOWNLOADFROMFAIL_CODE): {
			if(messageResponse.getFailCodeDF() == PeerMessageOps.FAIL_AMBIGUOUS) {
				System.out.println("* The provided code hash is ambiguous, try again");
			}else if(messageResponse.getFailCodeDF() == PeerMessageOps.FAIL_NOT_EXISTS) {
				System.out.println("* The provided code hash does not match any shared files");
			}
			downloaded = false;
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + opcode);
		}
		socket.close();
		
		/*
		 * TODO: Finalmente, comprobar la integridad del fichero creado para comprobar
		 * que es idéntico al original, calculando el hash a partir de su contenido con
		 * FileDigest.computeFileChecksumString y comparándolo con el hash completo del
		 * fichero solicitado. Para ello, es necesario obtener del servidor el hash
		 * completo del fichero descargado, ya que quizás únicamente obtuvimos una
		 * subcadena del mismo como parámetro.
		 */
		
		
		
		/*
		 * NOTA: Hay que tener en cuenta que puede que la subcadena del hash pasada como
		 * parámetro no identifique unívocamente ningún fichero disponible en el
		 * servidor (porque no concuerde o porque haya más de un fichero coincidente con
		 * dicha subcadena)
		 */
		
		
		return downloaded;
	}





	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
