package com.mobilize.apibuilder.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@Entity
class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotEmpty(message = "El Nombre es requerido ")
    String name

    @Email(message = "Email no válido")
    @NotEmpty(message = "El Email es requerido")
    String email

    @Size(min = 8, message ="El Password requiere 8 caracteres")
    String password

    String session

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Users)) return false

        Users user = (Users) o

        if (id != user.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

}
