package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;




public class PeerMessage {




	private byte opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	private int nameServerLen = (int) PeerMessageOps.OPCODE_INVALID_CODE;
	private String nameServer = null;
	private int hashLen = (int) PeerMessageOps.OPCODE_INVALID_CODE;
	private String codeHash = null;
	private int nameLocalFileLen = (int) PeerMessageOps.OPCODE_INVALID_CODE;
	private String nameLocalFile = null;
	private long indice = (long) PeerMessageOps.OPCODE_INVALID_CODE;
	private long dataLen = (long) PeerMessageOps.OPCODE_INVALID_CODE;
	
	private byte failCodeDF = PeerMessageOps.OPCODE_INVALID_CODE;
	private long contentFileLen = (long) PeerMessageOps.OPCODE_INVALID_CODE;
	private byte[] contentFile = null;
	

	/*
	 * TODO: Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */

	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	/*
	 * DownloadFromOK: BYTE OPCODE | INT HASHLEN | STRING CODEHASH
	 * DownloadFrom: BYTE OPCODE | INT HASHLEN | STRING CODEHASH
	 */
	public PeerMessage(byte opcode, int hashLen, String hash) {
		this.opcode = opcode;
		this.hashLen = hashLen;
		this.codeHash = hash;
	}
	/*
	 * CONTENT: BYTE OPCODE | LONG INDICE | LONG CONTENIDOLEN | LONG DATALEN | BYTE[] CONTENT
	 */
	public PeerMessage(byte opcode, long indice, long contentFileLen, long dataLen, byte[] contentFile) {
		this.opcode = opcode;
		this.contentFile = contentFile;
		this.contentFileLen = contentFileLen;
		this.dataLen = dataLen;
		this.indice = indice;
	}
	/*
	 * DOWNLOADFROM_FAIL: BYTE OPCODE | BYTE FAILCODE 
	 */
	public PeerMessage(byte opcode, byte failCodeDF) {
		this.opcode = opcode;
		this.failCodeDF = failCodeDF;
	}

	/*
	 * TODO: Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */

    public byte getOpcode() {
        return opcode;
    }

    public int getNameServerLen() {
    	if(nameServerLen != (int) PeerMessageOps.OPCODE_INVALID_CODE) {
    		return nameServerLen;
    	}else {
    		return (int) PeerMessageOps.OPCODE_INVALID_CODE;
    	}
    }

    public String getNameServer() {
    	if(nameServer != null) {
    		return nameServer;
    	}else {
    		return null;
    	}
    }

    public int getHashLen() {
    	if(hashLen != (int) PeerMessageOps.OPCODE_INVALID_CODE) {
    		return hashLen;
    	}else {
    		return (int) PeerMessageOps.OPCODE_INVALID_CODE;
    	}
    }

    public String getCodeHash() {
    	if(codeHash != null) {
    		return codeHash;
    	}else {
    		return null;
    	}
    }

    public int getNameLocalFileLen() {
    	if(nameLocalFileLen != (int) PeerMessageOps.OPCODE_INVALID_CODE) {
    		return nameLocalFileLen;
    	}else {
    		return (int) PeerMessageOps.OPCODE_INVALID_CODE;
    	}
    }

    public String getNameLocalFile() {
    	if(nameLocalFile != null) {
    		return nameLocalFile;
    	}else {
    		return null;
    	}
    }

    public byte getFailCodeDF() {
    	if(failCodeDF !=  PeerMessageOps.OPCODE_INVALID_CODE) {
    		return failCodeDF;
    	}else {
    		return PeerMessageOps.OPCODE_INVALID_CODE;
    	}
    }

    public long getContentFileLen() {
    	if(contentFileLen != (long) PeerMessageOps.OPCODE_INVALID_CODE) {
    		return contentFileLen;
    	}else {
    		return (long) PeerMessageOps.OPCODE_INVALID_CODE;
    	}
    }

    public byte[] getContentFile() {
    	if(contentFile != null) {
    		return contentFile;
    	}else {
    		return null;
    	}
    }
    
