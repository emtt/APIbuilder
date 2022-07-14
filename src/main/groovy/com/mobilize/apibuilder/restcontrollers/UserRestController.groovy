package com.mobilize.apibuilder.restcontrollers

import com.mobilize.apibuilder.config.CustomExceptionHandler
import com.mobilize.apibuilder.config.SecurityUtil
import com.mobilize.apibuilder.dao.UsersDao
import com.mobilize.apibuilder.models.Users
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.ServletException
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserRestController {

    @Autowired
    UsersDao usersDao

    @Autowired
    SecurityUtil securityUtil

    @PostMapping
    ResponseEntity<?> saveUser(@Valid @RequestBody Users user) throws CustomExceptionHandler {
        def map = [:]
        if(user != null){
            user.password = securityUtil.encodeText(user.password)

            map.put("message", "user created")
            map.put("user", usersDao.save(user))
        }

        return new ResponseEntity<?>(map, HttpStatus.OK)
    }

}
