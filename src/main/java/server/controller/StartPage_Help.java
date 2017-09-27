package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import server.functions.DefaultRestResponse;
import server.functions.FindRepeatsDelete;
import java.util.List;


@RestController
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("")
public class StartPage_Help {
    @Autowired
    private FindRepeatsDelete findRepeatsDelete;

    @RequestMapping(method = RequestMethod.GET)
    public Object Help()
    {
        String str= "Help page:\n"+
                "Examples:\n"+
                "/books   -GET All books or PUT 1 book...   Paging and Sorting:\n"+
                "/books?size=10&number=0&property=id&direction=descending\n"+
                "/books/1    -GET book by id=1, or POST - update it, or DELETE it\n"+
                "/authors    -GET All authors, or PUT 1 author...   Paging and Sorting:\n"+
                "/authors?size=10&number=0&property=lastName&direction=descending\n"+
                "/authors/1    -GET author by id=1, or POST - update him, or DELETE him\n"+
                "/repeats   -GET -Show clones,\n"+
                "/repeats   -DELETE clones\n";
                //MERGE -СЛИЯНИЕ ПОВТОРОВ В 1
        return str;
    }

    @RequestMapping(value = "repeats", method = RequestMethod.GET)
    public String repeats(){
        return findRepeatsDelete.FindRepeats();
    }
    @RequestMapping(value = "repeats", method = RequestMethod.DELETE)
    public String repeatsDelete(){
        return findRepeatsDelete.DeleteRepeats(true);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DefaultRestResponse handleMissingParameter() {
        return new DefaultRestResponse(true, "Invalid JSON.");
    }

    //instead of whitelabel error
    @RequestMapping("/oauth/error")
    public String error() {
        return "Sorry. Error...";
    }
}