    public long getIndice() {
    	if(indice != (long) PeerMessageOps.OPCODE_INVALID_CODE) {
    		return indice;
    	}else {
    		return (long) PeerMessageOps.OPCODE_INVALID_CODE;
    	}
    }
    

	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: En función del tipo de mensaje, leer del socket a través del "dis" el
		 * resto de campos para ir extrayendo con los valores y establecer los atributos
		 * del un objeto DirMessage que contendrá toda la información del mensaje, y que
		 * será devuelto como resultado. NOTA: Usar dis.readFully para leer un array de
		 * bytes, dis.readInt para leer un entero, etc.
		 */
		PeerMessage message = null;
		byte opcode = dis.readByte();
		switch (opcode) {
		case PeerMessageOps.OPCODE_DOWNLOADFROM_CODE:
			//public PeerMessage(byte opcode, int hashLen, String hash) {
			int hashLen1 = dis.readInt();
			byte[] hash1 = new byte[hashLen1];
			dis.readFully(hash1);
			message = new PeerMessage(opcode, hashLen1, new String(hash1));
			break;
		case PeerMessageOps.OPCODE_CONTENT:
			//public PeerMessage(byte opcode, long indice, long contentFileLen, long dataLen, byte[] contentFile) {
			long indice = dis.readLong();
			long contenidoFileLen = dis.readLong();
			long dataLen = dis.readLong();
			byte[] data = new byte[(int)dataLen];
			dis.readFully(data);
			message = new PeerMessage(opcode, indice, contenidoFileLen, dataLen, data);
			break;
		case PeerMessageOps.OPCODE_DOWNLOADFROMOK_CODE:
			//public PeerMessage(byte opcode, int hashLen, String hash) {
			int hashLen = dis.readInt();
			byte[] hash = new byte[hashLen];
			dis.readFully(hash);
			message = new PeerMessage(opcode, hashLen, new String(hash));
			break;
		case PeerMessageOps.OPCODE_DOWNLOADFROMFAIL_CODE:
			byte FailCode = dis.readByte();
			message = new PeerMessage(opcode, FailCode);
			break;
		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO: Escribir los bytes en los que se codifica el mensaje en el socket a
		 * través del "dos", teniendo en cuenta opcode del mensaje del que se trata y
		 * los campos relevantes en cada caso. NOTA: Usar dos.write para leer un array
		 * de bytes, dos.writeInt para escribir un entero, etc.
		 */
		/// OPCODE = 1 /// hashlenght = 4 /// codehash = "abcd"
		dos.writeByte(opcode);
		switch (opcode) {
		case PeerMessageOps.OPCODE_DOWNLOADFROMOK_CODE:
			//public PeerMessage(byte opcode, int hashLen, String hash) {
			assert(hashLen > 0 && codeHash.length() == hashLen);
			dos.writeInt(hashLen);
			byte[] hash = codeHash.getBytes();
			dos.write(hash);
			break;
		case PeerMessageOps.OPCODE_DOWNLOADFROM_CODE:
			//public PeerMessage(byte opcode, int nameServerLen, String nameServer , int hashLen, String codeHash, int nameLocalFileLen ,String nameLocalFile) {
			assert(hashLen > 0 && codeHash.length() == hashLen);
			dos.writeInt(hashLen);
			byte[] hash1 = codeHash.getBytes();
			dos.write(hash1);
			break;
		case PeerMessageOps.OPCODE_CONTENT:
			//public PeerMessage(byte opcode, long indice, long contentFileLen, long dataLen, byte[] contentFile) {
			//cambiar orden de read tb
			dos.writeLong(indice);
			dos.writeLong(contentFileLen);
			dos.writeLong(dataLen);
			dos.write(contentFile);
			break;	
		case PeerMessageOps.OPCODE_DOWNLOADFROMFAIL_CODE:
			dos.writeByte(failCodeDF);
			break;
 		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}





}
