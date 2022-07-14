package com.mobilize.apibuilder.config

import com.mobilize.apibuilder.dao.UsersDao
import com.mobilize.apibuilder.models.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class CookieService {

    @Autowired
    UsersDao usersDao

    void setUserCookie(HttpServletResponse response, String sessionId) {
        def userCookie = new Cookie("sessionId", sessionId)
        userCookie.setMaxAge(2629743)
        userCookie.setPath("/")
        response.addCookie(userCookie)
    }

    /**
     * Create cookie with following params
     * @param name address for cookie
     * @param value string
     * @param age surival time of cookie
     * @param response
     * cookieService.setCookie("sessionId", user.getAuthKey(), 3600, response);
     */
    void setCookie(String name, String value, int age, HttpServletResponse response) {
        def userCookie = new Cookie(name, value)
        userCookie.setMaxAge(age)
        userCookie.setPath("/")
        response.addCookie(userCookie)
    }

    void setFastLoginCookie(HttpServletResponse response, Users user) {
        def userCookie = new Cookie("fast", user.sessionToken)
        userCookie.maxAge = 3600
        userCookie.path = "/"
        response.addCookie(userCookie)
    }

    Users getFastLogin(HttpServletRequest request) {
        request.cookies.each {cookie ->
            if(cookie.name.equals("fast")){
                def user = usersDao.findBysessionToken(cookie.value)
                if (user == null) {
                    return null
                }
                return user
            }
        }
        return null
    }

    String getSessionId(HttpServletRequest request) {

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("sessionId")) {
                return cookie.getValue()
            }
        }
        return null
    }

    Users getCurrentUser(HttpServletRequest request) throws NullPointerException {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("sessionId")) {
                def user = usersDao.findBySession(cookie.getValue())
                if (user == null) {
                    throw new NullPointerException()
                }
                return user
            }
        }
        throw new NullPointerException()
    }

}