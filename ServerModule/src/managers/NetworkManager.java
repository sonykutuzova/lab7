package managers;

import commands.Container;
import utility.ExecutionResponse;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkManager {

    private final Logger logger;
    private DatagramSocket socket;
    private int port;//порт, на котором сервер будет слушать входящие сообщения.
    private byte[] buffer = new byte[1024];//массив для приему данных
    private InetAddress inetAddress;// IP-адрес клиента, который отправил запрос.
    private int userPort;// IP-адрес клиента, который отправил запрос.


    public NetworkManager(int port, Logger logger){
        this.port = port;
        this.logger = logger;
    }
    // для запуска сервера и создания UDP-сокета на заданном порту
    public ExecutionResponse startPolling() {
        if (port < 0 || port > 65535) { // Проверка на валидность порта
            return new ExecutionResponse( false, "Плохой порт.");
        }
        try {
            socket = new DatagramSocket(port); // Попытка создать сокет
            return new ExecutionResponse(true, "Сервер запущен на порту " + port);
        } catch (SocketException e) {
            logger.log(Level.WARNING,"Ошибка при выборе порта", e);
            return new ExecutionResponse(false,"Ошибка запуска: " + e.getMessage()); // Возвращение сообщения об ошибке
        }
    }

    public Container getRequest(){
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            inetAddress = packet.getAddress();
            userPort = packet.getPort();
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Container receivedObject = (Container) ois.readObject();
            logger.log(Level.INFO,"Получен новый запрос от "+ inetAddress.toString() + ": " + userPort);
            return receivedObject;
        }
        catch (IOException e){
            logger.log(Level.WARNING,"Вызвана ошибка при получении запроса" , e);
            return null;
        }
        catch (ClassNotFoundException e){
            logger.log(Level.WARNING,"Вызвана ошибка при получении запроса" , e);
            return null;
        }
    }

    public ExecutionResponse sendPacket(Container container){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(container);
            oos.flush();
            byte[] data = baos.toByteArray();
            DatagramPacket responsePacket = new DatagramPacket(data, data.length, inetAddress, userPort);
            socket.send(responsePacket);
            logger.log(Level.INFO,"Отправлен ответ на " + inetAddress.toString() + ": " + userPort);
            return new ExecutionResponse(true,"Сообщение успешно отправлено");
        }
        catch (IOException e){
            logger.log(Level.WARNING,"Ошибка при отправке ответа: ", e);
            return new ExecutionResponse(false, "Ошибка отправки " + e);
        }
    }

}
