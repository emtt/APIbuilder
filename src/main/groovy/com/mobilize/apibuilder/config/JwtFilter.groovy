package com.mobilize.apibuilder.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.security.SignatureException
import java.util.logging.Logger


class JwtFilter extends GenericFilterBean {

    Logger logger = Logger.getLogger("APIBUILDER - JwtFilter")

    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req
        final HttpServletResponse response = (HttpServletResponse) res
        final String authHeader = request.getHeader("Authorization")

        def BearerKey = "Token "

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK)

            chain.doFilter(req, res)
        } else {

            if (authHeader == null || !authHeader.startsWith("Token ")) {
                throw new ServletException("Missing or invalid Authorization header")
            }

            final String token = authHeader.substring(BearerKey.length())

            try {

                Date expiresAt = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody().getExpiration()
                Date now = new Date()
                logger.info("->---------------------------------------------<-")
                //the value 0 if the argument Date is equal to this Date;
                // a value less than 0 if this Date is before the Date argument;
                // and a value greater than 0 if this Date is after the Date argument.
                // and NullPointerException - if anotherDate is null.
                logger.info("-> CHECK IF TOKEN IS EXPIRED -> ${now.compareTo(expiresAt)}")
                logger.info("-> -1 Token is Valid")
                logger.info("-> 0 Token is on  time but about to expire")
                logger.info("-> <0 Token is expired")
                logger.info("->---------------------------------------------<-")
                if (now.compareTo(expiresAt) > 0) {
                    throw new ServletException("THE TOKEN HAS EXPIRED")
                }

            } catch (final SignatureException e) {
                throw new ServletException("THE TOKEN HAS EXPIRED")
            }

            try {
                final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody()
                request.setAttribute("claims", claims)

            } catch (final SignatureException e) {
                throw new ServletException("Invalid token")
            }

            chain.doFilter(req, res)
        }
    }
}