package com.zangyalong.mingzangpicturebackend.shared.websocket.disruptor;

import com.zangyalong.mingzangpicturebackend.shared.websocket.model.PictureEditRequestMessage;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;
import com.zangyalong.mingzangpicturebackend.domain.user.entity.User;

@Data
public class PictureEditEvent {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 当前用户的 session
     */
    private WebSocketSession session;

    /**
     * 当前用户
     */
    private User user;

    /**
     * 图片 id
     */
    private Long pictureId;

}

