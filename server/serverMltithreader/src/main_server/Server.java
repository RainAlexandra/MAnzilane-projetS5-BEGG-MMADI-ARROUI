package main_server;

public class Server {

	/**
	 * @param idGroupe
	 * @param idTicket
	 * @param titleOfTicket
	 * @return 
	 */
	public boolean pushTicket(String idGroupe, String idTicket, String titleOfTicket) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param idTicket
	 * @param contenue
	 * @return
	 */
	public boolean updateTicket(String idTicket, String contenue) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param id
	 * @param pwd
	 * @return
	 */
	public boolean userIsExist(String id, String pwd) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @param idClien
	 * @return
	 */
	public String getListOfGroup(String idClient) {
		//TODO
		return "liste de groupe";
	}
	
	/**
	 * @param idClient
	 * @return
	 */
	public String getListOfTicket(String idClient) {
		//TODO
		return "liste de tickets";
	}
	
	/**
	 * @param idTicket
	 * @return
	 */
	public String getListOfMsg(String idTicket) {
		//TODO
		return "list de msg";
	}

}
