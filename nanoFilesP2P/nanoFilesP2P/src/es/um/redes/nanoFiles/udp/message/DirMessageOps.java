package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: Añadir aquí todas las constantes que definen los diferentes tipos de
	 * mensajes del protocolo de comunicación con el directorio.
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	
	public static final String OPERATION_LOGIN = "login";
	public static final String OPERATION_LOGIN_OK = "loginOk";
	public static final String OPERATION_LOGIN_FAIL = "loginFail";
	
	public static final String OPERATION_LOGOUT = "logout";
	public static final String OPERATION_LOGOUT_OK = "logoutOk";
	public static final String OPERATION_LOGOUT_FAIL = "logoutFail";
	
	public static final String OPERATION_USERLIST = "getUserList";
	public static final String OPERATION_USERLIST_OK = "getUserListOk";
	public static final String OPERATION_USERLIST_FAIL = "getUserListFail";

	public static final String OPERATION_REGISTERSERVERPORT = "resgisterServer";
	public static final String OPERATION_REGISTERSERVERPORT_OK = "resgisterServerOk";
	public static final String OPERATION_REGISTERSERVERPORT_FAIL = "resgisterServerFail";
	
	public static final String OPERATION_UNREGISTERSERVERPORT = "deresgisterServer";
	public static final String OPERATION_UNREGISTERSERVERPORT_OK = "deresgisterServerOk";
	public static final String OPERATION_UNREGISTERSERVERPORT_FAIL = "deresgisterServerFail";
	
	public static final String OPERATION_LOOKUPSERVERADD = "lookupserveraddr";
	public static final String OPERATION_LOOKUPSERVERADD_OK = "lookupserveraddrOk";
	public static final String OPERATION_LOOKUPSERVERADD_FAIL = "lookupserveraddrFail";
	
}
