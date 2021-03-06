package io.ads21.serverchat.server;

import java.net.*;
import java.util.*;
import java.io.*;

public class ChatServer {
  static Vector<Socket> ClientSockets;
  static Vector<String> LoginNames;
  private static final int PORT = 5217;

  public ChatServer() throws Exception {
    @SuppressWarnings("resource")
    ServerSocket soc = new ServerSocket(PORT);
    ClientSockets = new Vector<Socket>();
    LoginNames = new Vector<String>();
    System.out.println("ChatServer rodando na porta: " + PORT);

    while (true) {
      Socket CSoc = soc.accept();
      new AcceptClient(CSoc);
    }
  }

  public static void main(String args[]) throws Exception {

     new ChatServer();
  }

 public class AcceptClient extends Thread {
    Socket ClientSocket;
    DataInputStream din;
    DataOutputStream dout;

    public AcceptClient(Socket CSoc) throws Exception {
      ClientSocket = CSoc;

      din = new DataInputStream(ClientSocket.getInputStream());
      dout = new DataOutputStream(ClientSocket.getOutputStream());

      String LoginName = din.readUTF();

      System.out.println("User Logged In :" + LoginName);
      LoginNames.add(LoginName);
      ClientSockets.add(ClientSocket);
      start();
    }

    public void run() {
      while (true) {

        try {
          String msgFromClient = new String();
          msgFromClient = din.readUTF();
          StringTokenizer st = new StringTokenizer(msgFromClient);
          String Sendto = st.nextToken();
          String MsgType = st.nextToken();
          int iCount = 0;

          if (MsgType.equals("LOGOUT")) {
            for (iCount = 0; iCount < LoginNames.size(); iCount++) {
              if (LoginNames.elementAt(iCount).equals(Sendto)) {
                LoginNames.removeElementAt(iCount);
                ClientSockets.removeElementAt(iCount);
                System.out.println("User " + Sendto + " Logged Out ...");
                break;
              }
            }

          } else {
            String msg = "";
            while (st.hasMoreTokens()) {
              msg = msg + " " + st.nextToken();
            }
            for (iCount = 0; iCount < LoginNames.size(); iCount++) {
              if (LoginNames.elementAt(iCount).equals(Sendto)) {
                Socket tSoc = (Socket) ClientSockets.elementAt(iCount);
                DataOutputStream tdout = new DataOutputStream(tSoc.getOutputStream());
                tdout.writeUTF(msg);
                break;
              }
            }
            if (iCount == LoginNames.size()) {
              dout.writeUTF("I am offline");
            } else {

            }
          }
          if (MsgType.equals("LOGOUT")) {
            break;
          }

        } catch (Exception ex) {
          ex.printStackTrace();
        }


      }
    }
  }
}
