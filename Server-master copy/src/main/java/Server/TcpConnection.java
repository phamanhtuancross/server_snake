package Server;

import Model.Snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpConnection implements Runnable{

    private Server server;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public TcpConnection(Server server, Socket socket){
        this.server = server;
        this.socket = socket;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            while (true){
                String message = dataInputStream.readUTF();
                ServerMessage serverMessage = (ServerMessage) Helper.getObjectFromJsonString(ObjectType.SEVER_MESSAGE_OBJECT,message);
                System.out.println("serverMessage Type:" + serverMessage.messageType);
                switch (serverMessage.messageType){
                    case MessageType.GET_ID:{
                        dataOutputStream.writeUTF(server.getId()+"");
                        break;
                    }
                    case MessageType.GET_IP_ID_PORT:{
                        String ipString = socket.getInetAddress().getHostName();
                        InetAddress clientIp = InetAddress.getByName(ipString);
                        server.addressBook(clientIp,serverMessage.port);
                        break;
                    }
                    case MessageType.SEND_MAIN_CHARACTER:{
                        Snake snake  = serverMessage.character;
                        server.includeCharacter(snake);
                        break;
                    }
                    case MessageType.GET_CHARACTER: {
                        break;
                    }

                    case MessageType.GET_MAP:{
                        break;
                    }
                    case MessageType.REMOVE_CHARACTER:{
                        break;
                    }
                    default:{
                        break;
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
