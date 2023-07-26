package com.bsh.bshauction.component;

import com.bsh.bshauction.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.AccessException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(accessor.getCommand() == StompCommand.CONNECT) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if(StringUtils.hasText(token) && token.startsWith("Bearer")) {
                token = token.substring(7);
            }

            System.out.println(message);
            log.info("token : {}", token);

            if(token != null && !jwtTokenProvider.validateToken(token)) {
                try {
                    throw new AccessDeniedException("");
                } catch (AccessDeniedException e) {
                    throw new RuntimeException(e);
                }
            } else if(token == null) {
                return message;
            } else {
                return null;
            }
        }

        return message;
    }
}
