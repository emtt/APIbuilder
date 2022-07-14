# APIbuilder
Example of Spring Boot project with Groovy-Gradle implementation and JWT as token security layer.



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

