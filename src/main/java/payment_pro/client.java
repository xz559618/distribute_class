package payment_pro;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        try (
                Socket server = new Socket("127.0.0.1", 4444);
                ObjectOutputStream output = new ObjectOutputStream(server.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(server.getInputStream());
        ) {
            System.out.println("已连接到服务器");
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入操作类型（charge/pay）:");
            String operation = scanner.nextLine();

            // 发送充值请求
            if ("charge".equals(operation)) {
                System.out.println("请输入卡号:");
                String card = scanner.nextLine();
                System.out.println("请输入充值金额:");
                float amount = scanner.nextFloat();
                chargeRequest chargeRequest = new chargeRequest(card, amount);
                output.writeObject(chargeRequest);
            }
            // 发送支付请求
            else if ("pay".equals(operation)) {
                System.out.println("请输入卡号:");
                String card = scanner.nextLine();
                System.out.println("请输入支付金额:");
                float amount = scanner.nextFloat();
                payRequest payRequest = new payRequest(card, amount);
                output.writeObject(payRequest);
            } else {
                System.out.println("未知的操作类型");
                return;
            }

            // 接收响应
            Response response = (Response) input.readObject();
            System.out.println(response.getMessage());
            System.out.println("服务器断开");
        } catch (UnknownHostException e) {
            System.out.println("未知的主机地址：" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO异常：" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("未找到类：" + e.getMessage());
        }
    }
}
