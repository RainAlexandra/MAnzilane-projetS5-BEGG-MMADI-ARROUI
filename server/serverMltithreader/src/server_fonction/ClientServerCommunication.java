package server_fonction;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.Date;

public class ClientServerCommunication implements Runnable {
	private Msg msg;
	private Socket sock;
	private String idClient = null;
	private PrintWriter writer = null;
	private boolean isConected = false;
	private BufferedInputStream reader = null;
	private ClientsServerSocketInit server = null;

	public ClientServerCommunication(Socket sock) {
		this.sock = sock;
		try {
			writer = new PrintWriter(sock.getOutputStream());
			reader = new BufferedInputStream(sock.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	// Le traitement lancé dans un thread séparé
	public void run() {
		System.err.println("Lancement du traitement de la connexion cliente");

		boolean closeConnexion = false;
		// tant que la connexion est active, on traite les demandes
		while (!sock.isClosed()) {

			try {
				
				// writer = new PrintWriter(sock.getOutputStream());
				// reader = new BufferedInputStream(sock.getInputStream());
				String toSend = "";
				msg = new Msg(read()); // on recuperer le msg
				InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

				// On affiche quelques infos, pour le débuggage
				String debug = "";
				debug = "Thread : " + Thread.currentThread().getName() + ". ";
				debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() + ".";
				debug += " Sur le port : " + remote.getPort() + ".\n";
				debug += "\t -> Commande reçue : " + msg.getTypeOfmsg() + "\n";
				System.err.println("\n" + debug);
				
				
				if (!isConected) { //on demande l'authentification avat toute communication
					if (msg.getTypeOfmsg() == TypeOfRequest.AUTHENTIFICATION) {
						if (authentification(msg)) {
							server.updateInfos(idClient); //on met à jour les infos du client 
						}else {
							System.out.println("je ne suis pas connecter et le type de msg n'est pas authent");
							writer.write("ERROR AUTHENTIFICATION"); //la verification des id a échoué 
						}
					}else if (msg.getTypeOfmsg() == TypeOfRequest.CLOSE_CONN) {
						closeConnexion = true;
						System.out.println("je ne suis pas connecter et le type de msg n'est pas authent");
						writer.write("ERROR AUTHENTIFICATION"); //la verification des id a échoué 
					}
				}else { //il est connecter 
					if (msg != null) {
						switch (msg.getTypeOfmsg()) {
							case CREATE_TICKET:
								toSend = server.create_Ticket(msg);
								break;
							case ANSWAR: //repondre à un ticket
								toSend = server.answar_back(msg);
								break;
							case CLOSE_CONN: // fermeture
								toSend = "Communication terminée";
								closeConnexion = true;
								isConected = false;
								break;
							default:
								toSend = "Commande inconnu !";
								break;
						}
						writer.write(toSend);
					}
				}
				// Il FAUT IMPERATIVEMENT UTILISER flush()
				// Sinon les données ne seront pas transmises au client
				// et il attendra indéfiniment
				writer.flush();

				if (closeConnexion) {
					System.err.println("COMMANDE CLOSE DETECTEE ! ");
					writer = null;
					reader = null;
					server.dellClient(idClient); //on supprime le client dans ce cas
					sock.close();
					break;
				}
			} catch (SocketException e) {
				System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param msg2
	 * @return
	 */
	private boolean authentification(Msg msg2) {
		//TODO le code d'authentification ici 
		int indice = 1;
		String id = msg.getLogin();
		String pwd = msg.getPwd();
		System.out.println("id :"+id+"\npwd :"+pwd);
		idClient = id; //pour l'instant on met celui la mais on peut mettre un autre
		if (server.login(id, pwd)) {
			server.addClient(idClient, writer); //on ajoute le nouveau client une fois authentifier 
			isConected = true;
		}
		return isConected;
	}

	// La méthode que nous utilisons pour lire les réponses
	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);
		return response;
	}
	
	/**
	 * @return the server
	 */
	public ClientsServerSocketInit getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(ClientsServerSocketInit server) {
		this.server = server;
	}

}