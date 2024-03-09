package payment_pro;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {
    private static final String FILENAME = "D:\\JavaWeb\\distribute_class\\src\\main\\cardBalances.txt";
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
                // TODO: 2024/3/7 写于文件
                updateOrAppendCardInfo(card, money);
                System.out.println(card+"用户充值"+money+"元");
                output.writeObject(new Response(true,"充值成功"+money));
            }else if(request instanceof payRequest){ //判断为支付请求
                payRequest payRequest = (payRequest)request;
                //处理支付请求
                String card = payRequest.getCard();
                float money = payRequest.getMoney();
                // TODO: 2024/3/7  读取文件并比较
                readCardInfo("cardBalances.txt");
                double balance = checkBalance(card);
                if(balance>=money){
                    updateOrAppendCardInfo(card, -money);
                    output.writeObject(new Response(true,"支付成功"));
                    System.out.println(card+"用户消费"+money+"元");
                }else {
                    output.writeObject(new Response(false,"支付失败"));
                    System.out.println(card+"用户消费失败");
                }
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

    public static void updateOrAppendCardInfo(String cardNumber, double newBalance) {

        // 读取现有数据并存储到一个HashMap中
        Map<String, Double> cardBalances = readCardInfo(FILENAME);

        // 检查卡号是否存在，如果不存在，get 方法将返回 null
        Double currentBalance = cardBalances.get(cardNumber);
        double updatedBalance = (currentBalance == null ? 0 : currentBalance) + newBalance;
        cardBalances.put(cardNumber, updatedBalance);

        // 将更新后的信息写回文件
        try (PrintWriter pw = new PrintWriter(new File(FILENAME))) {
            for (Map.Entry<String, Double> entry : cardBalances.entrySet()) {
                pw.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
    public static double checkBalance(String cardNumber){
        // 读取现有数据并存储到一个HashMap中
        Map<String, Double> cardBalances = readCardInfo(FILENAME);
        Double balance = cardBalances.get(cardNumber);
        return balance == null ? 0 : balance;
    }
    public static Map<String, Double> readCardInfo(String filename) {
        // 读取现有数据并存储到一个HashMap中
        Map<String, Double> cardBalances = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    cardBalances.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        return cardBalances;

    }
}
