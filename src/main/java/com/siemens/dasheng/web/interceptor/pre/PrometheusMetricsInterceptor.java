package com.siemens.dasheng.web.interceptor.pre;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: fan.bian.ext@siemens.com
 * @Date: Created in 6/25/2018.
 */
public class PrometheusMetricsInterceptor extends HandlerInterceptorAdapter {

    static final Counter REQUEST_COUNTER = Counter.build()
            .name("module_user_http_requests_total").labelNames("path", "method", "code")
            .help("Total requests.").register();

    static final Gauge INPROGRESS_REQUESTS = Gauge.build()
            .name("module_user_http_inprogress_requests").labelNames("path", "method", "code")
            .help("Inprogress requests.").register();

    static final Gauge REQUEST_TIME = Gauge.build()
            .name("module_user_http_requests_costTime").labelNames("path", "method", "code")
            .help("requests cost time.").register();

    static final Histogram REQUEST_LATENCY_HISTOGRAM = Histogram.build().labelNames("path", "method", "code")
            .name("module_user_http_requests_latency_seconds_histogram").help("Request latency in seconds.")
            .register();

    static final Summary REQUEST_LATENCY = Summary.build()
            .name("module_user_http_requests_latency_seconds_summary")
            .quantile(0.5, 0.05)
            .quantile(0.9, 0.01)
            .labelNames("path", "method", "code")
            .help("Request latency in seconds.").register();

    private Histogram.Timer histogramRequestTimer;

    private Summary.Timer summaryTimer;

    private Gauge.Timer gaugeTimer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();
        INPROGRESS_REQUESTS.labels(requestURI, method, String.valueOf(status)).inc();
        histogramRequestTimer = REQUEST_LATENCY_HISTOGRAM.labels(requestURI, method, String.valueOf(status)).startTimer();
        summaryTimer = REQUEST_LATENCY.labels(requestURI, method, String.valueOf(status)).startTimer();
        gaugeTimer = REQUEST_TIME.labels(requestURI, method, String.valueOf(status)).startTimer();
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();

        REQUEST_COUNTER.labels(requestURI, method, String.valueOf(status)).inc();
        INPROGRESS_REQUESTS.labels(requestURI, method, String.valueOf(status)).dec();
        histogramRequestTimer.observeDuration();
        summaryTimer.observeDuration();
        gaugeTimer.setDuration();
        super.afterCompletion(request, response, handler, ex);
    }
}
