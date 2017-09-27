package server.unit;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Profile("unit")
@Component
public class LoadTest {

    //For Wildfly {{{
    @PostConstruct
    //For Wildfly }}}
    public void Start()throws Exception{
        System.out.println("      ...................Tests are preparing.....................................        ");

            XmlSuite suite = new XmlSuite();
            suite.setName("TmpSuite");
            XmlTest test = new XmlTest(suite);
            test.setName("TmpTest");
            List<XmlClass> classes = new ArrayList<XmlClass>();
            classes.add(new XmlClass("server.unit.Tests"));
            test.setXmlClasses(classes);
            List<XmlSuite> suites = new ArrayList<XmlSuite>();
            suites.add(suite);
            TestNG tng = new TestNG();
            tng.setXmlSuites(suites);

        System.out.println("..........................Tests are started .....................................");
            tng.run();

         System.out.println("         ......................Tests are finished .....................................       ");
    }
}
