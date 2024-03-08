package payment_pro;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);//设置线程池容量为5
        try{
            ServerSocket server = new ServerSocket(4444);
            System.out.println("服务器启动，等待连接...");
            while(true){
                Socket client = server.accept();
                Runnable worker = () -> {
                    try {
                        handelClient(client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                };//启动线程
                executor.execute(worker);
            }
        }catch (Exception e){
            System.out.print("Error:" + e);
            System.exit(-1);
        }
        executor.shutdown();//关闭线程
    }
    //线程处理客户端
    private static void handelClient(Socket client) throws IOException, ClassNotFoundException {
        try(ObjectInputStream input = new ObjectInputStream(client.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream())){
            //客户端发来的信息
            Object request = input.readObject();
            // instanceof 判断对象的类型
            if(request instanceof chargeRequest){ //判断为充值请求
                chargeRequest chargeRequest = (chargeRequest) request;
                //处理充值请求
                String card = chargeRequest.getCard();
                float money = chargeRequest.getMoney();
                output.writeObject(new Response(true,"充值成功"+money));
                // TODO: 2024/3/7 写于文件
            }else if(request instanceof payRequest){ //判断为支付请求
                payRequest payRequest = (payRequest)request;
                //处理支付请求
                String card = payRequest.getCard();
                float money = payRequest.getMoney();
                output.writeObject(new Response(true,"支付成功"));
                output.writeObject(new Response(false,"支付失败"));
                // TODO: 2024/3/7  读取文件并比较
            }

        }catch (Exception e){
            System.out.println("Error:"+e);
        }finally {
            try{
                client.close();
            }catch (IOException e){
                System.out.print("Error:"+e);
            }
        }
    }
}
