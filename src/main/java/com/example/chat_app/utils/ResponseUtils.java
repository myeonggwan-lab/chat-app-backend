package com.example.chat_app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.example.chat_app.dto.FailResponse.*;
import static com.example.chat_app.dto.SuccessResponse.*;

public class ResponseUtils {
    public static void sendSuccessResponse(HttpServletResponse response, String message, Object data, int status) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(success(message, data));

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(json);
        response.setStatus(status);
    }

    public static void sendFailResponse(HttpServletResponse response, String code, String message, int status) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(fail(code, message));

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(json);
        response.setStatus(status);
    }
}
