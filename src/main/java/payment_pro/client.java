package payment_pro;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
    public static void main(String[] args) {
        try(
            Socket server = new Socket("127.0.0.1",4444);//连接本地测试
            ObjectOutputStream output = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(server.getInputStream())){
            // TODO: 2024/3/7 通过交互接口判断发出请求
                 //Test--
                   //发送充值请求
                //if(flag.equals(charge))
                chargeRequest chargeRequest = new chargeRequest("card123", 150);
                output.writeObject(chargeRequest);
                Response response = (Response) input.readObject();
                System.out.println(response.getMessage());
                  // 发送支付请求
                //if(flag.equals(pay))
//                payRequest payRequest = new payRequest("card123", 50);
//                output.writeObject(payRequest);
//                Response response = (Response) input.readObject();
//                System.out.println(response.getMessage());

            } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
