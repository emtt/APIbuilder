package com.mobilize.apibuilder.webcontrollers

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import com.mobilize.apibuilder.config.CookieService
import com.mobilize.apibuilder.config.SecurityUtil
import com.mobilize.apibuilder.dao.UsersDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import java.util.logging.Logger


@Controller
class UserWebController {

    @Autowired
    UsersDao usersDao

    @Autowired
    CookieService cookieService

    @Autowired
    SecurityUtil securityUtil

    Logger logger = Logger.getLogger("APIBUILDER - WEBCONTROLLER/USERS")

    @GetMapping("login")
    String getLogin(Model model) {
        return "login"
    }

    @PostMapping("login")
    String processLogin(Model model, @Valid @RequestParam String email, @Valid @RequestParam String password, HttpServletResponse response){

        def users = usersDao.findByEmailAndPassword(email, securityUtil.encodeText(password))

        if(users != null){

            logger.info("USER FOUNDED: ${users.name}")

            def startTime = System.currentTimeMillis()
            def c = Calendar.getInstance()
            c.add(Calendar.HOUR, +2)
            def validUntil = c.getTimeInMillis()

            //CREATE SESSION TOKEN
            def jwtToken = Jwts.builder()
                    .setSubject(users.email)
                    .claim("roles", "admin")
                    .setIssuedAt(new Date(startTime))
                    .setExpiration(new Date(validUntil))
                    .signWith(SignatureAlgorithm.HS256, "secretkey")
                    .compact()

            users.session = jwtToken
            usersDao.save(users)

            cookieService.setCookie("sessionId",
                    jwtToken,
                    validUntil.intValue(),
                    response)

            return "redirect:/admin"

        }else{
            logger.info("USER NOT FOUND!")
            model.addAttribute("message", "Usuario o clave inválido")
            return "login"
        }


    }


}
