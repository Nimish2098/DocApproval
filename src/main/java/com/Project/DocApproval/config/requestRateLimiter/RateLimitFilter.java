package com.Project.DocApproval.config.requestRateLimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private final RateLimitService rateLimitService;
  public RateLimitFilter(RateLimitService rateLimitService){
    this.rateLimitService = rateLimitService;
  }
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String path = request.getServletPath();

    boolean isAnalysisRequest =
        path.equals("/api/v1/resume-analysis/analyze")
        && request.getMethod().equalsIgnoreCase("POST");

    if (!isAnalysisRequest) {
      filterChain.doFilter(request, response);
      return;
    }

    String clientIp = request.getRemoteAddr();

    boolean allowed =
        rateLimitService.allowRequest(clientIp);

    if (!allowed) {

      response.sendError(429,"Too Many Request!");

      response.setContentType(
          MediaType.APPLICATION_JSON_VALUE
      );

      response.getWriter().write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "You have exceeded the request limit. Please try again later."
                }
                """);

      return;
    }

    filterChain.doFilter(request, response);
  }
}