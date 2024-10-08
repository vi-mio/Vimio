package top.vimio.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.vimio.annotation.OperationLogger;
import top.vimio.entity.Logentity.OperationLog;
import top.vimio.service.OperationLogService;
import top.vimio.utils.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description:
 * @Author: Vimio
 * @Date: 2024/9/14 16:21
 */
@Component
@Aspect
public class OperationLogAspect {
    @Autowired
    OperationLogService operationLogService;

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(operationLogger)")
    public void logPointcut(OperationLogger operationLogger) {
    }

    /**
     * 配置环绕通知
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("logPointcut(operationLogger)")
    public Object logAround(ProceedingJoinPoint joinPoint, OperationLogger operationLogger) throws Throwable {
        currentTime.set(System.currentTimeMillis());
        Object result = joinPoint.proceed();
        int times = (int) (System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        OperationLog operationLog = handleLog(joinPoint, operationLogger, times);
        operationLogService.saveOperationLog(operationLog);
        return result;
    }

    /**
     * 获取HttpServletRequest请求对象，并设置OperationLog对象属性
     *
     * @param operationLogger
     * @param times
     * @return
     */
    private OperationLog handleLog(ProceedingJoinPoint joinPoint, OperationLogger operationLogger, int times) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String username = JwtUtils.getTokenBody(request.getHeader("Authorization")).getSubject();
        if ("admin:vi-mio@qq.com".equals(username)) {
            username = "admin";
        }
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String description = operationLogger.value();
        String ip = IpAddressUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        OperationLog log = new OperationLog(username, uri, method, description, ip, times, userAgent);
        Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
        log.setParam(StringUtils.substring(JacksonUtils.writeValueAsString(requestParams), 0, 2000));
        return log;
    }
}