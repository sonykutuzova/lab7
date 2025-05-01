package managers;

import commands.Container;
import utility.ExecutionResponse;
import utility.StandardConsole;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class NetworkManager {
    DatagramChannel ch;
    InetSocketAddress hostServer;
    StandardConsole console;

    public NetworkManager(String name, int port, StandardConsole console) {
        this.console = console;
        hostServer = new InetSocketAddress(name, port);
    }

    public ExecutionResponse init() {
        try {
            ch = DatagramChannel.open();
            ch.connect(hostServer);
            return new ExecutionResponse(true, "Подключение установлено");
        } catch (IOException e) {
            return new ExecutionResponse(false, "Ошибка подключения");
        }
    }

    public ExecutionResponse sendData(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            ch.write(buffer);
            return new ExecutionResponse(true, "Все хорошо");
        } catch (PortUnreachableException e){
            return new ExecutionResponse(false, "Ошибка сервера");
        } catch (IOException e) {
            return new ExecutionResponse(false, "Не очень получилось");
        }
    }

    public static byte[] serializer(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.close();
            byte[] objBytes = bos.toByteArray();
            return objBytes;
        } catch (IOException e) {
            return null;
        }
    }

    public static Object deserialize(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
             return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }


    public ExecutionResponse receiveData() {
        try{
            ByteBuffer responseBuffer = ByteBuffer.allocate(4096);
            while (responseBuffer.hasRemaining()) {
                int bytesRead = ch.read(responseBuffer);
                if (bytesRead > 0) {
                    break;
                }
            }
            responseBuffer.flip();
            if (responseBuffer.hasRemaining()) {
                byte[] responseData = new byte[responseBuffer.remaining()];
                responseBuffer.get(responseData);

                Container receivedObject = (Container) deserialize(responseData);
                if (receivedObject == null) {
                    return new ExecutionResponse(false, "Ошибка десериализации");
                }

                if (receivedObject.getAnswer().getMassage().equalsIgnoreCase("exit")){
                    ch.close();
                    System.exit(0);
                }
                return receivedObject.getAnswer();
                }
            }catch(IOException e) {return new ExecutionResponse(false, "Ошибка сервера, он не работает");}
        return new ExecutionResponse(false, "Unknown error");
    }
}


