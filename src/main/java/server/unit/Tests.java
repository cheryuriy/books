package server.unit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import server.Start;
import server.entities.Author;
import server.entities.Book;
import server.protobuf.BookAuthorProtos.AuthorProto;
import server.protobuf.BookAuthorProtos.BookProto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.*;
import static org.testng.Assert.assertEquals;
import org.json.JSONException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testng.annotations.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;


@TestPropertySource("/application-unit.properties")
@SpringBootTest(classes = Start.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//DEFINED_PORT RANDOM_PORT
@Transactional
@DirtiesContext
public class Tests extends AbstractTestNGSpringContextTests
{

    private List<String> print_to_file;

    //For Wildfly {{{
   @LocalServerPort
   int port_;// = 8080;
    private String port = port_ + "/books";
//For Wildfly }}}

    //For Tomcat {{{
 //   @LocalServerPort
   // int port;
//For Tomcat }}}

    private Author author;
    private AuthorProto.Builder protoA;
    private int ida,idp;


    @Autowired
    TestRestTemplate template;

    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().additionalMessageConverters(new ProtobufHttpMessageConverter());
        }
    }


        @Test
    public void testPassword() throws JsonParseException, JsonMappingException, IOException {

        ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://localhost:" + port + "/roles/user", String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//UNAUTHORIZED
        response = new TestRestTemplate("trusted-app", "secret").postForEntity("http://localhost:" + port + "/oauth/token?grant_type=password&username=user&password=password", null, String.class);
        String responseText = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        String accessToken = (String) jwtMap.get("access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        response = new TestRestTemplate().exchange("http://localhost:" + port + "/roles/user", HttpMethod.GET, new HttpEntity<>(null, headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = new TestRestTemplate().exchange("http://localhost:" + port + "/roles/principal", HttpMethod.GET, new HttpEntity<>(null, headers), String.class);
        assertEquals("user", response.getBody());

        response = new TestRestTemplate().exchange("http://localhost:" + port + "/roles/roles", HttpMethod.GET, new HttpEntity<>(null, headers), String.class);
        assertEquals("[{\"authority\":\"ROLE_USER\"}]", response.getBody());
        System.out.println("Tests Password successfull...");
        print_to_file = new ArrayList<String>();
        print_to_file.add("Tests Password successfull...");
        }


    @Test(dependsOnMethods={"testPassword"})
    public void testCreate_Edit_Author() throws JSONException,JsonParseException, JsonMappingException, IOException {
        ResponseEntity<String> response = new TestRestTemplate("trusted-app", "secret").postForEntity("http://localhost:" + port + "/oauth/token?grant_type=password&username=user&password=password", null, String.class);
        String responseText = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        String accessToken = (String) jwtMap.get("access_token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        LocalDate date = LocalDate.now();
        author= new Author ("TestA","TestA", date);

        response = new TestRestTemplate().exchange("http://localhost:" + port + "/authors", HttpMethod.PUT, new HttpEntity<>(author, headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        ida = Integer.parseInt((String) jwtMap.get("message"));
        author.setFirstName("EditTestA");
        author.setLastName("EditTestA");
        author.setDate(LocalDate.now());
        response = new TestRestTemplate().exchange("http://localhost:" + port + "/authors/"+ida, HttpMethod.POST, new HttpEntity<>(author, headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));
    }



    @Test(dependsOnMethods={"testCreate_Edit_Author"})
    public void testCreateEditBookDeleteAuthors() throws JsonParseException, JsonMappingException, IOException {
        ResponseEntity<String> response = new TestRestTemplate("trusted-app", "secret").postForEntity("http://localhost:" + port + "/oauth/token?grant_type=password&username=user&password=password", null, String.class);
        String responseText = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        String accessToken = (String) jwtMap.get("access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        LocalDate date = LocalDate.now();
        Book book= new Book("TestB","TestB", date);
        book.addAuthor(author);
        response = new TestRestTemplate().exchange("http://localhost:" + port + "/books", HttpMethod.PUT, new HttpEntity<>(book, headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        int id = Integer.parseInt((String) jwtMap.get("message"));//(String)
        book.setBook("EditTestB");
        book.setPublisher("TestBEdit");
        book.setDate(LocalDate.now());
        for (Author aut : book.getAuthors()) {
            aut.setLastName("NewAuthorTestB");
            aut.setFirstName("NewTestB");
        }
        response = new TestRestTemplate().exchange("http://localhost:" + port + "/books/"+id, HttpMethod.POST, new HttpEntity<>(book, headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        response = new TestRestTemplate().exchange("http://localhost:" + port + "/authors/"+ida, HttpMethod.DELETE, new HttpEntity<>( headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        response = new TestRestTemplate().exchange("http://localhost:" + port + "/books/"+id, HttpMethod.DELETE, new HttpEntity<>( headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        response = new TestRestTemplate().exchange("http://localhost:" + port + "/authors/"+(id+1), HttpMethod.DELETE, new HttpEntity<>( headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

    }


    @Test(dependsOnMethods={"testCreateEditBookDeleteAuthors"})
    public void ProtoCreate_Edit_Author() throws JSONException,JsonParseException, JsonMappingException, IOException {
        ResponseEntity<String> response = new TestRestTemplate("trusted-app", "secret").postForEntity("http://localhost:" + port + "/oauth/token?grant_type=password&username=user&password=password", null, String.class);
        String responseText = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        String accessToken = (String) jwtMap.get("access_token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        protoA = AuthorProto.newBuilder();
        protoA.setFirstName("Proto_nameA");
        protoA.setLastName("Proto_LastnameA");
        protoA.setDate("1983-03-13");

        response = template.exchange("http://localhost:" + port + "/protobuf/authors", HttpMethod.PUT, new HttpEntity<>(protoA.build(), headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        idp = Integer.parseInt((String) jwtMap.get("message"));
        protoA.setFirstName("EditProt_nameA");
        protoA.setLastName("EditProt_LastnameA");
        protoA.setDate("1983-02-13");
        response = template.exchange("http://localhost:" + port + "/protobuf/authors/"+idp, HttpMethod.POST, new HttpEntity<>(protoA.build(), headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));
    }

    @Test(dependsOnMethods={"ProtoCreate_Edit_Author"})
    public void ProtoCreateEditBookDeleteAuthors() throws JsonParseException, JsonMappingException, IOException {
        ResponseEntity<String> response = new TestRestTemplate("trusted-app", "secret").postForEntity("http://localhost:" + port + "/oauth/token?grant_type=password&username=user&password=password", null, String.class);
        String responseText = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        String accessToken = (String) jwtMap.get("access_token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        BookProto.Builder proto = BookProto.newBuilder();
        proto.setBook("Proto_bookB");
        proto.setPublisher("ProtoPub_B");
        proto.setDate("1983-03-13");
        proto.addAuthors(protoA);

        response = template.exchange("http://localhost:" + port + "/protobuf/books", HttpMethod.PUT, new HttpEntity<>(proto.build(), headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        int id = Integer.parseInt((String) jwtMap.get("message"));//(String)
        proto.setBook("ChangedProto_bookB");
        proto.setPublisher("ChangedProtoPub_B");
        proto.setDate("1983-02-13");

        protoA.setLastName("NewAuthorBProto");
        protoA.setFirstName("NewBProto");
        proto.setAuthors(0,protoA);
        response = template.exchange("http://localhost:" + port + "/protobuf/books/"+id, HttpMethod.POST, new HttpEntity<>(proto.build(), headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        response = template.exchange("http://localhost:" + port + "/protobuf/authors/"+idp, HttpMethod.DELETE, new HttpEntity<>( headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        response = template.exchange("http://localhost:" + port + "/protobuf/books/"+id, HttpMethod.DELETE, new HttpEntity<>( headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        response = template.exchange("http://localhost:" + port + "/protobuf/authors/"+(id+1), HttpMethod.DELETE, new HttpEntity<>( headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        responseText = response.getBody();
        System.out.println(responseText);
        print_to_file.add(responseText);
        jwtMap = new ObjectMapper().readValue(responseText, HashMap.class);
        assertEquals(false, jwtMap.get("error"));

        try {
        Files.write(Paths.get("d://temp//test_output.txt"), print_to_file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);//StandardOpenOption.CREATE
        } catch (IOException print_to){}
    }

}