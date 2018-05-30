package Server;

import Model.Snake;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {


    private static int SERVER_TCP_PORT;
    private static final long RESHRESH_GAP = 30;
    private UdpConnectionSend udpSend;
    private Vector<Snake> fullCharacter;
    private CopyOnWriteArrayList<IpPort> activeClients;
    private static long IDs = 0L;
    private WrapList gameplay;

    public static void main(String[] args) {
        Server server = new Server(1234);
        server.start();
    }


    public Server(int tcpPort) {
        SERVER_TCP_PORT = tcpPort;
        activeClients = new CopyOnWriteArrayList<>();
        udpSend = new UdpConnectionSend();
        fullCharacter = new Vector<>();
        gameplay = new WrapList();

    }

    public void start() {
        gameStateRefresh();
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_TCP_PORT);
           // System.out.println(serverSocke);
            Socket clientSocket;

            while ((clientSocket = serverSocket.accept()) != null) {
                new Thread(new TcpConnection(this, clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameStateRefresh() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGameplay();
                udpSend.sendGameplay();
            }

            private void updateGameplay() {
                gameplay.clear();
                for(Snake character : fullCharacter){
                    gameplay.addAll(character.update(fullCharacter));
                }
            }


        }, 0, RESHRESH_GAP);
    }

    public void addressBook(InetAddress address, int port){
        activeClients.add(new IpPort(address,port));
    }

    public void includeCharacter(Snake character){
        long specificId = character.Id;
        for(Snake mainCharacter : fullCharacter){
            if(specificId == mainCharacter.Id){
                mainCharacter.updateState(character.dots);
                return;
            }
        }

        fullCharacter.add(character);
    }

    public long getId() {
        return IDs++;
    }

    private static class IpPort {

        InetAddress address;
        int port;

        public IpPort(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

    }

    private class UdpConnectionSend {

        DatagramSocket gameplaySocket;

        public UdpConnectionSend() {
            try {
                gameplaySocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        public void sendGameplay(){

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream oos = new DataOutputStream(baos);
                Snake snake = new Snake();
                oos.writeUTF(Helper.getJsonStringFromObject(gameplay));
                byte[] bytes = baos.toByteArray();
                DatagramPacket packet = new DatagramPacket(bytes,bytes.length);

                for(IpPort dest : activeClients) {
                    packet.setAddress(dest.address);
                    packet.setPort(dest.port);

                    gameplaySocket.send(packet);

                    packet.setData(bytes);
                    packet.setLength(bytes.length);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            for(IpPort dest : activeClients){

            }
        }
    }
}
