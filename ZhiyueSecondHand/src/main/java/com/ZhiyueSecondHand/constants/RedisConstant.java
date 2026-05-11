package com.ZhiyueSecondHand.constants;

public interface RedisConstant {
    //登录注册相关
    String LOGIN_CODE_KEY = "login:code:";
    String LOGIN_ID_KEY = "login:id:";
    String LOGIN_USERINFO_KEY = "login:userinfo:";
    String LOGIN_TOKEN_KEY = "login:token:";
    
    //验证码相关
    String CODE_SEND_KEY = "code:send:";

    //minio 有关
    String MINIO_BOOK_BUCKET_NAME = "minio:book";

    //书籍评分相关
    String BOOK_SCORE_AMOUNT = "book:score:amount:";
    String BOOK_SCORE_AVERAGE = "book:score:average:";

    //用户收藏相关
    String COLLECT_USER_KEY = "collect:user:";
    
    //用户浏览历史相关
    String BOOK_HISTORY_KEY = "book:history:";


    //订单校验状态
    String ORDER_CHECK_KEY = "order:check";


    //聊天Session相关
    String CHAT_SESSION_KEY = "chat:session:";



    //ai聊天记录相关
    String CHAT_MEMORY_LIST_KEY = "chat:memory:list:";
}
