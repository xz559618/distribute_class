package payment_pro;

import java.io.Serializable;

//基础请求类
abstract class Request implements Serializable { //Serializable 序列化
    private static final long serialVersionUID = 1L;
}

//响应类
class Response implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String message;
    public Response(boolean seccess,String message){
        this.message = message;
        this.success = seccess;
    }
    public boolean isSuccess(){
        return success;
    }
    public String getMessage(){
        return message;
    }
}
//充值请求
class chargeRequest extends Request{
    private static final long serialVersionUID = 1L;
    private String card;
    private float money;
    chargeRequest(String card, int money){
        this.card = card;
        this.money = money;
    }
    public String getCard(){
        return card;
    }
    public float getMoney(){
        return money;
    }
}
//支付请求
class payRequest extends Request {
    private static final long serialVersionUID = 1L;
    private String card;
    private float money;
    payRequest(String card,int money){
        this.card = card;
        this.money = money;
    }
    public String getCard(){
        return card;
    }
    public float getMoney(){
        return money;
    }
}

