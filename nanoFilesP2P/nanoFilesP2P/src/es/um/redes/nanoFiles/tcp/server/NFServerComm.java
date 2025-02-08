package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServerComm {
	private static int MAX_LENGTH = 40000;
	public static void serveFilesToClient(Socket clientSocket) {
		/*
		 * TODO: Crear dis/dos a partir del socket
		*/
		
		DataOutputStream dos = null;
		DataInputStream dis = null;
		
		PeerMessage FromClient = null;
		PeerMessage ToClient = null;
		try {
			dos = new DataOutputStream(clientSocket.getOutputStream());
			dis = new DataInputStream(clientSocket.getInputStream());
			FromClient = PeerMessage.readMessageFromInputStream(dis);
			byte opcode = FromClient.getOpcode();
			switch (opcode) {
			case PeerMessageOps.OPCODE_DOWNLOADFROM_CODE: {
				//System.out.println("* Client connected from " + clientSocket + " *");
				String ClientHash = FromClient.getCodeHash();
				FileInfo[] files = FileInfo.lookupHashSubstring(NanoFiles.db.getFiles(), ClientHash);
				if(files.length == 0) {
					ToClient = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOADFROMFAIL_CODE, PeerMessageOps.FAIL_NOT_EXISTS);
					ToClient.writeMessageToOutputStream(dos);
				}else if (files.length > 1) {
					ToClient = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOADFROMFAIL_CODE, PeerMessageOps.FAIL_AMBIGUOUS);
					ToClient.writeMessageToOutputStream(dos);
				}else if(files.length == 1){
					String targethash = files[0].fileHash;
					int targethashLen = targethash.length();
					ToClient = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOADFROMOK_CODE, targethashLen, targethash);
					ToClient.writeMessageToOutputStream(dos);
					String FilePath = files[0].filePath;
					File file = new File(FilePath);
					long contentLen = file.length();
					byte[] content = new byte[(int) contentLen];
					FileInputStream fis = new FileInputStream(file);
					fis.read(content);
					fis.close();
					int index = 0;
					while (index < file.length()) {
						byte[] messageData = Arrays.copyOfRange(content, index, Math.min(index + MAX_LENGTH, (int) file.length()));
						index = index + MAX_LENGTH;
						if(index > file.length()) {
							index = (int)file.length();
						}
						ToClient = new PeerMessage(PeerMessageOps.OPCODE_CONTENT, (long) index, file.length(), messageData.length, messageData);
						ToClient.writeMessageToOutputStream(dos);
					}
					
					System.out.println("Client " + clientSocket.getInetAddress()+ "/" + clientSocket.getLocalPort() 
										+ " has downloaded the file: "+ FilePath);
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + opcode);
			}
			//System.out.println("* Client disconnected from " + clientSocket + " *");
			clientSocket.close();
			dos.close();
			dis.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		/*
		int receive;
		try {
			dos = new DataOutputStream(clientSocket.getOutputStream());
			dis = new DataInputStream(clientSocket.getInputStream());
			receive = dis.readInt();
			System.out.println("Receive: " + receive);
			int send = receive + 1;
			System.out.println("Sending: " +  + send);
			dos.writeInt(send);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		/*
		 * TODO: Mientras el cliente esté conectado, leer mensajes de socket,
		 * convertirlo a un objeto PeerMessage y luego actuar en función del tipo de
		 * mensaje recibido, enviando los correspondientes mensajes de respuesta.
		 */
		/*
		 * TODO: Para servir un fichero, hay que localizarlo a partir de su hash (o
		 * subcadena) en nuestra base de datos de ficheros compartidos. Los ficheros
		 * compartidos se pueden obtener con NanoFiles.db.getFiles(). El método
		 * FileInfo.lookupHashSubstring es útil para buscar coincidencias de una
		 * subcadena del hash. El método NanoFiles.db.lookupFilePath(targethash)
		 * devuelve la ruta al fichero a partir de su hash completo.
		 */


	}
	
}
