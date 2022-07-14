package com.mobilize.apibuilder.dao

import com.mobilize.apibuilder.models.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersDao extends JpaRepository<Users, Long> {

    //@Query("FROM Users u where u.session = ?1")
    Users findBySession(String arg1)

    Users findByEmailAndPassword(String arg1, String arg2)

}