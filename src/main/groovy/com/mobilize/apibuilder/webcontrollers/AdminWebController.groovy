package com.mobilize.apibuilder.webcontrollers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.mobilize.apibuilder.config.CookieService
import com.mobilize.apibuilder.config.SecurityUtil
import com.mobilize.apibuilder.dao.UsersDao
import com.mobilize.apibuilder.utils.JsonFlattener
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.http.MediaType
import org.springframework.boot.json.JsonParser
import org.springframework.boot.json.JsonParserFactory
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest
import java.util.logging.Logger

@Controller
@RequestMapping("admin")
class AdminWebController {

    @Autowired
    UsersDao usersDao

    @Autowired
    CookieService cookieService

    @Autowired
    SecurityUtil securityUtil

    Logger logger = Logger.getLogger("APIBUILDER - WEBCONTROLLER/ADMIN CONTROLLER")



    @GetMapping
    String adminHome(Model model, HttpServletRequest request) {

        def sessionId = cookieService.getSessionId(request)
        logger.info("USER SESSION ID ${sessionId}")

        if(sessionId == null){
            return "redirect:/login?expired"
        }

        def user = cookieService.getCurrentUser(request)

        if(!isValidtoken(sessionId)){
            logger.info("INVALID TOKEN. MUST BE REDIRECTED")
            user.session = null
            usersDao.save(user)
            return "redirect:/login?expired"
        }

        model.addAttribute("userName", user.name)

        final RestTemplate restTemplate = new RestTemplate()
        def url = "https://jsonplaceholder.typicode.com/users"
        def result = restTemplate.getForObject(url, String.class)

        ObjectMapper mapper = new ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        JsonNode jsonNode = mapper.readTree(result)

        JsonNode node = JsonNodeFactory.instance.objectNode()
        node.set("emails", jsonNode.findValues("email") as JsonNode)

        println("node: $node")
        /*
        //println("result: $result")
        //JsonFlattener flatter = new JsonFlattener(result)

        def flattered = new JsonFlattener(result)

        String json = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(flattered.flattenToMap())
        print("FLATTERED JSON: $json")



        *//*
        JsonNode nameNode = jsonNode.path("name")
        JsonNode emailNode = jsonNode.path("email")

        JsonNode responseNode = JsonNodeFactory.instance.objectNode()
        responseNode.set("name", nameNode)

         *//*
        //responseNode.set("email", emailNode.findValues("email").get(0))

        //println("responseNode: " + responseNode)

        //def newapi = jsonNode.findValues("email") + jsonNode.findValues("lat")
        //println("newapi: " + newapi)
        *//*
        newapi.each {val ->
            println val
        }*//*
        //println("email: " + jsonNode.findValues("email"))
        //println("LAT: " + jsonNode.findValues("lat"))
        *//*
        def mapResponse = [:]
        mapResponse.put("emails", jsonNode.findValues("email"))
        mapResponse.put("geo", jsonNode.findValues("lat"))
        println("mapResponse: " + mapResponse)
        *//*


        //mapResponse.each { k, v -> println "key: $k, value: $v"}
        *//*
        def map = []

        mapResponse.put("lat", jsonNode.findValues("lat"))

         *//*

        //process("", jsonNode)

        //result.eachWithIndex{entry, i -> println "$i $entry.key: $entry.value"}
*/
        model.addAttribute("jsonData", jsonNode)
        return "admon"
    }

    void process(String prefix, JsonNode currentNode) {
        if (currentNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) currentNode;
            Iterator<JsonNode> node = arrayNode.elements();
            int index = 1;
            while (node.hasNext()) {
                process(!prefix.isEmpty() ? prefix + "-" + index : String.valueOf(index), node.next());
                index += 1;
            }
        }
        else if (currentNode.isObject()) {
            currentNode.fields().forEachRemaining(entry -> process(!prefix.isEmpty() ? prefix + "-" + entry.getKey() : entry.getKey(), entry.getValue()));
        }
        else {
            logger.info(prefix + ": " + currentNode.toString())
        }
    }

    boolean isValidtoken(String sessionId){
        //-1 Token is expired
        //0  Token is on  time but about to expire
        //< 0 Token is valid
        Date tokenExpirationDate = Jwts.parser()
                .setSigningKey("secretkey")
                .parseClaimsJws(sessionId)
                .getBody()
                .getExpiration()
        Date now = new Date()
        if((tokenExpirationDate <=> now) > -1){
            return true
        }else{
            return false
        }
    }

    HttpEntity<?> getEntity() {
        HttpHeaders headers = new HttpHeaders()
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON))

        String plainCreds = "test:Test2022@"
        byte[] plainCredsBytes = plainCreds.getBytes()
        byte[] base64CredsBytes = Base64.getEncoder().encodeToString(plainCredsBytes)
        String base64Creds = new String(base64CredsBytes)

        headers.add("Authorization", "Basic " + base64Creds)


        //NOT WORKING WITHOUT THIS HEADER
        /*
        headers.add("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36")*/

        HttpEntity<String> entity = new HttpEntity<String>(headers)

        return entity
    }
}
